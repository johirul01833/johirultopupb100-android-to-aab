package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AdminConfig
import com.example.data.local.CustomerMeta
import com.example.data.model.OrderRecord
import com.example.data.model.PromoBanner
import com.example.data.model.ReferralRewardRecord
import com.example.data.model.ServiceCategory
import com.example.data.model.SocialBoostCategory
import com.example.data.model.SocialBoostPlatform
import com.example.data.model.SocialBoostService
import com.example.data.model.SupportChatMessage
import com.example.data.model.TopupPackage
import com.example.data.model.WalletTransactionEntry
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

class FirebaseCloudRepository(context: Context) {

    private val appContext = context.applicationContext
    private val authPrefs: SharedPreferences =
        appContext.getSharedPreferences("johirul_firebase_secure_auth", Context.MODE_PRIVATE)

    private val _isCloudConnected = MutableStateFlow(false)
    val isCloudConnected: StateFlow<Boolean> = _isCloudConnected.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(0L)
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    private var ordersListener: ListenerRegistration? = null
    private var configListener: ListenerRegistration? = null
    private var notificationsListener: ListenerRegistration? = null
    private var supportMessagesListener: ListenerRegistration? = null
    private var customersListener: ListenerRegistration? = null

    private fun ensureFirebaseInitialized(): Boolean {
        return try {
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                FirebaseApp.initializeApp(appContext)
            }
            val connected = FirebaseApp.getApps(appContext).isNotEmpty()
            _isCloudConnected.value = connected
            connected
        } catch (_: Exception) {
            _isCloudConnected.value = false
            false
        }
    }

    private fun getFirestoreOrNull(): FirebaseFirestore? {
        return try {
            if (ensureFirebaseInitialized()) {
                val db = FirebaseFirestore.getInstance()
                _isCloudConnected.value = true
                db
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun getFirebaseAuthOrNull(): FirebaseAuth? {
        return try {
            if (ensureFirebaseInitialized()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun getDetectedProjectId(): String {
        return try {
            if (ensureFirebaseInitialized()) {
                FirebaseApp.getInstance().options.projectId ?: "chamet-top-up-bd"
            } else {
                "Not Connected"
            }
        } catch (_: Exception) {
            "chamet-top-up-bd"
        }
    }

    // -------------------------------------------------------------------
    // REAL-TIME FIRESTORE LISTENERS (ORDERS & ADMIN CONFIG)
    // -------------------------------------------------------------------

    fun startRealtimeSync(
        currentConfigProvider: () -> AdminConfig,
        onOrdersUpdatedFromCloud: (List<OrderRecord>) -> Unit,
        onConfigUpdatedFromCloud: (AdminConfig) -> Unit,
        onNotificationFromCloud: (
            orderNumber: String,
            status: String,
            itemName: String,
            priceBdt: Double,
            trxId: String,
            timestamp: Long,
            customerEmail: String
        ) -> Unit = { _, _, _, _, _, _, _ -> },
        onSupportMessagesUpdatedFromCloud: (List<SupportChatMessage>) -> Unit = {},
        onCustomersUpdatedFromCloud: (List<CustomerMeta>) -> Unit = {}
    ) {
        val db = getFirestoreOrNull() ?: return

        try {
            ordersListener?.remove()
            ordersListener = db.collection("orders")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    _isCloudConnected.value = true
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    val cloudOrders = snapshot.documents.mapNotNull { doc ->
                        try {
                            OrderRecord(
                                id = (doc.get("localId") as? Number)?.toLong() ?: doc.id.hashCode().toLong(),
                                orderNumber = doc.getString("orderNumber") ?: doc.id,
                                category = doc.getString("category") ?: "",
                                itemName = doc.getString("itemName") ?: "",
                                amountOrQuantity = doc.getString("amountOrQuantity") ?: "",
                                priceBdt = (doc.get("priceBdt") as? Number)?.toDouble() ?: 0.0,
                                userAccountId = doc.getString("userAccountId") ?: "",
                                paymentMethod = doc.getString("paymentMethod") ?: "",
                                transactionTrxId = doc.getString("transactionTrxId") ?: "",
                                orderNote = doc.getString("orderNote") ?: "",
                                screenshotUri = doc.getString("screenshotUri") ?: "",
                                status = doc.getString("status") ?: "Processing",
                                timestamp = (doc.get("timestamp") as? Number)?.toLong() ?: System.currentTimeMillis(),
                                contactPlatform = doc.getString("contactPlatform") ?: "In-App",
                                customerEmail = doc.getString("customerEmail") ?: "",
                                customerPhone = doc.getString("customerPhone") ?: "",
                                customerName = doc.getString("customerName") ?: ""
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }.sortedByDescending { it.timestamp }
                    onOrdersUpdatedFromCloud(cloudOrders)
                }

            customersListener?.remove()
            customersListener = db.collection("customers")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    _isCloudConnected.value = true
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    val cloudCustomers = snapshot.documents.mapNotNull { doc ->
                        try {
                            val email = (doc.getString("email") ?: "").trim().lowercase()
                            val accountId = (doc.getString("accountId") ?: email.ifBlank { doc.id }).trim()
                            if (accountId.isBlank() && email.isBlank()) return@mapNotNull null
                            CustomerMeta(
                                accountId = email.ifBlank { accountId },
                                displayName = doc.getString("displayName") ?: "",
                                email = email.ifBlank { if (accountId.contains("@")) accountId.lowercase() else "" },
                                phone = doc.getString("phone") ?: "",
                                photoUri = doc.getString("photoUri") ?: "",
                                walletBalanceBdt = (doc.get("walletBalanceBdt") as? Number)?.toDouble() ?: 0.0,
                                referralCode = doc.getString("referralCode") ?: "",
                                referredByCode = doc.getString("referredByCode") ?: "",
                                referralEarningsBdt = (doc.get("referralEarningsBdt") as? Number)?.toDouble() ?: 0.0,
                                referralCount = (doc.get("referralCount") as? Number)?.toInt() ?: 0,
                                isVip = doc.getBoolean("isVip") ?: false,
                                isBlocked = doc.getBoolean("isBlocked") ?: false,
                                adminNote = doc.getString("adminNote") ?: ""
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                    if (cloudCustomers.isNotEmpty()) {
                        onCustomersUpdatedFromCloud(cloudCustomers)
                    }
                }

            configListener?.remove()
            configListener = db.collection("app_config")
                .document("global_settings")
                .addSnapshotListener { doc, error ->
                    if (error != null || doc == null) return@addSnapshotListener
                    _isCloudConnected.value = true
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    if (!doc.exists()) {
                        syncAdminConfigToCloud(currentConfigProvider())
                        return@addSnapshotListener
                    }
                    try {
                        val base = currentConfigProvider()
                        val rawPrices = doc.get("customPrices") as? Map<*, *>
                        val parsedPrices = mutableMapOf<String, Double>()
                        rawPrices?.forEach { (k, v) ->
                            val keyStr = k?.toString() ?: return@forEach
                            val numVal = (v as? Number)?.toDouble()
                            if (numVal != null) parsedPrices[keyStr] = numVal
                        }

                        val rawDeleted = doc.get("deletedPackageIds") as? List<*>
                        val parsedDeleted = rawDeleted?.mapNotNull { it?.toString() }?.toSet() ?: base.deletedPackageIds

                        val rawCustomPkgs = doc.get("customPackages") as? List<*>
                        val parsedCustomPkgs = rawCustomPkgs?.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            val catStr = map["category"]?.toString() ?: "TIKTOK"
                            val cat = ServiceCategory.entries.find { it.name == catStr } ?: ServiceCategory.TIKTOK
                            TopupPackage(
                                id = map["id"]?.toString() ?: return@mapNotNull null,
                                category = cat,
                                name = map["name"]?.toString() ?: "",
                                subtitle = map["subtitle"]?.toString() ?: "Custom Pack",
                                amount = map["amount"]?.toString() ?: "",
                                priceBdt = (map["priceBdt"] as? Number)?.toDouble() ?: 0.0,
                                badge = map["badge"]?.toString()?.takeIf { it.isNotBlank() },
                                isPopular = map["isPopular"] as? Boolean ?: false,
                                bonus = map["bonus"]?.toString()?.takeIf { it.isNotBlank() }
                            )
                        } ?: base.customPackages

                        val rawBanners = doc.get("promoBanners") as? List<*>
                        val parsedBanners = rawBanners?.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            PromoBanner(
                                id = map["id"]?.toString() ?: return@mapNotNull null,
                                title = map["title"]?.toString() ?: "",
                                subtitle = map["subtitle"]?.toString() ?: "",
                                imageUrl = map["imageUrl"]?.toString() ?: "",
                                badge = map["badge"]?.toString() ?: "Offer",
                                actionUrl = map["actionUrl"]?.toString() ?: "",
                                serviceTarget = map["serviceTarget"]?.toString() ?: "TIKTOK",
                                gradientStartHex = map["gradientStartHex"]?.toString() ?: "#FF1F1641",
                                gradientEndHex = map["gradientEndHex"]?.toString() ?: "#FF8B104E",
                                isEnabled = map["isEnabled"] as? Boolean ?: true
                            )
                        }?.takeIf { it.isNotEmpty() } ?: base.promoBanners

                        val rawSbPlatforms = doc.get("socialBoostPlatforms") as? List<*>
                        val parsedSbPlatforms = rawSbPlatforms?.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            SocialBoostPlatform(
                                id = map["id"]?.toString() ?: return@mapNotNull null,
                                name = map["name"]?.toString() ?: "",
                                iconKey = map["iconKey"]?.toString() ?: "GENERIC",
                                description = map["description"]?.toString() ?: "",
                                displayOrder = (map["displayOrder"] as? Number)?.toInt() ?: 1,
                                isEnabled = map["isEnabled"] as? Boolean ?: true
                            )
                        }?.takeIf { it.isNotEmpty() } ?: base.socialBoostPlatforms

                        val rawSbCategories = doc.get("socialBoostCategories") as? List<*>
                        val parsedSbCategories = rawSbCategories?.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            SocialBoostCategory(
                                id = map["id"]?.toString() ?: return@mapNotNull null,
                                platformId = map["platformId"]?.toString() ?: "",
                                name = map["name"]?.toString() ?: "",
                                description = map["description"]?.toString() ?: "",
                                displayOrder = (map["displayOrder"] as? Number)?.toInt() ?: 1,
                                isEnabled = map["isEnabled"] as? Boolean ?: true
                            )
                        }?.takeIf { it.isNotEmpty() } ?: base.socialBoostCategories

                        val rawSbServices = doc.get("socialBoostServices") as? List<*>
                        val parsedSbServices = rawSbServices?.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            val resolvedId = (map["id"]?.toString() ?: map["serviceId"]?.toString()) ?: return@mapNotNull null
                            val resolvedName = map["name"]?.toString()?.takeIf { it.isNotBlank() }
                                ?: map["serviceName"]?.toString() ?: ""
                            val resolvedPrice = (map["pricePerThousandBdt"] as? Number)?.toDouble()
                                ?: (map["pricePer1000"] as? Number)?.toDouble() ?: 100.0
                            SocialBoostService(
                                id = resolvedId,
                                platformId = map["platformId"]?.toString() ?: "",
                                categoryId = map["categoryId"]?.toString() ?: "",
                                name = resolvedName,
                                pricePerThousandBdt = resolvedPrice,
                                minQuantity = (map["minQuantity"] as? Number)?.toInt() ?: 100,
                                maxQuantity = (map["maxQuantity"] as? Number)?.toInt() ?: 100000,
                                averageTime = map["averageTime"]?.toString() ?: "15 - 45 Minutes",
                                description = map["description"]?.toString() ?: "",
                                requiresLink = map["requiresLink"] as? Boolean ?: true,
                                displayOrder = (map["displayOrder"] as? Number)?.toInt() ?: 1,
                                isEnabled = (map["isEnabled"] as? Boolean) ?: (map["enabled"] as? Boolean) ?: true,
                                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            )
                        }?.takeIf { it.isNotEmpty() } ?: base.socialBoostServices

                        val rawCustMeta = doc.get("customerMetadata") as? Map<*, *>
                        val parsedCustMeta = base.customerMetadata.toMutableMap()
                        rawCustMeta?.forEach { (k, v) ->
                            val accountId = k?.toString() ?: return@forEach
                            val m = v as? Map<*, *> ?: return@forEach
                            val existing = parsedCustMeta[accountId]
                            parsedCustMeta[accountId] = CustomerMeta(
                                accountId = accountId,
                                displayName = m["displayName"]?.toString()?.ifBlank { existing?.displayName ?: "" } ?: (existing?.displayName ?: ""),
                                email = m["email"]?.toString() ?: existing?.email ?: if (accountId.contains("@")) accountId.trim().lowercase() else "",
                                phone = m["phone"]?.toString()?.ifBlank { existing?.phone ?: "" } ?: (existing?.phone ?: ""),
                                photoUri = m["photoUri"]?.toString()?.ifBlank { existing?.photoUri ?: "" } ?: (existing?.photoUri ?: ""),
                                walletBalanceBdt = (m["walletBalanceBdt"] as? Number)?.toDouble() ?: existing?.walletBalanceBdt ?: 0.0,
                                referralCode = m["referralCode"]?.toString()?.ifBlank { existing?.referralCode ?: "" } ?: (existing?.referralCode ?: ""),
                                referredByCode = m["referredByCode"]?.toString()?.ifBlank { existing?.referredByCode ?: "" } ?: (existing?.referredByCode ?: ""),
                                referralEarningsBdt = (m["referralEarningsBdt"] as? Number)?.toDouble() ?: existing?.referralEarningsBdt ?: 0.0,
                                referralCount = (m["referralCount"] as? Number)?.toInt() ?: existing?.referralCount ?: 0,
                                isVip = (m["isVip"] as? Boolean) ?: existing?.isVip ?: false,
                                isBlocked = (m["isBlocked"] as? Boolean) ?: existing?.isBlocked ?: false,
                                adminNote = m["adminNote"]?.toString()?.ifBlank { existing?.adminNote ?: "" } ?: (existing?.adminNote ?: "")
                            )
                        }

                        val rawRefRewards = doc.get("referralRewardsHistory") as? List<*>
                        val parsedRefRewards = rawRefRewards?.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            ReferralRewardRecord(
                                id = map["id"]?.toString() ?: return@mapNotNull null,
                                referrerEmail = map["referrerEmail"]?.toString() ?: "",
                                referrerCode = map["referrerCode"]?.toString() ?: "",
                                buyerEmail = map["buyerEmail"]?.toString() ?: "",
                                buyerName = map["buyerName"]?.toString() ?: "",
                                orderNumber = map["orderNumber"]?.toString() ?: "",
                                itemName = map["itemName"]?.toString() ?: "",
                                orderPriceBdt = (map["orderPriceBdt"] as? Number)?.toDouble() ?: 0.0,
                                commissionBdt = (map["commissionBdt"] as? Number)?.toDouble() ?: 0.0,
                                status = map["status"]?.toString() ?: "Completed",
                                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            )
                        } ?: base.referralRewardsHistory

                        val rawWalletTx = doc.get("walletTransactionsHistory") as? List<*>
                        val parsedWalletTx = rawWalletTx?.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            WalletTransactionEntry(
                                id = map["id"]?.toString() ?: return@mapNotNull null,
                                userEmail = map["userEmail"]?.toString()?.trim()?.lowercase() ?: "",
                                type = map["type"]?.toString() ?: "ADD_MONEY",
                                title = map["title"]?.toString() ?: "",
                                amountBdt = (map["amountBdt"] as? Number)?.toDouble() ?: 0.0,
                                isCredit = map["isCredit"] as? Boolean ?: true,
                                status = map["status"]?.toString() ?: "Completed",
                                reasonOrNote = map["reasonOrNote"]?.toString() ?: "",
                                referenceId = map["referenceId"]?.toString() ?: "",
                                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            )
                        } ?: base.walletTransactionsHistory

                        val rawEligibleServices = doc.get("referralEligibleServices") as? List<*>
                        val parsedEligibleServices = rawEligibleServices
                            ?.mapNotNull { it?.toString()?.trim()?.uppercase() }
                            ?.toSet()
                            ?.takeIf { it.isNotEmpty() }
                            ?: base.referralEligibleServices

                        val rawHandoffMap = doc.get("supportChatHandoffMap") as? Map<*, *>
                        val parsedHandoffMap = base.supportChatHandoffMap.toMutableMap()
                        rawHandoffMap?.forEach { (k, v) ->
                            val convKey = k?.toString()?.trim()?.lowercase() ?: return@forEach
                            val modeVal = v?.toString()?.trim()?.uppercase() ?: "AI_AUTO"
                            if (convKey.isNotBlank()) {
                                parsedHandoffMap[convKey] = modeVal
                            }
                        }

                        val updated = base.copy(
                            adminEmail = doc.getString("adminEmail") ?: base.adminEmail,
                            epayEmail = doc.getString("epayEmail") ?: base.epayEmail,
                            bkashNumber = doc.getString("bkashNumber") ?: base.bkashNumber,
                            bkashType = doc.getString("bkashType") ?: base.bkashType,
                            bkashInstruction = doc.getString("bkashInstruction") ?: base.bkashInstruction,
                            nagadNumber = doc.getString("nagadNumber") ?: base.nagadNumber,
                            nagadType = doc.getString("nagadType") ?: base.nagadType,
                            nagadInstruction = doc.getString("nagadInstruction") ?: base.nagadInstruction,
                            rocketNumber = doc.getString("rocketNumber") ?: base.rocketNumber,
                            isRocketEnabled = doc.getBoolean("isRocketEnabled") ?: base.isRocketEnabled,
                            whatsappNumber = doc.getString("whatsappNumber") ?: base.whatsappNumber,
                            whatsappDisplay = doc.getString("whatsappDisplay") ?: base.whatsappDisplay,
                            telegramUsername = doc.getString("telegramUsername") ?: base.telegramUsername,
                            telegramUrl = doc.getString("telegramUrl") ?: base.telegramUrl,
                            noticeTitle = doc.getString("noticeTitle") ?: base.noticeTitle,
                            noticeRule1 = doc.getString("noticeRule1") ?: base.noticeRule1,
                            noticeRule2 = doc.getString("noticeRule2") ?: base.noticeRule2,
                            noticeRule3 = doc.getString("noticeRule3") ?: base.noticeRule3,
                            noticeWarning = doc.getString("noticeWarning") ?: base.noticeWarning,
                            noticeAlert = doc.getString("noticeAlert") ?: base.noticeAlert,
                            noticeShowOnLaunch = doc.getBoolean("noticeShowOnLaunch") ?: base.noticeShowOnLaunch,
                            epayBaseRate = doc.getDouble("epayBaseRate") ?: base.epayBaseRate,
                            epayRate970Fixed = doc.getDouble("epayRate970Fixed") ?: base.epayRate970Fixed,
                            epayRate10Fixed = doc.getDouble("epayRate10Fixed") ?: base.epayRate10Fixed,
                            epayRate30Bulk = doc.getDouble("epayRate30Bulk") ?: base.epayRate30Bulk,
                            epayRate500Wholesale = doc.getDouble("epayRate500Wholesale") ?: base.epayRate500Wholesale,
                            inrBaseRate = doc.getDouble("inrBaseRate") ?: base.inrBaseRate,
                            tiktokRatePerCoin = doc.getDouble("tiktokRatePerCoin") ?: base.tiktokRatePerCoin,
                            chametRatePerDiamond = doc.getDouble("chametRatePerDiamond") ?: base.chametRatePerDiamond,
                            customPrices = if (parsedPrices.isNotEmpty()) parsedPrices else base.customPrices,
                            customPackages = parsedCustomPkgs,
                            deletedPackageIds = parsedDeleted,
                            customerMetadata = parsedCustMeta,
                            storeStatusOverride = doc.getString("storeStatusOverride") ?: base.storeStatusOverride,
                            isReferralEnabled = doc.getBoolean("isReferralEnabled") ?: base.isReferralEnabled,
                            referralCommissionMode = doc.getString("referralCommissionMode") ?: base.referralCommissionMode,
                            referralBaseOrderAmountBdt = doc.getDouble("referralBaseOrderAmountBdt") ?: base.referralBaseOrderAmountBdt,
                            referralCommissionBdt = doc.getDouble("referralCommissionBdt") ?: base.referralCommissionBdt,
                            referralCommissionPercent = doc.getDouble("referralCommissionPercent") ?: base.referralCommissionPercent,
                            referralMinOrderAmountBdt = doc.getDouble("referralMinOrderAmountBdt") ?: base.referralMinOrderAmountBdt,
                            referralMaxCommissionBdt = doc.getDouble("referralMaxCommissionBdt") ?: base.referralMaxCommissionBdt,
                            referralEligibleServices = parsedEligibleServices,
                            referralRulesText = doc.getString("referralRulesText") ?: base.referralRulesText,
                            referralSignupBonusBdt = doc.getDouble("referralSignupBonusBdt") ?: base.referralSignupBonusBdt,
                            referralNewUserBonusBdt = doc.getDouble("referralSignupBonusBdt") ?: base.referralNewUserBonusBdt,
                            minWalletAddMoneyBdt = doc.getDouble("minWalletAddMoneyBdt") ?: base.minWalletAddMoneyBdt,
                            referralProportionalScaling = doc.getBoolean("referralProportionalScaling") ?: base.referralProportionalScaling,
                            referralRewardsHistory = parsedRefRewards,
                            walletTransactionsHistory = parsedWalletTx,
                            promoBanners = parsedBanners,
                            socialBoostPlatforms = parsedSbPlatforms,
                            socialBoostCategories = parsedSbCategories,
                            socialBoostServices = parsedSbServices,
                            isAiAutoChatEnabled = doc.getBoolean("isAiAutoChatEnabled") ?: base.isAiAutoChatEnabled,
                            supportChatHandoffMap = parsedHandoffMap,
                            adminCustomAiInstructions = doc.getString("adminCustomAiInstructions") ?: base.adminCustomAiInstructions
                        )
                        onConfigUpdatedFromCloud(updated)
                    } catch (_: Exception) {
                    }
                }

            notificationsListener?.remove()
            notificationsListener = db.collection("notifications")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    _isCloudConnected.value = true
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    snapshot.documentChanges.forEach { change ->
                        try {
                            if (change.type == com.google.firebase.firestore.DocumentChange.Type.REMOVED) {
                                return@forEach
                            }
                            val doc = change.document
                            val orderNumber = doc.getString("orderNumber") ?: doc.id
                            val status = doc.getString("status") ?: "Processing"
                            val itemName = doc.getString("itemName") ?: "Order"
                            val priceBdt = (doc.get("priceBdt") as? Number)?.toDouble() ?: 0.0
                            val trxId = doc.getString("trxId") ?: ""
                            val timestamp = (doc.get("timestamp") as? Number)?.toLong() ?: System.currentTimeMillis()
                            val customerEmail = (doc.getString("customerEmail") ?: "").trim().lowercase()
                            onNotificationFromCloud(orderNumber, status, itemName, priceBdt, trxId, timestamp, customerEmail)
                        } catch (_: Exception) {
                        }
                    }
                }

            supportMessagesListener?.remove()
            supportMessagesListener = db.collection("support_messages")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    _isCloudConnected.value = true
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    val chatList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val senderEmail = doc.getString("senderEmail") ?: ""
                            val senderContact = doc.getString("senderContact") ?: ""
                            val senderName = doc.getString("senderName") ?: ""
                            val fallbackConvId = senderEmail.ifBlank {
                                senderContact.ifBlank { senderName.ifBlank { doc.id } }
                            }.trim().lowercase()
                            SupportChatMessage(
                                id = doc.getString("id") ?: doc.id,
                                conversationId = (doc.getString("conversationId") ?: "").ifBlank { fallbackConvId },
                                senderName = senderName,
                                senderContact = senderContact,
                                senderEmail = senderEmail,
                                message = doc.getString("message") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                senderRole = doc.getString("senderRole") ?: "USER",
                                adminReply = doc.getString("adminReply") ?: "",
                                isReadByAdmin = doc.getBoolean("isReadByAdmin") ?: false,
                                isReadByUser = doc.getBoolean("isReadByUser") ?: true,
                                timestamp = (doc.get("timestamp") as? Number)?.toLong() ?: System.currentTimeMillis()
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }.sortedBy { it.timestamp }
                    onSupportMessagesUpdatedFromCloud(chatList)
                }
        } catch (_: Exception) {
        }
    }

    fun sendSupportMessageToCloud(
        msg: SupportChatMessage,
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull()
        if (db == null) {
            onResult(false, null)
            return
        }
        try {
            val docId = msg.id.ifBlank { "MSG-${System.currentTimeMillis()}" }
            val payload = hashMapOf(
                "id" to docId,
                "conversationId" to msg.conversationId,
                "senderName" to msg.senderName,
                "senderContact" to msg.senderContact,
                "senderEmail" to msg.senderEmail,
                "message" to msg.message,
                "imageUrl" to msg.imageUrl,
                "senderRole" to msg.senderRole,
                "adminReply" to msg.adminReply,
                "isReadByAdmin" to msg.isReadByAdmin,
                "isReadByUser" to msg.isReadByUser,
                "timestamp" to msg.timestamp,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("support_messages")
                .document(docId)
                .set(payload, SetOptions.merge())
                .addOnSuccessListener {
                    onResult(true, null)
                }
                .addOnFailureListener { e ->
                    onResult(false, e.localizedMessage)
                }
        } catch (e: Exception) {
            onResult(false, e.localizedMessage)
        }
    }

    fun replyToSupportMessageInCloud(messageId: String, replyText: String) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull() ?: return
        try {
            val updates = mapOf(
                "adminReply" to replyText.trim(),
                "isReadByAdmin" to true,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("support_messages")
                .document(messageId)
                .set(updates, SetOptions.merge())
        } catch (_: Exception) {
        }
    }

    fun markConversationReadByAdminInCloud(messageIds: List<String>) {
        val db = getFirestoreOrNull() ?: return
        try {
            messageIds.forEach { id ->
                db.collection("support_messages")
                    .document(id)
                    .set(mapOf("isReadByAdmin" to true), SetOptions.merge())
            }
        } catch (_: Exception) {
        }
    }

    fun markConversationReadByUserInCloud(messageIds: List<String>) {
        val db = getFirestoreOrNull() ?: return
        try {
            messageIds.forEach { id ->
                db.collection("support_messages")
                    .document(id)
                    .set(mapOf("isReadByUser" to true), SetOptions.merge())
            }
        } catch (_: Exception) {
        }
    }

    fun syncSupportHandoffStateToCloud(handoffMap: Map<String, String>, isAiAutoChatEnabled: Boolean) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull() ?: return
        try {
            val docRef = db.collection("app_config").document("global_settings")
            docRef.set(
                mapOf(
                    "isAiAutoChatEnabled" to isAiAutoChatEnabled,
                    "supportChatHandoffMap" to handoffMap,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
        } catch (_: Exception) {
        }
    }

    fun deleteSupportMessageFromCloud(messageId: String) {
        val db = getFirestoreOrNull() ?: return
        try {
            db.collection("support_messages").document(messageId).delete()
        } catch (_: Exception) {
        }
    }

    fun deleteConversationFromCloud(messageIds: List<String>) {
        val db = getFirestoreOrNull() ?: return
        try {
            messageIds.forEach { id ->
                db.collection("support_messages").document(id).delete()
            }
        } catch (_: Exception) {
        }
    }

    fun upsertAuthenticatedUserToCloud(
        uid: String,
        email: String,
        displayName: String,
        phone: String = "",
        photoUri: String = "",
        walletBalanceBdt: Double? = null,
        referralCode: String = "",
        referredByCode: String = "",
        referralEarningsBdt: Double? = null,
        referralCount: Int? = null,
        isVip: Boolean? = null,
        isBlocked: Boolean? = null,
        adminNote: String? = null
    ) {
        val db = getFirestoreOrNull() ?: return
        try {
            val cleanEmail = email.trim().lowercase()
            val rawCustomerKey = cleanEmail.ifBlank { uid.trim() }.replace(Regex("[^a-zA-Z0-9_@.-]"), "_").take(64)
            if (rawCustomerKey.isNotBlank()) {
                val userMap = hashMapOf<String, Any>(
                    "accountId" to cleanEmail.ifBlank { rawCustomerKey },
                    "email" to cleanEmail,
                    "displayName" to displayName.ifBlank { cleanEmail.substringBefore("@") },
                    "lastCategory" to "Registered User",
                    "lastPaymentMethod" to "bKash / Nagad",
                    "lastOrderTimestamp" to System.currentTimeMillis()
                )
                if (uid.trim().isNotBlank()) {
                    userMap["uid"] = uid.trim()
                }
                if (phone.trim().isNotBlank()) {
                    userMap["phone"] = phone.trim()
                }
                if (photoUri.trim().isNotBlank()) {
                    userMap["photoUri"] = photoUri.trim()
                }
                if (walletBalanceBdt != null) {
                    userMap["walletBalanceBdt"] = walletBalanceBdt
                }
                if (referralCode.trim().isNotBlank()) {
                    userMap["referralCode"] = referralCode.trim().uppercase()
                }
                if (referredByCode.trim().isNotBlank()) {
                    userMap["referredByCode"] = referredByCode.trim().uppercase()
                }
                if (referralEarningsBdt != null) {
                    userMap["referralEarningsBdt"] = referralEarningsBdt
                }
                if (referralCount != null) {
                    userMap["referralCount"] = referralCount
                }
                if (isVip != null) {
                    userMap["isVip"] = isVip
                }
                if (isBlocked != null) {
                    userMap["isBlocked"] = isBlocked
                }
                if (adminNote != null) {
                    userMap["adminNote"] = adminNote
                }
                db.collection("customers")
                    .document(rawCustomerKey)
                    .set(userMap, SetOptions.merge())
            }
        } catch (_: Exception) {
        }
    }

    fun fetchCustomerProfileByEmail(
        email: String,
        onResult: (displayName: String, phone: String) -> Unit
    ) {
        val db = getFirestoreOrNull() ?: return
        try {
            val rawCustomerKey = email.trim().lowercase().replace(Regex("[^a-zA-Z0-9_@.-]"), "_").take(64)
            if (rawCustomerKey.isBlank()) return
            db.collection("customers")
                .document(rawCustomerKey)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val name = doc.getString("displayName") ?: ""
                        val phone = doc.getString("phone") ?: ""
                        if (name.isNotBlank() || phone.isNotBlank()) {
                            onResult(name, phone)
                        }
                    }
                }
        } catch (_: Exception) {
        }
    }

    fun fetchFullCustomerCloudProfileByEmail(
        email: String,
        onResult: (
            displayName: String,
            phone: String,
            photoUri: String,
            walletBalanceBdt: Double?,
            referralCode: String,
            referredByCode: String,
            referralEarningsBdt: Double?,
            referralCount: Int?
        ) -> Unit
    ) {
        val db = getFirestoreOrNull()
        if (db == null) {
            onResult("", "", "", null, "", "", null, null)
            return
        }
        try {
            val rawCustomerKey = email.trim().lowercase().replace(Regex("[^a-zA-Z0-9_@.-]"), "_").take(64)
            if (rawCustomerKey.isBlank()) {
                onResult("", "", "", null, "", "", null, null)
                return
            }
            db.collection("customers")
                .document(rawCustomerKey)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        onResult(
                            doc.getString("displayName") ?: "",
                            doc.getString("phone") ?: "",
                            doc.getString("photoUri") ?: "",
                            (doc.get("walletBalanceBdt") as? Number)?.toDouble(),
                            doc.getString("referralCode") ?: "",
                            doc.getString("referredByCode") ?: "",
                            (doc.get("referralEarningsBdt") as? Number)?.toDouble(),
                            (doc.get("referralCount") as? Number)?.toInt()
                        )
                    } else {
                        onResult("", "", "", null, "", "", null, null)
                    }
                }
                .addOnFailureListener {
                    onResult("", "", "", null, "", "", null, null)
                }
        } catch (_: Exception) {
            onResult("", "", "", null, "", "", null, null)
        }
    }

    fun restoreAndSyncCustomersToCloud(
        seedMembers: List<CustomerMeta>,
        onCustomersFetched: (List<CustomerMeta>) -> Unit = {}
    ) {
        val db = getFirestoreOrNull() ?: return
        try {
            db.collection("customers")
                .get()
                .addOnSuccessListener { snapshot ->
                    val existingDocsMap = snapshot?.documents?.associateBy { doc ->
                        (doc.getString("email") ?: doc.getString("accountId") ?: doc.id).trim().lowercase()
                    } ?: emptyMap()

                    val fetchedList = mutableListOf<CustomerMeta>()
                    snapshot?.documents?.forEach { doc ->
                        try {
                            val email = (doc.getString("email") ?: "").trim().lowercase()
                            val accountId = (doc.getString("accountId") ?: email.ifBlank { doc.id }).trim()
                            if (accountId.isNotBlank() || email.isNotBlank()) {
                                fetchedList.add(
                                    CustomerMeta(
                                        accountId = email.ifBlank { accountId },
                                        displayName = doc.getString("displayName") ?: "",
                                        email = email.ifBlank { if (accountId.contains("@")) accountId.lowercase() else "" },
                                        phone = doc.getString("phone") ?: "",
                                        photoUri = doc.getString("photoUri") ?: "",
                                        walletBalanceBdt = (doc.get("walletBalanceBdt") as? Number)?.toDouble() ?: 0.0,
                                        referralCode = doc.getString("referralCode") ?: "",
                                        referredByCode = doc.getString("referredByCode") ?: "",
                                        referralEarningsBdt = (doc.get("referralEarningsBdt") as? Number)?.toDouble() ?: 0.0,
                                        referralCount = (doc.get("referralCount") as? Number)?.toInt() ?: 0,
                                        isVip = doc.getBoolean("isVip") ?: false,
                                        isBlocked = doc.getBoolean("isBlocked") ?: false,
                                        adminNote = doc.getString("adminNote") ?: ""
                                    )
                                )
                            }
                        } catch (_: Exception) {
                        }
                    }

                    // Ensure all seedMembers (e.g. Firebase Auth accounts) exist in Firestore `customers` collection
                    seedMembers.forEach { member ->
                        val cleanEmail = member.email.trim().lowercase().ifBlank { member.accountId.trim().lowercase() }
                        val rawKey = cleanEmail.replace(Regex("[^a-zA-Z0-9_@.-]"), "_").take(64)
                        if (rawKey.isNotBlank() && !existingDocsMap.containsKey(cleanEmail)) {
                            upsertAuthenticatedUserToCloud(
                                uid = "",
                                email = cleanEmail,
                                displayName = member.displayName.ifBlank { cleanEmail.substringBefore("@") },
                                phone = member.phone,
                                photoUri = member.photoUri,
                                walletBalanceBdt = member.walletBalanceBdt,
                                referralCode = member.referralCode,
                                referredByCode = member.referredByCode,
                                referralEarningsBdt = member.referralEarningsBdt,
                                referralCount = member.referralCount,
                                isVip = member.isVip,
                                isBlocked = member.isBlocked,
                                adminNote = member.adminNote
                            )
                            fetchedList.add(member)
                        }
                    }

                    if (fetchedList.isNotEmpty()) {
                        onCustomersFetched(fetchedList)
                    }
                }
        } catch (_: Exception) {
        }
    }

    // -------------------------------------------------------------------
    // CLOUD FIRESTORE ORDER & CUSTOMER OPERATIONS
    // -------------------------------------------------------------------

    fun uploadOrderToCloud(order: OrderRecord) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull() ?: return
        try {
            val orderMap = hashMapOf(
                "localId" to order.id,
                "orderNumber" to order.orderNumber,
                "category" to order.category,
                "itemName" to order.itemName,
                "amountOrQuantity" to order.amountOrQuantity,
                "priceBdt" to order.priceBdt,
                "userAccountId" to order.userAccountId,
                "paymentMethod" to order.paymentMethod,
                "transactionTrxId" to order.transactionTrxId,
                "orderNote" to order.orderNote,
                "screenshotUri" to order.screenshotUri,
                "status" to order.status,
                "timestamp" to order.timestamp,
                "contactPlatform" to order.contactPlatform,
                "customerEmail" to order.customerEmail,
                "customerPhone" to order.customerPhone,
                "customerName" to order.customerName,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("orders")
                .document(order.orderNumber)
                .set(orderMap, SetOptions.merge())

            // Also upsert customer record in Firestore `customers` collection by email (if available) or accountId
            val cleanCustomerEmail = order.customerEmail.trim().lowercase()
            if (cleanCustomerEmail.isNotBlank()) {
                val emailKey = cleanCustomerEmail.replace(Regex("[^a-zA-Z0-9_@.-]"), "_").take(64)
                val emailCustomerMap = hashMapOf<String, Any>(
                    "accountId" to cleanCustomerEmail,
                    "email" to cleanCustomerEmail,
                    "lastCategory" to order.category,
                    "lastPaymentMethod" to order.paymentMethod,
                    "lastOrderNumber" to order.orderNumber,
                    "lastOrderPriceBdt" to order.priceBdt,
                    "lastOrderTimestamp" to order.timestamp
                )
                if (order.customerName.isNotBlank()) {
                    emailCustomerMap["displayName"] = order.customerName.trim()
                }
                if (order.customerPhone.isNotBlank()) {
                    emailCustomerMap["phone"] = order.customerPhone.trim()
                }
                db.collection("customers")
                    .document(emailKey)
                    .set(emailCustomerMap, SetOptions.merge())
            } else {
                val rawCustomerKey = order.userAccountId.trim().replace(Regex("[^a-zA-Z0-9_@.-]"), "_").take(64)
                if (rawCustomerKey.isNotBlank()) {
                    val customerMap = hashMapOf(
                        "accountId" to order.userAccountId.trim(),
                        "lastCategory" to order.category,
                        "lastPaymentMethod" to order.paymentMethod,
                        "lastOrderNumber" to order.orderNumber,
                        "lastOrderPriceBdt" to order.priceBdt,
                        "lastOrderTimestamp" to order.timestamp
                    )
                    db.collection("customers")
                        .document(rawCustomerKey)
                        .set(customerMap, SetOptions.merge())
                }
            }
        } catch (_: Exception) {
        }
    }

    fun updateOrderStatusInCloud(order: OrderRecord, newStatus: String) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull() ?: return
        try {
            val updates = mapOf(
                "status" to newStatus,
                "orderNote" to order.orderNote,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("orders")
                .document(order.orderNumber)
                .set(updates, SetOptions.merge())

            val notifMap = hashMapOf(
                "orderNumber" to order.orderNumber,
                "userAccountId" to order.userAccountId,
                "customerEmail" to order.customerEmail.trim().lowercase(),
                "status" to newStatus,
                "itemName" to order.itemName,
                "priceBdt" to order.priceBdt,
                "trxId" to order.transactionTrxId,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("notifications")
                .document(order.orderNumber)
                .set(notifMap, SetOptions.merge())
        } catch (_: Exception) {
        }
    }

    fun deleteOrderFromCloud(orderNumber: String) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull() ?: return
        try {
            db.collection("orders").document(orderNumber).delete()
            db.collection("notifications").document(orderNumber).delete()
        } catch (_: Exception) {
        }
    }

    fun clearAllOrdersFromCloud(orders: List<OrderRecord>) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull() ?: return
        try {
            orders.forEach { ord ->
                db.collection("orders").document(ord.orderNumber).delete()
                db.collection("notifications").document(ord.orderNumber).delete()
            }
            clearAllCollectionDocuments("orders")
            clearAllCollectionDocuments("notifications")
        } catch (_: Exception) {
        }
    }

    fun clearAllNotificationsFromCloud() {
        val db = getFirestoreOrNull() ?: return
        try {
            clearAllCollectionDocuments("notifications")
        } catch (_: Exception) {
        }
    }

    private fun clearAllCollectionDocuments(collectionName: String) {
        val db = getFirestoreOrNull() ?: return
        try {
            db.collection(collectionName).get()
                .addOnSuccessListener { snapshot ->
                    snapshot?.documents?.forEach { doc ->
                        doc.reference.delete()
                    }
                }
        } catch (_: Exception) {
        }
    }

    fun purgeAllDemoAndLegacyDataFromCloud() {
        val db = getFirestoreOrNull() ?: return
        try {
            clearAllCollectionDocuments("orders")
            clearAllCollectionDocuments("notifications")
            clearAllCollectionDocuments("customers")
        } catch (_: Exception) {
        }
        try {
            val ordersSnap = com.google.android.gms.tasks.Tasks.await(db.collection("orders").get())
            ordersSnap.documents.forEach { doc ->
                try {
                    com.google.android.gms.tasks.Tasks.await(doc.reference.delete())
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }
        try {
            val notifSnap = com.google.android.gms.tasks.Tasks.await(db.collection("notifications").get())
            notifSnap.documents.forEach { doc ->
                try {
                    com.google.android.gms.tasks.Tasks.await(doc.reference.delete())
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }
        try {
            val custSnap = com.google.android.gms.tasks.Tasks.await(db.collection("customers").get())
            custSnap.documents.forEach { doc ->
                try {
                    com.google.android.gms.tasks.Tasks.await(doc.reference.delete())
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }
        try {
            com.google.android.gms.tasks.Tasks.await(
                db.collection("app_config")
                    .document("global_settings")
                    .update("customerMetadata", emptyMap<String, Any>())
            )
        } catch (_: Exception) {
            try {
                db.collection("app_config")
                    .document("global_settings")
                    .set(mapOf("customerMetadata" to com.google.firebase.firestore.FieldValue.delete()), SetOptions.merge())
            } catch (_: Exception) {
            }
        }
    }

    fun syncAdminConfigToCloud(config: AdminConfig) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull() ?: return
        try {
            val customPkgsList = config.customPackages.map { pkg ->
                mapOf(
                    "id" to pkg.id,
                    "category" to pkg.category.name,
                    "name" to pkg.name,
                    "subtitle" to pkg.subtitle,
                    "amount" to pkg.amount,
                    "priceBdt" to pkg.priceBdt,
                    "badge" to (pkg.badge ?: ""),
                    "isPopular" to pkg.isPopular,
                    "bonus" to (pkg.bonus ?: "")
                )
            }

            val bannersList = config.promoBanners.map { b ->
                mapOf(
                    "id" to b.id,
                    "title" to b.title,
                    "subtitle" to b.subtitle,
                    "imageUrl" to b.imageUrl,
                    "badge" to b.badge,
                    "actionUrl" to b.actionUrl,
                    "serviceTarget" to b.serviceTarget,
                    "gradientStartHex" to b.gradientStartHex,
                    "gradientEndHex" to b.gradientEndHex,
                    "isEnabled" to b.isEnabled
                )
            }

            val sbPlatformsList = config.socialBoostPlatforms.map { p ->
                mapOf(
                    "id" to p.id,
                    "name" to p.name,
                    "iconKey" to p.iconKey,
                    "description" to p.description,
                    "displayOrder" to p.displayOrder,
                    "isEnabled" to p.isEnabled
                )
            }

            val sbCategoriesList = config.socialBoostCategories.map { c ->
                mapOf(
                    "id" to c.id,
                    "platformId" to c.platformId,
                    "name" to c.name,
                    "description" to c.description,
                    "displayOrder" to c.displayOrder,
                    "isEnabled" to c.isEnabled
                )
            }

            val sbServicesList = config.socialBoostServices.map { s ->
                mapOf(
                    "id" to s.id,
                    "serviceId" to s.id,
                    "platformId" to s.platformId,
                    "categoryId" to s.categoryId,
                    "name" to s.name,
                    "serviceName" to s.name,
                    "pricePerThousandBdt" to s.pricePerThousandBdt,
                    "pricePer1000" to s.pricePerThousandBdt,
                    "minQuantity" to s.minQuantity,
                    "maxQuantity" to s.maxQuantity,
                    "averageTime" to s.averageTime,
                    "description" to s.description,
                    "requiresLink" to s.requiresLink,
                    "displayOrder" to s.displayOrder,
                    "isEnabled" to s.isEnabled,
                    "enabled" to s.isEnabled,
                    "createdAt" to s.createdAt,
                    "updatedAt" to s.updatedAt
                )
            }

            val customerMetaMap = config.customerMetadata.mapValues { (_, meta) ->
                mapOf(
                    "displayName" to meta.displayName,
                    "email" to meta.email,
                    "phone" to meta.phone,
                    "photoUri" to meta.photoUri,
                    "walletBalanceBdt" to meta.walletBalanceBdt,
                    "referralCode" to meta.referralCode,
                    "referredByCode" to meta.referredByCode,
                    "referralEarningsBdt" to meta.referralEarningsBdt,
                    "referralCount" to meta.referralCount,
                    "isVip" to meta.isVip,
                    "isBlocked" to meta.isBlocked,
                    "adminNote" to meta.adminNote
                )
            }

            val referralRewardsList = config.referralRewardsHistory.take(200).map { r ->
                mapOf(
                    "id" to r.id,
                    "referrerEmail" to r.referrerEmail,
                    "referrerCode" to r.referrerCode,
                    "buyerEmail" to r.buyerEmail,
                    "buyerName" to r.buyerName,
                    "orderNumber" to r.orderNumber,
                    "itemName" to r.itemName,
                    "orderPriceBdt" to r.orderPriceBdt,
                    "commissionBdt" to r.commissionBdt,
                    "status" to r.status,
                    "timestamp" to r.timestamp
                )
            }

            val walletTxList = config.walletTransactionsHistory.take(250).map { tx ->
                mapOf(
                    "id" to tx.id,
                    "userEmail" to tx.userEmail,
                    "type" to tx.type,
                    "title" to tx.title,
                    "amountBdt" to tx.amountBdt,
                    "isCredit" to tx.isCredit,
                    "status" to tx.status,
                    "reasonOrNote" to tx.reasonOrNote,
                    "referenceId" to tx.referenceId,
                    "timestamp" to tx.timestamp
                )
            }

            val configData = hashMapOf(
                "adminEmail" to config.adminEmail,
                "epayEmail" to config.epayEmail,
                "bkashNumber" to config.bkashNumber,
                "bkashType" to config.bkashType,
                "bkashInstruction" to config.bkashInstruction,
                "nagadNumber" to config.nagadNumber,
                "nagadType" to config.nagadType,
                "nagadInstruction" to config.nagadInstruction,
                "rocketNumber" to config.rocketNumber,
                "isRocketEnabled" to config.isRocketEnabled,
                "whatsappNumber" to config.whatsappNumber,
                "whatsappDisplay" to config.whatsappDisplay,
                "telegramUsername" to config.telegramUsername,
                "telegramUrl" to config.telegramUrl,
                "noticeTitle" to config.noticeTitle,
                "noticeRule1" to config.noticeRule1,
                "noticeRule2" to config.noticeRule2,
                "noticeRule3" to config.noticeRule3,
                "noticeWarning" to config.noticeWarning,
                "noticeAlert" to config.noticeAlert,
                "noticeShowOnLaunch" to config.noticeShowOnLaunch,
                "epayBaseRate" to config.epayBaseRate,
                "epayRate970Fixed" to config.epayRate970Fixed,
                "epayRate10Fixed" to config.epayRate10Fixed,
                "epayRate30Bulk" to config.epayRate30Bulk,
                "epayRate500Wholesale" to config.epayRate500Wholesale,
                "inrBaseRate" to config.inrBaseRate,
                "tiktokRatePerCoin" to config.tiktokRatePerCoin,
                "chametRatePerDiamond" to config.chametRatePerDiamond,
                "customPrices" to config.customPrices,
                "customPackages" to customPkgsList,
                "deletedPackageIds" to config.deletedPackageIds.toList(),
                "customerMetadata" to customerMetaMap,
                "storeStatusOverride" to config.storeStatusOverride,
                "isReferralEnabled" to config.isReferralEnabled,
                "referralCommissionMode" to config.referralCommissionMode,
                "referralBaseOrderAmountBdt" to config.referralBaseOrderAmountBdt,
                "referralCommissionBdt" to config.referralCommissionBdt,
                "referralCommissionPercent" to config.referralCommissionPercent,
                "referralMinOrderAmountBdt" to config.referralMinOrderAmountBdt,
                "referralMaxCommissionBdt" to config.referralMaxCommissionBdt,
                "referralEligibleServices" to config.referralEligibleServices.toList(),
                "referralRulesText" to config.referralRulesText,
                "referralSignupBonusBdt" to config.referralSignupBonusBdt,
                "minWalletAddMoneyBdt" to config.minWalletAddMoneyBdt,
                "referralProportionalScaling" to config.referralProportionalScaling,
                "referralRewardsHistory" to referralRewardsList,
                "walletTransactionsHistory" to walletTxList,
                "promoBanners" to bannersList,
                "socialBoostPlatforms" to sbPlatformsList,
                "socialBoostCategories" to sbCategoriesList,
                "socialBoostServices" to sbServicesList,
                "isAiAutoChatEnabled" to config.isAiAutoChatEnabled,
                "supportChatHandoffMap" to config.supportChatHandoffMap,
                "adminCustomAiInstructions" to config.adminCustomAiInstructions,
                "updatedAt" to System.currentTimeMillis()
            )

            val docRef = db.collection("app_config").document("global_settings")
            docRef.set(configData, SetOptions.merge())
        } catch (_: Exception) {
        }
    }

    fun pushAdminNotificationToCloud(
        targetEmail: String,
        title: String,
        message: String,
        status: String = "Completed",
        priceBdt: Double = 0.0
    ) {
        _lastSyncTimestamp.value = System.currentTimeMillis()
        val db = getFirestoreOrNull() ?: return
        try {
            val docId = "ADMIN-NOTIF-${System.currentTimeMillis()}"
            val notifMap = hashMapOf(
                "orderNumber" to docId,
                "userAccountId" to targetEmail.trim(),
                "customerEmail" to targetEmail.trim().lowercase(),
                "status" to status,
                "itemName" to "$title — $message",
                "priceBdt" to priceBdt,
                "trxId" to "OFFICIAL",
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("notifications")
                .document(docId)
                .set(notifMap, SetOptions.merge())
        } catch (_: Exception) {
        }
    }

    // -------------------------------------------------------------------
    // SECURE ADMIN CREDENTIAL HASH VERIFICATION (ZERO HARDCODED PASSWORD)
    // Used ONLY when Firebase project google-services.json is not yet
    // attached in local preview, storing salted SHA-256 hash upon registration.
    // -------------------------------------------------------------------

    fun hasRegisteredAdminHash(): Boolean {
        return authPrefs.getString("admin_cred_sha256", null) != null
    }

    fun registerAdminHash(email: String, passwordPlain: String) {
        val normalizedEmail = email.trim().lowercase()
        val digest = computeSaltedHash(normalizedEmail, passwordPlain.trim())
        authPrefs.edit()
            .putString("admin_registered_email", normalizedEmail)
            .putString("admin_cred_sha256", digest)
            .apply()
    }

    fun verifyAdminHash(email: String, passwordPlain: String): Boolean {
        val savedEmail = authPrefs.getString("admin_registered_email", null) ?: return false
        val savedHash = authPrefs.getString("admin_cred_sha256", null) ?: return false
        val normalizedEmail = email.trim().lowercase()
        if (normalizedEmail != savedEmail) return false
        val inputHash = computeSaltedHash(normalizedEmail, passwordPlain.trim())
        return inputHash == savedHash
    }

    fun updateAdminHashPassword(currentPasswordPlain: String, newPasswordPlain: String): Boolean {
        val savedEmail = authPrefs.getString("admin_registered_email", null) ?: return false
        if (!verifyAdminHash(savedEmail, currentPasswordPlain)) return false
        registerAdminHash(savedEmail, newPasswordPlain)
        return true
    }

    private fun computeSaltedHash(email: String, password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val payload = "jbd_firebase_auth_v1:$email:$password"
        val bytes = md.digest(payload.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
