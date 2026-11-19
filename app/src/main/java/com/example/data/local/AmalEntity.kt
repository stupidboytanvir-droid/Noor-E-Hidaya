package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "daily_amal")
data class AmalEntity(
    @PrimaryKey
    val dateString: String, // "YYYY-MM-DD"
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false,
    val tahajjud: Boolean = false,
    val surahKahf: Boolean = false,
    val tasbeehDone: Boolean = false
)

@Dao
interface AmalDao {
    @Query("SELECT * FROM daily_amal WHERE dateString = :dateString LIMIT 1")
    fun getAmalForDate(dateString: String): Flow<AmalEntity?>

    @Query("SELECT * FROM daily_amal ORDER BY dateString DESC LIMIT 7")
    fun getRecentWeekAmal(): Flow<List<AmalEntity>>

    @Query("SELECT * FROM daily_amal ORDER BY dateString DESC LIMIT 30")
    suspend fun getAllRecentAmals(): List<AmalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAmal(amal: AmalEntity)

    @Query("DELETE FROM daily_amal")
    suspend fun clearAll()
}
