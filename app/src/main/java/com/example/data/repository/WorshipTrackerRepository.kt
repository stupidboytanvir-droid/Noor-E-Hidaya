package com.example.data.repository

import android.content.Context
import com.example.data.cloud.CloudSyncRepository
import com.example.data.local.AppDatabase
import com.example.data.local.MonthlyWorshipDao
import com.example.data.local.MonthlyWorshipEntity
import com.example.data.preference.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class WorshipCategory(
    val titleBengali: String,
    val titleEnglish: String,
    val unitBengali: String,
    val unitEnglish: String,
    val iconEmoji: String
) {
    QURAN("কুরআন", "Quran", "মিনিট", "min", "📖"),
    DUA("দু'আ", "Du'a", "বার", "times", "🤲"),
    TASBIH("তাসবীহ", "Tasbih", "বার", "times", "📿"),
    DUROOD("দরূদ", "Durood", "বার", "times", "💚"),
    HADITH("হাদিস", "Hadith", "মিনিট", "min", "📚");

    fun localizedTitle(lang: String): String = com.example.ui.theme.localizedText(lang, titleBengali, titleEnglish)
    fun localizedUnit(lang: String): String = com.example.ui.theme.localizedText(lang, unitBengali, unitEnglish)
}

class WorshipTrackerRepository(
    private val context: Context,
    private val prefs: AppPreferences = AppPreferences(context),
    private val database: AppDatabase = AppDatabase.getInstance(context),
    private val cloudSyncRepo: CloudSyncRepository? = CloudSyncRepository(context, prefs, database),
    private val clockMillis: () -> Long = { System.currentTimeMillis() }
) {
    private val monthlyDao: MonthlyWorshipDao = database.monthlyWorshipDao()
    private val mutex = Mutex()

    fun observeLast12Months(): Flow<List<MonthlyWorshipEntity>> = monthlyDao.observeLast12Months()

    fun currentYearMonth(nowMillis: Long = clockMillis()): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
        return sdf.format(Date(nowMillis))
    }

    /**
     * Checks if the active calendar month has changed since last recorded activity.
     * On month rollover:
     * 1. Archives the previous month's totals into Room & Firestore.
     * 2. Resets active monthly counters in AppPreferences to strict 0.
     * 3. Initializes the new month row in Room with 0 totals.
     */
    suspend fun ensureCurrentMonthAndArchiveIfNeeded(): String = mutex.withLock {
        ensureCurrentMonthLocked()
    }

    private suspend fun ensureCurrentMonthLocked(): String {
        val nowMonth = currentYearMonth(clockMillis())
        val storedMonth = prefs.activeYearMonth.first()

        if (storedMonth.isBlank()) {
            prefs.setActiveYearMonth(nowMonth)
            val existing = monthlyDao.getMonth(nowMonth)
            if (existing == null) {
                val initial = MonthlyWorshipEntity(
                    yearMonth = nowMonth,
                    quranMinutes = prefs.quranReadMinutesToday.first(),
                    duaCount = prefs.duasReadCount.first(),
                    tasbihCount = prefs.totalTasbihCount.first(),
                    duroodCount = prefs.salawatJumatCount.first(),
                    hadithMinutes = prefs.hadithReadMinutes.first(),
                    hadithReadCount = prefs.hadithReadCount.first(),
                    updatedAt = clockMillis()
                )
                monthlyDao.upsert(initial)
            }
            return nowMonth
        }

        if (storedMonth != nowMonth) {
            val existingOld = monthlyDao.getMonth(storedMonth)
            val archivedOld = MonthlyWorshipEntity(
                yearMonth = storedMonth,
                quranMinutes = maxOf(existingOld?.quranMinutes ?: 0, prefs.quranReadMinutesToday.first()),
                duaCount = maxOf(existingOld?.duaCount ?: 0, prefs.duasReadCount.first()),
                tasbihCount = maxOf(existingOld?.tasbihCount ?: 0, prefs.totalTasbihCount.first()),
                duroodCount = maxOf(existingOld?.duroodCount ?: 0, prefs.salawatJumatCount.first()),
                hadithMinutes = maxOf(existingOld?.hadithMinutes ?: 0, prefs.hadithReadMinutes.first()),
                hadithReadCount = maxOf(existingOld?.hadithReadCount ?: 0, prefs.hadithReadCount.first()),
                updatedAt = clockMillis()
            )
            monthlyDao.upsert(archivedOld)
            cloudSyncRepo?.autoSyncMonthlyWorship(archivedOld)

            // Reset active dashboard counters cleanly to zero for the new month
            prefs.resetCurrentMonthCounters()
            prefs.setActiveYearMonth(nowMonth)

            val newMonthEntity = monthlyDao.getMonth(nowMonth)
                ?: MonthlyWorshipEntity(
                    yearMonth = nowMonth,
                    updatedAt = clockMillis()
                ).also { monthlyDao.upsert(it) }

            cloudSyncRepo?.autoSyncMonthlyWorship(newMonthEntity)
            cloudSyncRepo?.autoSyncSpiritualStats()
        } else if (monthlyDao.getMonth(nowMonth) == null) {
            monthlyDao.upsert(MonthlyWorshipEntity(yearMonth = nowMonth, updatedAt = clockMillis()))
        }

        return nowMonth
    }

    suspend fun recordQuranMinutes(minutes: Int = 1) {
        if (minutes <= 0) return
        mutex.withLock {
            val ym = ensureCurrentMonthLocked()
            prefs.incrementQuranReadMinutes(minutes)
            val current = monthlyDao.getMonth(ym) ?: MonthlyWorshipEntity(yearMonth = ym)
            val updated = current.copy(
                quranMinutes = current.quranMinutes + minutes,
                updatedAt = clockMillis()
            )
            monthlyDao.upsert(updated)
            cloudSyncRepo?.autoSyncMonthlyWorship(updated)
            cloudSyncRepo?.autoSyncSpiritualStats()
        }
    }

    suspend fun recordDuaEngagement(count: Int = 1) {
        if (count <= 0) return
        mutex.withLock {
            val ym = ensureCurrentMonthLocked()
            prefs.incrementDuasReadCount(count)
            val current = monthlyDao.getMonth(ym) ?: MonthlyWorshipEntity(yearMonth = ym)
            val updated = current.copy(
                duaCount = current.duaCount + count,
                updatedAt = clockMillis()
            )
            monthlyDao.upsert(updated)
            cloudSyncRepo?.autoSyncMonthlyWorship(updated)
            cloudSyncRepo?.autoSyncSpiritualStats()
        }
    }

    suspend fun recordTasbihCount(count: Int = 1, isDurood: Boolean = false) {
        if (count <= 0) return
        mutex.withLock {
            val ym = ensureCurrentMonthLocked()
            prefs.addTasbihCount(count)
            if (isDurood) {
                repeat(count) { prefs.incrementSalawatJumatCount() }
            }
            val current = monthlyDao.getMonth(ym) ?: MonthlyWorshipEntity(yearMonth = ym)
            val updated = current.copy(
                tasbihCount = current.tasbihCount + count,
                duroodCount = if (isDurood) current.duroodCount + count else current.duroodCount,
                updatedAt = clockMillis()
            )
            monthlyDao.upsert(updated)
            cloudSyncRepo?.autoSyncMonthlyWorship(updated)
            cloudSyncRepo?.autoSyncSpiritualStats()
        }
    }

    suspend fun recordDuroodCount(count: Int = 1) {
        if (count <= 0) return
        mutex.withLock {
            val ym = ensureCurrentMonthLocked()
            repeat(count) { prefs.incrementSalawatJumatCount() }
            val current = monthlyDao.getMonth(ym) ?: MonthlyWorshipEntity(yearMonth = ym)
            val updated = current.copy(
                duroodCount = current.duroodCount + count,
                updatedAt = clockMillis()
            )
            monthlyDao.upsert(updated)
            cloudSyncRepo?.autoSyncMonthlyWorship(updated)
            cloudSyncRepo?.autoSyncSpiritualStats()
        }
    }

    suspend fun recordHadithMinutes(minutes: Int = 1) {
        if (minutes <= 0) return
        mutex.withLock {
            val ym = ensureCurrentMonthLocked()
            prefs.incrementHadithReadMinutes(minutes)
            val current = monthlyDao.getMonth(ym) ?: MonthlyWorshipEntity(yearMonth = ym)
            val updated = current.copy(
                hadithMinutes = current.hadithMinutes + minutes,
                updatedAt = clockMillis()
            )
            monthlyDao.upsert(updated)
            cloudSyncRepo?.autoSyncMonthlyWorship(updated)
            cloudSyncRepo?.autoSyncSpiritualStats()
        }
    }

    suspend fun recordHadithRead(count: Int = 1) {
        if (count <= 0) return
        mutex.withLock {
            val ym = ensureCurrentMonthLocked()
            prefs.incrementHadithReadCount(count)
            val current = monthlyDao.getMonth(ym) ?: MonthlyWorshipEntity(yearMonth = ym)
            val updated = current.copy(
                hadithReadCount = current.hadithReadCount + count,
                updatedAt = clockMillis()
            )
            monthlyDao.upsert(updated)
            cloudSyncRepo?.autoSyncMonthlyWorship(updated)
            cloudSyncRepo?.autoSyncSpiritualStats()
        }
    }

    companion object {
        private val BENGALI_MONTHS_SHORT = arrayOf(
            "জানু", "ফেব্রু", "মার্চ", "এপ্রি", "মে", "জুন",
            "জুলা", "আগ", "সেপ্টে", "অক্টো", "নভে", "ডিসে"
        )
        private val BENGALI_MONTHS_FULL = arrayOf(
            "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        )

        /**
         * Builds a continuous 12-month chronological list ending at [referenceMillis]'s month.
         * Missing months are padded with 0 values so 12-month charts always show all 12 months.
         */
        fun build12MonthSeries(
            entities: List<MonthlyWorshipEntity>,
            referenceMillis: Long = System.currentTimeMillis()
        ): List<MonthlyWorshipEntity> {
            val byMonth = entities.associateBy { it.yearMonth }
            val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
            val cal = Calendar.getInstance().apply {
                timeInMillis = referenceMillis
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, -11)
            }
            val result = ArrayList<MonthlyWorshipEntity>(12)
            repeat(12) {
                val key = sdf.format(cal.time)
                result.add(byMonth[key] ?: MonthlyWorshipEntity(yearMonth = key, updatedAt = 0L))
                cal.add(Calendar.MONTH, 1)
            }
            return result
        }

        fun valueForCategory(entity: MonthlyWorshipEntity, category: WorshipCategory): Int {
            return when (category) {
                WorshipCategory.QURAN -> entity.quranMinutes
                WorshipCategory.DUA -> entity.duaCount
                WorshipCategory.TASBIH -> entity.tasbihCount
                WorshipCategory.DUROOD -> entity.duroodCount
                WorshipCategory.HADITH -> entity.hadithMinutes
            }
        }

        private val ENGLISH_MONTHS_SHORT = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        private val ENGLISH_MONTHS_FULL = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        fun formatYearMonthBengali(yearMonth: String, short: Boolean = true): String {
            val parts = yearMonth.split("-")
            if (parts.size != 2) return yearMonth
            val year = parts[0]
            val monthIndex = (parts[1].toIntOrNull() ?: 1) - 1
            if (monthIndex !in 0..11) return yearMonth
            val bnMonth = if (short) BENGALI_MONTHS_SHORT[monthIndex] else BENGALI_MONTHS_FULL[monthIndex]
            val yearSuffix = if (short) "'${toBengaliDigits(year.takeLast(2))}" else " ${toBengaliDigits(year)}"
            return "$bnMonth$yearSuffix"
        }

        fun formatYearMonth(yearMonth: String, lang: String, short: Boolean = true): String {
            if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") {
                return formatYearMonthBengali(yearMonth, short)
            }
            val parts = yearMonth.split("-")
            if (parts.size != 2) return yearMonth
            val year = parts[0]
            val monthIndex = (parts[1].toIntOrNull() ?: 1) - 1
            if (monthIndex !in 0..11) return yearMonth
            val enMonth = if (short) ENGLISH_MONTHS_SHORT[monthIndex] else ENGLISH_MONTHS_FULL[monthIndex]
            val yearSuffix = if (short) "'${year.takeLast(2)}" else " $year"
            return "$enMonth$yearSuffix"
        }

        fun formatDigits(input: String, lang: String): String =
            com.example.data.util.SupportedLanguage.formatDigits(input, lang)

        fun toBengaliDigits(input: String): String {
            val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
            val sb = StringBuilder(input.length)
            for (ch in input) {
                if (ch in '0'..'9') {
                    sb.append(bnDigits[ch - '0'])
                } else {
                    sb.append(ch)
                }
            }
            return sb.toString()
        }
    }
}
