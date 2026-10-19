package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdminConfig
import com.example.data.model.CustomerProfile
import com.example.data.model.OrderRecord
import com.example.data.model.PromoBanner
import com.example.data.model.ServiceCategory
import com.example.data.model.SupportChatMessage
import com.example.data.model.TopupPackage
import com.example.ui.components.AdminChatOpenRequest
import com.example.ui.components.AdminSocialBoostTab
import com.example.ui.components.AdminSupportMessagesTab
import com.example.ui.components.PremiumWhatsAppButton
import com.example.ui.theme.BrandDarkBg
import com.example.ui.theme.BrandDarkCard
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.WhatsAppGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    orders: List<OrderRecord>,
    customers: List<CustomerProfile> = emptyList(),
    adminConfig: AdminConfig,
    firebaseUserEmail: String? = null,
    allPackages: List<TopupPackage> = emptyList(),
    onUpdateConfig: (AdminConfig) -> Unit,
    onUpdatePackagePrice: (String, Double) -> Unit,
    onAddOrUpdatePackage: (TopupPackage) -> Unit = {},
    onDeletePackage: (String) -> Unit = {},
    onResetPackagePrices: () -> Unit,
    onUpdateExchangeRates: (Double, Double, Double, Double, Double, Double, Double, Double) -> Unit,
    onUpdateOrderStatus: (OrderRecord, String) -> Unit,
    onDeleteOrder: (OrderRecord) -> Unit,
    onClearAllOrders: () -> Unit,
    onUpdateCustomerProfile: (accountId: String, displayName: String, isVip: Boolean, isBlocked: Boolean, adminNote: String) -> Unit = { _, _, _, _, _ -> },
    onAdjustCustomerWallet: (accountIdOrEmail: String, amountDelta: Double) -> Unit = { _, _ -> },
    onSyncCustomers: () -> Unit = {},
    onAddCustomerByAdmin: (email: String, displayName: String, phone: String) -> Unit = { _, _, _ -> },
    onContactCustomerWhatsApp: (String) -> Unit = {},
    onSendCustomerSuccessWhatsApp: (OrderRecord) -> Unit,
    onSendCustomerCancelWhatsApp: (OrderRecord, String) -> Unit,
    onChangePassword: (String, String) -> Boolean = { _, _ -> true },
    onAutoFulfillOrder: ((OrderRecord) -> Unit)? = null,
    onTestApi: ((String, String, (Boolean, String) -> Unit) -> Unit)? = null,
    supportMessages: List<SupportChatMessage> = emptyList(),
    onSendAdminSupportMessage: (
        conversationId: String,
        customerName: String,
        customerContact: String,
        customerEmail: String,
        messageText: String,
        imageUrl: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    onMarkSupportConversationRead: (String) -> Unit = {},
    onReplySupportMessage: (String, String) -> Unit = { _, _ -> },
    onDeleteSupportMessage: (String) -> Unit = {},
    onDeleteSupportConversation: (String) -> Unit = {},
    onToggleMasterAiAutoChat: (Boolean) -> Unit = {},
    onSendAdminAiCommand: (String) -> Unit = {},
    onClearAdminAiInstructions: () -> Unit = {},
    onSetSupportConversationMode: (
        conversationId: String,
        customerName: String,
        customerContact: String,
        customerEmail: String,
        mode: String
    ) -> Unit = { _, _, _, _, _ -> },
    onLogout: () -> Unit,
    onCopyText: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var isSupportConversationOpen by remember { mutableStateOf(false) }
    var targetChatRequest by remember { mutableStateOf<AdminChatOpenRequest?>(null) }
    var initialOrderCategoryFilter by remember { mutableStateOf("ALL") }

    val openCustomerLiveChat: (String, String, String, String) -> Unit = { accountId, name, phone, email ->
        val resolvedConvKey = email.trim().ifBlank { phone.trim().ifBlank { accountId.trim() } }.lowercase()
        targetChatRequest = AdminChatOpenRequest(
            conversationId = resolvedConvKey,
            customerName = name.trim().ifBlank { accountId.trim() },
            customerPhone = phone.trim(),
            customerEmail = email.trim().lowercase()
        )
        isSupportConversationOpen = true
        selectedTab = 2
    }

    val unreadSupportCount = remember(supportMessages) {
        supportMessages.count {
            val role = it.senderRole.uppercase()
            (role == "USER" || it.adminReply == "MODE:HANDOFF_REQUESTED") && !it.isReadByAdmin
        }
    }

    val tabs = listOf(
        "📊 ওভারভিউ",
        "📋 অর্ডারসমূহ (${orders.size})",
        "💬 সাপোর্ট চ্যাট (${supportMessages.size})",
        "👥 কাস্টমারসমূহ (${customers.size})",
        "💰 প্রাইস ও প্যাকেজ",
        "🚀 Social Boost (${adminConfig.socialBoostServices.size})",
        "💱 এক্সচেঞ্জ রেটস",
        "🎨 ব্যানার কন্ট্রোল (${adminConfig.promoBanners.size})",
        "🤖 অটো-ডেলিভারি (API)",
        "💳 পেমেন্ট নাম্বার",
        "📢 নোটিশ কন্ট্রোল",
        "⚙️ সেটিংস ও সিকিউরিটি"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandDarkBg)
    ) {
        if (!(selectedTab == 2 && isSupportConversationOpen)) {
            // Admin Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF19062E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                .background(GoldPrimary.copy(alpha = 0.2f))
                                .border(1.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Admin Control Room",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = SuccessGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, SuccessGreen)
                                ) {
                                    Text(
                                        text = "Firebase 🛡️",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = firebaseUserEmail ?: adminConfig.adminEmail,
                                fontSize = 11.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick Support Chat Top Button
                        Surface(
                            onClick = {
                                isSupportConversationOpen = false
                                selectedTab = 2
                            },
                            shape = RoundedCornerShape(50),
                            color = if (selectedTab == 2) GoldPrimary else Color(0xFF2B104E),
                            border = BorderStroke(1.dp, GoldPrimary)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (unreadSupportCount > 0) "💬 চ্যাট ($unreadSupportCount)" else "💬 চ্যাট",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedTab == 2) Color(0xFF140526) else GoldLight
                                )
                            }
                        }

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF32124D), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = DangerRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Horizontal Scrollable Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F041C),
                contentColor = GoldPrimary,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GoldPrimary,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            if (index != 2) isSupportConversationOpen = false
                            selectedTab = index
                        },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) GoldPrimary else Color(0xFF9CA3AF)
                            )
                        }
                    )
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> AdminOverviewTab(
                    orders = orders,
                    adminConfig = adminConfig,
                    unreadSupportCount = unreadSupportCount,
                    onNavigateToOrders = {
                        initialOrderCategoryFilter = "ALL"
                        selectedTab = 1
                    },
                    onNavigateToAddMoneyOrders = {
                        initialOrderCategoryFilter = "ADD_MONEY"
                        selectedTab = 1
                    },
                    onNavigateToSupportChat = {
                        isSupportConversationOpen = false
                        selectedTab = 2
                    },
                    onNavigateToPrices = { selectedTab = 4 }
                )
                1 -> AdminOrdersTab(
                    orders = orders,
                    initialCategoryFilter = initialOrderCategoryFilter,
                    onUpdateStatus = onUpdateOrderStatus,
                    onDeleteOrder = onDeleteOrder,
                    onClearAll = onClearAllOrders,
                    onSendSuccess = onSendCustomerSuccessWhatsApp,
                    onSendCancel = onSendCustomerCancelWhatsApp,
                    onOpenLiveChat = { order ->
                        openCustomerLiveChat(
                            order.userAccountId,
                            order.customerName.ifBlank { order.userAccountId },
                            order.customerPhone,
                            order.customerEmail
                        )
                    },
                    onContactWhatsApp = { order ->
                        val phoneOrAcc = order.customerPhone.ifBlank { order.userAccountId }
                        if (phoneOrAcc.any { it.isDigit() } && phoneOrAcc.length >= 10) {
                            onContactCustomerWhatsApp(phoneOrAcc)
                        } else if (order.status == "Cancelled") {
                            onSendCustomerCancelWhatsApp(order, "ভুল TrxID বা পেমেন্ট তথ্যে গরমিল থাকায়")
                        } else {
                            onSendCustomerSuccessWhatsApp(order)
                        }
                    },
                    onCopy = onCopyText,
                    onAutoFulfill = onAutoFulfillOrder
                )
                2 -> AdminSupportMessagesTab(
                    messages = supportMessages,
                    orders = orders,
                    customers = customers,
                    isAiMasterEnabled = adminConfig.isAiAutoChatEnabled,
                    supportHandoffMap = adminConfig.supportChatHandoffMap,
                    adminCustomAiInstructions = adminConfig.adminCustomAiInstructions,
                    onSendAdminAiCommand = onSendAdminAiCommand,
                    onClearAdminAiInstructions = onClearAdminAiInstructions,
                    targetConversationRequest = targetChatRequest,
                    onTargetConversationConsumed = { targetChatRequest = null },
                    onToggleMasterAi = onToggleMasterAiAutoChat,
                    onSetConversationMode = onSetSupportConversationMode,
                    onSendAdminMessage = onSendAdminSupportMessage,
                    onMarkConversationRead = onMarkSupportConversationRead,
                    onReply = onReplySupportMessage,
                    onDelete = onDeleteSupportMessage,
                    onDeleteConversation = onDeleteSupportConversation,
                    onContactWhatsApp = onContactCustomerWhatsApp,
                    onCopyText = onCopyText,
                    onConversationOpenChanged = { isOpen ->
                        isSupportConversationOpen = isOpen
                    }
                )
                3 -> AdminCustomersTab(
                    customers = customers,
                    adminConfig = adminConfig,
                    onSaveConfig = onUpdateConfig,
                    onUpdateCustomer = onUpdateCustomerProfile,
                    onAdjustCustomerWallet = onAdjustCustomerWallet,
                    onSyncCustomers = onSyncCustomers,
                    onAddCustomerByAdmin = onAddCustomerByAdmin,
                    onOpenCustomerLiveChat = { customer ->
                        openCustomerLiveChat(
                            customer.accountId,
                            customer.displayName.ifBlank { customer.accountId },
                            customer.phone,
                            customer.email
                        )
                    },
                    onContactWhatsApp = onContactCustomerWhatsApp,
                    onCopy = onCopyText
                )
                4 -> AdminProductPricingTab(
                    packages = allPackages,
                    customPrices = adminConfig.customPrices,
                    onUpdatePrice = onUpdatePackagePrice,
                    onAddOrUpdatePackage = onAddOrUpdatePackage,
                    onDeletePackage = onDeletePackage,
                    onResetAll = onResetPackagePrices
                )
                5 -> AdminSocialBoostTab(
                    config = adminConfig,
                    onSaveConfig = onUpdateConfig
                )
                6 -> AdminExchangeRatesTab(
                    config = adminConfig,
                    onSaveRates = onUpdateExchangeRates
                )
                7 -> AdminBannersTab(
                    config = adminConfig,
                    onSave = onUpdateConfig
                )
                8 -> AdminAutoDeliveryTab(
                    config = adminConfig,
                    onSave = onUpdateConfig,
                    onTestApi = onTestApi
                )
                9 -> AdminPaymentTab(config = adminConfig, onSave = onUpdateConfig)
                10 -> AdminNoticeTab(config = adminConfig, onSave = onUpdateConfig)
                11 -> AdminSettingsTab(
                    config = adminConfig,
                    firebaseEmail = firebaseUserEmail,
                    onSave = onUpdateConfig,
                    onChangePassword = onChangePassword,
                    onLogout = onLogout
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 0: OVERVIEW & METRICS
// -------------------------------------------------------------
@Composable
fun AdminOverviewTab(
    orders: List<OrderRecord>,
    adminConfig: AdminConfig,
    unreadSupportCount: Int = 0,
    onNavigateToOrders: () -> Unit,
    onNavigateToAddMoneyOrders: () -> Unit = {},
    onNavigateToSupportChat: () -> Unit = {},
    onNavigateToPrices: () -> Unit
) {
    val totalRevenue = orders.filter { it.status == "Completed" }.sumOf { it.priceBdt }
    val pendingCount = orders.count { it.status == "Processing" }
    val completedCount = orders.count { it.status == "Completed" }
    val cancelledCount = orders.count { it.status == "Cancelled" }
    val addMoneyOrdersCount = orders.count {
        it.category.contains("Wallet", ignoreCase = true) ||
            it.category.contains("Add Money", ignoreCase = true) ||
            it.itemName.contains("Add Money", ignoreCase = true) ||
            it.orderNumber.startsWith("WAL-", ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Live Status Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1F0B36),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "🛡️ Firebase Manager Session", fontSize = 11.sp, color = GoldLight)
                        Text(text = adminConfig.adminEmail, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = "bKash: ${adminConfig.bkashNumber} | Nagad: ${adminConfig.nagadNumber}",
                            fontSize = 11.sp,
                            color = Color(0xFFD1D5DB)
                        )
                    }
                    Surface(
                        color = SuccessGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                    ) {
                        Text(
                            text = "LIVE ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(text = "📈 সামগ্রিক পরিসংখ্যান (Analytics)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "মোট আয় (Completed)",
                    value = "৳${String.format(Locale.US, "%,.0f", totalRevenue)}",
                    subtitle = "$completedCount টি সফল অর্ডার",
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "চলমান অর্ডার",
                    value = "$pendingCount টি",
                    subtitle = "অপেক্ষমাণ রয়েছে",
                    color = WarningYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "মোট অর্ডার সংখ্যা",
                    value = "${orders.size} টি",
                    subtitle = "ডাটাবেসে সংরক্ষিত",
                    color = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "বাতিলকৃত অর্ডার",
                    value = "$cancelledCount টি",
                    subtitle = "সমস্যাযুক্ত অর্ডার",
                    color = DangerRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(text = "⚡ কুইক অ্যাকশন (Quick Actions)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onNavigateToSupportChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A884))
                ) {
                    Text(
                        text = if (unreadSupportCount > 0) {
                            "💬 কাস্টমার লাইভ সাপোর্ট চ্যাট ওপেন করুন ($unreadSupportCount নতুন মেসেজ)"
                        } else {
                            "💬 কাস্টমার লাইভ সাপোর্ট চ্যাট ইনবক্স ওপেন করুন"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onNavigateToPrices,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1054))
                    ) {
                        Text(text = "💰 প্রাইস এডিট", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }

                    Button(
                        onClick = onNavigateToOrders,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text(text = "📋 অর্ডার দেখুন ($pendingCount)", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF140526))
                    }
                }

                Button(
                    onClick = onNavigateToAddMoneyOrders,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Text(
                        text = "💳 এড মানি (Add Money) রিকোয়েস্ট পেজ দেখুন ($addMoneyOrdersCount টি)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        // Live Rates Summary
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF140526),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "💱 বর্তমান সক্রিয় রেটসমূহ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "EPAY Base Rate:", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                        Text(text = "৳${adminConfig.epayBaseRate} / USD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "EPAY 9.70 USD Fix:", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                        Text(text = "৳${adminConfig.epayRate970Fixed}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "BD ➔ INR Base:", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                        Text(text = "৳${adminConfig.inrBaseRate} per 1 INR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF18072E),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.5.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.5.sp, color = Color(0xFF6B7280))
        }
    }
}

