package com.example.data.local

import android.content.Context
import android.util.Log
import com.example.data.model.Hadith
import com.example.data.model.HadithBook
import com.example.data.model.HadithChapter
import com.example.data.repository.HadithMasterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object HadithDatabaseLoader {

    private const val TAG = "HadithDatabaseLoader"
    private const val ASSETS_HADITH_DIR = "hadith"

    /**
     * Initializes the Room Database with offline JSON assets on first app launch or when empty.
     */
    suspend fun populateDatabaseIfEmpty(context: Context, database: AppDatabase) = withContext(Dispatchers.IO) {
        val hadithDao = database.hadithDao()
        val count = hadithDao.getTotalHadithCount()
        if (count > 0) {
            Log.d(TAG, "Hadith database already seeded ($count records). Skipping.")
            return@withContext
        }

        Log.d(TAG, "Hadith database empty. Seeding from assets...")
        val bookFiles = listOf(
            "bukhari.json",
            "muslim.json",
            "riyadus_salihin.json",
            "tirmidhi.json",
            "abudawood.json",
            "nasai.json",
            "ibnmajah.json"
        )

        val entitiesToInsert = mutableListOf<HadithEntity>()

        for (fileName in bookFiles) {
            try {
                val assetPath = "$ASSETS_HADITH_DIR/$fileName"
                context.assets.open(assetPath).use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val jsonString = reader.readText()
                    val root = JSONObject(jsonString)

                    val bookId = root.optString("bookId", "")
                    val bookNameBengali = root.optString("nameBengali", "")

                    // Build a lookup map of chapterNumber -> chapter info
                    val chaptersArray = root.optJSONArray("chapters")
                    val chaptersMap = mutableMapOf<Int, JSONObject>()
                    if (chaptersArray != null) {
                        for (i in 0 until chaptersArray.length()) {
                            val chObj = chaptersArray.getJSONObject(i)
                            val chNum = chObj.optInt("chapterNumber", 1)
                            chaptersMap[chNum] = chObj
                        }
                    }

                    val hadithsArray = root.optJSONArray("hadiths")
                    if (hadithsArray != null) {
                        for (i in 0 until hadithsArray.length()) {
                            val hObj = hadithsArray.getJSONObject(i)
                            val chNum = hObj.optInt("chapterNumber", 1)
                            val chInfo = chaptersMap[chNum]

                            val chBengali = chInfo?.optString("nameBengali", "সাধারণ অধ্যায়") ?: "সাধারণ অধ্যায়"
                            val chArabic = chInfo?.optString("nameArabic", "") ?: ""
                            val hNumber = hObj.optString("hadithNumber", "${i + 1}")
                            val hIndex = hObj.optInt("hadithIndexInBook", i + 1)
                            val pNum = hObj.optInt("pageNumber", 1)
                            val narrator = hObj.optString("narrator", "হযরত আবু হুরায়রা (রা.)")
                            val arabic = hObj.optString("arabicText", "")
                            val bangla = hObj.optString("banglaText", "")
                            val explanation = hObj.optString("explanation", "")
                            val grade = hObj.optString("grade", "সহীহ")

                            val entity = HadithEntity(
                                id = "${bookId}_$hIndex",
                                bookId = bookId,
                                bookNameBengali = bookNameBengali,
                                chapterId = "${bookId}_ch_$chNum",
                                chapterNameBengali = chBengali,
                                chapterNameArabic = chArabic,
                                chapterNumber = chNum,
                                hadithNumber = hNumber,
                                hadithIndexInBook = hIndex,
                                pageNumber = pNum,
                                narrator = narrator,
                                arabicText = arabic,
                                banglaText = bangla,
                                explanation = explanation,
                                grade = grade
                            )
                            entitiesToInsert.add(entity)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading asset $fileName: ${e.message}", e)
            }
        }

        if (entitiesToInsert.isNotEmpty()) {
            hadithDao.insertAll(entitiesToInsert)
            Log.d(TAG, "Successfully seeded ${entitiesToInsert.size} hadiths into Room Database.")
        }
    }

    /**
     * Converts a Room HadithEntity into the Domain Hadith model.
     */
    fun HadithEntity.toDomain(): Hadith {
        return Hadith(
            id = this.id,
            bookId = this.bookId,
            bookNameBengali = this.bookNameBengali,
            chapterId = this.chapterId,
            chapterNameBengali = this.chapterNameBengali,
            chapterNumber = this.chapterNumber,
            hadithNumber = this.hadithNumber,
            pageNumber = this.pageNumber,
            narrator = this.narrator,
            arabicText = this.arabicText,
            banglaText = this.banglaText,
            explanation = this.explanation,
            grade = this.grade
        )
    }

    /**
     * Loads chapters for a book directly from the JSON asset definition.
     */
    fun loadBookChaptersFromAsset(context: Context, bookId: String): List<HadithChapter> {
        val fileName = "$ASSETS_HADITH_DIR/$bookId.json"
        return try {
            context.assets.open(fileName).use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = reader.readText()
                val root = JSONObject(jsonString)
                val chaptersArray = root.optJSONArray("chapters") ?: return emptyList()

                val list = mutableListOf<HadithChapter>()
                for (i in 0 until chaptersArray.length()) {
                    val obj = chaptersArray.getJSONObject(i)
                    val chNum = obj.optInt("chapterNumber", i + 1)
                    val nameBn = obj.optString("nameBengali", "")
                    val nameAr = obj.optString("nameArabic", "")
                    val range = obj.optString("hadithRange", "")
                    val count = obj.optInt("hadithCount", 0)
                    val startP = obj.optInt("startPage", 1)
                    val endP = obj.optInt("endPage", 1)

                    val repoEnglish = HadithMasterRepository.getChaptersForBook(bookId)
                        .find { it.chapterNumber == chNum }?.nameEnglish ?: ""

                    list.add(
                        HadithChapter(
                            id = "${bookId}_ch_$chNum",
                            bookId = bookId,
                            chapterNumber = chNum,
                            nameBengali = nameBn,
                            nameArabic = nameAr,
                            hadithRange = range,
                            hadithCount = count,
                            startPage = startP,
                            endPage = endP,
                            nameEnglish = obj.optString("nameEnglish", repoEnglish).ifBlank { repoEnglish }
                        )
                    )
                }
                list
            }
        } catch (e: Exception) {
            Log.w(TAG, "Falling back to static chapters for $bookId: ${e.message}")
            HadithMasterRepository.getChaptersForBook(bookId)
        }
    }
}
