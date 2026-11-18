package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthRepository
import com.example.data.auth.AuthState
import com.example.data.auth.UserSession
import com.example.data.preference.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = AppPreferences(application)
    val repository = AuthRepository(application, prefs)

    val authState: StateFlow<AuthState> = repository.authState
    val currentUserSession: StateFlow<UserSession> = repository.currentUserSession

    val isUserLoggedIn: StateFlow<Boolean> = prefs.isUserLoggedIn
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val currentUserName: StateFlow<String> = prefs.userName
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppPreferences.DEFAULT_USER_NAME)

    val currentUserEmail: StateFlow<String> = prefs.userEmail
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppPreferences.DEFAULT_USER_EMAIL)

    val currentUserPhone: StateFlow<String> = prefs.userPhone
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppPreferences.DEFAULT_USER_PHONE)

    val currentAuthProvider: StateFlow<String> = prefs.authProvider
        .stateIn(viewModelScope, SharingStarted.Eagerly, "GOOGLE")

    val currentUserPhotoUrl: StateFlow<String?> = prefs.userPhotoUrl
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppPreferences.DEFAULT_USER_PHOTO_URL)

    // UI Input states
    private val _phoneNumberInput = MutableStateFlow("")
    val phoneNumberInput: StateFlow<String> = _phoneNumberInput.asStateFlow()

    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput.asStateFlow()

    private val _currentVerificationId = MutableStateFlow<String?>(null)
    val currentVerificationId: StateFlow<String?> = _currentVerificationId.asStateFlow()

    val cloudSyncRepo = com.example.data.cloud.CloudSyncRepository(
        application,
        prefs,
        com.example.data.local.AppDatabase.getInstance(application)
    )
    val syncStatus: StateFlow<com.example.data.cloud.SyncStatus> = cloudSyncRepo.syncStatus

    init {
        viewModelScope.launch {
            authState.collect { state ->
                if (state is AuthState.PhoneCodeSent) {
                    _currentVerificationId.value = state.verificationId
                } else if (state is AuthState.Success &&
                    state.user.isLoggedIn &&
                    state.user.uid.isNotBlank() &&
                    state.user.provider != com.example.data.auth.AuthProviderType.GUEST
                ) {
                    launch(kotlinx.coroutines.Dispatchers.IO) {
                        cloudSyncRepo.restoreUserDataFromCloud(state.user.uid)
                        cloudSyncRepo.syncProfileAndStats(state.user)
                    }
                }
            }
        }

        // 100% Zero-click automatic restore on app launch for authenticated user
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val uid = cloudSyncRepo.getActiveUserId()
            if (!uid.isNullOrEmpty()) {
                cloudSyncRepo.restoreUserDataFromCloud(uid)
            }
        }
    }

    fun triggerCloudBackup() {
        val fbUser = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        } catch (_: Exception) {
            null
        }
        if (fbUser == null) {
            viewModelScope.launch {
                val lang = prefs.uiLanguage.first()
                cloudSyncRepo.triggerUnauthenticatedStatus(
                    com.example.ui.theme.localizedText(
                        lang,
                        "ক্লাউড ব্যাকআপের জন্য প্রথমে গুগল বা ফোন দিয়ে লগইন করুন",
                        "Please sign in with Google or Phone first for cloud backup"
                    )
                )
            }
            return
        }
        val user = currentUserSession.value.copy(uid = fbUser.uid)
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            cloudSyncRepo.syncProfileAndStats(user)
            cloudSyncRepo.backupAllLocalData(fbUser.uid)
        }
    }

    fun triggerCloudRestore() {
        val fbUser = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        } catch (_: Exception) {
            null
        }
        if (fbUser == null) {
            viewModelScope.launch {
                val lang = prefs.uiLanguage.first()
                cloudSyncRepo.triggerUnauthenticatedStatus(
                    com.example.ui.theme.localizedText(
                        lang,
                        "ক্লাউড রিস্টোরের জন্য প্রথমে গুগল বা ফোন দিয়ে লগইন করুন",
                        "Please sign in with Google or Phone first to restore from cloud"
                    )
                )
            }
            return
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            cloudSyncRepo.restoreUserDataFromCloud(fbUser.uid)
        }
    }

    fun updateProfilePhoto(dataUri: String) {
        if (dataUri.isBlank()) return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            prefs.setUserPhotoUrl(dataUri)
            val fbUser = try {
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            } catch (_: Exception) {
                null
            }
            if (fbUser != null && fbUser.uid.isNotBlank()) {
                val updatedUser = currentUserSession.value.copy(
                    uid = fbUser.uid,
                    photoUrl = dataUri,
                    isLoggedIn = true
                )
                cloudSyncRepo.syncProfileAndStats(updatedUser)
            }
        }
    }

    fun onPhoneNumberChanged(number: String) {
        // Allow up to 11 digits (e.g. 01712345678 or 1712345678)
        val digitsOnly = number.filter { it.isDigit() }.take(11)
        _phoneNumberInput.value = digitsOnly
    }

    fun onOtpChanged(otp: String) {
        val digitsOnly = otp.filter { it.isDigit() }.take(6)
        _otpInput.value = digitsOnly
    }

    fun sendPhoneOtp(activity: Activity) {
        val raw = _phoneNumberInput.value.trim()
        repository.sendPhoneOtp(activity, raw)
    }

    fun resendPhoneOtp(activity: Activity) {
        val state = authState.value
        if (state is AuthState.PhoneCodeSent) {
            repository.sendPhoneOtp(activity, state.phoneNumber, state.resendToken)
        } else {
            sendPhoneOtp(activity)
        }
    }

    fun verifyOtp() {
        val vid = _currentVerificationId.value ?: return
        val code = _otpInput.value.trim()
        val phone = repository.formatBdPhoneNumber(_phoneNumberInput.value)
        repository.verifyOtp(vid, code, phone)
    }

    fun signInWithGoogle(activity: Activity) {
        repository.signInWithGoogle(activity)
    }

    fun continueAsGuest() {
        repository.continueAsGuest()
    }

    fun signOut() {
        _phoneNumberInput.value = ""
        _otpInput.value = ""
        _currentVerificationId.value = null
        repository.signOut()
    }

    fun resetToPhoneInput() {
        _otpInput.value = ""
        _currentVerificationId.value = null
        repository.resetState()
    }
}
