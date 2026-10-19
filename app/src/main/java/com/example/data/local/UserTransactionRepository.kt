package com.example.data.local

import com.example.data.model.TransactionStatus
import com.example.data.model.UserTransactionRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern implementation for managing UserTransactionRecords.
 * Abstracts DAO access for Clean Architecture.
 */
class UserTransactionRepository(private val dao: UserTransactionDao) {

    val allTransactions: Flow<List<UserTransactionRecord>> = dao.getAllTransactions()
    val pendingTransactions: Flow<List<UserTransactionRecord>> = dao.getPendingTransactions()
    val verifiedTransactions: Flow<List<UserTransactionRecord>> = dao.getVerifiedTransactions()
    val rejectedTransactions: Flow<List<UserTransactionRecord>> = dao.getRejectedTransactions()

    fun getTransactionsByStatus(status: String): Flow<List<UserTransactionRecord>> =
        dao.getTransactionsByStatus(status)

    fun getTransactionsByUserId(userId: String): Flow<List<UserTransactionRecord>> =
        dao.getTransactionsByUserId(userId)

    fun getTransactionByIdFlow(id: Long): Flow<UserTransactionRecord?> =
        dao.getTransactionByIdFlow(id)

    suspend fun getTransactionById(id: Long): UserTransactionRecord? =
        dao.getTransactionById(id)

    suspend fun getTransactionByTrxId(trxId: String): UserTransactionRecord? =
        dao.getTransactionByTrxId(trxId)

    suspend fun isTransactionIdExists(trxId: String): Boolean =
        dao.isTransactionIdExists(trxId)

    suspend fun getCountByStatus(status: String): Int =
        dao.getCountByStatus(status)

    suspend fun insertTransaction(transaction: UserTransactionRecord): Long =
        dao.insertTransaction(transaction)

    suspend fun insertTransactions(transactions: List<UserTransactionRecord>): List<Long> =
        dao.insertTransactions(transactions)

    suspend fun updateTransaction(transaction: UserTransactionRecord) =
        dao.updateTransaction(transaction)

    suspend fun markAsVerified(trxId: String, verifiedAt: Long = System.currentTimeMillis()) {
        dao.updateStatusByTrxId(
            trxId = trxId,
            newStatus = TransactionStatus.VERIFIED,
            verifiedAt = verifiedAt,
            rejectionReason = null
        )
    }

    suspend fun markAsRejected(trxId: String, reason: String) {
        dao.updateStatusByTrxId(
            trxId = trxId,
            newStatus = TransactionStatus.REJECTED,
            verifiedAt = null,
            rejectionReason = reason
        )
    }

    suspend fun markAsPending(trxId: String) {
        dao.updateStatusByTrxId(
            trxId = trxId,
            newStatus = TransactionStatus.PENDING,
            verifiedAt = null,
            rejectionReason = null
        )
    }

    suspend fun deleteTransaction(transaction: UserTransactionRecord) =
        dao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        dao.deleteTransactionById(id)

    suspend fun deleteTransactionByTrxId(trxId: String) =
        dao.deleteTransactionByTrxId(trxId)

    suspend fun clearAll() = dao.clearAllTransactions()
}
