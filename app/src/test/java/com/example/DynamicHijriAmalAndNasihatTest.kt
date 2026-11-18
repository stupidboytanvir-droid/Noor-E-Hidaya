package com.example

import com.example.data.ai.DailyAiNasihah
import com.example.data.ai.DailyNasihahCache
import com.example.data.model.AmalPlanType
import com.example.data.model.DynamicHijriAmalProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DynamicHijriAmalAndNasihatTest {

    @Test
    fun testFridayActivatesJumatSpecialPlan() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            set(Calendar.HOUR_OF_DAY, 12)
        }

        val plan = DynamicHijriAmalProvider.getTodayAmalPlan(cal)
        assertEquals(AmalPlanType.JUMAT_SPECIAL, plan.type)
        assertTrue(plan.titleBn.contains("জুমু'আ"))
        assertTrue(plan.checklistItems.any { it.titleBn.contains("সূরা আল-কাহাফ") })
        assertTrue(plan.checklistItems.any { it.titleBn.contains("দরূদ") })
    }

    @Test
    fun testNonFridayProvidesNonFridayAmalPlan() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 4) // Jan 4, 2026 is Sunday
            set(Calendar.HOUR_OF_DAY, 10)
        }

        val plan = DynamicHijriAmalProvider.getTodayAmalPlan(cal)
        assertNotEquals(AmalPlanType.JUMAT_SPECIAL, plan.type)
        assertTrue(plan.checklistItems.isNotEmpty())
        assertNotNull(plan.hadithReference)
        assertNotNull(plan.hijriDayBadge)
    }

    @Test
    fun testDailyAiNasihahSerializationAndMultilingualShareText() {
        val enItem = DailyAiNasihah(
            dateKey = "2026-11-16",
            languageCode = "en",
            title = "Gratitude in Every Moment",
            categoryLabel = "Shukr & Patience",
            arabicQuote = "لَئِن شَكَرْتُمْ لَأَزِيدَنَّكُمْ",
            adviceText = "Believers turn blessings into worship by thanking Allah sincerely.",
            actionPoint = "Say Alhamdulillah after completing each task today.",
            reference = "Surah Ibrahim 14:7"
        )

        val encoded = DailyNasihahCache.encode(enItem)
        val decoded = DailyNasihahCache.decode(encoded)
        assertNotNull(decoded)
        assertEquals("en", decoded!!.languageCode)
        assertEquals(enItem.title, decoded.title)
        assertEquals(enItem.actionPoint, decoded.actionPoint)

        val enShare = decoded.buildShareableText()
        assertTrue(enShare.contains("Daily Believer's Nasihah"))
        assertTrue(enShare.contains("Practical Action"))
        assertFalse(enShare.contains("আজকের আমল"))

        val bnItem = enItem.copy(
            languageCode = "bn",
            title = "কৃতজ্ঞতার প্রশান্তি",
            actionPoint = "প্রতিটি কাজের পর মন থেকে আলহামদুলিল্লাহ বলুন।"
        )
        val bnShare = bnItem.buildShareableText()
        assertTrue(bnShare.contains("দৈনিক মুমিনের নসিহত"))
        assertTrue(bnShare.contains(bnItem.actionPoint))
    }
}
