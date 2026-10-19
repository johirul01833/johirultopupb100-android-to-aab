package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Status constants for user transaction records.
 */
object TransactionStatus {
    const val PENDING = "pending"
    const val VERIFIED = "verified"
    const val REJECTED = "rejected"

    val ALL = listOf(PENDING, VERIFIED, REJECTED)
}

/**
 * Room Entity representing a user transaction record with its status and transaction ID.
 */
@Entity(
    tableName = "user_transactions",
    indices = [
        Index(value = ["transactionId"], unique = true),
        Index(value = ["status"]),
        Index(value = ["userId"]),
        Index(value = ["timestamp"])
    ]
)
data class UserTransactionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "transactionId")
    val transactionId: String,

    @ColumnInfo(name = "status")
    val status: String = TransactionStatus.PENDING, // "pending", "verified", "rejected"

    @ColumnInfo(name = "userId")
    val userId: String = "",

    @ColumnInfo(name = "paymentMethod")
    val paymentMethod: String = "",

    @ColumnInfo(name = "amount")
    val amount: Double = 0.0,

    @ColumnInfo(name = "currency")
    val currency: String = "BDT",

    @ColumnInfo(name = "orderNumber")
    val orderNumber: String = "",

    @ColumnInfo(name = "category")
    val category: String = "",

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "verifiedAt")
    val verifiedAt: Long? = null,

    @ColumnInfo(name = "rejectionReason")
    val rejectionReason: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String = ""
)
