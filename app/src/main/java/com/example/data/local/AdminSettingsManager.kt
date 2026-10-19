package com.example.data.local

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.model.CustomerProfile
import com.example.data.model.OrderNotification
import com.example.data.model.OrderRecord
import com.example.data.model.PaymentAccount
import com.example.data.model.PromoBanner
import com.example.data.model.ReferralRewardRecord
import com.example.data.model.SavedServiceAccount
import com.example.data.model.ServiceCategory
import com.example.data.model.SocialBoostCategory
import com.example.data.model.SocialBoostPlatform
import com.example.data.model.SocialBoostService
import com.example.data.model.TopupPackage
import com.example.data.model.WalletTransactionEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

data class CustomerMeta(
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
    val isVip: Boolean = false,
    val isBlocked: Boolean = false,
    val adminNote: String = ""
) {
    val totalReferralsCount: Int get() = referralCount
}

data class AdminConfig(
    val adminEmail: String = "johirul958423@gmail.com",
    val epayEmail: String = "johirulagency01833@gmail.com",
    val firebaseUserEmail: String = "",
    val firebaseUserId: String = "",
    val bkashNumber: String = "01733256802",
    val bkashType: String = "Personal (Send Money / ক্যাশ ইন)",
    val bkashInstruction: String = "বিকাশ অ্যাপ থেকে Send Money করুন অথবা ক্যাশ ইন করুন। রেফারেন্সে আপনার নাম বা UID লিখুন।",
    val nagadNumber: String = "01874-954130",
    val nagadType: String = "Personal (Send Money / ক্যাশ আউট)",
    val nagadInstruction: String = "নগদ অ্যাপ থেকে Send Money করুন। পেমেন্টের পর TrxID বা স্ক্রিনশট সংরক্ষণ করুন।",
    val rocketNumber: String = "01733256802-8",
    val isRocketEnabled: Boolean = false,
    val whatsappNumber: String = "8801880467335",
    val whatsappDisplay: String = "+880 1880-467335",
    val telegramUsername: String = "johirul0188",
    val telegramUrl: String = "https://t.me/johirul0188",
    val noticeTitle: String = "IMPORTANT RULES (জরুরি নিয়মাবলী)",
    val noticeRule1: String = "Bangladesh account holders can send BDT ➔ INR only.",
    val noticeRule2: String = "EPAY Dollar Exchange করতে প্রথমে আপনার Dollar Amount জানাতে হবে।",
    val noticeRule3: String = "bKash / Nagad-এ টাকা নিতে পারবেন আমাদের বর্তমান রেট অনুযায়ী।",
    val noticeWarning: String = "কোনো ভুল Payment করার আগে অবশ্যই তথ্য যাচাই করুন।",
    val noticeAlert: String = "কোনো অবস্থাতেই আপনার bKash/Nagad PIN, OTP, Verification Code বা Password কাউকে পাঠাবেন না।",
    val noticeShowOnLaunch: Boolean = true,

    // Dynamic Exchange Rates
    val epayBaseRate: Double = 121.0,
    val epayRate970Fixed: Double = 1170.0,
    val epayRate10Fixed: Double = 1200.0,
    val epayRate30Bulk: Double = 122.0,
    val epayRate500Wholesale: Double = 123.0,
    val inrBaseRate: Double = 1.45,
    val tiktokRatePerCoin: Double = 1.80,
    val chametRatePerDiamond: Double = 0.023,

    // Dynamic Product Package Custom Prices: packageId -> custom price in BDT
    val customPrices: Map<String, Double> = emptyMap(),
    // Custom Added Packages & Deleted Package IDs
    val customPackages: List<TopupPackage> = emptyList(),
    val deletedPackageIds: Set<String> = emptySet(),
    // Customer Metadata (VIP / Blocked / Notes / Wallet / Referral)
    val customerMetadata: Map<String, CustomerMeta> = emptyMap(),

    val storeStatusOverride: String = "AUTO", // AUTO, OPEN, CLOSED

    // Automation settings
    val isAutoTopupEnabled: Boolean = true,
    val autoInstantWalletEnabled: Boolean = false,
    val tiktokApiUrl: String = "",
    val tiktokApiKey: String = "",
    val chametApiUrl: String = "",
    val chametApiKey: String = "",

    // Carousel banners
    val promoBanners: List<PromoBanner> = emptyList(),

    // Dynamic Social Boost (Platforms, Categories, Services) controlled from Admin Panel & Firebase
    val socialBoostPlatforms: List<SocialBoostPlatform> = emptyList(),
    val socialBoostCategories: List<SocialBoostCategory> = emptyList(),
    val socialBoostServices: List<SocialBoostService> = emptyList(),

    // Dynamic Referral & Wallet System Settings controlled from Admin Panel & Firebase
    val isReferralEnabled: Boolean = true,
    val referralCommissionMode: String = "FIXED", // FIXED (per base amount), FLAT (per order), PERCENTAGE
    val referralBaseOrderAmountBdt: Double = 360.0,
    val referralCommissionBdt: Double = 5.0,
    val referralCommissionPercent: Double = 1.5,
    val referralMinOrderAmountBdt: Double = 50.0,
    val referralMaxCommissionBdt: Double = 500.0,
    val referralEligibleServices: Set<String> = setOf("CHAMET", "TIKTOK", "FREEFIRE", "EPAY", "EXCHANGE", "SOCIAL_BOOST"),
    val referralRulesText: String = "আপনার Referral Code ব্যবহার করে কেউ সার্ভিস কিনলে অর্ডার Completed হওয়ার সাথে সাথে আপনি Admin নির্ধারিত Referral Bonus পাবেন।",
    val referralSignupBonusBdt: Double = 0.0,
    val referralNewUserBonusBdt: Double = 0.0,
    val minWalletAddMoneyBdt: Double = 50.0,
    val referralProportionalScaling: Boolean = true,
    val referralRewardsHistory: List<ReferralRewardRecord> = emptyList(),
    val walletTransactionsHistory: List<WalletTransactionEntry> = emptyList(),
    // AI Auto Support Chat & Admin Handoff Settings
    val isAiAutoChatEnabled: Boolean = true,
    val supportChatHandoffMap: Map<String, String> = emptyMap(),
    val adminCustomAiInstructions: String = ""
)

class AdminSettingsManager(context: Context) {
    private val appContext = context.applicationContext
    private val prefs: SharedPreferences = context.getSharedPreferences("johirul_admin_prefs", Context.MODE_PRIVATE)

    init {
        createOrderNotificationChannel()
    }

