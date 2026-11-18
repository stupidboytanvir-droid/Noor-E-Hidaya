package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "hadith_records",
    indices = [
        Index(value = ["bookId", "chapterNumber", "hadithIndexInBook"]),
        Index(value = ["bookId", "pageNumber"]),
        Index(value = ["hadithNumber"])
    ]
)
data class HadithEntity(
    @PrimaryKey
    val id: String,                 // e.g. "bukhari_1"
    val bookId: String,             // e.g. "bukhari", "muslim", "riyadus_salihin"
    val bookNameBengali: String,
    val chapterId: String,
    val chapterNameBengali: String,
    val chapterNameArabic: String,
    val chapterNumber: Int,
    val hadithNumber: String,       // e.g. "১", "২"
    val hadithIndexInBook: Int,     // numeric index for sequential sorting 1, 2, 3...
    val pageNumber: Int,            // physical book page number
    val narrator: String,
    val arabicText: String,
    val banglaText: String,
    val explanation: String = "",
    val grade: String = "সহীহ"
)

@Dao
interface HadithDao {

    @Query("SELECT COUNT(*) FROM hadith_records WHERE bookId = :bookId")
    suspend fun getHadithCountForBook(bookId: String): Int

    @Query("SELECT COUNT(*) FROM hadith_records")
    suspend fun getTotalHadithCount(): Int

    @Query("SELECT * FROM hadith_records WHERE bookId = :bookId ORDER BY hadithIndexInBook ASC LIMIT :limit OFFSET :offset")
    suspend fun getHadithsPaged(bookId: String, limit: Int, offset: Int): List<HadithEntity>

    @Query("SELECT * FROM hadith_records WHERE bookId = :bookId AND chapterNumber = :chapterNumber ORDER BY hadithIndexInBook ASC LIMIT :limit OFFSET :offset")
    suspend fun getHadithsByChapterPaged(bookId: String, chapterNumber: Int, limit: Int, offset: Int): List<HadithEntity>

    @Query("SELECT * FROM hadith_records WHERE bookId = :bookId AND pageNumber = :pageNumber ORDER BY hadithIndexInBook ASC")
    suspend fun getHadithsByPage(bookId: String, pageNumber: Int): List<HadithEntity>

    @Query("SELECT * FROM hadith_records WHERE id = :id LIMIT 1")
    suspend fun getHadithById(id: String): HadithEntity?

    @Query("SELECT * FROM hadith_records WHERE bookId = :bookId AND hadithNumber = :hadithNumber LIMIT 1")
    suspend fun getHadithByNumber(bookId: String, hadithNumber: String): HadithEntity?

    @Query("""
        SELECT * FROM hadith_records 
        WHERE (:bookId IS NULL OR bookId = :bookId) 
          AND (hadithNumber LIKE '%' || :query || '%' 
               OR banglaText LIKE '%' || :query || '%' 
               OR narrator LIKE '%' || :query || '%' 
               OR chapterNameBengali LIKE '%' || :query || '%'
               OR arabicText LIKE '%' || :query || '%')
        ORDER BY hadithIndexInBook ASC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchHadiths(bookId: String?, query: String, limit: Int = 50, offset: Int = 0): List<HadithEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hadiths: List<HadithEntity>)

    @Query("SELECT DISTINCT chapterNumber, chapterId, chapterNameBengali, chapterNameArabic, MIN(pageNumber) as minPage, MAX(pageNumber) as maxPage, COUNT(*) as cnt FROM hadith_records WHERE bookId = :bookId GROUP BY chapterNumber ORDER BY chapterNumber ASC")
    suspend fun getChapterSummaries(bookId: String): List<ChapterSummaryTuple>
}

data class ChapterSummaryTuple(
    val chapterNumber: Int,
    val chapterId: String,
    val chapterNameBengali: String,
    val chapterNameArabic: String,
    val minPage: Int,
    val maxPage: Int,
    val cnt: Int
)
