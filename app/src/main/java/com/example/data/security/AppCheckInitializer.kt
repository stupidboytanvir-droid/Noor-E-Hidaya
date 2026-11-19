package com.example.data.security

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.example.BuildConfig
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaException
import com.google.android.recaptcha.RecaptchaTasksClient
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.appcheck.AppCheckProvider
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val TAG = "AppCheckInit"

private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val HARNESS_PREFS_NAME = "com.example.appcheck.harness_prefs"
private const val KEY_PROVISIONED_DEBUG_TOKEN = "has_provisioned_debug_token"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

object AppCheckDiagnostics {
    @Volatile
    var activeProviderMode: String = "Initializing"
        internal set

    @Volatile
    var lastErrorMessage: String? = null
        internal set

    @Volatile
    var lastSuccessAtMillis: Long = 0L
        internal set

    @Volatile
    var activeSiteKeyPrefix: String = ""
        internal set

    fun recordSuccess(mode: String) {
        activeProviderMode = mode
        lastErrorMessage = null
        lastSuccessAtMillis = System.currentTimeMillis()
    }

    fun recordError(mode: String, error: Throwable) {
        activeProviderMode = mode
        lastErrorMessage = formatConciseError(error)
    }

    fun formatConciseError(error: Throwable): String {
        val root = generateSequence(error) { it.cause }.lastOrNull() ?: error
        val recaptchaEx = generateSequence(error) { it.cause }
            .filterIsInstance<RecaptchaException>()
            .firstOrNull()
        if (recaptchaEx != null) {
            return "reCAPTCHA(${recaptchaEx.errorCode}): ${recaptchaEx.errorMessage}"
        }
        val msg = root.message?.takeIf { it.isNotBlank() }
            ?: error.message?.takeIf { it.isNotBlank() }
            ?: error.javaClass.simpleName
        return msg.take(180)
    }
}

private class SimpleAppCheckToken(
    private val jwtToken: String,
    private val expirationTimeMillis: Long
) : AppCheckToken() {
    override fun getToken(): String = jwtToken
    override fun getExpireTimeMillis(): Long = expirationTimeMillis
}

/**
 * Auto-retrying reCAPTCHA Enterprise AppCheckProvider that fixes Firebase SDK's
 * `RecaptchaAppCheckProvider` permanent failure caching bug:
 * - Uses a 25-second initialization timeout instead of 10 seconds
 * - Never caches a failed `RecaptchaTasksClient`; automatically retries on subsequent attempts
 * - Falls back between `Recaptcha.fetchTaskClient` and `Recaptcha.getTasksClient`
 * - Preserves exact HTTP error codes/messages from `exchangeRecaptchaEnterpriseToken`
 */
class ResilientRecaptchaAppCheckProviderFactory(
    private val siteKey: String
) : AppCheckProviderFactory {

    @Volatile
    private var cachedProvider: ResilientRecaptchaAppCheckProvider? = null

    override fun create(firebaseApp: FirebaseApp): AppCheckProvider {
        cachedProvider?.let { return it }
        synchronized(this) {
            cachedProvider?.let { return it }
            val created = ResilientRecaptchaAppCheckProvider(firebaseApp, siteKey)
            cachedProvider = created
            return created
        }
    }
}

