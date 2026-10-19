package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.AdminConfig
import com.example.data.model.OrderNotification
import com.example.data.model.OrderRecord
import com.example.data.model.ReferralRewardRecord
import com.example.data.model.ReferredMemberSummary
import com.example.data.model.SavedServiceAccount
import com.example.data.model.ServiceCategory
import com.example.data.model.TopupPackage
import com.example.data.model.WalletTransactionEntry
import com.example.ui.components.ChametReferenceSquircle
import com.example.ui.components.ExchangeReferenceSquircle
import com.example.ui.components.FreeFireGamingSquircle
import com.example.ui.components.PaymentGatewayDialog
import com.example.ui.components.TikTokReferenceSquircle
import com.example.ui.theme.BrandDarkBg
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppNavTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class ProfileAccountSubSection {
    NONE,
    SAVED_IDS,
    MY_ORDERS,
    NOTIFICATIONS,
    VIP_SUPPORT,
    SECURITY_INFO
}

@Composable
fun ProfileScreen(
    loggedInUserEmail: String,
    loggedInUserName: String,
    loggedInUserPhone: String,
    loggedInUserPhotoUri: String,
    walletBalance: Double,
    referralCode: String,
    referredByCode: String,
    referralEarningsBdt: Double,
    totalReferralsCount: Int,
    referralRewardsHistory: List<ReferralRewardRecord>,
    userOrders: List<OrderRecord>,
    adminConfig: AdminConfig,
    isAdminLoggedIn: Boolean,
    unreadNotificationCount: Int,
    savedChametUid: String,
    savedTikTokUid: String,
    savedFreeFireUid: String,
    onBackHome: () -> Unit,
    onNavigateTab: (AppNavTab) -> Unit,
    onUpdateProfile: (name: String, phone: String, photoUri: String?) -> Unit,
    onSaveDefaultUids: (chametUid: String, tiktokUid: String, freeFireUid: String) -> Unit,
    onSubmitAddMoney: (amount: Double, method: String, trxId: String, senderNumber: String) -> Unit,
    onApplyReferralCode: (code: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onOpenOrderReceipt: (OrderRecord) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSupportChat: () -> Unit,
    onSendPasswordReset: (String) -> Unit,
    onOpenLoginDialog: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onLogout: () -> Unit,
    onCopyText: (String, String) -> Unit,
    walletTransactions: List<WalletTransactionEntry> = emptyList(),
    savedAccounts: List<SavedServiceAccount> = emptyList(),
    userNotifications: List<OrderNotification> = emptyList(),
    allPackages: List<TopupPackage> = emptyList(),
    referredMembers: List<ReferredMemberSummary> = emptyList(),
    allOrders: List<OrderRecord> = emptyList(),
    onSaveServiceAccount: (serviceName: String, accountId: String, label: String, isDefault: Boolean, editId: String?) -> Unit = { _, _, _, _, _ -> },
    onDeleteServiceAccount: (String) -> Unit = {},
    onSetDefaultServiceAccount: (String) -> Unit = {},
    onMarkSingleNotificationRead: (String) -> Unit = {},
    onMarkAllNotificationsRead: () -> Unit = {},
    onOpenPackageCheckout: (TopupPackage, String) -> Unit = { _, _ -> },
    onOpenWhatsAppSupport: () -> Unit = {},
    onOpenTelegramSupport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUserLoggedIn = loggedInUserEmail.isNotBlank() || isAdminLoggedIn

    // Main Profile Segmented Tabs: 0 = 💳 Wallet, 1 = 🎁 Referral, 2 = ⚙️ Account
    var activeProfileTab by remember { mutableIntStateOf(0) }
    var activeAccountSubSection by remember { mutableStateOf(ProfileAccountSubSection.NONE) }

    // Wallet Tab expandable panels & Payment Gateway state
    var showAddMoneySheet by remember { mutableStateOf(false) }
    var showStepByStepServiceBuyer by remember { mutableStateOf(false) }
    var addMoneyAmountInput by remember { mutableStateOf("500") }
    var selectedAddMoneyMethod by remember { mutableStateOf("bKash") }
    var addMoneySenderInput by remember(loggedInUserPhone) { mutableStateOf(loggedInUserPhone) }
    var addMoneyTrxIdInput by remember { mutableStateOf("") }
    var activeAddMoneyGatewayPackage by remember { mutableStateOf<TopupPackage?>(null) }

    // Clean Edit Profile Panel (Name, Phone, Photo)
    var showEditProfilePanel by remember { mutableStateOf(false) }
    var editNameInput by remember(loggedInUserName) { mutableStateOf(loggedInUserName) }
    var editPhoneInput by remember(loggedInUserPhone) { mutableStateOf(loggedInUserPhone) }

    // Referral code input
    var friendRefCodeInput by remember { mutableStateOf("") }

    // Handle back button when inside a sub-section of Account tab
    BackHandler(enabled = activeAccountSubSection != ProfileAccountSubSection.NONE) {
        activeAccountSubSection = ProfileAccountSubSection.NONE
    }

    // Zero-permission Android Photo Picker for Profile Picture
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            onUpdateProfile(
                loggedInUserName.ifBlank { loggedInUserEmail.substringBefore("@") },
                loggedInUserPhone,
                uri.toString()
            )
            Toast.makeText(context, "✅ প্রোফাইল ছবি আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    val totalSpentBdt = remember(userOrders) {
        userOrders
            .filter {
                (it.status == "Completed" || it.status == "Approved") &&
                    !it.category.equals("Wallet Top-Up", ignoreCase = true)
            }
            .sumOf { it.priceBdt }
    }
    val completedOrdersCount = remember(userOrders) {
        userOrders.count { it.status == "Completed" || it.status == "Approved" }
    }
    val processingOrdersCount = remember(userOrders) {
        userOrders.count { it.status == "Processing" || it.status == "Pending" }
    }
    val pendingAddMoneyOrders = remember(userOrders) {
        userOrders.filter {
            (it.category.contains("Wallet Top-Up", ignoreCase = true) ||
                it.category.contains("Wallet Deposit", ignoreCase = true) ||
                it.itemName.contains("Wallet Add Money", ignoreCase = true)) &&
                (it.status == "Processing" || it.status == "Pending")
        }
    }

    // Live effective referral metrics so referred members and bonus always show at the top
    val effectiveReferralsCount = remember(totalReferralsCount, referredMembers, referralRewardsHistory) {
        maxOf(
            totalReferralsCount,
            referredMembers.size,
            referralRewardsHistory.map { it.buyerEmail.trim().lowercase() }.filter { it.isNotBlank() }.distinct().size
        )
    }
    val effectiveReferralEarnings = remember(referralEarningsBdt, referredMembers, referralRewardsHistory) {
        maxOf(
            referralEarningsBdt,
            referredMembers.sumOf { it.totalBonusEarnedBdt },
            referralRewardsHistory.sumOf { it.commissionBdt }
        )
    }

    val vipTierLabel = when {
        isAdminLoggedIn -> "👑 Official Admin"
        totalSpentBdt >= 10000 -> "💎 Diamond VIP"
        totalSpentBdt >= 3000 -> "👑 Gold VIP"
        totalSpentBdt >= 500 -> "⭐ Silver Verified"
        else -> "🛡️ Verified Member"
    }

    val memberSinceText = remember(userOrders) {
        val earliestOrderTs = userOrders.minOfOrNull { it.timestamp }
        if (earliestOrderTs != null && earliestOrderTs > 0L) {
            SimpleDateFormat("MMM yyyy", Locale.US).format(Date(earliestOrderTs))
        } else {
            SimpleDateFormat("MMM yyyy", Locale.US).format(Date(System.currentTimeMillis()))
        }
    }

    // Build effective Saved Accounts list (merges SavedServiceAccount list + legacy saved UIDs if needed)
    val effectiveSavedAccounts = remember(savedAccounts, savedChametUid, savedTikTokUid, savedFreeFireUid, loggedInUserEmail) {
        if (savedAccounts.isNotEmpty()) {
            savedAccounts
        } else {
            buildList {
                if (savedChametUid.isNotBlank()) {
                    add(
                        SavedServiceAccount(
                            id = "default_chamet",
                            userEmail = loggedInUserEmail,
                            serviceName = "CHAMET",
                            accountId = savedChametUid,
                            label = "Chamet ID",
                            isDefault = true
                        )
                    )
                }
                if (savedTikTokUid.isNotBlank()) {
                    add(
                        SavedServiceAccount(
                            id = "default_tiktok",
                            userEmail = loggedInUserEmail,
                            serviceName = "TIKTOK",
                            accountId = savedTikTokUid,
                            label = "TikTok Account",
                            isDefault = true
                        )
                    )
                }
                if (savedFreeFireUid.isNotBlank()) {
                    add(
                        SavedServiceAccount(
                            id = "default_freefire",
                            userEmail = loggedInUserEmail,
                            serviceName = "FREEFIRE",
                            accountId = savedFreeFireUid,
                            label = "Free Fire UID",
                            isDefault = true
                        )
                    )
                }
            }
        }
    }

    // Build effective Wallet Transactions list (combines explicit WalletTransactionEntry records + fallback from orders/referrals)
    val effectiveWalletTransactions = remember(walletTransactions, userOrders, referralRewardsHistory, loggedInUserEmail) {
        if (walletTransactions.isNotEmpty()) {
            walletTransactions
        } else {
            val fromOrders = userOrders
                .filter {
                    it.category.equals("Wallet Top-Up", ignoreCase = true) ||
                        it.paymentMethod.contains("Wallet", ignoreCase = true)
                }
                .map { ord ->
                    val isTopUp = ord.category.equals("Wallet Top-Up", ignoreCase = true)
                    WalletTransactionEntry(
                        id = "ORD-${ord.orderNumber}",
                        userEmail = loggedInUserEmail,
                        type = if (isTopUp) "ADD_MONEY" else "SERVICE_PURCHASE",
                        title = if (isTopUp) "Balance Added (${ord.paymentMethod})" else "Service Purchase • ${ord.itemName}",
                        amountBdt = ord.priceBdt,
                        isCredit = isTopUp,
                        status = ord.status,
                        reasonOrNote = "Order #${ord.orderNumber}",
                        referenceId = ord.orderNumber,
                        timestamp = ord.timestamp
                    )
                }
            val fromReferrals = referralRewardsHistory.map { ref ->
                WalletTransactionEntry(
                    id = ref.id,
                    userEmail = loggedInUserEmail,
                    type = "REFERRAL_BONUS",
                    title = "Referral Bonus • ${ref.referredUserName.ifBlank { "User" }}",
                    amountBdt = ref.commissionBdt,
                    isCredit = true,
                    status = ref.status,
                    reasonOrNote = "Order #${ref.orderNumber}",
                    referenceId = ref.orderNumber,
                    timestamp = ref.timestamp
                )
            }
            (fromOrders + fromReferrals).sortedByDescending { it.timestamp }
        }
    }

    if (!isUserLoggedIn) {
        // Guest State: Prompt to Login / Create Account to unlock Full Profile, Wallet & Referral
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BrandDarkBg)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140A2A)),
                border = BorderStroke(
                    1.5.dp,
                    Brush.linearGradient(listOf(GoldPrimary, NeonMagenta))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF4C1D95), Color(0xFF1E0B3B))
                                )
                            )
                            .border(2.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = GoldPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "আমার প্রোফাইল ও ওয়ালেট",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "প্রোফাইল ওয়ালেটে টাকা এড করতে, ১-ক্লিকে সার্ভিস কিনতে এবং রেফার করে কমিশন ইনকাম করতে আপনার একাউন্টে লগইন বা সাইন আপ করুন।",
                        fontSize = 12.5.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ProfilePrimaryButton(
                        text = "লগইন / সাইন আপ করুন",
                        onClick = onOpenLoginDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_guest_login_btn")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileSecondaryButton(
                        text = "হোম পেজে ফিরে যান",
                        onClick = onBackHome,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        return
    }

    val openDirectAddMoneyGateway: () -> Unit = {
        val defaultAmt = addMoneyAmountInput.toDoubleOrNull()
            ?.takeIf { it >= adminConfig.minWalletAddMoneyBdt } ?: 500.0
        showAddMoneySheet = false
        showStepByStepServiceBuyer = false
        activeAddMoneyGatewayPackage = TopupPackage(
            id = "wallet_add_${defaultAmt.toInt()}",
            category = ServiceCategory.CHAMET,
            name = "৳${String.format(Locale.US, "%,.0f", defaultAmt)} ওয়ালেট ব্যালেন্স",
            subtitle = "প্রোফাইল ওয়ালেট অ্যাড মানি",
            amount = "৳${String.format(Locale.US, "%,.0f", defaultAmt)} BDT",
            priceBdt = defaultAmt
        )
    }

    // Direct bKash / Nagad / Rocket / Upay Payment Gateway Dialog for Wallet Add Money
    activeAddMoneyGatewayPackage?.let { gwPkg ->
        PaymentGatewayDialog(
            packageItem = gwPkg,
            adminConfig = adminConfig,
            initialAccountId = loggedInUserPhone.ifBlank { loggedInUserEmail },
            walletBalance = walletBalance,
            existingOrders = allOrders.ifEmpty { userOrders },
            isWalletDeposit = true,
            onDismiss = { activeAddMoneyGatewayPackage = null },
            onSubmitOrder = { finalPkg, _, trxId, method ->
                onSubmitAddMoney(
                    finalPkg.priceBdt,
                    method,
                    trxId,
                    loggedInUserPhone
                )
            },
            onOpenLiveChat = {
                activeAddMoneyGatewayPackage = null
                onOpenSupportChat()
            },
            onCopyText = onCopyText
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandDarkBg)
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ====================================================================
        // 1. ULTRA-MODERN 3D GLASSMORPHIC FLOATING TOP NAVIGATION BAR
        // ====================================================================
        item {
            val backInteraction = remember { MutableInteractionSource() }
            val isBackPressed by backInteraction.collectIsPressedAsState()
            val backScale by animateFloatAsState(
                targetValue = if (isBackPressed) 0.94f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "back_btn_scale"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = Color(0xFF7C3AED).copy(alpha = 0.4f),
                        spotColor = GoldPrimary.copy(alpha = 0.35f)
                    ),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF160A2E),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(
                        listOf(
                            GoldPrimary.copy(alpha = 0.65f),
                            Color(0xFF7C3AED).copy(alpha = 0.5f),
                            NeonCyan.copy(alpha = 0.65f)
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF211042),
                                    Color(0xFF15092D),
                                    Color(0xFF1B0E3A)
                                )
                            )
                        )
                ) {
                    // Top 3D Glass Highlight Sheen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.14f), Color.Transparent)
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 3D Neon Pill Back Button
                        Surface(
                            onClick = {
                                if (activeAccountSubSection != ProfileAccountSubSection.NONE) {
                                    activeAccountSubSection = ProfileAccountSubSection.NONE
                                } else {
                                    onBackHome()
                                }
                            },
                            interactionSource = backInteraction,
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Transparent,
                            border = BorderStroke(
                                1.2.dp,
                                Brush.horizontalGradient(
                                    listOf(GoldPrimary.copy(alpha = 0.8f), Color(0xFF7C3AED).copy(alpha = 0.6f))
                                )
                            ),
                            modifier = Modifier
                                .scale(backScale)
                                .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = GoldPrimary.copy(alpha = 0.45f))
                                .testTag("profile_back_btn")
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF2E155C), Color(0xFF1D0C3D))
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(GoldPrimary.copy(alpha = 0.22f))
                                            .border(1.dp, GoldPrimary.copy(alpha = 0.6f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(7.dp))
                                    Text(
                                        text = if (activeAccountSubSection != ProfileAccountSubSection.NONE) "ফিরে যান" else "হোম",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Center Title + Glowing Subtitle Pill
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "আমার প্রোফাইল",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFF0B041A).copy(alpha = 0.7f),
                                border = BorderStroke(0.8.dp, GoldPrimary.copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4ADE80))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "VIP WALLET & HUB",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldLight,
                                        letterSpacing = 0.7.sp
                                    )
                                }
                            }
                        }

                        // Right 3D Squircle Action Buttons (Notifications + Edit Profile)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // 3D Notification Squircle Button
                            Box {
                                Surface(
                                    onClick = {
                                        activeProfileTab = 2
                                        activeAccountSubSection = ProfileAccountSubSection.NOTIFICATIONS
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(
                                        1.2.dp,
                                        Brush.linearGradient(
                                            listOf(GoldPrimary.copy(alpha = 0.85f), Color(0xFFF59E0B).copy(alpha = 0.4f))
                                        )
                                    ),
                                    modifier = Modifier
                                        .size(42.dp)
                                        .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = GoldPrimary.copy(alpha = 0.5f))
                                        .testTag("profile_notifications_btn")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(GoldPrimary.copy(alpha = 0.25f), Color(0xFF1F0E3D))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = "Notifications",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                if (unreadNotificationCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEF4444))
                                            .border(1.5.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // 3D Edit Profile Squircle Button
                            Surface(
                                onClick = { showEditProfilePanel = !showEditProfilePanel },
                                shape = RoundedCornerShape(14.dp),
                                color = Color.Transparent,
                                border = BorderStroke(
                                    1.2.dp,
                                    if (showEditProfilePanel) GoldPrimary else NeonCyan.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier
                                    .size(42.dp)
                                    .shadow(
                                        8.dp,
                                        RoundedCornerShape(14.dp),
                                        spotColor = if (showEditProfilePanel) GoldPrimary else NeonCyan
                                    )
                                    .testTag("profile_edit_btn")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                if (showEditProfilePanel) {
                                                    listOf(GoldPrimary.copy(alpha = 0.32f), Color(0xFF2D145C))
                                                } else {
                                                    listOf(NeonCyan.copy(alpha = 0.25f), Color(0xFF16133B))
                                                }
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = if (showEditProfilePanel) GoldPrimary else NeonCyan,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 2. FLAGSHIP 3D EXECUTIVE VIP PROFILE HERO CARD
        // Dual-Ring Neon Avatar + Modern Vector Status Badges + Contact Chips
        // + 3D Quick Action Strip + 2x2 Bento Grid of 3D Stat Cards
        // ====================================================================
        item {
            val effectiveRefCode = referralCode.ifBlank { "CTB-VIP2026" }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = Color(0xFF7C3AED).copy(alpha = 0.5f),
                        spotColor = GoldPrimary.copy(alpha = 0.45f)
                    ),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF120726)),
                border = BorderStroke(
                    1.6.dp,
                    Brush.linearGradient(
                        listOf(
                            GoldPrimary.copy(alpha = 0.95f),
                            NeonMagenta.copy(alpha = 0.8f),
                            NeonCyan.copy(alpha = 0.85f)
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF261052),
                                    Color(0xFF160932),
                                    Color(0xFF0D0520)
                                )
                            )
                        )
                        .drawBehind {
                            // Ambient Top-Left Gold Glow Orb
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        GoldPrimary.copy(alpha = 0.22f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.width * 0.48f,
                                center = Offset(size.width * 0.15f, size.height * 0.15f)
                            )
                            // Ambient Top-Right Magenta Glow Orb
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        NeonMagenta.copy(alpha = 0.18f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.width * 0.45f,
                                center = Offset(size.width * 0.88f, size.height * 0.18f)
                            )
                            // Ambient Bottom Cyan Glow Orb
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        NeonCyan.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.width * 0.5f,
                                center = Offset(size.width * 0.5f, size.height * 0.88f)
                            )
                        }
                ) {
                    // Top 3D Specular Glass Sheen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.18f), Color.Transparent)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Top Identity Section: Avatar + Badges + Name + Modern Info Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 86.dp 3D Dual-Ring Neon Avatar with Squircle Camera Badge
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("profile_avatar_picker"),
                                contentAlignment = Alignment.Center
                            ) {
                                // Outer Neon Halo Ring
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .shadow(14.dp, CircleShape, spotColor = GoldPrimary)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.sweepGradient(
                                                listOf(
                                                    GoldPrimary,
                                                    NeonMagenta,
                                                    NeonCyan,
                                                    GoldPrimary
                                                )
                                            )
                                        )
                                        .padding(3.dp)
                                ) {
                                    // Inner Avatar Frame
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(Color(0xFF4C1D95), Color(0xFF14072B))
                                                )
                                            )
                                            .border(1.5.dp, Color(0xFF0E0520), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (loggedInUserPhotoUri.isNotBlank()) {
                                            AsyncImage(
                                                model = loggedInUserPhotoUri,
                                                contentDescription = "User Profile Photo",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            val initials = loggedInUserName
                                                .ifBlank { loggedInUserEmail }
                                                .trim()
                                                .take(2)
                                                .uppercase()
                                            Text(
                                                text = initials.ifBlank { "U" },
                                                fontSize = 27.sp,
                                                fontWeight = FontWeight.Black,
                                                color = GoldPrimary
                                            )
                                        }
                                    }
                                }

                                // 3D Metallic Gold Squircle Camera Overlay
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(28.dp)
                                        .shadow(6.dp, RoundedCornerShape(9.dp), spotColor = GoldPrimary)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFFFFE082), GoldPrimary, Color(0xFFF59E0B))
                                            )
                                        )
                                        .border(1.5.dp, Color(0xFF120726), RoundedCornerShape(9.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Change Photo",
                                        tint = Color(0xFF120726),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(15.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                // Modern Vector Badges Row (No raw flat emojis!)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // VIP / Official Admin 3D Badge
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color.Transparent,
                                        border = BorderStroke(
                                            1.dp,
                                            Brush.horizontalGradient(
                                                listOf(GoldPrimary, Color(0xFFF59E0B))
                                            )
                                        ),
                                        modifier = Modifier.shadow(6.dp, RoundedCornerShape(50), spotColor = GoldPrimary)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(
                                                            GoldPrimary.copy(alpha = 0.25f),
                                                            Color(0xFF3B1668)
                                                        )
                                                    )
                                                )
                                                .padding(horizontal = 9.dp, vertical = 3.5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isAdminLoggedIn) Icons.Default.AdminPanelSettings else Icons.Default.WorkspacePremium,
                                                contentDescription = null,
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = vipTierLabel
                                                    .replace("👑 ", "")
                                                    .replace("💎 ", "")
                                                    .replace("⭐ ", "")
                                                    .replace("🛡️ ", ""),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = GoldLight,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    // Glowing Active Verified Pill
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color(0xFF052E22),
                                        border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.7f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF4ADE80))
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "Active",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF4ADE80)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // User Display Name + Glowing Verified Badge
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = loggedInUserName.ifBlank { loggedInUserEmail.substringBefore("@") },
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified Account",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                // Modern Vector Email Pill
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = NeonCyan.copy(alpha = 0.85f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = loggedInUserEmail,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFE2E8F0),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Modern Vector Phone + Member Since Chips
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        onClick = { showEditProfilePanel = true },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF072B20).copy(alpha = 0.85f),
                                        border = BorderStroke(0.8.dp, Color(0xFF34D399).copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = null,
                                                tint = Color(0xFF4ADE80),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = loggedInUserPhone.ifBlank { "নাম্বার যুক্ত করুন" },
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF86EFAC)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "• $memberSinceText",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF94A3B8),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // ================================================================
                        // 3. 2x2 BENTO GRID OF 3D NEON GLASSMORPHIC STAT CARDS
                        // ================================================================
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProfileQuickStatCard(
                                icon = Icons.Default.AccountBalanceWallet,
                                value = "৳${String.format(Locale.US, "%,.0f", walletBalance)}",
                                label = "ওয়ালেট ব্যালেন্স",
                                actionHint = "টপ-আপ",
                                accentColor = Color(0xFF34D399),
                                secondaryAccent = Color(0xFF059669),
                                onClick = {
                                    activeAccountSubSection = ProfileAccountSubSection.NONE
                                    activeProfileTab = 0
                                    openDirectAddMoneyGateway()
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ProfileQuickStatCard(
                                icon = Icons.Default.CardGiftcard,
                                value = "৳${String.format(Locale.US, "%,.0f", effectiveReferralEarnings)}",
                                label = "রেফার বোনাস",
                                actionHint = "বোনাস লগ",
                                accentColor = GoldPrimary,
                                secondaryAccent = Color(0xFFF59E0B),
                                onClick = {
                                    activeAccountSubSection = ProfileAccountSubSection.NONE
                                    activeProfileTab = 1
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProfileQuickStatCard(
                                icon = Icons.Default.ShoppingBag,
                                value = "$completedOrdersCount টি",
                                label = "সফল অর্ডার",
                                actionHint = "রিসিট দেখুন",
                                accentColor = NeonCyan,
                                secondaryAccent = Color(0xFF0284C7),
                                onClick = {
                                    activeProfileTab = 2
                                    activeAccountSubSection = ProfileAccountSubSection.MY_ORDERS
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ProfileQuickStatCard(
                                icon = Icons.Default.Person,
                                value = "$effectiveReferralsCount জন",
                                label = "রেফার মেম্বার",
                                actionHint = "তালিকা দেখুন",
                                accentColor = Color(0xFFF472B6),
                                secondaryAccent = Color(0xFF9333EA),
                                onClick = {
                                    activeAccountSubSection = ProfileAccountSubSection.NONE
                                    activeProfileTab = 1
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // ====================================================================
        // CLEAN EDIT PROFILE PANEL (Name, Phone, Profile Photo only)
        // ====================================================================
        item {
            AnimatedVisibility(
                visible = showEditProfilePanel,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                    border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.8f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✏️ প্রোফাইল তথ্য আপডেট করুন",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Surface(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF281352),
                                border = BorderStroke(1.dp, GoldPrimary)
                            ) {
                                Text(
                                    text = "📷 ছবি বদলান",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        ProfileDarkTextField(
                            value = editNameInput,
                            onValueChange = { editNameInput = it },
                            label = "আপনার নাম (Full Name)"
                        )
                        ProfileDarkTextField(
                            value = editPhoneInput,
                            onValueChange = { editPhoneInput = it },
                            label = "মোবাইল নাম্বার (Phone / WhatsApp)",
                            keyboardType = KeyboardType.Phone
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProfileSecondaryButton(
                                text = "বাতিল",
                                onClick = { showEditProfilePanel = false },
                                modifier = Modifier.weight(1f)
                            )
                            ProfilePrimaryButton(
                                text = "সেভ করুন",
                                onClick = {
                                    onUpdateProfile(editNameInput.trim(), editPhoneInput.trim(), null)
                                    showEditProfilePanel = false
                                    Toast.makeText(
                                        context,
                                        "✅ প্রোফাইল তথ্য সেভ হয়েছে!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 4. 3D NEON GLASSMORPHIC SEGMENTED NAVIGATION DOCK
        // ওয়ালেট (Wallet) | রেফারেল (Referral) | একাউন্ট (Account)
        // ====================================================================
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF7C3AED).copy(alpha = 0.35f)),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF110724),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(
                        listOf(
                            GoldPrimary.copy(alpha = 0.45f),
                            Color(0xFF4C1D95),
                            NeonCyan.copy(alpha = 0.45f)
                        )
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabItems = listOf(
                        Triple("ওয়ালেট", Icons.Default.AccountBalanceWallet, Color(0xFF34D399)),
                        Triple("রেফারেল ($effectiveReferralsCount)", Icons.Default.CardGiftcard, GoldPrimary),
                        Triple("অপশন ও অর্ডার", Icons.Default.Person, NeonCyan)
                    )
                    tabItems.forEachIndexed { index, (title, iconVec, tabAccent) ->
                        val selected = activeProfileTab == index
                        val tabInteraction = remember { MutableInteractionSource() }
                        val isTabPressed by tabInteraction.collectIsPressedAsState()
                        val tabScale by animateFloatAsState(
                            targetValue = if (isTabPressed) 0.95f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "seg_tab_scale_$index"
                        )

                        Surface(
                            onClick = {
                                activeProfileTab = index
                                activeAccountSubSection = ProfileAccountSubSection.NONE
                            },
                            interactionSource = tabInteraction,
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Transparent,
                            border = if (selected) {
                                BorderStroke(
                                    1.2.dp,
                                    Brush.horizontalGradient(
                                        listOf(tabAccent, Color.White.copy(alpha = 0.5f))
                                    )
                                )
                            } else {
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .scale(tabScale)
                                .then(
                                    if (selected) {
                                        Modifier.shadow(10.dp, RoundedCornerShape(14.dp), spotColor = tabAccent)
                                    } else {
                                        Modifier
                                    }
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (selected) {
                                            Brush.verticalGradient(
                                                listOf(
                                                    tabAccent.copy(alpha = 0.28f),
                                                    Color(0xFF2B125A),
                                                    Color(0xFF1A093B)
                                                )
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF180B30), Color(0xFF120724))
                                            )
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color.White.copy(alpha = 0.18f), Color.Transparent)
                                                )
                                            )
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = iconVec,
                                        contentDescription = title,
                                        tint = if (selected) tabAccent else Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = title,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
                                        color = if (selected) Color.White else Color(0xFFCBD5E1),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // TAB 0: 💳 WALLET DASHBOARD & WALLET TRANSACTION HISTORY
        // ====================================================================
        if (activeProfileTab == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A211A)),
                    border = BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(Color(0xFF10B981), GoldPrimary))
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF11382B),
                                        Color(0xFF091D16)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "প্রোফাইল ওয়ালেট",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFA7F3D0)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "৳${String.format(Locale.US, "%,.2f", walletBalance)} BDT",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.22f))
                                    .border(1.dp, Color(0xFF34D399), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color(0xFF4ADE80),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "এই ব্যালেন্স দিয়ে ১-ক্লিকে টিকটক কয়েন, চ্যামেট ডায়মন্ড ও সকল সার্ভিস কিনতে পারবেন।",
                            fontSize = 12.sp,
                            color = Color(0xFFD1FAE5),
                            fontWeight = FontWeight.Medium
                        )

                        if (pendingAddMoneyOrders.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val pendingTotal = pendingAddMoneyOrders.sumOf { it.priceBdt }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF3B2106).copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "⏳ পেন্ডিং অ্যাড মানি (${pendingAddMoneyOrders.size} টি): ৳${String.format(Locale.US, "%,.0f", pendingTotal)}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFFCD34D)
                                    )
                                    Text(
                                        text = "এডমিন অ্যাপ্রুভের অপেক্ষায়",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDE68A)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProfilePrimaryButton(
                                text = "➕ অ্যাড মানি (Add Money)",
                                gradientColors = listOf(Color(0xFF34D399), Color(0xFF10B981), Color(0xFF059669)),
                                contentColor = Color(0xFF032215),
                                onClick = {
                                    openDirectAddMoneyGateway()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_add_money_button")
                            )

                            ProfileSecondaryButton(
                                text = "🛍️ সার্ভিস কিনুন",
                                borderColor = Color(0xFF34D399),
                                onClick = {
                                    showStepByStepServiceBuyer = !showStepByStepServiceBuyer
                                    if (showStepByStepServiceBuyer) showAddMoneySheet = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_buy_service_button")
                            )
                        }
                    }
                }
            }

            // Expandable Luxury Add Money Amount Picker -> Opens Full bKash/Nagad PaymentGatewayDialog
            item {
                AnimatedVisibility(
                    visible = showAddMoneySheet,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(14.dp, RoundedCornerShape(22.dp), spotColor = GoldPrimary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF160B2E)),
                        border = BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(listOf(GoldPrimary, NeonMagenta, NeonCyan))
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF24114A), Color(0xFF130829))
                                    )
                                )
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "💰 ওয়ালেটে টাকা Add করুন",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary
                                    )
                                    Text(
                                        text = "কয়েন কেনার মতোই বিকাশ/নগদ সিলেক্ট করে শুধু TrxID দিয়ে ভেরিফাই করুন",
                                        fontSize = 11.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFF062E22),
                                    border = BorderStroke(1.dp, Color(0xFF34D399))
                                ) {
                                    Text(
                                        text = "বিকাশ / নগদ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF4ADE80),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = "১. কত টাকা অ্যাড করতে চান নির্বাচন করুন (সর্বনিম্ন ৳${String.format(Locale.US, "%.0f", adminConfig.minWalletAddMoneyBdt)}):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("100", "200", "300", "500", "1000", "2000", "5000").forEach { preset ->
                                    val selected = addMoneyAmountInput == preset
                                    Surface(
                                        onClick = { addMoneyAmountInput = preset },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (selected) GoldPrimary else Color(0xFF241346),
                                        border = BorderStroke(
                                            1.2.dp,
                                            if (selected) Color.White else GoldPrimary.copy(alpha = 0.45f)
                                        ),
                                        modifier = if (selected) {
                                            Modifier.shadow(8.dp, RoundedCornerShape(12.dp), spotColor = GoldPrimary)
                                        } else {
                                            Modifier
                                        }
                                    ) {
                                        Text(
                                            text = "৳$preset",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (selected) Color(0xFF120724) else GoldLight,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                                        )
                                    }
                                }
                            }

                            ProfileDarkTextField(
                                value = addMoneyAmountInput,
                                onValueChange = { addMoneyAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = "কাস্টম টাকার পরিমাণ লিখুন (Amount in BDT)",
                                keyboardType = KeyboardType.Number
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B0E38),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "ℹ️", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "নিচের বাটনে ক্লিক করলে বিকাশ/নগদ/রকেট পেমেন্ট গেটওয়ে ওপেন হবে। টাকা পাঠিয়ে শুধু ট্রানজেকশন আইডি (TrxID) বসিয়ে ভেরিফাই করলে অর্ডারটি পেন্ডিং থাকবে এবং এডমিন প্যানেল থেকে অ্যাপ্রুভ করলেই আপনার ব্যালেন্সে যোগ হয়ে যাবে।",
                                        fontSize = 11.sp,
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            ProfilePrimaryButton(
                                text = "💳 বিকাশ / নগদ পেমেন্ট গেটওয়েতে যান (৳${addMoneyAmountInput.ifBlank { "0" }})",
                                gradientColors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300), Color(0xFFF57C00)),
                                contentColor = Color(0xFF120724),
                                onClick = {
                                    val amountVal = addMoneyAmountInput.toDoubleOrNull() ?: 0.0
                                    val minAmt = adminConfig.minWalletAddMoneyBdt
                                    if (amountVal < minAmt) {
                                        Toast.makeText(
                                            context,
                                            "⚠️ সর্বনিম্ন ৳${String.format(Locale.US, "%.0f", minAmt)} টাকা এড করা যাবে",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@ProfilePrimaryButton
                                    }
                                    activeAddMoneyGatewayPackage = TopupPackage(
                                        id = "wallet_add_${amountVal.toInt()}",
                                        category = ServiceCategory.CHAMET,
                                        name = "৳${String.format(Locale.US, "%,.0f", amountVal)} ওয়ালেট ব্যালেন্স",
                                        subtitle = "প্রোফাইল ওয়ালেট অ্যাড মানি",
                                        amount = "৳${String.format(Locale.US, "%,.0f", amountVal)} BDT",
                                        priceBdt = amountVal
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_submit_add_money_btn")
                            )
                        }
                    }
                }
            }

            // Expandable Step-by-Step Service Buyer
            item {
                AnimatedVisibility(
                    visible = showStepByStepServiceBuyer,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ProfileStepByStepServiceBuyer(
                        walletBalance = walletBalance,
                        allPackages = allPackages,
                        savedAccounts = effectiveSavedAccounts,
                        onSelectPackageForCheckout = { pkg, targetUid ->
                            showStepByStepServiceBuyer = false
                            onOpenPackageCheckout(pkg, targetUid)
                        },
                        onNavigateToSpecialTab = { cat ->
                            showStepByStepServiceBuyer = false
                            val targetTab = when (cat) {
                                ServiceCategory.TIKTOK -> AppNavTab.TIKTOK
                                ServiceCategory.CHAMET -> AppNavTab.CHAMET
                                ServiceCategory.FREEFIRE -> AppNavTab.FREEFIRE
                                ServiceCategory.EPAY -> AppNavTab.EPAY
                                ServiceCategory.EXCHANGE -> AppNavTab.EXCHANGE
                                ServiceCategory.SOCIAL_BOOST -> AppNavTab.SOCIAL_BOOST
                            }
                            onNavigateTab(targetTab)
                        }
                    )
                }
            }

            // Quick Services Grid to spend Wallet Balance
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF140A29)),
                    border = BorderStroke(1.dp, Color(0xFF2E1859))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "⚡ ওয়ালেট ব্যালেন্স দিয়ে সরাসরি সার্ভিস কিনুন",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "যেকোনো সার্ভিস সিলেক্ট করে পেমেন্ট অপশনে 'Profile Wallet' সিলেক্ট করলেই ব্যালেন্স থেকে পেমেন্ট হয়ে যাবে:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickWalletServiceChip(
                                logo = { TikTokReferenceSquircle(size = 36.dp) },
                                title = "TikTok Coin",
                                accentColor = Color(0xFFEC4899),
                                onClick = { onNavigateTab(AppNavTab.TIKTOK) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickWalletServiceChip(
                                logo = { ChametReferenceSquircle(size = 36.dp) },
                                title = "Chamet",
                                accentColor = Color(0xFFA855F7),
                                onClick = { onNavigateTab(AppNavTab.CHAMET) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickWalletServiceChip(
                                logo = { ExchangeReferenceSquircle(size = 36.dp) },
                                title = "Social Boost",
                                accentColor = Color(0xFF06B6D4),
                                onClick = { onNavigateTab(AppNavTab.SOCIAL_BOOST) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickWalletServiceChip(
                                logo = { FreeFireGamingSquircle(size = 36.dp) },
                                title = "Free Fire",
                                accentColor = Color(0xFFF59E0B),
                                onClick = { onNavigateTab(AppNavTab.FREEFIRE) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ================================================================
            // 6. WALLET TRANSACTION HISTORY ("ওয়ালেট লেনদেন")
            // ================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ওয়ালেট লেনদেন (${effectiveWalletTransactions.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Wallet History",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldPrimary
                    )
                }
            }

            if (effectiveWalletTransactions.isEmpty()) {
                item {
                    ProfileEmptyStateCard(
                        icon = Icons.Default.History,
                        title = "এখনো কোনো ওয়ালেট লেনদেন নেই",
                        subtitle = "আপনার ওয়ালেটে টাকা Add করলে, সার্ভিস কিনলে বা রেফার বোনাস পেলে এখানে সকল লেনদেনের বিস্তারিত হিস্ট্রি দেখতে পাবেন।",
                        actionText = "➕ টাকা Add করুন",
                        onActionClick = { openDirectAddMoneyGateway() }
                    )
                }
            } else {
                items(effectiveWalletTransactions.take(30), key = { it.id }) { tx ->
                    WalletTransactionCard(tx = tx)
                }
            }
        }

        // ====================================================================
        // TAB 1: 🎁 REFERRAL SYSTEM, REFERRED MEMBERS LIST & BONUS TRACKER
        // ====================================================================
        if (activeProfileTab == 1) {
            item {
                val baseBdt = adminConfig.referralBaseOrderAmountBdt
                val commBdt = adminConfig.referralCommissionBdt
                val minOrdBdt = adminConfig.referralMinOrderAmountBdt
                val isPercentMode = adminConfig.referralCommissionMode.equals("PERCENTAGE", ignoreCase = true)

                val totalMemberOrdersCount = remember(referredMembers, referralRewardsHistory) {
                    val fromMembers = referredMembers.sumOf { it.totalOrdersCount }
                    maxOf(fromMembers, referralRewardsHistory.size)
                }
                val totalCompletedMemberOrders = remember(referredMembers, referralRewardsHistory) {
                    val fromMembers = referredMembers.sumOf { it.completedOrdersCount }
                    maxOf(fromMembers, referralRewardsHistory.size)
                }
                val activeOrderingMembersCount = remember(referredMembers) {
                    referredMembers.count { it.totalOrdersCount > 0 }
                }

                val (milestoneTitle, milestoneTarget, milestoneRewardNote) = remember(effectiveReferralsCount) {
                    when {
                        effectiveReferralsCount < 3 -> Triple(
                            "ব্রোঞ্জ রেফারার (Bronze Tier)",
                            3,
                            "৩ জন মেম্বার পূর্ণ হলে বিশেষ ব্রোঞ্জ ব্যাজ ও দ্রুত সাপোর্ট!"
                        )
                        effectiveReferralsCount < 10 -> Triple(
                            "সিলভার পার্টনার (Silver Partner)",
                            10,
                            "১০ জন মেম্বার পূর্ণ হলে ভিআইপি রেফারার স্ট্যাটাস!"
                        )
                        effectiveReferralsCount < 25 -> Triple(
                            "গোল্ড অ্যাম্বাসেডর (Gold Ambassador)",
                            25,
                            "২৫ জন মেম্বার পূর্ণ হলে প্রিমিয়াম অ্যাম্বাসেডর র‍্যাংক!"
                        )
                        else -> Triple(
                            "ডায়মন্ড লিডার (Diamond Leader)",
                            50,
                            "আপনি আমাদের শীর্ষ ভিআইপি রেফারেল লিডারদের একজন!"
                        )
                    }
                }
                val milestoneProgress = (effectiveReferralsCount.toFloat() / milestoneTarget.toFloat()).coerceIn(0.06f, 1f)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = GoldPrimary.copy(alpha = 0.35f),
                            spotColor = NeonMagenta.copy(alpha = 0.35f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF190B36)),
                    border = BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(listOf(GoldPrimary, NeonMagenta, NeonCyan))
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Top 3D Glass Highlight Sheen
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.White.copy(alpha = 0.16f), Color.Transparent)
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = GoldPrimary)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(GoldPrimary.copy(alpha = 0.28f), NeonMagenta.copy(alpha = 0.25f))
                                                )
                                            )
                                            .border(1.dp, GoldPrimary, RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CardGiftcard,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "🎁 ভিআইপি রেফারেল ও বোনাস প্রোগ্রাম",
                                            fontSize = 16.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (adminConfig.isReferralEnabled) "🟢 রেফারেল প্রোগ্রাম চালু আছে • অটো বোনাস" else "🔴 রেফারেল প্রোগ্রাম সাময়িকভাবে বন্ধ",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (adminConfig.isReferralEnabled) Color(0xFF4ADE80) else Color(0xFFF87171)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Live 2x2 Summary Grid (Total Members, Total Earned Bonus, Member Orders, Commission Rate)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF0E0522),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "👥 মোট রেফার মেম্বার",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFFBAE6FD),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$effectiveReferralsCount জন",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "অর্ডার করেছেন: $activeOrderingMembersCount জন",
                                            fontSize = 10.sp,
                                            color = NeonCyan,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF062319),
                                    border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.55f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "💰 মোট অর্জিত বোনাস",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFFA7F3D0),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "৳${String.format(Locale.US, "%,.0f", effectiveReferralEarnings)}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF4ADE80)
                                        )
                                        Text(
                                            text = "ওয়ালেটে অটো জমা হয়েছে",
                                            fontSize = 10.sp,
                                            color = Color(0xFF6EE7B7),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF0E0522),
                                    border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "📦 মেম্বারদের মোট অর্ডার",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFFE9D5FF),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$totalMemberOrdersCount টি অর্ডার",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "সম্পন্ন অর্ডার: $totalCompletedMemberOrders টি",
                                            fontSize = 10.sp,
                                            color = Color(0xFFC084FC),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF0E0522),
                                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "⚡ প্রতি অর্ডারে বোনাস",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFFFDE68A),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isPercentMode) {
                                                "${String.format(Locale.US, "%.0f", adminConfig.referralCommissionPercent)}% কমিশন"
                                            } else {
                                                "+৳${String.format(Locale.US, "%.0f", commBdt)} / ৳${String.format(Locale.US, "%.0f", baseBdt)}"
                                            },
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GoldPrimary
                                        )
                                        Text(
                                            text = "সর্বনিম্ন অর্ডার: ৳${String.format(Locale.US, "%.0f", minOrdBdt)}",
                                            fontSize = 10.sp,
                                            color = Color(0xFFFCD34D),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Referral Milestone & Rank Progress Bar
                            Surface(
                                shape = RoundedCornerShape(15.dp),
                                color = Color(0xFF14082E),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.38f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🏆 $milestoneTitle",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "$effectiveReferralsCount / $milestoneTarget জন",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GoldPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(7.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF080314))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(milestoneProgress)
                                                .height(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(GoldPrimary, Color(0xFF10B981), NeonCyan)
                                                    )
                                                )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = milestoneRewardNote,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Referral Code Card
                            Text(
                                text = "আপনার পার্সোনাল ভিআইপি Referral Code:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val effectiveCode = referralCode.ifBlank { "CTB-VIP2026" }
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0E0521),
                                border = BorderStroke(1.5.dp, GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = effectiveCode,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary,
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        ProfilePrimaryButton(
                                            text = "📋 কোড কপি করুন",
                                            onClick = {
                                                onCopyText("Referral Code", effectiveCode)
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("profile_copy_referral_btn")
                                        )
                                        ProfileSecondaryButton(
                                            text = "📤 বন্ধুদের শেয়ার করুন",
                                            borderColor = GoldPrimary,
                                            onClick = {
                                                val shareMessage = "🔥 Chamet Top Up BD অ্যাপে জয়েন করুন!\n" +
                                                    "TikTok Coin, Chamet Diamond, Free Fire ও Social Boost কিনুন সবচেয়ে কম দামে।\n" +
                                                    "🎁 আমার Referral Code ব্যবহার করুন: $effectiveCode"
                                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                                                }
                                                context.startActivity(
                                                    Intent.createChooser(sendIntent, "Share Referral Code")
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("profile_share_referral_btn")
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Link a Friend's Referral Code if not linked yet
                            if (referredByCode.isBlank()) {
                                Text(
                                    text = "আপনাকে কেউ রেফার করলে তার কোড দিন:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = friendRefCodeInput,
                                        onValueChange = { friendRefCodeInput = it.uppercase() },
                                        placeholder = {
                                            Text(
                                                text = "CTB-XXXXXXX",
                                                fontSize = 12.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldPrimary,
                                            unfocusedBorderColor = Color(0xFF4C1D95),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedContainerColor = Color(0xFF110724),
                                            unfocusedContainerColor = Color(0xFF110724)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    ProfilePrimaryButton(
                                        text = "এপ্লাই",
                                        enabled = friendRefCodeInput.trim().length >= 4,
                                        onClick = {
                                            onApplyReferralCode(friendRefCodeInput.trim()) { ok, msg ->
                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                if (ok) friendRefCodeInput = ""
                                            }
                                        }
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF0A2E20),
                                    border = BorderStroke(1.dp, Color(0xFF34D399)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF4ADE80),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "রেফার কোড ($referredByCode) যুক্ত আছে। আপনার প্রতিটি সফল অর্ডারে রেফারার বোনাস পাবেন!",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFFD1FAE5),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ================================================================
            // 8.5 MY REFERRED MEMBERS LIST ("আমার রেফার করা মেম্বার তালিকা")
            // Shows every member added via referral code, their name, order count, and bonus earned
            // ================================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(22.dp), spotColor = NeonCyan.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF150A2C)),
                    border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(NeonCyan.copy(alpha = 0.18f))
                                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "👥 আমার রেফার করা মেম্বার তালিকা",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "কে জয়েন করেছে, কয়টা অর্ডার করেছে ও কত বোনাস পেয়েছেন",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = NeonCyan.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.55f))
                            ) {
                                Text(
                                    text = "${referredMembers.size} জন",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (referredMembers.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0E0521),
                                border = BorderStroke(1.dp, Color(0xFF2D1B4E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "এখনো কোনো মেম্বার আপনার রেফার কোডে যুক্ত হননি",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "আপনার রেফারেল কোড দিয়ে কেউ একাউন্ট খুললে বা প্রোফাইলে কোড এপ্লাই করলে সাথে সাথে তার নাম, মোট অর্ডার সংখ্যা এবং আপনার অর্জিত বোনাস এখানে উপরে দেখা যাবে!",
                                        fontSize = 11.sp,
                                        color = Color(0xFFCBD5E1),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                referredMembers.forEachIndexed { idx, member ->
                                    val hasCompleted = member.completedOrdersCount > 0
                                    val hasPending = member.pendingOrdersCount > 0
                                    val badgeColor = when {
                                        hasCompleted -> Color(0xFF4ADE80)
                                        hasPending -> Color(0xFFFBBF24)
                                        else -> NeonCyan
                                    }
                                    val badgeLabel = when {
                                        hasCompleted -> "✅ সক্রিয় (${member.completedOrdersCount} অর্ডার)"
                                        hasPending -> "⏳ পেন্ডিং (${member.pendingOrdersCount})"
                                        else -> "🆕 নতুন মেম্বার"
                                    }
                                    val joinDateText = remember(member.lastActivityTimestamp) {
                                        if (member.lastActivityTimestamp > 0L) {
                                            SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(member.lastActivityTimestamp))
                                        } else {
                                            "রেফার মেম্বার"
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFF0E0521),
                                        border = BorderStroke(
                                            1.dp,
                                            if (hasCompleted) Color(0xFF34D399).copy(alpha = 0.5f)
                                            else Color(0xFF3B1F6E)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(13.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(38.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                Brush.linearGradient(
                                                                    listOf(badgeColor.copy(alpha = 0.28f), Color(0xFF7C3AED).copy(alpha = 0.25f))
                                                                )
                                                            )
                                                            .border(1.dp, badgeColor.copy(alpha = 0.65f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "#${idx + 1}",
                                                            fontSize = 12.5.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color.White
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(
                                                            text = member.memberName,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color.White,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = "${member.memberPhone.ifBlank { member.memberEmail }} • $joinDateText",
                                                            fontSize = 10.5.sp,
                                                            color = Color(0xFF94A3B8),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = badgeColor.copy(alpha = 0.15f),
                                                    border = BorderStroke(0.8.dp, badgeColor.copy(alpha = 0.5f))
                                                ) {
                                                    Text(
                                                        text = badgeLabel,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = badgeColor,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // 3 Mini Stat Boxes: Total Orders, Order Spent, Bonus Earned
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color(0xFF180B33),
                                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            text = "মোট অর্ডার",
                                                            fontSize = 9.5.sp,
                                                            color = Color(0xFF94A3B8)
                                                        )
                                                        Text(
                                                            text = "${member.totalOrdersCount} টি",
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color.White
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color(0xFF180B33),
                                                    border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.35f)),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            text = "অর্ডার মূল্য",
                                                            fontSize = 9.5.sp,
                                                            color = Color(0xFF94A3B8)
                                                        )
                                                        Text(
                                                            text = "৳${String.format(Locale.US, "%,.0f", member.totalOrderSpentBdt)}",
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color(0xFFE9D5FF)
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color(0xFF06291D),
                                                    border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.55f)),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            text = "প্রাপ্ত বোনাস",
                                                            fontSize = 9.5.sp,
                                                            color = Color(0xFFA7F3D0)
                                                        )
                                                        Text(
                                                            text = "+৳${String.format(Locale.US, "%,.0f", member.totalBonusEarnedBdt)}",
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color(0xFF4ADE80)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ================================================================
            // 9. REFERRAL HISTORY ("অর্ডার অনুযায়ী বোনাস হিস্ট্রি")
            // ================================================================
            item {
                Text(
                    text = "🎁 অর্ডার অনুযায়ী বোনাস হিস্ট্রি (${referralRewardsHistory.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            if (referralRewardsHistory.isEmpty()) {
                item {
                    ProfileEmptyStateCard(
                        icon = Icons.Default.EmojiEvents,
                        title = "এখনো কোনো অর্ডার বোনাস হিস্ট্রি নেই",
                        subtitle = "আপনার রেফার করা মেম্বাররা সার্ভিস কিনলে অর্ডার Completed হওয়ার সাথে সাথে প্রতি অর্ডারের বোনাস হিসাব এখানে যুক্ত হবে।"
                    )
                }
            } else {
                items(referralRewardsHistory, key = { it.id }) { reward ->
                    val dateStr = remember(reward.timestamp) {
                        SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.US).format(Date(reward.timestamp))
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF140B29)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = "👤 ${reward.referredUserName.ifBlank { reward.referredUserEmail.substringBefore("@").ifBlank { "User" } }}",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "📦 ${reward.serviceName.ifBlank { "Service Order" }} (৳${String.format(Locale.US, "%,.0f", reward.orderAmountBdt)})",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFCBD5E1),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "📅 $dateStr • Order #${reward.orderNumber}",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+৳${String.format(Locale.US, "%,.1f", reward.commissionBdt)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF4ADE80)
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF062E22),
                                    border = BorderStroke(0.8.dp, Color(0xFF34D399).copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "✅ ${reward.status.ifBlank { "Completed" }}",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4ADE80),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // TAB 2: ⚙️ ACCOUNT / OPTIONS TAB (Grouped Premium Sections + Sub-Pages)
        // ====================================================================
        if (activeProfileTab == 2) {
            when (activeAccountSubSection) {
                ProfileAccountSubSection.SAVED_IDS -> {
                    item {
                        ProfileSubPageHeaderBar(
                            title = "💾 Saved Service IDs",
                            onBack = { activeAccountSubSection = ProfileAccountSubSection.NONE }
                        )
                    }
                    item {
                        ProfileSavedIdsSubPage(
                            savedAccounts = effectiveSavedAccounts,
                            onSaveAccount = { serviceName, accountId, label, isDefault, editId ->
                                onSaveServiceAccount(serviceName, accountId, label, isDefault, editId)
                                Toast.makeText(context, "✅ সার্ভিস আইডি সেভ হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            onDeleteAccount = { id ->
                                onDeleteServiceAccount(id)
                                Toast.makeText(context, "🗑️ আইডি মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onSetDefaultAccount = { id ->
                                onSetDefaultServiceAccount(id)
                                Toast.makeText(context, "⭐ Default ID আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                ProfileAccountSubSection.MY_ORDERS -> {
                    item {
                        ProfileSubPageHeaderBar(
                            title = "📦 My Orders & Receipts",
                            onBack = { activeAccountSubSection = ProfileAccountSubSection.NONE }
                        )
                    }
                    item {
                        ProfileMyOrdersSubPage(
                            userOrders = userOrders,
                            onOpenOrderReceipt = onOpenOrderReceipt,
                            onBuyServiceClick = onBackHome
                        )
                    }
                }

                ProfileAccountSubSection.NOTIFICATIONS -> {
                    item {
                        ProfileSubPageHeaderBar(
                            title = "🔔 Notifications & Alerts",
                            onBack = { activeAccountSubSection = ProfileAccountSubSection.NONE }
                        )
                    }
                    item {
                        ProfileNotificationsSubPage(
                            notifications = userNotifications,
                            onMarkSingleRead = onMarkSingleNotificationRead,
                            onMarkAllRead = onMarkAllNotificationsRead
                        )
                    }
                }

                ProfileAccountSubSection.VIP_SUPPORT -> {
                    item {
                        ProfileSubPageHeaderBar(
                            title = "🎧 24/7 VIP Support",
                            onBack = { activeAccountSubSection = ProfileAccountSubSection.NONE }
                        )
                    }
                    item {
                        ProfileVipSupportSubPage(
                            adminConfig = adminConfig,
                            onOpenSupportChat = onOpenSupportChat,
                            onOpenWhatsApp = onOpenWhatsAppSupport,
                            onOpenTelegram = onOpenTelegramSupport
                        )
                    }
                }

                ProfileAccountSubSection.SECURITY_INFO -> {
                    item {
                        ProfileSubPageHeaderBar(
                            title = "🛡️ Account Security",
                            onBack = { activeAccountSubSection = ProfileAccountSubSection.NONE }
                        )
                    }
                    item {
                        ProfileSecuritySubPage(
                            email = loggedInUserEmail,
                            phone = loggedInUserPhone,
                            isAdmin = isAdminLoggedIn,
                            onSendPasswordReset = {
                                if (loggedInUserEmail.isNotBlank()) {
                                    onSendPasswordReset(loggedInUserEmail)
                                }
                            },
                            onLogout = {
                                onLogout()
                                onBackHome()
                            }
                        )
                    }
                }

                ProfileAccountSubSection.NONE -> {
                    // VIP Membership Progress Card (Requirement 10)
                    item {
                        VipMembershipProgressCard(
                            vipTierLabel = vipTierLabel,
                            totalSpentBdt = totalSpentBdt,
                            completedOrdersCount = completedOrdersCount
                        )
                    }

                    // SECTION 1: 🛠️ অ্যাকাউন্ট ও সেবা (Grouped Premium Option Cards)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
                            border = BorderStroke(1.dp, Color(0xFF2E1859))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "🛠️ অ্যাকাউন্ট ও সেবা",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )

                                // 2-Column Highlight Feature Cards Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ProfileFeatureGridCard(
                                        icon = Icons.Default.AccountBalanceWallet,
                                        iconTint = Color(0xFF4ADE80),
                                        title = "Wallet & Balance",
                                        subtitle = "৳${String.format(Locale.US, "%,.0f", walletBalance)} BDT",
                                        onClick = {
                                            activeProfileTab = 0
                                            openDirectAddMoneyGateway()
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    ProfileFeatureGridCard(
                                        icon = Icons.Default.CardGiftcard,
                                        iconTint = GoldPrimary,
                                        title = "Referral Program",
                                        subtitle = "আয়: ৳${String.format(Locale.US, "%,.0f", referralEarningsBdt)}",
                                        onClick = { activeProfileTab = 1 },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ProfileFeatureGridCard(
                                        icon = Icons.Default.BookmarkAdded,
                                        iconTint = NeonCyan,
                                        title = "Saved IDs",
                                        subtitle = "${effectiveSavedAccounts.size} টি সেভ করা আইডি",
                                        onClick = {
                                            activeAccountSubSection = ProfileAccountSubSection.SAVED_IDS
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    ProfileFeatureGridCard(
                                        icon = Icons.Default.ReceiptLong,
                                        iconTint = Color(0xFFC084FC),
                                        title = "My Orders",
                                        subtitle = "${userOrders.size} টি অর্ডার হিস্ট্রি",
                                        onClick = {
                                            activeAccountSubSection = ProfileAccountSubSection.MY_ORDERS
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Compact Premium Option Cards with Icon Container, Title, Description, Right Arrow
                                ProfileMenuOptionTile(
                                    icon = Icons.Default.PhotoCamera,
                                    iconTint = Color(0xFF38BDF8),
                                    title = "প্রোফাইল ছবি পরিবর্তন (Profile Photo)",
                                    subtitle = "গ্যালারি থেকে আপনার প্রোফাইল ছবি আপলোড করুন",
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                )

                                ProfileMenuOptionTile(
                                    icon = Icons.Default.NotificationsActive,
                                    iconTint = Color(0xFFF472B6),
                                    title = "নোটিফিকেশন ও অর্ডার অ্যালার্ট (Notifications)",
                                    subtitle = if (unreadNotificationCount > 0) "$unreadNotificationCount টি অপঠিত আপডেট রয়েছে" else "সকল অর্ডার ও পেমেন্ট মেসেজ দেখুন",
                                    onClick = {
                                        activeAccountSubSection = ProfileAccountSubSection.NOTIFICATIONS
                                    }
                                )

                                ProfileMenuOptionTile(
                                    icon = Icons.Default.SupportAgent,
                                    iconTint = Color(0xFF34D399),
                                    title = "২৪/৭ ভিআইপি সাপোর্ট (VIP Support)",
                                    subtitle = "অ্যাপের ভেতর লাইভ চ্যাট, WhatsApp ও Telegram হেল্প",
                                    onClick = {
                                        activeAccountSubSection = ProfileAccountSubSection.VIP_SUPPORT
                                    }
                                )
                            }
                        }
                    }

                    // SECTION 2: 🔐 সিকিউরিটি ও একাউন্ট কন্ট্রোল (Security: Change Password, Account Security, Logout)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
                            border = BorderStroke(1.dp, Color(0xFF2E1859))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "🔐 নিরাপত্তা ও অ্যাকাউন্ট (Security)",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )

                                ProfileMenuOptionTile(
                                    icon = Icons.Default.LockReset,
                                    iconTint = Color(0xFFFBBF24),
                                    title = "পাসওয়ার্ড পরিবর্তন করুন (Change Password)",
                                    subtitle = "$loggedInUserEmail এ রিসেট লিংক পাঠান",
                                    onClick = {
                                        if (loggedInUserEmail.isNotBlank()) {
                                            onSendPasswordReset(loggedInUserEmail)
                                        }
                                    }
                                )

                                ProfileMenuOptionTile(
                                    icon = Icons.Default.Security,
                                    iconTint = NeonCyan,
                                    title = "অ্যাকাউন্ট সিকিউরিটি স্ট্যাটাস (Account Security)",
                                    subtitle = "Firebase Authentication ও ক্লাউড ব্যাকআপ সুরক্ষিত",
                                    onClick = {
                                        activeAccountSubSection = ProfileAccountSubSection.SECURITY_INFO
                                    }
                                )

                                if (isAdminLoggedIn) {
                                    ProfileMenuOptionTile(
                                        icon = Icons.Default.AdminPanelSettings,
                                        iconTint = Color(0xFF4ADE80),
                                        title = "এডমিন প্যানেল ড্যাশবোর্ড (Admin Panel)",
                                        subtitle = "অর্ডার, কাস্টমার ওয়ালেট ও রেফারেল কমিশন কন্ট্রোল করুন",
                                        onClick = onOpenAdminPanel
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                ProfileDangerButton(
                                    text = "লগআউট করুন (Logout)",
                                    icon = Icons.AutoMirrored.Filled.Logout,
                                    onClick = {
                                        onLogout()
                                        onBackHome()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_logout_btn")
                                )
                            }
                        }
                    }

                    // Recent Orders preview at the bottom of Account tab
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📦 সাম্প্রতিক অর্ডারসমূহ (${userOrders.size})",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (processingOrdersCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color(0xFF3B2106),
                                        border = BorderStroke(1.dp, Color(0xFFF59E0B))
                                    ) {
                                        Text(
                                            text = "⏳ $processingOrdersCount প্রসেসিং",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFBBF24),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "সব দেখুন →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary,
                                    modifier = Modifier.clickable {
                                        activeAccountSubSection = ProfileAccountSubSection.MY_ORDERS
                                    }
                                )
                            }
                        }
                    }

                    if (userOrders.isEmpty()) {
                        item {
                            ProfileEmptyStateCard(
                                icon = Icons.Default.ReceiptLong,
                                title = "এখনো কোনো অর্ডার নেই",
                                subtitle = "আপনার সকল সার্ভিস অর্ডার ও রিসিট এখানে দেখতে পাবেন।",
                                actionText = "🛍️ সার্ভিস কিনুন",
                                onActionClick = onBackHome
                            )
                        }
                    } else {
                        items(userOrders.take(5), key = { it.orderNumber }) { ord ->
                            RecentOrderCompactCard(
                                ord = ord,
                                onClick = { onOpenOrderReceipt(ord) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeroQuickActionButton(
    icon: ImageVector,
    label: String,
    subLabel: String,
    gradientColors: List<Color>,
    accentGlow: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "hero_quick_action_scale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(15.dp),
        color = Color.Transparent,
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.45f), accentGlow.copy(alpha = 0.65f))
            )
        ),
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(15.dp),
                ambientColor = accentGlow.copy(alpha = 0.38f),
                spotColor = accentGlow.copy(alpha = 0.5f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradientColors))
        ) {
            // Top 3D Glass Highlight Sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subLabel,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileQuickStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    actionHint: String,
    accentColor: Color,
    secondaryAccent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bento_stat_scale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = accentColor.copy(alpha = 0.35f),
                spotColor = accentColor.copy(alpha = 0.45f)
            ),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF150A2E),
        border = BorderStroke(
            1.3.dp,
            Brush.linearGradient(
                listOf(
                    accentColor.copy(alpha = 0.75f),
                    secondaryAccent.copy(alpha = 0.35f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            accentColor.copy(alpha = 0.16f),
                            Color(0xFF160B30),
                            Color(0xFF0E0520)
                        )
                    )
                )
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        radius = size.width * 0.45f,
                        center = Offset(size.width * 0.18f, size.height * 0.35f)
                    )
                }
        ) {
            // Top 3D Specular Glass Sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(11.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.14f), Color.Transparent)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D Glowing Vector Icon Squircle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(8.dp, RoundedCornerShape(13.dp), spotColor = accentColor)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    accentColor.copy(alpha = 0.35f),
                                    secondaryAccent.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(1.dp, accentColor.copy(alpha = 0.65f), RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = value,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = label,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = actionHint,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletTransactionCard(tx: WalletTransactionEntry) {
    val dateStr = remember(tx.timestamp) {
        SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.US).format(Date(tx.timestamp))
    }
    val iconEmoji = when (tx.type.uppercase()) {
        "ADD_MONEY" -> "➕"
        "SERVICE_PURCHASE" -> "🛍️"
        "REFERRAL_BONUS" -> "🎁"
        else -> if (tx.isCredit) "➕" else "➖"
    }
    val amountSign = if (tx.isCredit) "+" else "-"
    val amountColor = if (tx.isCredit) Color(0xFF4ADE80) else Color(0xFFFBBF24)
    val statusColor = when {
        tx.status.equals("Completed", ignoreCase = true) || tx.status.equals("Approved", ignoreCase = true) -> Color(0xFF4ADE80)
        tx.status.equals("Cancelled", ignoreCase = true) -> Color(0xFFF87171)
        else -> GoldPrimary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF140B29)),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(amountColor.copy(alpha = 0.14f))
                        .border(1.dp, amountColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 17.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tx.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (tx.reasonOrNote.isNotBlank()) {
                        Text(
                            text = tx.reasonOrNote,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = dateStr,
                        fontSize = 10.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${amountSign}৳${String.format(Locale.US, "%,.0f", tx.amountBdt)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = tx.status.ifBlank { "Completed" },
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VipMembershipProgressCard(
    vipTierLabel: String,
    totalSpentBdt: Double,
    completedOrdersCount: Int
) {
    val nextTierTarget = when {
        totalSpentBdt < 500 -> 500.0
        totalSpentBdt < 3000 -> 3000.0
        totalSpentBdt < 10000 -> 10000.0
        else -> 10000.0
    }
    val nextTierName = when {
        totalSpentBdt < 500 -> "⭐ Silver Verified"
        totalSpentBdt < 3000 -> "👑 Gold VIP"
        totalSpentBdt < 10000 -> "💎 Diamond VIP"
        else -> "💎 Max Diamond VIP"
    }
    val progress = (totalSpentBdt / nextTierTarget).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF180C34)),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(listOf(GoldPrimary, NeonMagenta))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VIP Membership Status",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = GoldPrimary.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, GoldPrimary)
                ) {
                    Text(
                        text = vipTierLabel,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50)),
                color = GoldPrimary,
                trackColor = Color(0xFF2D1657)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "মোট সফল কেনাকাটা: ৳${String.format(Locale.US, "%,.0f", totalSpentBdt)} ($completedOrdersCount অর্ডার)",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Text(
                    text = if (totalSpentBdt >= 10000) "সর্বোচ্চ লেভেল আনলকড!" else "পরবর্তী: $nextTierName",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            }
        }
    }
}

@Composable
private fun ProfileFeatureGridCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "grid_card_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = iconTint),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1B0E36),
        border = BorderStroke(1.dp, iconTint.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = iconTint,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProfileSubPageHeaderBar(
    title: String,
    onBack: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF170B30),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onBack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = GoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Text(
                text = "অপশনে ফিরুন",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}

@Composable
private fun ProfileSecuritySubPage(
    email: String,
    phone: String,
    isAdmin: Boolean,
    onSendPasswordReset: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF150A2C)),
        border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonCyan.copy(alpha = 0.16f))
                        .border(1.dp, NeonCyan, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "🛡️ Account Security",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Firebase Authentication দ্বারা সুরক্ষিত",
                        fontSize = 11.5.sp,
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            Text(
                text = "• ভেরিফাইড ইমেইল: $email\n" +
                    "• ফোন নাম্বার: ${phone.ifBlank { "যুক্ত করা হয়নি" }}\n" +
                    "• একাউন্ট রোল: ${if (isAdmin) "Official Admin" else "Verified Customer"}\n" +
                    "• ডাটা সিকিউরিটি: ওয়ালেট ব্যালেন্স, রেফারেল ও অর্ডার ডাটা রিয়েল-টাইম ক্লাউড ডাটাবেসে সংরক্ষিত।",
                fontSize = 12.sp,
                color = Color(0xFFE2E8F0),
                lineHeight = 18.sp
            )

            ProfilePrimaryButton(
                text = "🔑 পাসওয়ার্ড রিসেট ইমেইল পাঠান",
                icon = Icons.Default.LockReset,
                onClick = onSendPasswordReset,
                modifier = Modifier.fillMaxWidth()
            )

            ProfileDangerButton(
                text = "🚪 এই ডিভাইস থেকে লগআউট করুন",
                icon = Icons.AutoMirrored.Filled.Logout,
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RecentOrderCompactCard(
    ord: OrderRecord,
    onClick: () -> Unit
) {
    val isApproved = ord.status == "Completed" || ord.status == "Approved"
    val isCancelled = ord.status == "Cancelled" || ord.status == "Incorrect TrxID"
    val statusColor = when {
        isApproved -> Color(0xFF4ADE80)
        isCancelled -> Color(0xFFF87171)
        else -> Color(0xFFFBBF24)
    }
    val dateStr = remember(ord.timestamp) {
        SimpleDateFormat("dd MMM • hh:mm a", Locale.US).format(Date(ord.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF140B29)),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${ord.category} • ${ord.itemName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "#${ord.orderNumber} • ${ord.paymentMethod} • $dateStr",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "৳${String.format(Locale.US, "%,.0f", ord.priceBdt)}",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = ord.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickWalletServiceChip(
    logo: @Composable () -> Unit,
    title: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "quick_chip_scale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(15.dp),
                ambientColor = accentColor.copy(alpha = 0.35f),
                spotColor = accentColor.copy(alpha = 0.45f)
            ),
        shape = RoundedCornerShape(15.dp),
        color = Color(0xFF1A0D36),
        border = BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                listOf(accentColor.copy(alpha = 0.7f), accentColor.copy(alpha = 0.25f))
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(accentColor.copy(alpha = 0.18f), Color(0xFF14092A))
                    )
                )
        ) {
            // Top 3D Glass Highlight Sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.16f), Color.Transparent)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                logo()
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuOptionTile(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "menu_tile_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = iconTint.copy(alpha = 0.28f),
                spotColor = iconTint.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = iconTint),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF180C32),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(iconTint.copy(alpha = 0.55f), Color(0xFF2E1859))
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(iconTint.copy(alpha = 0.12f), Color(0xFF160A2E))
                    )
                )
        ) {
            // Top 3D Sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(8.dp, RoundedCornerShape(13.dp), spotColor = iconTint)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(iconTint.copy(alpha = 0.3f), iconTint.copy(alpha = 0.14f))
                            )
                        )
                        .border(1.dp, iconTint.copy(alpha = 0.55f), RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.16f))
                        .border(1.dp, iconTint.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileDarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.5.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = Color(0xFF3B1F6E),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = GoldPrimary,
            unfocusedLabelColor = Color(0xFF94A3B8),
            focusedContainerColor = Color(0xFF0E0620),
            unfocusedContainerColor = Color(0xFF0E0620)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