// -------------------------------------------------------------
// TAB: PRODUCT PRICING & PACKAGE MANAGEMENT (ADD / EDIT / DELETE)
// -------------------------------------------------------------
@Composable
fun AdminProductPricingTab(
    packages: List<TopupPackage>,
    customPrices: Map<String, Double>,
    onUpdatePrice: (String, Double) -> Unit,
    onAddOrUpdatePackage: (TopupPackage) -> Unit = {},
    onDeletePackage: (String) -> Unit = {},
    onResetAll: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategoryFilter by remember { mutableStateOf<ServiceCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var editingPackage by remember { mutableStateOf<TopupPackage?>(null) }
    var newPriceInput by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }

    // Full Add / Edit Package Dialog state
    var showPackageEditor by remember { mutableStateOf(false) }
    var editorPkgId by remember { mutableStateOf("") }
    var editorCategory by remember { mutableStateOf(ServiceCategory.TIKTOK) }
    var editorName by remember { mutableStateOf("") }
    var editorSubtitle by remember { mutableStateOf("") }
    var editorAmount by remember { mutableStateOf("") }
    var editorPrice by remember { mutableStateOf("") }
    var editorBadge by remember { mutableStateOf("") }

    val filteredPackages = remember(packages, selectedCategoryFilter, searchQuery) {
        packages.filter { pkg ->
            (selectedCategoryFilter == null || pkg.category == selectedCategoryFilter) &&
            (searchQuery.isBlank() || pkg.name.contains(searchQuery, ignoreCase = true) || pkg.subtitle.contains(searchQuery, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Header Info & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "💰 প্রাইস ও প্যাকেজ কন্ট্রোল", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(text = "নতুন প্যাকেজ যোগ করুন বা যেকোনো প্যাকেজের দাম আপডেট করুন", fontSize = 11.sp, color = GoldLight)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            editorPkgId = "pkg_${System.currentTimeMillis()}"
                            editorCategory = selectedCategoryFilter ?: ServiceCategory.TIKTOK
                            editorName = ""
                            editorSubtitle = "Official Pack"
                            editorAmount = ""
                            editorPrice = ""
                            editorBadge = "Hot"
                            showPackageEditor = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF140526), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "নতুন প্যাকেজ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF140526))
                    }
                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🔄 রিসেট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("প্যাকেজ খুঁজুন (e.g. 500 Coins, 115 Diamonds...)", fontSize = 12.sp, color = Color(0xFF6B7280)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF9CA3AF), modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedContainerColor = Color(0xFF140526),
                    unfocusedContainerColor = Color(0xFF140526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text("সব (${packages.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color(0xFF140526),
                        containerColor = Color(0xFF1C0933),
                        labelColor = Color(0xFFD1D5DB)
                    )
                )
                FilterChip(
                    selected = selectedCategoryFilter == ServiceCategory.TIKTOK,
                    onClick = { selectedCategoryFilter = ServiceCategory.TIKTOK },
                    label = { Text("TikTok", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color(0xFF140526),
                        containerColor = Color(0xFF1C0933),
                        labelColor = Color(0xFFD1D5DB)
                    )
                )
                FilterChip(
                    selected = selectedCategoryFilter == ServiceCategory.CHAMET,
                    onClick = { selectedCategoryFilter = ServiceCategory.CHAMET },
                    label = { Text("Chamet", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color(0xFF140526),
                        containerColor = Color(0xFF1C0933),
                        labelColor = Color(0xFFD1D5DB)
                    )
                )
                FilterChip(
                    selected = selectedCategoryFilter == ServiceCategory.FREEFIRE,
                    onClick = { selectedCategoryFilter = ServiceCategory.FREEFIRE },
                    label = { Text("Gaming", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = Color(0xFF140526),
                        containerColor = Color(0xFF1C0933),
                        labelColor = Color(0xFFD1D5DB)
                    )
                )
            }
        }

        // Packages List
        items(filteredPackages, key = { it.id }) { pkg ->
            val hasCustomPrice = customPrices.containsKey(pkg.id)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF17062B),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hasCustomPrice) GoldPrimary else Color(0xFF38155E)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = pkg.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (hasCustomPrice) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = GoldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldPrimary)
                                ) {
                                    Text(
                                        text = "Custom",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${pkg.category.displayName} • ${pkg.subtitle}",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                editingPackage = pkg
                                newPriceInput = String.format(Locale.US, "%.0f", pkg.priceBdt)
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF2C0F4A), CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Price", tint = GoldPrimary, modifier = Modifier.size(15.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                onDeletePackage(pkg.id)
                                Toast.makeText(context, "🗑️ ${pkg.name} মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF310B22), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Package", tint = DangerRed, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }

    // Add / Full Edit Package Dialog
    if (showPackageEditor) {
        AlertDialog(
            onDismissRequest = { showPackageEditor = false },
            title = {
                Text(text = "📦 নতুন প্যাকেজ সংযোজন (Cloud Sync)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(ServiceCategory.TIKTOK, ServiceCategory.CHAMET, ServiceCategory.FREEFIRE).forEach { cat ->
                            FilterChip(
                                selected = editorCategory == cat,
                                onClick = { editorCategory = cat },
                                label = { Text(cat.name, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Color(0xFF140526),
                                    containerColor = Color(0xFF1C0933),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                    OutlinedTextField(
                        value = editorName,
                        onValueChange = { editorName = it },
                        label = { Text("Package Name (e.g. 1,500 Coins)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editorSubtitle,
                        onValueChange = { editorSubtitle = it },
                        label = { Text("Subtitle (e.g. Special VIP Pack)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editorAmount,
                            onValueChange = { editorAmount = it },
                            label = { Text("Amount (e.g. 1500)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editorPrice,
                            onValueChange = { editorPrice = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Price BDT (৳)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = editorBadge,
                        onValueChange = { editorBadge = it },
                        label = { Text("Badge (Optional, e.g. Hot / VIP)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedPrice = editorPrice.toDoubleOrNull()
                        if (editorName.isBlank() || parsedPrice == null || parsedPrice <= 0) {
                            Toast.makeText(context, "প্যাকেজের নাম এবং সঠিক মূল্য দিন", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newPkg = TopupPackage(
                            id = editorPkgId,
                            category = editorCategory,
                            name = editorName.trim(),
                            subtitle = editorSubtitle.trim().ifBlank { "Special Pack" },
                            amount = editorAmount.trim().ifBlank { editorName.trim() },
                            priceBdt = parsedPrice,
                            badge = editorBadge.trim().takeIf { it.isNotBlank() },
                            isPopular = true
                        )
                        onAddOrUpdatePackage(newPkg)
                        showPackageEditor = false
                        Toast.makeText(context, "✅ নতুন প্যাকেজ যুক্ত ও ক্লাউডে সিঙ্ক হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("সংরক্ষণ করুন", color = Color(0xFF140526), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPackageEditor = false }) {
                    Text("বাতিল", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color(0xFF19062E)
        )
    }

    // Edit Price Dialog
    editingPackage?.let { pkg ->
        AlertDialog(
            onDismissRequest = { editingPackage = null },
            title = {
                Text(text = "দাম পরিবর্তন করুন: ${pkg.name}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Column {
                    Text(text = "সার্ভিস: ${pkg.category.displayName} (${pkg.subtitle})", fontSize = 12.sp, color = GoldLight)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPriceInput,
                        onValueChange = { newPriceInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("নতুন মূল্য (BDT)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = newPriceInput.toDoubleOrNull()
                        if (parsed != null && parsed > 0) {
                            onUpdatePrice(pkg.id, parsed)
                            Toast.makeText(context, "${pkg.name}-এর নতুন দাম ৳$parsed সেট হয়েছে!", Toast.LENGTH_SHORT).show()
                            editingPackage = null
                        } else {
                            Toast.makeText(context, "সঠিক দাম লিখুন", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(text = "সংরক্ষণ করুন", color = Color(0xFF140526), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingPackage = null }) {
                    Text(text = "বাতিল", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color(0xFF19062E)
        )
    }

    // Reset All Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "সব প্যাকেজের দাম রিসেট করবেন?", color = Color.White) },
            text = { Text(text = "এটি সমস্ত কাস্টম মূল্য মুছে দিয়ে ডিফল্ট মূল্যে ফিরিয়ে নেবে।", color = Color(0xFFD1D5DB)) },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAll()
                        showResetDialog = false
                        Toast.makeText(context, "সব প্যাকেজের দাম ডিফল্টে ফিরিয়ে নেওয়া হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text(text = "হ্যাঁ, রিসেট করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text(text = "বাতিল", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color(0xFF19062E)
        )
    }
}

// -------------------------------------------------------------
// TAB 2: EXCHANGE RATES DYNAMIC MANAGEMENT
// -------------------------------------------------------------
@Composable
fun AdminExchangeRatesTab(
    config: AdminConfig,
    onSaveRates: (Double, Double, Double, Double, Double, Double, Double, Double) -> Unit
) {
    val context = LocalContext.current

    var epayBase by remember(config) { mutableStateOf(config.epayBaseRate.toString()) }
    var epay970 by remember(config) { mutableStateOf(config.epayRate970Fixed.toString()) }
    var epay10 by remember(config) { mutableStateOf(config.epayRate10Fixed.toString()) }
    var epay30 by remember(config) { mutableStateOf(config.epayRate30Bulk.toString()) }
    var epay500 by remember(config) { mutableStateOf(config.epayRate500Wholesale.toString()) }
    var inrBase by remember(config) { mutableStateOf(config.inrBaseRate.toString()) }
    var tiktokRate by remember(config) { mutableStateOf(config.tiktokRatePerCoin.toString()) }
    var chametRate by remember(config) { mutableStateOf(config.chametRatePerDiamond.toString()) }

    // Live Test Calculator
    var testUsdInput by remember { mutableStateOf("100") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(text = "💱 এক্সচেঞ্জ ও কারেন্সি রেট কন্ট্রোল", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(text = "ডলার এক্সচেঞ্জ এবং বিভিন্ন কয়েন রেট লাইভ কনফিগার করুন", fontSize = 11.sp, color = GoldLight)
        }

        // EPAY Rates Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF16062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "💸 EPAY Dollar Rates (BDT)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = epayBase,
                            onValueChange = { epayBase = it },
                            label = { Text("Base Rate (৳/USD)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = epay970,
                            onValueChange = { epay970 = it },
                            label = { Text("9.70 USD Fix (৳)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = epay10,
                            onValueChange = { epay10 = it },
                            label = { Text("10.00 USD Fix (৳)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = epay30,
                            onValueChange = { epay30 = it },
                            label = { Text("30+ Bulk (৳/USD)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    OutlinedTextField(
                        value = epay500,
                        onValueChange = { epay500 = it },
                        label = { Text("500+ Wholesale (৳/USD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // India Exchange & Custom Rates Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF16062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "🇮🇳 BD ➔ India & Custom Coins Rates", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)

                    OutlinedTextField(
                        value = inrBase,
                        onValueChange = { inrBase = it },
                        label = { Text("BD to INR Rate (৳ per 1 INR)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = tiktokRate,
                            onValueChange = { tiktokRate = it },
                            label = { Text("TikTok (৳ / Coin)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = chametRate,
                            onValueChange = { chametRate = it },
                            label = { Text("Chamet (৳ / Diamond)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val epBase = epayBase.toDoubleOrNull() ?: config.epayBaseRate
                    val ep97 = epay970.toDoubleOrNull() ?: config.epayRate970Fixed
                    val ep10Val = epay10.toDoubleOrNull() ?: config.epayRate10Fixed
                    val ep30Val = epay30.toDoubleOrNull() ?: config.epayRate30Bulk
                    val ep500Val = epay500.toDoubleOrNull() ?: config.epayRate500Wholesale
                    val inrVal = inrBase.toDoubleOrNull() ?: config.inrBaseRate
                    val ttVal = tiktokRate.toDoubleOrNull() ?: config.tiktokRatePerCoin
                    val chVal = chametRate.toDoubleOrNull() ?: config.chametRatePerDiamond

                    onSaveRates(epBase, ep97, ep10Val, ep30Val, ep500Val, inrVal, ttVal, chVal)
                    Toast.makeText(context, "✅ এক্সচেঞ্জ রেট সফলভাবে সেভ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text(text = "💾 এক্সচেঞ্জ রেট সংরক্ষণ করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF140526))
            }
        }

        // Live Rate Tester
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F041C),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "🧪 রেট ক্যালকুলেশন প্রিভিউ (Live Tester)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = testUsdInput,
                        onValueChange = { testUsdInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("USD Amount Test") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    val parsedUsd = testUsdInput.toDoubleOrNull() ?: 0.0
                    val currentBase = epayBase.toDoubleOrNull() ?: 121.0
                    val current30 = epay30.toDoubleOrNull() ?: 122.0
                    val current500 = epay500.toDoubleOrNull() ?: 123.0
                    val appliedRate = when {
                        parsedUsd in 9.69..9.71 -> (epay970.toDoubleOrNull() ?: 1170.0) / 9.70
                        parsedUsd in 9.99..10.01 -> (epay10.toDoubleOrNull() ?: 1200.0) / 10.0
                        parsedUsd < 30.0 -> currentBase
                        parsedUsd < 500.0 -> current30
                        else -> current500
                    }
                    val estBdt = if (parsedUsd in 9.69..9.71) (epay970.toDoubleOrNull() ?: 1170.0)
                        else if (parsedUsd in 9.99..10.01) (epay10.toDoubleOrNull() ?: 1200.0)
                        else parsedUsd * appliedRate

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Estimated BDT:", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                        Text(text = "৳${String.format(Locale.US, "%,.1f", estBdt)} BDT", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: ORDERS MANAGEMENT (WITH CATEGORY PAGES: ALL, TIKTOK, CHAMET, ADD MONEY, ETC.)
// -------------------------------------------------------------
private fun matchesOrderCategoryTab(order: OrderRecord, tabKey: String): Boolean {
    val cat = order.category.lowercase()
    val item = order.itemName.lowercase()
    val num = order.orderNumber.uppercase()
    val isAddMoney = cat.contains("wallet") || cat.contains("add money") || item.contains("add money") || item.contains("টাকা এড") || num.startsWith("WAL-")
    return when (tabKey) {
        "ALL" -> true
        "ADD_MONEY" -> isAddMoney
        "TIKTOK" -> !isAddMoney && (cat.contains("tiktok") || item.contains("tiktok") || item.contains("coin"))
        "CHAMET" -> !isAddMoney && (cat.contains("chamet") || item.contains("chamet"))
        "FREEFIRE" -> !isAddMoney && (cat.contains("free") || cat.contains("gaming") || item.contains("free fire"))
        "EPAY" -> !isAddMoney && (cat.contains("epay") || num.startsWith("EPAY-"))
        "EXCHANGE" -> !isAddMoney && (cat.contains("exchange") || cat.contains("india") || num.startsWith("EXC-"))
        "SOCIAL_BOOST" -> !isAddMoney && (cat.contains("social") || cat.contains("boost") || num.startsWith("SB-"))
        else -> true
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<OrderRecord>,
    initialCategoryFilter: String = "ALL",
    onUpdateStatus: (OrderRecord, String) -> Unit,
    onDeleteOrder: (OrderRecord) -> Unit,
    onClearAll: () -> Unit,
    onSendSuccess: (OrderRecord) -> Unit,
    onSendCancel: (OrderRecord, String) -> Unit,
    onOpenLiveChat: (OrderRecord) -> Unit = {},
    onContactWhatsApp: (OrderRecord) -> Unit = {},
    onCopy: (String, String) -> Unit,
    onAutoFulfill: ((OrderRecord) -> Unit)? = null
) {
    val context = LocalContext.current
    var categoryTab by remember(initialCategoryFilter) { mutableStateOf(initialCategoryFilter) }
    var statusFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showClearConfirm by remember { mutableStateOf(false) }

    val categoryOptions = remember(orders) {
        listOf(
             Triple("ALL", "🌐 All (সব)", orders.size),
            Triple("TIKTOK", "🪙 TikTok", orders.count { matchesOrderCategoryTab(it, "TIKTOK") }),
            Triple("CHAMET", "💎 Chamet", orders.count { matchesOrderCategoryTab(it, "CHAMET") }),
            Triple("ADD_MONEY", "💳 Add Money", orders.count { matchesOrderCategoryTab(it, "ADD_MONEY") }),
            Triple("FREEFIRE", "🎮 Gaming", orders.count { matchesOrderCategoryTab(it, "FREEFIRE") }),
            Triple("EPAY", "💵 EPAY", orders.count { matchesOrderCategoryTab(it, "EPAY") }),
            Triple("EXCHANGE", "🇮🇳 India", orders.count { matchesOrderCategoryTab(it, "EXCHANGE") }),
            Triple("SOCIAL_BOOST", "🚀 Boost", orders.count { matchesOrderCategoryTab(it, "SOCIAL_BOOST") })
        )
    }

    val categoryFilteredOrders = remember(orders, categoryTab) {
        orders.filter { matchesOrderCategoryTab(it, categoryTab) }
    }

    val filteredOrders = remember(categoryFilteredOrders, statusFilter, searchQuery) {
        categoryFilteredOrders.filter { order ->
            (statusFilter == "All" || order.status.equals(statusFilter, ignoreCase = true)) &&
            (searchQuery.isBlank() || order.orderNumber.contains(searchQuery, ignoreCase = true) ||
             order.userAccountId.contains(searchQuery, ignoreCase = true) ||
             order.customerName.contains(searchQuery, ignoreCase = true) ||
             order.customerEmail.contains(searchQuery, ignoreCase = true) ||
             order.customerPhone.contains(searchQuery, ignoreCase = true) ||
             order.itemName.contains(searchQuery, ignoreCase = true) ||
             order.transactionTrxId.contains(searchQuery, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "📋 গ্রাহকদের অর্ডারসমূহ ও এড মানি পেজ", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(
                        text = "উপরের সার্ভিস বাটনে ক্লিক করে কে কোন সার্ভিস বা Add Money করেছে দেখুন",
                        fontSize = 11.sp,
                        color = GoldLight
                    )
                }
                if (orders.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { showClearConfirm = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.6f))
                    ) {
                        Text(text = "🗑️ সব মুছুন", fontSize = 10.5.sp)
                    }
                }
            }
        }

        // Top Service / Category Page Selector Buttons (All, TikTok, Chamet, Add Money, etc.)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryOptions.forEach { (key, label, count) ->
                    val isSelected = categoryTab == key
                    Surface(
                        onClick = { categoryTab = key },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) GoldPrimary else Color(0xFF1B0933),
                        border = BorderStroke(
                            1.2.dp,
                            if (isSelected) Color.White else GoldPrimary.copy(alpha = 0.45f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$label ($count)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color(0xFF140526) else Color.White
                            )
                        }
                    }
                }
            }
        }

        // Category Buyers Summary Card (Shows who bought in this category / Add Money)
        if (categoryTab != "ALL" && categoryFilteredOrders.isNotEmpty()) {
            item {
                val categoryTitle = categoryOptions.find { it.first == categoryTab }?.second ?: categoryTab
                val totalCategoryBdt = categoryFilteredOrders.sumOf { it.priceBdt }
                val pendingCategoryCount = categoryFilteredOrders.count { it.status == "Processing" }
                val buyersGroup = categoryFilteredOrders.groupBy {
                    it.customerEmail.ifBlank { it.customerPhone.ifBlank { it.userAccountId } }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1A0936),
                    border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📊 $categoryTitle — কে কে বাই করেছে (${buyersGroup.size} জন)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary
                            )
                            Text(
                                text = "মোট: ৳${String.format(Locale.US, "%,.0f", totalCategoryBdt)} (Pending: $pendingCategoryCount)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }

                        buyersGroup.entries.take(6).forEach { (buyerKey, buyerOrders) ->
                            val latestOrd = buyerOrders.first()
                            val buyerDisplayName = latestOrd.customerName.ifBlank {
                                latestOrd.customerEmail.substringBefore("@").ifBlank { latestOrd.userAccountId }
                            }
                            val buyerTotalSpent = buyerOrders.sumOf { it.priceBdt }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF110424),
                                border = BorderStroke(0.8.dp, Color(0xFF38155E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "👤 $buyerDisplayName (${buyerOrders.size} টি অর্ডার • ৳${String.format(Locale.US, "%,.0f", buyerTotalSpent)})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "UID/Info: ${latestOrd.userAccountId}" +
                                                if (latestOrd.customerEmail.isNotBlank()) " • ${latestOrd.customerEmail}" else "",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF93C5FD)
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            onClick = { onOpenLiveChat(latestOrd) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF0284C7),
                                            border = BorderStroke(0.8.dp, Color(0xFF7DD3FC))
                                        ) {
                                            Text(
                                                text = "💬 লাইভ চ্যাট",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                            )
                                        }
                                        Surface(
                                            onClick = { onContactWhatsApp(latestOrd) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = WhatsAppGreen,
                                            border = BorderStroke(0.8.dp, Color(0xFF86EFAC))
                                        ) {
                                            Text(
                                                text = "📱 WhatsApp",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
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

        // Search Orders
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Order ID, নাম, ইমেইল, UID বা TrxID খুঁজুন...", fontSize = 12.sp, color = Color(0xFF6B7280)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedContainerColor = Color(0xFF140526),
                    unfocusedContainerColor = Color(0xFF140526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Status Filter Chips
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Processing", "Completed", "Cancelled").forEach { status ->
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { statusFilter = status },
                        label = { Text(status, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = Color(0xFF140526),
                            containerColor = Color(0xFF1C0933),
                            labelColor = Color(0xFFD1D5DB)
                        )
                    )
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF140526)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📭", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "এই ক্যাটাগরিতে কোনো অর্ডার পাওয়া যায়নি", fontSize = 14.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(filteredOrders, key = { it.id }) { order ->
                AdminOrderItemCard(
                    order = order,
                    onUpdateStatus = { newStatus -> onUpdateStatus(order, newStatus) },
                    onDelete = { onDeleteOrder(order) },
                    onSendSuccess = { onSendSuccess(order) },
                    onSendCancel = { reason -> onSendCancel(order, reason) },
                    onOpenLiveChat = { onOpenLiveChat(order) },
                    onContactWhatsApp = { onContactWhatsApp(order) },
                    onCopy = onCopy,
                    onAutoFulfill = onAutoFulfill?.let { { it(order) } }
                )
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(text = "সব অর্ডার ইতিহাস মুছে ফেলবেন?", color = Color.White) },
            text = { Text(text = "এটি ডাটাবেস থেকে সমস্ত অর্ডার স্থায়ীভাবে ডিলিট করবে।", color = Color(0xFFD1D5DB)) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearConfirm = false
                        Toast.makeText(context, "সমস্ত অর্ডার মুছে ফেলা হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text(text = "ডিলিট করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirm = false }) {
                    Text(text = "বাতিল", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color(0xFF19062E)
        )
    }
}

@Composable
fun AdminOrderItemCard(
    order: OrderRecord,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit,
    onSendSuccess: () -> Unit,
    onSendCancel: (String) -> Unit,
    onOpenLiveChat: () -> Unit = {},
    onContactWhatsApp: () -> Unit = {},
    onCopy: (String, String) -> Unit,
    onAutoFulfill: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateFormatted = remember(order.timestamp) { sdf.format(Date(order.timestamp)) }

    val statusColor = when (order.status) {
        "Completed" -> SuccessGreen
        "Cancelled" -> DangerRed
        else -> WarningYellow
    }

    val statusBadgeLabel = when (order.status) {
        "Completed" -> "✅ Approved"
        "Cancelled" -> "❌ ভুল TrxID"
        else -> "⏳ Processing"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF17062B),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, statusColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "#${order.orderNumber}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = { onCopy("Order Ref", order.orderNumber) }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Ref", tint = GoldLight, modifier = Modifier.size(12.dp))
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, statusColor)
                ) {
                    Text(
                        text = statusBadgeLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "${order.category}: ${order.itemName}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "মূল্য: ৳${String.format(Locale.US, "%,.0f", order.priceBdt)} • $dateFormatted", fontSize = 12.sp, color = GoldLight)

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F041C),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "UID/Account: ${order.userAccountId}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        IconButton(onClick = { onCopy("Player UID", order.userAccountId) }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy UID", tint = GoldPrimary, modifier = Modifier.size(12.dp))
                        }
                    }
                    if (order.transactionTrxId.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TrxID / EPAY No: ${order.transactionTrxId}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldLight,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onCopy("Transaction / Order ID", order.transactionTrxId) }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy TrxID", tint = GoldLight, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                    if (order.customerName.isNotBlank() || order.customerEmail.isNotBlank() || order.customerPhone.isNotBlank()) {
                        Text(
                            text = "👤 কাস্টমার: ${order.customerName.ifBlank { "গ্রাহক" }}" +
                                (if (order.customerEmail.isNotBlank()) " • ${order.customerEmail}" else "") +
                                (if (order.customerPhone.isNotBlank()) " • 📱 ${order.customerPhone}" else ""),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                    Text(text = "পেমেন্ট মেথড: ${order.paymentMethod}", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3-Button Admin Order Status Control (Approve / Processing / ভুল TrxID) -> Sends Notification to Top Bell
            Text(
                text = "🔔 স্ট্যাটাস পরিবর্তন করুন (টপ নোটিফিকেশনে মেসেজ যাবে):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldLight
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Approve Button
                val isApproved = order.status == "Completed"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onUpdateStatus("Completed")
                            Toast.makeText(context, "✅ অর্ডার #${order.orderNumber} Approved করা হয়েছে ও নোটিফিকেশন পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isApproved) SuccessGreen else SuccessGreen.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, SuccessGreen)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "✅ Approve",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isApproved) Color.White else SuccessGreen
                        )
                    }
                }

                // 2. Processing Button
                val isProcessing = order.status == "Processing"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onUpdateStatus("Processing")
                            Toast.makeText(context, "⏳ অর্ডার #${order.orderNumber} Processing স্ট্যাটাসে রাখা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isProcessing) WarningYellow else WarningYellow.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, WarningYellow)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "⏳ Processing",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isProcessing) Color(0xFF130724) else WarningYellow
                        )
                    }
                }

                // 3. Wrong TrxID Button
                val isWrongTrx = order.status == "Cancelled"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onUpdateStatus("Cancelled")
                            Toast.makeText(context, "❌ অর্ডার #${order.orderNumber} ভুল TrxID হিসেবে চিহ্নিত ও নোটিফিকেশন পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isWrongTrx) DangerRed else DangerRed.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, DangerRed)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "❌ ভুল TrxID",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isWrongTrx) Color.White else DangerRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Customer Communication Row: Live Support Chat + WhatsApp + Auto Fulfill + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Direct Live Support Chat Button (Opens customer's chat thread in Admin Panel)
                Button(
                    onClick = onOpenLiveChat,
                    modifier = Modifier
                        .weight(1.15f)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "💬 লাইভ সাপোর্ট",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // 2. Direct WhatsApp Button
                PremiumWhatsAppButton(
                    text = "WhatsApp",
                    onClick = onContactWhatsApp,
                    modifier = Modifier.weight(1.15f),
                    height = 40.dp,
                    cornerRadius = 10.dp,
                    fontSize = 11.5.sp,
                    iconSize = 17.dp
                )

                if (order.status != "Completed" && onAutoFulfill != null) {
                    Button(
                        onClick = { onAutoFulfill() },
                        modifier = Modifier
                            .weight(0.95f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(text = "⚡ অটো", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2C0F4A), RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: BANNER & ADS CAROUSEL MANAGER (5-SECOND ROTATING BANNERS)
// -------------------------------------------------------------
@Composable
fun AdminBannersTab(
    config: AdminConfig,
    onSave: (AdminConfig) -> Unit
) {
    val context = LocalContext.current
    var bannersList by remember(config) { mutableStateOf(config.promoBanners) }

    // Dialog State for Add/Edit
    var showEditDialog by remember { mutableStateOf(false) }
    var editingBanner by remember { mutableStateOf<PromoBanner?>(null) }

    var bannerTitle by remember { mutableStateOf("") }
    var bannerSubtitle by remember { mutableStateOf("") }
    var bannerImageUrl by remember { mutableStateOf("") }
    var bannerBadge by remember { mutableStateOf("স্পেশাল অফার") }
    var bannerActionUrl by remember { mutableStateOf("") }
    var bannerTargetService by remember { mutableStateOf("TIKTOK") }
    var bannerGradientStart by remember { mutableStateOf("#FF1F1641") }
    var bannerGradientEnd by remember { mutableStateOf("#FF8B104E") }

    fun openDialog(banner: PromoBanner?) {
        editingBanner = banner
        if (banner != null) {
            bannerTitle = banner.title
            bannerSubtitle = banner.subtitle
            bannerImageUrl = banner.imageUrl
            bannerBadge = banner.badge
            bannerActionUrl = banner.actionUrl
            bannerTargetService = banner.serviceTarget
            bannerGradientStart = banner.gradientStartHex
            bannerGradientEnd = banner.gradientEndHex
        } else {
            bannerTitle = ""
            bannerSubtitle = ""
            bannerImageUrl = ""
            bannerBadge = "🔥 অফার"
            bannerActionUrl = ""
            bannerTargetService = "TIKTOK"
            bannerGradientStart = "#FF1F1641"
            bannerGradientEnd = "#FF8B104E"
        }
        showEditDialog = true
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkCard),
                border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(GoldPrimary, NeonMagenta)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎨 হোম স্ক্রিন ব্যানার ক্যারোসেল",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "অ্যাপের উপরে ৫ সেকেন্ড পর পর ব্যানার পরিবর্তিত হয়। এখানে আনলিমিটেড ব্যানার যোগ, ছবি, ইউটিউব/টিকটক ভিডিও লিঙ্ক বা Ads লিঙ্ক বসাতে পারবেন।",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 16.sp
                            )
                        }

                        Button(
                            onClick = { openDialog(null) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF1B072B), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "নতুন ব্যানার", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B072B))
                        }
                    }
                }
            }
        }

        // List of Active Banners
        if (bannersList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোনো ব্যানার নেই। 'নতুন ব্যানার' বাটনে ক্লিক করে ব্যানার যুক্ত করুন।",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(bannersList, key = { it.id }) { banner ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A2E)),
                    border = BorderStroke(1.dp, if (banner.isEnabled) GoldPrimary.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldPrimary.copy(alpha = 0.2f),
                                border = BorderStroke(0.8.dp, GoldPrimary)
                            ) {
                                Text(
                                    text = banner.badge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (banner.isEnabled) "সক্রিয়" else "নিষ্ক্রিয়",
                                    fontSize = 11.sp,
                                    color = if (banner.isEnabled) SuccessGreen else DangerRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = banner.isEnabled,
                                    onCheckedChange = { checked ->
                                        val updated = bannersList.map {
                                            if (it.id == banner.id) it.copy(isEnabled = checked) else it
                                        }
                                        bannersList = updated
                                        onSave(config.copy(promoBanners = updated))
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = GoldPrimary,
                                        checkedTrackColor = NeonPurple
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = banner.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = banner.subtitle,
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            maxLines = 2
                        )

                        if (banner.imageUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🖼️ ছবির লিঙ্ক: ${banner.imageUrl}",
                                fontSize = 11.sp,
                                color = Color(0xFF60A5FA),
                                maxLines = 1
                            )
                        }

                        if (banner.actionUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🔗 ভিডিও / Ads লিঙ্ক: ${banner.actionUrl}",
                                fontSize = 11.sp,
                                color = SuccessGreen,
                                maxLines = 1
                            )
                        } else {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🎯 ট্যাপ অ্যাকশন: ${banner.serviceTarget} ট্যাব ওপেন হবে",
                                fontSize = 11.sp,
                                color = GoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Edit & Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { openDialog(banner) },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GoldPrimary)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("এডিট করুন", fontSize = 11.5.sp, color = GoldPrimary)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    val updated = bannersList.filter { it.id != banner.id }
                                    bannersList = updated
                                    onSave(config.copy(promoBanners = updated))
                                    Toast.makeText(context, "ব্যানার মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF2E0C25), RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Banner Dialog
    if (showEditDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showEditDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                color = Color(0xFF17042B),
                border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(GoldPrimary, NeonMagenta)))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = if (editingBanner != null) "✏️ ব্যানার পরিবর্তন করুন" else "➕ নতুন ব্যানার তৈরি করুন",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Text(
                            text = "ব্যানারে ছবি বা চমৎকার গ্রেডিয়েন্ট এবং ইউটিউব/টিকটক বা বিজ্ঞাপনের লিংক দিতে পারবেন।",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = bannerTitle,
                            onValueChange = { bannerTitle = it },
                            label = { Text("ব্যানার টাইটেল (Title)") },
                            placeholder = { Text("e.g. 🪙 TikTok Coins Instant Recharge") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF4A1E75),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = bannerSubtitle,
                            onValueChange = { bannerSubtitle = it },
                            label = { Text("সাবটাইটেল / বর্ণনা (Subtitle)") },
                            placeholder = { Text("e.g. ১০০ কয়েন থেকে আনলিমিটেড রিচার্জ করুন সবচেয়ে কম রেটে") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF4A1E75),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = bannerBadge,
                            onValueChange = { bannerBadge = it },
                            label = { Text("টপ ব্যাজ (Badge Text)") },
                            placeholder = { Text("e.g. 🔥 ধামাকা অফার / ⚡ ফাস্ট ডেলিভারি") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF4A1E75),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = bannerImageUrl,
                            onValueChange = { bannerImageUrl = it },
                            label = { Text("ছবির ওয়েব লিঙ্ক (Image URL) - ঐচ্ছিক") },
                            placeholder = { Text("https://example.com/banner.jpg (না দিলে অটো গ্রেডিয়েন্ট হবে)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF4A1E75),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = bannerActionUrl,
                            onValueChange = { bannerActionUrl = it },
                            label = { Text("ভিডিও বা Ads লিঙ্ক (Action URL) - ঐচ্ছিক") },
                            placeholder = { Text("https://youtu.be/... বা TikTok লিঙ্ক বা বিজ্ঞাপনের লিংক") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SuccessGreen,
                                unfocusedBorderColor = Color(0xFF4A1E75),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Text(
                            text = "💡 এই লিঙ্কটি দিলে ব্যানারে ক্লিক করলে সরাসরি ইউটিউব/টিকটক বা ওয়েবসাইটে চলে যাবে। খালি রাখলে নিচের সার্ভিস সিলেক্ট অনুযায়ী অ্যাপের ভিতরে যাবে।",
                            fontSize = 10.5.sp,
                            color = Color.LightGray
                        )
                    }

                    item {
                        Text(
                            text = "অভ্যন্তরীণ সার্ভিস (যদি লিঙ্ক খালি থাকে):",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("TIKTOK", "CHAMET", "FREEFIRE", "EPAY", "EXCHANGE").forEach { srv ->
                                val isSel = bannerTargetService == srv
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { bannerTargetService = srv },
                                    color = if (isSel) GoldPrimary else Color(0xFF2B1046),
                                    border = BorderStroke(0.8.dp, if (isSel) GoldLight else Color.Transparent),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = srv,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color(0xFF1E072B) else Color.White,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showEditDialog = false }) {
                                Text("বাতিল", color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (bannerTitle.isBlank()) {
                                        Toast.makeText(context, "দয়া করে ব্যানারের টাইটেল লিখুন", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    val bannerId = editingBanner?.id ?: "b_${System.currentTimeMillis()}"
                                    val newBanner = PromoBanner(
                                        id = bannerId,
                                        title = bannerTitle.trim(),
                                        subtitle = bannerSubtitle.trim(),
                                        imageUrl = bannerImageUrl.trim(),
                                        badge = bannerBadge.trim().ifBlank { "PROMO" },
                                        actionUrl = bannerActionUrl.trim(),
                                        serviceTarget = bannerTargetService,
                                        gradientStartHex = bannerGradientStart,
                                        gradientEndHex = bannerGradientEnd,
                                        isEnabled = editingBanner?.isEnabled ?: true
                                    )

                                    val updated = if (editingBanner != null) {
                                        bannersList.map { if (it.id == editingBanner?.id) newBanner else it }
                                    } else {
                                        bannersList + newBanner
                                    }

                                    bannersList = updated
                                    onSave(config.copy(promoBanners = updated))
                                    showEditDialog = false
                                    Toast.makeText(context, "✅ ব্যানার সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                            ) {
                                Text(
                                    text = if (editingBanner != null) "আপডেট করুন" else "সংরক্ষণ করুন",
                                    color = Color(0xFF1E072B),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: AUTO-DELIVERY (TIKTOK COINS & CHAMET DIAMONDS API ENGINE)
// -------------------------------------------------------------
@Composable
fun AdminAutoDeliveryTab(
    config: AdminConfig,
    onSave: (AdminConfig) -> Unit,
    onTestApi: ((String, String, (Boolean, String) -> Unit) -> Unit)? = null
) {
    val context = LocalContext.current
    var isAutoTopupEnabled by remember(config) { mutableStateOf(config.isAutoTopupEnabled) }
    var autoInstantWalletEnabled by remember(config) { mutableStateOf(config.autoInstantWalletEnabled) }
    var tiktokApiUrl by remember(config) { mutableStateOf(config.tiktokApiUrl) }
    var tiktokApiKey by remember(config) { mutableStateOf(config.tiktokApiKey) }
    var chametApiUrl by remember(config) { mutableStateOf(config.chametApiUrl) }
    var chametApiKey by remember(config) { mutableStateOf(config.chametApiKey) }

    var testStatusTikTok by remember { mutableStateOf<String?>(null) }
    var testStatusChamet by remember { mutableStateOf<String?>(null) }
    var isTestingTikTok by remember { mutableStateOf(false) }
    var isTestingChamet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤖", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "অটোমেটিক ডেলিভারি কন্ট্রোল",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "টিকটক কয়েন ও চ্যামেট ডায়মন্ড সম্পূর্ণ স্বয়ংক্রিয়ভাবে পাঠানোর জন্য এই ইঞ্জিনটি কাজ করে। গ্রাহক অর্ডার করার সাথে সাথে মানুষের হস্তক্ষেপ ছাড়াই অটোমেটিক রিচার্জ সম্পন্ন হয়ে যাবে।",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Master Automation Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkCard),
                border = BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(text = "⚙️ অটোমেশন সুইচসমূহ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "অটো-টপআপ ইঞ্জিন সক্রিয়", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text(text = "চালু রাখলে অর্ডার সাথে সাথে অটো প্রসেস হবে", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isAutoTopupEnabled,
                            onCheckedChange = { isAutoTopupEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = GoldDark)
                        )
                    }

                    HorizontalDivider(color = Color(0xFF38155E))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "ইনস্ট্যান্ট ওয়ালেট অটো রিচার্জ", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text(text = "ইউজার ব্যালেন্স দিয়ে পে করলে ০ সেকেন্ডে সরাসরি একাউন্টে চলে যাবে", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = autoInstantWalletEnabled,
                            onCheckedChange = { autoInstantWalletEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen, checkedTrackColor = Color(0xFF065F46))
                        )
                    }
                }
            }
        }

        // TikTok Reseller API Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkCard),
                border = BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "TikTok Coins Reseller API কনফিগারেশন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text(
                        text = "আপনার ডায়মন্ড/কয়েন প্রোভাইডার (যেমন: Smile.One, UniPin, বা নিজস্ব সার্ভার) এর API Endpoint ও Key দিন। খালি রাখলে ইন্টারনাল অটোমেশন সিস্টেমে ডেলিভারি হবে।",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    OutlinedTextField(
                        value = tiktokApiUrl,
                        onValueChange = { tiktokApiUrl = it },
                        label = { Text("TikTok API Endpoint URL") },
                        placeholder = { Text("e.g. https://api.supplier.com/v1/tiktok/recharge") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = tiktokApiKey,
                        onValueChange = { tiktokApiKey = it },
                        label = { Text("TikTok Secret API Key / Bearer Token") },
                        placeholder = { Text("e.g. secret_key_abc123...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (tiktokApiUrl.isBlank()) {
                                    Toast.makeText(context, "দয়া করে প্রথমে API URL লিখুন", Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }
                                isTestingTikTok = true
                                testStatusTikTok = "টেস্টিং হচ্ছে..."
                                onTestApi?.invoke(tiktokApiUrl.trim(), tiktokApiKey.trim()) { ok, msg ->
                                    isTestingTikTok = false
                                    testStatusTikTok = if (ok) "✅ $msg" else "❌ $msg"
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, GoldPrimary),
                            enabled = !isTestingTikTok
                        ) {
                            Text(text = if (isTestingTikTok) "চেক হচ্ছে..." else "🔌 TikTok API টেস্ট করুন", fontSize = 11.sp, color = GoldPrimary)
                        }

                        testStatusTikTok?.let {
                            Text(text = it, fontSize = 11.sp, color = if (it.startsWith("✅")) SuccessGreen else DangerRed)
                        }
                    }
                }
            }
        }

        // Chamet Reseller API Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkCard),
                border = BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💎", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Chamet Diamonds Reseller API কনফিগারেশন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text(
                        text = "চ্যামেট ডায়মন্ড প্রোভাইডারের রিচার্জ API Endpoint ও Secret Key দিন।",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    OutlinedTextField(
                        value = chametApiUrl,
                        onValueChange = { chametApiUrl = it },
                        label = { Text("Chamet API Endpoint URL") },
                        placeholder = { Text("e.g. https://api.supplier.com/v1/chamet/recharge") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMagenta,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = chametApiKey,
                        onValueChange = { chametApiKey = it },
                        label = { Text("Chamet Secret API Key / Bearer Token") },
                        placeholder = { Text("e.g. chamet_token_xyz987...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMagenta,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (chametApiUrl.isBlank()) {
                                    Toast.makeText(context, "দয়া করে প্রথমে API URL লিখুন", Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }
                                isTestingChamet = true
                                testStatusChamet = "টেস্টিং হচ্ছে..."
                                onTestApi?.invoke(chametApiUrl.trim(), chametApiKey.trim()) { ok, msg ->
                                    isTestingChamet = false
                                    testStatusChamet = if (ok) "✅ $msg" else "❌ $msg"
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, NeonMagenta),
                            enabled = !isTestingChamet
                        ) {
                            Text(text = if (isTestingChamet) "চেক হচ্ছে..." else "🔌 Chamet API টেস্ট করুন", fontSize = 11.sp, color = NeonMagenta)
                        }

                        testStatusChamet?.let {
                            Text(text = it, fontSize = 11.sp, color = if (it.startsWith("✅")) SuccessGreen else DangerRed)
                        }
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    onSave(
                        config.copy(
                            isAutoTopupEnabled = isAutoTopupEnabled,
                            autoInstantWalletEnabled = autoInstantWalletEnabled,
                            tiktokApiUrl = tiktokApiUrl.trim(),
                            tiktokApiKey = tiktokApiKey.trim(),
                            chametApiUrl = chametApiUrl.trim(),
                            chametApiKey = chametApiKey.trim()
                        )
                    )
                    Toast.makeText(context, "✅ অটোমেশন সেটিংস সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text(
                    text = "💾 অটোমেশন সেটিংস সংরক্ষণ করুন",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF140526)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: PAYMENT ACCOUNTS TAB
// -------------------------------------------------------------
@Composable
fun AdminPaymentTab(
    config: AdminConfig,
    onSave: (AdminConfig) -> Unit
) {
    val context = LocalContext.current
    var bkashNum by remember(config) { mutableStateOf(config.bkashNumber) }
    var bkashIns by remember(config) { mutableStateOf(config.bkashInstruction) }
    var nagadNum by remember(config) { mutableStateOf(config.nagadNumber) }
    var nagadIns by remember(config) { mutableStateOf(config.nagadInstruction) }
    var rocketNum by remember(config) { mutableStateOf(config.rocketNumber) }
    var rocketEnabled by remember(config) { mutableStateOf(config.isRocketEnabled) }
    var epayEmailInput by remember(config) { mutableStateOf(config.epayEmail) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(text = "💳 পেমেন্ট অ্যাকাউন্ট কনফিগারেশন", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(text = "গ্রাহকরা এই নাম্বারগুলোতে পেমেন্ট পাঠাবে", fontSize = 11.sp, color = GoldLight)
        }

        // bKash Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF17062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD8226A))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🌸 bKash Account", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF528E))
                    OutlinedTextField(
                        value = bkashNum,
                        onValueChange = { bkashNum = it },
                        label = { Text("bKash Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF528E),
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = bkashIns,
                        onValueChange = { bkashIns = it },
                        label = { Text("Instruction") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF528E),
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // Nagad Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF17062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF7941D))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🔶 Nagad Account", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFA940))
                    OutlinedTextField(
                        value = nagadNum,
                        onValueChange = { nagadNum = it },
                        label = { Text("Nagad Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFA940),
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = nagadIns,
                        onValueChange = { nagadIns = it },
                        label = { Text("Instruction") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFA940),
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // Rocket Switch & Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF17062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8C2686))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🟣 Rocket Account", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD56BF0))
                        Switch(
                            checked = rocketEnabled,
                            onCheckedChange = { rocketEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = Color(0xFF8C2686))
                        )
                    }
                    if (rocketEnabled) {
                        OutlinedTextField(
                            value = rocketNum,
                            onValueChange = { rocketNum = it },
                            label = { Text("Rocket Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD56BF0),
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // EPAY Global Wallet Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF17062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0073BC))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🌐 EPAY Global Receiver Email", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    OutlinedTextField(
                        value = epayEmailInput,
                        onValueChange = { epayEmailInput = it },
                        label = { Text("EPAY Gmail Address") },
                        placeholder = { Text("johirulagency01833@gmail.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    onSave(
                        config.copy(
                            bkashNumber = bkashNum.trim(),
                            bkashInstruction = bkashIns.trim(),
                            nagadNumber = nagadNum.trim(),
                            nagadInstruction = nagadIns.trim(),
                            rocketNumber = rocketNum.trim(),
                            isRocketEnabled = rocketEnabled,
                            epayEmail = epayEmailInput.trim()
                        )
                    )
                    Toast.makeText(context, "✅ পেমেন্ট ও EPAY ইমেইল সফলভাবে সংরক্ষণ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text(text = "💾 পেমেন্ট তথ্য সংরক্ষণ করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF140526))
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: NOTICE CONTROL
// -------------------------------------------------------------
@Composable
fun AdminNoticeTab(
    config: AdminConfig,
    onSave: (AdminConfig) -> Unit
) {
    val context = LocalContext.current
    var title by remember(config) { mutableStateOf(config.noticeTitle) }
    var rule1 by remember(config) { mutableStateOf(config.noticeRule1) }
    var rule2 by remember(config) { mutableStateOf(config.noticeRule2) }
    var rule3 by remember(config) { mutableStateOf(config.noticeRule3) }
    var warning by remember(config) { mutableStateOf(config.noticeWarning) }
    var alert by remember(config) { mutableStateOf(config.noticeAlert) }
    var showOnLaunch by remember(config) { mutableStateOf(config.noticeShowOnLaunch) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "📢 পপআপ নোটিশ ও নিয়মাবলী", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(text = "অ্যাপ ওপেন করার পর গ্রাহকরা এই নোটিশ দেখতে পাবে", fontSize = 11.sp, color = GoldLight)
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF17062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "অ্যাপ চালুর সময় নোটিশ দেখান", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Switch(
                        checked = showOnLaunch,
                        onCheckedChange = { showOnLaunch = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = Color(0xFF531E85))
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Notice Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = rule1,
                onValueChange = { rule1 = it },
                label = { Text("Rule 1 (India Transfer Rule)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = rule2,
                onValueChange = { rule2 = it },
                label = { Text("Rule 2 (EPAY Dollar Info)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = rule3,
                onValueChange = { rule3 = it },
                label = { Text("Rule 3 (bKash/Nagad Info)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = warning,
                onValueChange = { warning = it },
                label = { Text("Warning Note") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = alert,
                onValueChange = { alert = it },
                label = { Text("Security Alert (PIN/OTP Warning)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DangerRed,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        item {
            Button(
                onClick = {
                    onSave(
                        config.copy(
                            noticeTitle = title.trim(),
                            noticeRule1 = rule1.trim(),
                            noticeRule2 = rule2.trim(),
                            noticeRule3 = rule3.trim(),
                            noticeWarning = warning.trim(),
                            noticeAlert = alert.trim(),
                            noticeShowOnLaunch = showOnLaunch
                        )
                    )
                    Toast.makeText(context, "✅ নোটিশ সফলভাবে আপডেট করা হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text(text = "💾 নোটিশ সংরক্ষণ করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF140526))
            }
        }
    }
}

// -------------------------------------------------------------
// TAB: CUSTOMERS MANAGEMENT (FIRESTORE + ORDER AGGREGATION)
// -------------------------------------------------------------
@Composable
fun AdminCustomersTab(
    customers: List<CustomerProfile>,
    adminConfig: AdminConfig = AdminConfig(),
    onSaveConfig: (AdminConfig) -> Unit = {},
    onUpdateCustomer: (accountId: String, displayName: String, isVip: Boolean, isBlocked: Boolean, adminNote: String) -> Unit,
    onAdjustCustomerWallet: (accountIdOrEmail: String, amountDelta: Double) -> Unit = { _, _ -> },
    onSyncCustomers: () -> Unit = {},
    onAddCustomerByAdmin: (email: String, displayName: String, phone: String) -> Unit = { _, _, _ -> },
    onOpenCustomerLiveChat: (CustomerProfile) -> Unit = {},
    onContactWhatsApp: (String) -> Unit,
    onCopy: (String, String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("All") } // All, VIP, Blocked
    var editingCustomer by remember { mutableStateOf<CustomerProfile?>(null) }
    var editName by remember { mutableStateOf("") }
    var editNote by remember { mutableStateOf("") }
    var editVip by remember { mutableStateOf(false) }
    var editBlocked by remember { mutableStateOf(false) }
    var walletDeltaInput by remember { mutableStateOf("") }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var newMemberEmail by remember { mutableStateOf("") }
    var newMemberName by remember { mutableStateOf("") }
    var newMemberPhone by remember { mutableStateOf("") }

    // Referral & Wallet Rule Inputs (Editable directly here as well as in Settings)
    var refEnabled by remember(adminConfig) { mutableStateOf(adminConfig.isReferralEnabled) }
    var refMode by remember(adminConfig) { mutableStateOf(adminConfig.referralCommissionMode.ifBlank { "FIXED" }) }
    var refBaseBdtInput by remember(adminConfig) { mutableStateOf(String.format(Locale.US, "%.0f", adminConfig.referralBaseOrderAmountBdt)) }
    var refCommBdtInput by remember(adminConfig) { mutableStateOf(String.format(Locale.US, "%.0f", adminConfig.referralCommissionBdt)) }
    var refCommPercentInput by remember(adminConfig) { mutableStateOf(String.format(Locale.US, "%.1f", adminConfig.referralCommissionPercent)) }
    var refMinOrderInput by remember(adminConfig) { mutableStateOf(String.format(Locale.US, "%.0f", adminConfig.referralMinOrderAmountBdt)) }
    var refMaxCommInput by remember(adminConfig) { mutableStateOf(String.format(Locale.US, "%.0f", adminConfig.referralMaxCommissionBdt)) }
    var refNewUserBonusInput by remember(adminConfig) { mutableStateOf(String.format(Locale.US, "%.0f", adminConfig.referralNewUserBonusBdt)) }
    var minAddMoneyInput by remember(adminConfig) { mutableStateOf(String.format(Locale.US, "%.0f", adminConfig.minWalletAddMoneyBdt)) }
    var refRulesInput by remember(adminConfig) { mutableStateOf(adminConfig.referralRulesText) }
    var eligibleServicesSet by remember(adminConfig) { mutableStateOf(adminConfig.referralEligibleServices) }

    val filteredCustomers = remember(customers, searchQuery, filterMode) {
        customers.filter { c ->
            val matchesFilter = when (filterMode) {
                "VIP" -> c.isVip
                "Blocked" -> c.isBlocked
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                c.accountId.contains(searchQuery, ignoreCase = true) ||
                c.email.contains(searchQuery, ignoreCase = true) ||
                c.phone.contains(searchQuery, ignoreCase = true) ||
                c.displayName.contains(searchQuery, ignoreCase = true) ||
                c.lastCategory.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "👥 কাস্টমার ও মেম্বার একাউন্ট (${customers.size})", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(text = "Firebase Auth মেম্বার, প্রোফাইল ব্যালেন্স ও রেফারেল কন্ট্রোল", fontSize = 11.sp, color = GoldLight)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onSyncCustomers,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, GoldPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🔄 সিঙ্ক", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }
                    Button(
                        onClick = { showAddMemberDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "+ মেম্বার যুক্ত", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF140526))
                    }
                }
            }
        }

        // Referral & Wallet Rules Control Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A0933),
                border = BorderStroke(1.2.dp, GoldPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎁 রেফারেল ও প্রোফাইল ওয়ালেট সেটিংস",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary
                            )
                            Text(
                                text = "কোনো ইউজার রেফার করলে কত টাকার সার্ভিসে কত টাকা কমিশন পাবে তা এখান থেকে এডিট করুন",
                                fontSize = 10.5.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        Switch(
                            checked = refEnabled,
                            onCheckedChange = { refEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF140526),
                                checkedTrackColor = SuccessGreen
                            )
                        )
                    }

                    // Commission Mode Selector: Fixed vs Percentage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("FIXED" to "৳ Fixed Bonus", "PERCENTAGE" to "% Percentage").forEach { (modeKey, modeLabel) ->
                            val isSelected = refMode.equals(modeKey, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { refMode = modeKey },
                                label = { Text(modeLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Color(0xFF140526),
                                    containerColor = Color(0xFF261047),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    if (refMode.equals("PERCENTAGE", ignoreCase = true)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = refCommPercentInput,
                                onValueChange = { refCommPercentInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("কমিশন শতাংশ (%)", fontSize = 10.5.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SuccessGreen,
                                    unfocusedBorderColor = Color(0xFF38155E),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = refMaxCommInput,
                                onValueChange = { refMaxCommInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("সর্বোচ্চ কমিশন (৳)", fontSize = 10.5.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0xFF38155E),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = refBaseBdtInput,
                                onValueChange = { refBaseBdtInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("কত টাকার কেনাকাটায়? (৳)", fontSize = 10.5.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0xFF38155E),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = refCommBdtInput,
                                onValueChange = { refCommBdtInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("রেফারার কত পাবে? (৳)", fontSize = 10.5.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SuccessGreen,
                                    unfocusedBorderColor = Color(0xFF38155E),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = refMinOrderInput,
                            onValueChange = { refMinOrderInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("সর্বনিম্ন অর্ডার (৳)", fontSize = 10.5.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = refNewUserBonusInput,
                            onValueChange = { refNewUserBonusInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("নতুন ইউজার বোনাস (৳)", fontSize = 10.5.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = minAddMoneyInput,
                            onValueChange = { minAddMoneyInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("মিনিমাম এড মানি (৳)", fontSize = 10.5.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    // Eligible Services Selector
                    Text(
                        text = "✅ কোন সার্ভিসগুলোতে রেফারেল কমিশন প্রযোজ্য:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("CHAMET", "TIKTOK", "FREEFIRE", "EPAY", "EXCHANGE", "SOCIAL_BOOST").forEach { srv ->
                            val isEligible = eligibleServicesSet.contains(srv)
                            FilterChip(
                                selected = isEligible,
                                onClick = {
                                    eligibleServicesSet = if (isEligible && eligibleServicesSet.size > 1) {
                                        eligibleServicesSet - srv
                                    } else {
                                        eligibleServicesSet + srv
                                    }
                                },
                                label = { Text(srv, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SuccessGreen.copy(alpha = 0.25f),
                                    selectedLabelColor = SuccessGreen,
                                    containerColor = Color(0xFF1F0D3D),
                                    labelColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = refRulesInput,
                        onValueChange = { refRulesInput = it },
                        label = { Text("রেফারেল নিয়মাবলী / নির্দেশনা (Referral Rules Text)", fontSize = 10.5.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Text(
                        text = if (refMode.equals("PERCENTAGE", ignoreCase = true)) {
                            "📌 বর্তমান নিয়ম: রেফার করা ইউজার অর্ডার সম্পন্ন করলে অর্ডারের ${refCommPercentInput.ifBlank { "1.5" }}% কমিশন রেফারারের ওয়ালেটে যুক্ত হবে।"
                        } else {
                            "📌 বর্তমান নিয়ম: রেফার করা ইউজার ৳${refBaseBdtInput.ifBlank { "360" }} টাকার সার্ভিস কিনলে যে রেফার করেছে তার ওয়ালেটে অটোমেটিক ৳${refCommBdtInput.ifBlank { "5" }} টাকা যোগ হবে।"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFF86EFAC),
                        fontWeight = FontWeight.SemiBold
                    )

                    Button(
                        onClick = {
                            val updated = adminConfig.copy(
                                isReferralEnabled = refEnabled,
                                referralCommissionMode = refMode,
                                referralBaseOrderAmountBdt = (refBaseBdtInput.toDoubleOrNull() ?: 360.0).coerceAtLeast(1.0),
                                referralCommissionBdt = (refCommBdtInput.toDoubleOrNull() ?: 5.0).coerceAtLeast(0.0),
                                referralCommissionPercent = (refCommPercentInput.toDoubleOrNull() ?: 1.5).coerceAtLeast(0.0),
                                referralMinOrderAmountBdt = (refMinOrderInput.toDoubleOrNull() ?: 50.0).coerceAtLeast(0.0),
                                referralMaxCommissionBdt = (refMaxCommInput.toDoubleOrNull() ?: 500.0).coerceAtLeast(1.0),
                                referralEligibleServices = eligibleServicesSet,
                                referralRulesText = refRulesInput.trim(),
                                referralNewUserBonusBdt = (refNewUserBonusInput.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0),
                                minWalletAddMoneyBdt = (minAddMoneyInput.toDoubleOrNull() ?: 50.0).coerceAtLeast(10.0)
                            )
                            onSaveConfig(updated)
                            Toast.makeText(context, "✅ রেফারেল কমিশন ও ওয়ালেট রুলস সেভ হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text(
                            text = "💾 রেফারেল ও ওয়ালেট রুলস সেভ করুন",
                            color = Color(0xFF140526),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("গ্রাহকের UID, নাম বা সার্ভিস খুঁজুন...", fontSize = 12.sp, color = Color(0xFF6B7280)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF38155E),
                    focusedContainerColor = Color(0xFF140526),
                    unfocusedContainerColor = Color(0xFF140526),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "VIP", "Blocked").forEach { mode ->
                    FilterChip(
                        selected = filterMode == mode,
                        onClick = { filterMode = mode },
                        label = { Text(mode, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = Color(0xFF140526),
                            containerColor = Color(0xFF1C0933),
                            labelColor = Color.White
                        )
                    )
                }
            }
        }

        if (filteredCustomers.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF140526)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "👥", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "কোনো কাস্টমার পাওয়া যায়নি", fontSize = 13.5.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(filteredCustomers, key = { it.accountId }) { customer ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF17062B),
                    border = BorderStroke(
                        1.dp,
                        when {
                            customer.isBlocked -> DangerRed
                            customer.isVip -> GoldPrimary
                            else -> Color(0xFF38155E)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = customer.displayName.ifBlank { customer.accountId },
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onCopy("Customer Account", customer.email.ifBlank { customer.accountId }) },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary, modifier = Modifier.size(13.dp))
                                    }
                                }
                                if (customer.email.isNotBlank()) {
                                    Text(
                                        text = "📧 ${customer.email}" + if (customer.phone.isNotBlank()) " • 📱 ${customer.phone}" else "",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF38BDF8)
                                    )
                                } else if (customer.phone.isNotBlank()) {
                                    Text(
                                        text = "📱 ${customer.phone} • ID: ${customer.accountId}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (customer.email.equals("johirul958423@gmail.com", ignoreCase = true)) {
                                    Surface(
                                        color = Color(0xFF22C55E).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.8.dp, Color(0xFF22C55E))
                                    ) {
                                        Text(
                                            text = "🛡️ Admin",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4ADE80),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                } else if (customer.email.isNotBlank()) {
                                    Surface(
                                        color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.8.dp, Color(0xFF38BDF8))
                                    ) {
                                        Text(
                                            text = "✅ Member",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF38BDF8),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                if (customer.isVip) {
                                    Surface(
                                        color = GoldPrimary.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.8.dp, GoldPrimary)
                                    ) {
                                        Text(
                                            text = "👑 VIP",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                if (customer.isBlocked) {
                                    Surface(
                                        color = DangerRed.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(0.8.dp, DangerRed)
                                    ) {
                                        Text(
                                            text = "🚫 Blocked",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DangerRed,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "মোট অর্ডার: ${customer.totalOrders} টি (সফল: ${customer.completedOrders} টি) • মোট খরচ: ৳${String.format(Locale.US, "%,.0f", customer.totalSpentBdt)} BDT",
                            fontSize = 12.sp,
                            color = GoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "💳 ওয়ালেট ব্যালেন্স: ৳${String.format(Locale.US, "%,.1f", customer.walletBalanceBdt)} • 🎁 রেফার আয়: ৳${String.format(Locale.US, "%,.1f", customer.referralEarningsBdt)} (${customer.totalReferralsCount} জন)",
                            fontSize = 11.5.sp,
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold
                        )
                        if (customer.referralCode.isNotBlank() || customer.referredByCode.isNotBlank()) {
                            Text(
                                text = "রেফার কোড: ${customer.referralCode.ifBlank { "N/A" }}" +
                                    if (customer.referredByCode.isNotBlank()) " • রেফারার: ${customer.referredByCode}" else "",
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                        Text(
                            text = "সর্বশেষ সার্ভিস: ${customer.lastCategory} • পেমেন্ট: ${customer.paymentMethod}",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        if (customer.adminNote.isNotBlank()) {
                            Text(
                                text = "📝 এডমিন নোট: ${customer.adminNote}",
                                fontSize = 11.sp,
                                color = Color(0xFFC084FC)
                            )
                        }

                        // Direct Customer Live Support Chat & WhatsApp Contact Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onOpenCustomerLiveChat(customer) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "💬 লাইভ চ্যাট (সাপোর্ট)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            PremiumWhatsAppButton(
                                text = "WhatsApp মেসেজ",
                                onClick = {
                                    onContactWhatsApp(customer.phone.ifBlank { customer.accountId })
                                },
                                modifier = Modifier.weight(1f),
                                height = 40.dp,
                                cornerRadius = 10.dp,
                                fontSize = 11.5.sp,
                                iconSize = 17.dp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onUpdateCustomer(
                                        customer.accountId,
                                        customer.displayName,
                                        !customer.isVip,
                                        customer.isBlocked,
                                        customer.adminNote
                                    )
                                    Toast.makeText(context, "VIP স্ট্যাটাস আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.7f))
                            ) {
                                Text(
                                    text = if (customer.isVip) "VIP সরান" else "👑 VIP করুন",
                                    fontSize = 11.sp,
                                    color = GoldPrimary
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    editingCustomer = customer
                                    editName = customer.displayName
                                    editNote = customer.adminNote
                                    editVip = customer.isVip
                                    editBlocked = customer.isBlocked
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFA855F7))
                            ) {
                                Text(text = "✏️ ম্যানেজ", fontSize = 11.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    onUpdateCustomer(
                                        customer.accountId,
                                        customer.displayName,
                                        customer.isVip,
                                        !customer.isBlocked,
                                        customer.adminNote
                                    )
                                    Toast.makeText(
                                        context,
                                        if (!customer.isBlocked) "🚫 একাউন্ট ব্লক করা হয়েছে" else "✅ আনব্লক করা হয়েছে",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (customer.isBlocked) SuccessGreen else DangerRed)
                            ) {
                                Text(
                                    text = if (customer.isBlocked) "✅ Unblock" else "🚫 Block",
                                    fontSize = 11.sp,
                                    color = if (customer.isBlocked) SuccessGreen else DangerRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editingCustomer?.let { cust ->
        AlertDialog(
            onDismissRequest = { editingCustomer = null },
            title = { Text("কাস্টমার প্রোফাইল: ${cust.accountId}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "বর্তমান ওয়ালেট ব্যালেন্স: ৳${String.format(Locale.US, "%.2f", cust.walletBalanceBdt)} BDT",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4ADE80)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = walletDeltaInput,
                            onValueChange = { walletDeltaInput = it.filter { ch -> ch.isDigit() || ch == '.' || ch == '-' } },
                            label = { Text("টাকার পরিমাণ (৳)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val amt = walletDeltaInput.toDoubleOrNull() ?: 0.0
                                if (amt != 0.0) {
                                    onAdjustCustomerWallet(cust.email.ifBlank { cust.accountId }, amt)
                                    walletDeltaInput = ""
                                    editingCustomer = null
                                    Toast.makeText(context, "✅ ওয়ালেট ব্যালেন্স আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("+/- ব্যালেন্স", fontSize = 11.sp, color = Color(0xFF052E16), fontWeight = FontWeight.Black)
                        }
                    }
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("কাস্টমারের নাম / ট্যাগ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editNote,
                        onValueChange = { editNote = it },
                        label = { Text("এডমিন নোট (Admin Note)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateCustomer(cust.accountId, editName.trim(), editVip, editBlocked, editNote.trim())
                        editingCustomer = null
                        Toast.makeText(context, "✅ কাস্টমার তথ্য ক্লাউডে সেভ হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("সংরক্ষণ করুন", color = Color(0xFF140526), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingCustomer = null }) {
                    Text("বাতিল", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color(0xFF19062E)
        )
    }

    if (showAddMemberDialog) {
        AlertDialog(
            onDismissRequest = { showAddMemberDialog = false },
            title = { Text("➕ মেম্বার একাউন্ট যুক্ত / রিস্টোর করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Firebase Authentication-এ থাকা যেকোনো মেম্বারের ইমেইল, নাম ও ফোন নাম্বার দিয়ে সরাসরি প্যানেলে যুক্ত বা সিঙ্ক করুন:",
                        fontSize = 11.5.sp,
                        color = Color(0xFFD1D5DB)
                    )
                    OutlinedTextField(
                        value = newMemberEmail,
                        onValueChange = { newMemberEmail = it },
                        label = { Text("মেম্বার ইমেইল (Email)") },
                        placeholder = { Text("example@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMemberName,
                        onValueChange = { newMemberName = it },
                        label = { Text("মেম্বারের নাম (Name)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMemberPhone,
                        onValueChange = { newMemberPhone = it },
                        label = { Text("ফোন নাম্বার (ঐচ্ছিক)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMemberEmail.isNotBlank()) {
                            onAddCustomerByAdmin(newMemberEmail.trim(), newMemberName.trim(), newMemberPhone.trim())
                            newMemberEmail = ""
                            newMemberName = ""
                            newMemberPhone = ""
                            showAddMemberDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("যুক্ত ও সিঙ্ক করুন", color = Color(0xFF140526), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddMemberDialog = false }) {
                    Text("বাতিল", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color(0xFF19062E)
        )
    }
}

// -------------------------------------------------------------
// TAB: SETTINGS, FIREBASE AUTH & SECURITY
// -------------------------------------------------------------
@Composable
fun AdminSettingsTab(
    config: AdminConfig,
    firebaseEmail: String?,
    onSave: (AdminConfig) -> Unit,
    onChangePassword: (String, String) -> Boolean = { _, _ -> true },
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var whatsappNum by remember(config) { mutableStateOf(config.whatsappNumber) }
    var telegramUrl by remember(config) { mutableStateOf(config.telegramUrl) }
    var adminEmailInput by remember(config) { mutableStateOf(config.adminEmail) }

    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Firebase Security Gateway Status
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A0833),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔥 Firebase Auth & Cloud Firestore", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, SuccessGreen)
                        ) {
                            Text(
                                text = "Secured",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "লগইনকৃত এডমিন: ${firebaseEmail ?: config.adminEmail}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Firebase Authentication এবং Cloud Firestore ডাটাবেসের মাধ্যমে অর্ডার, কাস্টমার ও প্রাইস সম্পূর্ণ অনলাইনে সুরক্ষিত। কোনো পাসওয়ার্ড অ্যাপে হার্ডকোড করা নেই।",
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }

        // WhatsApp & Telegram Settings
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF17062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "💬 সাপোর্ট ও কমিউনিকেশন চ্যানেল", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)

                    OutlinedTextField(
                        value = whatsappNum,
                        onValueChange = { whatsappNum = it },
                        label = { Text("WhatsApp Number (e.g. 8801880467335)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhatsAppGreen,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = telegramUrl,
                        onValueChange = { telegramUrl = it },
                        label = { Text("Telegram Link (e.g. https://t.me/johirul0188)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TelegramBlue,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = adminEmailInput,
                        onValueChange = { adminEmailInput = it },
                        label = { Text("Authorized Admin Email (Firebase Auth)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Button(
                        onClick = {
                            onSave(
                                config.copy(
                                    whatsappNumber = whatsappNum.trim(),
                                    telegramUrl = telegramUrl.trim(),
                                    adminEmail = adminEmailInput.trim()
                                )
                            )
                            Toast.makeText(context, "✅ সেটিংস ক্লাউডে সংরক্ষণ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text(text = "💾 সেটিংস সেভ করুন", color = Color(0xFF140526), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Firebase Admin Password Change Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF17062B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38155E))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔐 Firebase Admin পাসওয়ার্ড পরিবর্তন", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }

                    Text(
                        text = "আপনার বর্তমান পাসওয়ার্ড যাচাই করে নতুন নিরাপদ পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর) সেট করুন।",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    OutlinedTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it },
                        label = { Text("বর্তমান এডমিন পাসওয়ার্ড (Current Password)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("নতুন এডমিন পাসওয়ার্ড (New Password - 6+ chars)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Button(
                        onClick = {
                            if (newPass.trim().length < 6) {
                                Toast.makeText(context, "নতুন পাসওয়ার্ড অন্তত ৬ অক্ষরের হতে হবে", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val success = onChangePassword(oldPass, newPass)
                            if (success) {
                                Toast.makeText(context, "✅ এডমিন পাসওয়ার্ড সফলভাবে আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
                                oldPass = ""
                                newPass = ""
                            } else {
                                Toast.makeText(context, "❌ বর্তমান পাসওয়ার্ড ভুল!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text(text = "🔐 পাসওয়ার্ড পরিবর্তন ও সংরক্ষণ করুন", color = Color(0xFF130724), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Logout Button
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.85f))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "এডমিন প্যানেল থেকে লগআউট করুন", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
