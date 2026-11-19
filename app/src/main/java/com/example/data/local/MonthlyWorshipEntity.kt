package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "monthly_worship")
data class MonthlyWorshipEntity(
    @PrimaryKey
    val yearMonth: String, // Format: "yyyy-MM" e.g. "2026-11"
    val quranMinutes: Int = 0,
    val duaCount: Int = 0,
    val tasbihCount: Int = 0,
    val duroodCount: Int = 0,
    val hadithMinutes: Int = 0,
    val hadithReadCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface MonthlyWorshipDao {
    @Query("SELECT * FROM monthly_worship ORDER BY yearMonth DESC LIMIT 12")
    fun observeLast12Months(): Flow<List<MonthlyWorshipEntity>>

    @Query("SELECT * FROM monthly_worship ORDER BY yearMonth ASC")
    suspend fun getAllMonths(): List<MonthlyWorshipEntity>

    @Query("SELECT * FROM monthly_worship WHERE yearMonth = :yearMonth LIMIT 1")
    suspend fun getMonth(yearMonth: String): MonthlyWorshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MonthlyWorshipEntity)

    @Query("DELETE FROM monthly_worship")
    suspend fun clearAll()
}
