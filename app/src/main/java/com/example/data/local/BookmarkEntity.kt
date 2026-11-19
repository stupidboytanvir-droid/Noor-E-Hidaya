package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "quran_bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val key: String, // "surah_ayah"
    val surahNumber: Int,
    val surahNameArabic: String,
    val surahNameEnglish: String,
    val ayahNumber: Int,
    val ayahTextArabic: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM quran_bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM quran_bookmarks WHERE `key` = :key")
    suspend fun removeBookmark(key: String)

    @Query("SELECT EXISTS(SELECT 1 FROM quran_bookmarks WHERE `key` = :key)")
    suspend fun isBookmarked(key: String): Boolean

    @Query("DELETE FROM quran_bookmarks")
    suspend fun clearAll()
}
