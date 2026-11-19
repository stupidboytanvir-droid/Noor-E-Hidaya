package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasbih_counters")
data class TasbihEntity(
    @PrimaryKey
    val id: String,
    val arabicPhrase: String,
    val transliteration: String,
    val bengaliMeaning: String,
    val targetCount: Int = 33,
    val currentCount: Int = 0,
    val completedRounds: Int = 0,
    val totalHistoricalCount: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isCustom: Boolean get() = id.startsWith("custom_")
}

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_counters ORDER BY updatedAt DESC")
    fun getAllCounters(): Flow<List<TasbihEntity>>

    @Query("SELECT * FROM tasbih_counters WHERE id = :id LIMIT 1")
    suspend fun getCounterById(id: String): TasbihEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(counter: TasbihEntity)

    @Update
    suspend fun update(counter: TasbihEntity)

    @Query("DELETE FROM tasbih_counters WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM tasbih_counters")
    suspend fun clearAll()
}
