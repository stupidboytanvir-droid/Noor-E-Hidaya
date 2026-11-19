package com.example.data.ai

import android.content.Context
import com.example.data.util.SupportedLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)

data class DailyAiNasihah(
    val dateKey: String,
    val languageCode: String,
    val title: String,
    val categoryLabel: String,
    val arabicQuote: String,
    val adviceText: String,
    val actionPoint: String,
    val reference: String,
    val isAiGenerated: Boolean = true,
    val generatedAt: Long = System.currentTimeMillis()
) {
    fun buildShareableText(): String {
        val arabicBlock = if (arabicQuote.isNotBlank()) "\n$arabicQuote\n" else ""
        val lang = SupportedLanguage.normalizeCode(languageCode)
        val headerTitle = com.example.ui.theme.LocalizationRegistry.translate(lang, "দৈনিক মুমিনের নসিহত", "Daily Believer's Nasihah")
        val topicLabel = com.example.ui.theme.LocalizationRegistry.translate(lang, "বিষয়:", "Topic:")
        val actionLabel = com.example.ui.theme.LocalizationRegistry.translate(lang, "আজকের আমল:", "Practical Action:")
        val refLabel = com.example.ui.theme.LocalizationRegistry.translate(lang, "রেফারেন্স:", "Reference:")
        val footerLabel = com.example.ui.theme.LocalizationRegistry.translate(
            lang,
            "নূর-এ-হেদায়াহ অ্যাপ থেকে সংরক্ষিত",
            "Saved from Noor-e-Hidayah"
        )
        return """
            ✨ $headerTitle ($dateKey)
            📌 $topicLabel $categoryLabel

            ❝ $title ❞
            $arabicBlock
            $adviceText

            ✅ $actionLabel
            $actionPoint

            📖 $refLabel $reference

            ($footerLabel)
        """.trimIndent()
    }
}

sealed interface DailyNasihahUiState {
    data object Loading : DailyNasihahUiState
    data class Ready(val nasihah: DailyAiNasihah) : DailyNasihahUiState
    data class Error(val message: String, val cachedFallback: DailyAiNasihah? = null) : DailyNasihahUiState
}

object DailyNasihahCache {
    private const val PREFS_NAME = "noor_ai_nasihah_cache"

    private fun cacheKey(dateKey: String, languageCode: String): String =
        "nasihah_${dateKey}_${SupportedLanguage.normalizeCode(languageCode)}"

    private fun latestKey(languageCode: String): String =
        "latest_${SupportedLanguage.normalizeCode(languageCode)}"

    fun getCached(context: Context, dateKey: String, languageCode: String): DailyAiNasihah? {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = sp.getString(cacheKey(dateKey, languageCode), null) ?: return null
        return decode(raw)
    }

    fun getLatestForLanguage(context: Context, languageCode: String): DailyAiNasihah? {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = sp.getString(latestKey(languageCode), null) ?: return null
        return decode(raw)
    }

    fun save(context: Context, item: DailyAiNasihah) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encoded = encode(item)
        sp.edit()
            .putString(cacheKey(item.dateKey, item.languageCode), encoded)
            .putString(latestKey(item.languageCode), encoded)
            .apply()
    }

    internal fun encode(item: DailyAiNasihah): String = JSONObject().apply {
        put("dateKey", item.dateKey)
        put("languageCode", SupportedLanguage.normalizeCode(item.languageCode))
        put("title", item.title)
        put("categoryLabel", item.categoryLabel)
        put("arabicQuote", item.arabicQuote)
        put("adviceText", item.adviceText)
        put("actionPoint", item.actionPoint)
        put("reference", item.reference)
        put("isAiGenerated", item.isAiGenerated)
        put("generatedAt", item.generatedAt)
    }.toString()

    internal fun decode(raw: String): DailyAiNasihah? {
        return try {
            val json = JSONObject(raw)
            val title = json.optString("title", "").trim()
            val advice = json.optString("adviceText", "").trim()
            if (title.isBlank() || advice.isBlank()) return null
            DailyAiNasihah(
                dateKey = json.optString("dateKey", ""),
                languageCode = SupportedLanguage.normalizeCode(json.optString("languageCode", "en")),
                title = title,
                categoryLabel = json.optString("categoryLabel", ""),
                arabicQuote = json.optString("arabicQuote", ""),
                adviceText = advice,
                actionPoint = json.optString("actionPoint", ""),
                reference = json.optString("reference", ""),
                isAiGenerated = json.optBoolean("isAiGenerated", true),
                generatedAt = json.optLong("generatedAt", System.currentTimeMillis())
            )
        } catch (_: Exception) {
            null
        }
    }
}

