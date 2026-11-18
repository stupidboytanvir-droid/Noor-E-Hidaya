package com.example.data.security

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"

private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val HARNESS_PREFS_NAME = "com.example.appcheck.harness_prefs"
private const val KEY_PROVISIONED_DEBUG_TOKEN = "has_provisioned_debug_token"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

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
                Log.d(TAG, "DebugAppCheckProviderFactory installed with provisioned debug token.")
                return
            }

            val hasProvisionedToken = harnessPrefs.getBoolean(KEY_PROVISIONED_DEBUG_TOKEN, false) &&
                !debugStorePrefs.getString(DEBUG_SECRET_KEY, null).isNullOrEmpty()
            if (hasProvisionedToken) {
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
                Log.d(TAG, "DebugAppCheckProviderFactory installed from previously provisioned token.")
                return
            }

            // Clear any stale auto-generated unregistered debug secret on physical devices
            if (debugStorePrefs.contains(DEBUG_SECRET_KEY)) {
                debugStorePrefs.edit().remove(DEBUG_SECRET_KEY).commit()
            }
            Log.w(TAG, "No provisioned debug token found; falling back to RecaptchaAppCheckProviderFactory for physical device.")
        }

        val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
        if (resId != 0) {
            val siteKey = context.getString(resId)
            if (siteKey.isNotBlank()) {
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                )
                Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
            } else {
                Log.e(TAG, "recaptcha_site_key resource string is blank.")
            }
        } else {
            Log.e(TAG, "recaptcha_site_key string resource not found in package.")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}