    private fun createOrderNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ORDER_NOTIFICATION_CHANNEL_ID,
                "Order Approval & Status Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Live notifications when Admin approves or updates your order"
                enableVibration(true)
            }
            val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun showSystemOrderNotification(notif: OrderNotification) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) return
            }

            val intent = Intent(appContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("open_order_notifications", true)
                putExtra("order_number", notif.orderNumber)
            }
            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getActivity(
                appContext,
                notif.orderNumber.hashCode(),
                intent,
                pendingIntentFlags
            )

            val builder = NotificationCompat.Builder(appContext, ORDER_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(notif.title)
                .setContentText(notif.message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notif.message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            NotificationManagerCompat.from(appContext)
                .notify(notif.orderNumber.hashCode(), builder.build())
        } catch (_: Exception) {
        }
    }

    companion object {
        private const val ORDER_NOTIFICATION_CHANNEL_ID = "chamet_order_status_alerts"
    }

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<AdminConfig> = _config.asStateFlow()

    private val _loggedInUserEmail = MutableStateFlow(prefs.getString("logged_in_user_email", "") ?: "")
    val loggedInUserEmail: StateFlow<String> = _loggedInUserEmail.asStateFlow()

    private val _loggedInUserName = MutableStateFlow(prefs.getString("logged_in_user_name", "") ?: "")
    val loggedInUserName: StateFlow<String> = _loggedInUserName.asStateFlow()

    private val _loggedInUserPhone = MutableStateFlow(prefs.getString("logged_in_user_phone", "") ?: "")
    val loggedInUserPhone: StateFlow<String> = _loggedInUserPhone.asStateFlow()

    private val _supportChatUserName = MutableStateFlow(prefs.getString("support_chat_user_name", "") ?: "")
    val supportChatUserName: StateFlow<String> = _supportChatUserName.asStateFlow()

    private val _supportChatUserPhone = MutableStateFlow(prefs.getString("support_chat_user_phone", "") ?: "")
    val supportChatUserPhone: StateFlow<String> = _supportChatUserPhone.asStateFlow()

    private val _supportChatUserEmail = MutableStateFlow(prefs.getString("support_chat_user_email", "") ?: "")
    val supportChatUserEmail: StateFlow<String> = _supportChatUserEmail.asStateFlow()

    fun getOrCreateSupportDeviceId(): String {
        val existing = prefs.getString("support_chat_device_id", null)
        if (!existing.isNullOrBlank()) return existing
        val generated = "user_${System.currentTimeMillis().toString().takeLast(8)}"
        prefs.edit().putString("support_chat_device_id", generated).apply()
        return generated
    }

    fun saveSupportChatUserInfo(name: String, phone: String, email: String = "") {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanEmail = email.trim().ifBlank { _loggedInUserEmail.value.trim() }
        _supportChatUserName.value = cleanName
        _supportChatUserPhone.value = cleanPhone
        _supportChatUserEmail.value = cleanEmail
        if (cleanEmail.equals(_loggedInUserEmail.value.trim(), ignoreCase = true) && cleanEmail.isNotBlank()) {
            if (cleanName.isNotBlank()) _loggedInUserName.value = cleanName
            if (cleanPhone.isNotBlank()) _loggedInUserPhone.value = cleanPhone
            prefs.edit()
                .putString("logged_in_user_name", _loggedInUserName.value)
                .putString("logged_in_user_phone", _loggedInUserPhone.value)
                .apply()
        }
        if (cleanEmail.isNotBlank()) {
            saveUserAccountProfile(cleanEmail, cleanName, cleanPhone)
        }
        prefs.edit()
            .putString("support_chat_user_name", cleanName)
            .putString("support_chat_user_phone", cleanPhone)
            .putString("support_chat_user_email", cleanEmail)
            .apply()
    }

    fun saveUserAccountProfile(email: String, name: String, phone: String, photoUri: String? = null) {
        val key = email.trim().lowercase()
        if (key.isBlank()) return
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        prefs.edit().apply {
            if (cleanName.isNotBlank()) putString("acct_name_$key", cleanName)
            if (cleanPhone.isNotBlank()) putString("acct_phone_$key", cleanPhone)
            if (photoUri != null) putString("acct_photo_$key", photoUri.trim())
        }.apply()
    }

    fun getUserAccountProfile(email: String): Pair<String, String> {
        val key = email.trim().lowercase()
        if (key.isBlank()) return "" to ""
        val savedName = prefs.getString("acct_name_$key", null)
            ?: prefs.getString("user_profile_name_$key", "") ?: ""
        val savedPhone = prefs.getString("acct_phone_$key", null)
            ?: prefs.getString("user_profile_phone_$key", "") ?: ""
        return savedName to savedPhone
    }

    fun getSavedAccountName(email: String): String = getUserAccountProfile(email).first

    fun getSavedAccountPhone(email: String): String = getUserAccountProfile(email).second

    fun getSavedAccountPhotoUri(email: String): String {
        val key = email.trim().lowercase()
        if (key.isBlank()) return ""
        return prefs.getString("acct_photo_$key", null)
            ?: prefs.getString("user_profile_photo_$key", null)
            ?: _config.value.customerMetadata[key]?.photoUri
            ?: ""
    }

    fun getOrCreateUserReferralCode(email: String): String {
        val key = email.trim().lowercase()
        if (key.isBlank()) return ""
        val existing = prefs.getString("acct_ref_code_$key", null)
            ?: prefs.getString("user_referral_code_$key", null)
        if (!existing.isNullOrBlank()) return existing
        val prefix = key.substringBefore("@").filter { it.isLetterOrDigit() }.uppercase().take(3).padEnd(3, 'X')
        val hashDigits = (kotlin.math.abs(key.hashCode()) % 9000 + 1000).toString()
        val code = "CTB-$prefix$hashDigits"
        prefs.edit().putString("acct_ref_code_$key", code).apply()
        return code
    }

    fun getUserReferredByCode(email: String): String {
        val key = email.trim().lowercase()
        if (key.isBlank()) return ""
        return prefs.getString("acct_referred_by_$key", null)
            ?: prefs.getString("user_referred_by_$key", "") ?: ""
    }

    fun getUserReferralEarnings(email: String): Double {
        val key = email.trim().lowercase()
        if (key.isBlank()) return 0.0
        val stored = prefs.getFloat("acct_ref_earnings_$key", 0.0f).toDouble()
        val myCode = getOrCreateUserReferralCode(key)
        val fromHistory = _config.value.referralRewardsHistory
            .filter {
                it.referrerEmail.equals(key, ignoreCase = true) ||
                    (myCode.isNotBlank() && it.referrerCode.equals(myCode, ignoreCase = true))
            }
            .sumOf { it.commissionBdt }
        val metaVal = _config.value.customerMetadata[key]?.referralEarningsBdt ?: 0.0
        return maxOf(stored, fromHistory, metaVal)
    }

    fun getUserReferralCount(email: String): Int {
        val key = email.trim().lowercase()
        if (key.isBlank()) return 0
        val stored = prefs.getInt("acct_ref_count_$key", 0)
        val myCode = getOrCreateUserReferralCode(key)
        val referredEmails = mutableSetOf<String>()
        if (myCode.isNotBlank()) {
            _config.value.customerMetadata.values.forEach { meta ->
                val mEmail = meta.email.trim().lowercase().ifBlank { meta.accountId.trim().lowercase() }
                val mRefBy = meta.referredByCode.trim().ifBlank {
                    if (mEmail.isNotBlank()) getUserReferredByCode(mEmail) else ""
                }
                if (mEmail.isNotBlank() && mEmail != key && mRefBy.equals(myCode, ignoreCase = true)) {
                    referredEmails.add(mEmail)
                }
            }
            _config.value.referralRewardsHistory.forEach { rew ->
                val bEmail = rew.buyerEmail.trim().lowercase()
                if (bEmail.isNotBlank() && bEmail != key &&
                    (rew.referrerEmail.equals(key, ignoreCase = true) || rew.referrerCode.equals(myCode, ignoreCase = true))
                ) {
                    referredEmails.add(bEmail)
                }
            }
        }
        val metaCount = _config.value.customerMetadata[key]?.referralCount ?: 0
        return maxOf(stored, referredEmails.size, metaCount)
    }

    fun getUserWalletBalance(email: String): Double {
        val key = email.trim().lowercase()
        if (key.isBlank()) return 0.0
        return if (prefs.contains("acct_wallet_bdt_$key")) {
            prefs.getFloat("acct_wallet_bdt_$key", 0.0f).toDouble().coerceAtLeast(0.0)
        } else {
            prefs.getFloat("user_wallet_balance_$key", 0.0f).toDouble().coerceAtLeast(0.0)
        }
    }

    fun setUserWalletBalance(email: String, amountBdt: Double) {
        val key = email.trim().lowercase()
        if (key.isBlank()) return
        val safe = amountBdt.coerceAtLeast(0.0)
        prefs.edit().putFloat("acct_wallet_bdt_$key", safe.toFloat()).apply()
        if (_loggedInUserEmail.value.trim().lowercase() == key) {
            _userWalletBalance.value = safe
        }
    }

    fun addUserWalletBalance(email: String, deltaBdt: Double) {
        val key = email.trim().lowercase()
        if (key.isBlank() || deltaBdt <= 0.0) return
        val current = getUserWalletBalance(key)
        setUserWalletBalance(key, current + deltaBdt)
    }

    fun deductUserWalletBalance(email: String, amountBdt: Double): Boolean {
        val key = email.trim().lowercase()
        if (key.isBlank() || amountBdt <= 0.0) return false
        val current = getUserWalletBalance(key)
        if (current < amountBdt) return false
        setUserWalletBalance(key, current - amountBdt)
        return true
    }

    private val _loggedInUserPhotoUri = MutableStateFlow(
        getSavedAccountPhotoUri(prefs.getString("logged_in_user_email", "") ?: "")
    )
    val loggedInUserPhotoUri: StateFlow<String> = _loggedInUserPhotoUri.asStateFlow()

    private val _loggedInUserReferralCode = MutableStateFlow(
        getOrCreateUserReferralCode(prefs.getString("logged_in_user_email", "") ?: "")
    )
    val loggedInUserReferralCode: StateFlow<String> = _loggedInUserReferralCode.asStateFlow()

    private val _loggedInUserReferredByCode = MutableStateFlow(
        getUserReferredByCode(prefs.getString("logged_in_user_email", "") ?: "")
    )
    val loggedInUserReferredByCode: StateFlow<String> = _loggedInUserReferredByCode.asStateFlow()

    private val _loggedInUserReferralEarnings = MutableStateFlow(
        getUserReferralEarnings(prefs.getString("logged_in_user_email", "") ?: "")
    )
    val loggedInUserReferralEarnings: StateFlow<Double> = _loggedInUserReferralEarnings.asStateFlow()

    private val _loggedInUserReferralCount = MutableStateFlow(
        getUserReferralCount(prefs.getString("logged_in_user_email", "") ?: "")
    )
    val loggedInUserReferralCount: StateFlow<Int> = _loggedInUserReferralCount.asStateFlow()

    private val _latestBannerNotification = MutableStateFlow<OrderNotification?>(null)
    val latestBannerNotification: StateFlow<OrderNotification?> = _latestBannerNotification.asStateFlow()

    private val _userClearedNotifTimestamp = MutableStateFlow(
        prefs.getLong(
            "user_notif_cleared_ts_${(prefs.getString("logged_in_user_email", "") ?: "").trim().lowercase()}",
            0L
        )
    )
    val userClearedNotifTimestamp: StateFlow<Long> = _userClearedNotifTimestamp.asStateFlow()

    private val _userWalletBalance = MutableStateFlow(
        run {
            val initEmail = (prefs.getString("logged_in_user_email", "") ?: "").trim().lowercase()
            if (initEmail.isNotBlank()) getUserWalletBalance(initEmail) else 0.0
        }
    )
    val userWalletBalance: StateFlow<Double> = _userWalletBalance.asStateFlow()

    fun setLoggedInUser(email: String, name: String, phone: String = "", photoUri: String? = null) {
        val cleanEmail = email.trim()
        val nextEmail = cleanEmail.lowercase()
        val (savedNameForEmail, savedPhoneForEmail) = getUserAccountProfile(cleanEmail)
        val savedPhotoForEmail = getSavedAccountPhotoUri(cleanEmail)
        val cleanName = name.trim()
            .ifBlank { savedNameForEmail }
            .ifBlank { cleanEmail.substringBefore("@") }
        val cleanPhone = phone.trim()
            .ifBlank { savedPhoneForEmail }
        val cleanPhoto = (photoUri?.trim() ?: "").ifBlank { savedPhotoForEmail }

        val previousEmail = _loggedInUserEmail.value.trim().lowercase()
        if (previousEmail != nextEmail) {
            _latestBannerNotification.value = null
        }

        _loggedInUserEmail.value = cleanEmail
        _loggedInUserName.value = cleanName
        _loggedInUserPhone.value = cleanPhone
        _loggedInUserPhotoUri.value = cleanPhoto
        _loggedInUserReferralCode.value = getOrCreateUserReferralCode(cleanEmail)
        _loggedInUserReferredByCode.value = getUserReferredByCode(cleanEmail)
        _loggedInUserReferralEarnings.value = getUserReferralEarnings(cleanEmail)
        _loggedInUserReferralCount.value = getUserReferralCount(cleanEmail)
        _userWalletBalance.value = getUserWalletBalance(cleanEmail)
        _supportChatUserEmail.value = cleanEmail
        _supportChatUserName.value = cleanName
        _supportChatUserPhone.value = cleanPhone
        _userClearedNotifTimestamp.value = prefs.getLong("user_notif_cleared_ts_$nextEmail", 0L)

        prefs.edit()
            .putString("logged_in_user_email", cleanEmail)
            .putString("logged_in_user_name", cleanName)
            .putString("logged_in_user_phone", cleanPhone)
            .putString("support_chat_user_email", cleanEmail)
            .putString("support_chat_user_name", cleanName)
            .putString("support_chat_user_phone", cleanPhone)
            .apply()

        saveUserAccountProfile(cleanEmail, cleanName, cleanPhone, cleanPhoto)
    }

    fun syncCloudUserWalletAndReferral(
        email: String,
        cloudWalletBalance: Double?,
        cloudReferralCode: String?,
        cloudReferredBy: String?,
        cloudReferralEarnings: Double?,
        cloudReferralCount: Int?
    ) {
        val key = email.trim().lowercase()
        if (key.isBlank()) return
        prefs.edit().apply {
            if (cloudWalletBalance != null) putFloat("acct_wallet_bdt_$key", cloudWalletBalance.coerceAtLeast(0.0).toFloat())
            if (!cloudReferralCode.isNullOrBlank()) putString("acct_ref_code_$key", cloudReferralCode.trim().uppercase())
            if (!cloudReferredBy.isNullOrBlank()) putString("acct_referred_by_$key", cloudReferredBy.trim().uppercase())
            if (cloudReferralEarnings != null) putFloat("acct_ref_earnings_$key", cloudReferralEarnings.coerceAtLeast(0.0).toFloat())
            if (cloudReferralCount != null) putInt("acct_ref_count_$key", cloudReferralCount.coerceAtLeast(0))
        }.apply()
        if (_loggedInUserEmail.value.trim().lowercase() == key) {
            _userWalletBalance.value = getUserWalletBalance(key)
            _loggedInUserReferralCode.value = getOrCreateUserReferralCode(key)
            _loggedInUserReferredByCode.value = getUserReferredByCode(key)
            _loggedInUserReferralEarnings.value = getUserReferralEarnings(key)
            _loggedInUserReferralCount.value = getUserReferralCount(key)
        }
    }

    fun clearLoggedInUser() {
        _loggedInUserEmail.value = ""
        _loggedInUserName.value = ""
        _loggedInUserPhone.value = ""
        _loggedInUserPhotoUri.value = ""
        _loggedInUserReferralCode.value = ""
        _loggedInUserReferredByCode.value = ""
        _loggedInUserReferralEarnings.value = 0.0
        _loggedInUserReferralCount.value = 0
        _userWalletBalance.value = 0.0
        _supportChatUserEmail.value = ""
        _supportChatUserName.value = ""
        _supportChatUserPhone.value = ""
        _userClearedNotifTimestamp.value = 0L
        _latestBannerNotification.value = null
        prefs.edit()
            .remove("logged_in_user_email")
            .remove("logged_in_user_name")
            .remove("logged_in_user_phone")
            .remove("support_chat_user_email")
            .remove("support_chat_user_name")
            .remove("support_chat_user_phone")
            .apply()
    }

    fun dismissBannerNotification() {
        _latestBannerNotification.value = null
    }

    fun updateUserWalletBalance(newBalance: Double) {
        val clamped = if (newBalance < 0.0) 0.0 else newBalance
        val email = _loggedInUserEmail.value.trim().lowercase()
        if (email.isNotBlank()) {
            setUserWalletBalance(email, clamped)
        } else {
            _userWalletBalance.value = clamped
            prefs.edit().putFloat("user_wallet_balance", clamped.toFloat()).apply()
        }
    }

    fun addFundsToWallet(amount: Double) {
        updateUserWalletBalance(_userWalletBalance.value + amount)
    }

    fun deductFundsFromWallet(amount: Double): Boolean {
        if (_userWalletBalance.value >= amount) {
            updateUserWalletBalance(_userWalletBalance.value - amount)
            return true
        }
        return false
    }

    private fun loadConfig(): AdminConfig {
        val pricesJson = prefs.getString("custom_prices_json", "{}") ?: "{}"
        val customPricesMap = mutableMapOf<String, Double>()
        try {
            val jsonObject = JSONObject(pricesJson)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                customPricesMap[key] = jsonObject.optDouble(key, 0.0)
            }
        } catch (_: Exception) {
        }

        val customPkgsJson = prefs.getString("custom_packages_json", "[]") ?: "[]"
        val customPkgsList = mutableListOf<TopupPackage>()
        try {
            val arr = JSONArray(customPkgsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val catName = obj.optString("category", "TIKTOK")
                val cat = ServiceCategory.entries.find { it.name == catName } ?: ServiceCategory.TIKTOK
                customPkgsList.add(
                    TopupPackage(
                        id = obj.optString("id", "custom_$i"),
                        category = cat,
                        name = obj.optString("name", ""),
                        subtitle = obj.optString("subtitle", "Custom Pack"),
                        amount = obj.optString("amount", ""),
                        priceBdt = obj.optDouble("priceBdt", 0.0),
                        badge = obj.optString("badge", "").takeIf { it.isNotBlank() },
                        isPopular = obj.optBoolean("isPopular", false),
                        bonus = obj.optString("bonus", "").takeIf { it.isNotBlank() }
                    )
                )
            }
        } catch (_: Exception) {
        }

        val deletedIdsSet = prefs.getStringSet("deleted_package_ids", emptySet()) ?: emptySet()

        val customerMetaJson = prefs.getString("customer_meta_json", "{}") ?: "{}"
        val customerMetaMap = mutableMapOf<String, CustomerMeta>()
        try {
            val jsonObject = JSONObject(customerMetaJson)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val obj = jsonObject.getJSONObject(key)
                customerMetaMap[key] = CustomerMeta(
                    accountId = key,
                    displayName = obj.optString("displayName", ""),
                    email = obj.optString("email", if (key.contains("@")) key.trim().lowercase() else ""),
                    phone = obj.optString("phone", ""),
                    photoUri = obj.optString("photoUri", ""),
                    walletBalanceBdt = obj.optDouble("walletBalanceBdt", 0.0),
                    referralCode = obj.optString("referralCode", ""),
                    referredByCode = obj.optString("referredByCode", ""),
                    referralEarningsBdt = obj.optDouble("referralEarningsBdt", 0.0),
                    referralCount = obj.optInt("referralCount", 0),
                    isVip = obj.optBoolean("isVip", false),
                    isBlocked = obj.optBoolean("isBlocked", false),
                    adminNote = obj.optString("adminNote", "")
                )
            }
        } catch (_: Exception) {
        }

        val referralRewardsJson = prefs.getString("referral_rewards_json", "[]") ?: "[]"
        val referralRewardsList = mutableListOf<ReferralRewardRecord>()
        try {
            val arr = JSONArray(referralRewardsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                referralRewardsList.add(
                    ReferralRewardRecord(
                        id = obj.optString("id", "ref_$i"),
                        referrerEmail = obj.optString("referrerEmail", ""),
                        referrerCode = obj.optString("referrerCode", ""),
                        buyerEmail = obj.optString("buyerEmail", ""),
                        buyerName = obj.optString("buyerName", ""),
                        orderNumber = obj.optString("orderNumber", ""),
                        itemName = obj.optString("itemName", ""),
                        orderPriceBdt = obj.optDouble("orderPriceBdt", 0.0),
                        commissionBdt = obj.optDouble("commissionBdt", 0.0),
                        status = obj.optString("status", "Completed"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
        }

        val walletTxJson = prefs.getString("wallet_transactions_json", "[]") ?: "[]"
        val walletTxList = mutableListOf<WalletTransactionEntry>()
        try {
            val arr = JSONArray(walletTxJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                walletTxList.add(
                    WalletTransactionEntry(
                        id = obj.optString("id", "wtx_$i"),
                        userEmail = obj.optString("userEmail", "").trim().lowercase(),
                        type = obj.optString("type", "ADD_MONEY"),
                        title = obj.optString("title", ""),
                        amountBdt = obj.optDouble("amountBdt", 0.0),
                        isCredit = obj.optBoolean("isCredit", true),
                        status = obj.optString("status", "Completed"),
                        reasonOrNote = obj.optString("reasonOrNote", ""),
                        referenceId = obj.optString("referenceId", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
        }

        val bannersJson = prefs.getString("promo_banners_json", null)
        val bannersList = if (bannersJson.isNullOrBlank()) {
            defaultBanners()
        } else {
            try {
                val jsonArray = JSONArray(bannersJson)
                val list = mutableListOf<PromoBanner>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        PromoBanner(
                            id = obj.optString("id", "b_$i"),
                            title = obj.optString("title", ""),
                            subtitle = obj.optString("subtitle", ""),
                            imageUrl = obj.optString("imageUrl", ""),
                            badge = obj.optString("badge", "Special Offer"),
                            actionUrl = obj.optString("actionUrl", ""),
                            serviceTarget = obj.optString("serviceTarget", "TIKTOK"),
                            gradientStartHex = obj.optString("gradientStartHex", "#FF1E0B36"),
                            gradientEndHex = obj.optString("gradientEndHex", "#FF6B0B99"),
                            isEnabled = obj.optBoolean("isEnabled", true)
                        )
                    )
                }
                if (list.isEmpty()) defaultBanners() else list
            } catch (_: Exception) {
                defaultBanners()
            }
        }

        val sbPlatformsJson = prefs.getString("social_boost_platforms_json", null)
        val rawSbPlatformsList = if (sbPlatformsJson.isNullOrBlank()) {
            defaultSocialBoostPlatforms()
        } else {
            try {
                val arr = JSONArray(sbPlatformsJson)
                val list = mutableListOf<SocialBoostPlatform>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    list.add(
                        SocialBoostPlatform(
                            id = obj.optString("id", "plat_$i"),
                            name = obj.optString("name", ""),
                            iconKey = obj.optString("iconKey", "GENERIC"),
                            description = obj.optString("description", ""),
                            displayOrder = obj.optInt("displayOrder", i + 1),
                            isEnabled = obj.optBoolean("isEnabled", true)
                        )
                    )
                }
                if (list.isEmpty()) defaultSocialBoostPlatforms() else list
            } catch (_: Exception) {
                defaultSocialBoostPlatforms()
            }
        }

        val sbCategoriesJson = prefs.getString("social_boost_categories_json", null)
        val rawSbCategoriesList = if (sbCategoriesJson.isNullOrBlank()) {
            defaultSocialBoostCategories()
        } else {
            try {
                val arr = JSONArray(sbCategoriesJson)
                val list = mutableListOf<SocialBoostCategory>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    list.add(
                        SocialBoostCategory(
                            id = obj.optString("id", "cat_$i"),
                            platformId = obj.optString("platformId", ""),
                            name = obj.optString("name", ""),
                            description = obj.optString("description", ""),
                            displayOrder = obj.optInt("displayOrder", i + 1),
                            isEnabled = obj.optBoolean("isEnabled", true)
                        )
                    )
                }
                if (list.isEmpty()) defaultSocialBoostCategories() else list
            } catch (_: Exception) {
                defaultSocialBoostCategories()
            }
        }

        val sbServicesJson = prefs.getString("social_boost_services_json", null)
        val rawSbServicesList = if (sbServicesJson.isNullOrBlank()) {
            defaultSocialBoostServices()
        } else {
            try {
                val arr = JSONArray(sbServicesJson)
                val list = mutableListOf<SocialBoostService>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val resolvedId = obj.optString("id", "").ifBlank { obj.optString("serviceId", "srv_$i") }
                    val resolvedName = obj.optString("name", "").ifBlank { obj.optString("serviceName", "") }
                    val resolvedPrice = if (obj.has("pricePerThousandBdt")) {
                        obj.optDouble("pricePerThousandBdt", 100.0)
                    } else {
                        obj.optDouble("pricePer1000", 100.0)
                    }
                    list.add(
                        SocialBoostService(
                            id = resolvedId,
                            platformId = obj.optString("platformId", ""),
                            categoryId = obj.optString("categoryId", ""),
                            name = resolvedName,
                            pricePerThousandBdt = resolvedPrice,
                            minQuantity = obj.optInt("minQuantity", 100),
                            maxQuantity = obj.optInt("maxQuantity", 100000),
                            averageTime = obj.optString("averageTime", "15 - 45 Minutes"),
                            description = obj.optString("description", ""),
                            requiresLink = obj.optBoolean("requiresLink", true),
                            displayOrder = obj.optInt("displayOrder", i + 1),
                            isEnabled = obj.optBoolean("isEnabled", true),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
                if (list.isEmpty()) defaultSocialBoostServices() else list
            } catch (_: Exception) {
                defaultSocialBoostServices()
            }
        }

        // Non-destructively merge any missing default platforms/categories/services once so existing data is preserved
        val isV2Merged = prefs.getBoolean("social_boost_v2_platforms_merged", false)
        val sbPlatformsList = if (!isV2Merged) {
            val merged = rawSbPlatformsList.toMutableList()
            defaultSocialBoostPlatforms().forEach { defPlat ->
                if (merged.none { it.id == defPlat.id || it.name.equals(defPlat.name, ignoreCase = true) }) {
                    merged.add(defPlat.copy(displayOrder = merged.size + 1))
                }
            }
            merged
        } else {
            rawSbPlatformsList
        }

        val sbCategoriesList = if (!isV2Merged) {
            val merged = rawSbCategoriesList.toMutableList()
            defaultSocialBoostCategories().forEach { defCat ->
                if (merged.none { it.id == defCat.id }) {
                    merged.add(defCat)
                }
            }
            merged
        } else {
            rawSbCategoriesList
        }

        val sbServicesList = if (!isV2Merged) {
            val merged = rawSbServicesList.toMutableList()
            defaultSocialBoostServices().forEach { defSrv ->
                if (merged.none { it.id == defSrv.id }) {
                    merged.add(defSrv)
                }
            }
            prefs.edit().putBoolean("social_boost_v2_platforms_merged", true).apply()
            merged
        } else {
            rawSbServicesList
        }

        val handoffJson = prefs.getString("support_chat_handoff_json", "{}") ?: "{}"
        val handoffMap = mutableMapOf<String, String>()
        try {
            val hObj = JSONObject(handoffJson)
            val keys = hObj.keys()
            while (keys.hasNext()) {
                val k = keys.next().trim().lowercase()
                val v = hObj.optString(k, "AI_AUTO").trim().uppercase()
                if (k.isNotBlank()) {
                    handoffMap[k] = v
                }
            }
        } catch (_: Exception) {
        }

        return AdminConfig(
            adminEmail = prefs.getString("admin_email", "johirul958423@gmail.com") ?: "johirul958423@gmail.com",
            epayEmail = prefs.getString("epay_email", "johirulagency01833@gmail.com") ?: "johirulagency01833@gmail.com",
            firebaseUserEmail = prefs.getString("firebase_user_email", "") ?: "",
            firebaseUserId = prefs.getString("firebase_user_id", "") ?: "",
            bkashNumber = prefs.getString("bkash_number", "01733256802") ?: "01733256802",
            bkashType = prefs.getString("bkash_type", "Personal (Send Money / ক্যাশ ইন)") ?: "Personal (Send Money / ক্যাশ ইন)",
            bkashInstruction = prefs.getString("bkash_instruction", "বিকাশ অ্যাপ থেকে Send Money করুন অথবা ক্যাশ ইন করুন। রেফারেন্সে আপনার নাম বা UID লিখুন।") ?: "বিকাশ অ্যাপ থেকে Send Money করুন অথবা ক্যাশ ইন করুন। রেফারেন্সে আপনার নাম বা UID লিখুন।",
            nagadNumber = prefs.getString("nagad_number", "01874-954130") ?: "01874-954130",
            nagadType = prefs.getString("nagad_type", "Personal (Send Money / ক্যাশ আউট)") ?: "Personal (Send Money / ক্যাশ আউট)",
            nagadInstruction = prefs.getString("nagad_instruction", "নগদ অ্যাপ থেকে Send Money করুন। পেমেন্টের পর TrxID বা স্ক্রিনশট সংরক্ষণ করুন।") ?: "নগদ অ্যাপ থেকে Send Money করুন। পেমেন্টের পর TrxID বা স্ক্রিনশট সংরক্ষণ করুন।",
            rocketNumber = prefs.getString("rocket_number", "01733256802-8") ?: "01733256802-8",
            isRocketEnabled = prefs.getBoolean("rocket_enabled", false),
            whatsappNumber = prefs.getString("whatsapp_number", "8801880467335") ?: "8801880467335",
            whatsappDisplay = prefs.getString("whatsapp_display", "+880 1880-467335") ?: "+880 1880-467335",
            telegramUsername = prefs.getString("telegram_username", "johirul0188") ?: "johirul0188",
            telegramUrl = prefs.getString("telegram_url", "https://t.me/johirul0188") ?: "https://t.me/johirul0188",
            noticeTitle = prefs.getString("notice_title", "IMPORTANT RULES (জরুরি নিয়মাবলী)") ?: "IMPORTANT RULES (জরুরি নিয়মাবলী)",
            noticeRule1 = prefs.getString("notice_rule1", "Bangladesh account holders can send BDT ➔ INR only.") ?: "Bangladesh account holders can send BDT ➔ INR only.",
            noticeRule2 = prefs.getString("notice_rule2", "EPAY Dollar Exchange করতে প্রথমে আপনার Dollar Amount জানাতে হবে।") ?: "EPAY Dollar Exchange করতে প্রথমে আপনার Dollar Amount জানাতে হবে।",
            noticeRule3 = prefs.getString("notice_rule3", "bKash / Nagad-এ টাকা নিতে পারবেন আমাদের বর্তমান রেট অনুযায়ী।") ?: "bKash / Nagad-এ টাকা নিতে পারবেন আমাদের বর্তমান রেট অনুযায়ী।",
            noticeWarning = prefs.getString("notice_warning", "কোনো ভুল Payment করার আগে অবশ্যই তথ্য যাচাই করুন।") ?: "কোনো ভুল Payment করার আগে অবশ্যই তথ্য যাচাই করুন।",
            noticeAlert = prefs.getString("notice_alert", "কোনো অবস্থাতেই আপনার bKash/Nagad PIN, OTP, Verification Code বা Password কাউকে পাঠাবেন না।") ?: "কোনো অবস্থাতেই আপনার bKash/Nagad PIN, OTP, Verification Code বা Password কাউকে পাঠাবেন না।",
            noticeShowOnLaunch = prefs.getBoolean("notice_show_launch", true),
            epayBaseRate = prefs.getFloat("epay_base_rate", 121.0f).toDouble(),
            epayRate970Fixed = prefs.getFloat("epay_rate_970_fixed", 1170.0f).toDouble(),
            epayRate10Fixed = prefs.getFloat("epay_rate_10_fixed", 1200.0f).toDouble(),
            epayRate30Bulk = prefs.getFloat("epay_rate_30_bulk", 122.0f).toDouble(),
            epayRate500Wholesale = prefs.getFloat("epay_rate_500_wholesale", 123.0f).toDouble(),
            inrBaseRate = prefs.getFloat("inr_base_rate", 1.45f).toDouble(),
            tiktokRatePerCoin = prefs.getFloat("tiktok_rate_coin", 1.80f).toDouble(),
            chametRatePerDiamond = prefs.getFloat("chamet_rate_diamond", 0.023f).toDouble(),
            customPrices = customPricesMap,
            customPackages = customPkgsList,
            deletedPackageIds = deletedIdsSet,
            customerMetadata = customerMetaMap,
            storeStatusOverride = prefs.getString("store_override", "AUTO") ?: "AUTO",
            isAutoTopupEnabled = prefs.getBoolean("is_auto_topup_enabled", true),
            autoInstantWalletEnabled = prefs.getBoolean("auto_instant_wallet_enabled", false),
            tiktokApiUrl = prefs.getString("tiktok_api_url", "") ?: "",
            tiktokApiKey = prefs.getString("tiktok_api_key", "") ?: "",
            chametApiUrl = prefs.getString("chamet_api_url", "") ?: "",
            chametApiKey = prefs.getString("chamet_api_key", "") ?: "",
            promoBanners = bannersList,
            socialBoostPlatforms = sbPlatformsList,
            socialBoostCategories = sbCategoriesList,
            socialBoostServices = sbServicesList,
            isReferralEnabled = prefs.getBoolean("is_referral_enabled", true),
            referralCommissionMode = prefs.getString("referral_commission_mode", "FIXED") ?: "FIXED",
            referralBaseOrderAmountBdt = prefs.getFloat("referral_base_order_amount_bdt", 360.0f).toDouble(),
            referralCommissionBdt = prefs.getFloat("referral_commission_bdt", 5.0f).toDouble(),
            referralCommissionPercent = prefs.getFloat("referral_commission_percent", 1.5f).toDouble(),
            referralMinOrderAmountBdt = prefs.getFloat("referral_min_order_amount_bdt", 50.0f).toDouble(),
            referralMaxCommissionBdt = prefs.getFloat("referral_max_commission_bdt", 500.0f).toDouble(),
            referralEligibleServices = prefs.getStringSet(
                "referral_eligible_services",
                setOf("CHAMET", "TIKTOK", "FREEFIRE", "EPAY", "EXCHANGE", "SOCIAL_BOOST")
            ) ?: setOf("CHAMET", "TIKTOK", "FREEFIRE", "EPAY", "EXCHANGE", "SOCIAL_BOOST"),
            referralRulesText = prefs.getString(
                "referral_rules_text",
                "আপনার Referral Code ব্যবহার করে কেউ সার্ভিস কিনলে অর্ডার Completed হওয়ার সাথে সাথে আপনি Admin নির্ধারিত Referral Bonus পাবেন।"
            ) ?: "আপনার Referral Code ব্যবহার করে কেউ সার্ভিস কিনলে অর্ডার Completed হওয়ার সাথে সাথে আপনি Admin নির্ধারিত Referral Bonus পাবেন।",
            referralSignupBonusBdt = prefs.getFloat("referral_signup_bonus_bdt", 0.0f).toDouble(),
            referralNewUserBonusBdt = prefs.getFloat("referral_signup_bonus_bdt", 0.0f).toDouble(),
            minWalletAddMoneyBdt = prefs.getFloat("min_wallet_add_money_bdt", 50.0f).toDouble(),
            referralProportionalScaling = prefs.getBoolean("referral_proportional_scaling", true),
            referralRewardsHistory = referralRewardsList,
            walletTransactionsHistory = walletTxList,
            isAiAutoChatEnabled = prefs.getBoolean("is_ai_auto_chat_enabled", true),
            supportChatHandoffMap = handoffMap,
            adminCustomAiInstructions = prefs.getString("admin_custom_ai_instructions", "") ?: ""
        )
    }

    fun defaultSocialBoostPlatforms(): List<SocialBoostPlatform> {
        return listOf(
            SocialBoostPlatform(
                id = "plat_facebook",
                name = "Facebook",
                iconKey = "FACEBOOK",
                description = "Page Likes, Profile Followers, Post Reactions & Video Views",
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_instagram",
                name = "Instagram",
                iconKey = "INSTAGRAM",
                description = "High Quality Followers, Post Likes & Reels Views",
                displayOrder = 2,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_youtube",
                name = "YouTube",
                iconKey = "YOUTUBE",
                description = "Channel Subscribers, Watch Time Hours & Video Views",
                displayOrder = 3,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_x",
                name = "X",
                iconKey = "X",
                description = "X / Twitter Followers, Retweets, Likes & Views",
                displayOrder = 4,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_spotify",
                name = "Spotify",
                iconKey = "SPOTIFY",
                description = "Spotify Track Plays, Monthly Listeners & Followers",
                displayOrder = 5,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_tiktok",
                name = "TikTok",
                iconKey = "TIKTOK",
                description = "TikTok Followers, Video Hearts, Shares & Viral Views",
                displayOrder = 6,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_telegram",
                name = "Telegram",
                iconKey = "TELEGRAM",
                description = "Telegram Channel Members, Post Views & Reactions",
                displayOrder = 7,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_linkedin",
                name = "LinkedIn",
                iconKey = "LINKEDIN",
                description = "LinkedIn Company & Profile Followers, Post Likes",
                displayOrder = 8,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_video",
                name = "Video",
                iconKey = "VIDEO",
                description = "Short Video & Livestream Views Boost",
                displayOrder = 9,
                isEnabled = true
            ),
            SocialBoostPlatform(
                id = "plat_website",
                name = "Website",
                iconKey = "WEBSITE",
                description = "Website Organic Visitors & SEO Traffic",
                displayOrder = 10,
                isEnabled = true
            )
        )
    }

    fun defaultSocialBoostCategories(): List<SocialBoostCategory> {
        return listOf(
            // Facebook Categories
            SocialBoostCategory(
                id = "cat_fb_followers",
                platformId = "plat_facebook",
                name = "Facebook Followers | High-Quality Service",
                description = "Organic & Non-Drop Page/Profile Followers",
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostCategory(
                id = "cat_fb_bd_followers",
                platformId = "plat_facebook",
                name = "Facebook Followers | Bangladesh Services",
                description = "100% Targeted Bangladesh Profile & Page Followers",
                displayOrder = 2,
                isEnabled = true
            ),
            SocialBoostCategory(
                id = "cat_fb_group",
                platformId = "plat_facebook",
                name = "Facebook Group Members",
                description = "Active Public Group Members",
                displayOrder = 3,
                isEnabled = true
            ),
            SocialBoostCategory(
                id = "cat_fb_reactions",
                platformId = "plat_facebook",
                name = "Facebook Post Reactions",
                description = "Like, Love, Care & Wow Post Reactions",
                displayOrder = 4,
                isEnabled = true
            ),
            SocialBoostCategory(
                id = "cat_fb_views",
                platformId = "plat_facebook",
                name = "Facebook Video Views",
                description = "Monetizable Reels & Video Views",
                displayOrder = 5,
                isEnabled = true
            ),
            SocialBoostCategory(
                id = "cat_fb_comments",
                platformId = "plat_facebook",
                name = "Facebook Comments",
                description = "Relevant & Custom Post Comments",
                displayOrder = 6,
                isEnabled = true
            ),
            // Instagram Categories
            SocialBoostCategory(
                id = "cat_ig_followers",
                platformId = "plat_instagram",
                name = "Instagram Followers",
                description = "Fast Refill Guaranteed Followers",
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostCategory(
                id = "cat_ig_likes_views",
                platformId = "plat_instagram",
                name = "Instagram Likes",
                description = "Instant Likes & Viral Reels Reach",
                displayOrder = 2,
                isEnabled = true
            ),
            // YouTube Categories
            SocialBoostCategory(
                id = "cat_yt_subs",
                platformId = "plat_youtube",
                name = "YouTube Subscribers",
                description = "Non-Drop Real Channel Subscribers",
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostCategory(
                id = "cat_yt_views",
                platformId = "plat_youtube",
                name = "YouTube Views",
                description = "High Retention Views & Monetization Watch Hours",
                displayOrder = 2,
                isEnabled = true
            ),
            // X / Twitter Categories
            SocialBoostCategory(
                id = "cat_x_followers",
                platformId = "plat_x",
                name = "X / Twitter Followers & Likes",
                description = "High Quality X Profile Followers & Post Engagement",
                displayOrder = 1,
                isEnabled = true
            ),
            // Spotify Categories
            SocialBoostCategory(
                id = "cat_sp_plays",
                platformId = "plat_spotify",
                name = "Spotify Plays & Followers",
                description = "Royalty Eligible Track Streams & Artist Followers",
                displayOrder = 1,
                isEnabled = true
            ),
            // TikTok Categories
            SocialBoostCategory(
                id = "cat_tt_followers",
                platformId = "plat_tiktok",
                name = "TikTok Followers",
                description = "Real Looking Active TikTok Followers",
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostCategory(
                id = "cat_tt_likes_views",
                platformId = "plat_tiktok",
                name = "TikTok Views",
                description = "Instant ForYou Page Views & Hearts",
                displayOrder = 2,
                isEnabled = true
            ),
            // Telegram Categories
            SocialBoostCategory(
                id = "cat_tg_members",
                platformId = "plat_telegram",
                name = "Telegram Channel Members & Views",
                description = "Non-Drop Channel Subscribers & Post Views",
                displayOrder = 1,
                isEnabled = true
            ),
            // LinkedIn Categories
            SocialBoostCategory(
                id = "cat_li_followers",
                platformId = "plat_linkedin",
                name = "LinkedIn Followers & Connections",
                description = "Professional Profile & Company Page Followers",
                displayOrder = 1,
                isEnabled = true
            ),
            // Video Categories
            SocialBoostCategory(
                id = "cat_vid_views",
                platformId = "plat_video",
                name = "Viral Short Video Views",
                description = "High Speed Multi-Platform Video Views",
                displayOrder = 1,
                isEnabled = true
            ),
            // Website Categories
            SocialBoostCategory(
                id = "cat_web_traffic",
                platformId = "plat_website",
                name = "Website Organic Traffic",
                description = "Real Direct & Social Referral Visitors",
                displayOrder = 1,
                isEnabled = true
            )
        )
    }

    fun defaultSocialBoostServices(): List<SocialBoostService> {
        return listOf(
            // Facebook Services
            SocialBoostService(
                id = "srv_fb_page_follow_848",
                platformId = "plat_facebook",
                categoryId = "cat_fb_followers",
                name = "Facebook Page Likes + Followers [30 Days Refill]",
                pricePerThousandBdt = 160.0,
                minQuantity = 100,
                maxQuantity = 100000,
                averageTime = "15 - 45 Minutes",
                description = "প্রোফাইল অথবা পেজের পাবলিক লিঙ্ক দিন। ৩০ দিনের গ্যারান্টেড রিফিল সুবিধা ও দ্রুত ডেলিভারি।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_fb_profile_follow_925",
                platformId = "plat_facebook",
                categoryId = "cat_fb_followers",
                name = "Facebook Profile Followers [Non-Drop • High Speed]",
                pricePerThousandBdt = 140.0,
                minQuantity = 100,
                maxQuantity = 50000,
                averageTime = "10 - 30 Minutes",
                description = "প্রোফাইলের ফলো বাটন অবশ্যই Public থাকতে হবে। হাই-স্পিড অটো স্টার্ট।",
                requiresLink = true,
                displayOrder = 2,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_fb_bd_follow_860",
                platformId = "plat_facebook",
                categoryId = "cat_fb_bd_followers",
                name = "Facebook Bangladesh Followers [100% Real BD Accounts]",
                pricePerThousandBdt = 240.0,
                minQuantity = 100,
                maxQuantity = 50000,
                averageTime = "20 - 60 Minutes",
                description = "বাংলাদেশি প্রোফাইল ও পেজের জন্য ১০০% টার্গেটেড ফলোয়ার।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_fb_group_872",
                platformId = "plat_facebook",
                categoryId = "cat_fb_group",
                name = "Facebook Public Group Members [30 Days Refill]",
                pricePerThousandBdt = 190.0,
                minQuantity = 500,
                maxQuantity = 100000,
                averageTime = "1 - 3 Hours",
                description = "পাবলিক ফেসবুক গ্রুপের লিঙ্ক দিন। অটোমেটিক মেম্বার যুক্ত হবে।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_fb_post_react_881",
                platformId = "plat_facebook",
                categoryId = "cat_fb_reactions",
                name = "Facebook Post Like / Love / Care Reactions [Instant]",
                pricePerThousandBdt = 90.0,
                minQuantity = 100,
                maxQuantity = 50000,
                averageTime = "5 - 20 Minutes",
                description = "পাবলিক পোস্টের লিঙ্ক দিন। দ্রুততম সময়ে পোস্টে রিয়েকশন যুক্ত হবে।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_fb_reels_views_890",
                platformId = "plat_facebook",
                categoryId = "cat_fb_views",
                name = "Facebook Reels & Video Views [Monetization Safe]",
                pricePerThousandBdt = 45.0,
                minQuantity = 500,
                maxQuantity = 500000,
                averageTime = "10 - 30 Minutes",
                description = "ফেসবুক রিলস অথবা ভিডিওর লিঙ্ক দিন। মনিটাইজেশন সেফ অর্গানিক ভিউ।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_fb_comments_895",
                platformId = "plat_facebook",
                categoryId = "cat_fb_comments",
                name = "Facebook Post Relevant Comments [Real Looking]",
                pricePerThousandBdt = 450.0,
                minQuantity = 20,
                maxQuantity = 5000,
                averageTime = "15 - 45 Minutes",
                description = "পাবলিক পোস্টের লিঙ্ক দিন এবং কমেন্ট অপশন Everyone করে রাখুন।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            // Instagram Services
            SocialBoostService(
                id = "srv_ig_followers_901",
                platformId = "plat_instagram",
                categoryId = "cat_ig_followers",
                name = "Instagram Followers [365 Days Refill • HQ]",
                pricePerThousandBdt = 180.0,
                minQuantity = 100,
                maxQuantity = 100000,
                averageTime = "10 - 30 Minutes",
                description = "ইনস্টাগ্রাম একাউন্ট অবশ্যই Public থাকতে হবে। ৩৬৫ দিন রিফিল গ্যারান্টি।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_ig_likes_905",
                platformId = "plat_instagram",
                categoryId = "cat_ig_likes_views",
                name = "Instagram Post/Reels Likes [Instant Start]",
                pricePerThousandBdt = 60.0,
                minQuantity = 100,
                maxQuantity = 100000,
                averageTime = "5 - 15 Minutes",
                description = "পোস্ট অথবা রিলসের পাবলিক লিঙ্ক দিন। ইনস্ট্যান্ট ডেলিভারি।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_ig_reels_views_908",
                platformId = "plat_instagram",
                categoryId = "cat_ig_likes_views",
                name = "Instagram Reels Viral Views [Super Fast]",
                pricePerThousandBdt = 30.0,
                minQuantity = 500,
                maxQuantity = 1000000,
                averageTime = "5 - 15 Minutes",
                description = "ইনস্টাগ্রাম রিলস লিঙ্ক দিন। সুপার ফাস্ট ভাইরাল ভিউ।",
                requiresLink = true,
                displayOrder = 2,
                isEnabled = true
            ),
            // YouTube Services
            SocialBoostService(
                id = "srv_yt_subs_912",
                platformId = "plat_youtube",
                categoryId = "cat_yt_subs",
                name = "YouTube Channel Subscribers [Non-Drop • Lifetime Guarantee]",
                pricePerThousandBdt = 850.0,
                minQuantity = 50,
                maxQuantity = 20000,
                averageTime = "1 - 4 Hours",
                description = "চ্যানেলের লিঙ্ক দিন এবং সাবস্ক্রাইবার কাউন্ট Public রাখুন। নন-ড্রপ অর্গানিক সাবস্ক্রাইবার।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_yt_views_915",
                platformId = "plat_youtube",
                categoryId = "cat_yt_views",
                name = "YouTube High Retention Video Views [Monetizable]",
                pricePerThousandBdt = 190.0,
                minQuantity = 500,
                maxQuantity = 500000,
                averageTime = "30 - 60 Minutes",
                description = "ইউটিউব ভিডিও লিঙ্ক দিন। হাই রিটেনশন ও ১০০% চ্যানেল সেফ ভিউ।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            // X / Twitter Services
            SocialBoostService(
                id = "srv_x_followers_930",
                platformId = "plat_x",
                categoryId = "cat_x_followers",
                name = "X (Twitter) Profile Followers [30 Days Refill]",
                pricePerThousandBdt = 260.0,
                minQuantity = 100,
                maxQuantity = 50000,
                averageTime = "15 - 45 Minutes",
                description = "আপনার X / Twitter প্রোফাইলের পাবলিক লিঙ্ক দিন।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            // Spotify Services
            SocialBoostService(
                id = "srv_sp_plays_940",
                platformId = "plat_spotify",
                categoryId = "cat_sp_plays",
                name = "Spotify Track Plays / Streams [Fast & Safe]",
                pricePerThousandBdt = 150.0,
                minQuantity = 500,
                maxQuantity = 500000,
                averageTime = "30 - 90 Minutes",
                description = "Spotify Track অথবা Album লিঙ্ক পেস্ট করুন।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            // TikTok Services
            SocialBoostService(
                id = "srv_tt_followers_950",
                platformId = "plat_tiktok",
                categoryId = "cat_tt_followers",
                name = "TikTok Real Active Followers [30 Days Refill]",
                pricePerThousandBdt = 220.0,
                minQuantity = 100,
                maxQuantity = 100000,
                averageTime = "15 - 40 Minutes",
                description = "টিকটক প্রোফাইল লিঙ্ক বা ইউজারনেম দিন (Private একাউন্টে কাজ করবে না)।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_tt_likes_952",
                platformId = "plat_tiktok",
                categoryId = "cat_tt_likes_views",
                name = "TikTok Video Hearts / Likes [Instant Start]",
                pricePerThousandBdt = 55.0,
                minQuantity = 100,
                maxQuantity = 100000,
                averageTime = "5 - 15 Minutes",
                description = "টিকটক ভিডিও লিঙ্ক দিন। ইনস্ট্যান্ট হার্টস/লাইক ডেলিভারি।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            SocialBoostService(
                id = "srv_tt_views_955",
                platformId = "plat_tiktok",
                categoryId = "cat_tt_likes_views",
                name = "TikTok Viral Video Views [ForYou Boost]",
                pricePerThousandBdt = 20.0,
                minQuantity = 1000,
                maxQuantity = 5000000,
                averageTime = "5 - 10 Minutes",
                description = "টিকটক ভিডিও লিঙ্ক দিন। তাৎক্ষণিক ভিউ শুরু হবে।",
                requiresLink = true,
                displayOrder = 2,
                isEnabled = true
            ),
            // Telegram Services
            SocialBoostService(
                id = "srv_tg_members_960",
                platformId = "plat_telegram",
                categoryId = "cat_tg_members",
                name = "Telegram Channel Members [Non-Drop • High Quality]",
                pricePerThousandBdt = 170.0,
                minQuantity = 100,
                maxQuantity = 100000,
                averageTime = "10 - 30 Minutes",
                description = "পাবলিক টেলিগ্রাম চ্যানেল বা গ্রুপের লিঙ্ক দিন।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            // LinkedIn Services
            SocialBoostService(
                id = "srv_li_followers_970",
                platformId = "plat_linkedin",
                categoryId = "cat_li_followers",
                name = "LinkedIn Profile / Company Page Followers",
                pricePerThousandBdt = 380.0,
                minQuantity = 100,
                maxQuantity = 25000,
                averageTime = "30 - 90 Minutes",
                description = "LinkedIn প্রোফাইল অথবা কোম্পানি পেজের লিঙ্ক দিন।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            // Video Services
            SocialBoostService(
                id = "srv_vid_views_980",
                platformId = "plat_video",
                categoryId = "cat_vid_views",
                name = "Short Video High-Speed Viral Views",
                pricePerThousandBdt = 35.0,
                minQuantity = 500,
                maxQuantity = 1000000,
                averageTime = "5 - 20 Minutes",
                description = "যেকোনো পাবলিক শর্ট ভিডিওর লিঙ্ক পেস্ট করুন।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            ),
            // Website Services
            SocialBoostService(
                id = "srv_web_traffic_990",
                platformId = "plat_website",
                categoryId = "cat_web_traffic",
                name = "Website Organic Visitors [Global & Targeted]",
                pricePerThousandBdt = 95.0,
                minQuantity = 500,
                maxQuantity = 500000,
                averageTime = "15 - 45 Minutes",
                description = "আপনার ওয়েবসাইটের সম্পূর্ণ URL (https://...) দিন।",
                requiresLink = true,
                displayOrder = 1,
                isEnabled = true
            )
        )
    }

    private fun defaultBanners(): List<PromoBanner> {
        return listOf(
            PromoBanner(
                id = "b_epay_1",
                title = "EPAY Dollar → BDT",
                subtitle = "Fast • Secure • Trusted Exchange",
                badge = "Best Exchange Rate",
                actionUrl = "",
                serviceTarget = "EPAY",
                gradientStartHex = "#FF0B1340",
                gradientEndHex = "#FF3A0F6B",
                isEnabled = true
            ),
            PromoBanner(
                id = "b_tiktok_1",
                title = "🪙 TikTok Coins Instant Recharge",
                subtitle = "Fast • Official • Lowest Rate Coin Top-Up",
                badge = "Trending Offer",
                actionUrl = "",
                serviceTarget = "TIKTOK",
                gradientStartHex = "#FF1F1641",
                gradientEndHex = "#FF8B104E",
                isEnabled = true
            ),
            PromoBanner(
                id = "b_chamet_1",
                title = "💎 Chamet Diamond VIP Top-up",
                subtitle = "Instant ID Delivery • VIP Streamer Bonus",
                badge = "Super Fast",
                actionUrl = "",
                serviceTarget = "CHAMET",
                gradientStartHex = "#FF1A0933",
                gradientEndHex = "#FF7C1D88",
                isEnabled = true
            ),
            PromoBanner(
                id = "b_exchange_1",
                title = "🇮🇳 BD ➔ India Rupee Exchange",
                subtitle = "PhonePe • Google Pay • Paytm • Instant UPI",
                badge = "Instant Send",
                actionUrl = "",
                serviceTarget = "EXCHANGE",
                gradientStartHex = "#FF10243E",
                gradientEndHex = "#FF1E56A0",
                isEnabled = true
            ),
            PromoBanner(
                id = "b_freefire_1",
                title = "🔥 Free Fire UID Diamond Top-Up",
                subtitle = "2-5 Mins Delivery • Weekly & Monthly Pass",
                badge = "Gaming Deal",
                actionUrl = "",
                serviceTarget = "FREEFIRE",
                gradientStartHex = "#FF220C38",
                gradientEndHex = "#FF6B114D",
                isEnabled = true
            )
        )
    }

    fun updateConfig(newConfig: AdminConfig) {
        val jsonObject = JSONObject()
        newConfig.customPrices.forEach { (key, value) ->
            jsonObject.put(key, value)
        }

        val customPkgsArr = JSONArray()
        newConfig.customPackages.forEach { pkg ->
            val pObj = JSONObject().apply {
                put("id", pkg.id)
                put("category", pkg.category.name)
                put("name", pkg.name)
                put("subtitle", pkg.subtitle)
                put("amount", pkg.amount)
                put("priceBdt", pkg.priceBdt)
                put("badge", pkg.badge ?: "")
                put("isPopular", pkg.isPopular)
                put("bonus", pkg.bonus ?: "")
            }
            customPkgsArr.put(pObj)
        }

        val customerMetaObj = JSONObject()
        newConfig.customerMetadata.forEach { (key, meta) ->
            val mObj = JSONObject().apply {
                put("displayName", meta.displayName)
                put("email", meta.email)
                put("phone", meta.phone)
                put("photoUri", meta.photoUri)
                put("walletBalanceBdt", meta.walletBalanceBdt)
                put("referralCode", meta.referralCode)
                put("referredByCode", meta.referredByCode)
                put("referralEarningsBdt", meta.referralEarningsBdt)
                put("referralCount", meta.referralCount)
                put("isVip", meta.isVip)
                put("isBlocked", meta.isBlocked)
                put("adminNote", meta.adminNote)
            }
            customerMetaObj.put(key, mObj)
        }

        val referralRewardsArr = JSONArray()
        newConfig.referralRewardsHistory.take(200).forEach { r ->
            referralRewardsArr.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("referrerEmail", r.referrerEmail)
                    put("referrerCode", r.referrerCode)
                    put("buyerEmail", r.buyerEmail)
                    put("buyerName", r.buyerName)
                    put("orderNumber", r.orderNumber)
                    put("itemName", r.itemName)
                    put("orderPriceBdt", r.orderPriceBdt)
                    put("commissionBdt", r.commissionBdt)
                    put("status", r.status)
                    put("timestamp", r.timestamp)
                }
            )
        }

        val walletTxArr = JSONArray()
        newConfig.walletTransactionsHistory.take(250).forEach { tx ->
            walletTxArr.put(
                JSONObject().apply {
                    put("id", tx.id)
                    put("userEmail", tx.userEmail)
                    put("type", tx.type)
                    put("title", tx.title)
                    put("amountBdt", tx.amountBdt)
                    put("isCredit", tx.isCredit)
                    put("status", tx.status)
                    put("reasonOrNote", tx.reasonOrNote)
                    put("referenceId", tx.referenceId)
                    put("timestamp", tx.timestamp)
                }
            )
        }

        val bannersArray = JSONArray()
        newConfig.promoBanners.forEach { banner ->
            val bObj = JSONObject()
            bObj.put("id", banner.id)
            bObj.put("title", banner.title)
            bObj.put("subtitle", banner.subtitle)
            bObj.put("imageUrl", banner.imageUrl)
            bObj.put("badge", banner.badge)
            bObj.put("actionUrl", banner.actionUrl)
            bObj.put("serviceTarget", banner.serviceTarget)
            bObj.put("gradientStartHex", banner.gradientStartHex)
            bObj.put("gradientEndHex", banner.gradientEndHex)
            bObj.put("isEnabled", banner.isEnabled)
            bannersArray.put(bObj)
        }

        val sbPlatformsArr = JSONArray()
        newConfig.socialBoostPlatforms.forEach { p ->
            sbPlatformsArr.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("iconKey", p.iconKey)
                    put("description", p.description)
                    put("displayOrder", p.displayOrder)
                    put("isEnabled", p.isEnabled)
                }
            )
        }

        val sbCategoriesArr = JSONArray()
        newConfig.socialBoostCategories.forEach { c ->
            sbCategoriesArr.put(
                JSONObject().apply {
                    put("id", c.id)
                    put("platformId", c.platformId)
                    put("name", c.name)
                    put("description", c.description)
                    put("displayOrder", c.displayOrder)
                    put("isEnabled", c.isEnabled)
                }
            )
        }

        val sbServicesArr = JSONArray()
        newConfig.socialBoostServices.forEach { s ->
            sbServicesArr.put(
                JSONObject().apply {
                    put("id", s.id)
                    put("serviceId", s.id)
                    put("platformId", s.platformId)
                    put("categoryId", s.categoryId)
                    put("name", s.name)
                    put("serviceName", s.name)
                    put("pricePerThousandBdt", s.pricePerThousandBdt)
                    put("pricePer1000", s.pricePerThousandBdt)
                    put("minQuantity", s.minQuantity)
                    put("maxQuantity", s.maxQuantity)
                    put("averageTime", s.averageTime)
                    put("description", s.description)
                    put("requiresLink", s.requiresLink)
                    put("displayOrder", s.displayOrder)
                    put("isEnabled", s.isEnabled)
                    put("createdAt", s.createdAt)
                    put("updatedAt", s.updatedAt)
                }
            )
        }

        val handoffObj = JSONObject()
        newConfig.supportChatHandoffMap.forEach { (k, v) ->
            val cleanK = k.trim().lowercase()
            if (cleanK.isNotBlank()) {
                handoffObj.put(cleanK, v.trim().uppercase())
            }
        }

        prefs.edit().apply {
            putString("admin_email", newConfig.adminEmail)
            putString("epay_email", newConfig.epayEmail)
            putString("firebase_user_email", newConfig.firebaseUserEmail)
            putString("firebase_user_id", newConfig.firebaseUserId)
            putString("bkash_number", newConfig.bkashNumber)
            putString("bkash_type", newConfig.bkashType)
            putString("bkash_instruction", newConfig.bkashInstruction)
            putString("nagad_number", newConfig.nagadNumber)
            putString("nagad_type", newConfig.nagadType)
            putString("nagad_instruction", newConfig.nagadInstruction)
            putString("rocket_number", newConfig.rocketNumber)
            putBoolean("rocket_enabled", newConfig.isRocketEnabled)
            putString("whatsapp_number", newConfig.whatsappNumber)
            putString("whatsapp_display", newConfig.whatsappDisplay)
            putString("telegram_username", newConfig.telegramUsername)
            putString("telegram_url", newConfig.telegramUrl)
            putString("notice_title", newConfig.noticeTitle)
            putString("notice_rule1", newConfig.noticeRule1)
            putString("notice_rule2", newConfig.noticeRule2)
            putString("notice_rule3", newConfig.noticeRule3)
            putString("notice_warning", newConfig.noticeWarning)
            putString("notice_alert", newConfig.noticeAlert)
            putBoolean("notice_show_launch", newConfig.noticeShowOnLaunch)
            putFloat("epay_base_rate", newConfig.epayBaseRate.toFloat())
            putFloat("epay_rate_970_fixed", newConfig.epayRate970Fixed.toFloat())
            putFloat("epay_rate_10_fixed", newConfig.epayRate10Fixed.toFloat())
            putFloat("epay_rate_30_bulk", newConfig.epayRate30Bulk.toFloat())
            putFloat("epay_rate_500_wholesale", newConfig.epayRate500Wholesale.toFloat())
            putFloat("inr_base_rate", newConfig.inrBaseRate.toFloat())
            putFloat("tiktok_rate_coin", newConfig.tiktokRatePerCoin.toFloat())
            putFloat("chamet_rate_diamond", newConfig.chametRatePerDiamond.toFloat())
            putString("custom_prices_json", jsonObject.toString())
            putString("custom_packages_json", customPkgsArr.toString())
            putStringSet("deleted_package_ids", newConfig.deletedPackageIds)
            putString("customer_meta_json", customerMetaObj.toString())
            putString("store_override", newConfig.storeStatusOverride)
            putBoolean("is_auto_topup_enabled", newConfig.isAutoTopupEnabled)
            putBoolean("auto_instant_wallet_enabled", newConfig.autoInstantWalletEnabled)
            putBoolean("is_referral_enabled", newConfig.isReferralEnabled)
            putString("referral_commission_mode", newConfig.referralCommissionMode)
            putFloat("referral_base_order_amount_bdt", newConfig.referralBaseOrderAmountBdt.toFloat())
            putFloat("referral_commission_bdt", newConfig.referralCommissionBdt.toFloat())
            putFloat("referral_commission_percent", newConfig.referralCommissionPercent.toFloat())
            putFloat("referral_min_order_amount_bdt", newConfig.referralMinOrderAmountBdt.toFloat())
            putFloat("referral_max_commission_bdt", newConfig.referralMaxCommissionBdt.toFloat())
            putStringSet("referral_eligible_services", newConfig.referralEligibleServices)
            putString("referral_rules_text", newConfig.referralRulesText)
            val resolvedSignupBonus = if (newConfig.referralNewUserBonusBdt > 0.0) newConfig.referralNewUserBonusBdt else newConfig.referralSignupBonusBdt
            putFloat("referral_signup_bonus_bdt", resolvedSignupBonus.toFloat())
            putFloat("min_wallet_add_money_bdt", newConfig.minWalletAddMoneyBdt.toFloat())
            putBoolean("referral_proportional_scaling", newConfig.referralProportionalScaling)
            putString("referral_rewards_json", referralRewardsArr.toString())
            putString("wallet_transactions_json", walletTxArr.toString())
            putString("tiktok_api_url", newConfig.tiktokApiUrl)
            putString("tiktok_api_key", newConfig.tiktokApiKey)
            putString("chamet_api_url", newConfig.chametApiUrl)
            putString("chamet_api_key", newConfig.chametApiKey)
            putString("promo_banners_json", bannersArray.toString())
            putString("social_boost_platforms_json", sbPlatformsArr.toString())
            putString("social_boost_categories_json", sbCategoriesArr.toString())
            putString("social_boost_services_json", sbServicesArr.toString())
            putBoolean("is_ai_auto_chat_enabled", newConfig.isAiAutoChatEnabled)
            putString("support_chat_handoff_json", handoffObj.toString())
            putString("admin_custom_ai_instructions", newConfig.adminCustomAiInstructions.trim())
            apply()
        }
        _config.value = newConfig
    }

    fun appendAdminCustomAiInstruction(instruction: String): AdminConfig {
        val clean = instruction.trim()
        if (clean.isBlank()) return _config.value
        val existing = _config.value.adminCustomAiInstructions.trim()
        val combined = if (existing.isBlank()) {
            "• $clean"
        } else if (existing.contains(clean, ignoreCase = true)) {
            existing
        } else {
            "$existing\n• $clean"
        }
        val updated = _config.value.copy(adminCustomAiInstructions = combined)
        updateConfig(updated)
        return updated
    }

    fun clearAdminCustomAiInstructions(): AdminConfig {
        val updated = _config.value.copy(adminCustomAiInstructions = "")
        updateConfig(updated)
        return updated
    }

    fun getConversationChatMode(conversationId: String): String {
        val key = conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
        if (key.isBlank()) return "AI_AUTO"
        return _config.value.supportChatHandoffMap[key] ?: "AI_AUTO"
    }

    fun setConversationChatMode(conversationId: String, mode: String) {
        val key = conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
        if (key.isBlank()) return
        val nextMap = _config.value.supportChatHandoffMap.toMutableMap()
        nextMap[key] = mode.trim().uppercase()
        updateConfig(_config.value.copy(supportChatHandoffMap = nextMap))
    }

    fun setAiAutoChatMasterEnabled(enabled: Boolean) {
        updateConfig(_config.value.copy(isAiAutoChatEnabled = enabled))
    }

    fun updatePackagePrice(packageId: String, newPrice: Double) {
        val currentPrices = _config.value.customPrices.toMutableMap()
        currentPrices[packageId] = newPrice
        val updatedCustomPkgs = _config.value.customPackages.map {
            if (it.id == packageId) it.copy(priceBdt = newPrice) else it
        }
        updateConfig(_config.value.copy(customPrices = currentPrices, customPackages = updatedCustomPkgs))
    }

    fun addOrUpdatePackage(pkg: TopupPackage) {
        val currentCustom = _config.value.customPackages.toMutableList()
        val index = currentCustom.indexOfFirst { it.id == pkg.id }
        if (index >= 0) {
            currentCustom[index] = pkg
        } else {
            currentCustom.add(pkg)
        }
        val currentPrices = _config.value.customPrices.toMutableMap()
        currentPrices[pkg.id] = pkg.priceBdt
        val updatedDeleted = _config.value.deletedPackageIds - pkg.id
        updateConfig(
            _config.value.copy(
                customPackages = currentCustom,
                customPrices = currentPrices,
                deletedPackageIds = updatedDeleted
            )
        )
    }

    fun deletePackage(packageId: String) {
        val updatedCustom = _config.value.customPackages.filterNot { it.id == packageId }
        val updatedDeleted = _config.value.deletedPackageIds + packageId
        updateConfig(
            _config.value.copy(
                customPackages = updatedCustom,
                deletedPackageIds = updatedDeleted
            )
        )
    }

    fun resetPackagePrices() {
        updateConfig(
            _config.value.copy(
                customPrices = emptyMap(),
                customPackages = emptyList(),
                deletedPackageIds = emptySet()
            )
        )
    }

    fun updateCustomerMeta(accountId: String, displayName: String, isVip: Boolean, isBlocked: Boolean, adminNote: String) {
        val map = _config.value.customerMetadata.toMutableMap()
        val key = if (accountId.contains("@")) accountId.trim().lowercase() else accountId.trim()
        val existing = map[key] ?: map[accountId]
        val emailVal = existing?.email?.ifBlank {
            if (key.contains("@")) key else ""
        } ?: if (key.contains("@")) key else ""
        map[key] = CustomerMeta(
            accountId = key,
            displayName = displayName.ifBlank { existing?.displayName ?: "" },
            email = emailVal,
            phone = existing?.phone?.ifBlank { if (emailVal.isNotBlank()) getSavedAccountPhone(emailVal) else "" } ?: "",
            photoUri = existing?.photoUri?.ifBlank { if (emailVal.isNotBlank()) getSavedAccountPhotoUri(emailVal) else "" } ?: "",
            walletBalanceBdt = existing?.walletBalanceBdt?.coerceAtLeast(if (emailVal.isNotBlank()) getUserWalletBalance(emailVal) else 0.0)
                ?: if (emailVal.isNotBlank()) getUserWalletBalance(emailVal) else 0.0,
            referralCode = existing?.referralCode?.ifBlank { if (emailVal.isNotBlank()) getOrCreateUserReferralCode(emailVal) else "" }
                ?: if (emailVal.isNotBlank()) getOrCreateUserReferralCode(emailVal) else "",
            referredByCode = existing?.referredByCode?.ifBlank { if (emailVal.isNotBlank()) getUserReferredByCode(emailVal) else "" }
                ?: if (emailVal.isNotBlank()) getUserReferredByCode(emailVal) else "",
            referralEarningsBdt = existing?.referralEarningsBdt?.coerceAtLeast(if (emailVal.isNotBlank()) getUserReferralEarnings(emailVal) else 0.0)
                ?: if (emailVal.isNotBlank()) getUserReferralEarnings(emailVal) else 0.0,
            referralCount = existing?.referralCount?.coerceAtLeast(if (emailVal.isNotBlank()) getUserReferralCount(emailVal) else 0)
                ?: if (emailVal.isNotBlank()) getUserReferralCount(emailVal) else 0,
            isVip = isVip,
            isBlocked = isBlocked,
            adminNote = adminNote
        )
        updateConfig(_config.value.copy(customerMetadata = map))
    }

    fun adjustCustomerWalletBalance(
        accountId: String,
        email: String,
        newBalanceBdt: Double,
        reason: String = "",
        adminReference: String = ""
    ) {
        val safeBal = newBalanceBdt.coerceAtLeast(0.0)
        val cleanEmail = email.trim().lowercase().ifBlank {
            if (accountId.contains("@")) accountId.trim().lowercase() else ""
        }
        val prevBal = if (cleanEmail.isNotBlank()) {
            getUserWalletBalance(cleanEmail)
        } else {
            _config.value.customerMetadata[accountId]?.walletBalanceBdt ?: 0.0
        }
        val delta = safeBal - prevBal
        if (cleanEmail.isNotBlank()) {
            setUserWalletBalance(cleanEmail, safeBal)
        }
        val map = _config.value.customerMetadata.toMutableMap()
        val existing = map[accountId] ?: (if (cleanEmail.isNotBlank()) map[cleanEmail] else null)
        val targetKey = if (map.containsKey(accountId)) accountId else cleanEmail.ifBlank { accountId }
        map[targetKey] = (existing ?: CustomerMeta(accountId = targetKey, email = cleanEmail)).copy(
            walletBalanceBdt = safeBal
        )
        val updatedTxList = _config.value.walletTransactionsHistory.toMutableList()
        if (kotlin.math.abs(delta) >= 0.01 && cleanEmail.isNotBlank()) {
            val isCredit = delta > 0
            val absAmt = kotlin.math.abs(delta)
            val txEntry = WalletTransactionEntry(
                id = "wtx_admin_${System.currentTimeMillis()}",
                userEmail = cleanEmail,
                type = if (isCredit) "ADMIN_CREDIT" else "ADMIN_DEBIT",
                title = if (isCredit) "Balance Added by Admin" else "Balance Deducted by Admin",
                amountBdt = absAmt,
                isCredit = isCredit,
                status = "Completed",
                reasonOrNote = reason.ifBlank { if (isCredit) "Admin Wallet Credit" else "Admin Wallet Deduction" },
                referenceId = adminReference.ifBlank { "ADM-${System.currentTimeMillis().toString().takeLast(5)}" },
                timestamp = System.currentTimeMillis()
            )
            updatedTxList.add(0, txEntry)
            pushCustomNotification(
                customerEmail = cleanEmail,
                title = if (isCredit) "💰 ওয়ালেটে +৳${absAmt.toInt()} যুক্ত হয়েছে" else "💳 ওয়ালেট ব্যালেন্স সমন্বয় (-৳${absAmt.toInt()})",
                message = "Admin আপনার প্রোফাইল ওয়ালেট ব্যালেন্স আপডেট করেছেন। বর্তমান ব্যালেন্স: ৳${String.format(Locale.US, "%.2f", safeBal)} BDT" +
                    if (reason.isNotBlank()) " • কারণ: $reason" else "",
                status = "Completed",
                priceBdt = absAmt,
                referenceId = txEntry.referenceId
            )
        }
        updateConfig(
            _config.value.copy(
                customerMetadata = map,
                walletTransactionsHistory = updatedTxList.take(250)
            )
        )
    }

    fun recordWalletTransaction(entry: WalletTransactionEntry) {
        val current = _config.value.walletTransactionsHistory.toMutableList()
        val existingIdx = current.indexOfFirst {
            it.id == entry.id || (entry.referenceId.isNotBlank() && it.referenceId == entry.referenceId && it.type == entry.type)
        }
        if (existingIdx >= 0) {
            current[existingIdx] = entry
        } else {
            current.add(0, entry)
        }
        updateConfig(_config.value.copy(walletTransactionsHistory = current.take(250)))
    }

    fun getUserWalletTransactions(email: String, userOrders: List<OrderRecord>): List<WalletTransactionEntry> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) return emptyList()
        val result = mutableListOf<WalletTransactionEntry>()
        val seenRefs = mutableSetOf<String>()

        // 1. Explicit wallet transactions stored in config
        _config.value.walletTransactionsHistory
            .filter { it.userEmail.equals(cleanEmail, ignoreCase = true) }
            .forEach { tx ->
                result.add(tx)
                if (tx.referenceId.isNotBlank()) seenRefs.add("${tx.type}_${tx.referenceId}")
            }

        // 2. Synthesize from user's orders (Add Money & Wallet Service Purchases)
        userOrders
            .filter {
                it.customerEmail.equals(cleanEmail, ignoreCase = true) ||
                    it.userAccountId.lowercase().startsWith(cleanEmail)
            }
            .forEach { ord ->
                val isAddMoney = ord.category.contains("Wallet", ignoreCase = true) &&
                    !ord.category.contains("Cashout", ignoreCase = true)
                val isPaidByWallet = ord.paymentMethod.contains("Wallet", ignoreCase = true) ||
                    ord.paymentMethod.contains("ওয়ালেট", ignoreCase = true)

                if (isAddMoney && !seenRefs.contains("ADD_MONEY_${ord.orderNumber}")) {
                    seenRefs.add("ADD_MONEY_${ord.orderNumber}")
                    val normStatus = when (ord.status) {
                        "Approved", "Completed" -> "Completed"
                        "Cancelled" -> "Cancelled"
                        else -> "Processing"
                    }
                    result.add(
                        WalletTransactionEntry(
                            id = "ord_add_${ord.orderNumber}",
                            userEmail = cleanEmail,
                            type = "ADD_MONEY",
                            title = "Balance Added (${ord.paymentMethod})",
                            amountBdt = ord.priceBdt,
                            isCredit = true,
                            status = normStatus,
                            reasonOrNote = "TrxID: ${ord.transactionTrxId.ifBlank { "N/A" }}",
                            referenceId = ord.orderNumber,
                            timestamp = ord.timestamp
                        )
                    )
                } else if (isPaidByWallet && !isAddMoney && !seenRefs.contains("SERVICE_PURCHASE_${ord.orderNumber}")) {
                    seenRefs.add("SERVICE_PURCHASE_${ord.orderNumber}")
                    val normStatus = when (ord.status) {
                        "Approved", "Completed" -> "Completed"
                        "Cancelled" -> "Cancelled"
                        else -> "Processing"
                    }
                    result.add(
                        WalletTransactionEntry(
                            id = "ord_buy_${ord.orderNumber}",
                            userEmail = cleanEmail,
                            type = "SERVICE_PURCHASE",
                            title = "Service Purchase • ${ord.itemName}",
                            amountBdt = ord.priceBdt,
                            isCredit = false,
                            status = normStatus,
                            reasonOrNote = "${ord.category} (${ord.amountOrQuantity})",
                            referenceId = ord.orderNumber,
                            timestamp = ord.timestamp
                        )
                    )
                }
            }

        // 3. Synthesize from referral rewards history
        val myCode = getOrCreateUserReferralCode(cleanEmail)
        _config.value.referralRewardsHistory
            .filter {
                it.referrerEmail.equals(cleanEmail, ignoreCase = true) ||
                    (myCode.isNotBlank() && it.referrerCode.equals(myCode, ignoreCase = true))
            }
            .forEach { ref ->
                if (!seenRefs.contains("REFERRAL_BONUS_${ref.orderNumber}")) {
                    seenRefs.add("REFERRAL_BONUS_${ref.orderNumber}")
                    result.add(
                        WalletTransactionEntry(
                            id = ref.id,
                            userEmail = cleanEmail,
                            type = "REFERRAL_BONUS",
                            title = "Referral Bonus • ${ref.buyerName.ifBlank { "User" }}",
                            amountBdt = ref.commissionBdt,
                            isCredit = true,
                            status = ref.status.ifBlank { "Completed" },
                            reasonOrNote = "${ref.itemName} (#${ref.orderNumber})",
                            referenceId = ref.orderNumber,
                            timestamp = ref.timestamp
                        )
                    )
                }
            }

        return result.sortedByDescending { it.timestamp }
    }

    fun isServiceEligibleForReferral(orderCategory: String, conf: AdminConfig = _config.value): Boolean {
        if (conf.referralEligibleServices.isEmpty()) return true
        val upper = orderCategory.trim().uppercase()
        return conf.referralEligibleServices.any { eligible ->
            upper.contains(eligible.uppercase()) ||
                (eligible.equals("FREEFIRE", ignoreCase = true) && (upper.contains("GAMING") || upper.contains("FREE"))) ||
                (eligible.equals("SOCIAL_BOOST", ignoreCase = true) && upper.contains("SOCIAL"))
        }
    }

    fun calculateReferralCommissionForAmount(
        orderPriceBdt: Double,
        conf: AdminConfig = _config.value,
        orderCategory: String = ""
    ): Double {
        if (!conf.isReferralEnabled || orderPriceBdt < conf.referralMinOrderAmountBdt) return 0.0
        if (orderCategory.isNotBlank() && !isServiceEligibleForReferral(orderCategory, conf)) return 0.0

        val raw = when (conf.referralCommissionMode.uppercase()) {
            "PERCENTAGE" -> {
                val pct = conf.referralCommissionPercent.coerceIn(0.0, 100.0)
                orderPriceBdt * (pct / 100.0)
            }
            "FLAT" -> {
                conf.referralCommissionBdt.coerceAtLeast(0.0)
            }
            else -> {
                val base = conf.referralBaseOrderAmountBdt.coerceAtLeast(1.0)
                val comm = conf.referralCommissionBdt.coerceAtLeast(0.0)
                if (comm <= 0.0) 0.0
                else if (conf.referralProportionalScaling) {
                    (orderPriceBdt / base) * comm
                } else {
                    val units = (orderPriceBdt / base).toInt()
                    if (units >= 1) units * comm else 0.0
                }
            }
        }
        val capped = if (conf.referralMaxCommissionBdt > 0.0) {
            raw.coerceAtMost(conf.referralMaxCommissionBdt)
        } else {
            raw
        }
        return kotlin.math.round(capped * 100.0) / 100.0
    }

    /**
     * Called when an order transitions to Approved/Completed.
     * 1) If it is a Wallet Add Money order, credits the customer's wallet balance once.
     * 2) If it is a regular service order and the buyer was referred by another user,
     *    credits the referrer's wallet balance and records the ReferralRewardRecord once.
     */
    fun processOrderCompletionRewards(order: OrderRecord): ReferralRewardRecord? {
        val processedWalletOrderIds = prefs.getStringSet("processed_wallet_topup_orders", emptySet())?.toMutableSet() ?: mutableSetOf()
        val buyerEmail = order.customerEmail.trim().lowercase().ifBlank {
            if (order.userAccountId.contains("@")) order.userAccountId.substringBefore(" ").trim().lowercase() else ""
        }
        val isWalletAddOrder = order.category.equals("Wallet", ignoreCase = true) ||
            order.category.contains("Wallet Top-Up", ignoreCase = true) ||
            order.category.contains("Wallet Deposit", ignoreCase = true) ||
            order.itemName.contains("Wallet Balance", ignoreCase = true) ||
            order.itemName.contains("Wallet Add Money", ignoreCase = true) ||
            order.itemName.contains("ওয়ালেট ব্যালেন্স", ignoreCase = true)

        if (isWalletAddOrder) {
            if (buyerEmail.isNotBlank() && !processedWalletOrderIds.contains(order.orderNumber)) {
                processedWalletOrderIds.add(order.orderNumber)
                prefs.edit().putStringSet("processed_wallet_topup_orders", processedWalletOrderIds).apply()
                addUserWalletBalance(buyerEmail, order.priceBdt)
                val map = _config.value.customerMetadata.toMutableMap()
                val currentBal = getUserWalletBalance(buyerEmail)
                val existing = map[buyerEmail] ?: CustomerMeta(accountId = buyerEmail, email = buyerEmail)
                map[buyerEmail] = existing.copy(walletBalanceBdt = currentBal)
                val updatedTx = _config.value.walletTransactionsHistory.toMutableList()
                val txEntry = WalletTransactionEntry(
                    id = "ord_add_${order.orderNumber}",
                    userEmail = buyerEmail,
                    type = "ADD_MONEY",
                    title = "Balance Added (${order.paymentMethod})",
                    amountBdt = order.priceBdt,
                    isCredit = true,
                    status = "Completed",
                    reasonOrNote = "TrxID: ${order.transactionTrxId}",
                    referenceId = order.orderNumber,
                    timestamp = System.currentTimeMillis()
                )
                val existingTxIdx = updatedTx.indexOfFirst { it.referenceId == order.orderNumber && it.type == "ADD_MONEY" }
                if (existingTxIdx >= 0) updatedTx[existingTxIdx] = txEntry else updatedTx.add(0, txEntry)
                updateConfig(
                    _config.value.copy(
                        customerMetadata = map,
                        walletTransactionsHistory = updatedTx.take(250)
                    )
                )
            }
            return null
        }

        // Process Referral Commission for completed/paid service orders ONLY
        if (order.status != "Completed" && order.status != "Approved") return null
        val conf = _config.value
        if (!conf.isReferralEnabled || buyerEmail.isBlank()) return null

        // Avoid duplicate reward for the same orderNumber
        val processedRefOrderIds = prefs.getStringSet("processed_referral_order_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        if (processedRefOrderIds.contains(order.orderNumber) || conf.referralRewardsHistory.any { it.orderNumber == order.orderNumber }) {
            return null
        }

        val referredByCode = getUserReferredByCode(buyerEmail)
            .ifBlank { conf.customerMetadata[buyerEmail]?.referredByCode ?: "" }
            .trim()
            .uppercase()
        if (referredByCode.isBlank()) return null

        val commission = calculateReferralCommissionForAmount(order.priceBdt, conf, order.category)
        if (commission <= 0.0) return null

        // Find referrer email by referralCode
        val referrerEmail = findEmailByReferralCode(referredByCode) ?: return null
        if (referrerEmail.equals(buyerEmail, ignoreCase = true)) return null

        processedRefOrderIds.add(order.orderNumber)
        prefs.edit().putStringSet("processed_referral_order_ids", processedRefOrderIds).apply()

        // Credit referrer's wallet and referral earnings
        addUserWalletBalance(referrerEmail, commission)
        val newRefEarnings = getUserReferralEarnings(referrerEmail) + commission
        prefs.edit().putFloat("acct_ref_earnings_$referrerEmail", newRefEarnings.toFloat()).apply()
        if (_loggedInUserEmail.value.equals(referrerEmail, ignoreCase = true)) {
            _loggedInUserReferralEarnings.value = newRefEarnings
        }

        val maskedBuyer = getSavedAccountName(buyerEmail).ifBlank {
            val prefix = buyerEmail.substringBefore("@").take(3).uppercase()
            "User #$prefix${kotlin.math.abs(buyerEmail.hashCode()) % 900 + 100}"
        }

        val rewardRecord = ReferralRewardRecord(
            id = "ref_${order.orderNumber}_${System.currentTimeMillis()}",
            referrerEmail = referrerEmail,
            referrerCode = referredByCode,
            buyerEmail = buyerEmail,
            buyerName = maskedBuyer,
            orderNumber = order.orderNumber,
            itemName = order.itemName,
            orderPriceBdt = order.priceBdt,
            commissionBdt = commission,
            status = "Completed",
            timestamp = System.currentTimeMillis()
        )

        val refWalletTx = WalletTransactionEntry(
            id = rewardRecord.id,
            userEmail = referrerEmail,
            type = "REFERRAL_BONUS",
            title = "Referral Bonus • $maskedBuyer",
            amountBdt = commission,
            isCredit = true,
            status = "Completed",
            reasonOrNote = "${order.itemName} (#${order.orderNumber})",
            referenceId = order.orderNumber,
            timestamp = rewardRecord.timestamp
        )

        val updatedHistory = listOf(rewardRecord) + conf.referralRewardsHistory
        val updatedWalletTx = listOf(refWalletTx) + conf.walletTransactionsHistory
        val map = conf.customerMetadata.toMutableMap()
        val existingReferrerMeta = map[referrerEmail] ?: CustomerMeta(
            accountId = referrerEmail,
            email = referrerEmail,
            displayName = getSavedAccountName(referrerEmail),
            referralCode = referredByCode
        )
        map[referrerEmail] = existingReferrerMeta.copy(
            walletBalanceBdt = getUserWalletBalance(referrerEmail),
            referralEarningsBdt = newRefEarnings,
            referralCount = getUserReferralCount(referrerEmail).coerceAtLeast(existingReferrerMeta.referralCount)
        )
        updateConfig(
            conf.copy(
                customerMetadata = map,
                referralRewardsHistory = updatedHistory.take(200),
                walletTransactionsHistory = updatedWalletTx.take(250)
            )
        )
        pushCustomNotification(
            customerEmail = referrerEmail,
            title = "🎁 রেফারেল বোনাস পেয়েছেন! (+৳${String.format(Locale.US, "%.1f", commission)})",
            message = "$maskedBuyer আপনার রেফার কোডে '${order.itemName}' কিনেছেন। আপনার প্রোফাইল ওয়ালেটে +৳${String.format(Locale.US, "%.1f", commission)} যুক্ত হয়েছে!",
            status = "Completed",
            priceBdt = commission,
            referenceId = order.orderNumber
        )
        return rewardRecord
    }

    fun findEmailByReferralCode(code: String): String? {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.isBlank()) return null
        // Check customerMetadata first (both explicit referralCode and deterministic code from email)
        _config.value.customerMetadata.values.firstOrNull { meta ->
            val emailCandidate = meta.email.trim().lowercase().ifBlank {
                if (meta.accountId.contains("@")) meta.accountId.trim().lowercase() else ""
            }
            val effectiveCode = meta.referralCode.ifBlank {
                if (emailCandidate.isNotBlank()) getOrCreateUserReferralCode(emailCandidate) else ""
            }
            emailCandidate.isNotBlank() && effectiveCode.equals(cleanCode, ignoreCase = true)
        }?.let {
            return it.email.trim().lowercase().ifBlank { it.accountId.trim().lowercase() }
        }

        // Check all stored prefs keys acct_ref_code_<email>
        for ((k, v) in prefs.all) {
            if (k.startsWith("acct_ref_code_") && (v as? String)?.equals(cleanCode, ignoreCase = true) == true) {
                return k.removePrefix("acct_ref_code_").trim().lowercase()
            }
        }
        return null
    }

    /**
     * Applies a referral code for a user if they haven't set one yet.
     * Returns pair (success: Boolean, message: String)
     */
    fun applyReferralCodeForUser(userEmail: String, rawCode: String): Pair<Boolean, String> {
        val email = userEmail.trim().lowercase()
        val code = rawCode.trim().uppercase()
        if (email.isBlank()) return false to "প্রথমে আপনার একাউন্টে লগইন করুন।"
        if (code.length < 4) return false to "সঠিক রেফারেল কোড লিখুন।"
        val myCode = getOrCreateUserReferralCode(email)
        if (code.equals(myCode, ignoreCase = true)) {
            return false to "নিজের রেফারেল কোড ব্যবহার করা যাবে না!"
        }
        val existingRef = getUserReferredByCode(email)
        if (existingRef.isNotBlank()) {
            return false to "আপনি ইতিমধ্যে একটি রেফারেল কোড ($existingRef) ব্যবহার করেছেন।"
        }

        val referrerEmail = findEmailByReferralCode(code)
        prefs.edit().putString("acct_referred_by_$email", code).apply()
        if (_loggedInUserEmail.value.equals(email, ignoreCase = true)) {
            _loggedInUserReferredByCode.value = code
        }

        val conf = _config.value
        val map = conf.customerMetadata.toMutableMap()
        val myMeta = map[email] ?: CustomerMeta(
            accountId = email,
            email = email,
            displayName = getSavedAccountName(email),
            referralCode = myCode,
            walletBalanceBdt = getUserWalletBalance(email)
        )
        var myNewWallet = getUserWalletBalance(email)
        if (conf.isReferralEnabled && conf.referralSignupBonusBdt > 0.0) {
            addUserWalletBalance(email, conf.referralSignupBonusBdt)
            myNewWallet = getUserWalletBalance(email)
        }
        map[email] = myMeta.copy(
            referredByCode = code,
            walletBalanceBdt = myNewWallet
        )

        if (!referrerEmail.isNullOrBlank() && !referrerEmail.equals(email, ignoreCase = true)) {
            val newCount = getUserReferralCount(referrerEmail) + 1
            prefs.edit().putInt("acct_ref_count_$referrerEmail", newCount).apply()
            if (_loggedInUserEmail.value.equals(referrerEmail, ignoreCase = true)) {
                _loggedInUserReferralCount.value = newCount
            }
            val refMeta = map[referrerEmail] ?: CustomerMeta(
                accountId = referrerEmail,
                email = referrerEmail,
                displayName = getSavedAccountName(referrerEmail),
                referralCode = code
            )
            map[referrerEmail] = refMeta.copy(referralCount = newCount)
        }

        updateConfig(conf.copy(customerMetadata = map))
        val bonusMsg = if (conf.isReferralEnabled && conf.referralSignupBonusBdt > 0.0) {
            " এবং বোনাস ৳${conf.referralSignupBonusBdt.toInt()} আপনার ওয়ালেটে যোগ হয়েছে!"
        } else ""
        return true to "রেফারেল কোড ($code) সফলভাবে যুক্ত হয়েছে$bonusMsg"
    }

    fun mergeCustomersFromCloud(cloudCustomers: List<CustomerMeta>) {
        if (cloudCustomers.isEmpty()) return
        val currentMap = _config.value.customerMetadata.toMutableMap()
        var changed = false
        cloudCustomers.forEach { cloudMeta ->
            val key = cloudMeta.email.trim().lowercase().ifBlank { cloudMeta.accountId.trim() }
            if (key.isBlank()) return@forEach
            val existing = currentMap[key] ?: currentMap[cloudMeta.accountId]
            val emailVal = cloudMeta.email.trim().lowercase().ifBlank { existing?.email ?: (if (key.contains("@")) key else "") }
            val refCodeVal = cloudMeta.referralCode.ifBlank {
                existing?.referralCode?.ifBlank {
                    if (emailVal.isNotBlank()) getOrCreateUserReferralCode(emailVal) else ""
                } ?: if (emailVal.isNotBlank()) getOrCreateUserReferralCode(emailVal) else ""
            }
            val merged = CustomerMeta(
                accountId = key,
                displayName = cloudMeta.displayName.ifBlank {
                    existing?.displayName?.ifBlank {
                        if (emailVal.isNotBlank()) getSavedAccountName(emailVal).ifBlank { emailVal.substringBefore("@") } else ""
                    } ?: if (emailVal.isNotBlank()) emailVal.substringBefore("@") else ""
                },
                email = emailVal,
                phone = cloudMeta.phone.ifBlank { existing?.phone ?: "" },
                photoUri = cloudMeta.photoUri.ifBlank { existing?.photoUri ?: "" },
                walletBalanceBdt = if (cloudMeta.walletBalanceBdt > 0.0) cloudMeta.walletBalanceBdt else (existing?.walletBalanceBdt ?: 0.0),
                referralCode = refCodeVal,
                referredByCode = cloudMeta.referredByCode.ifBlank { existing?.referredByCode ?: "" },
                referralEarningsBdt = if (cloudMeta.referralEarningsBdt > 0.0) cloudMeta.referralEarningsBdt else (existing?.referralEarningsBdt ?: 0.0),
                referralCount = if (cloudMeta.referralCount > 0) cloudMeta.referralCount else (existing?.referralCount ?: 0),
                isVip = cloudMeta.isVip || (existing?.isVip == true),
                isBlocked = cloudMeta.isBlocked || (existing?.isBlocked == true),
                adminNote = cloudMeta.adminNote.ifBlank { existing?.adminNote ?: "" }
            )
            if (currentMap[key] != merged) {
                currentMap[key] = merged
                changed = true
            }
            if (merged.email.isNotBlank()) {
                saveUserAccountProfile(merged.email, merged.displayName, merged.phone, merged.photoUri.takeIf { it.isNotBlank() })
                syncCloudUserWalletAndReferral(
                    email = merged.email,
                    cloudWalletBalance = merged.walletBalanceBdt.takeIf { it > 0.0 || getUserWalletBalance(merged.email) == 0.0 },
                    cloudReferralCode = merged.referralCode.takeIf { it.isNotBlank() },
                    cloudReferredBy = merged.referredByCode.takeIf { it.isNotBlank() },
                    cloudReferralEarnings = merged.referralEarningsBdt.takeIf { it > 0.0 },
                    cloudReferralCount = merged.referralCount.takeIf { it > 0 }
                )
            }
        }
        if (changed) {
            updateConfig(_config.value.copy(customerMetadata = currentMap))
        }
        val activeEmail = _loggedInUserEmail.value.trim().lowercase()
        if (activeEmail.isNotBlank()) {
            _loggedInUserReferralCount.value = getUserReferralCount(activeEmail)
            _loggedInUserReferralEarnings.value = getUserReferralEarnings(activeEmail)
            _userWalletBalance.value = getUserWalletBalance(activeEmail)
        }
    }

    fun getReferredMembersForUser(
        referrerEmail: String,
        allOrders: List<OrderRecord>
    ): List<com.example.data.model.ReferredMemberSummary> {
        val cleanReferrer = referrerEmail.trim().lowercase()
        if (cleanReferrer.isBlank()) return emptyList()
        val myCode = getOrCreateUserReferralCode(cleanReferrer)
        if (myCode.isBlank()) return emptyList()

        val referredEmails = mutableSetOf<String>()
        val conf = _config.value

        // 1. From customerMetadata
        conf.customerMetadata.values.forEach { meta ->
            val mEmail = meta.email.trim().lowercase().ifBlank {
                if (meta.accountId.contains("@")) meta.accountId.trim().lowercase() else ""
            }
            if (mEmail.isNotBlank() &&
                !mEmail.equals(cleanReferrer, ignoreCase = true) &&
                meta.referredByCode.equals(myCode, ignoreCase = true)
            ) {
                referredEmails.add(mEmail)
            }
        }

        // 2. From referralRewardsHistory
        val myRewards = conf.referralRewardsHistory.filter {
            it.referrerEmail.equals(cleanReferrer, ignoreCase = true) ||
                it.referrerCode.equals(myCode, ignoreCase = true)
        }
        myRewards.forEach { rw ->
            val bEmail = rw.buyerEmail.trim().lowercase()
            if (bEmail.isNotBlank() && !bEmail.equals(cleanReferrer, ignoreCase = true)) {
                referredEmails.add(bEmail)
            }
        }

        // 3. From SharedPreferences acct_referred_by_*
        for ((k, v) in prefs.all) {
            if (k.startsWith("acct_referred_by_") && (v as? String)?.equals(myCode, ignoreCase = true) == true) {
                val mEmail = k.removePrefix("acct_referred_by_").trim().lowercase()
                if (mEmail.isNotBlank() && !mEmail.equals(cleanReferrer, ignoreCase = true)) {
                    referredEmails.add(mEmail)
                }
            }
        }

        return referredEmails.map { mEmail ->
            val meta = conf.customerMetadata[mEmail]
            val memberOrders = allOrders.filter { ord ->
                ord.customerEmail.equals(mEmail, ignoreCase = true) &&
                    !ord.category.contains("Wallet Top-Up", ignoreCase = true) &&
                    !ord.category.contains("Wallet Cashout", ignoreCase = true)
            }
            val memberRewards = myRewards.filter { it.buyerEmail.equals(mEmail, ignoreCase = true) }

            val resolvedName = meta?.displayName?.takeIf { it.isNotBlank() }
                ?: getSavedAccountName(mEmail).takeIf { it.isNotBlank() }
                ?: memberOrders.firstOrNull { it.customerName.isNotBlank() }?.customerName
                ?: memberRewards.firstOrNull { it.buyerName.isNotBlank() }?.buyerName
                ?: mEmail.substringBefore("@")

            val resolvedPhone = meta?.phone?.takeIf { it.isNotBlank() }
                ?: getSavedAccountPhone(mEmail).takeIf { it.isNotBlank() }
                ?: memberOrders.firstOrNull { it.customerPhone.isNotBlank() }?.customerPhone
                ?: ""

            val completedOrdersFromRoom = memberOrders.filter { it.status == "Completed" || it.status == "Approved" }
            val pendingOrdersFromRoom = memberOrders.filter { it.status == "Processing" || it.status == "Pending" }

            val completedCount = maxOf(completedOrdersFromRoom.size, memberRewards.map { it.orderNumber }.distinct().size)
            val pendingCount = pendingOrdersFromRoom.size
            val totalCount = maxOf(memberOrders.size, completedCount + pendingCount)

            val spentFromOrders = completedOrdersFromRoom.sumOf { it.priceBdt }
            val spentFromRewards = memberRewards.sumOf { it.orderPriceBdt }
            val totalSpent = maxOf(spentFromOrders, spentFromRewards)

            val totalBonus = memberRewards.sumOf { it.commissionBdt }
            val latestTs = maxOf(
                memberOrders.maxOfOrNull { it.timestamp } ?: 0L,
                memberRewards.maxOfOrNull { it.timestamp } ?: 0L
            )

            com.example.data.model.ReferredMemberSummary(
                memberEmail = mEmail,
                memberName = resolvedName,
                memberPhone = resolvedPhone,
                totalOrdersCount = totalCount,
                completedOrdersCount = completedCount,
                pendingOrdersCount = pendingCount,
                totalOrderSpentBdt = totalSpent,
                totalBonusEarnedBdt = totalBonus,
                lastActivityTimestamp = if (latestTs > 0L) latestTs else System.currentTimeMillis()
            )
        }.sortedWith(
            compareByDescending<com.example.data.model.ReferredMemberSummary> { it.totalBonusEarnedBdt }
                .thenByDescending { it.totalOrdersCount }
                .thenByDescending { it.lastActivityTimestamp }
        )
    }

    fun ensureFirebaseAuthMembersRestored(): List<CustomerMeta> {
        val knownAuthAccounts = listOf(
            Triple("raselalir975@gmail.com", "Rasel Ali", "uL5v3Ezh6xTeN357rUtyeH3eZ9g2"),
            Triple("subhanagency33@gmail.com", "Subhan Agency", "b1mP181cM9h2t1B1uX2oY156jO82"),
            Triple("johirulagency01833@gmail.com", "Johirul Agency", "058iM96P3rQ3i5G4a3s8V5G4A1E2"),
            Triple("johirul958423@gmail.com", "Johirul Official (Admin)", "b3B6S2b3G6N9E7F1s1A2M8C5z9R2")
        )
        val currentMap = _config.value.customerMetadata.toMutableMap()
        val restoredList = mutableListOf<CustomerMeta>()
        var changed = false

        knownAuthAccounts.forEach { (rawEmail, defaultName, _) ->
            val email = rawEmail.trim().lowercase()
            val existing = currentMap[email]
            val savedName = getSavedAccountName(email).ifBlank { existing?.displayName ?: defaultName }
            val savedPhone = getSavedAccountPhone(email).ifBlank { existing?.phone ?: "" }
            val savedPhoto = getSavedAccountPhotoUri(email).ifBlank { existing?.photoUri ?: "" }
            val walletBal = getUserWalletBalance(email).coerceAtLeast(existing?.walletBalanceBdt ?: 0.0)
            val refCode = getOrCreateUserReferralCode(email).ifBlank { existing?.referralCode ?: "" }
            val refBy = getUserReferredByCode(email).ifBlank { existing?.referredByCode ?: "" }
            val refEarnings = getUserReferralEarnings(email).coerceAtLeast(existing?.referralEarningsBdt ?: 0.0)
            val refCount = getUserReferralCount(email).coerceAtLeast(existing?.referralCount ?: 0)

            val updatedMeta = CustomerMeta(
                accountId = email,
                displayName = savedName,
                email = email,
                phone = savedPhone,
                photoUri = savedPhoto,
                walletBalanceBdt = walletBal,
                referralCode = refCode,
                referredByCode = refBy,
                referralEarningsBdt = refEarnings,
                referralCount = refCount,
                isVip = existing?.isVip ?: (email == "johirul958423@gmail.com"),
                isBlocked = existing?.isBlocked ?: false,
                adminNote = existing?.adminNote ?: ""
            )
            if (currentMap[email] != updatedMeta) {
                currentMap[email] = updatedMeta
                changed = true
            }
            restoredList.add(updatedMeta)
        }

        val activeEmail = _loggedInUserEmail.value.trim().lowercase()
        if (activeEmail.isNotBlank() && currentMap[activeEmail] == null) {
            val activeMeta = CustomerMeta(
                accountId = activeEmail,
                displayName = _loggedInUserName.value.ifBlank { activeEmail.substringBefore("@") },
                email = activeEmail,
                phone = _loggedInUserPhone.value,
                photoUri = _loggedInUserPhotoUri.value ?: "",
                walletBalanceBdt = getUserWalletBalance(activeEmail),
                referralCode = getOrCreateUserReferralCode(activeEmail),
                referredByCode = getUserReferredByCode(activeEmail),
                referralEarningsBdt = getUserReferralEarnings(activeEmail),
                referralCount = getUserReferralCount(activeEmail)
            )
            currentMap[activeEmail] = activeMeta
            restoredList.add(activeMeta)
            changed = true
        }

        if (changed) {
            updateConfig(_config.value.copy(customerMetadata = currentMap))
        }
        return restoredList
    }

    fun clearAllCustomerMetadata() {
        updateConfig(_config.value.copy(customerMetadata = emptyMap()))
    }

    fun isDemoDataPurged(): Boolean {
        return true
    }

    fun markDemoDataPurged(cutoffTimestamp: Long = 0L) {
        prefs.edit()
            .putBoolean("clean_demo_data_wipe_v5", true)
            .putLong("demo_purge_cutoff_ts", 0L)
            .apply()
    }

    fun getDemoPurgeCutoffTimestamp(): Long {
        return 0L
    }

    fun setDemoPurgeCutoffTimestamp(cutoffTimestamp: Long) {
        prefs.edit().putLong("demo_purge_cutoff_ts", cutoffTimestamp).apply()
    }

    fun updateExchangeRates(
        epayBase: Double,
        epay970: Double,
        epay10: Double,
        epay30: Double,
        epay500: Double,
        inrBase: Double,
        tiktokCoin: Double,
        chametDiamond: Double
    ) {
        updateConfig(
            _config.value.copy(
                epayBaseRate = epayBase,
                epayRate970Fixed = epay970,
                epayRate10Fixed = epay10,
                epayRate30Bulk = epay30,
                epayRate500Wholesale = epay500,
                inrBaseRate = inrBase,
                tiktokRatePerCoin = tiktokCoin,
                chametRatePerDiamond = chametDiamond
            )
        )
    }

    fun getPaymentAccounts(): List<PaymentAccount> {
        val conf = _config.value
        val list = mutableListOf(
            PaymentAccount(
                method = "bKash Personal",
                number = conf.bkashNumber,
                type = conf.bkashType,
                instruction = conf.bkashInstruction,
                colorHex = 0xFFD8226A
            ),
            PaymentAccount(
                method = "Nagad Personal",
                number = conf.nagadNumber,
                type = conf.nagadType,
                instruction = conf.nagadInstruction,
                colorHex = 0xFFF7941D
            )
        )
        if (conf.epayEmail.isNotBlank()) {
            list.add(
                PaymentAccount(
                    method = "EPAY Global Wallet",
                    number = conf.epayEmail,
                    type = "Official Epay Email",
                    instruction = "আপনার Epay ওয়ালেট থেকে সরাসরি এই ইমেইলে ডলার পাঠান।",
                    colorHex = 0xFF0073BC
                )
            )
        }
        if (conf.isRocketEnabled) {
            list.add(
                PaymentAccount(
                    method = "Rocket Personal",
                    number = conf.rocketNumber,
                    type = "Personal (Send Money)",
                    instruction = "রকেট অ্যাপ থেকে Send Money করুন এবং TrxID দিন।",
                    colorHex = 0xFF8C2686
                )
            )
        }
        return list
    }

    private val _notifications = MutableStateFlow(loadNotifications())
    val notifications: StateFlow<List<OrderNotification>> = _notifications.asStateFlow()

    private fun loadNotifications(): List<OrderNotification> {
        val json = prefs.getString("order_notifications_json", "[]") ?: "[]"
        val list = mutableListOf<OrderNotification>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    OrderNotification(
                        id = obj.optString("id", "notif_$i"),
                        orderNumber = obj.optString("orderNumber", ""),
                        title = obj.optString("title", ""),
                        message = obj.optString("message", ""),
                        status = obj.optString("status", "Processing"),
                        trxId = obj.optString("trxId", ""),
                        itemName = obj.optString("itemName", ""),
                        priceBdt = obj.optDouble("priceBdt", 0.0),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isRead = obj.optBoolean("isRead", false),
                        customerEmail = obj.optString("customerEmail", "").trim().lowercase()
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list.sortedByDescending { it.timestamp }
    }

    private fun saveNotifications(list: List<OrderNotification>) {
        val arr = JSONArray()
        list.take(100).forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("orderNumber", item.orderNumber)
                put("title", item.title)
                put("message", item.message)
                put("status", item.status)
                put("trxId", item.trxId)
                put("itemName", item.itemName)
                put("priceBdt", item.priceBdt)
                put("timestamp", item.timestamp)
                put("isRead", item.isRead)
                put("customerEmail", item.customerEmail.trim().lowercase())
            }
            arr.put(obj)
        }
        prefs.edit().putString("order_notifications_json", arr.toString()).apply()
        _notifications.value = list.take(100)
    }

    fun pushOrderStatusNotification(order: OrderRecord, newStatus: String) {
        val (title, msg) = when (newStatus) {
            "Completed", "Approved" -> {
                "✅ অর্ডার সম্পন্ন হয়েছে! (#${order.orderNumber})" to
                    "অভিনন্দন! আপনার অর্ডার #${order.orderNumber} (${order.itemName} - ${order.amountOrQuantity}) সফলভাবে সম্পন্ন (Approved) করা হয়েছে।"
            }
            "Cancelled" -> {
                val trxPart = if (order.transactionTrxId.isNotBlank()) " (TrxID: ${order.transactionTrxId})" else ""
                "❌ ভুল TrxID / বাতিল (#${order.orderNumber})" to
                    "দুঃখিত! আপনার অর্ডার #${order.orderNumber}$trxPart যাচাই করে ভুল TrxID হিসেবে চিহ্নিত করা হয়েছে। সঠিক TrxID বা স্ক্রিনশট দিয়ে সাপোর্টে যোগাযোগ করুন।"
            }
            else -> {
                "⏳ অর্ডার প্রসেসিং চলছে (#${order.orderNumber})" to
                    "আপনার অর্ডার #${order.orderNumber} (${order.itemName}) বর্তমানে প্রসেসিংয়ে (Processing) রয়েছে। সম্পন্ন হলে এখানে মেসেজ চলে আসবে।"
            }
        }

        val orderEmail = order.customerEmail.trim().lowercase()
        val notif = OrderNotification(
            id = "notif_${order.orderNumber}_${System.currentTimeMillis()}",
            orderNumber = order.orderNumber,
            title = title,
            message = msg,
            status = newStatus,
            trxId = order.transactionTrxId,
            itemName = "${order.itemName} (${order.amountOrQuantity})",
            priceBdt = order.priceBdt,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            customerEmail = orderEmail
        )

        val existingForOrder = _notifications.value.find { it.orderNumber == order.orderNumber }
        val isStatusChanged = existingForOrder == null || existingForOrder.status != newStatus

        val updated = mutableListOf(notif).apply {
            addAll(_notifications.value.filterNot { it.orderNumber == order.orderNumber })
        }
        saveNotifications(updated)

        // Strictly only show banner / system alert if this order belongs to the currently logged-in user
        val activeUserEmail = _loggedInUserEmail.value.trim().lowercase()
        if (activeUserEmail.isNotEmpty() && orderEmail == activeUserEmail) {
            _latestBannerNotification.value = notif
            if (isStatusChanged) {
                showSystemOrderNotification(notif)
            }
        }
    }

    fun syncCloudNotification(
        orderNumber: String,
        status: String,
        itemName: String,
        priceBdt: Double,
        trxId: String,
        timestamp: Long,
        customerEmail: String = ""
    ) {
        if (orderNumber.isBlank()) return
        val existing = _notifications.value.find { it.orderNumber == orderNumber }
        val resolvedEmail = customerEmail.trim().lowercase().ifBlank {
            existing?.customerEmail?.trim()?.lowercase() ?: ""
        }
        if (existing != null && existing.status == status && existing.customerEmail == resolvedEmail) return

        val (title, msg) = when (status) {
            "Completed", "Approved" -> {
                "✅ অর্ডার সম্পন্ন হয়েছে! (#$orderNumber)" to
                    "অভিনন্দন! আপনার অর্ডার #$orderNumber ($itemName) সফলভাবে সম্পন্ন (Approved) করা হয়েছে।"
            }
            "Cancelled" -> {
                val trxPart = if (trxId.isNotBlank()) " (TrxID: $trxId)" else ""
                "❌ ভুল TrxID / বাতিল (#$orderNumber)" to
                    "দুঃখিত! আপনার অর্ডার #$orderNumber$trxPart বাতিল বা ভুল TrxID হিসেবে চিহ্নিত করা হয়েছে।"
            }
            else -> {
                "⏳ অর্ডার প্রসেসিং চলছে (#$orderNumber)" to
                    "আপনার অর্ডার #$orderNumber ($itemName) প্রসেসিংয়ে রয়েছে। সম্পন্ন হলে নোটিফিকেশন পাবেন।"
            }
        }

        val notif = OrderNotification(
            id = "notif_${orderNumber}_$timestamp",
            orderNumber = orderNumber,
            title = title,
            message = msg,
            status = status,
            trxId = trxId,
            itemName = itemName,
            priceBdt = priceBdt,
            timestamp = timestamp,
            isRead = false,
            customerEmail = resolvedEmail
        )

        val updated = mutableListOf(notif).apply {
            addAll(_notifications.value.filterNot { it.orderNumber == orderNumber })
        }
        saveNotifications(updated)

        val activeUserEmail = _loggedInUserEmail.value.trim().lowercase()
        val belongsToActiveUser = activeUserEmail.isNotEmpty() && resolvedEmail == activeUserEmail
        if (belongsToActiveUser && (existing != null || System.currentTimeMillis() - timestamp < 120_000L)) {
            _latestBannerNotification.value = notif
            showSystemOrderNotification(notif)
        }
    }

    fun markAllNotificationsRead(userEmail: String = _loggedInUserEmail.value) {
        val cleanEmail = userEmail.trim().lowercase()
        if (cleanEmail.isEmpty()) return
        val updated = _notifications.value.map {
            if (it.customerEmail.trim().lowercase() == cleanEmail || it.customerEmail.isBlank()) {
                it.copy(isRead = true)
            } else {
                it
            }
        }
        saveNotifications(updated)
    }

    fun markSingleNotificationRead(notificationId: String) {
        val updated = _notifications.value.map {
            if (it.id == notificationId || it.orderNumber == notificationId) it.copy(isRead = true) else it
        }
        saveNotifications(updated)
    }

    fun pushCustomNotification(
        customerEmail: String,
        title: String,
        message: String,
        status: String = "Completed",
        priceBdt: Double = 0.0,
        referenceId: String = "ALERT-${System.currentTimeMillis().toString().takeLast(5)}"
    ) {
        val cleanEmail = customerEmail.trim().lowercase()
        val notif = OrderNotification(
            id = "notif_${referenceId}_${System.currentTimeMillis()}",
            orderNumber = referenceId,
            title = title,
            message = message,
            status = status,
            trxId = "",
            itemName = title,
            priceBdt = priceBdt,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            customerEmail = cleanEmail
        )
        val updated = listOf(notif) + _notifications.value
        saveNotifications(updated)
        val activeEmail = _loggedInUserEmail.value.trim().lowercase()
        if (cleanEmail.isBlank() || (activeEmail.isNotBlank() && activeEmail == cleanEmail)) {
            _latestBannerNotification.value = notif
            showSystemOrderNotification(notif)
        }
    }

    fun getSavedServiceAccounts(email: String): List<SavedServiceAccount> {
        val key = email.trim().lowercase()
        if (key.isBlank()) return emptyList()
        val json = prefs.getString("saved_service_accounts_$key", null)
        val list = mutableListOf<SavedServiceAccount>()
        if (!json.isNullOrBlank()) {
            try {
                val arr = JSONArray(json)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val accId = obj.optString("accountId", "").trim()
                    if (accId.isNotBlank()) {
                        list.add(
                            SavedServiceAccount(
                                id = obj.optString("id", "sa_$i"),
                                userEmail = key,
                                serviceName = obj.optString("serviceName", "CHAMET"),
                                accountId = accId,
                                label = obj.optString("label", ""),
                                isDefault = obj.optBoolean("isDefault", false),
                                updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                            )
                        )
                    }
                }
            } catch (_: Exception) {
            }
        }
        // Migrate legacy single default UIDs if list is empty
        if (list.isEmpty()) {
            listOf("CHAMET", "TIKTOK", "FREEFIRE").forEach { cat ->
                val legacyUid = getDefaultServiceUid(key, cat)
                if (legacyUid.isNotBlank()) {
                    list.add(
                        SavedServiceAccount(
                            id = "legacy_${cat.lowercase()}",
                            userEmail = key,
                            serviceName = cat,
                            accountId = legacyUid,
                            label = "Primary $cat ID",
                            isDefault = true,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
            if (list.isNotEmpty()) {
                persistSavedServiceAccounts(key, list)
            }
        }
        return list.sortedWith(compareByDescending<SavedServiceAccount> { it.isDefault }.thenByDescending { it.updatedAt })
    }

    private fun persistSavedServiceAccounts(email: String, list: List<SavedServiceAccount>) {
        val key = email.trim().lowercase()
        val arr = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("userEmail", key)
                put("serviceName", item.serviceName)
                put("accountId", item.accountId)
                put("label", item.label)
                put("isDefault", item.isDefault)
                put("updatedAt", item.updatedAt)
            }
            arr.put(obj)
        }
        prefs.edit().putString("saved_service_accounts_$key", arr.toString()).apply()
    }

    fun saveOrUpdateServiceAccount(
        email: String,
        serviceName: String,
        accountId: String,
        label: String = "",
        isDefault: Boolean = true,
        editId: String? = null
    ): List<SavedServiceAccount> {
        val key = email.trim().lowercase()
        val cleanAcc = accountId.trim()
        if (key.isBlank() || cleanAcc.isBlank()) return getSavedServiceAccounts(key)
        val normService = serviceName.trim().uppercase()
        val current = getSavedServiceAccounts(key).toMutableList()

        val shouldBeDefault = isDefault || current.none { it.serviceName.equals(normService, ignoreCase = true) }
        if (shouldBeDefault) {
            for (i in current.indices) {
                if (current[i].serviceName.equals(normService, ignoreCase = true)) {
                    current[i] = current[i].copy(isDefault = false)
                }
            }
            saveDefaultServiceUid(key, normService, cleanAcc)
        }

        val now = System.currentTimeMillis()
        if (!editId.isNullOrBlank()) {
            val idx = current.indexOfFirst { it.id == editId }
            if (idx >= 0) {
                current[idx] = current[idx].copy(
                    serviceName = normService,
                    accountId = cleanAcc,
                    label = label.trim(),
                    isDefault = shouldBeDefault,
                    updatedAt = now
                )
            } else {
                current.add(
                    0,
                    SavedServiceAccount(
                        id = editId,
                        userEmail = key,
                        serviceName = normService,
                        accountId = cleanAcc,
                        label = label.trim(),
                        isDefault = shouldBeDefault,
                        updatedAt = now
                    )
                )
            }
        } else {
            val existingIdx = current.indexOfFirst {
                it.serviceName.equals(normService, ignoreCase = true) && it.accountId.equals(cleanAcc, ignoreCase = true)
            }
            if (existingIdx >= 0) {
                current[existingIdx] = current[existingIdx].copy(
                    label = label.trim().ifBlank { current[existingIdx].label },
                    isDefault = shouldBeDefault,
                    updatedAt = now
                )
            } else {
                current.add(
                    0,
                    SavedServiceAccount(
                        id = "sa_${now}_${(100..999).random()}",
                        userEmail = key,
                        serviceName = normService,
                        accountId = cleanAcc,
                        label = label.trim(),
                        isDefault = shouldBeDefault,
                        updatedAt = now
                    )
                )
            }
        }
        persistSavedServiceAccounts(key, current)
        return getSavedServiceAccounts(key)
    }

    fun deleteSavedServiceAccount(email: String, itemId: String): List<SavedServiceAccount> {
        val key = email.trim().lowercase()
        if (key.isBlank()) return emptyList()
        val current = getSavedServiceAccounts(key).toMutableList()
        val removed = current.firstOrNull { it.id == itemId }
        current.removeAll { it.id == itemId }
        if (removed != null && removed.isDefault) {
            val nextForService = current.indexOfFirst { it.serviceName.equals(removed.serviceName, ignoreCase = true) }
            if (nextForService >= 0) {
                current[nextForService] = current[nextForService].copy(isDefault = true)
                saveDefaultServiceUid(key, removed.serviceName, current[nextForService].accountId)
            } else {
                saveDefaultServiceUid(key, removed.serviceName, "")
            }
        }
        persistSavedServiceAccounts(key, current)
        return getSavedServiceAccounts(key)
    }

    fun setDefaultSavedServiceAccount(email: String, itemId: String): List<SavedServiceAccount> {
        val key = email.trim().lowercase()
        if (key.isBlank()) return emptyList()
        val current = getSavedServiceAccounts(key).toMutableList()
        val target = current.firstOrNull { it.id == itemId } ?: return current
        for (i in current.indices) {
            if (current[i].serviceName.equals(target.serviceName, ignoreCase = true)) {
                current[i] = current[i].copy(isDefault = current[i].id == itemId)
            }
        }
        saveDefaultServiceUid(key, target.serviceName, target.accountId)
        persistSavedServiceAccounts(key, current)
        return getSavedServiceAccounts(key)
    }

    fun clearNotificationsForUser(userEmail: String = _loggedInUserEmail.value) {
        val cleanEmail = userEmail.trim().lowercase()
        _latestBannerNotification.value = null
        if (cleanEmail.isEmpty()) return
        val now = System.currentTimeMillis()
        prefs.edit().putLong("user_notif_cleared_ts_$cleanEmail", now).apply()
        _userClearedNotifTimestamp.value = now
        val updated = _notifications.value.filterNot { it.customerEmail.trim().lowercase() == cleanEmail }
        saveNotifications(updated)
    }

    fun clearAllNotifications() {
        _latestBannerNotification.value = null
        saveNotifications(emptyList())
    }

    fun removeNotificationByOrderNumber(orderNumber: String) {
        val updated = _notifications.value.filterNot { it.orderNumber == orderNumber }
        saveNotifications(updated)
    }

    fun getDefaultServiceUid(email: String, categoryName: String): String {
        val key = email.trim().lowercase()
        return prefs.getString("saved_default_uid_${key}_$categoryName", "") ?: ""
    }

    fun saveDefaultServiceUid(email: String, categoryName: String, uid: String) {
        val key = email.trim().lowercase()
        prefs.edit().putString("saved_default_uid_${key}_$categoryName", uid.trim()).apply()
    }
}
