package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdminConfig
import com.example.data.local.AdminSettingsManager
import com.example.data.local.TopupDatabase
import com.example.data.local.TopupRepository
import com.example.data.local.UserTransactionRepository
import com.example.data.model.CustomerProfile
import com.example.data.model.EpayRateTier
import com.example.data.model.OrderRecord
import com.example.data.model.PaymentAccount
import com.example.data.model.ServiceCategory
import com.example.data.model.SupportChatMessage
import com.example.data.model.TopupPackage
import com.example.data.model.TransactionStatus
import com.example.data.model.UserTransactionRecord
import com.example.data.remote.FirebaseCloudRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppNavTab(val title: String, val iconEmoji: String) {
    HOME("Home", "🏠"),
    PROFILE("Profile", "👤"),
    TIKTOK("TikTok", "🪙"),
    CHAMET("Chamet", "💎"),
    FREEFIRE("Gaming", "🔥"),
    EPAY("EPAY", "💸"),
    EXCHANGE("Exchange", "🔄"),
    CALCULATOR("Calculate", "🧮"),
    SOCIAL_BOOST("Social Boost", "🚀"),
    SUPPORT("Support", "💬"),
    ADMIN("Admin", "⚡")
}

data class OrderFormState(
    val packageItem: TopupPackage? = null,
    val userAccountId: String = "",
    val userName: String = "",
    val paymentMethod: String = "bKash Personal",
    val transactionTrxId: String = "",
    val orderNote: String = "",
    val saveUidForFuture: Boolean = true
)

class TopupViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TopupRepository
    val userTransactionRepository: UserTransactionRepository
    val adminSettings: AdminSettingsManager = AdminSettingsManager(application)
    val cloudRepository: FirebaseCloudRepository = FirebaseCloudRepository(application)
    val adminConfig: StateFlow<AdminConfig> = adminSettings.config
    val notifications = adminSettings.notifications
    val latestBannerNotification = adminSettings.latestBannerNotification
    val userClearedNotifTimestamp: StateFlow<Long> = adminSettings.userClearedNotifTimestamp
    val loggedInUserEmail: StateFlow<String> = adminSettings.loggedInUserEmail
    val loggedInUserName: StateFlow<String> = adminSettings.loggedInUserName
    val loggedInUserPhone: StateFlow<String> = adminSettings.loggedInUserPhone
    val loggedInUserPhotoUri: StateFlow<String> = adminSettings.loggedInUserPhotoUri
    val loggedInUserReferralCode: StateFlow<String> = adminSettings.loggedInUserReferralCode
    val loggedInUserReferredByCode: StateFlow<String> = adminSettings.loggedInUserReferredByCode
    val loggedInUserReferralEarnings: StateFlow<String>
        get() = throw UnsupportedOperationException()
    val userReferralEarnings: StateFlow<Double> = adminSettings.loggedInUserReferralEarnings
    val userReferralCount: StateFlow<Int> = adminSettings.loggedInUserReferralCount
    val supportChatUserName: StateFlow<String> = adminSettings.supportChatUserName
    val supportChatUserPhone: StateFlow<String> = adminSettings.supportChatUserPhone
    val supportChatUserEmail: StateFlow<String> = adminSettings.supportChatUserEmail

    private val _showUserProfileFullScreen = MutableStateFlow(false)
    val showUserProfileFullScreen: StateFlow<Boolean> = _showUserProfileFullScreen.asStateFlow()

    fun openUserProfileFullScreen() {
        _showUserProfileFullScreen.value = true
    }

    fun closeUserProfileFullScreen() {
        _showUserProfileFullScreen.value = false
    }
    private val autoTopupService by lazy { com.example.data.remote.AutoTopupService() }
    private val supportAiResponder by lazy { com.example.data.remote.SupportAiAutoResponder() }
    private val _isAiTypingSupportReply = MutableStateFlow(false)
    val isAiTypingSupportReply: StateFlow<Boolean> = _isAiTypingSupportReply.asStateFlow()
    private var pendingPackageAfterAuth: TopupPackage? = null
    private var pendingAccountIdAfterAuth: String = ""
    private var pendingOrderModalAfterAuth: String? = null

    val orders: StateFlow<List<OrderRecord>>
    val userTransactions: StateFlow<List<UserTransactionRecord>>

    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Admin & Customer Auth State
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _showAdminLoginDialog = MutableStateFlow(false)
    val showAdminLoginDialog: StateFlow<Boolean> = _showAdminLoginDialog.asStateFlow()

    private val _authDialogStartInRegister = MutableStateFlow(false)
    val authDialogStartInRegister: StateFlow<Boolean> = _authDialogStartInRegister.asStateFlow()

    private val _firebaseUserEmail = MutableStateFlow<String?>(
        adminSettings.loggedInUserEmail.value.takeIf { it.isNotBlank() }
    )
    val firebaseUserEmail: StateFlow<String?> = _firebaseUserEmail.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

    // App Launch Notice (জরুরি নিয়মাবলী) - Shown ONLY when the app launches / opens
    private val _showAppLaunchNotice = MutableStateFlow(adminSettings.config.value.noticeShowOnLaunch)
    val showAppLaunchNotice: StateFlow<Boolean> = _showAppLaunchNotice.asStateFlow()

    // Full-Screen Notifications Screen - Opened via top notification icon
    private val _showNotificationScreen = MutableStateFlow(false)
    val showNotificationScreen: StateFlow<Boolean> = _showNotificationScreen.asStateFlow()

    // Backward-compatible alias
    val showNoticeDialog: StateFlow<Boolean> = _showNotificationScreen.asStateFlow()
    private val _noticeDialogTab = MutableStateFlow(0)
    val noticeDialogTab: StateFlow<Int> = _noticeDialogTab.asStateFlow()

    private val _showPaymentAccountsSheet = MutableStateFlow(false)
    val showPaymentAccountsSheet: StateFlow<Boolean> = _showPaymentAccountsSheet.asStateFlow()

    private val _orderForm = MutableStateFlow<OrderFormState?>(null)
    val orderForm: StateFlow<OrderFormState?> = _orderForm.asStateFlow()

    private val _selectedPackageForChooser = MutableStateFlow<TopupPackage?>(null)
    val selectedPackageForChooser: StateFlow<TopupPackage?> = _selectedPackageForChooser.asStateFlow()

    private val _selectedPackageForGateway = MutableStateFlow<TopupPackage?>(null)
    val selectedPackageForGateway: StateFlow<TopupPackage?> = _selectedPackageForGateway.asStateFlow()

    private val _savedUidForCurrentCategory = MutableStateFlow<String>("")
    val savedUidForCurrentCategory: StateFlow<String> = _savedUidForCurrentCategory.asStateFlow()

    private val _selectedOrderForReceipt = MutableStateFlow<OrderRecord?>(null)
    val selectedOrderForReceipt: StateFlow<OrderRecord?> = _selectedOrderForReceipt.asStateFlow()

    private val _showEpayOrderDialog = MutableStateFlow(false)
    val showEpayOrderDialog: StateFlow<Boolean> = _showEpayOrderDialog.asStateFlow()

    private val _showExchangeOrderDialog = MutableStateFlow(false)
    val showExchangeOrderDialog: StateFlow<Boolean> = _showExchangeOrderDialog.asStateFlow()

    // Interactive EPAY Calculator State
    private val _epayUsdInput = MutableStateFlow("10")
    val epayUsdInput: StateFlow<String> = _epayUsdInput.asStateFlow()

    // Interactive BD -> India Exchange State
    private val _exchangeSendMethod = MutableStateFlow("bKash")
    val exchangeSendMethod: StateFlow<String> = _exchangeSendMethod.asStateFlow()

    private val _exchangeReceiveMethod = MutableStateFlow("PhonePe (India)")
    val exchangeReceiveMethod: StateFlow<String> = _exchangeReceiveMethod.asStateFlow()

    private val _exchangeAmountBdt = MutableStateFlow("1200")
    val exchangeAmountBdt: StateFlow<String> = _exchangeAmountBdt.asStateFlow()

    private val _exchangeRecipientUpi = MutableStateFlow("")
    val exchangeRecipientUpi: StateFlow<String> = _exchangeRecipientUpi.asStateFlow()

    // Interactive Custom TikTok & Chamet Calculator
    private val _customTikTokCoins = MutableStateFlow("500")
    val customTikTokCoins: StateFlow<String> = _customTikTokCoins.asStateFlow()

    private val _customChametDiamonds = MutableStateFlow("50000")
    val customChametDiamonds: StateFlow<String> = _customChametDiamonds.asStateFlow()

    val userWalletBalance: StateFlow<Double> = adminSettings.userWalletBalance

    // Live Permanent Room + Firebase Firestore Support Chat State
    val supportMessages: StateFlow<List<SupportChatMessage>>

    private val _showSupportChatDialog = MutableStateFlow(false)
    val showSupportChatDialog: StateFlow<Boolean> = _showSupportChatDialog.asStateFlow()

    private val _isSendingSupportMessage = MutableStateFlow(false)
    val isSendingSupportMessage: StateFlow<Boolean> = _isSendingSupportMessage.asStateFlow()

    init {
        val db = TopupDatabase.getDatabase(application)
        repository = TopupRepository(db.topupDao())
        userTransactionRepository = UserTransactionRepository(db.userTransactionDao())
        orders = repository.allOrders.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        userTransactions = userTransactionRepository.allTransactions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        supportMessages = repository.allSupportMessages.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Start Real-Time Cloud Firestore Sync & Auth Check off the Main Thread
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.clearSavedUids()
            val legacyDemoIds = setOf("EX-90412", "EP-77319", "EX-84195")
            kotlinx.coroutines.delay(100)
            orders.value.filter { it.orderNumber in legacyDemoIds }.forEach { demoOrd ->
                repository.deleteOrder(demoOrd)
                adminSettings.removeNotificationByOrderNumber(demoOrd.orderNumber)
            }

            cloudRepository.startRealtimeSync(
                currentConfigProvider = { adminConfig.value },
                onOrdersUpdatedFromCloud = { cloudOrders ->
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        val localMap = orders.value.associateBy { it.orderNumber }
                        cloudOrders.forEach { cloudOrd ->
                            if (cloudOrd.orderNumber in legacyDemoIds) {
                                return@forEach
                            }
                            val existing = localMap[cloudOrd.orderNumber]
                            if (existing == null) {
                                repository.insertOrder(cloudOrd.copy(id = 0))
                            } else if (
                                existing.status != cloudOrd.status ||
                                existing.orderNote != cloudOrd.orderNote ||
                                (existing.customerEmail.isBlank() && cloudOrd.customerEmail.isNotBlank())
                            ) {
                                val updatedLocal = existing.copy(
                                    status = cloudOrd.status,
                                    orderNote = cloudOrd.orderNote,
                                    customerEmail = cloudOrd.customerEmail.ifBlank { existing.customerEmail },
                                    customerPhone = cloudOrd.customerPhone.ifBlank { existing.customerPhone },
                                    customerName = cloudOrd.customerName.ifBlank { existing.customerName }
                                )
                                repository.updateOrder(updatedLocal)
                                if (existing.status != cloudOrd.status) {
                                    adminSettings.pushOrderStatusNotification(updatedLocal, cloudOrd.status)
                                    if (cloudOrd.status == "Completed" || cloudOrd.status == "Approved") {
                                        adminSettings.processOrderCompletionRewards(updatedLocal)
                                    }
                                }
                            }
                        }
                    }
                },
                onConfigUpdatedFromCloud = { syncedConfig ->
                    adminSettings.updateConfig(syncedConfig)
                },
                onNotificationFromCloud = { orderNumber, status, itemName, priceBdt, trxId, timestamp, customerEmail ->
                    val resolvedEmail = customerEmail.ifBlank {
                        orders.value.find { it.orderNumber == orderNumber }?.customerEmail ?: ""
                    }
                    adminSettings.syncCloudNotification(
                        orderNumber = orderNumber,
                        status = status,
                        itemName = itemName,
                        priceBdt = priceBdt,
                        trxId = trxId,
                        timestamp = timestamp,
                        customerEmail = resolvedEmail
                    )
                },
                onSupportMessagesUpdatedFromCloud = { messages ->
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        if (messages.isNotEmpty()) {
                            repository.insertSupportMessages(messages)
                        }
                    }
                },
                onCustomersUpdatedFromCloud = { cloudCustomers ->
                    adminSettings.mergeCustomersFromCloud(cloudCustomers)
                }
            )

            // Restore & sync registered Firebase Authentication accounts into local metadata and Cloud Firestore
            val restoredAuthMembers = adminSettings.ensureFirebaseAuthMembersRestored()
            cloudRepository.restoreAndSyncCustomersToCloud(restoredAuthMembers) { fetchedCustomers ->
                adminSettings.mergeCustomersFromCloud(fetchedCustomers)
            }

            // Check if Firebase Auth has an active user or admin session
            try {
                val currentAuthUser = cloudRepository.getFirebaseAuthOrNull()?.currentUser
                if (currentAuthUser != null && !currentAuthUser.email.isNullOrBlank()) {
                    val email = currentAuthUser.email!!.trim().lowercase()
                    val (savedName, savedPhone) = adminSettings.getUserAccountProfile(email)
                    val name = currentAuthUser.displayName?.takeIf { it.isNotBlank() }
                        ?: savedName.ifBlank { email.substringBefore("@") }
                    val phone = savedPhone
                    _firebaseUserEmail.value = email
                    adminSettings.setLoggedInUser(email, name, phone)
                    cloudRepository.fetchFullCustomerCloudProfileByEmail(email) { cloudName, cloudPhone, cloudPhoto, cloudWallet, cloudRefCode, cloudReferredBy, cloudRefEarnings, cloudRefCount ->
                        val resolvedName = cloudName.ifBlank { name }
                        val resolvedPhone = cloudPhone.ifBlank { phone }
                        adminSettings.setLoggedInUser(email, resolvedName, resolvedPhone, cloudPhoto.takeIf { it.isNotBlank() })
                        adminSettings.syncCloudUserWalletAndReferral(
                            email = email,
                            cloudWalletBalance = cloudWallet,
                            cloudReferralCode = cloudRefCode,
                            cloudReferredBy = cloudReferredBy,
                            cloudReferralEarnings = cloudRefEarnings,
                            cloudReferralCount = cloudRefCount
                        )
                        // Always ensure active authenticated user's profile is present in Firestore `customers` collection
                        cloudRepository.upsertAuthenticatedUserToCloud(
                            uid = currentAuthUser.uid,
                            email = email,
                            displayName = resolvedName,
                            phone = resolvedPhone,
                            photoUri = cloudPhoto.ifBlank { adminSettings.getSavedAccountPhotoUri(email) },
                            walletBalanceBdt = adminSettings.getUserWalletBalance(email),
                            referralCode = adminSettings.getOrCreateUserReferralCode(email),
                            referredByCode = adminSettings.getUserReferredByCode(email),
                            referralEarningsBdt = adminSettings.getUserReferralEarnings(email),
                            referralCount = adminSettings.getUserReferralCount(email)
                        )
                    }
                    if (isAuthorizedAdminEmail(email)) {
                        _isAdminLoggedIn.value = true
                    }
                } else if (adminSettings.loggedInUserEmail.value.isNotBlank()) {
                    val savedEmail = adminSettings.loggedInUserEmail.value.trim().lowercase()
                    val (savedName, savedPhone) = adminSettings.getUserAccountProfile(savedEmail)
                    _firebaseUserEmail.value = savedEmail
                    cloudRepository.fetchFullCustomerCloudProfileByEmail(savedEmail) { cloudName, cloudPhone, cloudPhoto, cloudWallet, cloudRefCode, cloudReferredBy, cloudRefEarnings, cloudRefCount ->
                        val resolvedName = cloudName.ifBlank { savedName.ifBlank { savedEmail.substringBefore("@") } }
                        val resolvedPhone = cloudPhone.ifBlank { savedPhone }
                        adminSettings.setLoggedInUser(savedEmail, resolvedName, resolvedPhone, cloudPhoto.takeIf { it.isNotBlank() })
                        adminSettings.syncCloudUserWalletAndReferral(
                            email = savedEmail,
                            cloudWalletBalance = cloudWallet,
                            cloudReferralCode = cloudRefCode,
                            cloudReferredBy = cloudReferredBy,
                            cloudReferralEarnings = cloudRefEarnings,
                            cloudReferralCount = cloudRefCount
                        )
                        cloudRepository.upsertAuthenticatedUserToCloud(
                            uid = "",
                            email = savedEmail,
                            displayName = resolvedName,
                            phone = resolvedPhone,
                            photoUri = cloudPhoto.ifBlank { adminSettings.getSavedAccountPhotoUri(savedEmail) },
                            walletBalanceBdt = adminSettings.getUserWalletBalance(savedEmail),
                            referralCode = adminSettings.getOrCreateUserReferralCode(savedEmail),
                            referredByCode = adminSettings.getUserReferredByCode(savedEmail),
                            referralEarningsBdt = adminSettings.getUserReferralEarnings(savedEmail),
                            referralCount = adminSettings.getUserReferralCount(savedEmail)
                        )
                    }
                    if (isAuthorizedAdminEmail(savedEmail)) {
                        _isAdminLoggedIn.value = true
                    }
                }
            } catch (_: Exception) {
            }

            kotlinx.coroutines.delay(400)
            orders.value
                .filter { it.orderNumber !in legacyDemoIds }
                .forEach { cloudRepository.uploadOrderToCloud(it) }
            supportMessages.value.forEach { msg ->
                cloudRepository.sendSupportMessageToCloud(msg)
            }
        }
    }

    private fun isAuthorizedAdminEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        val normalized = email.trim().lowercase()
        val configuredAdmin = adminConfig.value.adminEmail.trim().lowercase()
        val savedFirebaseAdmin = adminConfig.value.firebaseUserEmail.trim().lowercase()
        return normalized == configuredAdmin ||
            normalized == "johirul958423@gmail.com" ||
            (savedFirebaseAdmin.isNotEmpty() && normalized == savedFirebaseAdmin)
    }

    fun hasRegisteredAdminAccount(): Boolean {
        return cloudRepository.getFirebaseAuthOrNull()?.currentUser != null
    }

    fun selectTab(tab: AppNavTab) {
        if (tab == AppNavTab.ADMIN && !_isAdminLoggedIn.value) {
            openAdminLoginDialog()
        } else {
            _currentTab.value = tab
        }
    }

    // Dynamic Packages Helpers
    fun getTikTokPackages(): List<TopupPackage> = TopupRepository.getTikTokPackages(
        adminConfig.value.customPrices,
        adminConfig.value.customPackages,
        adminConfig.value.deletedPackageIds
    )

    fun getChametPackages(): List<TopupPackage> = TopupRepository.getChametPackages(
        adminConfig.value.customPrices,
        adminConfig.value.customPackages,
        adminConfig.value.deletedPackageIds
    )

    fun getFreeFirePackages(): List<TopupPackage> = TopupRepository.getFreeFirePackages(
        adminConfig.value.customPrices,
        adminConfig.value.customPackages,
        adminConfig.value.deletedPackageIds
    )

    fun getAllPackages(): List<TopupPackage> = TopupRepository.getAllPackages(
        adminConfig.value.customPrices,
        adminConfig.value.customPackages,
        adminConfig.value.deletedPackageIds
    )

    fun getEpayTiers(): List<EpayRateTier> = TopupRepository.getEpayTiers(adminConfig.value)
    fun calculateEpay(usd: Double): Pair<Double, Double> = TopupRepository.calculateEpayBdt(usd, adminConfig.value)

    // Customer Profiles Aggregation (from Orders + Admin Customer Metadata + Support Messages)
    fun getCustomerProfiles(): List<CustomerProfile> {
        val currentOrders = orders.value
        val currentMessages = supportMessages.value
        val metaMap = adminConfig.value.customerMetadata

        // Group orders by customerEmail (if registered) or userAccountId (if guest/UID)
        val grouped = currentOrders
            .filter { it.userAccountId.isNotBlank() || it.customerEmail.isNotBlank() }
            .groupBy { ord ->
                val emailKey = ord.customerEmail.trim().lowercase()
                if (emailKey.isNotBlank()) emailKey else ord.userAccountId.trim()
            }

        val profiles = mutableListOf<CustomerProfile>()
        grouped.forEach { (groupKey, userOrders) ->
            val sorted = userOrders.sortedByDescending { it.timestamp }
            val latest = sorted.first()
            val emailKey = latest.customerEmail.trim().lowercase().ifBlank {
                if (groupKey.contains("@")) groupKey.trim().lowercase() else ""
            }
            val meta = (if (emailKey.isNotBlank()) metaMap[emailKey] else null)
                ?: metaMap[groupKey]
                ?: metaMap[latest.userAccountId.trim()]
            val completed = userOrders.filter { it.status == "Completed" || it.status == "Approved" }
            val walletBal = if (emailKey.isNotBlank()) {
                adminSettings.getUserWalletBalance(emailKey).coerceAtLeast(meta?.walletBalanceBdt ?: 0.0)
            } else {
                meta?.walletBalanceBdt ?: 0.0
            }
            val refCode = if (emailKey.isNotBlank()) {
                adminSettings.getOrCreateUserReferralCode(emailKey)
            } else {
                meta?.referralCode ?: ""
            }
            val refBy = if (emailKey.isNotBlank()) {
                adminSettings.getUserReferredByCode(emailKey).ifBlank { meta?.referredByCode ?: "" }
            } else {
                meta?.referredByCode ?: ""
            }
            val refEarnings = if (emailKey.isNotBlank()) {
                adminSettings.getUserReferralEarnings(emailKey).coerceAtLeast(meta?.referralEarningsBdt ?: 0.0)
            } else {
                meta?.referralEarningsBdt ?: 0.0
            }
            val refCount = if (emailKey.isNotBlank()) {
                adminSettings.getUserReferralCount(emailKey).coerceAtLeast(meta?.referralCount ?: 0)
            } else {
                meta?.referralCount ?: 0
            }
            val resolvedDisplayName = meta?.displayName?.takeIf { it.isNotBlank() }
                ?: latest.customerName.takeIf { it.isNotBlank() }
                ?: (if (emailKey.isNotBlank()) adminSettings.getSavedAccountName(emailKey) else "").takeIf { it.isNotBlank() }
                ?: (if (emailKey.isNotBlank()) emailKey.substringBefore("@") else latest.contactPlatform)
            profiles.add(
                CustomerProfile(
                    accountId = emailKey.ifBlank { latest.userAccountId.trim().ifBlank { groupKey } },
                    displayName = resolvedDisplayName,
                    email = emailKey.ifBlank { meta?.email ?: "" },
                    phone = latest.customerPhone.ifBlank { meta?.phone ?: "" },
                    photoUri = meta?.photoUri ?: (if (emailKey.isNotBlank()) adminSettings.getSavedAccountPhotoUri(emailKey) else ""),
                    walletBalanceBdt = walletBal,
                    referralCode = refCode,
                    referredByCode = refBy,
                    referralEarningsBdt = refEarnings,
                    referralCount = refCount,
                    lastCategory = latest.category,
                    paymentMethod = latest.paymentMethod,
                    totalOrders = userOrders.size,
                    completedOrders = completed.size,
                    totalSpentBdt = completed.sumOf { it.priceBdt },
                    lastOrderTimestamp = latest.timestamp,
                    isVip = meta?.isVip ?: (completed.sumOf { it.priceBdt } >= 5000.0),
                    isBlocked = meta?.isBlocked ?: false,
                    adminNote = meta?.adminNote ?: ""
                )
            )
        }

        // Also include customers added in metadata (Firebase Auth & Cloud `customers`) who may have no orders yet
        metaMap.forEach { (accountId, meta) ->
            val emailKey = meta.email.trim().lowercase().ifBlank {
                if (accountId.contains("@")) accountId.trim().lowercase() else ""
            }
            if (profiles.none {
                    it.accountId.equals(accountId, ignoreCase = true) ||
                        (emailKey.isNotBlank() && (it.email.equals(emailKey, ignoreCase = true) || it.accountId.equals(emailKey, ignoreCase = true)))
                }
            ) {
                val walletBal = if (emailKey.isNotBlank()) {
                    adminSettings.getUserWalletBalance(emailKey).coerceAtLeast(meta.walletBalanceBdt)
                } else {
                    meta.walletBalanceBdt
                }
                val refCode = if (emailKey.isNotBlank()) {
                    adminSettings.getOrCreateUserReferralCode(emailKey)
                } else {
                    meta.referralCode
                }
                val resolvedName = meta.displayName.ifBlank {
                    if (emailKey.isNotBlank()) adminSettings.getSavedAccountName(emailKey).ifBlank { emailKey.substringBefore("@") } else accountId
                }
                profiles.add(
                    CustomerProfile(
                        accountId = emailKey.ifBlank { accountId },
                        displayName = resolvedName,
                        email = emailKey,
                        phone = meta.phone,
                        photoUri = meta.photoUri,
                        walletBalanceBdt = walletBal,
                        referralCode = refCode,
                        referredByCode = meta.referredByCode,
                        referralEarningsBdt = meta.referralEarningsBdt,
                        referralCount = meta.referralCount,
                        lastCategory = "Registered Member",
                        paymentMethod = "Wallet / Mobile Banking",
                        totalOrders = 0,
                        completedOrders = 0,
                        totalSpentBdt = 0.0,
                        lastOrderTimestamp = System.currentTimeMillis(),
                        isVip = meta.isVip,
                        isBlocked = meta.isBlocked,
                        adminNote = meta.adminNote
                    )
                )
            }
        }

        // Also include any customer who sent a Live Support Chat message if not already in profiles
        currentMessages
            .filter { it.senderRole.equals("USER", ignoreCase = true) }
            .groupBy {
                it.senderEmail.trim().lowercase().ifBlank {
                    it.conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
                }
            }
            .forEach { (chatKey, msgs) ->
                if (chatKey.isBlank()) return@forEach
                val emailKey = if (chatKey.contains("@")) chatKey else ""
                if (profiles.none {
                        it.accountId.equals(chatKey, ignoreCase = true) ||
                            (emailKey.isNotBlank() && it.email.equals(emailKey, ignoreCase = true))
                    }
                ) {
                    val latestMsg = msgs.maxByOrNull { it.timestamp } ?: msgs.first()
                    val walletBal = if (emailKey.isNotBlank()) adminSettings.getUserWalletBalance(emailKey) else 0.0
                    val refCode = if (emailKey.isNotBlank()) adminSettings.getOrCreateUserReferralCode(emailKey) else ""
                    profiles.add(
                        CustomerProfile(
                            accountId = emailKey.ifBlank { chatKey },
                            displayName = latestMsg.senderName.ifBlank { if (emailKey.isNotBlank()) emailKey.substringBefore("@") else chatKey },
                            email = emailKey,
                            phone = latestMsg.senderContact,
                            photoUri = "",
                            walletBalanceBdt = walletBal,
                            referralCode = refCode,
                            referredByCode = "",
                            referralEarningsBdt = 0.0,
                            referralCount = 0,
                            lastCategory = "Support Chat Member",
                            paymentMethod = "Live Support",
                            totalOrders = 0,
                            completedOrders = 0,
                            totalSpentBdt = 0.0,
                            lastOrderTimestamp = latestMsg.timestamp,
                            isVip = false,
                            isBlocked = false,
                            adminNote = ""
                        )
                    )
                }
            }

        return profiles.sortedByDescending { it.lastOrderTimestamp }
    }

    fun syncAndRefreshCloudCustomers(context: Context) {
        val restored = adminSettings.ensureFirebaseAuthMembersRestored()
        cloudRepository.restoreAndSyncCustomersToCloud(restored) { fetched ->
            adminSettings.mergeCustomersFromCloud(fetched)
        }
        Toast.makeText(context, "✅ ক্লাউড ও Firebase মেম্বার একাউন্টগুলো সিঙ্ক করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    fun addOrRestoreCustomerAccountByAdmin(
        context: Context,
        email: String,
        displayName: String,
        phone: String
    ) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) {
            Toast.makeText(context, "ইমেইল অথবা ইউজার আইডি লিখুন", Toast.LENGTH_SHORT).show()
            return
        }
        val cleanName = displayName.trim().ifBlank { cleanEmail.substringBefore("@") }
        val cleanPhone = phone.trim()
        val refCode = adminSettings.getOrCreateUserReferralCode(cleanEmail)
        val walletBal = adminSettings.getUserWalletBalance(cleanEmail)
        adminSettings.saveUserAccountProfile(cleanEmail, cleanName, cleanPhone)
        val map = adminConfig.value.customerMetadata.toMutableMap()
        val existing = map[cleanEmail]
        map[cleanEmail] = com.example.data.local.CustomerMeta(
            accountId = cleanEmail,
            displayName = cleanName,
            email = if (cleanEmail.contains("@")) cleanEmail else (existing?.email ?: ""),
            phone = cleanPhone.ifBlank { existing?.phone ?: "" },
            photoUri = existing?.photoUri ?: "",
            walletBalanceBdt = walletBal.coerceAtLeast(existing?.walletBalanceBdt ?: 0.0),
            referralCode = refCode,
            referredByCode = existing?.referredByCode ?: "",
            referralEarningsBdt = existing?.referralEarningsBdt ?: 0.0,
            referralCount = existing?.referralCount ?: 0,
            isVip = existing?.isVip ?: false,
            isBlocked = existing?.isBlocked ?: false,
            adminNote = existing?.adminNote ?: ""
        )
        adminSettings.updateConfig(adminConfig.value.copy(customerMetadata = map))
        cloudRepository.upsertAuthenticatedUserToCloud(
            uid = "",
            email = cleanEmail,
            displayName = cleanName,
            phone = cleanPhone,
            walletBalanceBdt = walletBal.coerceAtLeast(existing?.walletBalanceBdt ?: 0.0),
            referralCode = refCode
        )
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
        Toast.makeText(context, "✅ মেম্বার একাউন্ট ($cleanEmail) সফলভাবে যুক্ত ও ক্লাউডে সিঙ্ক হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    fun updateCustomerProfile(
        accountId: String,
        displayName: String,
        isVip: Boolean,
        isBlocked: Boolean,
        adminNote: String
    ) {
        adminSettings.updateCustomerMeta(accountId, displayName, isVip, isBlocked, adminNote)
        val meta = adminConfig.value.customerMetadata[accountId]
        val email = meta?.email?.takeIf { it.isNotBlank() } ?: if (accountId.contains("@")) accountId.trim().lowercase() else ""
        if (email.isNotBlank()) {
            cloudRepository.upsertAuthenticatedUserToCloud(
                uid = "",
                email = email,
                displayName = displayName,
                phone = meta?.phone ?: "",
                photoUri = meta?.photoUri ?: "",
                walletBalanceBdt = meta?.walletBalanceBdt,
                referralCode = meta?.referralCode ?: "",
                referredByCode = meta?.referredByCode ?: "",
                referralEarningsBdt = meta?.referralEarningsBdt,
                referralCount = meta?.referralCount,
                isVip = isVip,
                isBlocked = isBlocked,
                adminNote = adminNote
            )
        }
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
    }

    // Dynamic Price, Package & Rate Actions (Synced to Firebase Cloud)
    fun updatePackagePrice(packageId: String, newPrice: Double) {
        adminSettings.updatePackagePrice(packageId, newPrice)
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
    }

    fun addOrUpdatePackage(pkg: TopupPackage) {
        adminSettings.addOrUpdatePackage(pkg)
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
    }

    fun deletePackage(packageId: String) {
        adminSettings.deletePackage(packageId)
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
    }

    fun resetPackagePrices() {
        adminSettings.resetPackagePrices()
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
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
        adminSettings.updateExchangeRates(
            epayBase, epay970, epay10, epay30, epay500, inrBase, tiktokCoin, chametDiamond
        )
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
    }

    // Dynamic Promo Banners
    fun updatePromoBanners(banners: List<com.example.data.model.PromoBanner>) {
        adminSettings.updateConfig(adminConfig.value.copy(promoBanners = banners))
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
    }

    fun addPromoBanner(banner: com.example.data.model.PromoBanner) {
        val updated = adminConfig.value.promoBanners + banner
        updatePromoBanners(updated)
    }

    fun deletePromoBanner(bannerId: String) {
        val updated = adminConfig.value.promoBanners.filter { it.id != bannerId }
        updatePromoBanners(updated)
    }

    // -------------------------------------------------------------------
    // SECURE FIREBASE USER & ADMIN AUTHENTICATION (SIGN UP & LOGIN)
    // -------------------------------------------------------------------

    fun openAdminLoginDialog(startInRegister: Boolean = false) {
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
        _authDialogStartInRegister.value = startInRegister
        _showAdminLoginDialog.value = true
    }

    fun dismissAdminLoginDialog() {
        _showAdminLoginDialog.value = false
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
    }

    fun signInWithFirebaseAuth(email: String, pass: String, context: Context) {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        if (trimmedEmail.isEmpty() || trimmedPass.isEmpty()) {
            _authErrorMessage.value = "অনুগ্রহ করে আপনার ইমেইল ও পাসওয়ার্ড প্রদান করুন"
            return
        }

        _isAuthLoading.value = true
        _authErrorMessage.value = null
        _authSuccessMessage.value = null

        val auth = cloudRepository.getFirebaseAuthOrNull()
        if (auth != null) {
            auth.signInWithEmailAndPassword(trimmedEmail, trimmedPass)
                .addOnCompleteListener { task ->
                    _isAuthLoading.value = false
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userEmail = user?.email ?: trimmedEmail
                        val (savedName, savedPhone) = adminSettings.getUserAccountProfile(userEmail)
                        val displayName = user?.displayName?.takeIf { it.isNotBlank() }
                            ?: savedName.ifBlank { userEmail.substringBefore("@") }
                        val resolvedPhone = savedPhone

                        _firebaseUserEmail.value = userEmail
                        adminSettings.setLoggedInUser(userEmail, displayName, resolvedPhone)
                        cloudRepository.upsertAuthenticatedUserToCloud(
                            uid = user?.uid ?: "",
                            email = userEmail,
                            displayName = displayName,
                            phone = resolvedPhone
                        )
                        cloudRepository.fetchFullCustomerCloudProfileByEmail(userEmail) { cloudName, cloudPhone, cloudPhoto, cloudWallet, cloudRefCode, cloudReferredBy, cloudRefEarnings, cloudRefCount ->
                            val finalName = cloudName.ifBlank { displayName }
                            val finalPhone = cloudPhone.ifBlank { resolvedPhone }
                            adminSettings.setLoggedInUser(userEmail, finalName, finalPhone, cloudPhoto.takeIf { it.isNotBlank() })
                            adminSettings.syncCloudUserWalletAndReferral(
                                email = userEmail,
                                cloudWalletBalance = cloudWallet,
                                cloudReferralCode = cloudRefCode,
                                cloudReferredBy = cloudReferredBy,
                                cloudReferralEarnings = cloudRefEarnings,
                                cloudReferralCount = cloudRefCount
                            )
                        }

                        if (isAuthorizedAdminEmail(userEmail)) {
                            _isAdminLoggedIn.value = true
                            _showAdminLoginDialog.value = false
                            _currentTab.value = AppNavTab.ADMIN
                            adminSettings.updateConfig(
                                adminConfig.value.copy(
                                    firebaseUserEmail = userEmail,
                                    firebaseUserId = user?.uid ?: ""
                                )
                            )
                            cloudRepository.syncAdminConfigToCloud(adminConfig.value)
                            Toast.makeText(context, "✅ এডমিন লগইন সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                        } else {
                            _isAdminLoggedIn.value = false
                            _showAdminLoginDialog.value = false
                            resumePendingOrderModalIfNeeded()
                            Toast.makeText(context, "✅ লগইন সফল হয়েছে! স্বাগতম $displayName", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val exception = task.exception
                        val msg = when (exception) {
                            is FirebaseAuthInvalidUserException -> "এই ইমেইলে কোনো একাউন্ট পাওয়া যায়নি। 'সাইন আপ (Sign Up)' ট্যাব থেকে নতুন একাউন্ট তৈরি করুন।"
                            is FirebaseAuthInvalidCredentialsException -> "❌ ভুল ইমেইল বা পাসওয়ার্ড! অনুগ্রহ করে সঠিক পাসওয়ার্ড দিন অথবা নতুন একাউন্ট সাইন আপ করুন।"
                            else -> exception?.localizedMessage ?: "লগইন ব্যর্থ হয়েছে। ইন্টারনেট সংযোগ যাচাই করুন।"
                        }
                        _authErrorMessage.value = msg
                    }
                }
        } else {
            _isAuthLoading.value = false
            _authErrorMessage.value = "Firebase Authentication সংযোগ পাওয়া যায়নি।"
        }
    }

    fun registerWithFirebaseAuth(
        email: String,
        pass: String,
        context: Context,
        displayName: String = "",
        phone: String = "",
        referralCode: String = ""
    ) {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        val trimmedName = displayName.trim().ifBlank { trimmedEmail.substringBefore("@") }
        val trimmedPhone = phone.trim()
        val trimmedRefCode = referralCode.trim().uppercase()

        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) {
            _authErrorMessage.value = "অনুগ্রহ করে সঠিক ইমেইল এড্রেস লিখুন"
            return
        }
        if (trimmedPass.length < 6) {
            _authErrorMessage.value = "পাসওয়ার্ড অন্তত ৬ অক্ষরের হতে হবে"
            return
        }

        _isAuthLoading.value = true
        _authErrorMessage.value = null
        _authSuccessMessage.value = null

        val auth = cloudRepository.getFirebaseAuthOrNull()
        if (auth != null) {
            auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPass)
                .addOnCompleteListener { task ->
                    _isAuthLoading.value = false
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userEmail = user?.email ?: trimmedEmail
                        try {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(trimmedName)
                                .build()
                            user?.updateProfile(profileUpdates)
                        } catch (_: Exception) {
                        }
                        _firebaseUserEmail.value = userEmail
                        adminSettings.setLoggedInUser(userEmail, trimmedName, trimmedPhone)
                        if (trimmedRefCode.isNotBlank()) {
                            adminSettings.applyReferralCodeForUser(userEmail, trimmedRefCode)
                            val refOwnerEmail = adminSettings.findEmailByReferralCode(trimmedRefCode)
                            if (!refOwnerEmail.isNullOrBlank()) {
                                cloudRepository.upsertAuthenticatedUserToCloud(
                                    uid = "",
                                    email = refOwnerEmail,
                                    displayName = adminSettings.getSavedAccountName(refOwnerEmail),
                                    phone = adminSettings.getSavedAccountPhone(refOwnerEmail),
                                    photoUri = adminSettings.getSavedAccountPhotoUri(refOwnerEmail),
                                    walletBalanceBdt = adminSettings.getUserWalletBalance(refOwnerEmail),
                                    referralCode = adminSettings.getOrCreateUserReferralCode(refOwnerEmail),
                                    referredByCode = adminSettings.getUserReferredByCode(refOwnerEmail),
                                    referralEarningsBdt = adminSettings.getUserReferralEarnings(refOwnerEmail),
                                    referralCount = adminSettings.getUserReferralCount(refOwnerEmail)
                                )
                            }
                        }
                        adminSettings.updateCustomerMeta(
                            accountId = userEmail,
                            displayName = if (trimmedPhone.isNotBlank()) "$trimmedName ($trimmedPhone)" else trimmedName,
                            isVip = false,
                            isBlocked = false,
                            adminNote = "Registered via App Sign Up"
                        )
                        cloudRepository.upsertAuthenticatedUserToCloud(
                            uid = user?.uid ?: "",
                            email = userEmail,
                            displayName = trimmedName,
                            phone = trimmedPhone,
                            walletBalanceBdt = adminSettings.getUserWalletBalance(userEmail),
                            referralCode = adminSettings.getOrCreateUserReferralCode(userEmail),
                            referredByCode = adminSettings.getUserReferredByCode(userEmail),
                            referralEarningsBdt = adminSettings.getUserReferralEarnings(userEmail),
                            referralCount = adminSettings.getUserReferralCount(userEmail)
                        )

                        if (isAuthorizedAdminEmail(userEmail)) {
                            _isAdminLoggedIn.value = true
                            _showAdminLoginDialog.value = false
                            _currentTab.value = AppNavTab.ADMIN
                            adminSettings.updateConfig(
                                adminConfig.value.copy(
                                    firebaseUserEmail = userEmail,
                                    firebaseUserId = user?.uid ?: ""
                                )
                            )
                            cloudRepository.syncAdminConfigToCloud(adminConfig.value)
                            Toast.makeText(context, "✅ এডমিন একাউন্ট তৈরি ও লগইন সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                        } else {
                            _isAdminLoggedIn.value = false
                            _showAdminLoginDialog.value = false
                            cloudRepository.syncAdminConfigToCloud(adminConfig.value)
                            resumePendingOrderModalIfNeeded()
                            Toast.makeText(context, "🎉 সাইন আপ সফল হয়েছে! স্বাগতম $trimmedName", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            _authErrorMessage.value = "এই ইমেইল দিয়ে ইতোমধ্যে একাউন্ট রয়েছে। 'লগইন (Sign In)' ট্যাব থেকে লগইন করুন।"
                        } else {
                            _authErrorMessage.value = exception?.localizedMessage
                                ?: "একাউন্ট তৈরি ব্যর্থ হয়েছে। Firebase Console-এ Email/Password Authentication চালু আছে কি না যাচাই করুন।"
                        }
                    }
                }
        } else {
            _isAuthLoading.value = false
            _authErrorMessage.value = "Firebase Authentication সংযোগ পাওয়া যায়নি।"
        }
    }

    fun sendPasswordReset(email: String, context: Context) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) {
            _authErrorMessage.value = "পাসওয়ার্ড রিসেট করতে আপনার সঠিক ইমেইল লিখুন"
            return
        }

        val auth = cloudRepository.getFirebaseAuthOrNull()
        if (auth == null) {
            _authErrorMessage.value = "Firebase সংযোগ পাওয়া যায়নি। ইন্টারনেট সংযোগ যাচাই করুন।"
            return
        }

        _isAuthLoading.value = true
        _authErrorMessage.value = null
        auth.sendPasswordResetEmail(trimmedEmail)
            .addOnCompleteListener { task ->
                _isAuthLoading.value = false
                if (task.isSuccessful) {
                    _authSuccessMessage.value = "$trimmedEmail-এ পাসওয়ার্ড রিসেট লিংক পাঠানো হয়েছে।"
                    Toast.makeText(context, "পাসওয়ার্ড রিসেট লিংক পাঠানো হয়েছে!", Toast.LENGTH_LONG).show()
                } else {
                    _authErrorMessage.value = task.exception?.localizedMessage ?: "রিসেট ইমেইল পাঠানো যায়নি"
                }
            }
    }

    fun changeAdminPassword(oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit = { _, _ -> }): Boolean {
        val trimmedOld = oldPass.trim()
        val trimmedNew = newPass.trim()
        if (trimmedNew.length < 6) {
            onResult(false, "নতুন পাসওয়ার্ড অন্তত ৬ অক্ষরের হতে হবে")
            return false
        }

        val auth = cloudRepository.getFirebaseAuthOrNull()
        val currentUser = auth?.currentUser
        if (currentUser != null && !currentUser.email.isNullOrBlank()) {
            val credential = EmailAuthProvider.getCredential(currentUser.email!!, trimmedOld)
            currentUser.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    currentUser.updatePassword(trimmedNew).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            cloudRepository.registerAdminHash(currentUser.email!!, trimmedNew)
                            onResult(true, "✅ Firebase Admin পাসওয়ার্ড সফলভাবে আপডেট হয়েছে!")
                        } else {
                            onResult(false, updateTask.exception?.localizedMessage ?: "পাসওয়ার্ড আপডেট ব্যর্থ হয়েছে")
                        }
                    }
                } else {
                    onResult(false, "❌ বর্তমান পাসওয়ার্ড সঠিক নয়!")
                }
            }
            return true
        }

        val updated = cloudRepository.updateAdminHashPassword(trimmedOld, trimmedNew)
        if (updated) {
            onResult(true, "✅ এডমিন পাসওয়ার্ড সফলভাবে আপডেট হয়েছে!")
        } else {
            onResult(false, "❌ বর্তমান পাসওয়ার্ড সঠিক নয়!")
        }
        return updated
    }

    fun logoutAdmin() {
        try {
            cloudRepository.getFirebaseAuthOrNull()?.signOut()
        } catch (_: Exception) {
        }
        adminSettings.clearLoggedInUser()
        _firebaseUserEmail.value = null
        _isAdminLoggedIn.value = false
        _showAdminLoginDialog.value = false
        _currentTab.value = AppNavTab.HOME
    }

    fun updateAdminConfig(newConfig: AdminConfig) {
        adminSettings.updateConfig(newConfig)
        cloudRepository.syncAdminConfigToCloud(newConfig)
    }

    fun getPaymentAccountsList(): List<PaymentAccount> {
        return adminSettings.getPaymentAccounts()
    }

    private fun resumePendingOrderModalIfNeeded() {
        val pkg = pendingPackageAfterAuth
        val pendingAccId = pendingAccountIdAfterAuth
        val modal = pendingOrderModalAfterAuth
        pendingPackageAfterAuth = null
        pendingAccountIdAfterAuth = ""
        pendingOrderModalAfterAuth = null
        when {
            pkg != null -> {
                _savedUidForCurrentCategory.value = pendingAccId
                _selectedPackageForGateway.value = pkg
            }
            modal == "EPAY" -> {
                _showEpayOrderDialog.value = true
            }
            modal == "EXCHANGE" -> {
                _showExchangeOrderDialog.value = true
            }
        }
    }

    fun dismissAppLaunchNotice() {
        _showAppLaunchNotice.value = false
    }

    fun setNotificationScreenVisible(visible: Boolean) {
        _showNotificationScreen.value = visible
        if (visible) {
            adminSettings.markAllNotificationsRead(loggedInUserEmail.value)
        }
    }

    fun setNoticeDialogVisible(visible: Boolean, initialTab: Int = 0) {
        setNotificationScreenVisible(visible)
    }

    fun setNoticeDialogTab(tab: Int) {
        _noticeDialogTab.value = tab
    }

    fun dismissBannerNotification() {
        adminSettings.dismissBannerNotification()
    }

    fun clearAllNotifications() {
        adminSettings.clearNotificationsForUser(loggedInUserEmail.value)
    }

    fun markAllNotificationsRead() {
        adminSettings.markAllNotificationsRead(loggedInUserEmail.value)
    }

    fun setPaymentAccountsSheetVisible(visible: Boolean) {
        _showPaymentAccountsSheet.value = visible
    }

    fun setOrderReceipt(order: OrderRecord?) {
        _selectedOrderForReceipt.value = order
    }

    fun openOrderSheet(pkg: TopupPackage, initialAccountId: String = "") {
        if (loggedInUserEmail.value.isBlank()) {
            pendingPackageAfterAuth = pkg
            pendingAccountIdAfterAuth = initialAccountId
            pendingOrderModalAfterAuth = null
            _authErrorMessage.value = "অর্ডার করতে ও আপনার একাউন্টে ট্রানজেকশন নোটিফিকেশন পেতে অনুগ্রহ করে লগইন বা সাইন আপ করুন"
            _authSuccessMessage.value = null
            _authDialogStartInRegister.value = false
            _showAdminLoginDialog.value = true
            return
        }
        _savedUidForCurrentCategory.value = initialAccountId
        _selectedPackageForGateway.value = pkg
    }

    fun dismissPaymentGateway() {
        _selectedPackageForGateway.value = null
    }

    private fun isCustomerBlocked(accountId: String): Boolean {
        val trimmed = accountId.trim()
        return adminConfig.value.customerMetadata[trimmed]?.isBlocked == true
    }

    fun submitGatewayOrder(
        context: Context,
        pkg: TopupPackage,
        accountId: String,
        trxId: String,
        paymentMethod: String,
        dismissGateway: Boolean = false
    ) {
        if (isCustomerBlocked(accountId)) {
            Toast.makeText(context, "এই একাউন্টটি এডমিন কর্তৃক সাময়িকভাবে সীমাবদ্ধ করা হয়েছে। সাপোর্টে যোগাযোগ করুন।", Toast.LENGTH_LONG).show()
            return
        }
        viewModelScope.launch {
            if (accountId.isNotBlank()) {
                repository.saveUid(pkg.category.name, accountId.trim(), "")
            }
            val orderNum = "JBD-${System.currentTimeMillis().toString().takeLast(6)}"
            val record = OrderRecord(
                orderNumber = orderNum,
                category = pkg.category.displayName,
                itemName = pkg.name,
                amountOrQuantity = pkg.amount,
                priceBdt = pkg.priceBdt,
                userAccountId = accountId.trim(),
                paymentMethod = paymentMethod,
                transactionTrxId = trxId.trim(),
                orderNote = "In-App Verified Online Order",
                status = "Processing",
                timestamp = System.currentTimeMillis(),
                contactPlatform = "In-App Gateway",
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )
            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
            adminSettings.pushOrderStatusNotification(record, "Processing")
            if (trxId.isNotBlank()) {
                userTransactionRepository.insertTransaction(
                    UserTransactionRecord(
                        transactionId = trxId.trim().uppercase(),
                        status = TransactionStatus.PENDING,
                        userId = accountId.trim(),
                        paymentMethod = paymentMethod,
                        amount = pkg.priceBdt,
                        orderNumber = orderNum,
                        category = pkg.category.displayName,
                        notes = "${pkg.name} (${pkg.amount})"
                    )
                )
            }
            triggerOrderPlacedAiMessageAndAutoLiveChat(record, delayMs = 3000L)
            if (dismissGateway) {
                _selectedPackageForGateway.value = null
                _selectedOrderForReceipt.value = record
            }
        }
    }

    fun submitWalletInstantOrder(
        context: Context,
        pkg: TopupPackage,
        accountId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (isCustomerBlocked(accountId)) {
            onResult(false, "এই একাউন্টটি এডমিন কর্তৃক সীমাবদ্ধ করা হয়েছে।")
            return
        }
        val price = pkg.priceBdt
        val currentBal = userWalletBalance.value
        if (currentBal < price) {
            onResult(false, "ওয়ালেট ব্যালেন্স অপর্যাপ্ত! আপনার ব্যালেন্স ৳${String.format(Locale.US, "%,.2f", currentBal)} BDT, প্রয়োজন ৳${String.format(Locale.US, "%,.2f", price)} BDT")
            return
        }

        viewModelScope.launch {
            val deducted = adminSettings.deductFundsFromWallet(price)
            if (!deducted) {
                onResult(false, "ওয়ালেট থেকে ব্যালেন্স কর্তন করা সম্ভব হয়নি।")
                return@launch
            }

            if (accountId.isNotBlank()) {
                repository.saveUid(pkg.category.name, accountId.trim(), "")
            }

            val orderNum = "AUTO-${System.currentTimeMillis().toString().takeLast(6)}"
            val deliveryResult = autoTopupService.executeAutoDelivery(
                category = pkg.category,
                targetUid = accountId.trim(),
                pkg = pkg,
                orderNumber = orderNum,
                config = adminConfig.value
            )

            val (finalStatus, trxId, note) = when (deliveryResult) {
                is com.example.data.remote.AutoDeliveryResult.Success -> {
                    Triple("Processing", deliveryResult.transactionId, "⚡ ওয়ালেট অর্ডার প্রসেসিং: ${deliveryResult.message}")
                }
                is com.example.data.remote.AutoDeliveryResult.Processing -> {
                    Triple("Processing", "AUTO-PROC-${System.currentTimeMillis().toString().takeLast(6)}", deliveryResult.message)
                }
                is com.example.data.remote.AutoDeliveryResult.Failed -> {
                    Triple("Processing", "AUTO-QUEUED", "অটো-কিউ: ${deliveryResult.error}")
                }
            }

            val record = OrderRecord(
                orderNumber = orderNum,
                category = pkg.category.displayName,
                itemName = pkg.name,
                amountOrQuantity = pkg.amount,
                priceBdt = pkg.priceBdt,
                userAccountId = accountId.trim(),
                paymentMethod = "Profile Wallet Balance",
                transactionTrxId = trxId.takeIf { it.isNotBlank() && it != "AUTO-QUEUED" } ?: "WAL-PAID-${orderNum.takeLast(6)}",
                orderNote = "✅ Paid from Profile Wallet Balance | $note",
                status = finalStatus,
                timestamp = System.currentTimeMillis(),
                contactPlatform = "Profile Wallet",
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )

            val email = loggedInUserEmail.value.trim().lowercase()
            if (email.isNotBlank()) {
                adminSettings.recordWalletTransaction(
                    com.example.data.model.WalletTransactionEntry(
                        id = "ord_buy_$orderNum",
                        userEmail = email,
                        type = "SERVICE_PURCHASE",
                        title = "Service Purchase • ${pkg.name}",
                        amountBdt = pkg.priceBdt,
                        isCredit = false,
                        status = "Completed",
                        reasonOrNote = "${pkg.category.displayName} (${pkg.amount}) • UID: ${accountId.trim()}",
                        referenceId = orderNum,
                        timestamp = record.timestamp
                    )
                )
                val map = adminConfig.value.customerMetadata.toMutableMap()
                val existing = map[email]
                map[email] = (existing ?: com.example.data.local.CustomerMeta(accountId = email, email = email)).copy(
                    walletBalanceBdt = adminSettings.getUserWalletBalance(email)
                )
                adminSettings.updateConfig(adminConfig.value.copy(customerMetadata = map))
                cloudRepository.upsertAuthenticatedUserToCloud(
                    uid = cloudRepository.getFirebaseAuthOrNull()?.currentUser?.uid ?: "",
                    email = email,
                    displayName = loggedInUserName.value,
                    phone = loggedInUserPhone.value,
                    photoUri = loggedInUserPhotoUri.value,
                    walletBalanceBdt = adminSettings.getUserWalletBalance(email),
                    referralCode = loggedInUserReferralCode.value
                )
            }
            cloudRepository.syncAdminConfigToCloud(adminConfig.value)

            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
            adminSettings.pushOrderStatusNotification(record, finalStatus)

            userTransactionRepository.insertTransaction(
                UserTransactionRecord(
                    transactionId = record.transactionTrxId,
                    status = TransactionStatus.VERIFIED,
                    userId = accountId.trim(),
                    paymentMethod = "Profile Wallet Balance",
                    amount = pkg.priceBdt,
                    orderNumber = orderNum,
                    category = pkg.category.displayName,
                    notes = "${pkg.name} (${pkg.amount}) - Paid via Profile Wallet"
                )
            )

            triggerOrderPlacedAiMessageAndAutoLiveChat(record, delayMs = 3000L)
            onResult(true, "✅ প্রোফাইল ওয়ালেট ব্যালেন্স থেকে অর্ডার সফল হয়েছে! ৩ সেকেন্ড পর লাইভ চ্যাটে যুক্ত হচ্ছে...")
        }
    }

    fun dispatchAutoFulfillOrder(context: Context, order: OrderRecord) {
        viewModelScope.launch {
            val matchingCat = ServiceCategory.entries.find { it.displayName == order.category || it.name == order.category }
                ?: ServiceCategory.TIKTOK
            val pkg = TopupPackage(
                id = "order_${order.orderNumber}",
                category = matchingCat,
                name = order.itemName,
                subtitle = "Manual Auto-Dispatch",
                amount = order.amountOrQuantity,
                priceBdt = order.priceBdt
            )
            val result = autoTopupService.executeAutoDelivery(
                category = matchingCat,
                targetUid = order.userAccountId,
                pkg = pkg,
                orderNumber = order.orderNumber,
                config = adminConfig.value
            )
            when (result) {
                is com.example.data.remote.AutoDeliveryResult.Success -> {
                    val updated = order.copy(
                        status = "Completed",
                        orderNote = "${order.orderNote} | ⚡ Auto-Fulfill: ${result.transactionId}"
                    )
                    repository.updateOrder(updated)
                    cloudRepository.updateOrderStatusInCloud(updated, "Completed")
                    adminSettings.pushOrderStatusNotification(updated, "Completed")
                    if (_selectedOrderForReceipt.value?.orderNumber == order.orderNumber) {
                        _selectedOrderForReceipt.value = updated
                    }
                    Toast.makeText(context, "⚡ অটো ডেলিভারি সফল! TrxID: ${result.transactionId}", Toast.LENGTH_LONG).show()
                }
                is com.example.data.remote.AutoDeliveryResult.Failed -> {
                    Toast.makeText(context, "❌ ডেলিভারি এরর: ${result.error}", Toast.LENGTH_LONG).show()
                }
                is com.example.data.remote.AutoDeliveryResult.Processing -> {
                    Toast.makeText(context, "সার্ভারে প্রসেস হচ্ছে: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun testProviderApi(url: String, apiKey: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = autoTopupService.testApiConnection(url, apiKey)
            onResult(res.first, res.second)
        }
    }

    fun dismissPackageChooser() {
        _selectedPackageForChooser.value = null
    }

    fun orderPackageViaWhatsApp(context: Context, pkg: TopupPackage) {
        _selectedPackageForChooser.value = null
        val orderNum = "JBD-${System.currentTimeMillis().toString().takeLast(6)}"
        val conf = adminConfig.value
        viewModelScope.launch {
            val record = OrderRecord(
                orderNumber = orderNum,
                category = pkg.category.displayName,
                itemName = pkg.name,
                amountOrQuantity = pkg.amount,
                priceBdt = pkg.priceBdt,
                userAccountId = "WhatsApp Direct Order",
                paymentMethod = "bKash / Nagad",
                transactionTrxId = "",
                orderNote = "Initiated via WhatsApp Direct",
                status = "Processing",
                timestamp = System.currentTimeMillis(),
                contactPlatform = "WhatsApp",
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )
            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
        }
        val message = """
            🛒 *Chamet Top Up BD - NEW ORDER*
            
            📋 *Order Ref:* $orderNum
            🎮 *Category:* ${pkg.category.displayName}
            📦 *Package:* ${pkg.name} (${pkg.subtitle})
            💰 *Price:* ৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)}
            
            📱 *Payment Number:*
            • bKash: ${conf.bkashNumber}
            • Nagad: ${conf.nagadNumber}
            ${if (conf.isRocketEnabled) "• Rocket: ${conf.rocketNumber}\n" else ""}
            আমি এই প্যাকেজটি কিনতে চাই। অনুগ্রহ করে পরবর্তী প্রক্রিয়া সম্পন্ন করুন।
        """.trimIndent()
        launchWhatsApp(context, message)
    }

    fun orderPackageViaTelegram(context: Context, pkg: TopupPackage) {
        _selectedPackageForChooser.value = null
        val orderNum = "JBD-${System.currentTimeMillis().toString().takeLast(6)}"
        val conf = adminConfig.value
        viewModelScope.launch {
            val record = OrderRecord(
                orderNumber = orderNum,
                category = pkg.category.displayName,
                itemName = pkg.name,
                amountOrQuantity = pkg.amount,
                priceBdt = pkg.priceBdt,
                userAccountId = "Telegram Direct Order",
                paymentMethod = "bKash / Nagad",
                transactionTrxId = "",
                orderNote = "Initiated via Telegram Direct",
                status = "Processing",
                timestamp = System.currentTimeMillis(),
                contactPlatform = "Telegram",
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )
            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
        }
        val message = """
            🛒 *Chamet Top Up BD - NEW ORDER*
            
            📋 *Order Ref:* $orderNum
            🎮 *Category:* ${pkg.category.displayName}
            📦 *Package:* ${pkg.name} (${pkg.subtitle})
            💰 *Price:* ৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)}
            
            📱 *Payment Number:*
            • bKash: ${conf.bkashNumber}
            • Nagad: ${conf.nagadNumber}
            ${if (conf.isRocketEnabled) "• Rocket: ${conf.rocketNumber}\n" else ""}
            আমি এই প্যাকেজটি কিনতে চাই। অনুগ্রহ করে পরবর্তী প্রক্রিয়া সম্পন্ন করুন।
        """.trimIndent()
        copyToClipboardAndOpenTelegram(context, message)
    }

    fun openFullOrderSheet(pkg: TopupPackage) {
        if (loggedInUserEmail.value.isBlank()) {
            pendingPackageAfterAuth = pkg
            pendingOrderModalAfterAuth = null
            _authErrorMessage.value = "অর্ডার করতে ও আপনার একাউন্টে ট্রানজেকশন নোটিফিকেশন পেতে অনুগ্রহ করে লগইন বা সাইন আপ করুন"
            _authSuccessMessage.value = null
            _authDialogStartInRegister.value = false
            _showAdminLoginDialog.value = true
            return
        }
        viewModelScope.launch {
            val accounts = adminSettings.getPaymentAccounts()
            val firstMethod = accounts.firstOrNull()?.let { "${it.method} (${it.number})" } ?: "bKash Personal"
            _orderForm.value = OrderFormState(
                packageItem = pkg,
                userAccountId = "",
                userName = "",
                paymentMethod = firstMethod
            )
        }
    }

    fun dismissOrderSheet() {
        _orderForm.value = null
    }

    fun updateOrderForm(
        uid: String? = null,
        name: String? = null,
        payment: String? = null,
        trx: String? = null,
        note: String? = null,
        saveUid: Boolean? = null
    ) {
        _orderForm.value?.let { current ->
            _orderForm.value = current.copy(
                userAccountId = uid ?: current.userAccountId,
                userName = name ?: current.userName,
                paymentMethod = payment ?: current.paymentMethod,
                transactionTrxId = trx ?: current.transactionTrxId,
                orderNote = note ?: current.orderNote,
                saveUidForFuture = saveUid ?: current.saveUidForFuture
            )
        }
    }

    fun updateEpayUsd(usd: String) {
        _epayUsdInput.value = usd.filter { it.isDigit() || it == '.' }
    }

    fun updateExchangeSendMethod(method: String) {
        _exchangeSendMethod.value = method
    }

    fun updateExchangeReceiveMethod(method: String) {
        _exchangeReceiveMethod.value = method
    }

    fun updateExchangeAmount(amount: String) {
        _exchangeAmountBdt.value = amount.filter { it.isDigit() || it == '.' }
    }

    fun updateExchangeRecipientUpi(upi: String) {
        _exchangeRecipientUpi.value = upi
    }

    fun updateCustomTikTokCoins(coins: String) {
        _customTikTokCoins.value = coins.filter { it.isDigit() }
    }

    fun updateCustomChametDiamonds(diamonds: String) {
        _customChametDiamonds.value = diamonds.filter { it.isDigit() }
    }

    // Submit Order to Room + Firebase Cloud and trigger WhatsApp / Telegram
    fun submitOrder(context: Context, platform: String) {
        val form = _orderForm.value ?: return
        val pkg = form.packageItem ?: return

        if (form.userAccountId.isBlank()) {
            Toast.makeText(context, "অনুগ্রহ করে আপনার UID / Account ID লিখুন", Toast.LENGTH_SHORT).show()
            return
        }
        if (isCustomerBlocked(form.userAccountId)) {
            Toast.makeText(context, "এই একাউন্টটি এডমিন কর্তৃক সীমাবদ্ধ করা হয়েছে।", Toast.LENGTH_LONG).show()
            return
        }

        viewModelScope.launch {
            if (form.saveUidForFuture) {
                repository.saveUid(pkg.category.name, form.userAccountId.trim(), form.userName.trim())
            }

            val orderNum = "JBD-${System.currentTimeMillis().toString().takeLast(6)}"
            val record = OrderRecord(
                orderNumber = orderNum,
                category = pkg.category.displayName,
                itemName = pkg.name,
                amountOrQuantity = pkg.amount,
                priceBdt = pkg.priceBdt,
                userAccountId = form.userAccountId.trim(),
                paymentMethod = form.paymentMethod,
                transactionTrxId = form.transactionTrxId.trim(),
                orderNote = form.orderNote.trim(),
                status = "Processing",
                timestamp = System.currentTimeMillis(),
                contactPlatform = platform,
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )
            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
            adminSettings.pushOrderStatusNotification(record, "Processing")
            _orderForm.value = null
            _selectedOrderForReceipt.value = record
            triggerOrderPlacedAiMessageAndAutoLiveChat(record, delayMs = 3000L)
        }
    }

    fun openEpayOrderDialog() {
        if (loggedInUserEmail.value.isBlank()) {
            pendingPackageAfterAuth = null
            pendingOrderModalAfterAuth = "EPAY"
            _authErrorMessage.value = "অর্ডার করতে ও আপনার একাউন্টে ট্রানজেকশন নোটিফিকেশন পেতে অনুগ্রহ করে লগইন বা সাইন আপ করুন"
            _authSuccessMessage.value = null
            _authDialogStartInRegister.value = false
            _showAdminLoginDialog.value = true
            return
        }
        _showEpayOrderDialog.value = true
    }

    fun dismissEpayOrderDialog() {
        _showEpayOrderDialog.value = false
    }

    fun submitEpayDirectOrder(
        context: Context,
        usd: Double,
        bdt: Double,
        rate: Double,
        epayOrderNo: String,
        senderAccount: String,
        payoutMethod: String,
        payoutNumber: String
    ) {
        viewModelScope.launch {
            val orderNum = "EPAY-${System.currentTimeMillis().toString().takeLast(6)}"
            val accountInfo = buildString {
                append("Payout to $payoutMethod: $payoutNumber")
                if (senderAccount.isNotBlank()) {
                    append(" | Sender EPAY: $senderAccount")
                }
            }

            val record = OrderRecord(
                orderNumber = orderNum,
                category = ServiceCategory.EPAY.displayName,
                itemName = "EPAY $usd USD Exchange",
                amountOrQuantity = "$usd USD",
                priceBdt = bdt,
                userAccountId = accountInfo,
                paymentMethod = payoutMethod,
                transactionTrxId = epayOrderNo.trim(),
                orderNote = "Rate: ৳${String.format(Locale.US, "%.1f", rate)}/USD | Payout: ৳${String.format(Locale.US, "%,.0f", bdt)}",
                status = "Processing",
                timestamp = System.currentTimeMillis(),
                contactPlatform = "In-App EPAY",
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )
            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
            adminSettings.pushOrderStatusNotification(record, "Processing")
            _showEpayOrderDialog.value = false
            _selectedOrderForReceipt.value = record
            triggerOrderPlacedAiMessageAndAutoLiveChat(record, delayMs = 3000L)
        }
    }

    fun submitCustomEpayOrder(context: Context, platform: String) {
        val usd = _epayUsdInput.value.toDoubleOrNull() ?: 10.0
        val (bdt, rate) = calculateEpay(usd)
        val orderNum = "EPAY-${System.currentTimeMillis().toString().takeLast(6)}"

        viewModelScope.launch {
            val record = OrderRecord(
                orderNumber = orderNum,
                category = ServiceCategory.EPAY.displayName,
                itemName = "EPAY $usd USD Exchange",
                amountOrQuantity = "$usd USD",
                priceBdt = bdt,
                userAccountId = "EPAY Dollar Request",
                paymentMethod = "bKash / Nagad Receive",
                transactionTrxId = "Rate: ৳${String.format(Locale.US, "%.1f", rate)}/USD",
                orderNote = "Auto Calculated BDT: ৳${String.format(Locale.US, "%,.0f", bdt)}",
                status = "Processing",
                timestamp = System.currentTimeMillis(),
                contactPlatform = platform,
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )
            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
            adminSettings.pushOrderStatusNotification(record, "Processing")

            val epayEmail = adminConfig.value.epayEmail
            val message = """
                💸 *Chamet Top Up BD - EPAY DOLLAR EXCHANGE*
                
                📋 *Order ID:* $orderNum
                💵 *Dollar Amount:* $usd USD
                💰 *Estimated BDT:* ৳${String.format(Locale.US, "%,.0f", bdt)}
                📊 *Applied Rate:* ৳${String.format(Locale.US, "%.1f", rate)} / USD
                📧 *Receiver EPAY:* $epayEmail
                
                📱 *Payment to:* bKash / Nagad
                
                আমি আমার EPAY Dollar ($usd USD) $epayEmail এই একাউন্টে পাঠাতে চাই। দয়া করে পেমেন্ট প্রক্রিয়া নিশ্চিত করুন।
            """.trimIndent()

            if (platform == "WhatsApp") {
                launchWhatsApp(context, message)
            } else {
                copyToClipboardAndOpenTelegram(context, message)
            }
        }
    }

    fun submitExchangeRequest(context: Context, platform: String) {
        val send = _exchangeSendMethod.value
        val receive = _exchangeReceiveMethod.value
        val amount = _exchangeAmountBdt.value.toDoubleOrNull() ?: 1000.0
        val recipient = _exchangeRecipientUpi.value.trim()
        val inrRate = adminConfig.value.inrBaseRate
        val inrEstimate = if (inrRate > 0) amount / inrRate else amount * 0.69
        val orderNum = "EXC-${System.currentTimeMillis().toString().takeLast(6)}"

        viewModelScope.launch {
            val record = OrderRecord(
                orderNumber = orderNum,
                category = ServiceCategory.EXCHANGE.displayName,
                itemName = "$send 🇧🇩 ➔ $receive 🇮🇳",
                amountOrQuantity = "৳${String.format(Locale.US, "%,.0f", amount)} BDT",
                priceBdt = amount,
                userAccountId = if (recipient.isNotBlank()) "UPI/Phone: $recipient" else "To be provided",
                paymentMethod = "$send ➔ $receive",
                transactionTrxId = "Est. INR: ₹${String.format(Locale.US, "%,.0f", inrEstimate)}",
                orderNote = "BD to India Money Exchange Request",
                status = "Processing",
                timestamp = System.currentTimeMillis(),
                contactPlatform = platform,
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )
            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
            adminSettings.pushOrderStatusNotification(record, "Processing")

            val message = """
                🔄 *Chamet Top Up BD - PAYMENT EXCHANGE REQUEST*
                
                📋 *Order ID:* $orderNum
                🇧🇩 *আমি পাঠাবো:* $send (৳${String.format(Locale.US, "%,.0f", amount)} BDT)
                🇮🇳 *আমি নেবো:* $receive (আনুমানিক ₹${String.format(Locale.US, "%,.0f", inrEstimate)} INR)
                🟣 *Recipient UPI / Account:* ${if (recipient.isNotBlank()) recipient else "Chat-এ দিচ্ছি"}
                
                💱 বর্তমান Exchange Rate এবং পেমেন্ট একাউন্ট নাম্বার পাঠাবেন।
            """.trimIndent()

            if (platform == "WhatsApp") {
                launchWhatsApp(context, message)
            } else {
                copyToClipboardAndOpenTelegram(context, message)
            }
        }
    }

    fun openExchangeOrderDialog() {
        if (loggedInUserEmail.value.isBlank()) {
            pendingPackageAfterAuth = null
            pendingOrderModalAfterAuth = "EXCHANGE"
            _authErrorMessage.value = "অর্ডার করতে ও আপনার একাউন্টে ট্রানজেকশন নোটিফিকেশন পেতে অনুগ্রহ করে লগইন বা সাইন আপ করুন"
            _authSuccessMessage.value = null
            _authDialogStartInRegister.value = false
            _showAdminLoginDialog.value = true
            return
        }
        _showExchangeOrderDialog.value = true
    }

    fun dismissExchangeOrderDialog() {
        _showExchangeOrderDialog.value = false
    }

    fun submitDirectExchangeOrder(
        context: Context,
        sendMethod: String,
        receiveMethod: String,
        bdt: Double,
        inr: Double,
        phonePeNumber: String,
        upiUrl: String,
        screenshotUri: String,
        trxId: String
    ) {
        viewModelScope.launch {
            val orderNum = "EXC-${System.currentTimeMillis().toString().takeLast(6)}"
            val userAccountStr = buildString {
                append("PhonePe/UPI: $phonePeNumber")
                if (upiUrl.isNotBlank()) {
                    append(" | URL: $upiUrl")
                }
            }

            val inrRate = adminConfig.value.inrBaseRate
            val rateInfo = if (inrRate > 0) "Rate: ৳${String.format(Locale.US, "%.2f", inrRate)} BDT = ₹1 INR" else ""

            val record = OrderRecord(
                orderNumber = orderNum,
                category = ServiceCategory.EXCHANGE.displayName,
                itemName = "$sendMethod 🇧🇩 ➔ $receiveMethod 🇮🇳",
                amountOrQuantity = "৳${String.format(Locale.US, "%,.0f", bdt)} BDT ➔ ₹${String.format(Locale.US, "%,.0f", inr)} INR",
                priceBdt = bdt,
                userAccountId = userAccountStr,
                paymentMethod = sendMethod,
                transactionTrxId = trxId.trim(),
                orderNote = "$rateInfo | Receive: ₹${String.format(Locale.US, "%,.0f", inr)} INR",
                screenshotUri = screenshotUri,
                status = "Processing",
                timestamp = System.currentTimeMillis(),
                contactPlatform = "In-App Exchange",
                customerEmail = loggedInUserEmail.value.trim().lowercase(),
                customerPhone = loggedInUserPhone.value.trim(),
                customerName = loggedInUserName.value.trim()
            )

            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
            adminSettings.pushOrderStatusNotification(record, "Processing")
            _showExchangeOrderDialog.value = false
            _selectedOrderForReceipt.value = record
            triggerOrderPlacedAiMessageAndAutoLiveChat(record, delayMs = 3000L)
        }
    }

    fun deleteOrder(order: OrderRecord) {
        viewModelScope.launch {
            repository.deleteOrder(order)
            adminSettings.removeNotificationByOrderNumber(order.orderNumber)
            cloudRepository.deleteOrderFromCloud(order.orderNumber)
        }
    }

    fun clearAllOrders() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val currentOrders = orders.value
            repository.clearOrders()
            userTransactionRepository.clearAll()
            adminSettings.clearAllNotifications()
            cloudRepository.clearAllOrdersFromCloud(currentOrders)
        }
    }

    fun updateOrderStatus(order: OrderRecord, newStatus: String) {
        viewModelScope.launch {
            val updatedOrder = order.copy(status = newStatus)
            repository.updateOrder(updatedOrder)
            cloudRepository.updateOrderStatusInCloud(updatedOrder, newStatus)
            adminSettings.pushOrderStatusNotification(updatedOrder, newStatus)
            if (_selectedOrderForReceipt.value?.orderNumber == order.orderNumber) {
                _selectedOrderForReceipt.value = updatedOrder
            }
            if (newStatus == "Completed" || newStatus == "Approved") {
                val referralReward = adminSettings.processOrderCompletionRewards(updatedOrder)
                cloudRepository.syncAdminConfigToCloud(adminConfig.value)
                // Sync buyer and referrer wallet balance to cloud customers collection
                val buyerEmail = updatedOrder.customerEmail.trim().lowercase()
                if (buyerEmail.isNotBlank()) {
                    cloudRepository.upsertAuthenticatedUserToCloud(
                        uid = "",
                        email = buyerEmail,
                        displayName = adminSettings.getSavedAccountName(buyerEmail),
                        phone = adminSettings.getSavedAccountPhone(buyerEmail),
                        photoUri = adminSettings.getSavedAccountPhotoUri(buyerEmail),
                        walletBalanceBdt = adminSettings.getUserWalletBalance(buyerEmail),
                        referralCode = adminSettings.getOrCreateUserReferralCode(buyerEmail),
                        referredByCode = adminSettings.getUserReferredByCode(buyerEmail)
                    )
                }
                if (referralReward != null && referralReward.referrerEmail.isNotBlank()) {
                    val refEmail = referralReward.referrerEmail.trim().lowercase()
                    cloudRepository.upsertAuthenticatedUserToCloud(
                        uid = "",
                        email = refEmail,
                        displayName = adminSettings.getSavedAccountName(refEmail),
                        phone = adminSettings.getSavedAccountPhone(refEmail),
                        photoUri = adminSettings.getSavedAccountPhotoUri(refEmail),
                        walletBalanceBdt = adminSettings.getUserWalletBalance(refEmail),
                        referralCode = referralReward.referrerCode,
                        referralEarningsBdt = adminSettings.getUserReferralEarnings(refEmail),
                        referralCount = adminSettings.getUserReferralCount(refEmail)
                    )
                }
            }
            if (order.transactionTrxId.isNotBlank()) {
                val trx = order.transactionTrxId.trim().uppercase()
                when (newStatus) {
                    "Completed", "Approved" -> userTransactionRepository.markAsVerified(trx)
                    "Cancelled" -> userTransactionRepository.markAsRejected(trx, "Cancelled / Invalid TrxID")
                    else -> userTransactionRepository.markAsPending(trx)
                }
            }
            sendAiOrderStatusUpdateToChat(updatedOrder, newStatus)
        }
    }

    fun sendCustomerSuccessWhatsApp(context: Context, order: OrderRecord) {
        val message = """
            ✅ *Chamet Top Up BD - অর্ডার সম্পন্ন হয়েছে!*
            
            প্রিয় গ্রাহক,
            আপনার অর্ডার #${order.orderNumber} সফলভাবে সম্পন্ন করা হয়েছে।
            
            📦 সার্ভিস: ${order.itemName} (${order.amountOrQuantity})
            👤 একাউন্ট/UID: ${order.userAccountId}
            💰 পেইড: ৳${String.format(Locale.US, "%,.0f", order.priceBdt)} BDT
            
            আমাদের সেবা ব্যবহারের জন্য ধন্যবাদ! যেকোনো প্রয়োজনে আবার যোগাযোগ করুন। 🪙
        """.trimIndent()
        launchWhatsApp(context, message)
    }

    fun sendCustomerCancelWhatsApp(context: Context, order: OrderRecord, reason: String = "পেমেন্ট বা UID তথ্যে ভুল থাকায়") {
        val message = """
            ⚠️ *Chamet Top Up BD - অর্ডার স্ট্যাটাস আপডেট*
            
            প্রিয় গ্রাহক,
            আপনার অর্ডার #${order.orderNumber} সম্পন্ন করা যায়নি।
            
            📦 প্যাকেজ: ${order.itemName}
            👤 UID: ${order.userAccountId}
            ❌ কারণ: $reason
            
            অনুগ্রহ করে সঠিক তথ্য দিয়ে আমাদের সাথে যোগাযোগ করুন।
        """.trimIndent()
        launchWhatsApp(context, message)
    }

    private fun sendWhatsAppOrder(context: Context, order: OrderRecord) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = sdf.format(Date(order.timestamp))

        val message = """
            🪙 *Chamet Top Up BD - NEW ORDER*
            
            📋 *Order Number:* ${order.orderNumber}
            📦 *Service:* ${order.category}
            💎 *Package:* ${order.itemName} (${order.amountOrQuantity})
            💰 *Total Price:* ৳${String.format(Locale.US, "%,.0f", order.priceBdt)} BDT
            
            👤 *Player UID / Username:* ${order.userAccountId}
            💳 *Payment Method:* ${order.paymentMethod}
            ${if (order.transactionTrxId.isNotBlank()) "🔖 *TrxID:* ${order.transactionTrxId}\n" else ""}${if (order.orderNote.isNotBlank()) "📝 *Note:* ${order.orderNote}\n" else ""}🕒 *Time:* $dateStr
            
            আমি পেমেন্ট সম্পন্ন করেছি / অর্ডার কনফার্ম করতে চাই। দয়া করে দ্রুত ডেলিভারি দিন।
        """.trimIndent()

        launchWhatsApp(context, message)
    }

    private fun sendTelegramOrder(context: Context, order: OrderRecord) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = sdf.format(Date(order.timestamp))

        val message = """
            🪙 *Chamet Top Up BD - NEW ORDER*
            
            📋 *Order Number:* ${order.orderNumber}
            📦 *Service:* ${order.category}
            💎 *Package:* ${order.itemName} (${order.amountOrQuantity})
            💰 *Total Price:* ৳${String.format(Locale.US, "%,.0f", order.priceBdt)} BDT
            👤 *Player UID / Username:* ${order.userAccountId}
            💳 *Payment:* ${order.paymentMethod}
            🕒 *Time:* $dateStr
        """.trimIndent()

        copyToClipboardAndOpenTelegram(context, message)
    }

    fun launchWhatsApp(context: Context, customText: String? = null) {
        val num = adminConfig.value.whatsappNumber.ifBlank { TopupRepository.WHATSAPP_NUMBER }
        val text = customText ?: "আসসালামু আলাইকুম! Chamet Top Up BD থেকে সেবা নিতে চাই।"
        try {
            val uri = Uri.parse("https://wa.me/$num?text=${Uri.encode(text)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "হোয়াটসঅ্যাপ ওপেন করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboardAndOpenTelegram(context: Context, message: String? = null) {
        val tgUrl = adminConfig.value.telegramUrl.ifBlank { TopupRepository.TELEGRAM_URL }
        val textToCopy = message ?: "Chamet Top Up BD: Topup & Dollar Exchange Service"
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("JohirulTopupOrder", textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "অর্ডারের বিবরণ কপি করা হয়েছে! টেলিগ্রামে পেস্ট করুন।", Toast.LENGTH_LONG).show()

        try {
            val uri = Uri.parse(tgUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "টেলিগ্রাম ওপেন করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyText(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label কপি করা হয়েছে: $text", Toast.LENGTH_SHORT).show()
    }

    fun depositToWallet(
        context: Context,
        amount: Double,
        method: String,
        trxId: String,
        senderNumber: String = "",
        screenshotUri: String = "",
        note: String = "",
        showReceiptImmediately: Boolean = false
    ) {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) {
            Toast.makeText(context, "প্রথমে আপনার একাউন্টে লগইন করুন!", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            // Always keep Add Money requests in Pending ("Processing") until Admin verifies & approves in Admin Panel
            val isInstant = false
            val initialStatus = "Processing"
            val orderNum = "WAL-${System.currentTimeMillis().toString().takeLast(6)}"
            val cleanSender = senderNumber.trim().ifBlank { loggedInUserPhone.value.trim() }
            val record = OrderRecord(
                orderNumber = orderNum,
                category = "Wallet",
                itemName = "Wallet Balance Add Money ($method)",
                amountOrQuantity = "+ ৳${String.format(Locale.US, "%,.0f", amount)} BDT",
                priceBdt = amount,
                userAccountId = if (cleanSender.isNotBlank()) "$email ($cleanSender)" else email,
                paymentMethod = method,
                transactionTrxId = trxId.trim().uppercase(),
                orderNote = note.ifBlank { "Profile Wallet Add Money Request | TrxID: ${trxId.trim().uppercase()}" },
                screenshotUri = screenshotUri,
                status = initialStatus,
                timestamp = System.currentTimeMillis(),
                contactPlatform = "Profile Wallet",
                customerEmail = email,
                customerPhone = cleanSender,
                customerName = loggedInUserName.value.trim()
            )
            adminSettings.recordWalletTransaction(
                com.example.data.model.WalletTransactionEntry(
                    id = "ord_add_$orderNum",
                    userEmail = email,
                    type = "ADD_MONEY",
                    title = "Balance Added ($method)",
                    amountBdt = amount,
                    isCredit = true,
                    status = "Processing",
                    reasonOrNote = "TrxID: ${trxId.trim().uppercase()}",
                    referenceId = orderNum,
                    timestamp = record.timestamp
                )
            )
            repository.insertOrder(record)
            cloudRepository.uploadOrderToCloud(record)
            cloudRepository.syncAdminConfigToCloud(adminConfig.value)
            adminSettings.pushOrderStatusNotification(record, initialStatus)
            if (trxId.isNotBlank()) {
                userTransactionRepository.insertTransaction(
                    UserTransactionRecord(
                        transactionId = trxId.trim().uppercase(),
                        status = TransactionStatus.PENDING,
                        userId = email,
                        paymentMethod = method,
                        amount = amount,
                        orderNumber = orderNum,
                        category = "Wallet Add Money",
                        notes = "Profile Wallet Balance Top-Up (+৳${amount.toInt()})"
                    )
                )
            }
            if (showReceiptImmediately) {
                _selectedOrderForReceipt.value = record
            }
            triggerOrderPlacedAiMessageAndAutoLiveChat(record, delayMs = 3000L)
            val msg = "⏳ অ্যাড মানি রিকোয়েস্ট (#$orderNum) পেন্ডিং তালিকায় জমা হয়েছে! ৩ সেকেন্ড পর অটোমেটিক লাইভ চ্যাটে যুক্ত হচ্ছে..."
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    fun updateUserProfileDetails(
        context: Context,
        newName: String,
        newPhone: String,
        newPhotoUri: String? = null
    ) {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) return
        val cleanName = newName.trim().ifBlank { email.substringBefore("@") }
        val cleanPhone = newPhone.trim()
        val finalPhoto = newPhotoUri ?: loggedInUserPhotoUri.value
        adminSettings.setLoggedInUser(email, cleanName, cleanPhone, finalPhoto)
        val map = adminConfig.value.customerMetadata.toMutableMap()
        val existing = map[email]
        map[email] = (existing ?: com.example.data.local.CustomerMeta(accountId = email, email = email)).copy(
            displayName = cleanName,
            email = email,
            phone = cleanPhone,
            photoUri = finalPhoto,
            walletBalanceBdt = adminSettings.getUserWalletBalance(email),
            referralCode = adminSettings.getOrCreateUserReferralCode(email),
            referredByCode = adminSettings.getUserReferredByCode(email),
            referralEarningsBdt = adminSettings.getUserReferralEarnings(email),
            referralCount = adminSettings.getUserReferralCount(email)
        )
        adminSettings.updateConfig(adminConfig.value.copy(customerMetadata = map))
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
        cloudRepository.upsertAuthenticatedUserToCloud(
            uid = cloudRepository.getFirebaseAuthOrNull()?.currentUser?.uid ?: "",
            email = email,
            displayName = cleanName,
            phone = cleanPhone,
            photoUri = finalPhoto,
            walletBalanceBdt = adminSettings.getUserWalletBalance(email),
            referralCode = adminSettings.getOrCreateUserReferralCode(email),
            referredByCode = adminSettings.getUserReferredByCode(email),
            referralEarningsBdt = adminSettings.getUserReferralEarnings(email),
            referralCount = adminSettings.getUserReferralCount(email)
        )
        Toast.makeText(context, "✅ প্রোফাইল সফলভাবে আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    fun applyReferralCodeForLoggedInUser(context: Context, rawCode: String): Pair<Boolean, String> {
        val email = loggedInUserEmail.value.trim().lowercase()
        val result = adminSettings.applyReferralCodeForUser(email, rawCode)
        if (result.first) {
            val refOwnerEmail = adminSettings.findEmailByReferralCode(rawCode)
            if (!refOwnerEmail.isNullOrBlank()) {
                cloudRepository.upsertAuthenticatedUserToCloud(
                    uid = "",
                    email = refOwnerEmail,
                    displayName = adminSettings.getSavedAccountName(refOwnerEmail),
                    phone = adminSettings.getSavedAccountPhone(refOwnerEmail),
                    photoUri = adminSettings.getSavedAccountPhotoUri(refOwnerEmail),
                    walletBalanceBdt = adminSettings.getUserWalletBalance(refOwnerEmail),
                    referralCode = adminSettings.getOrCreateUserReferralCode(refOwnerEmail),
                    referredByCode = adminSettings.getUserReferredByCode(refOwnerEmail),
                    referralEarningsBdt = adminSettings.getUserReferralEarnings(refOwnerEmail),
                    referralCount = adminSettings.getUserReferralCount(refOwnerEmail)
                )
            }
            cloudRepository.syncAdminConfigToCloud(adminConfig.value)
            cloudRepository.upsertAuthenticatedUserToCloud(
                uid = cloudRepository.getFirebaseAuthOrNull()?.currentUser?.uid ?: "",
                email = email,
                displayName = loggedInUserName.value,
                phone = loggedInUserPhone.value,
                photoUri = loggedInUserPhotoUri.value,
                walletBalanceBdt = adminSettings.getUserWalletBalance(email),
                referralCode = adminSettings.getOrCreateUserReferralCode(email),
                referredByCode = adminSettings.getUserReferredByCode(email),
                referralEarningsBdt = adminSettings.getUserReferralEarnings(email),
                referralCount = adminSettings.getUserReferralCount(email)
            )
        }
        Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
        return result
    }

    fun applyReferralCodeForLoggedInUser(rawCode: String, onResult: (Boolean, String) -> Unit) {
        val res = applyReferralCodeForLoggedInUser(getApplication(), rawCode)
        onResult(res.first, res.second)
    }

    fun getLoggedInUserReferralEarnings(): Double {
        val email = loggedInUserEmail.value.trim().lowercase()
        return if (email.isNotBlank()) adminSettings.getUserReferralEarnings(email) else 0.0
    }

    fun getLoggedInUserTotalReferrals(): Int {
        val email = loggedInUserEmail.value.trim().lowercase()
        return if (email.isNotBlank()) adminSettings.getUserReferralCount(email) else 0
    }

    fun getLoggedInUserReferralRewardsHistory(): List<com.example.data.model.ReferralRewardRecord> {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) return emptyList()
        val myCode = adminSettings.getOrCreateUserReferralCode(email)
        return adminConfig.value.referralRewardsHistory.filter {
            it.referrerEmail.equals(email, ignoreCase = true) ||
                (myCode.isNotBlank() && it.referrerCode.equals(myCode, ignoreCase = true))
        }
    }

    fun getLoggedInUserReferredMembers(): List<com.example.data.model.ReferredMemberSummary> {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) return emptyList()
        return adminSettings.getReferredMembersForUser(email, orders.value)
    }

    fun getLoggedInUserWalletTransactions(): List<com.example.data.model.WalletTransactionEntry> {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) return emptyList()
        return adminSettings.getUserWalletTransactions(email, orders.value)
    }

    fun getCustomerWalletTransactions(emailOrAccountId: String): List<com.example.data.model.WalletTransactionEntry> {
        val key = emailOrAccountId.trim().lowercase()
        if (key.isBlank()) return emptyList()
        return adminSettings.getUserWalletTransactions(key, orders.value)
    }

    fun getLoggedInUserSavedAccounts(): List<com.example.data.model.SavedServiceAccount> {
        val email = loggedInUserEmail.value.trim().lowercase()
        return adminSettings.getSavedServiceAccounts(email)
    }

    fun saveOrUpdateUserServiceAccount(
        serviceName: String,
        accountId: String,
        label: String,
        isDefault: Boolean,
        editId: String? = null
    ) {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) return
        adminSettings.saveOrUpdateServiceAccount(email, serviceName, accountId, label, isDefault, editId)
        viewModelScope.launch {
            if (accountId.isNotBlank()) {
                repository.saveUid(serviceName.uppercase(), accountId.trim(), label.trim())
            }
        }
    }

    fun deleteUserServiceAccount(itemId: String) {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) return
        adminSettings.deleteSavedServiceAccount(email, itemId)
    }

    fun setDefaultUserServiceAccount(itemId: String) {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) return
        adminSettings.setDefaultSavedServiceAccount(email, itemId)
    }

    fun markSingleNotificationRead(notificationId: String) {
        adminSettings.markSingleNotificationRead(notificationId)
    }

    fun markAllUserNotificationsRead() {
        adminSettings.markAllNotificationsRead(loggedInUserEmail.value)
    }

    fun sendAdminCustomNotification(targetEmail: String, title: String, message: String) {
        if (title.isBlank() && message.isBlank()) return
        adminSettings.pushCustomNotification(
            customerEmail = targetEmail.trim().lowercase(),
            title = title.trim().ifBlank { "📢 Admin Notice" },
            message = message.trim(),
            status = "Completed"
        )
        cloudRepository.pushAdminNotificationToCloud(
            targetEmail = targetEmail.trim().lowercase(),
            title = title.trim().ifBlank { "📢 Admin Notice" },
            message = message.trim()
        )
    }

    fun refreshUserProfileFromCloud(onComplete: () -> Unit = {}) {
        val email = loggedInUserEmail.value.trim().lowercase()
        if (email.isBlank()) {
            onComplete()
            return
        }
        cloudRepository.fetchFullCustomerCloudProfileByEmail(email) { cloudName, cloudPhone, cloudPhoto, cloudWallet, cloudRefCode, cloudReferredBy, cloudRefEarnings, cloudRefCount ->
            val resolvedName = cloudName.ifBlank { loggedInUserName.value }
            val resolvedPhone = cloudPhone.ifBlank { loggedInUserPhone.value }
            adminSettings.setLoggedInUser(email, resolvedName, resolvedPhone, cloudPhoto.takeIf { it.isNotBlank() })
            adminSettings.syncCloudUserWalletAndReferral(
                email = email,
                cloudWalletBalance = cloudWallet,
                cloudReferralCode = cloudRefCode,
                cloudReferredBy = cloudReferredBy,
                cloudReferralEarnings = cloudRefEarnings,
                cloudReferralCount = cloudRefCount
            )
            onComplete()
        }
        onComplete()
    }

    fun getSavedUidForCategory(category: ServiceCategory): String {
        return adminSettings.getDefaultServiceUid(loggedInUserEmail.value, category.name)
    }

    fun updateUserProfile(name: String, phone: String, photoUri: String? = null) {
        updateUserProfileDetails(
            context = getApplication(),
            newName = name,
            newPhone = phone,
            newPhotoUri = photoUri?.ifBlank { null }
        )
    }

    fun saveDefaultServiceUids(chametId: String, tiktokId: String, ffId: String) {
        val email = loggedInUserEmail.value
        adminSettings.saveDefaultServiceUid(email, ServiceCategory.CHAMET.name, chametId)
        adminSettings.saveDefaultServiceUid(email, ServiceCategory.TIKTOK.name, tiktokId)
        adminSettings.saveDefaultServiceUid(email, ServiceCategory.FREEFIRE.name, ffId)
        if (chametId.isNotBlank()) adminSettings.saveOrUpdateServiceAccount(email, "CHAMET", chametId, "Chamet Default", true)
        if (tiktokId.isNotBlank()) adminSettings.saveOrUpdateServiceAccount(email, "TIKTOK", tiktokId, "TikTok Default", true)
        if (ffId.isNotBlank()) adminSettings.saveOrUpdateServiceAccount(email, "FREEFIRE", ffId, "Free Fire Default", true)
        viewModelScope.launch {
            if (chametId.isNotBlank()) repository.saveUid(ServiceCategory.CHAMET.name, chametId.trim())
            if (tiktokId.isNotBlank()) repository.saveUid(ServiceCategory.TIKTOK.name, tiktokId.trim())
            if (ffId.isNotBlank()) repository.saveUid(ServiceCategory.FREEFIRE.name, ffId.trim())
        }
    }

    fun adjustCustomerWalletByAdmin(
        accountIdOrEmail: String,
        amountDelta: Double,
        reason: String = "",
        adminReference: String = ""
    ) {
        val key = accountIdOrEmail.trim()
        if (key.isBlank() || amountDelta == 0.0) return
        val email = if (key.contains("@")) key.lowercase() else (adminConfig.value.customerMetadata[key]?.email ?: "")
        val currentBal = if (email.isNotBlank()) {
            adminSettings.getUserWalletBalance(email)
        } else {
            adminConfig.value.customerMetadata[key]?.walletBalanceBdt ?: 0.0
        }
        val nextBal = (currentBal + amountDelta).coerceAtLeast(0.0)
        adminAdjustCustomerWalletBalance(
            context = getApplication(),
            accountId = key,
            email = email,
            newBalanceBdt = nextBal,
            reason = reason,
            adminReference = adminReference
        )
    }

    fun adminAdjustCustomerWalletBalance(
        context: Context,
        accountId: String,
        email: String,
        newBalanceBdt: Double,
        reason: String = "",
        adminReference: String = ""
    ) {
        adminSettings.adjustCustomerWalletBalance(
            accountId = accountId,
            email = email,
            newBalanceBdt = newBalanceBdt,
            reason = reason,
            adminReference = adminReference
        )
        cloudRepository.syncAdminConfigToCloud(adminConfig.value)
        val cleanEmail = email.trim().lowercase().ifBlank {
            if (accountId.contains("@")) accountId.trim().lowercase() else ""
        }
        if (cleanEmail.isNotBlank()) {
            cloudRepository.upsertAuthenticatedUserToCloud(
                uid = "",
                email = cleanEmail,
                displayName = adminSettings.getSavedAccountName(cleanEmail),
                phone = adminSettings.getSavedAccountPhone(cleanEmail),
                photoUri = adminSettings.getSavedAccountPhotoUri(cleanEmail),
                walletBalanceBdt = newBalanceBdt.coerceAtLeast(0.0),
                referralCode = adminSettings.getOrCreateUserReferralCode(cleanEmail)
            )
        }
        Toast.makeText(
            context,
            "✅ ওয়ালেট ব্যালেন্স ৳${String.format(Locale.US, "%,.2f", newBalanceBdt.coerceAtLeast(0.0))} আপডেট করা হয়েছে!",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun updateReferralSettings(
        context: Context,
        isEnabled: Boolean,
        baseOrderAmountBdt: Double,
        commissionBdt: Double,
        minOrderAmountBdt: Double,
        signupBonusBdt: Double,
        proportionalScaling: Boolean
    ) {
        val updated = adminConfig.value.copy(
            isReferralEnabled = isEnabled,
            referralBaseOrderAmountBdt = baseOrderAmountBdt.coerceAtLeast(1.0),
            referralCommissionBdt = commissionBdt.coerceAtLeast(0.0),
            referralMinOrderAmountBdt = minOrderAmountBdt.coerceAtLeast(0.0),
            referralSignupBonusBdt = signupBonusBdt.coerceAtLeast(0.0),
            referralProportionalScaling = proportionalScaling
        )
        adminSettings.updateConfig(updated)
        cloudRepository.syncAdminConfigToCloud(updated)
        Toast.makeText(context, "✅ রেফারেল সেটিংস সেভ ও ক্লাউডে সিঙ্ক হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    fun withdrawFromWallet(context: Context, amount: Double, method: String, accountNum: String): Boolean {
        if (adminSettings.userWalletBalance.value < amount) {
            Toast.makeText(context, "অপর্যাপ্ত ওয়ালেট ব্যালেন্স!", Toast.LENGTH_SHORT).show()
            return false
        }
        val success = adminSettings.deductFundsFromWallet(amount)
        if (success) {
            viewModelScope.launch {
                val orderNum = "WTH-${System.currentTimeMillis().toString().takeLast(6)}"
                val record = OrderRecord(
                    orderNumber = orderNum,
                    category = "Wallet Cashout",
                    itemName = "Wallet Cash Out ($method)",
                    amountOrQuantity = "- ৳${String.format(Locale.US, "%,.2f", amount)} BDT",
                    priceBdt = amount,
                    userAccountId = accountNum,
                    paymentMethod = method,
                    transactionTrxId = "PENDING-${orderNum.takeLast(4)}",
                    orderNote = "Withdrawal request to $accountNum",
                    status = "Processing",
                    timestamp = System.currentTimeMillis(),
                    contactPlatform = "Wallet",
                    customerEmail = loggedInUserEmail.value.trim().lowercase(),
                    customerPhone = loggedInUserPhone.value.trim(),
                    customerName = loggedInUserName.value.trim()
                )
                repository.insertOrder(record)
                cloudRepository.uploadOrderToCloud(record)
                adminSettings.pushOrderStatusNotification(record, "Processing")
                Toast.makeText(context, "ক্যাশআউট অনুরোধ সফলভাবে গ্রহণ করা হয়েছে!", Toast.LENGTH_LONG).show()
            }
            return true
        }
        return false
    }

    fun setWalletBalance(amount: Double) {
        adminSettings.updateUserWalletBalance(amount)
    }

    private var autoLiveChatJob: kotlinx.coroutines.Job? = null

    fun triggerOrderPlacedAiMessageAndAutoLiveChat(
        record: OrderRecord,
        delayMs: Long = 3000L
    ) {
        val finalEmail = record.customerEmail.trim().lowercase()
            .ifBlank { loggedInUserEmail.value.trim().lowercase() }
            .ifBlank { supportChatUserEmail.value.trim().lowercase() }
            .ifBlank { getSupportDeviceId().trim().lowercase() }
        val finalName = record.customerName.trim()
            .ifBlank { loggedInUserName.value.trim() }
            .ifBlank { supportChatUserName.value.trim() }
            .ifBlank { finalEmail.substringBefore("@").ifBlank { "প্রিয় গ্রাহক" } }
        val finalContact = record.customerPhone.trim()
            .ifBlank { loggedInUserPhone.value.trim() }
            .ifBlank { supportChatUserPhone.value.trim() }

        val isApproved = record.status.equals("Completed", ignoreCase = true) ||
            record.status.equals("Approved", ignoreCase = true)
        val isCancelled = record.status.equals("Cancelled", ignoreCase = true) ||
            record.status.equals("Incorrect TrxID", ignoreCase = true)

        val statusLabel = when {
            isApproved -> "✅ এপ্রুভড (Approved)"
            isCancelled -> "❌ বাতিল (Cancelled)"
            else -> "⏳ পেন্ডিং (Pending - ভেরিফিকেশন চলছে)"
        }

        val isAddMoney = record.category.contains("Wallet", ignoreCase = true) ||
            record.itemName.contains("Add Money", ignoreCase = true)
        val isTikTokOrder = record.category.contains("TikTok", ignoreCase = true) ||
            record.itemName.contains("TikTok", ignoreCase = true)

        val aiOrderSummaryText = buildString {
            append("🤖 *AI অটোমেটিক অর্ডার আপডেট (Instant Order Assistant)*\n\n")
            append("আসসালামু আলাইকুম $finalName! আপনি এইমাত্র ${if (isAddMoney) "অ্যাড মানি (Add Money)" else if (isTikTokOrder) "TikTok কয়েন অর্ডার" else "সার্ভিস অর্ডার"} কনফার্ম করেছেন। আপনার অর্ডারের বিস্তারিত নিচে দেওয়া হলো:\n\n")
            append("📋 *অর্ডার আইডি:* #${record.orderNumber}\n")
            append("📦 *কি কিনেছেন / সার্ভিস:* ${record.itemName}\n")
            append("💎 *পরিমাণ:* ${record.amountOrQuantity}\n")
            append("💰 *মোট পেমেন্ট:* ৳${String.format(Locale.US, "%,.0f", record.priceBdt)} BDT\n")
            if (record.userAccountId.isNotBlank()) {
                append("👤 *${if (isTikTokOrder) "TikTok লগইন তথ্য" else "একাউন্ট / UID"}:* ${record.userAccountId}\n")
            }
            append("💳 *পেমেন্ট মাধ্যম:* ${record.paymentMethod}\n")
            if (record.transactionTrxId.isNotBlank()) {
                append("🔖 *TrxID:* ${record.transactionTrxId}\n")
            }
            append("📊 *বর্তমান স্ট্যাটাস:* $statusLabel\n\n")
            if (isApproved) {
                append("🎉 অভিনন্দন! আপনার অর্ডারটি *এপ্রুভড (Approved)* হয়েছে।")
            } else if (isTikTokOrder) {
                append("📩 *জরুরি বার্তা:* কিছুক্ষণের মধ্যে আপনার সাথে আমাদের এডমিন যোগাযোগ করবে। কারণ আপনার জিমেইল (Gmail) বা ফোন নাম্বারে একটি কোড (Verification Code) যাবে—ওই কোডটা এখানে লাইভ চ্যাটে দিতে হবে। তারপর এডমিন আপনার TikTok-এ কয়েন পাঠাতে পারবে এবং আপনার অর্ডারটি এপ্রুভ (Approve) হয়ে যাবে!")
            } else {
                append("⏳ আপনার অর্ডারটি বর্তমানে *পেন্ডিং (Pending)* অবস্থায় আছে। আমাদের এডমিন প্যানেল থেকে আপনার ট্রানজেকশন আইডি যাচাই করে দ্রুত *এপ্রুভ (Approve)* করে দেওয়া হবে। আপনার কোনো প্রশ্ন থাকলে এখানে লিখতে পারেন!")
            }
        }

        if (finalEmail.isNotBlank()) {
            val aiMsg = SupportChatMessage(
                id = "AI-ORD-${record.orderNumber}-${System.currentTimeMillis()}",
                conversationId = finalEmail,
                senderName = finalName,
                senderContact = finalContact,
                senderEmail = finalEmail,
                message = aiOrderSummaryText,
                imageUrl = "",
                senderRole = "AI",
                adminReply = "AI_AUTO_REPLIED",
                isReadByAdmin = false,
                isReadByUser = false,
                timestamp = System.currentTimeMillis()
            )
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                repository.insertSupportMessage(aiMsg)
            }
            cloudRepository.sendSupportMessageToCloud(aiMsg) { _, _ -> }
        }

        autoLiveChatJob?.cancel()
        autoLiveChatJob = viewModelScope.launch {
            if (delayMs > 0L) {
                kotlinx.coroutines.delay(delayMs)
            }
            _selectedPackageForGateway.value = null
            _selectedOrderForReceipt.value = null
            _showExchangeOrderDialog.value = false
            _showEpayOrderDialog.value = false
            _orderForm.value = null
            _showSupportChatDialog.value = true
            if (finalEmail.isNotBlank()) {
                markSupportConversationReadByUser(finalEmail)
            }
        }
    }

    private fun sendAiOrderStatusUpdateToChat(order: OrderRecord, newStatus: String) {
        val finalEmail = order.customerEmail.trim().lowercase()
            .ifBlank { loggedInUserEmail.value.trim().lowercase() }
        if (finalEmail.isBlank()) return
        val finalName = order.customerName.trim()
            .ifBlank { adminSettings.getSavedAccountName(finalEmail) }
            .ifBlank { finalEmail.substringBefore("@") }
        val finalContact = order.customerPhone.trim()
            .ifBlank { adminSettings.getSavedAccountPhone(finalEmail) }

        val isApproved = newStatus.equals("Completed", ignoreCase = true) ||
            newStatus.equals("Approved", ignoreCase = true)
        val isCancelled = newStatus.equals("Cancelled", ignoreCase = true) ||
            newStatus.equals("Incorrect TrxID", ignoreCase = true)

        val statusLabel = when {
            isApproved -> "✅ এপ্রুভড (Approved / Completed)"
            isCancelled -> "❌ বাতিল / ভুল TrxID ($newStatus)"
            else -> "⏳ পেন্ডিং (Pending / Processing)"
        }

        val statusUpdateText = buildString {
            append("🤖 *AI অর্ডার স্ট্যাটাস আপডেট*\n\n")
            append("📋 *অর্ডার আইডি:* #${order.orderNumber}\n")
            append("📦 *সার্ভিস:* ${order.itemName} (${order.amountOrQuantity})\n")
            append("💰 *অ্যামাউন্ট:* ৳${String.format(Locale.US, "%,.0f", order.priceBdt)} BDT\n")
            append("📊 *নতুন স্ট্যাটাস:* $statusLabel\n\n")
            when {
                isApproved -> append("🎉 আপনার অর্ডার / অ্যাড মানি রিকোয়েস্টটি এডমিন প্যানেল থেকে *এপ্রুভ (Approve)* করা হয়েছে! আমাদের সাথে থাকার জন্য ধন্যবাদ।")
                isCancelled -> append("⚠️ দুঃখিত, আপনার অর্ডারটি সম্পন্ন করা যায়নি ($newStatus)। অনুগ্রহ করে সঠিক TrxID বা স্ক্রিনশট এখানে পাঠান।")
                else -> append("⏳ আপনার অর্ডারটি বর্তমানে প্রসেসিংয়ে আছে।")
            }
        }

        val aiStatusMsg = SupportChatMessage(
            id = "AI-STATUS-${order.orderNumber}-${System.currentTimeMillis()}",
            conversationId = finalEmail,
            senderName = finalName,
            senderContact = finalContact,
            senderEmail = finalEmail,
            message = statusUpdateText,
            imageUrl = "",
            senderRole = "AI",
            adminReply = "AI_AUTO_REPLIED",
            isReadByAdmin = true,
            isReadByUser = false,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.insertSupportMessage(aiStatusMsg)
        }
        cloudRepository.sendSupportMessageToCloud(aiStatusMsg) { _, _ -> }
    }

    fun openSupportChatDialog() {
        autoLiveChatJob?.cancel()
        _selectedPackageForGateway.value = null
        _selectedOrderForReceipt.value = null
        _showSupportChatDialog.value = true
        val activeEmail = loggedInUserEmail.value.trim().lowercase()
        if (activeEmail.isNotEmpty()) {
            markSupportConversationReadByUser(activeEmail)
        }
    }

    fun dismissSupportChatDialog() {
        _showSupportChatDialog.value = false
    }

    fun getSupportConversationMode(conversationId: String): String {
        val cleanKey = conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
        if (cleanKey.isBlank()) return "AI_AUTO"
        val mapMode = adminSettings.getConversationChatMode(cleanKey)
        // Also inspect latest mode marker in messages in case cloud message arrived before config snapshot
        val latestMarkerMsg = supportMessages.value
            .filter {
                val msgEmail = it.senderEmail.trim().lowercase()
                val msgConv = it.conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
                (msgConv == cleanKey || msgEmail == cleanKey) && it.adminReply.startsWith("MODE:")
            }
            .maxByOrNull { it.timestamp }
        val markerMode = latestMarkerMsg?.adminReply?.removePrefix("MODE:")?.trim()?.uppercase()
        return when {
            mapMode == "HANDOFF_REQUESTED" || mapMode == "ADMIN_TAKEOVER" -> mapMode
            markerMode == "HANDOFF_REQUESTED" || markerMode == "ADMIN_TAKEOVER" -> markerMode
            else -> "AI_AUTO"
        }
    }

    fun requestSupportAdminHandoff(context: Context) {
        val finalEmail = loggedInUserEmail.value.trim().lowercase()
        if (finalEmail.isEmpty()) return
        val finalName = loggedInUserName.value.trim().ifBlank { finalEmail.substringBefore("@") }
        val finalContact = loggedInUserPhone.value.trim()

        _isAiTypingSupportReply.value = false
        adminSettings.setConversationChatMode(finalEmail, "HANDOFF_REQUESTED")
        cloudRepository.syncSupportHandoffStateToCloud(
            adminConfig.value.supportChatHandoffMap,
            adminConfig.value.isAiAutoChatEnabled
        )

        val handoffMsg = SupportChatMessage(
            id = "AI-HANDOFF-${System.currentTimeMillis()}",
            conversationId = finalEmail,
            senderName = finalName,
            senderContact = finalContact,
            senderEmail = finalEmail,
            message = "🚨 আপনি সরাসরি Admin-এর সাথে কথা বলার অনুরোধ করেছেন (Human Admin Handoff Active)।\n\n🤖 এখন থেকে AI Auto-Reply বন্ধ থাকবে এবং আমাদের Official Admin সরাসরি আপনার সাথে চ্যাট করবেন। অনুগ্রহ করে আপনার মেসেজ বা অর্ডারের বিস্তারিত লিখে রাখুন।",
            imageUrl = "",
            senderRole = "AI",
            adminReply = "MODE:HANDOFF_REQUESTED",
            isReadByAdmin = false,
            isReadByUser = true,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.insertSupportMessage(handoffMsg)
        }
        cloudRepository.sendSupportMessageToCloud(handoffMsg) { _, _ -> }
        Toast.makeText(context, "👨‍💻 চ্যাটটি Admin-এর কাছে হস্তান্তর করা হয়েছে। AI Auto-Reply বন্ধ আছে।", Toast.LENGTH_SHORT).show()
    }

    fun resumeSupportAiAutoReply(context: Context) {
        val finalEmail = loggedInUserEmail.value.trim().lowercase()
        if (finalEmail.isEmpty()) return
        val finalName = loggedInUserName.value.trim().ifBlank { finalEmail.substringBefore("@") }
        val finalContact = loggedInUserPhone.value.trim()

        adminSettings.setConversationChatMode(finalEmail, "AI_AUTO")
        cloudRepository.syncSupportHandoffStateToCloud(
            adminConfig.value.supportChatHandoffMap,
            adminConfig.value.isAiAutoChatEnabled
        )

        val resumeMsg = SupportChatMessage(
            id = "AI-RESUME-${System.currentTimeMillis()}",
            conversationId = finalEmail,
            senderName = finalName,
            senderContact = finalContact,
            senderEmail = finalEmail,
            message = "🤖 AI Auto-Assistant পুনরায় চালু হয়েছে! আপনার যেকোনো প্রশ্নে আমি তাৎক্ষণিক সাহায্য করতে প্রস্তুত।",
            imageUrl = "",
            senderRole = "AI",
            adminReply = "MODE:AI_AUTO",
            isReadByAdmin = true,
            isReadByUser = true,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.insertSupportMessage(resumeMsg)
        }
        cloudRepository.sendSupportMessageToCloud(resumeMsg) { _, _ -> }
        Toast.makeText(context, "🤖 AI Auto-Reply পুনরায় চালু করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    fun setSupportConversationModeByAdmin(
        context: Context,
        conversationId: String,
        targetCustomerName: String,
        targetCustomerContact: String,
        targetCustomerEmail: String,
        mode: String
    ) {
        val normalizedEmail = targetCustomerEmail.trim().lowercase()
        val normalizedConvId = if (normalizedEmail.isNotEmpty()) {
            normalizedEmail
        } else {
            conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
        }
        if (normalizedConvId.isBlank()) return

        val cleanMode = mode.trim().uppercase()
        adminSettings.setConversationChatMode(normalizedConvId, cleanMode)
        cloudRepository.syncSupportHandoffStateToCloud(
            adminConfig.value.supportChatHandoffMap,
            adminConfig.value.isAiAutoChatEnabled
        )

        val statusNotice = if (cleanMode == "AI_AUTO") {
            "🤖 Admin চ্যাটটি পুনরায় AI Auto-Reply মোডে ফিরিয়ে দিয়েছেন। এখন থেকে তাৎক্ষণিক সহায়তার জন্য AI সক্রিয় থাকবে।"
        } else {
            "👨‍💻 Official Admin চ্যাটটি গ্রহণ করেছেন (Human Support Mode Active)। AI Auto-Reply বন্ধ করা হয়েছে।"
        }

        val modeMsg = SupportChatMessage(
            id = "AI-MODE-${System.currentTimeMillis()}",
            conversationId = normalizedConvId,
            senderName = targetCustomerName.ifBlank { "গ্রাহক" },
            senderContact = targetCustomerContact,
            senderEmail = normalizedEmail.ifBlank { normalizedConvId },
            message = statusNotice,
            imageUrl = "",
            senderRole = "AI",
            adminReply = "MODE:$cleanMode",
            isReadByAdmin = true,
            isReadByUser = false,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.insertSupportMessage(modeMsg)
        }
        cloudRepository.sendSupportMessageToCloud(modeMsg) { _, _ -> }

        val toastText = if (cleanMode == "AI_AUTO") {
            "🤖 এই চ্যাটে AI Auto-Reply চালু করা হয়েছে"
        } else {
            "👨‍💻 আপনি এই চ্যাটটি Takeover করেছেন (AI Auto-Reply বন্ধ)"
        }
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
    }

    fun toggleMasterAiAutoChat(context: Context, enabled: Boolean) {
        adminSettings.setAiAutoChatMasterEnabled(enabled)
        cloudRepository.syncSupportHandoffStateToCloud(
            adminConfig.value.supportChatHandoffMap,
            enabled
        )
        val msg = if (enabled) "🤖 গ্লোবাল AI Auto-Reply চালু করা হয়েছে" else "⏸️ গ্লোবাল AI Auto-Reply বন্ধ করা হয়েছে"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun markSupportConversationReadByUser(conversationId: String) {
        val cleanTarget = conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
        if (cleanTarget.isBlank()) return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val unreadAdminMsgs = supportMessages.value.filter {
                val msgEmail = it.senderEmail.trim().lowercase()
                val msgConv = it.conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
                (msgConv == cleanTarget || msgEmail == cleanTarget) &&
                    (it.senderRole.equals("ADMIN", ignoreCase = true) || it.senderRole.equals("AI", ignoreCase = true)) &&
                    !it.isReadByUser
            }
            if (unreadAdminMsgs.isNotEmpty()) {
                repository.insertSupportMessages(unreadAdminMsgs.map { it.copy(isReadByUser = true) })
                cloudRepository.markConversationReadByUserInCloud(unreadAdminMsgs.map { it.id })
            }
        }
    }

    fun getSupportDeviceId(): String = adminSettings.getOrCreateSupportDeviceId()

    fun saveSupportChatUserProfile(name: String, phone: String, email: String) {
        adminSettings.saveSupportChatUserInfo(name, phone, email)
    }

    fun resolveUserConversationId(
        customEmail: String = "",
        customContact: String = ""
    ): String {
        val email = loggedInUserEmail.value.trim().ifBlank { customEmail.trim() }.lowercase()
        val contact = customContact.trim().ifBlank { loggedInUserPhone.value.trim() }.lowercase()
        return when {
            email.isNotEmpty() -> email
            contact.isNotEmpty() -> contact
            else -> adminSettings.getOrCreateSupportDeviceId()
        }
    }

    fun sendSupportChatMessage(
        context: Context,
        senderName: String,
        senderContact: String,
        senderEmail: String = "",
        messageText: String,
        imageUrl: String = ""
    ) {
        val trimmedMessage = messageText.trim()
        val trimmedImage = imageUrl.trim()
        if (trimmedMessage.isEmpty() && trimmedImage.isEmpty()) return

        // Strictly use the authenticated user's email so messages never leak to another user's ID
        val finalEmail = loggedInUserEmail.value.trim().ifBlank { senderEmail.trim() }.lowercase()
        if (finalEmail.isEmpty()) return

        val finalName = senderName.trim().ifBlank {
            loggedInUserName.value.ifBlank { finalEmail.substringBefore("@") }
        }
        val finalContact = senderContact.trim().ifBlank {
            loggedInUserPhone.value
        }

        adminSettings.saveSupportChatUserInfo(finalName, finalContact, finalEmail)

        val convId = finalEmail
        _isSendingSupportMessage.value = true
        val userTimestamp = System.currentTimeMillis()
        val msgId = "MSG-$userTimestamp"
        val newMsg = SupportChatMessage(
            id = msgId,
            conversationId = convId,
            senderName = finalName,
            senderContact = finalContact,
            senderEmail = finalEmail,
            message = trimmedMessage,
            imageUrl = trimmedImage,
            senderRole = "USER",
            adminReply = "",
            isReadByAdmin = false,
            isReadByUser = true,
            timestamp = userTimestamp
        )
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.insertSupportMessage(newMsg)
        }
        cloudRepository.sendSupportMessageToCloud(newMsg) { _, _ ->
            _isSendingSupportMessage.value = false
        }

        val isBossAdmin = _isAdminLoggedIn.value ||
            finalEmail.equals("johirul958423@gmail.com", ignoreCase = true) ||
            finalEmail.equals(adminConfig.value.adminEmail, ignoreCase = true)

        if (isBossAdmin && trimmedMessage.isNotEmpty()) {
            viewModelScope.launch {
                _isAiTypingSupportReply.value = true
                val executedSummary = processAndExecuteAdminAiDirective(trimmedMessage)
                kotlinx.coroutines.delay(650)
                val bossReply = supportAiResponder.generateAdminBossReply(
                    adminMessage = trimmedMessage,
                    executedActionSummary = executedSummary,
                    adminConfig = adminConfig.value,
                    totalOrdersCount = orders.value.size,
                    pendingOrdersCount = orders.value.count { it.status == "Processing" }
                )
                val aiMsg = SupportChatMessage(
                    id = "AI-BOSS-${System.currentTimeMillis()}",
                    conversationId = convId,
                    senderName = finalName,
                    senderContact = finalContact,
                    senderEmail = finalEmail,
                    message = bossReply.replyText,
                    imageUrl = "",
                    senderRole = "AI",
                    adminReply = "",
                    isReadByAdmin = true,
                    isReadByUser = true,
                    timestamp = System.currentTimeMillis()
                )
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    repository.insertSupportMessage(aiMsg)
                }
                cloudRepository.sendSupportMessageToCloud(aiMsg) { _, _ -> }
                _isAiTypingSupportReply.value = false
            }
            return
        }

        // Trigger AI Auto Reply if AI is enabled globally and conversation is NOT handed off to Admin
        val currentConvMode = getSupportConversationMode(convId)
        val shouldAiReply = adminConfig.value.isAiAutoChatEnabled &&
            currentConvMode == "AI_AUTO" &&
            trimmedMessage.isNotEmpty()

        if (shouldAiReply) {
            viewModelScope.launch {
                _isAiTypingSupportReply.value = true
                kotlinx.coroutines.delay(900)
                // Re-check conversation mode in case customer clicked Talk to Admin during typing delay
                if (getSupportConversationMode(convId) != "AI_AUTO" || !adminConfig.value.isAiAutoChatEnabled) {
                    _isAiTypingSupportReply.value = false
                    return@launch
                }
                val recentConv = supportMessages.value.filter {
                    val mEmail = it.senderEmail.trim().lowercase()
                    val mConv = it.conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
                    mEmail == finalEmail || mConv == convId
                }
                val aiResult = supportAiResponder.generateAutoReply(
                    customerMessage = trimmedMessage,
                    customerName = finalName,
                    customerEmail = finalEmail,
                    userWalletBalance = userWalletBalance.value,
                    adminConfig = adminConfig.value,
                    allPackages = getAllPackages(),
                    allOrders = orders.value,
                    recentConversation = recentConv
                )
                val aiMsg = SupportChatMessage(
                    id = "AI-${System.currentTimeMillis()}",
                    conversationId = convId,
                    senderName = finalName,
                    senderContact = finalContact,
                    senderEmail = finalEmail,
                    message = aiResult.replyText,
                    imageUrl = "",
                    senderRole = "AI",
                    adminReply = if (aiResult.showTalkToAdminButton) "SHOW_TALK_TO_ADMIN" else "",
                    isReadByAdmin = true,
                    isReadByUser = true,
                    timestamp = System.currentTimeMillis()
                )
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    repository.insertSupportMessage(aiMsg)
                }
                cloudRepository.sendSupportMessageToCloud(aiMsg) { _, _ -> }
                _isAiTypingSupportReply.value = false
            }
        }
    }

    fun processAndExecuteAdminAiDirective(rawCommand: String): String {
        val clean = rawCommand.trim()
        if (clean.isEmpty()) return ""
        val lower = clean.lowercase()
        val actionsDone = mutableListOf<String>()

        // 1. Check if Admin wants to clear previous AI instructions
        if (lower.contains("মেমোরি ক্লিয়ার") || lower.contains("clear memory") || lower.contains("নির্দেশনা মুছে") || lower.contains("reset ai")) {
            val updated = adminSettings.clearAdminCustomAiInstructions()
            cloudRepository.syncAdminConfigToCloud(updated)
            return "🧹 পূর্বের সমস্ত কাস্টম নির্দেশনা মুছে মেমোরি রিসেট করা হয়েছে।"
        }

        // 2. Check if Admin is updating bKash / Nagad number via chat
        val phoneMatch = Regex("(01[3-9]\\d{8})").find(clean)?.value
        if (phoneMatch != null) {
            if (lower.contains("বিকাশ") || lower.contains("bkash")) {
                val updated = adminConfig.value.copy(bkashNumber = phoneMatch)
                updateAdminConfig(updated)
                actionsDone.add("🌸 bKash নাম্বার আপডেট করে `$phoneMatch` সেট করা হয়েছে।")
            }
            if (lower.contains("নগদ") || lower.contains("nagad")) {
                val updated = adminConfig.value.copy(nagadNumber = phoneMatch)
                updateAdminConfig(updated)
                actionsDone.add("🔶 Nagad নাম্বার আপডেট করে `$phoneMatch` সেট করা হয়েছে।")
            }
        }

        // 3. Check if Admin is approving all pending orders via AI command
        if ((lower.contains("সব অর্ডার এপ্রুভ") || lower.contains("approve all") || lower.contains("সব পেন্ডিং এপ্রুভ"))) {
            val pendingList = orders.value.filter { it.status == "Processing" }
            pendingList.forEach { ord ->
                updateOrderStatus(ord, "Completed")
            }
            actionsDone.add("✅ মোট ${pendingList.size} টি পেন্ডিং অর্ডার সাথে সাথে Approved করা হয়েছে।")
        }

        // 4. Always store the Admin's instruction in AI memory so AI obeys it for all customers
        val updatedCfg = adminSettings.appendAdminCustomAiInstruction(clean)
        cloudRepository.syncAdminConfigToCloud(updatedCfg)
        actionsDone.add("🧠 আপনার নির্দেশনা (\"$clean\") আমার AI মেমোরিতে স্থায়ীভাবে সেভ করা হয়েছে—এখন থেকে সব কাস্টমারের সাথে কথা বলার সময় আমি এটি ১০০% মেনে চলবো।")

        return actionsDone.joinToString("\n")
    }

    fun sendAdminAiCommand(context: Context, commandText: String) {
        val clean = commandText.trim()
        if (clean.isEmpty()) return
        val summary = processAndExecuteAdminAiDirective(clean)
        Toast.makeText(context, "🤖 AI আপনার নির্দেশনা গ্রহণ ও কার্যকর করেছে!", Toast.LENGTH_SHORT).show()
        val adminEmail = adminConfig.value.adminEmail.ifBlank { "johirul958423@gmail.com" }
        viewModelScope.launch {
            val bossReply = supportAiResponder.generateAdminBossReply(
                adminMessage = clean,
                executedActionSummary = summary,
                adminConfig = adminConfig.value,
                totalOrdersCount = orders.value.size,
                pendingOrdersCount = orders.value.count { it.status == "Processing" }
            )
            val aiMsg = SupportChatMessage(
                id = "AI-BOSS-${System.currentTimeMillis()}",
                conversationId = adminEmail,
                senderName = "Admin Boss",
                senderContact = adminConfig.value.whatsappNumber,
                senderEmail = adminEmail,
                message = "👑 **আপনার কমান্ড:** $clean\n\n${bossReply.replyText}",
                imageUrl = "",
                senderRole = "AI",
                adminReply = "",
                isReadByAdmin = true,
                isReadByUser = true,
                timestamp = System.currentTimeMillis()
            )
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                repository.insertSupportMessage(aiMsg)
            }
            cloudRepository.sendSupportMessageToCloud(aiMsg) { _, _ -> }
        }
    }

    fun clearAdminAiInstructions(context: Context) {
        val updated = adminSettings.clearAdminCustomAiInstructions()
        cloudRepository.syncAdminConfigToCloud(updated)
        Toast.makeText(context, "🧹 AI-এর কাস্টম নির্দেশনা ক্লিয়ার করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    fun sendAdminSupportChatMessage(
        context: Context,
        conversationId: String,
        targetCustomerName: String,
        targetCustomerContact: String,
        targetCustomerEmail: String,
        messageText: String,
        imageUrl: String = ""
    ) {
        val trimmedMessage = messageText.trim()
        val trimmedImage = imageUrl.trim()
        if (trimmedMessage.isEmpty() && trimmedImage.isEmpty()) return

        val normalizedEmail = targetCustomerEmail.trim().lowercase()
        val normalizedConvId = if (normalizedEmail.isNotEmpty()) {
            normalizedEmail
        } else {
            conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
        }

        // Automatically switch conversation to ADMIN_TAKEOVER when Admin sends a direct reply
        // so AI Auto-Reply does not interrupt the human Admin conversation
        adminSettings.setConversationChatMode(normalizedConvId, "ADMIN_TAKEOVER")
        cloudRepository.syncSupportHandoffStateToCloud(
            adminConfig.value.supportChatHandoffMap,
            adminConfig.value.isAiAutoChatEnabled
        )

        val msgId = "ADM-${System.currentTimeMillis()}"
        val adminMsg = SupportChatMessage(
            id = msgId,
            conversationId = normalizedConvId,
            senderName = targetCustomerName.ifBlank { "গ্রাহক" },
            senderContact = targetCustomerContact,
            senderEmail = normalizedEmail.ifBlank { normalizedConvId },
            message = trimmedMessage,
            imageUrl = trimmedImage,
            senderRole = "ADMIN",
            adminReply = "MODE:ADMIN_TAKEOVER",
            isReadByAdmin = true,
            isReadByUser = false,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.insertSupportMessage(adminMsg)
            // Mark user messages in this conversation as read by admin
            val unreadUserMsgs = supportMessages.value.filter {
                val msgEmail = it.senderEmail.trim().lowercase()
                val msgConv = it.conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
                (msgConv == normalizedConvId || (normalizedEmail.isNotEmpty() && msgEmail == normalizedEmail)) && !it.isReadByAdmin
            }
            if (unreadUserMsgs.isNotEmpty()) {
                repository.insertSupportMessages(unreadUserMsgs.map { it.copy(isReadByAdmin = true) })
                cloudRepository.markConversationReadByAdminInCloud(unreadUserMsgs.map { it.id })
            }
        }
        cloudRepository.sendSupportMessageToCloud(adminMsg) { _, _ -> }
    }

    fun markSupportConversationReadByAdmin(conversationId: String) {
        val cleanTarget = conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val unreadMsgs = supportMessages.value.filter {
                val msgEmail = it.senderEmail.trim().lowercase()
                val msgConv = it.conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
                (msgConv == cleanTarget || msgEmail == cleanTarget) && !it.isReadByAdmin
            }
            if (unreadMsgs.isNotEmpty()) {
                repository.insertSupportMessages(unreadMsgs.map { it.copy(isReadByAdmin = true) })
                cloudRepository.markConversationReadByAdminInCloud(unreadMsgs.map { it.id })
            }
        }
    }

    fun replyToSupportChatMessage(context: Context, messageId: String, replyText: String) {
        val trimmed = replyText.trim()
        if (trimmed.isEmpty()) return
        val targetMsg = supportMessages.value.find { it.id == messageId }
        if (targetMsg != null) {
            val convId = targetMsg.conversationId.ifBlank {
                targetMsg.senderEmail.ifBlank { targetMsg.senderContact.ifBlank { targetMsg.id } }
            }
            sendAdminSupportChatMessage(
                context = context,
                conversationId = convId,
                targetCustomerName = targetMsg.senderName,
                targetCustomerContact = targetMsg.senderContact,
                targetCustomerEmail = targetMsg.senderEmail,
                messageText = trimmed,
                imageUrl = ""
            )
            Toast.makeText(context, "✅ কাস্টমার মেসেজের উত্তর পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteSupportChatMessage(messageId: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.deleteSupportMessageById(messageId)
        }
        cloudRepository.deleteSupportMessageFromCloud(messageId)
    }

    fun deleteSupportConversation(conversationId: String) {
        val cleanTarget = conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
        val idsToDelete = supportMessages.value
            .filter {
                val msgEmail = it.senderEmail.trim().lowercase()
                val msgConv = it.conversationId.trim().lowercase().removePrefix("email_").removePrefix("user_")
                val fallbackKey = it.senderContact.ifBlank { it.senderName.ifBlank { it.id } }.trim().lowercase()
                when {
                    msgEmail.isNotEmpty() -> msgEmail == cleanTarget
                    msgConv.isNotEmpty() -> msgConv == cleanTarget
                    else -> fallbackKey == cleanTarget
                }
            }
            .map { it.id }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            idsToDelete.forEach { id -> repository.deleteSupportMessageById(id) }
            repository.deleteSupportMessagesByConversation(conversationId)
        }
        cloudRepository.deleteConversationFromCloud(idsToDelete)
    }
}