class GeminiIslamicAssistant {

    companion object {
        private const val TAG = "GeminiIslamicAssistant"
        private const val CHAT_MODEL_NAME = "gemini-3.8-flash"
        private const val NASIHAH_MODEL_NAME = "gemini-3.8-flash"
        private const val INFERENCE_TIMEOUT_MS = 35_000L
    }

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = CHAT_MODEL_NAME,
            systemInstruction = content {
                text(
                    """
                    You are 'Noor Assistant', a compassionate, knowledgeable, and strictly Islamic spiritual guide inside the Noor-e-Hidayah app.
                    SCOPE RULE (MANDATORY): Answer ONLY questions related to Islam—including the Holy Quran, Tafsir, Sahih Hadith (Bukhari, Muslim, Abu Dawood, Tirmidhi, Nasa'i, Ibn Majah), Fiqh/Ibadah (Salah, Sawm, Zakat, Hajj), Seerah, Islamic history, Du'a, Akhlaq, halal lifestyle, Islamic finance, or Muslim spiritual guidance.
                    If a user asks a completely off-topic, secular, non-Islamic request (such as software coding, movie/celebrity gossip, gambling/crypto tips, or unrelated secular trivia), politely decline in the requested language and invite them to ask an Islamic or spiritual question instead.
                    QUALITY RULE: Always include full Arabic tashkeel/harakat on any Quranic ayah or du'a quoted, cite authentic references accurately, and write clearly in the exact target language requested.
                    """.trimIndent()
                )
            }
        )
    }

    private val nasihahSchema = Schema.obj(
        properties = mapOf(
            "title" to Schema.string(description = "Concise, inspiring title in the target language"),
            "categoryLabel" to Schema.string(description = "Short topic badge in the target language, e.g. Patience & Gratitude"),
            "arabicQuote" to Schema.string(description = "Authentic short Quranic ayah or Hadith phrase in Arabic with full tashkeel/harakat"),
            "adviceText" to Schema.string(description = "Heartfelt, authentic 2-3 sentence spiritual reflection in the target language"),
            "actionPoint" to Schema.string(description = "One specific, actionable Sunnah deed to apply today in the target language"),
            "reference" to Schema.string(description = "Authentic Quran or Hadith citation in the target language")
        )
    )

    private val nasihahModel by lazy {
        Firebase.ai.generativeModel(
            modelName = NASIHAH_MODEL_NAME,
            generationConfig = generationConfig {
                temperature = 0.7f
                maxOutputTokens = 768
                responseMimeType = "application/json"
                responseSchema = nasihahSchema
            },
            systemInstruction = content {
                text(
                    """
                    You are an authentic Islamic spiritual mentor grounded strictly in the Holy Quran and Sahih Hadith.
                    Generate one concise, uplifting daily Believer's Nasihah as JSON matching the schema.
                    CRITICAL LANGUAGE RULE: All fields EXCEPT arabicQuote must be written strictly in the requested target language. Never mix Bengali into non-Bengali outputs.
                    arabicQuote MUST be authentic Arabic with full tashkeel/harakat.
                    """.trimIndent()
                )
            }
        )
    }

    fun generateReflectionStream(
        userPrompt: String,
        uiLanguage: String,
        history: List<ChatMessage> = emptyList()
    ): Flow<String> = flow {
        val normLang = SupportedLanguage.normalizeCode(uiLanguage)
        val langObj = SupportedLanguage.fromCode(normLang)

        val recentHistory = history
            .filter { !it.isError && it.id != "welcome" && it.text.isNotBlank() }
            .takeLast(6)
            .map { msg ->
                content(role = if (msg.sender == "user") "user" else "model") {
                    text(msg.text)
                }
            }

        val chat = chatModel.startChat(history = recentHistory)
        val localizedInstruction =
            "[Target Language: ${langObj.englishName} (${langObj.nativeName}), code=$normLang. " +
                "Respond strictly in ${langObj.englishName}, keeping Quran/Du'a Arabic quotes with full tashkeel.]\n\n" +
                userPrompt.trim()

        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            chat.sendMessageStream(content("user") { text(localizedInstruction) }).collect { chunk ->
                val part = chunk.text.orEmpty()
                if (part.isNotEmpty()) {
                    accumulated.append(part)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("Model returned an empty response.")
        }
    }.flowOn(Dispatchers.IO)

    suspend fun generateDailyBelieversNasihah(
        context: Context,
        languageCode: String,
        dateKey: String,
        forceRefresh: Boolean = false
    ): DailyAiNasihah = withContext(Dispatchers.IO) {
        val normLang = SupportedLanguage.normalizeCode(languageCode)
        if (!forceRefresh) {
            DailyNasihahCache.getCached(context, dateKey, normLang)?.let { cached ->
                return@withContext cached
            }
        }

        val langObj = SupportedLanguage.fromCode(normLang)
        val seedNote = if (forceRefresh) "Provide a different topic and ayah/hadith than earlier today." else "Provide today's daily reflection."
        val prompt = """
            Date: $dateKey
            Target Language: ${langObj.englishName} (${langObj.nativeName}) [ISO: $normLang]
            Task: Generate a fresh, spiritually uplifting Daily Believer's Nasihah rooted in an authentic Quranic verse or Sahih Hadith.
            $seedNote
            Ensure title, categoryLabel, adviceText, actionPoint, and reference are 100% written in ${langObj.englishName} (${langObj.nativeName}), and arabicQuote has full tashkeel.
        """.trimIndent()

        val response = withTimeout(INFERENCE_TIMEOUT_MS) {
            nasihahModel.generateContent(prompt)
        }
        val rawJson = response.text.orEmpty()
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        if (rawJson.isBlank()) {
            throw IllegalStateException("Empty Nasihah response from Gemini.")
        }
        val json = JSONObject(rawJson)
        val parsedTitle = json.optString("title", "").trim()
        val parsedAdvice = json.optString("adviceText", "").trim()
        if (parsedTitle.isBlank() || parsedAdvice.isBlank()) {
            throw IllegalStateException("Incomplete Nasihah JSON from Gemini.")
        }

        val parsed = DailyAiNasihah(
            dateKey = dateKey,
            languageCode = normLang,
            title = parsedTitle,
            categoryLabel = json.optString("categoryLabel", "").trim(),
            arabicQuote = json.optString("arabicQuote", "").trim(),
            adviceText = parsedAdvice,
            actionPoint = json.optString("actionPoint", "").trim(),
            reference = json.optString("reference", "").trim(),
            isAiGenerated = true,
            generatedAt = System.currentTimeMillis()
        )
        DailyNasihahCache.save(context, parsed)
        parsed
    }

    /**
     * Dynamically constructs a verified, language-aware daily reflection from the app's
     * authentic Hadith & Quran repository when live Gemini AI is unreachable or times out.
     * Ensures the Believer's Nasihah card never hangs indefinitely in a Loading spinner.
     */
    fun buildAuthenticFallbackNasihah(
        context: Context,
        languageCode: String,
        dateKey: String
    ): DailyAiNasihah {
        val normLang = SupportedLanguage.normalizeCode(languageCode)
        val pool = com.example.data.model.HadithRepository.HADITHS
        val dayIndex = kotlin.math.abs((dateKey.hashCode() + 31)) % pool.size
        val hadith = pool[dayIndex]

        val localizedTitle = if (normLang == "bn") hadith.title else {
            val enTitle = hadith.titleEnglish.ifBlank { hadith.title }
            com.example.ui.theme.LocalizationRegistry.translate(normLang, hadith.title, enTitle)
        }
        val localizedAdvice = if (normLang == "bn") {
            hadith.explanationBengali.ifBlank { hadith.translationBengali }
        } else {
            val enAdvice = hadith.explanationEnglish.ifBlank { hadith.translationEnglish }
            com.example.ui.theme.LocalizationRegistry.translate(
                normLang,
                hadith.explanationBengali.ifBlank { hadith.translationBengali },
                enAdvice
            )
        }
        val localizedRef = if (normLang == "bn") {
            hadith.bookReference
        } else {
            val enRef = hadith.bookReferenceEnglish.ifBlank { hadith.bookReference }
            com.example.ui.theme.LocalizationRegistry.translate(normLang, hadith.bookReference, enRef)
        }
        val categoryBadge = com.example.ui.theme.LocalizationRegistry.translate(
            normLang,
            "দৈনিক সুন্নাহ হিকমাহ",
            "Daily Prophetic Wisdom"
        )
        val actionText = com.example.ui.theme.LocalizationRegistry.translate(
            normLang,
            "আজকের জীবনে এই হাদিসের শিক্ষা অনুযায়ী অন্তত একটি ভালো আমল করুন এবং আল্লাহর শুকরিয়া আদায় করুন।",
            "Apply this Prophetic guidance in one sincere deed today and thank Allah."
        )

        return DailyAiNasihah(
            dateKey = dateKey,
            languageCode = normLang,
            title = localizedTitle,
            categoryLabel = categoryBadge,
            arabicQuote = hadith.textArabic,
            adviceText = localizedAdvice,
            actionPoint = actionText,
            reference = localizedRef,
            isAiGenerated = false,
            generatedAt = System.currentTimeMillis()
        )
    }
}
