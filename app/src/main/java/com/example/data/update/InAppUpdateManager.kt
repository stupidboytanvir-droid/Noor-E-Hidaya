package com.example.data.update

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Enterprise In-App Updates Manager for Google Play releases.
 * Handles checking for updates, flexible background downloading,
 * and immediate critical update flows gracefully.
 */
class InAppUpdateManager(private val context: Context) {

    companion object {
        private const val TAG = "InAppUpdateManager"
        const val UPDATE_REQUEST_CODE = 9010
    }

    sealed class UpdateUiState {
        data object Idle : UpdateUiState()
        data object Checking : UpdateUiState()
        data class Available(val availableVersionCode: Int, val isImmediate: Boolean) : UpdateUiState()
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
     * Checks Google Play for new version availability.
     */
    fun checkForUpdates(onComplete: ((Boolean) -> Unit)? = null) {
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
                _updateState.value = UpdateUiState.UpToDate // Treat gracefully when sideloaded/debug
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
