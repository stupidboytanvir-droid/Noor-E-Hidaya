package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "debt_ledger")
data class DebtLedgerEntity(
    @PrimaryKey
    val id: String,
    val userId: String = "",
    val type: String, // "GIVE" (পাওনা / ঋণ দিয়েছি) or "TAKE" (দেনা / ঋণ নিয়েছি)
    val personName: String,
    val phoneNumber: String = "",
    val amount: Double,
    val transactionDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val notes: String = "",
    val proofAttachmentUri: String? = null, // Photo or doc uri/path
    val proofAttachmentType: String? = null, // "IMAGE", "DOCUMENT"
    val proofAttachmentNote: String? = null, // E.g., "চুক্তিপত্র স্বাক্ষর ও সাক্ষী"
    val isSettled: Boolean = false,
    val settledDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface DebtLedgerDao {
    @Query("SELECT * FROM debt_ledger ORDER BY isSettled ASC, transactionDate DESC")
    fun getAllEntries(): Flow<List<DebtLedgerEntity>>

    @Query("SELECT * FROM debt_ledger WHERE type = :type ORDER BY isSettled ASC, transactionDate DESC")
    fun getEntriesByType(type: String): Flow<List<DebtLedgerEntity>>

    @Query("SELECT * FROM debt_ledger WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: String): DebtLedgerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: DebtLedgerEntity)

    @Update
    suspend fun update(entry: DebtLedgerEntity)

    @Query("DELETE FROM debt_ledger WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE debt_ledger SET isSettled = :isSettled, settledDate = :settledDate, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSettlementStatus(id: String, isSettled: Boolean, settledDate: Long?, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM debt_ledger")
    suspend fun clearAll()
}