internal class ResilientRecaptchaAppCheckProvider(
    private val firebaseApp: FirebaseApp,
    private val siteKey: String
) : AppCheckProvider {

    private val executor = Executors.newCachedThreadPool()
    private val lock = Any()

    @Volatile
    private var cachedClient: RecaptchaTasksClient? = null

    init {
        // Non-blocking warm-up so WebView/JS loads early without poisoning future retries if offline at launch
        if (!Build.FINGERPRINT.equals("robolectric", ignoreCase = true)) {
            executor.execute {
                try {
                    getOrInitClient()
                } catch (t: Throwable) {
                    Log.w(TAG, "Background reCAPTCHA warm-up skipped/deferred: ${t.message}")
                }
            }
        }
    }

    override fun getToken(): Task<AppCheckToken> =
        Tasks.call(executor) { fetchAppCheckTokenSync(limitedUse = false) }

    override fun getLimitedUseToken(): Task<AppCheckToken> =
        Tasks.call(executor) { fetchAppCheckTokenSync(limitedUse = true) }

    private fun getOrInitClient(forceNew: Boolean = false): RecaptchaTasksClient {
        if (!forceNew) {
            cachedClient?.let { return it }
        }
        synchronized(lock) {
            if (!forceNew) {
                cachedClient?.let { return it }
            }
            val app = firebaseApp.applicationContext as? Application
                ?: throw FirebaseException("Application context unavailable for reCAPTCHA App Check.")

            var lastError: Exception? = null
            // Attempt 1: Recommended fetchTaskClient with 25s timeout
            try {
                val client = Tasks.await(
                    Recaptcha.fetchTaskClient(app, siteKey),
                    25_000L,
                    TimeUnit.MILLISECONDS
                )
                cachedClient = client
                return client
            } catch (e: Exception) {
                lastError = unwrapExecutionException(e)
                Log.w(TAG, "Recaptcha.fetchTaskClient failed, trying getTasksClient: ${lastError.message}")
            }

            // Attempt 2: Explicit timeout getTasksClient fallback
            try {
                val client = Tasks.await(
                    Recaptcha.getTasksClient(app, siteKey, 25_000L),
                    28_000L,
                    TimeUnit.MILLISECONDS
                )
                cachedClient = client
                return client
            } catch (e: Exception) {
                cachedClient = null
                val unwrapped = unwrapExecutionException(e)
                throw lastError?.takeIf { unwrapped === e } ?: unwrapped
            }
        }
    }

    private fun fetchAppCheckTokenSync(limitedUse: Boolean): AppCheckToken {
        try {
            val action = RecaptchaAction.custom("fire_app_check")
            var attestationToken: String? = null
            var recaptchaError: Exception? = null

            for (attempt in 0..1) {
                try {
                    val client = getOrInitClient(forceNew = attempt > 0)
                    attestationToken = Tasks.await(
                        client.executeTask(action),
                        20_000L,
                        TimeUnit.MILLISECONDS
                    )
                    if (!attestationToken.isNullOrBlank()) break
                } catch (e: Exception) {
                    cachedClient = null
                    recaptchaError = unwrapExecutionException(e)
                    Log.w(TAG, "reCAPTCHA executeTask attempt ${attempt + 1} failed: ${recaptchaError.message}")
                }
            }

            val finalAttestation = attestationToken?.takeIf { it.isNotBlank() }
                ?: throw (recaptchaError ?: FirebaseException("Empty reCAPTCHA attestation token"))

            val token = exchangeRecaptchaTokenWithFirebase(finalAttestation, limitedUse)
            AppCheckDiagnostics.recordSuccess("RecaptchaEnterprise")
            return token
        } catch (e: Exception) {
            AppCheckDiagnostics.recordError("RecaptchaEnterprise", e)
            throw e
        }
    }

    private fun exchangeRecaptchaTokenWithFirebase(
        recaptchaEnterpriseToken: String,
        limitedUse: Boolean
    ): AppCheckToken {
        val options = firebaseApp.options
        val projectId = options.projectId
            ?: throw FirebaseException("Firebase projectId missing in configuration.")
        val appId = options.applicationId
        val apiKey = options.apiKey
        val context = firebaseApp.applicationContext

        val endpoint =
            "https://firebaseappcheck.googleapis.com/v1/projects/$projectId/apps/$appId:exchangeRecaptchaEnterpriseToken?key=$apiKey"

        val payload = JSONObject().apply {
            put("recaptchaEnterpriseToken", recaptchaEnterpriseToken)
            put("limitedUse", limitedUse)
        }.toString().toByteArray(Charsets.UTF_8)

        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 15_000
            doOutput = true
            setFixedLengthStreamingMode(payload.size)
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("X-Android-Package", context.packageName)
            getSigningSha1Hex(context)?.let { sha1 ->
                setRequestProperty("X-Android-Cert", sha1)
            }
        }

        return try {
            conn.outputStream.use { it.write(payload) }
            val statusCode = conn.responseCode
            val stream = if (statusCode in 200..299) conn.inputStream else conn.errorStream
            val bodyText = stream?.bufferedReader()?.use { it.readText() }.orEmpty()

            if (statusCode !in 200..299) {
                val apiMsg = try {
                    JSONObject(bodyText).optJSONObject("error")?.optString("message")
                        ?.takeIf { it.isNotBlank() }
                } catch (_: Exception) {
                    null
                } ?: bodyText.take(180).ifBlank { "HTTP $statusCode" }
                throw FirebaseException("App Check attestation HTTP $statusCode: $apiMsg")
            }

            val json = JSONObject(bodyText)
            val jwt = json.optString("token", "")
            if (jwt.isBlank()) {
                throw FirebaseException("App Check exchange returned an empty token.")
            }
            val ttlMillis = parseTtlMillis(json.optString("ttl", "3600s"))
            SimpleAppCheckToken(jwt, System.currentTimeMillis() + ttlMillis)
        } catch (e: IOException) {
            throw FirebaseException("App Check network error: ${e.message ?: "IOException"}", e)
        } finally {
            conn.disconnect()
        }
    }

    private fun unwrapExecutionException(e: Exception): Exception {
        val cause = e.cause
        return if (e is java.util.concurrent.ExecutionException && cause is Exception) cause else e
    }
}

