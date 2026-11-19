package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "hadith_bookmarks")
data class HadithBookmarkEntity(
    @PrimaryKey
    val hadithId: String,
    val bookId: String,
    val bookName: String,
    val chapterName: String,
    val hadithNumber: String,
    val narrator: String,
    val arabicText: String,
    val banglaText: String,
    val explanation: String = "",
    val grade: String = "সহীহ",
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface HadithBookmarkDao {
    @Query("SELECT * FROM hadith_bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<HadithBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: HadithBookmarkEntity)

    @Query("DELETE FROM hadith_bookmarks WHERE hadithId = :hadithId")
    suspend fun removeBookmark(hadithId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM hadith_bookmarks WHERE hadithId = :hadithId)")
    suspend fun isBookmarked(hadithId: String): Boolean

    @Query("DELETE FROM hadith_bookmarks")
    suspend fun clearAll()
}
