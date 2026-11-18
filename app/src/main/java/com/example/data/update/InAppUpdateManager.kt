package com.example.data.update

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.BuildConfig
import com.example.MainActivity
import com.example.R
import com.example.data.util.LocaleHelper
import com.example.ui.theme.LocalizationRegistry
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class GitHubUpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val apkDownloadUrl: String,
    val releaseNotes: String,
    val releasePageUrl: String
)

/**
 * Unified App Update Manager supporting both:
 * 1. Automatic GitHub Release & version.json update checking + high-priority notifications + direct APK download.
 * 2. Google Play In-App Updates when installed from Google Play.
 */
class InAppUpdateManager(private val context: Context) {

    companion object {
        private const val TAG = "InAppUpdateManager"
        const val UPDATE_REQUEST_CODE = 9010

        const val GITHUB_OWNER = "stupidboytanvir-droid"
        const val GITHUB_REPO = "Noor-E-Hidaya"
        const val GITHUB_LATEST_RELEASE_API =
            "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
        const val GITHUB_RELEASES_LIST_API =
            "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases?per_page=1"
        const val GITHUB_VERSION_JSON_URL =
            "https://raw.githubusercontent.com/$GITHUB_OWNER/$GITHUB_REPO/main/version.json"
        const val FALLBACK_RAW_APK_URL =
            "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/raw/main/app-debug.apk"

        const val UPDATE_CHANNEL_ID = "noor_app_updates_channel"
        const val UPDATE_NOTIFICATION_ID = 9091
        const val EXTRA_UPDATE_DOWNLOAD_URL = "extra_update_download_url"
        const val EXTRA_UPDATE_VERSION_NAME = "extra_update_version_name"

        private const val PREFS_UPDATE = "noor_update_prefs"
        private const val KEY_NOTIFIED_VERSION = "last_notified_version_key"

        /**
         * Extracts numeric version parts from strings like "v1.2", "1.2.0", "Version-2".
         */
        internal fun parseVersionParts(raw: String): List<Int> {
            val trimmed = raw.trim()
            if (trimmed.isBlank()) return emptyList()
            val matches = Regex("\\d+").findAll(trimmed).mapNotNull { it.value.toIntOrNull() }.toList()
            return matches
        }

        internal fun compareVersionNames(a: String, b: String): Int {
            val partsA = parseVersionParts(a)
            val partsB = parseVersionParts(b)
            if (partsA.isEmpty() || partsB.isEmpty()) return 0
            val maxLen = maxOf(partsA.size, partsB.size)
            for (i in 0 until maxLen) {
                val va = partsA.getOrElse(i) { 0 }
                val vb = partsB.getOrElse(i) { 0 }
                if (va != vb) return va.compareTo(vb)
            }
            return 0
        }

        internal fun isNewerVersion(
            remoteVersionName: String,
            remoteVersionCode: Int,
            currentVersionName: String = BuildConfig.VERSION_NAME,
            currentVersionCode: Int = BuildConfig.VERSION_CODE
        ): Boolean {
            if (remoteVersionCode > 0 && currentVersionCode > 0) {
                if (remoteVersionCode > currentVersionCode) return true
                if (remoteVersionCode < currentVersionCode) return false
            }
            return compareVersionNames(remoteVersionName, currentVersionName) > 0
        }

        fun createUpdateNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val uiLang = LocaleHelper.getUiLanguageSync(context)
                val channelName = LocalizationRegistry.translate(
                    uiLang,
                    "অ্যাপ আপডেট ও নতুন সংস্করণ",
                    "App Updates & New Releases"
                )
                val channelDesc = LocalizationRegistry.translate(
                    uiLang,
                    "নতুন বাগ ফিক্স এবং আপডেট এপিকে ডাউনলোড নোটিফিকেশন",
                    "Notifications for new bug fixes and direct APK update downloads"
                )
                val channel = NotificationChannel(
                    UPDATE_CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = channelDesc
                    enableLights(true)
                    lightColor = Color.parseColor("#D4AF37")
                    enableVibration(true)
                }
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.createNotificationChannel(channel)
            }
        }

        fun showUpdateNotificationIfNeeded(context: Context, info: GitHubUpdateInfo) {
            val appCtx = context.applicationContext
            val prefs = appCtx.getSharedPreferences(PREFS_UPDATE, Context.MODE_PRIVATE)
            val versionKey = "${info.versionCode}_${info.versionName}"
            if (prefs.getString(KEY_NOTIFIED_VERSION, null) == versionKey) {
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    appCtx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) return
            }

            createUpdateNotificationChannel(appCtx)

            val uiLang = LocaleHelper.getUiLanguageSync(appCtx)
            val downloadIntent = Intent(appCtx, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_UPDATE_DOWNLOAD_URL, info.apkDownloadUrl)
                putExtra(EXTRA_UPDATE_VERSION_NAME, info.versionName)
            }

            val pendingIntent = PendingIntent.getActivity(
                appCtx,
                UPDATE_NOTIFICATION_ID,
                downloadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = LocalizationRegistry.translate(
                uiLang,
                "নূর-এ-হেদায়াত নতুন আপডেট (v${info.versionName})",
                "Noor-e-Hidayah Update Ready (v${info.versionName})"
            )
            val body = LocalizationRegistry.translate(
                uiLang,
                "নতুন বাগ ফিক্স ও আপডেট এসেছে। সরাসরি APK ডাউনলোড করতে এখানে ট্যাপ করুন।",
                "New fixes and improvements are available. Tap to download the APK directly."
            )

            val notification = NotificationCompat.Builder(appCtx, UPDATE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            try {
                val nm = appCtx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(UPDATE_NOTIFICATION_ID, notification)
                prefs.edit().putString(KEY_NOTIFIED_VERSION, versionKey).apply()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to post update notification: ${e.message}")
            }
        }

        /**
         * Downloads the APK directly via Android DownloadManager into Downloads with notification progress,
         * and falls back to opening the download URL in browser if DownloadManager is unavailable.
         */
        fun downloadApkDirectly(
            context: Context,
            downloadUrl: String,
            versionName: String
        ) {
            val safeUrl = downloadUrl.trim().ifBlank { FALLBACK_RAW_APK_URL }
            val uiLang = LocaleHelper.getUiLanguageSync(context)
            val cleanVer = versionName.trim().removePrefix("v").ifBlank { "latest" }
            val fileName = "Noor-e-Hidayah-v$cleanVer.apk"

            try {
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                if (dm != null) {
                    val request = DownloadManager.Request(Uri.parse(safeUrl))
                        .setTitle("Noor-e-Hidayah v$cleanVer")
                        .setDescription(
                            LocalizationRegistry.translate(
                                uiLang,
                                "আপডেট APK ডাউনলোড হচ্ছে...",
                                "Downloading update APK..."
                            )
                        )
                        .setMimeType("application/vnd.android.package-archive")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

                    dm.enqueue(request)
                    Toast.makeText(
                        context,
                        LocalizationRegistry.translate(
                            uiLang,
                            "নতুন ভার্সন ডাউনলোড শুরু হয়েছে — নোটিফিকেশন বার দেখুন",
                            "Update download started — check your notification bar"
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            } catch (e: Exception) {
                Log.w(TAG, "DownloadManager failed, falling back to browser: ${e.message}")
            }

            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open update download URL", e)
            }
        }
    }

    sealed class UpdateUiState {
        data object Idle : UpdateUiState()
        data object Checking : UpdateUiState()
        data class Available(val availableVersionCode: Int, val isImmediate: Boolean) : UpdateUiState()
        data class AvailableGitHub(val info: GitHubUpdateInfo) : UpdateUiState()
        data object Downloading : UpdateUiState()
        data object Downloaded : UpdateUiState()
        data object UpToDate : UpdateUiState()
        data class Error(val message: String) : UpdateUiState()
    }

    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(context)
    }

    private val _updateState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val updateState: StateFlow<UpdateUiState> = _updateState.asStateFlow()

    private var cachedAppUpdateInfo: AppUpdateInfo? = null

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                _updateState.value = UpdateUiState.Downloading
            }
            InstallStatus.DOWNLOADED -> {
                _updateState.value = UpdateUiState.Downloaded
            }
            InstallStatus.FAILED -> {
                _updateState.value = UpdateUiState.Error("ইনস্টলেশন ব্যর্থ হয়েছে")
            }
            InstallStatus.INSTALLED -> {
                _updateState.value = UpdateUiState.UpToDate
            }
            else -> {}
        }
    }

    init {
        try {
            appUpdateManager.registerListener(installStateUpdatedListener)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to register install listener: ${e.message}")
        }
    }

    /**
     * Checks GitHub Releases + raw version.json for new APK updates.
     * Posts an Android notification if a newer version is published and notifyUser = true.
     */
    suspend fun checkGitHubUpdates(notifyUser: Boolean = true): GitHubUpdateInfo? =
        withContext(Dispatchers.IO) {
            _updateState.value = UpdateUiState.Checking
            try {
                val versionJsonCandidate = fetchFromVersionJson()
                val releaseCandidate = fetchFromGitHubReleases()

                val bestCandidate = pickNewerCandidate(versionJsonCandidate, releaseCandidate)
                if (bestCandidate != null &&
                    isNewerVersion(
                        remoteVersionName = bestCandidate.versionName,
                        remoteVersionCode = bestCandidate.versionCode
                    )
                ) {
                    _updateState.value = UpdateUiState.AvailableGitHub(bestCandidate)
                    if (notifyUser) {
                        showUpdateNotificationIfNeeded(context, bestCandidate)
                    }
                    return@withContext bestCandidate
                } else {
                    _updateState.value = UpdateUiState.UpToDate
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.w(TAG, "GitHub update check failed: ${e.message}")
                if (_updateState.value is UpdateUiState.Checking) {
                    _updateState.value = UpdateUiState.UpToDate
                }
                return@withContext null
            }
        }

    private fun pickNewerCandidate(
        first: GitHubUpdateInfo?,
        second: GitHubUpdateInfo?
    ): GitHubUpdateInfo? {
        if (first == null) return second
        if (second == null) return first
        // If release has a direct release-asset APK and same/newer version, prefer it
        val secondNewer = isNewerVersion(
            remoteVersionName = second.versionName,
            remoteVersionCode = second.versionCode,
            currentVersionName = first.versionName,
            currentVersionCode = first.versionCode
        )
        return if (secondNewer) {
            second
        } else if (compareVersionNames(second.versionName, first.versionName) == 0 &&
            second.apkDownloadUrl.contains("/releases/download/")
        ) {
            second.copy(
                versionCode = maxOf(first.versionCode, second.versionCode),
                releaseNotes = second.releaseNotes.ifBlank { first.releaseNotes }
            )
        } else {
            first
        }
    }

    private fun fetchFromVersionJson(): GitHubUpdateInfo? {
        val body = httpGetString(GITHUB_VERSION_JSON_URL) ?: return null
        return try {
            val json = JSONObject(body)
            val versionCode = json.optInt("versionCode", 0)
            val versionName = json.optString("versionName", "").trim()
            val apkUrl = json.optString("apkUrl", FALLBACK_RAW_APK_URL).trim()
                .ifBlank { FALLBACK_RAW_APK_URL }
            val notes = json.optString("releaseNotes", "").trim()
            if (versionName.isBlank() && versionCode <= 0) return null
            GitHubUpdateInfo(
                versionCode = versionCode,
                versionName = versionName.ifBlank { versionCode.toString() },
                apkDownloadUrl = apkUrl,
                releaseNotes = notes,
                releasePageUrl = "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases"
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed parsing version.json: ${e.message}")
            null
        }
    }

    private fun fetchFromGitHubReleases(): GitHubUpdateInfo? {
        val latestBody = httpGetString(GITHUB_LATEST_RELEASE_API)
        val releaseObj = if (latestBody != null) {
            try {
                JSONObject(latestBody)
            } catch (_: Exception) {
                null
            }
        } else {
            val listBody = httpGetString(GITHUB_RELEASES_LIST_API) ?: return null
            try {
                val arr = JSONArray(listBody)
                if (arr.length() > 0) arr.optJSONObject(0) else null
            } catch (_: Exception) {
                null
            }
        } ?: return null

        val tagName = releaseObj.optString("tag_name", "").trim()
        val releaseName = releaseObj.optString("name", "").trim()
        val versionLabel = (tagName.ifBlank { releaseName }).removePrefix("v").trim()
        val notes = releaseObj.optString("body", "").trim()
        val htmlUrl = releaseObj.optString(
            "html_url",
            "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases"
        )

        var apkUrl: String? = null
        val assets = releaseObj.optJSONArray("assets")
        if (assets != null) {
            for (i in 0 until assets.length()) {
                val asset = assets.optJSONObject(i) ?: continue
                val name = asset.optString("name", "").lowercase()
                val download = asset.optString("browser_download_url", "")
                if (name.endsWith(".apk") && download.isNotBlank()) {
                    apkUrl = download
                    break
                }
            }
        }

        // Only surface a GitHub release if an APK asset is actually attached
        if (apkUrl.isNullOrBlank() || versionLabel.isBlank()) return null

        return GitHubUpdateInfo(
            versionCode = 0,
            versionName = versionLabel,
            apkDownloadUrl = apkUrl,
            releaseNotes = notes,
            releasePageUrl = htmlUrl
        )
    }

    private fun httpGetString(urlString: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8_000
                readTimeout = 8_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "Noor-e-Hidayah-Android/${BuildConfig.VERSION_NAME}")
                setRequestProperty("Cache-Control", "no-cache")
            }
            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    /**
     * Checks Google Play for new version availability.
     */
    fun checkForUpdates(onComplete: ((Boolean) -> Unit)? = null) {
        if (_updateState.value is UpdateUiState.AvailableGitHub) {
            onComplete?.invoke(true)
            return
        }
        _updateState.value = UpdateUiState.Checking

        try {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                cachedAppUpdateInfo = appUpdateInfo
                val isAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                val isFlexibleAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                val isImmediateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

                if (isAvailable && (isFlexibleAllowed || isImmediateAllowed)) {
                    val isImmediate = isImmediateAllowed && !isFlexibleAllowed
                    _updateState.value = UpdateUiState.Available(
                        availableVersionCode = appUpdateInfo.availableVersionCode(),
                        isImmediate = isImmediate
                    )
                    onComplete?.invoke(true)
                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    _updateState.value = UpdateUiState.Downloaded
                    onComplete?.invoke(true)
                } else {
                    _updateState.value = UpdateUiState.UpToDate
                    onComplete?.invoke(false)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Update check failed or not on Play Store: ${exception.message}")
                _updateState.value = UpdateUiState.UpToDate
                onComplete?.invoke(false)
            }
        } catch (e: Exception) {
            Log.w(TAG, "AppUpdateManager unavailable: ${e.message}")
            _updateState.value = UpdateUiState.UpToDate
            onComplete?.invoke(false)
        }
    }

    /**
     * Starts the update flow using the specified activity.
     */
    fun startUpdate(activity: Activity, immediate: Boolean = false) {
        val currentState = _updateState.value
        if (currentState is UpdateUiState.AvailableGitHub) {
            downloadApkDirectly(
                context = activity,
                downloadUrl = currentState.info.apkDownloadUrl,
                versionName = currentState.info.versionName
            )
            return
        }
        val updateInfo = cachedAppUpdateInfo ?: return
        val type = if (immediate) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE

        try {
            val options = AppUpdateOptions.newBuilder(type).build()
            appUpdateManager.startUpdateFlowForResult(updateInfo, activity, options, UPDATE_REQUEST_CODE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start update flow", e)
            _updateState.value = UpdateUiState.Error("আপডেট শুরু করা যায়নি: ${e.message}")
        }
    }

    /**
     * Completes flexible update installation by triggering restart.
     */
    fun completeUpdate() {
        try {
            appUpdateManager.completeUpdate()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to complete update", e)
        }
    }

    /**
     * Cleans up listener on lifecycle teardown.
     */
    fun unregister() {
        try {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        } catch (_: Exception) {}
    }
}
