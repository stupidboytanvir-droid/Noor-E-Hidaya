package com.example.data.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppAccentTheme
import com.example.ui.theme.AppArabicFont
import com.example.ui.theme.AppCanvasTheme
import com.example.ui.theme.AppUiFont
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "noor_hidayah_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        val KEY_UI_LANG = stringPreferencesKey("ui_language")
        val KEY_CONTENT_LANG = stringPreferencesKey("content_language")
        val KEY_QARI_ID = stringPreferencesKey("selected_qari_id")
        val KEY_CALC_METHOD = stringPreferencesKey("calculation_method")
        val KEY_JURISTIC_METHOD = stringPreferencesKey("juristic_method")
        val KEY_MOSQUE_MODE = booleanPreferencesKey("mosque_mode_enabled")
        val KEY_MOSQUE_BUFFER = intPreferencesKey("mosque_buffer_mins")
        val KEY_MOSQUE_DURATION = intPreferencesKey("mosque_duration_mins")
        val KEY_PRAYER_ALERTS = booleanPreferencesKey("prayer_alerts_enabled")
        val KEY_PRE_PRAYER_ALERT_ENABLED = booleanPreferencesKey("pre_prayer_alert_enabled")
        val KEY_PRE_PRAYER_ALERT_MINS = intPreferencesKey("pre_prayer_alert_mins")
        val KEY_MOSQUE_RINGER_MODE = stringPreferencesKey("mosque_ringer_mode")
        val KEY_SAVED_RINGER_MODE = intPreferencesKey("mosque_saved_ringer_mode")
        val KEY_MOSQUE_ACTIVE_NOW = booleanPreferencesKey("mosque_active_now")
        val KEY_THEME_STYLE = stringPreferencesKey("theme_style")

        // Global 3-Mode Theme Engine & Unrestricted Custom Accent
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // "LIGHT", "DARK", "CUSTOM"
        val KEY_CUSTOM_ACCENT_HEX = stringPreferencesKey("custom_accent_hex") // e.g. "#E5C158"

        // Granular Element Color Overrides
        val KEY_CUSTOM_ARABIC_COLOR = stringPreferencesKey("custom_arabic_color") // e.g. custom hex or empty for default
        val KEY_CUSTOM_TRANSLATION_COLOR = stringPreferencesKey("custom_translation_color")
        val KEY_CUSTOM_TRANSLITERATION_COLOR = stringPreferencesKey("custom_transliteration_color")

        // New Advanced Theme & Typography Keys
        val KEY_CANVAS_THEME = stringPreferencesKey("canvas_theme")
        val KEY_ACCENT_THEME = stringPreferencesKey("accent_theme")
        val KEY_UI_FONT = stringPreferencesKey("ui_font")
        val KEY_ARABIC_FONT = stringPreferencesKey("arabic_font")
        val KEY_TEXT_SCALE = floatPreferencesKey("text_scale")

        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_INITIAL_LANG_SELECTED = booleanPreferencesKey("initial_language_selected")
        val KEY_PRAYER_STREAK = intPreferencesKey("prayer_streak")
        val KEY_QURAN_READ_MINUTES_TODAY = intPreferencesKey("quran_read_minutes_today")
        val KEY_SALAWAT_JUMAT_COUNT = intPreferencesKey("salawat_jumat_count")
        val KEY_DUAS_READ_COUNT = intPreferencesKey("duas_read_count")
        val KEY_TOTAL_TASBIH_COUNT = intPreferencesKey("total_tasbih_count")
        val KEY_HADITH_READ_MINUTES = intPreferencesKey("hadith_read_minutes")
        val KEY_HADITH_READ_COUNT = intPreferencesKey("hadith_read_count")
        val KEY_ACTIVE_YEAR_MONTH = stringPreferencesKey("active_year_month")

        // District & Auto GPS Location
        val KEY_SELECTED_DISTRICT_ID = stringPreferencesKey("selected_district_id")
        val KEY_SELECTED_DISTRICT_NAME = stringPreferencesKey("selected_district_name")
        val KEY_SELECTED_DISTRICT_LAT = floatPreferencesKey("selected_district_lat")
        val KEY_SELECTED_DISTRICT_LNG = floatPreferencesKey("selected_district_lng")
        val KEY_LOCATION_MANUALLY_SELECTED = booleanPreferencesKey("location_manually_selected")
        val KEY_LOCATION_DISPLAY_NAME = stringPreferencesKey("location_display_name")
        val KEY_IS_GPS_LOCATION = booleanPreferencesKey("is_gps_location")

        // Fasting Cautions
        val KEY_SAHUR_CAUTION_MINS = intPreferencesKey("sahur_caution_mins")
        val KEY_IFTAR_CAUTION_MINS = intPreferencesKey("iftar_caution_mins")

        // Hijri Calendar Moon Sighting Offset (-2 to +2 days)
        val KEY_HIJRI_OFFSET = intPreferencesKey("hijri_calendar_offset")

        // Daily Reminders
        val KEY_DAILY_AYAH_NOTIF = booleanPreferencesKey("daily_ayah_notif_enabled")
        val KEY_DAILY_HADITH_NOTIF = booleanPreferencesKey("daily_hadith_notif_enabled")

        val KEY_MOSQUE_AUTO_REPLY_SMS = booleanPreferencesKey("mosque_auto_reply_sms_enabled")
        val KEY_MOSQUE_AUTO_REPLY_MSG = stringPreferencesKey("mosque_auto_reply_sms_msg")

        const val DEFAULT_AUTO_REPLY_SMS = "আমি এখন নামাজে/সালাতে আছি। নামাজ শেষে আপনার সাথে যোগাযোগ করব ইনশাআল্লাহ।"

        val KEY_USER_LOGGED_IN = booleanPreferencesKey("user_logged_in")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_PHONE = stringPreferencesKey("user_phone")
        val KEY_USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
        val KEY_AUTH_PROVIDER = stringPreferencesKey("auth_provider")
        val KEY_FAVORITE_DUAS = stringSetPreferencesKey("favorite_dua_ids")
        val KEY_CUSTOM_DUA_COLLECTIONS = stringSetPreferencesKey("custom_dua_collections")
        val KEY_LAST_READ_HADITH_BOOK = stringPreferencesKey("last_read_hadith_book")
        val KEY_LAST_READ_HADITH_CHAPTER = stringPreferencesKey("last_read_hadith_chapter")
        val KEY_LAST_READ_HADITH_ID = stringPreferencesKey("last_read_hadith_id")
        val KEY_LAST_READ_HADITH_NUM = stringPreferencesKey("last_read_hadith_num")
        val KEY_LAST_READ_HADITH_PAGE = intPreferencesKey("last_read_hadith_page")
        val KEY_HADITH_FONT_SCALE = floatPreferencesKey("hadith_font_scale")

        const val DEFAULT_USER_NAME = "সম্মানিত মেহমান (Guest)"
        const val DEFAULT_USER_EMAIL = ""
        const val DEFAULT_USER_PHONE = ""
        const val DEFAULT_USER_PHOTO_URL = ""

        private const val SHARED_PREFS_NAME = "noor_hidayah_sync_prefs"
        private const val SYNC_KEY_ONBOARDING_COMPLETED = "sync_onboarding_completed"
        private const val SYNC_KEY_RINGER_MODE = "sync_saved_ringer_mode"
        private const val SYNC_KEY_MOSQUE_RINGER_MODE = "sync_mosque_ringer_mode"
        private const val SYNC_KEY_MOSQUE_ACTIVE = "sync_mosque_active"
        private const val SYNC_KEY_AUTO_REPLY_ENABLED = "sync_mosque_auto_reply_enabled"
        private const val SYNC_KEY_AUTO_REPLY_MSG = "sync_mosque_auto_reply_msg"

        fun hasCompletedOnboardingSync(context: Context): Boolean {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            return sp.getBoolean(SYNC_KEY_ONBOARDING_COMPLETED, false)
        }

        fun setOnboardingCompletedSync(context: Context, completed: Boolean) {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putBoolean(SYNC_KEY_ONBOARDING_COMPLETED, completed).commit()
        }

        fun prayerMosqueKey(prayerName: String) = stringPreferencesKey("mosque_cfg_${prayerName.lowercase()}")

        fun getSavedRingerModeSync(context: Context): Int {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            return sp.getInt(SYNC_KEY_RINGER_MODE, android.media.AudioManager.RINGER_MODE_NORMAL)
        }

        fun setSavedRingerModeSync(context: Context, mode: Int) {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putInt(SYNC_KEY_RINGER_MODE, mode).apply()
        }

        fun getMosqueRingerModeSync(context: Context): com.example.data.mosque.MosqueRingerMode {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            val id = sp.getString(SYNC_KEY_MOSQUE_RINGER_MODE, com.example.data.mosque.MosqueRingerMode.VIBRATE.id)
            return com.example.data.mosque.MosqueRingerMode.fromId(id)
        }

        fun setMosqueRingerModeSync(context: Context, mode: com.example.data.mosque.MosqueRingerMode) {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putString(SYNC_KEY_MOSQUE_RINGER_MODE, mode.id).apply()
        }

        fun isMosqueActiveSync(context: Context): Boolean {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            return sp.getBoolean(SYNC_KEY_MOSQUE_ACTIVE, false)
        }

        fun setIsMosqueActiveSync(context: Context, active: Boolean) {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putBoolean(SYNC_KEY_MOSQUE_ACTIVE, active).apply()
        }

        fun isMosqueAutoReplySmsEnabledSync(context: Context): Boolean {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            return sp.getBoolean(SYNC_KEY_AUTO_REPLY_ENABLED, false)
        }

        fun setMosqueAutoReplySmsEnabledSync(context: Context, enabled: Boolean) {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putBoolean(SYNC_KEY_AUTO_REPLY_ENABLED, enabled).apply()
        }

        fun getMosqueAutoReplyMsgSync(context: Context): String {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            return sp.getString(SYNC_KEY_AUTO_REPLY_MSG, DEFAULT_AUTO_REPLY_SMS) ?: DEFAULT_AUTO_REPLY_SMS
        }

        fun setMosqueAutoReplyMsgSync(context: Context, msg: String) {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            sp.edit().putString(SYNC_KEY_AUTO_REPLY_MSG, msg).apply()
        }

        private const val SYNC_KEY_CALL_WAS_RINGING = "sync_call_was_ringing"
        private const val SYNC_KEY_CALL_WAS_ANSWERED = "sync_call_was_answered"
        private const val SYNC_KEY_CALL_LAST_NUMBER = "sync_call_last_number"
        private const val SYNC_KEY_CALL_RING_TS = "sync_call_ring_ts"
        private const val SYNC_KEY_CALL_IN_MOSQUE = "sync_call_in_mosque"

        private const val SYNC_KEY_LAST_SMS_TEXT = "sync_last_sms_status_text"
        private const val SYNC_KEY_LAST_SMS_OK = "sync_last_sms_status_ok"
        private const val SYNC_KEY_LAST_SMS_TS = "sync_last_sms_status_ts"
        private const val SMS_COOLDOWN_PREFS = "noor_hidayah_sms_cooldowns"

        fun getCallWasRingingSync(context: Context): Boolean {
            return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(SYNC_KEY_CALL_WAS_RINGING, false)
        }

        fun setCallWasRingingSync(context: Context, ringing: Boolean) {
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(SYNC_KEY_CALL_WAS_RINGING, ringing).apply()
        }

        fun getCallWasAnsweredSync(context: Context): Boolean {
            return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(SYNC_KEY_CALL_WAS_ANSWERED, false)
        }

        fun setCallWasAnsweredSync(context: Context, answered: Boolean) {
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(SYNC_KEY_CALL_WAS_ANSWERED, answered).apply()
        }

        fun getCallLastNumberSync(context: Context): String? {
            return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getString(SYNC_KEY_CALL_LAST_NUMBER, null)
        }

        fun setCallLastNumberSync(context: Context, number: String?) {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            if (number.isNullOrBlank()) {
                sp.edit().remove(SYNC_KEY_CALL_LAST_NUMBER).apply()
            } else {
                sp.edit().putString(SYNC_KEY_CALL_LAST_NUMBER, number).apply()
            }
        }

        fun getCallRingTimestampSync(context: Context): Long {
            return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(SYNC_KEY_CALL_RING_TS, 0L)
        }

        fun setCallRingTimestampSync(context: Context, timestampMs: Long) {
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putLong(SYNC_KEY_CALL_RING_TS, timestampMs).apply()
        }

        fun getCallStartedInMosqueSync(context: Context): Boolean {
            return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(SYNC_KEY_CALL_IN_MOSQUE, false)
        }

        fun setCallStartedInMosqueSync(context: Context, inMosque: Boolean) {
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(SYNC_KEY_CALL_IN_MOSQUE, inMosque).apply()
        }

        fun clearCallTrackingSync(context: Context) {
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SYNC_KEY_CALL_WAS_RINGING, false)
                .putBoolean(SYNC_KEY_CALL_WAS_ANSWERED, false)
                .putBoolean(SYNC_KEY_CALL_IN_MOSQUE, false)
                .putLong(SYNC_KEY_CALL_RING_TS, 0L)
                .remove(SYNC_KEY_CALL_LAST_NUMBER)
                .apply()
        }

        fun getSmsCooldownTimestampSync(context: Context, normalizedNumber: String): Long {
            return context.getSharedPreferences(SMS_COOLDOWN_PREFS, Context.MODE_PRIVATE)
                .getLong(normalizedNumber, 0L)
        }

        fun setSmsCooldownTimestampSync(context: Context, normalizedNumber: String, timestampMs: Long) {
            context.getSharedPreferences(SMS_COOLDOWN_PREFS, Context.MODE_PRIVATE)
                .edit().putLong(normalizedNumber, timestampMs).apply()
        }

        fun clearSmsCooldownSync(context: Context, normalizedNumber: String) {
            context.getSharedPreferences(SMS_COOLDOWN_PREFS, Context.MODE_PRIVATE)
                .edit().remove(normalizedNumber).apply()
        }

        fun pruneSmsCooldownsSync(context: Context, maxAgeMs: Long = 60 * 60 * 1000L) {
            val sp = context.getSharedPreferences(SMS_COOLDOWN_PREFS, Context.MODE_PRIVATE)
            val cutoff = System.currentTimeMillis() - maxAgeMs
            val editor = sp.edit()
            var changed = false
            for ((key, value) in sp.all) {
                val ts = (value as? Long) ?: 0L
                if (ts < cutoff) {
                    editor.remove(key)
                    changed = true
                }
            }
            if (changed) editor.apply()
        }

        fun setLastAutoSmsStatusSync(
            context: Context,
            statusText: String,
            isSuccess: Boolean,
            timestampMs: Long = System.currentTimeMillis()
        ) {
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(SYNC_KEY_LAST_SMS_TEXT, statusText)
                .putBoolean(SYNC_KEY_LAST_SMS_OK, isSuccess)
                .putLong(SYNC_KEY_LAST_SMS_TS, timestampMs)
                .apply()
        }

        fun getLastAutoSmsStatusSync(context: Context): Triple<String, Boolean, Long> {
            val sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            val text = sp.getString(SYNC_KEY_LAST_SMS_TEXT, "") ?: ""
            val ok = sp.getBoolean(SYNC_KEY_LAST_SMS_OK, true)
            val ts = sp.getLong(SYNC_KEY_LAST_SMS_TS, 0L)
            return Triple(text, ok, ts)
        }
    }

    val uiLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_UI_LANG] ?: com.example.data.util.LocaleHelper.getUiLanguageSync(context)
    }

    val contentLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CONTENT_LANG] ?: com.example.data.util.LocaleHelper.getContentLanguageSync(context)
    }

    val selectedQariId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_QARI_ID] ?: "alafasy"
    }

    val calculationMethod: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CALC_METHOD] ?: "MUSLIM_WORLD_LEAGUE"
    }

    val juristicMethod: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_JURISTIC_METHOD] ?: "STANDARD"
    }

    val mosqueModeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_MOSQUE_MODE] ?: false
    }

    val mosqueBufferMins: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_MOSQUE_BUFFER] ?: 5
    }

    val mosqueDurationMins: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_MOSQUE_DURATION] ?: 25
    }

    val mosqueAutoReplySmsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_MOSQUE_AUTO_REPLY_SMS] ?: false
    }

    val mosqueAutoReplyMessage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_MOSQUE_AUTO_REPLY_MSG] ?: DEFAULT_AUTO_REPLY_SMS
    }

    val prayerAlertsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_PRAYER_ALERTS] ?: true
    }

    val prePrayerAlertEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_PRE_PRAYER_ALERT_ENABLED] ?: true
    }

    val prePrayerAlertMinutes: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_PRE_PRAYER_ALERT_MINS] ?: 15
    }

    val mosqueRingerMode: Flow<com.example.data.mosque.MosqueRingerMode> = context.dataStore.data.map { prefs ->
        com.example.data.mosque.MosqueRingerMode.fromId(prefs[KEY_MOSQUE_RINGER_MODE])
    }

    val isMosqueActiveNow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_MOSQUE_ACTIVE_NOW] ?: false
    }

    val themeStyle: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_STYLE] ?: "EMERALD_GOLD"
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "DARK"
    }

    val customAccentHex: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOM_ACCENT_HEX] ?: "#E5C158"
    }

    val customArabicColor: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOM_ARABIC_COLOR] ?: ""
    }

    val customTranslationColor: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOM_TRANSLATION_COLOR] ?: ""
    }

    val customTransliterationColor: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOM_TRANSLITERATION_COLOR] ?: ""
    }

    val canvasTheme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CANVAS_THEME] ?: AppCanvasTheme.OBSIDIAN.id
    }

    val accentTheme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCENT_THEME] ?: AppAccentTheme.CHAMPAGNE_GOLD.id
    }

    val uiFont: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_UI_FONT] ?: AppUiFont.INTER.id
    }

    val arabicFont: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_ARABIC_FONT] ?: AppArabicFont.AMIRI.id
    }

    val textScale: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_TEXT_SCALE] ?: 1.0f
    }

    fun getPrayerMosqueConfig(prayer: com.example.data.model.PrayerName): Flow<com.example.data.mosque.PrayerMosqueConfig> {
        val key = prayerMosqueKey(prayer.name)
        return context.dataStore.data.map { prefs ->
            com.example.data.mosque.PrayerMosqueConfig.deserialize(prayer, prefs[key])
        }
    }

    fun getAllPrayerMosqueConfigs(): Flow<Map<com.example.data.model.PrayerName, com.example.data.mosque.PrayerMosqueConfig>> {
        val fardPrayers = listOf(
            com.example.data.model.PrayerName.FAJR,
            com.example.data.model.PrayerName.DHUHR,
            com.example.data.model.PrayerName.ASR,
            com.example.data.model.PrayerName.MAGHRIB,
            com.example.data.model.PrayerName.ISHA
        )
        return context.dataStore.data.map { prefs ->
            fardPrayers.associateWith { prayer ->
                val key = prayerMosqueKey(prayer.name)
                com.example.data.mosque.PrayerMosqueConfig.deserialize(prayer, prefs[key])
            }
        }
    }

    suspend fun savePrayerMosqueConfig(config: com.example.data.mosque.PrayerMosqueConfig) {
        val key = prayerMosqueKey(config.prayer.name)
        context.dataStore.edit { it[key] = config.serialize() }
    }

    suspend fun setUiLanguage(lang: String) {
        val normalized = com.example.data.util.LocaleHelper.normalizeLanguage(lang)
        com.example.data.util.LocaleHelper.setUiLanguageSync(context, normalized)
        context.dataStore.edit { it[KEY_UI_LANG] = normalized }
    }

    suspend fun setContentLanguage(lang: String) {
        val normalized = com.example.data.util.LocaleHelper.normalizeLanguage(lang)
        com.example.data.util.LocaleHelper.setContentLanguageSync(context, normalized)
        context.dataStore.edit { it[KEY_CONTENT_LANG] = normalized }
    }

    suspend fun setSelectedQariId(qariId: String) {
        context.dataStore.edit { it[KEY_QARI_ID] = qariId }
    }

    suspend fun setCalculationMethod(method: String) {
        context.dataStore.edit { it[KEY_CALC_METHOD] = method }
    }

    suspend fun setJuristicMethod(juristic: String) {
        context.dataStore.edit { it[KEY_JURISTIC_METHOD] = juristic }
    }

    suspend fun setMosqueModeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_MOSQUE_MODE] = enabled }
    }

    suspend fun setMosqueBuffers(buffer: Int, duration: Int) {
        context.dataStore.edit {
            it[KEY_MOSQUE_BUFFER] = buffer
            it[KEY_MOSQUE_DURATION] = duration
        }
    }

    suspend fun setMosqueActiveNow(active: Boolean) {
        context.dataStore.edit { it[KEY_MOSQUE_ACTIVE_NOW] = active }
        setIsMosqueActiveSync(context, active)
    }

    suspend fun setPrayerAlertsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_PRAYER_ALERTS] = enabled }
    }

    suspend fun setPrePrayerAlertEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_PRE_PRAYER_ALERT_ENABLED] = enabled }
    }

    suspend fun setPrePrayerAlertMinutes(minutes: Int) {
        context.dataStore.edit { it[KEY_PRE_PRAYER_ALERT_MINS] = minutes.coerceIn(5, 45) }
    }

    suspend fun setMosqueRingerMode(mode: com.example.data.mosque.MosqueRingerMode) {
        setMosqueRingerModeSync(context, mode)
        context.dataStore.edit { it[KEY_MOSQUE_RINGER_MODE] = mode.id }
    }

    suspend fun setThemeStyle(style: String) {
        context.dataStore.edit { it[KEY_THEME_STYLE] = style }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setCustomAccentHex(hex: String) {
        context.dataStore.edit { it[KEY_CUSTOM_ACCENT_HEX] = hex }
    }

    suspend fun setCustomArabicColor(hex: String) {
        context.dataStore.edit { it[KEY_CUSTOM_ARABIC_COLOR] = hex }
    }

    suspend fun setCustomTranslationColor(hex: String) {
        context.dataStore.edit { it[KEY_CUSTOM_TRANSLATION_COLOR] = hex }
    }

    suspend fun setCustomTransliterationColor(hex: String) {
        context.dataStore.edit { it[KEY_CUSTOM_TRANSLITERATION_COLOR] = hex }
    }

    suspend fun setCanvasTheme(theme: String) {
        context.dataStore.edit { it[KEY_CANVAS_THEME] = theme }
    }

    suspend fun setAccentTheme(theme: String) {
        context.dataStore.edit { it[KEY_ACCENT_THEME] = theme }
    }

    suspend fun setUiFont(font: String) {
        context.dataStore.edit { it[KEY_UI_FONT] = font }
    }

    suspend fun setArabicFont(font: String) {
        context.dataStore.edit { it[KEY_ARABIC_FONT] = font }
    }

    suspend fun setTextScale(scale: Float) {
        context.dataStore.edit { it[KEY_TEXT_SCALE] = scale }
    }

    suspend fun setMosqueAutoReplySmsEnabled(enabled: Boolean) {
        setMosqueAutoReplySmsEnabledSync(context, enabled)
        context.dataStore.edit { it[KEY_MOSQUE_AUTO_REPLY_SMS] = enabled }
    }

    suspend fun setMosqueAutoReplyMessage(msg: String) {
        setMosqueAutoReplyMsgSync(context, msg)
        context.dataStore.edit { it[KEY_MOSQUE_AUTO_REPLY_MSG] = msg }
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: hasCompletedOnboardingSync(context)
    }

    val initialLanguageSelected: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_INITIAL_LANG_SELECTED]
            ?: (if (com.example.data.util.LocaleHelper.hasSelectedInitialLanguageSync(context)) true else null)
            ?: (prefs[KEY_ONBOARDING_COMPLETED] ?: hasCompletedOnboardingSync(context))
    }

    val prayerStreak: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_PRAYER_STREAK] ?: 0
    }

    val quranReadMinutesToday: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_QURAN_READ_MINUTES_TODAY] ?: 0
    }

    val salawatJumatCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_SALAWAT_JUMAT_COUNT] ?: 0
    }

    val duasReadCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_DUAS_READ_COUNT] ?: 0
    }

    val totalTasbihCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_TOTAL_TASBIH_COUNT] ?: 0
    }

    val hadithReadMinutes: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_HADITH_READ_MINUTES] ?: 0
    }

    val hadithReadCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_HADITH_READ_COUNT] ?: 0
    }

    val activeYearMonth: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACTIVE_YEAR_MONTH] ?: ""
    }

    val selectedDistrictId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SELECTED_DISTRICT_ID] ?: "dhaka"
    }

    val selectedDistrictName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SELECTED_DISTRICT_NAME] ?: "ঢাকা"
    }

    val selectedDistrictLat: Flow<Double> = context.dataStore.data.map { prefs ->
        (prefs[KEY_SELECTED_DISTRICT_LAT] ?: 23.8103f).toDouble()
    }

    val selectedDistrictLng: Flow<Double> = context.dataStore.data.map { prefs ->
        (prefs[KEY_SELECTED_DISTRICT_LNG] ?: 90.4125f).toDouble()
    }

    val locationManuallySelected: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_LOCATION_MANUALLY_SELECTED] ?: false
    }

    val locationDisplayName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LOCATION_DISPLAY_NAME]?.takeIf { it.isNotBlank() }
            ?: "${prefs[KEY_SELECTED_DISTRICT_NAME] ?: "ঢাকা"}, বাংলাদেশ"
    }

    val isGpsLocation: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_GPS_LOCATION] ?: false
    }

    val sahurCautionMins: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_SAHUR_CAUTION_MINS] ?: 3
    }

    val iftarCautionMins: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_IFTAR_CAUTION_MINS] ?: 3
    }

    val hijriOffset: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_HIJRI_OFFSET] ?: 0
    }

    val dailyAyahNotifEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DAILY_AYAH_NOTIF] ?: true
    }

    val dailyHadithNotifEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DAILY_HADITH_NOTIF] ?: true
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        setOnboardingCompletedSync(context, completed)
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setInitialLanguageSelected(selected: Boolean) {
        com.example.data.util.LocaleHelper.setInitialLanguageSelectedSync(context, selected)
        context.dataStore.edit { it[KEY_INITIAL_LANG_SELECTED] = selected }
    }

    suspend fun setPrayerStreak(streak: Int) {
        context.dataStore.edit { it[KEY_PRAYER_STREAK] = streak }
    }

    suspend fun incrementQuranReadMinutes(minutes: Int = 1) {
        if (minutes <= 0) return
        context.dataStore.edit {
            val current = it[KEY_QURAN_READ_MINUTES_TODAY] ?: 0
            it[KEY_QURAN_READ_MINUTES_TODAY] = current + minutes
        }
    }

    suspend fun incrementDuasReadCount(count: Int = 1) {
        if (count <= 0) return
        context.dataStore.edit {
            val current = it[KEY_DUAS_READ_COUNT] ?: 0
            it[KEY_DUAS_READ_COUNT] = current + count
        }
    }

    suspend fun addTasbihCount(count: Int) {
        if (count <= 0) return
        context.dataStore.edit {
            val current = it[KEY_TOTAL_TASBIH_COUNT] ?: 0
            it[KEY_TOTAL_TASBIH_COUNT] = current + count
        }
    }

    suspend fun incrementHadithReadMinutes(minutes: Int = 1) {
        if (minutes <= 0) return
        context.dataStore.edit {
            val current = it[KEY_HADITH_READ_MINUTES] ?: 0
            it[KEY_HADITH_READ_MINUTES] = current + minutes
        }
    }

    suspend fun incrementHadithReadCount(count: Int = 1) {
        if (count <= 0) return
        context.dataStore.edit {
            val current = it[KEY_HADITH_READ_COUNT] ?: 0
            it[KEY_HADITH_READ_COUNT] = current + count
        }
    }

    suspend fun setActiveYearMonth(yearMonth: String) {
        context.dataStore.edit {
            it[KEY_ACTIVE_YEAR_MONTH] = yearMonth
        }
    }

    suspend fun resetCurrentMonthCounters() {
        context.dataStore.edit {
            it[KEY_QURAN_READ_MINUTES_TODAY] = 0
            it[KEY_DUAS_READ_COUNT] = 0
            it[KEY_TOTAL_TASBIH_COUNT] = 0
            it[KEY_SALAWAT_JUMAT_COUNT] = 0
            it[KEY_HADITH_READ_MINUTES] = 0
            it[KEY_HADITH_READ_COUNT] = 0
        }
    }

    suspend fun setDistrict(id: String, name: String, lat: Double, lng: Double) {
        context.dataStore.edit {
            it[KEY_SELECTED_DISTRICT_ID] = id
            it[KEY_SELECTED_DISTRICT_NAME] = name
            it[KEY_SELECTED_DISTRICT_LAT] = lat.toFloat()
            it[KEY_SELECTED_DISTRICT_LNG] = lng.toFloat()
            it[KEY_LOCATION_MANUALLY_SELECTED] = true
            it[KEY_LOCATION_DISPLAY_NAME] = "$name, বাংলাদেশ"
            it[KEY_IS_GPS_LOCATION] = false
        }
    }

    suspend fun setAutoDetectedLocation(
        districtId: String,
        districtName: String,
        displayLocationName: String,
        lat: Double,
        lng: Double
    ) {
        context.dataStore.edit {
            it[KEY_SELECTED_DISTRICT_ID] = districtId
            it[KEY_SELECTED_DISTRICT_NAME] = districtName
            it[KEY_SELECTED_DISTRICT_LAT] = lat.toFloat()
            it[KEY_SELECTED_DISTRICT_LNG] = lng.toFloat()
            it[KEY_LOCATION_DISPLAY_NAME] = displayLocationName
            it[KEY_IS_GPS_LOCATION] = true
        }
    }


    suspend fun setSahurCautionMins(mins: Int) {
        context.dataStore.edit { it[KEY_SAHUR_CAUTION_MINS] = mins }
    }

    suspend fun setIftarCautionMins(mins: Int) {
        context.dataStore.edit { it[KEY_IFTAR_CAUTION_MINS] = mins }
    }

    suspend fun setHijriOffset(offset: Int) {
        context.dataStore.edit { it[KEY_HIJRI_OFFSET] = offset.coerceIn(-2, 2) }
    }

    suspend fun setDailyAyahNotif(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DAILY_AYAH_NOTIF] = enabled }
    }

    suspend fun setDailyHadithNotif(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DAILY_HADITH_NOTIF] = enabled }
    }

    suspend fun incrementSalawatJumatCount() {
        context.dataStore.edit {
            val current = it[KEY_SALAWAT_JUMAT_COUNT] ?: 0
            it[KEY_SALAWAT_JUMAT_COUNT] = current + 1
        }
    }

    val isUserLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_LOGGED_IN] ?: false
    }

    val userId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID] ?: ""
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: DEFAULT_USER_NAME
    }

    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL] ?: DEFAULT_USER_EMAIL
    }

    val userPhone: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_PHONE] ?: DEFAULT_USER_PHONE
    }

    val authProvider: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTH_PROVIDER] ?: "GOOGLE"
    }

    val userPhotoUrl: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_PHOTO_URL] ?: DEFAULT_USER_PHOTO_URL
    }

    val favoriteDuaIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_FAVORITE_DUAS] ?: emptySet()
    }

    suspend fun toggleFavoriteDua(duaId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITE_DUAS] ?: emptySet()
            val updated = if (current.contains(duaId)) current - duaId else current + duaId
            prefs[KEY_FAVORITE_DUAS] = updated
        }
    }

    suspend fun setFavoriteDuas(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FAVORITE_DUAS] = ids
        }
    }

    val customDuaCollections: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOM_DUA_COLLECTIONS] ?: emptySet()
    }

    suspend fun addCustomDuaCollection(name: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_CUSTOM_DUA_COLLECTIONS] ?: emptySet()
            prefs[KEY_CUSTOM_DUA_COLLECTIONS] = current + name.trim()
        }
    }

    suspend fun removeCustomDuaCollection(name: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_CUSTOM_DUA_COLLECTIONS] ?: emptySet()
            prefs[KEY_CUSTOM_DUA_COLLECTIONS] = current - name
        }
    }

    suspend fun restoreSpiritualStats(
        quranMins: Int,
        duasCount: Int,
        tasbihCount: Int,
        salawatCount: Int,
        hadithMins: Int = 0,
        hadithCount: Int = 0,
        prayerStreak: Int? = null
    ) {
        context.dataStore.edit {
            it[KEY_QURAN_READ_MINUTES_TODAY] = quranMins.coerceAtLeast(0)
            it[KEY_DUAS_READ_COUNT] = duasCount.coerceAtLeast(0)
            it[KEY_TOTAL_TASBIH_COUNT] = tasbihCount.coerceAtLeast(0)
            it[KEY_SALAWAT_JUMAT_COUNT] = salawatCount.coerceAtLeast(0)
            it[KEY_HADITH_READ_MINUTES] = hadithMins.coerceAtLeast(0)
            it[KEY_HADITH_READ_COUNT] = hadithCount.coerceAtLeast(0)
            if (prayerStreak != null) {
                it[KEY_PRAYER_STREAK] = prayerStreak.coerceAtLeast(0)
            }
        }
    }

    suspend fun saveUserSession(name: String, email: String, phone: String, provider: String, photoUrl: String? = null, uid: String = "") {
        context.dataStore.edit {
            it[KEY_USER_LOGGED_IN] = true
            if (uid.isNotEmpty()) {
                it[KEY_USER_ID] = uid
            }
            it[KEY_USER_NAME] = name
            it[KEY_USER_EMAIL] = email
            it[KEY_USER_PHONE] = phone
            it[KEY_AUTH_PROVIDER] = provider
            if (photoUrl != null) {
                it[KEY_USER_PHOTO_URL] = photoUrl
            }
        }
    }

    suspend fun setUserPhotoUrl(photoUrl: String) {
        context.dataStore.edit {
            it[KEY_USER_PHOTO_URL] = photoUrl
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit {
            it[KEY_USER_LOGGED_IN] = false
            it[KEY_USER_ID] = ""
            it[KEY_USER_NAME] = ""
            it[KEY_USER_EMAIL] = ""
            it[KEY_USER_PHONE] = ""
            it[KEY_USER_PHOTO_URL] = ""
            it[KEY_AUTH_PROVIDER] = "GUEST"
        }
    }

    suspend fun resetSalawatJumatCount() {
        context.dataStore.edit { it[KEY_SALAWAT_JUMAT_COUNT] = 0 }
    }

    fun getTodayAmals(dateString: String): Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[androidx.datastore.preferences.core.stringSetPreferencesKey("amal_checked_$dateString")] ?: emptySet()
    }

    suspend fun toggleTodayAmal(dateString: String, amalKey: String) {
        val prefKey = androidx.datastore.preferences.core.stringSetPreferencesKey("amal_checked_$dateString")
        context.dataStore.edit { prefs ->
            val current = prefs[prefKey] ?: emptySet()
            val updated = if (current.contains(amalKey)) current - amalKey else current + amalKey
            prefs[prefKey] = updated
        }
    }

    suspend fun setTodayAmals(dateString: String, amals: Set<String>) {
        val prefKey = androidx.datastore.preferences.core.stringSetPreferencesKey("amal_checked_$dateString")
        context.dataStore.edit { prefs ->
            prefs[prefKey] = amals
        }
    }

    val jumuahReminderEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[androidx.datastore.preferences.core.booleanPreferencesKey("jumuah_reminder_enabled")] ?: true
    }

    suspend fun setJumuahReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[androidx.datastore.preferences.core.booleanPreferencesKey("jumuah_reminder_enabled")] = enabled }
    }

    val fastingReminderEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[androidx.datastore.preferences.core.booleanPreferencesKey("fasting_reminder_enabled")] ?: true
    }

    suspend fun setFastingReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[androidx.datastore.preferences.core.booleanPreferencesKey("fasting_reminder_enabled")] = enabled }
    }

    // --- Hadith Reader Preferences ---
    val lastReadHadithBook: Flow<String?> = context.dataStore.data.map { it[KEY_LAST_READ_HADITH_BOOK] }
    val lastReadHadithChapter: Flow<String?> = context.dataStore.data.map { it[KEY_LAST_READ_HADITH_CHAPTER] }
    val lastReadHadithId: Flow<String?> = context.dataStore.data.map { it[KEY_LAST_READ_HADITH_ID] }
    val lastReadHadithNum: Flow<String?> = context.dataStore.data.map { it[KEY_LAST_READ_HADITH_NUM] }
    val lastReadHadithPage: Flow<Int> = context.dataStore.data.map { it[KEY_LAST_READ_HADITH_PAGE] ?: 1 }
    val hadithFontScale: Flow<Float> = context.dataStore.data.map { it[KEY_HADITH_FONT_SCALE] ?: 1.0f }

    suspend fun saveLastReadHadith(
        bookId: String,
        chapterId: String,
        hadithId: String,
        hadithNumber: String,
        pageNumber: Int
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_READ_HADITH_BOOK] = bookId
            prefs[KEY_LAST_READ_HADITH_CHAPTER] = chapterId
            prefs[KEY_LAST_READ_HADITH_ID] = hadithId
            prefs[KEY_LAST_READ_HADITH_NUM] = hadithNumber
            prefs[KEY_LAST_READ_HADITH_PAGE] = pageNumber
        }
    }

    suspend fun setHadithFontScale(scale: Float) {
        context.dataStore.edit { it[KEY_HADITH_FONT_SCALE] = scale }
    }
}
