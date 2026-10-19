package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.AppLaunchRulesDialog
import com.example.ui.components.FullScreenNotificationScreen
import com.example.ui.components.NeonGlassBottomNavBar
import com.example.ui.components.NoticeDialog
import com.example.ui.components.OrderBottomSheet
import com.example.ui.components.PaymentAccountsSheet
import com.example.ui.components.PremiumLiveSupportFabPill
import com.example.ui.components.PremiumTelegramFabPill
import com.example.ui.components.PremiumWhatsAppFabPill
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.SupportChatDialog
import com.example.ui.components.TopupHeader
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.ChametScreen
import com.example.ui.screens.EpayScreen
import com.example.ui.screens.ExchangeScreen
import com.example.ui.screens.FreeFireScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SocialBoostScreen
import com.example.ui.screens.SupportScreen
import com.example.ui.screens.TikTokScreen
import com.example.ui.theme.BrandDarkBg
import com.example.ui.theme.BrandDarkCard
import com.example.ui.theme.DarkViolet
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.JohirulTopupTheme
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.TopupViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: TopupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JohirulTopupTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: TopupViewModel) {
    val context = LocalContext.current
    var showSplashScreen by remember { mutableStateOf(true) }

    var isFabMenuExpanded by remember { mutableStateOf(false) }

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val adminConfig by viewModel.adminConfig.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val showAdminLoginDialog by viewModel.showAdminLoginDialog.collectAsStateWithLifecycle()
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val authErrorMessage by viewModel.authErrorMessage.collectAsStateWithLifecycle()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsStateWithLifecycle()
    val firebaseUserEmail by viewModel.firebaseUserEmail.collectAsStateWithLifecycle()
    val loggedInUserEmail by viewModel.loggedInUserEmail.collectAsStateWithLifecycle()
    val loggedInUserName by viewModel.loggedInUserName.collectAsStateWithLifecycle()
    val loggedInUserPhone by viewModel.loggedInUserPhone.collectAsStateWithLifecycle()
    val loggedInUserPhotoUri by viewModel.loggedInUserPhotoUri.collectAsStateWithLifecycle()
    val loggedInUserReferralCode by viewModel.loggedInUserReferralCode.collectAsStateWithLifecycle()
    val loggedInUserReferredByCode by viewModel.loggedInUserReferredByCode.collectAsStateWithLifecycle()
    val supportChatUserName by viewModel.supportChatUserName.collectAsStateWithLifecycle()
    val supportChatUserPhone by viewModel.supportChatUserPhone.collectAsStateWithLifecycle()
    val supportChatUserEmail by viewModel.supportChatUserEmail.collectAsStateWithLifecycle()
    val latestBannerNotification by viewModel.latestBannerNotification.collectAsStateWithLifecycle()
    val userClearedNotifTimestamp by viewModel.userClearedNotifTimestamp.collectAsStateWithLifecycle()
    val noticeDialogInitialTab by viewModel.noticeDialogTab.collectAsStateWithLifecycle()
    val authDialogStartInRegister by viewModel.authDialogStartInRegister.collectAsStateWithLifecycle()

    val showAppLaunchNotice by viewModel.showAppLaunchNotice.collectAsStateWithLifecycle()
    val showNotificationScreen by viewModel.showNotificationScreen.collectAsStateWithLifecycle()
    val showNotice by viewModel.showNoticeDialog.collectAsStateWithLifecycle()
    val showPaymentAccounts by viewModel.showPaymentAccountsSheet.collectAsStateWithLifecycle()
    val showEpayOrderDialog by viewModel.showEpayOrderDialog.collectAsStateWithLifecycle()
    val showExchangeOrderDialog by viewModel.showExchangeOrderDialog.collectAsStateWithLifecycle()
    val selectedPackageForChooser by viewModel.selectedPackageForChooser.collectAsStateWithLifecycle()
    val selectedPackageForGateway by viewModel.selectedPackageForGateway.collectAsStateWithLifecycle()
    val savedUidForCurrentCategory by viewModel.savedUidForCurrentCategory.collectAsStateWithLifecycle()
    val orderFormState by viewModel.orderForm.collectAsStateWithLifecycle()
    val selectedReceipt by viewModel.selectedOrderForReceipt.collectAsStateWithLifecycle()

    val epayUsd by viewModel.epayUsdInput.collectAsStateWithLifecycle()
    val exchangeSendMethod by viewModel.exchangeSendMethod.collectAsStateWithLifecycle()
    val exchangeReceiveMethod by viewModel.exchangeReceiveMethod.collectAsStateWithLifecycle()
    val exchangeAmountBdt by viewModel.exchangeAmountBdt.collectAsStateWithLifecycle()
    val exchangeRecipientUpi by viewModel.exchangeRecipientUpi.collectAsStateWithLifecycle()
    val customTikTokCoins by viewModel.customTikTokCoins.collectAsStateWithLifecycle()
    val customChametDiamonds by viewModel.customChametDiamonds.collectAsStateWithLifecycle()
    val userWalletBalance by viewModel.userWalletBalance.collectAsStateWithLifecycle()
    val supportMessages by viewModel.supportMessages.collectAsStateWithLifecycle()
    val showSupportChatDialog by viewModel.showSupportChatDialog.collectAsStateWithLifecycle()
    val isSendingSupportMessage by viewModel.isSendingSupportMessage.collectAsStateWithLifecycle()
    val isAiTypingSupportReply by viewModel.isAiTypingSupportReply.collectAsStateWithLifecycle()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    if (showSplashScreen) {
        SplashScreen(
            onSkip = { showSplashScreen = false }
        )
    } else {
        LaunchedEffect(Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!hasPermission) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        LaunchedEffect(latestBannerNotification?.id) {
            if (latestBannerNotification != null) {
                delay(8500)
                viewModel.dismissBannerNotification()
            }
        }

        val cleanLoggedInEmail = remember(loggedInUserEmail) {
            loggedInUserEmail.trim().lowercase()
        }
        val currentUserOrders = remember(orders, cleanLoggedInEmail, userClearedNotifTimestamp) {
            if (cleanLoggedInEmail.isEmpty()) {
                emptyList()
            } else {
                orders.filter {
                    it.customerEmail.trim().lowercase() == cleanLoggedInEmail &&
                        it.timestamp > userClearedNotifTimestamp
                }
            }
        }
        val currentUserNotifications = remember(notifications, currentUserOrders, cleanLoggedInEmail, userClearedNotifTimestamp) {
            if (cleanLoggedInEmail.isEmpty()) {
                emptyList()
            } else {
                val userOrderNums = currentUserOrders.map { it.orderNumber }.toSet()
                notifications.filter { notif ->
                    val nEmail = notif.customerEmail.trim().lowercase()
                    val belongs = (nEmail.isNotEmpty() && nEmail == cleanLoggedInEmail) ||
                        (nEmail.isEmpty() && userOrderNums.contains(notif.orderNumber))
                    belongs && notif.timestamp > userClearedNotifTimestamp
                }
            }
        }
        val userUnreadNotifCount = remember(currentUserNotifications, currentUserOrders) {
            val unread = currentUserNotifications.count { !it.isRead }
            if (unread > 0) unread else currentUserOrders.count { it.status == "Processing" }
        }
        val userApprovedOrderCount = remember(currentUserOrders) {
            currentUserOrders.count { it.status == "Completed" || it.status == "Approved" }
        }
        val userProcessingOrderCount = remember(currentUserOrders) {
            currentUserOrders.count { it.status == "Processing" }
        }
        val visibleBannerNotification = remember(latestBannerNotification, cleanLoggedInEmail) {
            latestBannerNotification?.takeIf { notif ->
                cleanLoggedInEmail.isNotEmpty() &&
                    notif.customerEmail.trim().lowercase() == cleanLoggedInEmail
            }
        }

        BackHandler(enabled = currentTab != AppNavTab.HOME) {
            viewModel.selectTab(AppNavTab.HOME)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = BrandDarkBg,
            topBar = {
                if (currentTab != AppNavTab.ADMIN && currentTab != AppNavTab.PROFILE) {
                    TopupHeader(
                        onNoticeClick = { viewModel.setNotificationScreenVisible(true) },
                        onPaymentAccountsClick = { viewModel.setPaymentAccountsSheetVisible(true) },
                        onWhatsAppClick = { viewModel.launchWhatsApp(context) },
                        onTelegramClick = { viewModel.copyToClipboardAndOpenTelegram(context) },
                        onAdminClick = {
                            if (isAdminLoggedIn) {
                                viewModel.selectTab(AppNavTab.ADMIN)
                            } else {
                                viewModel.openAdminLoginDialog()
                            }
                        },
                        onAccountClick = {
                            if (loggedInUserEmail.isNotBlank() || isAdminLoggedIn) {
                                viewModel.selectTab(AppNavTab.PROFILE)
                            } else {
                                viewModel.openAdminLoginDialog()
                            }
                        },
                        loggedInUserEmail = loggedInUserEmail,
                        loggedInUserName = loggedInUserName,
                        loggedInUserPhotoUri = loggedInUserPhotoUri,
                        userWalletBalance = userWalletBalance,
                        isAdminLoggedIn = isAdminLoggedIn,
                        storeOverride = adminConfig.storeStatusOverride,
                        promoBanners = adminConfig.promoBanners,
                        onNavigateTab = { viewModel.selectTab(it) },
                        unreadNotificationCount = userUnreadNotifCount
                    )
                }
            },
            bottomBar = {
                BottomNavBar(
                    currentTab = currentTab,
                    orderCount = if (isAdminLoggedIn) orders.count { it.status == "Processing" } else userProcessingOrderCount,
                    isAdminLoggedIn = isAdminLoggedIn,
                    onSelectTab = { viewModel.selectTab(it) }
                )
            },
            floatingActionButton = {
                if (currentTab != AppNavTab.ADMIN) {
                    val exchangeSupportMsg = if (currentTab == AppNavTab.EXCHANGE) {
                        "🔄 Chamet Top Up BD - Exchange Support:\nআমি কারেন্সি এক্সচেঞ্জ সম্পর্কে সহায়তা চাই।\nকরিডোর: $exchangeSendMethod ➔ $exchangeReceiveMethod\nপরিমাণ: ৳${exchangeAmountBdt.ifBlank { "1000" }} BDT\nদয়া করে সহায়তা করবেন।"
                    } else {
                        "👋 Hello Chamet Top Up BD Support, I need assistance with my order / exchange request."
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Expanded Speed Dial Menu Options (WhatsApp & Telegram)
                        AnimatedVisibility(
                            visible = isFabMenuExpanded,
                            enter = fadeIn() + androidx.compose.animation.expandVertically(expandFrom = Alignment.Bottom),
                            exit = fadeOut() + androidx.compose.animation.shrinkVertically(shrinkTowards = Alignment.Bottom)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // 0. In-App Live Support Chat Pill Button (Firebase Firestore Backend)
                                PremiumLiveSupportFabPill(
                                    title = "লাইভ সাপোর্ট চ্যাট",
                                    subtitle = "সরাসরি মেসেজ পাঠান",
                                    onClick = {
                                        isFabMenuExpanded = false
                                        viewModel.openSupportChatDialog()
                                    }
                                )

                                // 1. Official WhatsApp Contact Pill Button
                                PremiumWhatsAppFabPill(
                                    title = "WhatsApp চ্যাট",
                                    subtitle = if (currentTab == AppNavTab.EXCHANGE) "Exchange Support" else "24/7 লাইভ হেল্প",
                                    onClick = {
                                        isFabMenuExpanded = false
                                        viewModel.launchWhatsApp(context, exchangeSupportMsg)
                                    }
                                )

                                // 2. Official Telegram Contact Pill Button
                                PremiumTelegramFabPill(
                                    title = "Telegram সাপোর্ট",
                                    subtitle = if (currentTab == AppNavTab.EXCHANGE) "Instant Channel" else "চ্যানেল ও হেল্প",
                                    onClick = {
                                        isFabMenuExpanded = false
                                        viewModel.copyToClipboardAndOpenTelegram(context, exchangeSupportMsg)
                                    }
                                )
                            }
                        }

                        // Main Floating Customer Support Button (smaller, cleaner, golden with green online badge)
                        Box(
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Surface(
                                onClick = { isFabMenuExpanded = !isFabMenuExpanded },
                                shape = CircleShape,
                                color = Color.Transparent,
                                modifier = Modifier
                                    .size(46.dp)
                                    .shadow(
                                        elevation = 12.dp,
                                        shape = CircleShape,
                                        spotColor = if (isFabMenuExpanded) Color(0xFFE11D48) else GoldPrimary
                                    ),
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isFabMenuExpanded) {
                                        Brush.linearGradient(listOf(Color(0xFFFF8AF8), Color(0xFFE11D48)))
                                    } else {
                                        Brush.linearGradient(listOf(Color(0xFFFFF8B8), Color(0xFFF59E0B)))
                                    }
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (isFabMenuExpanded) {
                                                Brush.radialGradient(
                                                    colors = listOf(Color(0xFFF43F5E), Color(0xFFBE123C))
                                                )
                                            } else {
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFFFFEA55),
                                                        Color(0xFFFACC15),
                                                        Color(0xFFEAB308)
                                                    )
                                                )
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isFabMenuExpanded) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close Menu",
                                            tint = Color.White,
                                            modifier = Modifier.size(21.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = "Customer Support Menu",
                                            tint = Color(0xFF110A1C),
                                            modifier = Modifier.size(23.dp)
                                        )
                                    }
                                }
                            }

                            if (!isFabMenuExpanded) {
                                Box(
                                    modifier = Modifier
                                        .size(11.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                        .border(1.5.dp, Color(0xFF0A0618), CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BrandDarkBg)
            ) {
                if (isFabMenuExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clickable { isFabMenuExpanded = false }
                    )
                }
                when (currentTab) {
                    AppNavTab.HOME -> HomeScreen(
                        onNavigateTab = { viewModel.selectTab(it) },
                        onPackageClick = { viewModel.openOrderSheet(it) },
                        onPaymentAccountsClick = { viewModel.setPaymentAccountsSheetVisible(true) },
                        onWhatsAppClick = { viewModel.launchWhatsApp(context) },
                        onTelegramClick = { viewModel.copyToClipboardAndOpenTelegram(context) },
                        onAccountClick = {
                            if (loggedInUserEmail.isNotBlank() || isAdminLoggedIn) {
                                viewModel.selectTab(AppNavTab.PROFILE)
                            } else {
                                viewModel.openAdminLoginDialog()
                            }
                        },
                        onNoticeClick = { viewModel.setNotificationScreenVisible(true) },
                        onSupportChatClick = { viewModel.openSupportChatDialog() },
                        onAdminPanelClick = { viewModel.selectTab(AppNavTab.ADMIN) },
                        loggedInUserEmail = loggedInUserEmail,
                        loggedInUserName = loggedInUserName,
                        isAdminLoggedIn = isAdminLoggedIn,
                        unreadNotificationCount = userUnreadNotifCount,
                        approvedOrderCount = userApprovedOrderCount,
                        tiktokPackages = viewModel.getTikTokPackages(),
                        chametPackages = viewModel.getChametPackages(),
                        freeFirePackages = viewModel.getFreeFirePackages(),
                        promoBanners = adminConfig.promoBanners
                    )

                    AppNavTab.PROFILE -> ProfileScreen(
                        loggedInUserEmail = loggedInUserEmail,
                        loggedInUserName = loggedInUserName,
                        loggedInUserPhone = loggedInUserPhone,
                        loggedInUserPhotoUri = loggedInUserPhotoUri,
                        walletBalance = userWalletBalance,
                        referralCode = loggedInUserReferralCode,
                        referredByCode = loggedInUserReferredByCode,
                        referralEarningsBdt = viewModel.getLoggedInUserReferralEarnings(),
                        totalReferralsCount = viewModel.getLoggedInUserTotalReferrals(),
                        referralRewardsHistory = viewModel.getLoggedInUserReferralRewardsHistory(),
                        userOrders = currentUserOrders,
                        adminConfig = adminConfig,
                        isAdminLoggedIn = isAdminLoggedIn,
                        unreadNotificationCount = userUnreadNotifCount,
                        savedChametUid = viewModel.getSavedUidForCategory(com.example.data.model.ServiceCategory.CHAMET),
                        savedTikTokUid = viewModel.getSavedUidForCategory(com.example.data.model.ServiceCategory.TIKTOK),
                        savedFreeFireUid = viewModel.getSavedUidForCategory(com.example.data.model.ServiceCategory.FREEFIRE),
                        onBackHome = { viewModel.selectTab(AppNavTab.HOME) },
                        onNavigateTab = { viewModel.selectTab(it) },
                        onUpdateProfile = { name, phone, photoUri ->
                            viewModel.updateUserProfile(name, phone, photoUri)
                        },
                        onSaveDefaultUids = { chametId, tiktokId, ffId ->
                            viewModel.saveDefaultServiceUids(chametId, tiktokId, ffId)
                        },
                        onSubmitAddMoney = { amount, method, trxId, senderNum ->
                            viewModel.depositToWallet(
                                context = context,
                                amount = amount,
                                method = method,
                                trxId = trxId,
                                senderNumber = senderNum
                            )
                        },
                        onApplyReferralCode = { code, cb ->
                            viewModel.applyReferralCodeForLoggedInUser(code, cb)
                        },
                        onOpenOrderReceipt = { ord -> viewModel.setOrderReceipt(ord) },
                        onOpenNotifications = { viewModel.setNotificationScreenVisible(true) },
                        onOpenSupportChat = { viewModel.openSupportChatDialog() },
                        onSendPasswordReset = { email -> viewModel.sendPasswordReset(email, context) },
                        onOpenLoginDialog = { viewModel.openAdminLoginDialog() },
                        onOpenAdminPanel = { viewModel.selectTab(AppNavTab.ADMIN) },
                        onLogout = { viewModel.logoutAdmin() },
                        onCopyText = { label, text -> viewModel.copyText(context, label, text) },
                        walletTransactions = viewModel.getLoggedInUserWalletTransactions(),
                        savedAccounts = viewModel.getLoggedInUserSavedAccounts(),
                        userNotifications = currentUserNotifications,
                        allPackages = viewModel.getAllPackages(),
                        referredMembers = viewModel.getLoggedInUserReferredMembers(),
                        allOrders = orders,
                        onSaveServiceAccount = { serviceName, accountId, label, isDefault, editId ->
                            viewModel.saveOrUpdateUserServiceAccount(
                                serviceName = serviceName,
                                accountId = accountId,
                                label = label,
                                isDefault = isDefault,
                                editId = editId
                            )
                        },
                        onDeleteServiceAccount = { itemId ->
                            viewModel.deleteUserServiceAccount(itemId)
                        },
                        onSetDefaultServiceAccount = { itemId ->
                            viewModel.setDefaultUserServiceAccount(itemId)
                        },
                        onMarkSingleNotificationRead = { notifId ->
                            viewModel.markSingleNotificationRead(notifId)
                        },
                        onMarkAllNotificationsRead = {
                            viewModel.markAllUserNotificationsRead()
                        },
                        onOpenPackageCheckout = { pkg, targetUid ->
                            viewModel.openOrderSheet(pkg, initialAccountId = targetUid)
                        },
                        onOpenWhatsAppSupport = {
                            viewModel.launchWhatsApp(context)
                        },
                        onOpenTelegramSupport = {
                            viewModel.copyToClipboardAndOpenTelegram(context)
                        }
                    )

                    AppNavTab.TIKTOK -> TikTokScreen(
                        customCoinsInput = customTikTokCoins,
                        onCustomCoinsChange = { viewModel.updateCustomTikTokCoins(it) },
                        onPackageClick = { viewModel.openOrderSheet(it) },
                        packages = viewModel.getTikTokPackages(),
                        coinRate = adminConfig.tiktokRatePerCoin
                    )

                    AppNavTab.CHAMET -> ChametScreen(
                        customDiamondsInput = customChametDiamonds,
                        onCustomDiamondsChange = { viewModel.updateCustomChametDiamonds(it) },
                        onPackageClick = { viewModel.openOrderSheet(it) },
                        packages = viewModel.getChametPackages(),
                        diamondRate = adminConfig.chametRatePerDiamond
                    )

                    AppNavTab.FREEFIRE -> FreeFireScreen(
                        onPackageClick = { viewModel.openOrderSheet(it) },
                        packages = viewModel.getFreeFirePackages()
                    )

                    AppNavTab.EPAY -> EpayScreen(
                        epayUsdInput = epayUsd,
                        onEpayUsdChange = { viewModel.updateEpayUsd(it) },
                        onOpenOrderDialog = { viewModel.openEpayOrderDialog() },
                        onSubmitEpayWhatsApp = { viewModel.submitCustomEpayOrder(context, "WhatsApp") },
                        onSubmitEpayTelegram = { viewModel.submitCustomEpayOrder(context, "Telegram") },
                        epayEmail = adminConfig.epayEmail,
                        onCopyEmail = { email -> viewModel.copyText(context, "EPAY Email", email) },
                        epayTiers = viewModel.getEpayTiers(),
                        onCalculateRate = { viewModel.calculateEpay(it) }
                    )

                    AppNavTab.EXCHANGE -> ExchangeScreen(
                        sendMethod = exchangeSendMethod,
                        onSendMethodChange = { viewModel.updateExchangeSendMethod(it) },
                        receiveMethod = exchangeReceiveMethod,
                        onReceiveMethodChange = { viewModel.updateExchangeReceiveMethod(it) },
                        amountBdt = exchangeAmountBdt,
                        onAmountBdtChange = { viewModel.updateExchangeAmount(it) },
                        recipientUpi = exchangeRecipientUpi,
                        onRecipientUpiChange = { viewModel.updateExchangeRecipientUpi(it) },
                        onOpenOrderDialog = { viewModel.openExchangeOrderDialog() },
                        onSubmitExchangeWhatsApp = { viewModel.submitExchangeRequest(context, "WhatsApp") },
                        onSubmitExchangeTelegram = { viewModel.submitExchangeRequest(context, "Telegram") },
                        inrRate = adminConfig.inrBaseRate,
                        epayRate = adminConfig.epayBaseRate
                    )

                    AppNavTab.CALCULATOR -> CalculatorScreen(
                        onWhatsAppDirect = { msg -> viewModel.launchWhatsApp(context, msg) },
                        onTelegramDirect = { msg -> viewModel.copyToClipboardAndOpenTelegram(context, msg) }
                    )

                    AppNavTab.SOCIAL_BOOST -> SocialBoostScreen(
                        adminConfig = adminConfig,
                        onSubmitOrder = { pkg, targetLink ->
                            viewModel.openOrderSheet(pkg, initialAccountId = targetLink)
                        },
                        onWhatsAppSupport = { msg -> viewModel.launchWhatsApp(context, msg) }
                    )

                    AppNavTab.SUPPORT -> SupportScreen(
                        onWhatsAppClick = { viewModel.launchWhatsApp(context) },
                        onTelegramClick = { viewModel.copyToClipboardAndOpenTelegram(context) },
                        onPaymentAccountsClick = { viewModel.setPaymentAccountsSheetVisible(true) },
                        onCopyContact = { label, text -> viewModel.copyText(context, label, text) },
                        onSupportChatClick = { viewModel.openSupportChatDialog() },
                        onAdminLoginClick = { viewModel.openAdminLoginDialog() },
                        adminConfig = adminConfig
                    )

                    AppNavTab.ADMIN -> {
                        val customerProfiles = remember(orders, adminConfig, supportMessages) {
                            viewModel.getCustomerProfiles()
                        }
                        AdminScreen(
                        orders = orders,
                        customers = customerProfiles,
                        adminConfig = adminConfig,
                        firebaseUserEmail = firebaseUserEmail,
                        allPackages = viewModel.getAllPackages(),
                        onUpdateConfig = { viewModel.updateAdminConfig(it) },
                        onUpdateOrderStatus = { ord, status -> viewModel.updateOrderStatus(ord, status) },
                        onDeleteOrder = { viewModel.deleteOrder(it) },
                        onClearAllOrders = { viewModel.clearAllOrders() },
                        onUpdateCustomerProfile = { accountId, displayName, isVip, isBlocked, adminNote ->
                            viewModel.updateCustomerProfile(accountId, displayName, isVip, isBlocked, adminNote)
                        },
                        onAdjustCustomerWallet = { accountIdOrEmail, amountDelta ->
                            viewModel.adjustCustomerWalletByAdmin(accountIdOrEmail, amountDelta)
                        },
                        onSyncCustomers = {
                            viewModel.syncAndRefreshCloudCustomers(context)
                        },
                        onAddCustomerByAdmin = { email, displayName, phone ->
                            viewModel.addOrRestoreCustomerAccountByAdmin(context, email, displayName, phone)
                        },
                        onContactCustomerWhatsApp = { accountIdOrPhone ->
                            val digits = accountIdOrPhone.filter { it.isDigit() }
                            val resolvedCustomerPhone = when {
                                digits.startsWith("8801") && digits.length == 13 -> digits
                                digits.startsWith("01") && digits.length == 11 -> "88$digits"
                                digits.length in 10..15 -> digits
                                else -> ""
                            }
                            if (resolvedCustomerPhone.isNotEmpty()) {
                                try {
                                    val encoded = android.net.Uri.encode("আসসালামু আলাইকুম, CHAMET TOP UP BD এডমিন প্যানেল থেকে যোগাযোগ করা হচ্ছে।")
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse("https://wa.me/$resolvedCustomerPhone?text=$encoded")
                                    )
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    viewModel.launchWhatsApp(context, "Hello $accountIdOrPhone, Chamet Top Up BD সাপোর্ট থেকে যোগাযোগ করা হচ্ছে।")
                                }
                            } else {
                                viewModel.launchWhatsApp(context, "Hello $accountIdOrPhone, Chamet Top Up BD সাপোর্ট থেকে যোগাযোগ করা হচ্ছে।")
                            }
                        },
                        onSendCustomerSuccessWhatsApp = { viewModel.sendCustomerSuccessWhatsApp(context, it) },
                        onSendCustomerCancelWhatsApp = { ord, reason -> viewModel.sendCustomerCancelWhatsApp(context, ord, reason) },
                        onChangePassword = { oldP, newP -> viewModel.changeAdminPassword(oldP, newP) },
                        onUpdatePackagePrice = { pkgId, price -> viewModel.updatePackagePrice(pkgId, price) },
                        onAddOrUpdatePackage = { pkg -> viewModel.addOrUpdatePackage(pkg) },
                        onDeletePackage = { pkgId -> viewModel.deletePackage(pkgId) },
                        onResetPackagePrices = { viewModel.resetPackagePrices() },
                        onUpdateExchangeRates = { epayB, epay97, epay10, epay30, epay500, inrB, ttRate, chRate ->
                            viewModel.updateExchangeRates(epayB, epay97, epay10, epay30, epay500, inrB, ttRate, chRate)
                        },
                        onAutoFulfillOrder = { ord -> viewModel.dispatchAutoFulfillOrder(context, ord) },
                        onTestApi = { url, key, cb -> viewModel.testProviderApi(url, key, cb) },
                        supportMessages = supportMessages,
                        onSendAdminSupportMessage = { convId, custName, custPhone, custEmail, msgText, imgUrl ->
                            viewModel.sendAdminSupportChatMessage(
                                context = context,
                                conversationId = convId,
                                targetCustomerName = custName,
                                targetCustomerContact = custPhone,
                                targetCustomerEmail = custEmail,
                                messageText = msgText,
                                imageUrl = imgUrl
                            )
                        },
                        onMarkSupportConversationRead = { convId ->
                            viewModel.markSupportConversationReadByAdmin(convId)
                        },
                        onReplySupportMessage = { id, reply -> viewModel.replyToSupportChatMessage(context, id, reply) },
                        onDeleteSupportMessage = { id -> viewModel.deleteSupportChatMessage(id) },
                        onDeleteSupportConversation = { convId -> viewModel.deleteSupportConversation(convId) },
                        onToggleMasterAiAutoChat = { enabled -> viewModel.toggleMasterAiAutoChat(context, enabled) },
                        onSendAdminAiCommand = { cmd -> viewModel.sendAdminAiCommand(context, cmd) },
                        onClearAdminAiInstructions = { viewModel.clearAdminAiInstructions(context) },
                        onSetSupportConversationMode = { convId, custName, custPhone, custEmail, mode ->
                            viewModel.setSupportConversationModeByAdmin(
                                context = context,
                                conversationId = convId,
                                targetCustomerName = custName,
                                targetCustomerContact = custPhone,
                                targetCustomerEmail = custEmail,
                                mode = mode
                            )
                        },
                        onLogout = { viewModel.logoutAdmin() },
                        onCopyText = { label, text -> viewModel.copyText(context, label, text) }
                    )
                    }
                }

                // Verified Payment Accounts Sheet
                if (showPaymentAccounts) {
                    PaymentAccountsSheet(
                        onDismiss = { viewModel.setPaymentAccountsSheetVisible(false) },
                        onCopyNumber = { label, number -> viewModel.copyText(context, label, number) },
                        accounts = viewModel.getPaymentAccountsList()
                    )
                }

                // Secure Firebase Customer & Admin Authentication Dialog (Sign Up, Login & Profile)
                if (showAdminLoginDialog) {
                    AdminLoginDialog(
                        defaultAdminEmail = adminConfig.adminEmail,
                        isLoading = isAuthLoading,
                        errorMessage = authErrorMessage,
                        successMessage = authSuccessMessage,
                        hasRegisteredAdmin = viewModel.hasRegisteredAdminAccount(),
                        startInRegisterMode = authDialogStartInRegister,
                        loggedInUserEmail = loggedInUserEmail,
                        loggedInUserName = loggedInUserName,
                        loggedInUserPhone = loggedInUserPhone,
                        isAdminLoggedIn = isAdminLoggedIn,
                        orders = orders,
                        onDismiss = { viewModel.dismissAdminLoginDialog() },
                        onSignInWithFirebase = { email, pass ->
                            viewModel.signInWithFirebaseAuth(email, pass, context)
                        },
                        onRegisterAdminWithFirebase = { email, pass ->
                            viewModel.registerWithFirebaseAuth(email, pass, context)
                        },
                        onRegisterUserWithFirebase = { name, phone, email, pass, refCode ->
                            viewModel.registerWithFirebaseAuth(
                                email = email,
                                pass = pass,
                                context = context,
                                displayName = name,
                                phone = phone,
                                referralCode = refCode
                            )
                        },
                        onSendPasswordReset = { email ->
                            viewModel.sendPasswordReset(email, context)
                        },
                        onLogoutUser = {
                            viewModel.logoutAdmin()
                        },
                        onOpenAdminPanel = {
                            viewModel.dismissAdminLoginDialog()
                            viewModel.selectTab(AppNavTab.ADMIN)
                        },
                        onOpenFullProfile = {
                            viewModel.dismissAdminLoginDialog()
                            viewModel.selectTab(AppNavTab.PROFILE)
                        },
                        onOpenNotifications = {
                            viewModel.dismissAdminLoginDialog()
                            viewModel.setNotificationScreenVisible(true)
                        }
                    )
                }

                // In-App 2-Step Payment Gateway Dialog (Matching Screenshot 1 & Screenshot 2)
                selectedPackageForGateway?.let { pkg ->
                    com.example.ui.components.PaymentGatewayDialog(
                        packageItem = pkg,
                        adminConfig = adminConfig,
                        initialAccountId = savedUidForCurrentCategory,
                        walletBalance = userWalletBalance,
                        existingOrders = orders,
                        onDismiss = { viewModel.dismissPaymentGateway() },
                        onSubmitOrder = { p, accountId, trxId, method ->
                            viewModel.submitGatewayOrder(
                                context = context,
                                pkg = p,
                                accountId = accountId,
                                trxId = trxId,
                                paymentMethod = method,
                                dismissGateway = false
                            )
                        },
                        onPayWithWallet = { p, accountId ->
                            viewModel.submitWalletInstantOrder(context, p, accountId) { success, msg ->
                                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        onOpenWalletAddMoney = {
                            viewModel.selectTab(AppNavTab.PROFILE)
                        },
                        onOpenLiveChat = {
                            viewModel.openSupportChatDialog()
                        },
                        onCopyText = { label, text -> viewModel.copyText(context, label, text) }
                    )
                }

                // Exchange Checkout Order Dialog (PhonePe / UPI / QR Screenshot Flow)
                if (showExchangeOrderDialog) {
                    val bdtVal = exchangeAmountBdt.toDoubleOrNull() ?: 1000.0
                    val inrRate = adminConfig.inrBaseRate
                    val inrVal = if (inrRate > 0) bdtVal / inrRate else bdtVal * 0.69
                    com.example.ui.components.ExchangeOrderDialog(
                        sendMethod = exchangeSendMethod.ifBlank { "bKash" },
                        receiveMethod = exchangeReceiveMethod.ifBlank { "PhonePe" },
                        amountBdt = bdtVal,
                        estimatedInr = inrVal,
                        inrRate = inrRate,
                        adminConfig = adminConfig,
                        onDismiss = { viewModel.dismissExchangeOrderDialog() },
                        onSubmitOrder = { sMethod, rMethod, bdt, inr, phonePe, upiUrl, screenshotUri, trxId ->
                            viewModel.submitDirectExchangeOrder(
                                context = context,
                                sendMethod = sMethod,
                                receiveMethod = rMethod,
                                bdt = bdt,
                                inr = inr,
                                phonePeNumber = phonePe,
                                upiUrl = upiUrl,
                                screenshotUri = screenshotUri,
                                trxId = trxId
                            )
                        },
                        onCopyText = { label, text -> viewModel.copyText(context, label, text) }
                    )
                }

                // EPAY Exchange Checkout Order Dialog
                if (showEpayOrderDialog) {
                    val usdVal = epayUsd.toDoubleOrNull() ?: 10.0
                    val (bdtVal, rateVal) = viewModel.calculateEpay(usdVal)
                    com.example.ui.components.EpayOrderDialog(
                        usdAmount = usdVal,
                        bdtAmount = bdtVal,
                        appliedRate = rateVal,
                        receiverEmail = adminConfig.epayEmail,
                        onDismiss = { viewModel.dismissEpayOrderDialog() },
                        onSubmitOrder = { usd, bdt, rate, orderNo, sender, method, payoutNum ->
                            viewModel.submitEpayDirectOrder(
                                context = context,
                                usd = usd,
                                bdt = bdt,
                                rate = rate,
                                epayOrderNo = orderNo,
                                senderAccount = sender,
                                payoutMethod = method,
                                payoutNumber = payoutNum
                            )
                        },
                        onCopyText = { label, text -> viewModel.copyText(context, label, text) }
                    )
                }

                // Package Order Contact Chooser (WhatsApp vs Telegram Pop-up)
                selectedPackageForChooser?.let { pkg ->
                    com.example.ui.components.PackageContactChooserDialog(
                        packageItem = pkg,
                        onDismiss = { viewModel.dismissPackageChooser() },
                        onWhatsAppClick = { viewModel.orderPackageViaWhatsApp(context, it) },
                        onTelegramClick = { viewModel.orderPackageViaTelegram(context, it) }
                    )
                }

                // Order Checkout Bottom Sheet
                orderFormState?.let { form ->
                    OrderBottomSheet(
                        formState = form,
                        onDismiss = { viewModel.dismissOrderSheet() },
                        onUidChange = { viewModel.updateOrderForm(uid = it) },
                        onNameChange = { viewModel.updateOrderForm(name = it) },
                        onPaymentChange = { viewModel.updateOrderForm(payment = it) },
                        onTrxChange = { viewModel.updateOrderForm(trx = it) },
                        onNoteChange = { viewModel.updateOrderForm(note = it) },
                        onSaveUidToggle = { viewModel.updateOrderForm(saveUid = it) },
                        onSubmitOrderWhatsApp = { viewModel.submitOrder(context, "WhatsApp") },
                        onSubmitOrderTelegram = { viewModel.submitOrder(context, "Telegram") },
                        onCopyPaymentNumber = { label, number -> viewModel.copyText(context, label, number) }
                    )
                }

                // Digital Order Receipt Dialog (Always resolves live order status from Room)
                selectedReceipt?.let { order ->
                    val liveOrder = orders.find { it.orderNumber == order.orderNumber } ?: order
                    ReceiptDialog(
                        order = liveOrder,
                        onDismiss = { viewModel.setOrderReceipt(null) },
                        onCopyReceipt = { receiptText -> viewModel.copyText(context, "Receipt Slip", receiptText) },
                        onDeleteOrder = { viewModel.deleteOrder(it) },
                        onWhatsAppDirect = {
                            viewModel.openSupportChatDialog()
                        },
                        onOpenLiveChat = {
                            viewModel.openSupportChatDialog()
                        }
                    )
                }
                // Real-Time In-App Floating Order Approval / Status Alert Banner (Strictly for the Logged-In User)
                AnimatedVisibility(
                    visible = visibleBannerNotification != null,
                    enter = fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { -it }),
                    exit = fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { -it }),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    visibleBannerNotification?.let { alertNotif ->
                        val isApproved = alertNotif.status == "Approved" || alertNotif.status == "Completed"
                        val isError = alertNotif.status == "Incorrect TrxID" || alertNotif.status == "Cancelled"
                        val accentColor = when {
                            isApproved -> Color(0xFF22C55E)
                            isError -> Color(0xFFEF4444)
                            else -> Color(0xFFF59E0B)
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xF5110726),
                            border = BorderStroke(
                                1.5.dp,
                                Brush.horizontalGradient(listOf(accentColor, GoldPrimary))
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(16.dp, RoundedCornerShape(18.dp), spotColor = accentColor)
                                .clickable {
                                    val matchingOrder = currentUserOrders.find { it.orderNumber == alertNotif.orderNumber }
                                    viewModel.dismissBannerNotification()
                                    if (matchingOrder != null) {
                                        viewModel.setOrderReceipt(matchingOrder)
                                    } else {
                                        viewModel.setNotificationScreenVisible(true)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(accentColor.copy(alpha = 0.2f))
                                        .border(1.dp, accentColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isApproved -> Icons.Default.CheckCircle
                                            isError -> Icons.Default.Error
                                            else -> Icons.Default.NotificationsActive
                                        },
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alertNotif.title,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = alertNotif.message,
                                        fontSize = 11.sp,
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 15.sp,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "ট্যাপ করে রিসিট ও নোটিফিকেশন দেখুন ➔",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .clickable { viewModel.dismissBannerNotification() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss Notification Banner",
                                        tint = Color(0xFFCBD5E1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. App Launch Emergency Rules & Official Notice (জরুরি নিয়মাবলী) - Shown ONLY when the app opens
        if (showAppLaunchNotice) {
            AppLaunchRulesDialog(
                onDismiss = { viewModel.dismissAppLaunchNotice() },
                onWhatsAppClick = {
                    viewModel.dismissAppLaunchNotice()
                    viewModel.launchWhatsApp(context)
                },
                onTelegramClick = {
                    viewModel.dismissAppLaunchNotice()
                    viewModel.copyToClipboardAndOpenTelegram(context)
                },
                adminConfig = adminConfig
            )
        }

        // 2. Full-Screen Notifications Center - Opened via top notification icon (No Emergency Rules here)
        if (showNotificationScreen) {
            FullScreenNotificationScreen(
                onDismiss = { viewModel.setNotificationScreenVisible(false) },
                onWhatsAppClick = {
                    viewModel.launchWhatsApp(context)
                },
                orders = orders,
                notifications = notifications,
                currentUserEmail = loggedInUserEmail,
                userClearedTimestamp = userClearedNotifTimestamp,
                onOpenOrderReceipt = { ord ->
                    viewModel.setNotificationScreenVisible(false)
                    viewModel.setOrderReceipt(ord)
                },
                onClearNotifications = { viewModel.clearAllNotifications() }
            )
        }

        // Full-Screen In-Window WhatsApp / Messenger Style Live Support Chat
        if (showSupportChatDialog) {
            SupportChatDialog(
                messages = supportMessages,
                loggedInUserName = loggedInUserName,
                loggedInUserEmail = loggedInUserEmail,
                loggedInUserPhone = loggedInUserPhone,
                savedSupportName = supportChatUserName,
                savedSupportPhone = supportChatUserPhone,
                savedSupportEmail = supportChatUserEmail,
                deviceId = viewModel.getSupportDeviceId(),
                isSending = isSendingSupportMessage,
                isAiTyping = isAiTypingSupportReply,
                conversationMode = viewModel.getSupportConversationMode(loggedInUserEmail),
                isAiMasterEnabled = adminConfig.isAiAutoChatEnabled,
                isAuthLoading = isAuthLoading,
                authErrorMessage = authErrorMessage,
                onSignInUser = { email, pass ->
                    viewModel.signInWithFirebaseAuth(email, pass, context)
                },
                onRegisterUser = { name, phone, email, pass ->
                    viewModel.registerWithFirebaseAuth(
                        email = email,
                        pass = pass,
                        context = context,
                        displayName = name,
                        phone = phone
                    )
                },
                onDismiss = { viewModel.dismissSupportChatDialog() },
                onSaveUserProfile = { name, phone, email ->
                    viewModel.saveSupportChatUserProfile(name, phone, email)
                },
                onRequestAdminHandoff = {
                    viewModel.requestSupportAdminHandoff(context)
                },
                onResumeAiAutoReply = {
                    viewModel.resumeSupportAiAutoReply(context)
                },
                onMarkMessagesReadByUser = { convId ->
                    viewModel.markSupportConversationReadByUser(convId)
                },
                onSendMessage = { senderName, senderContact, senderEmail, messageText, imageUrl ->
                    viewModel.sendSupportChatMessage(
                        context = context,
                        senderName = senderName,
                        senderContact = senderContact,
                        senderEmail = senderEmail,
                        messageText = messageText,
                        imageUrl = imageUrl
                    )
                }
            )
        }
        }
    }
}

@Composable
fun BottomNavBar(
    currentTab: AppNavTab,
    orderCount: Int,
    isAdminLoggedIn: Boolean = false,
    onSelectTab: (AppNavTab) -> Unit
) {
    NeonGlassBottomNavBar(
        currentTab = currentTab,
        onSelectTab = onSelectTab
    )
}

@Composable
fun SplashScreen(
    onSkip: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        val totalDuration = 1400L
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val p = (elapsed.toFloat() / totalDuration).coerceIn(0f, 1f)
            progress = p
            if (p >= 1f) {
                delay(60)
                onSkip()
                break
            }
            delay(40)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF38145E), Color(0xFF140526), Color(0xFF070010))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Glowing Official Chamet Top Up BD Emblem with Star Badge
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.size(116.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(106.dp)
                        .align(Alignment.Center)
                        .scale(scale)
                        .shadow(28.dp, CircleShape, spotColor = GoldPrimary)
                        .clip(CircleShape)
                        .background(Color(0xFF05010D))
                        .border(
                            3.dp,
                            Brush.linearGradient(listOf(GoldLight, GoldPrimary, NeonMagenta)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_chamet_topup_logo_1790587594025),
                        contentDescription = "Chamet Top Up BD Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                // Small Top-Right Star Badge
                Surface(
                    shape = CircleShape,
                    color = NeonMagenta,
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.5.dp, GoldPrimary, CircleShape)
                        .align(Alignment.TopEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "✨", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Chamet Top Up BD",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = GoldPrimary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "TRUSTED RATE • FAST PAYMENT • SECURE DEAL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD1D5DB),
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Progress text: "Loading Services..." and "91%"
            Row(
                modifier = Modifier.width(220.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Loading Services...",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF9CA3AF)
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF240E3E))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeonMagenta, GoldPrimary, GoldLight)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Skip Intro Button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSkip() },
                color = Color(0xFF1D0B34),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A1F78))
            ) {
                Text(
                    text = "Skip Intro ➔",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD8B4FE),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}
