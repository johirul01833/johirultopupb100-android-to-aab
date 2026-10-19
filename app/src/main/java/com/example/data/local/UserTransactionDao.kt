package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserTransactionRecord
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for UserTransactionRecord.
 * Provides reactive Flow observers and suspend functions for CRUD operations.
 */
@Dao
interface UserTransactionDao {

    @Query("SELECT * FROM user_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<UserTransactionRecord>>

    @Query("SELECT * FROM user_transactions WHERE status = :status ORDER BY timestamp DESC")
    fun getTransactionsByStatus(status: String): Flow<List<UserTransactionRecord>>

    @Query("SELECT * FROM user_transactions WHERE status = 'pending' ORDER BY timestamp DESC")
    fun getPendingTransactions(): Flow<List<UserTransactionRecord>>

    @Query("SELECT * FROM user_transactions WHERE status = 'verified' ORDER BY timestamp DESC")
    fun getVerifiedTransactions(): Flow<List<UserTransactionRecord>>

    @Query("SELECT * FROM user_transactions WHERE status = 'rejected' ORDER BY timestamp DESC")
    fun getRejectedTransactions(): Flow<List<UserTransactionRecord>>

    @Query("SELECT * FROM user_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsByUserId(userId: String): Flow<List<UserTransactionRecord>>

    @Query("SELECT * FROM user_transactions WHERE id = :id")
    fun getTransactionByIdFlow(id: Long): Flow<UserTransactionRecord?>

    @Query("SELECT * FROM user_transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): UserTransactionRecord?

    @Query("SELECT * FROM user_transactions WHERE transactionId = :trxId COLLATE NOCASE LIMIT 1")
    suspend fun getTransactionByTrxId(trxId: String): UserTransactionRecord?

    @Query("SELECT EXISTS(SELECT 1 FROM user_transactions WHERE transactionId = :trxId COLLATE NOCASE)")
    suspend fun isTransactionIdExists(trxId: String): Boolean

    @Query("SELECT COUNT(*) FROM user_transactions WHERE status = :status")
    suspend fun getCountByStatus(status: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: UserTransactionRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<UserTransactionRecord>): List<Long>

    @Update
    suspend fun updateTransaction(transaction: UserTransactionRecord)

    @Query("UPDATE user_transactions SET status = :newStatus, verifiedAt = :verifiedAt, rejectionReason = :rejectionReason WHERE id = :id")
    suspend fun updateStatusById(
        id: Long,
        newStatus: String,
        verifiedAt: Long? = null,
        rejectionReason: String? = null
    )

    @Query("UPDATE user_transactions SET status = :newStatus, verifiedAt = :verifiedAt, rejectionReason = :rejectionReason WHERE transactionId = :trxId COLLATE NOCASE")
    suspend fun updateStatusByTrxId(
        trxId: String,
        newStatus: String,
        verifiedAt: Long? = null,
        rejectionReason: String? = null
    )

    @Delete
    suspend fun deleteTransaction(transaction: UserTransactionRecord)

    @Query("DELETE FROM user_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM user_transactions WHERE transactionId = :trxId COLLATE NOCASE")
    suspend fun deleteTransactionByTrxId(trxId: String)

    @Query("DELETE FROM user_transactions")
    suspend fun clearAllTransactions()
}