internal fun parseTtlMillis(ttlRaw: String): Long {
    val cleaned = ttlRaw.trim().removeSuffix("s")
    val seconds = cleaned.toDoubleOrNull()?.toLong() ?: 3600L
    return (seconds.coerceAtLeast(60L)) * 1000L
}

fun getSigningSha1Hex(context: Context): String? {
    return try {
        val pm = context.packageManager
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val info = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            info.signingInfo?.apkContentsSigners
        } else {
            @Suppress("DEPRECATION")
            val info = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            @Suppress("DEPRECATION")
            info.signatures
        }
        val certBytes = signatures?.firstOrNull()?.toByteArray() ?: return null
        val md = MessageDigest.getInstance("SHA-1")
        md.digest(certBytes).joinToString("") { "%02X".format(it) }
    } catch (_: Exception) {
        null
    }
}

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val persistenceKey = FirebaseApp.getInstance().persistenceKey
        val debugStorePrefs = context.getSharedPreferences(
            "$DEBUG_STORE_PREFIX$persistenceKey",
            Context.MODE_PRIVATE
        )
        val harnessPrefs = context.getSharedPreferences(
            HARNESS_PREFS_NAME,
            Context.MODE_PRIVATE
        )

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)

            if (!debugToken.isNullOrEmpty()) {
                // Synchronous commit() is required before DebugAppCheckProviderFactory reads it.
                debugStorePrefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                harnessPrefs.edit()
                    .putBoolean(KEY_PROVISIONED_DEBUG_TOKEN, true)
                    .commit()
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
                AppCheckDiagnostics.activeProviderMode = "DebugToken"
                AppCheckDiagnostics.lastErrorMessage = null
                Log.d(TAG, "DebugAppCheckProviderFactory installed with provisioned debug token.")
                return
            }

            val hasProvisionedToken = harnessPrefs.getBoolean(KEY_PROVISIONED_DEBUG_TOKEN, false) &&
                !debugStorePrefs.getString(DEBUG_SECRET_KEY, null).isNullOrEmpty()
            if (hasProvisionedToken) {
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
                AppCheckDiagnostics.activeProviderMode = "DebugToken"
                AppCheckDiagnostics.lastErrorMessage = null
                Log.d(TAG, "DebugAppCheckProviderFactory installed from previously provisioned token.")
                return
            }

            // Clear any stale auto-generated unregistered debug secret on physical devices
            if (debugStorePrefs.contains(DEBUG_SECRET_KEY)) {
                debugStorePrefs.edit().remove(DEBUG_SECRET_KEY).commit()
            }
            Log.w(TAG, "No provisioned debug token found; installing ResilientRecaptchaAppCheckProviderFactory.")
        }

        val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
        if (resId != 0) {
            val siteKey = context.getString(resId)
            if (siteKey.isNotBlank()) {
                AppCheckDiagnostics.activeSiteKeyPrefix = siteKey.take(12)
                AppCheckDiagnostics.activeProviderMode = "RecaptchaEnterprise"
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    ResilientRecaptchaAppCheckProviderFactory(siteKey)
                )
                Log.d(TAG, "ResilientRecaptchaAppCheckProviderFactory installed.")
            } else {
                AppCheckDiagnostics.lastErrorMessage = "recaptcha_site_key string resource is blank."
                Log.e(TAG, "recaptcha_site_key resource string is blank.")
            }
        } else {
            AppCheckDiagnostics.lastErrorMessage = "recaptcha_site_key string resource not found."
            Log.e(TAG, "recaptcha_site_key string resource not found in package.")
        }
    } catch (e: Exception) {
        AppCheckDiagnostics.recordError("InitError", e)
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

/**
 * Installs a user-supplied registered Firebase App Check Debug Token (UUID) in debug builds
 * for devices where reCAPTCHA Enterprise cannot complete attestation (e.g., custom ROMs without Play).
 */
fun installManualDebugToken(context: Context, rawToken: String): Boolean {
    val cleanToken = rawToken.trim()
    if (!BuildConfig.DEBUG || cleanToken.length < 16) return false
    return try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        val persistenceKey = FirebaseApp.getInstance().persistenceKey
        val debugStorePrefs = context.getSharedPreferences(
            "$DEBUG_STORE_PREFIX$persistenceKey",
            Context.MODE_PRIVATE
        )
        val harnessPrefs = context.getSharedPreferences(
            HARNESS_PREFS_NAME,
            Context.MODE_PRIVATE
        )
        debugStorePrefs.edit().putString(DEBUG_SECRET_KEY, cleanToken).commit()
        harnessPrefs.edit().putBoolean(KEY_PROVISIONED_DEBUG_TOKEN, true).commit()

        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
        AppCheckDiagnostics.activeProviderMode = "DebugToken"
        AppCheckDiagnostics.lastErrorMessage = null
        true
    } catch (e: Exception) {
        AppCheckDiagnostics.recordError("DebugToken", e)
        false
    }
}

