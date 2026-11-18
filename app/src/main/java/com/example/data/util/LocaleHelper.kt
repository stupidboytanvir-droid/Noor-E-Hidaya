package com.example.data.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

/**
 * Manages synchronous persistence and runtime application of Android Locale
 * alongside independent UI and Content (Quran/Dua/Hadith translation) languages
 * across all 12 supported global languages.
 */
object LocaleHelper {

    private const val SHARED_PREFS_NAME = "noor_hidayah_sync_prefs"
    private const val SYNC_KEY_UI_LANG = "sync_ui_language"
    private const val SYNC_KEY_CONTENT_LANG = "sync_content_language"
    private const val SYNC_KEY_INITIAL_LANG_SELECTED = "sync_initial_language_selected"

    const val LANG_BN = "bn"
    const val LANG_EN = "en"

    fun normalizeLanguage(langCode: String?): String =
        SupportedLanguage.normalizeCode(langCode)

    fun toLocale(langCode: String): Locale =
        SupportedLanguage.fromCode(langCode).locale

    fun isRtl(langCode: String?): Boolean =
        SupportedLanguage.fromCode(langCode).isRtl

    fun getUiLanguageSync(context: Context): String {
        val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return normalizeLanguage(sp.getString(SYNC_KEY_UI_LANG, LANG_BN))
    }

    fun setUiLanguageSync(context: Context, langCode: String) {
        val normalized = normalizeLanguage(langCode)
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(SYNC_KEY_UI_LANG, normalized)
            .commit()
    }

    fun getContentLanguageSync(context: Context): String {
        val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return normalizeLanguage(sp.getString(SYNC_KEY_CONTENT_LANG, LANG_BN))
    }

    fun setContentLanguageSync(context: Context, langCode: String) {
        val normalized = normalizeLanguage(langCode)
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(SYNC_KEY_CONTENT_LANG, normalized)
            .commit()
    }

    fun hasSelectedInitialLanguageSync(context: Context): Boolean {
        val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        if (sp.contains(SYNC_KEY_INITIAL_LANG_SELECTED)) {
            return sp.getBoolean(SYNC_KEY_INITIAL_LANG_SELECTED, false)
        }
        return sp.getBoolean("sync_onboarding_completed", false)
    }

    fun setInitialLanguageSelectedSync(context: Context, selected: Boolean) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(SYNC_KEY_INITIAL_LANG_SELECTED, selected)
            .commit()
    }

    /**
     * Wraps a base Context with the selected UI Locale so Android resources
     * and regional date/number/layout-direction align with the user's chosen UI language.
     */
    fun wrapContext(base: Context, langCode: String = getUiLanguageSync(base)): Context {
        val normalized = normalizeLanguage(langCode)
        val locale = toLocale(normalized)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))
        config.setLayoutDirection(locale)
        return base.createConfigurationContext(config)
    }

    /**
     * Updates configuration on the given context and persists both UI and Content languages synchronously.
     * Triggers Activity.recreate() if the UI locale changed and recreateIfUiChanged is true.
     */
    fun applyLanguageSelection(
        activity: Activity?,
        context: Context,
        uiLang: String,
        contentLang: String,
        recreateIfUiChanged: Boolean = true
    ) {
        val previousUi = getUiLanguageSync(context)
        val normalizedUi = normalizeLanguage(uiLang)
        val normalizedContent = normalizeLanguage(contentLang)

        setUiLanguageSync(context, normalizedUi)
        setContentLanguageSync(context, normalizedContent)

        val locale = toLocale(normalizedUi)
        Locale.setDefault(locale)

        @Suppress("DEPRECATION")
        run {
            val res = context.resources
            val config = Configuration(res.configuration)
            config.setLocale(locale)
            config.setLocales(LocaleList(locale))
            config.setLayoutDirection(locale)
            res.updateConfiguration(config, res.displayMetrics)
        }

        val currentResourceLang = context.resources.configuration.locales.get(0)?.language
        val needsRecreate = recreateIfUiChanged &&
            activity != null &&
            !activity.isFinishing &&
            !activity.isDestroyed &&
            (previousUi != normalizedUi || currentResourceLang != locale.language)

        if (needsRecreate) {
            activity?.recreate()
        }
    }

    tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is android.content.ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
