package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ServiceCategory(val displayName: String, val iconEmoji: String) {
    TIKTOK("TikTok Coins", "🪙"),
    CHAMET("Chamet Diamonds", "💎"),
    FREEFIRE("Gaming Top-up", "🔥"),
    EPAY("EPAY Dollar", "💸"),
    EXCHANGE("BD → India Ex.", "🔄"),
    SOCIAL_BOOST("Social Boost", "🚀")
}

data class SocialBoostPlatform(
    val id: String,
    val name: String,
    val iconKey: String = "GENERIC",
    val description: String = "",
    val displayOrder: Int = 1,
    val isEnabled: Boolean = true
)

data class SocialBoostCategory(
    val id: String,
    val platformId: String,
    val name: String,
    val description: String = "",
    val displayOrder: Int = 1,
    val isEnabled: Boolean = true
)

data class SocialBoostService(
    val id: String,
    val platformId: String,
    val categoryId: String,
    val name: String,
    val pricePerThousandBdt: Double,
    val minQuantity: Int = 100,
    val maxQuantity: Int = 100000,
    val averageTime: String = "15 - 45 Minutes",
    val description: String = "",
    val requiresLink: Boolean = true,
    val displayOrder: Int = 1,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val serviceCode: Int
        get() {
            val digits = id.filter { it.isDigit() }
            if (digits.length in 2..4) {
                return digits.toIntOrNull() ?: 800
            }
            return 800 + (kotlin.math.abs(id.hashCode()) % 199)
        }

    fun displayCode(): String = serviceCode.toString()
}

data class TopupPackage(
    val id: String,
    val category: ServiceCategory,
    val name: String,
    val subtitle: String,
    val amount: String,
    val priceBdt: Double,
    val badge: String? = null,
    val isPopular: Boolean = false,
    val bonus: String? = null
)

@Entity(tableName = "orders")
data class OrderRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val category: String,
    val itemName: String,
    val amountOrQuantity: String,
    val priceBdt: Double,
    val userAccountId: String,
    val paymentMethod: String,
    val transactionTrxId: String,
    val orderNote: String = "",
    val screenshotUri: String = "",
    val status: String = "Pending", // Pending, Processing, Completed, Cancelled
    val timestamp: Long = System.currentTimeMillis(),
    val contactPlatform: String = "WhatsApp", // WhatsApp or Telegram
    val customerEmail: String = "",
    val customerPhone: String = "",
    val customerName: String = ""
)

@Entity(tableName = "saved_uids")
data class SavedUid(
    @PrimaryKey
    val serviceName: String,
    val lastUsedUid: String,
    val lastUsedName: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

data class EpayRateTier(
    val rangeLabel: String,
    val usdExample: Double,
    val rateText: String,
    val bdtCalculated: Double?,
    val isPremium: Boolean = false
)

data class PaymentAccount(
    val method: String,
    val number: String,
    val type: String, // Personal, Agent, Merchant
    val instruction: String,
    val colorHex: Long
)

data class OrderNotification(
    val id: String,
    val orderNumber: String,
    val title: String,
    val message: String,
    val status: String, // Processing, Completed, Cancelled
    val trxId: String,
    val itemName: String,
    val priceBdt: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val customerEmail: String = ""
)

data class CustomerProfile(
    val accountId: String,
    val displayName: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUri: String = "",
    val walletBalanceBdt: Double = 0.0,
    val referralCode: String = "",
    val referredByCode: String = "",
    val referralEarningsBdt: Double = 0.0,
    val referralCount: Int = 0,
    val lastCategory: String = "",
    val paymentMethod: String = "",
    val totalOrders: Int = 0,
    val completedOrders: Int = 0,
    val totalSpentBdt: Double = 0.0,
    val lastOrderTimestamp: Long = System.currentTimeMillis(),
    val isVip: Boolean = false,
    val isBlocked: Boolean = false,
    val adminNote: String = ""
) {
    val totalReferralsCount: Int get() = referralCount
}

data class ReferralRewardRecord(
    val id: String,
    val referrerEmail: String,
    val referrerCode: String,
    val buyerEmail: String,
    val buyerName: String,
    val orderNumber: String,
    val itemName: String,
    val orderPriceBdt: Double,
    val commissionBdt: Double,
    val status: String = "Completed",
    val timestamp: Long = System.currentTimeMillis()
) {
    val serviceName: String get() = itemName
    val orderAmountBdt: Double get() = orderPriceBdt
    val commissionEarnedBdt: Double get() = commissionBdt
    val referredUserName: String get() = buyerName
    val referredUserEmail: String get() = buyerEmail
}

data class ReferredMemberSummary(
    val memberEmail: String,
    val memberName: String,
    val memberPhone: String = "",
    val totalOrdersCount: Int = 0,
    val completedOrdersCount: Int = 0,
    val pendingOrdersCount: Int = 0,
    val totalOrderSpentBdt: Double = 0.0,
    val totalBonusEarnedBdt: Double = 0.0,
    val lastActivityTimestamp: Long = System.currentTimeMillis()
)

data class WalletTransactionEntry(
    val id: String,
    val userEmail: String,
    val type: String, // ADD_MONEY, SERVICE_PURCHASE, REFERRAL_BONUS, ADMIN_CREDIT, ADMIN_DEBIT
    val title: String,
    val amountBdt: Double,
    val isCredit: Boolean,
    val status: String = "Completed", // Completed, Processing, Cancelled
    val reasonOrNote: String = "",
    val referenceId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class SavedServiceAccount(
    val id: String,
    val userEmail: String,
    val serviceName: String, // e.g., Chamet, TikTok, Free Fire, EPAY, Exchange, Social Boost
    val accountId: String,
    val label: String = "",
    val isDefault: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_messages")
data class SupportChatMessage(
    @PrimaryKey
    val id: String = "",
    val conversationId: String = "",
    val senderName: String = "",
    val senderContact: String = "",
    val senderEmail: String = "",
    val message: String = "",
    val imageUrl: String = "",
    val senderRole: String = "USER", // "USER" or "ADMIN"
    val adminReply: String = "",
    val isReadByAdmin: Boolean = false,
    val isReadByUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)