fun clearManualDebugToken(context: Context) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        val persistenceKey = FirebaseApp.getInstance().persistenceKey
        context.getSharedPreferences("$DEBUG_STORE_PREFIX$persistenceKey", Context.MODE_PRIVATE)
            .edit().remove(DEBUG_SECRET_KEY).commit()
        context.getSharedPreferences(HARNESS_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_PROVISIONED_DEBUG_TOKEN, false).commit()
        configureAppCheck(context, null)
    } catch (e: Exception) {
        Log.w(TAG, "Error clearing manual debug token: ${e.message}")
    }
}

fun hasProvisionedDebugToken(context: Context): Boolean {
    return try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        val persistenceKey = FirebaseApp.getInstance().persistenceKey
        val harnessPrefs = context.getSharedPreferences(HARNESS_PREFS_NAME, Context.MODE_PRIVATE)
        val debugStorePrefs = context.getSharedPreferences("$DEBUG_STORE_PREFIX$persistenceKey", Context.MODE_PRIVATE)
        harnessPrefs.getBoolean(KEY_PROVISIONED_DEBUG_TOKEN, false) &&
            !debugStorePrefs.getString(DEBUG_SECRET_KEY, null).isNullOrEmpty()
    } catch (_: Exception) {
        false
    }
}

/**
 * Verifies/acquires a real Firebase App Check token before Firebase Auth operations.
 * Uses `getAppCheckToken(forceRefresh)` so actual exceptions are surfaced (never masked by placeholder tokens).
 */
suspend fun ensureValidAppCheckToken(context: Context): Result<String> = withContext(Dispatchers.IO) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        val appCheck = FirebaseAppCheck.getInstance()
        try {
            val token = appCheck.getAppCheckToken(false).await()
            if (token.token.isNotBlank()) {
                AppCheckDiagnostics.recordSuccess(AppCheckDiagnostics.activeProviderMode)
                return@withContext Result.success(token.token)
            }
        } catch (firstErr: Exception) {
            Log.w(TAG, "Initial App Check token attempt failed, retrying once: ${firstErr.message}")
            delay(700L)
        }

        val retryToken = appCheck.getAppCheckToken(true).await()
        if (retryToken.token.isBlank()) {
            throw FirebaseException("Firebase App Check returned an empty token.")
        }
        AppCheckDiagnostics.recordSuccess(AppCheckDiagnostics.activeProviderMode)
        Result.success(retryToken.token)
    } catch (e: Exception) {
        AppCheckDiagnostics.recordError(AppCheckDiagnostics.activeProviderMode, e)
        Log.e(TAG, "ensureValidAppCheckToken failed: ${e.message}", e)
        Result.failure(e)
    }
}
