package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.example.data.preference.AppPreferences
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

enum class AuthProviderType {
    GOOGLE,
    PHONE,
    GUEST
}

data class UserSession(
    val uid: String = "",
    val displayName: String = "",
    val email: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val provider: AuthProviderType = AuthProviderType.GUEST,
    val isLoggedIn: Boolean = false
)

sealed class AuthState {
    object Idle : AuthState()
    data class Loading(val message: String) : AuthState()
    data class PhoneCodeSent(
        val verificationId: String,
        val phoneNumber: String,
        val resendToken: PhoneAuthProvider.ForceResendingToken?
    ) : AuthState()
    data class Success(val user: UserSession) : AuthState()
    data class Error(
        val message: String,
        val suggestGoogleFallback: Boolean = false,
        val isAppCheckError: Boolean = false
    ) : AuthState()
}

class AuthRepository(
    private val context: Context,
    private val appPreferences: AppPreferences
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth initialization fallback: ${e.message}")
            null
        }
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUserSession = MutableStateFlow(
        UserSession(
            uid = "",
            displayName = AppPreferences.DEFAULT_USER_NAME,
            email = null,
            phoneNumber = null,
            photoUrl = null,
            provider = AuthProviderType.GUEST,
            isLoggedIn = false
        )
    )
    val currentUserSession: StateFlow<UserSession> = _currentUserSession.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            val fbUser = auth?.currentUser
            if (fbUser != null && fbUser.uid.isNotBlank()) {
                val storedProviderStr = appPreferences.authProvider.first()
                val provider = when {
                    fbUser.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } -> AuthProviderType.GOOGLE
                    fbUser.providerData.any { it.providerId == PhoneAuthProvider.PROVIDER_ID } -> AuthProviderType.PHONE
                    else -> try {
                        AuthProviderType.valueOf(storedProviderStr)
                    } catch (_: Exception) {
                        AuthProviderType.GOOGLE
                    }
                }
                val resolvedName = fbUser.displayName?.takeIf { it.isNotBlank() }
                    ?: appPreferences.userName.first().takeIf { it.isNotBlank() && it != AppPreferences.DEFAULT_USER_NAME }
                    ?: fbUser.email?.substringBefore("@")
                    ?: fbUser.phoneNumber
                    ?: "বিশ্বস্ত মুমিন"
                val resolvedEmail = fbUser.email?.takeIf { it.isNotBlank() }
                    ?: appPreferences.userEmail.first().takeIf { it.isNotBlank() }
                val resolvedPhone = fbUser.phoneNumber?.takeIf { it.isNotBlank() }
                    ?: appPreferences.userPhone.first().takeIf { it.isNotBlank() }
                val savedLocalPhoto = appPreferences.userPhotoUrl.first()?.takeIf { it.isNotBlank() }
                val resolvedPhoto = when {
                    savedLocalPhoto != null && savedLocalPhoto.startsWith("data:image") -> savedLocalPhoto
                    else -> fbUser.photoUrl?.toString()?.takeIf { it.isNotBlank() } ?: savedLocalPhoto
                }

                val restoredSession = UserSession(
                    uid = fbUser.uid,
                    displayName = resolvedName,
                    email = resolvedEmail,
                    phoneNumber = resolvedPhone,
                    photoUrl = resolvedPhoto,
                    provider = provider,
                    isLoggedIn = true
                )
                appPreferences.saveUserSession(
                    name = restoredSession.displayName,
                    email = restoredSession.email ?: "",
                    phone = restoredSession.phoneNumber ?: "",
                    provider = restoredSession.provider.name,
                    photoUrl = restoredSession.photoUrl,
                    uid = restoredSession.uid
                )
                _currentUserSession.value = restoredSession
            } else {
                // Never expose a phantom logged-in session when FirebaseAuth.currentUser is null
                if (appPreferences.isUserLoggedIn.first()) {
                    appPreferences.clearUserSession()
                }
                _currentUserSession.value = UserSession(
                    uid = "",
                    displayName = AppPreferences.DEFAULT_USER_NAME,
                    email = null,
                    phoneNumber = null,
                    photoUrl = null,
                    provider = AuthProviderType.GUEST,
                    isLoggedIn = false
                )
            }

            appPreferences.isUserLoggedIn.collect { isLogged ->
                if (!isLogged) {
                    _currentUserSession.value = UserSession(
                        uid = "",
                        displayName = AppPreferences.DEFAULT_USER_NAME,
                        email = null,
                        phoneNumber = null,
                        photoUrl = null,
                        provider = AuthProviderType.GUEST,
                        isLoggedIn = false
                    )
                }
            }
        }
    }

    /**
     * Formats Bangladeshi phone number to E.164 format (+880XXXXXXXXXX)
     */
    fun formatBdPhoneNumber(rawNumber: String): String {
        val cleaned = rawNumber.replace("[^0-9+]".toRegex(), "")
        return when {
            cleaned.startsWith("+88001") && cleaned.length >= 15 -> "+880${cleaned.substring(5)}"
            cleaned.startsWith("+880") -> cleaned
            cleaned.startsWith("88001") && cleaned.length >= 14 -> "+880${cleaned.substring(4)}"
            cleaned.startsWith("880") -> "+$cleaned"
            cleaned.startsWith("01") -> "+880${cleaned.substring(1)}"
            cleaned.startsWith("1") && cleaned.length == 10 -> "+880$cleaned"
            else -> "+880$cleaned"
        }
    }

    /**
     * Validate Bangladeshi mobile number (+8801[3-9]XXXXXXXX)
     */
    fun isValidBdNumber(rawNumber: String): Boolean {
        val formatted = formatBdPhoneNumber(rawNumber)
        return formatted.matches(Regex("^\\+8801[3-9][0-9]{8}$"))
    }

    /**
     * Send real OTP SMS to BD Phone Number using Firebase Phone Auth.
     * Gracefully surfaces actionable guidance if Phone provider is disabled in Firebase Console.
     */
    fun sendPhoneOtp(
        activity: Activity,
        rawNumber: String,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null
    ) {
        val formattedNumber = formatBdPhoneNumber(rawNumber)
        if (!isValidBdNumber(rawNumber)) {
            _authState.value = AuthState.Error(
                "সঠিক বাংলাদেশি মোবাইল নম্বর প্রদান করুন (যেমন: 017XXXXXXXX)"
            )
            return
        }

        // Anti-Brute-Force Rate Limiting (Max 3 attempts per minute per number)
        if (!com.example.data.security.AuthRateLimiter.isAllowed("otp_$formattedNumber", maxAttempts = 3, windowMs = 60_000L)) {
            val cooldown = com.example.data.security.AuthRateLimiter.getCooldownSeconds("otp_$formattedNumber", maxAttempts = 3, windowMs = 60_000L)
            _authState.value = AuthState.Error(
                "অতিরিক্ত ওটিপি অনুরোধ করা হয়েছে। অনুগ্রহ করে $cooldown সেকেন্ড অপেক্ষা করুন।"
            )
            return
        }

        val firebaseAuth = auth
        if (firebaseAuth == null) {
            _authState.value = AuthState.Error(
                message = "ফায়ারবেস অথেনটিকেশন সেবা এই মুহূর্তে প্রস্তুত নয়। অনুগ্রহ করে গুগল একাউন্ট দিয়ে লগইন করুন।",
                suggestGoogleFallback = true
            )
            return
        }

        _authState.value = AuthState.Loading("এসএমএস ওটিপি কোড পাঠানো হচ্ছে...")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                scope.launch {
                    signInWithPhoneCredential(credential, formattedNumber)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                val authErrCode = (e as? FirebaseAuthException)?.errorCode ?: ""
                val msg = e.message.orEmpty()
                val isOpNotAllowed = authErrCode == "ERROR_OPERATION_NOT_ALLOWED" ||
                    msg.contains("operation is not allowed", ignoreCase = true) ||
                    msg.contains("provider is disabled", ignoreCase = true) ||
                    msg.contains("17006", ignoreCase = true)
                val isConfigOrIntegrityIssue = authErrCode == "ERROR_APP_NOT_AUTHORIZED" ||
                    authErrCode == "ERROR_MISSING_CLIENT_IDENTIFIER" ||
                    msg.contains("app is not authorized", ignoreCase = true) ||
                    msg.contains("Play Integrity", ignoreCase = true) ||
                    msg.contains("reCAPTCHA", ignoreCase = true) ||
                    msg.contains("App Check", ignoreCase = true)

                if (isOpNotAllowed || isConfigOrIntegrityIssue) {
                    Log.w(TAG, "Phone OTP unavailable ($authErrCode): $msg")
                } else {
                    Log.e(TAG, "Phone verification failed: $msg", e)
                }

                _authState.value = when {
                    isOpNotAllowed -> AuthState.Error(
                        message = "এই প্রজেক্টে Phone OTP সেবা Firebase Console থেকে এখনও সক্রিয় করা হয়নি (Authentication → Sign-in method → Phone)। তাৎক্ষণিক লগইনের জন্য নিচের 'গুগল একাউন্ট' বাটনটি ব্যবহার করুন।",
                        suggestGoogleFallback = true
                    )
                    isConfigOrIntegrityIssue -> AuthState.Error(
                        message = "এই ডিভাইসে ফোন ভেরিফিকেশন যাচাই সম্পন্ন করা যায়নি। অনুগ্রহ করে 'গুগল একাউন্ট' দিয়ে নিরাপদে সাইন-ইন করুন।",
                        suggestGoogleFallback = true
                    )
                    authErrCode == "ERROR_QUOTA_EXCEEDED" || msg.contains("quota", ignoreCase = true) -> AuthState.Error(
                        message = "দৈনিক ফ্রি এসএমএস কোটা সাময়িকভাবে শেষ। গুগল একাউন্ট দিয়ে সাইন-ইন করতে পারেন।",
                        suggestGoogleFallback = true
                    )
                    authErrCode == "ERROR_TOO_MANY_REQUESTS" || msg.contains("blocked all requests", ignoreCase = true) -> AuthState.Error(
                        message = "নিরাপত্তার কারণে সাময়িকভাবে অনুরোধ সীমিত করা হয়েছে। কিছুক্ষণ পর চেষ্টা করুন অথবা গুগল দিয়ে লগইন করুন।",
                        suggestGoogleFallback = true
                    )
                    msg.contains("network", ignoreCase = true) -> AuthState.Error(
                        "ইন্টারনেট সংযোগ চেক করে পুনরায় চেষ্টা করুন।"
                    )
                    else -> AuthState.Error(
                        message = "ওটিপি পাঠাতে সমস্যা হয়েছে: ${e.localizedMessage ?: "পুনরায় চেষ্টা করুন"}",
                        suggestGoogleFallback = true
                    )
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "Phone OTP code sent: $verificationId")
                _authState.value = AuthState.PhoneCodeSent(
                    verificationId = verificationId,
                    phoneNumber = formattedNumber,
                    resendToken = token
                )
            }
        }

        try {
            val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(formattedNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)

            if (resendToken != null) {
                optionsBuilder.setForceResendingToken(resendToken)
            }

            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Exception during verifyPhoneNumber: ${e.message}", e)
            _authState.value = AuthState.Error(
                message = "ফোন ভেরিফিকেশন শুরু করা যায়নি। অনুগ্রহ করে গুগল একাউন্ট দিয়ে লগইন করুন।",
                suggestGoogleFallback = true
            )
        }
    }

    /**
     * Verify 6-digit OTP code entered by user against FirebaseAuth
     */
    fun verifyOtp(
        verificationId: String,
        code: String,
        phoneNumber: String
    ) {
        if (verificationId.isBlank()) {
            _authState.value = AuthState.Error("প্রথমে আপনার নম্বরে ওটিপি কোড পাঠান")
            return
        }
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _authState.value = AuthState.Error("৬ সংখ্যার সঠিক ওটিপি কোড লিখুন")
            return
        }
        if (auth == null) {
            _authState.value = AuthState.Error(
                message = "ফায়ারবেস অথেনটিকেশন অনুপলব্ধ।",
                suggestGoogleFallback = true
            )
            return
        }

        _authState.value = AuthState.Loading("ওটিপি কোড যাচাই করা হচ্ছে...")

        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            scope.launch {
                signInWithPhoneCredential(credential, phoneNumber)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error constructing credential: ${e.message}", e)
            _authState.value = AuthState.Error("ওটিপি কোড সঠিক নয়। আবার চেষ্টা করুন।")
        }
    }

    private suspend fun signInWithPhoneCredential(
        credential: PhoneAuthCredential,
        phoneNumber: String
    ) {
        try {
            val firebaseAuth = auth ?: error("FirebaseAuth not initialized")
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: error("FirebaseUser missing after phone sign-in")

            val user = UserSession(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName?.takeIf { it.isNotBlank() }
                    ?: "মুসাফির (${(firebaseUser.phoneNumber ?: phoneNumber).takeLast(4)})",
                email = firebaseUser.email,
                phoneNumber = firebaseUser.phoneNumber ?: phoneNumber,
                photoUrl = firebaseUser.photoUrl?.toString(),
                provider = AuthProviderType.PHONE,
                isLoggedIn = true
            )
            completeSuccessfulLogin(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error signing in with phone credential: ${e.message}", e)
            val errMsg = when ((e as? FirebaseAuthException)?.errorCode) {
                "ERROR_INVALID_VERIFICATION_CODE" -> "আপনার দেওয়া ওটিপি কোডটি সঠিক নয়। অনুগ্রহ করে আবার দেখুন।"
                "ERROR_SESSION_EXPIRED" -> "ওটিপি কোডের মেয়াদ শেষ হয়ে গেছে। নতুন কোড পাঠান।"
                else -> "ওটিপি কোড যাচাই ব্যর্থ হয়েছে: ${e.localizedMessage ?: "সঠিক কোড দিন"}"
            }
            _authState.value = AuthState.Error(errMsg)
        }
    }

    /**
     * Real Google Sign-In via Jetpack Credential Manager + Firebase GoogleAuthProvider
     */
    fun signInWithGoogle(activity: Activity) {
        // Anti-Brute-Force Rate Limiting
        if (!com.example.data.security.AuthRateLimiter.isAllowed("google_signin", maxAttempts = 5, windowMs = 60_000L)) {
            val cooldown = com.example.data.security.AuthRateLimiter.getCooldownSeconds("google_signin", maxAttempts = 5, windowMs = 60_000L)
            _authState.value = AuthState.Error("অতিরিক্ত সাইন ইন অনুরোধ করা হয়েছে। অনুগ্রহ করে $cooldown সেকেন্ড অপেক্ষা করুন।")
            return
        }

        val firebaseAuth = auth
        if (firebaseAuth == null) {
            _authState.value = AuthState.Error("ফায়ারবেস অথেনটিকেশন সক্রিয় করা যায়নি।")
            return
        }

        _authState.value = AuthState.Loading("গুগল একাউন্ট পিকার খোলা হচ্ছে...")

        scope.launch {
            try {
                val credentialManager = CredentialManager.create(activity)
                @Suppress("DiscouragedApi")
                val clientIdResId = activity.resources.getIdentifier(
                    "default_web_client_id",
                    "string",
                    activity.packageName
                )
                val serverClientId = if (clientIdResId != 0) {
                    activity.getString(clientIdResId)
                } else {
                    ""
                }
                if (serverClientId.isBlank()) {
                    _authState.value = AuthState.Error("গুগল সাইন ইন কনফিগারেশন (google-services.json) পাওয়া যায়নি।")
                    return@launch
                }
                val googleOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build()

                // Launch native Google account picker on UI/Activity context
                val credentialResult = withContext(Dispatchers.Main) {
                    credentialManager.getCredential(
                        context = activity,
                        request = request
                    )
                }

                val credential = credentialResult.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
                    _authState.value = AuthState.Loading("নিরাপত্তা যাচাই ও লগইন সম্পন্ন হচ্ছে...")

                    // Pre-acquire & verify a genuine Firebase App Check token with automatic retry.
                    // Prevents FirebaseAuth from silently sending a dummy error placeholder token.
                    val appCheckResult = com.example.data.security.ensureValidAppCheckToken(context)
                    if (appCheckResult.isFailure) {
                        val cause = appCheckResult.exceptionOrNull()
                        val detail = cause?.let {
                            com.example.data.security.AppCheckDiagnostics.formatConciseError(it)
                        } ?: "Unknown App Check error"
                        Log.e(TAG, "App Check verification failed before Google Sign-In: $detail", cause)
                        _authState.value = AuthState.Error(
                            message = buildAppCheckFriendlyMessage(detail),
                            isAppCheckError = true
                        )
                        return@launch
                    }

                    val firebaseCred = GoogleAuthProvider.getCredential(googleCred.idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(firebaseCred).await()
                    val firebaseUser = authResult.user
                        ?: error("FirebaseUser missing after Google Sign-In")

                    val resolvedName = firebaseUser.displayName?.takeIf { it.isNotBlank() }
                        ?: googleCred.displayName?.takeIf { it.isNotBlank() }
                        ?: firebaseUser.email?.substringBefore("@")
                        ?: "সম্মানিত মুমিন"
                    val resolvedEmail = firebaseUser.email?.takeIf { it.isNotBlank() } ?: googleCred.id
                    val resolvedPhoto = firebaseUser.photoUrl?.toString()
                        ?: googleCred.profilePictureUri?.toString()

                    val session = UserSession(
                        uid = firebaseUser.uid,
                        displayName = resolvedName,
                        email = resolvedEmail,
                        phoneNumber = firebaseUser.phoneNumber,
                        photoUrl = resolvedPhoto,
                        provider = AuthProviderType.GOOGLE,
                        isLoggedIn = true
                    )
                    completeSuccessfulLogin(session)
                } else {
                    Log.w(TAG, "Unexpected credential type: ${credential.type}")
                    _authState.value = AuthState.Error("গুগল ক্রেডেনশিয়াল শনাক্ত করা যায়নি। আবার চেষ্টা করুন।")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "User cancelled Google Sign-In picker")
                _authState.value = AuthState.Idle
            } catch (e: NoCredentialException) {
                Log.w(TAG, "No Google credential found on device: ${e.message}")
                _authState.value = AuthState.Error(
                    "এই ডিভাইসে কোনো সক্রিয় গুগল (Gmail) একাউন্ট পাওয়া যায়নি। ডিভাইস সেটিংসে গুগল একাউন্ট যোগ করে পুনরায় চেষ্টা করুন।"
                )
            } catch (e: GoogleIdTokenParsingException) {
                Log.e(TAG, "Failed to parse Google ID token: ${e.message}", e)
                _authState.value = AuthState.Error("গুগল আইডি টোকেন যাচাই করা যায়নি।")
            } catch (e: GetCredentialException) {
                Log.e(TAG, "CredentialManager error (${e.type}): ${e.message}", e)
                _authState.value = AuthState.Error(
                    "গুগল একাউন্ট নির্বাচন ব্যর্থ হয়েছে: ${e.localizedMessage ?: "পুনরায় চেষ্টা করুন"}"
                )
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed: ${e.message}", e)
                val errMsg = e.message.orEmpty()
                val isAppCheck = errMsg.contains("App Check", ignoreCase = true)
                val friendlyMsg = if (isAppCheck) {
                    val lastDetail = com.example.data.security.AppCheckDiagnostics.lastErrorMessage
                        ?.takeIf { it.isNotBlank() }
                        ?: errMsg
                    buildAppCheckFriendlyMessage(lastDetail)
                } else {
                    "গুগল দিয়ে সাইন ইন করতে সমস্যা হয়েছে: ${e.localizedMessage ?: "ইন্টারনেট সংযোগ চেক করুন"}"
                }
                _authState.value = AuthState.Error(
                    message = friendlyMsg,
                    isAppCheckError = isAppCheck
                )
            }
        }
    }

    internal fun buildAppCheckFriendlyMessage(rawDetail: String): String {
        val clean = rawDetail.trim().take(160)
        return when {
            clean.contains("HTTP 403", ignoreCase = true) ||
                clean.contains("attestation failed", ignoreCase = true) ->
                "Firebase App Check এই ডিভাইসের নিরাপত্তা টোকেন অনুমোদন করেনি ($clean)। প্লে-স্টোর ছাড়া ইনস্টল করা ডিবাগ APK হলে নিচে Firebase Console থেকে রেজিস্টার্ড Debug Token বসিয়ে অথবা Firebase Console-এ App Check → Authentication এনফোর্সমেন্ট চেক করে আবার চেষ্টা করুন।"
            clean.contains("NETWORK", ignoreCase = true) ||
                clean.contains("timeout", ignoreCase = true) ||
                clean.contains("Unable to resolve host", ignoreCase = true) ->
                "দুর্বল ইন্টারনেট বা reCAPTCHA সংযোগে দেরি হওয়ায় নিরাপত্তা যাচাই সম্পন্ন হয়নি ($clean)। স্থিতিশীল ইন্টারনেটে আবার চেষ্টা করুন।"
            else ->
                "নিরাপত্তা যাচাই (App Check) সম্পন্ন করা যায়নি ($clean)। অনুগ্রহ করে আবার চেষ্টা করুন।"
        }
    }

    /**
     * Continue as Guest (Unauthenticated Offline Mode)
     */
    fun continueAsGuest() {
        val guestSession = UserSession(
            uid = "",
            displayName = AppPreferences.DEFAULT_USER_NAME,
            email = null,
            phoneNumber = null,
            photoUrl = null,
            provider = AuthProviderType.GUEST,
            isLoggedIn = false
        )
        scope.launch {
            appPreferences.clearUserSession()
            _currentUserSession.value = guestSession
            _authState.value = AuthState.Success(guestSession)
        }
    }

    private suspend fun completeSuccessfulLogin(session: UserSession) {
        val existingPhoto = appPreferences.userPhotoUrl.first()?.takeIf { it.isNotBlank() }
        val finalPhoto = if (existingPhoto != null && existingPhoto.startsWith("data:image")) {
            existingPhoto
        } else {
            session.photoUrl
        }
        val finalSession = session.copy(photoUrl = finalPhoto)
        appPreferences.saveUserSession(
            name = finalSession.displayName,
            email = finalSession.email ?: "",
            phone = finalSession.phoneNumber ?: "",
            provider = finalSession.provider.name,
            photoUrl = finalSession.photoUrl,
            uid = finalSession.uid
        )
        _currentUserSession.value = finalSession
        _authState.value = AuthState.Success(finalSession)
    }

    /**
     * Sign out user completely (Credential Manager + FirebaseAuth + Local Session) and revert to Guest mode
     */
    fun signOut() {
        scope.launch {
            withContext(NonCancellable) {
                try {
                    CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                    Log.w(TAG, "CredentialManager clearCredentialState warning: ${e.message}")
                }
                try {
                    auth?.signOut()
                } catch (e: Exception) {
                    Log.w(TAG, "Firebase sign out error: ${e.message}")
                }
                appPreferences.clearUserSession()
                _currentUserSession.value = UserSession(
                    uid = "",
                    displayName = AppPreferences.DEFAULT_USER_NAME,
                    email = null,
                    phoneNumber = null,
                    photoUrl = null,
                    provider = AuthProviderType.GUEST,
                    isLoggedIn = false
                )
                _authState.value = AuthState.Idle
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
