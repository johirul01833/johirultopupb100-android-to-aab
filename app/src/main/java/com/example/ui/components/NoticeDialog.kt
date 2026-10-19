package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.R
import com.example.data.local.AdminConfig
import com.example.data.model.OrderNotification
import com.example.data.model.OrderRecord
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 1. FULL-SCREEN NOTIFICATION CENTER (ফুল স্ক্রিন নোটিফিকেশন সেন্টার)
 * Opened exclusively when user clicks the notification bell at the top header or home.
 * Per user instructions: Absolutely NO "জরুরী নিয়মাবলী" here.
 */
@Composable
fun FullScreenNotificationScreen(
    onDismiss: () -> Unit,
    onWhatsAppClick: () -> Unit = {},
    orders: List<OrderRecord> = emptyList(),
    notifications: List<OrderNotification> = emptyList(),
    currentUserEmail: String = "",
    userClearedTimestamp: Long = 0L,
    onOpenOrderReceipt: (OrderRecord) -> Unit = {},
    onClearNotifications: () -> Unit = {}
) {
    BackHandler(onBack = onDismiss)

    val cleanUserEmail = remember(currentUserEmail) { currentUserEmail.trim().lowercase() }

    val userOrders = remember(orders, cleanUserEmail) {
        if (cleanUserEmail.isEmpty()) {
            emptyList()
        } else {
            orders.filter { it.customerEmail.trim().lowercase() == cleanUserEmail }
        }
    }

    // Combine explicit notifications with orders belonging strictly to the logged-in user
    val mergedNotifications = remember(notifications, userOrders, cleanUserEmail, userClearedTimestamp) {
        if (cleanUserEmail.isEmpty()) {
            emptyList()
        } else {
            val userOrderNumbers = userOrders.map { it.orderNumber }.toSet()
            val filteredNotifs = notifications.filter { notif ->
                val nEmail = notif.customerEmail.trim().lowercase()
                val belongsToUser = (nEmail.isNotEmpty() && nEmail == cleanUserEmail) ||
                        (nEmail.isEmpty() && userOrderNumbers.contains(notif.orderNumber))
                belongsToUser && notif.timestamp > userClearedTimestamp
            }
            val byOrderNum = filteredNotifs.associateBy { it.orderNumber }.toMutableMap()
            userOrders.forEach { ord ->
                if (ord.timestamp > userClearedTimestamp) {
                    val existing = byOrderNum[ord.orderNumber]
                    val normalizedStatus = when (ord.status) {
                        "Completed", "Approved" -> "Approved"
                        "Cancelled", "Incorrect TrxID" -> "Incorrect TrxID"
                        else -> "Processing"
                    }
                    if (existing == null || existing.status != normalizedStatus) {
                        val (title, msg) = when (normalizedStatus) {
                            "Approved" -> {
                                "✅ অর্ডার Approved হয়েছে! (#${ord.orderNumber})" to
                                        "অভিনন্দন! আপনার অর্ডার #${ord.orderNumber} (${ord.itemName} - ${ord.amountOrQuantity}) সফলভাবে সম্পন্ন (Approved) করা হয়েছে।"
                            }
                            "Incorrect TrxID" -> {
                                val trxPart = if (ord.transactionTrxId.isNotBlank()) " (TrxID: ${ord.transactionTrxId})" else ""
                                "❌ ভুল TrxID / বাতিল (#${ord.orderNumber})" to
                                        "দুঃখিত! আপনার অর্ডার #${ord.orderNumber}$trxPart যাচাই করে ভুল TrxID হিসেবে চিহ্নিত করা হয়েছে। সঠিক তথ্য দিয়ে সাপোর্টে যোগাযোগ করুন।"
                            }
                            else -> {
                                "⏳ অর্ডার প্রসেসিং চলছে (#${ord.orderNumber})" to
                                        "আপনার অর্ডার #${ord.orderNumber} (${ord.itemName}) বর্তমানে প্রসেসিংয়ে (Processing) রয়েছে। সম্পন্ন হলে এখানে মেসেজ চলে আসবে।"
                            }
                        }
                        byOrderNum[ord.orderNumber] = OrderNotification(
                            id = "notif_${ord.orderNumber}",
                            orderNumber = ord.orderNumber,
                            title = title,
                            message = msg,
                            status = normalizedStatus,
                            trxId = ord.transactionTrxId,
                            itemName = "${ord.itemName} (${ord.amountOrQuantity})",
                            priceBdt = ord.priceBdt,
                            timestamp = existing?.timestamp ?: ord.timestamp,
                            isRead = existing?.isRead ?: false,
                            customerEmail = cleanUserEmail
                        )
                    }
                }
            }
            byOrderNum.values.sortedByDescending { it.timestamp }
        }
    }

    // Filter states: ALL, APPROVED, PROCESSING, CANCELLED
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredNotifications = remember(mergedNotifications, selectedFilter) {
        when (selectedFilter) {
            "APPROVED" -> mergedNotifications.filter { it.status == "Approved" || it.status == "Completed" }
            "PROCESSING" -> mergedNotifications.filter { it.status == "Processing" || it.status == "Pending" }
            "REJECTED" -> mergedNotifications.filter { it.status == "Cancelled" || it.status == "Incorrect TrxID" }
            else -> mergedNotifications
        }
    }

    val approvedCount = remember(mergedNotifications) {
        mergedNotifications.count { it.status == "Completed" || it.status == "Approved" }
    }
    val processingCount = remember(mergedNotifications) {
        mergedNotifications.count { it.status == "Processing" || it.status == "Pending" }
    }
    val cancelledCount = remember(mergedNotifications) {
        mergedNotifications.count { it.status == "Cancelled" || it.status == "Incorrect TrxID" }
    }

    // FULL SCREEN CONTAINER
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(60f)
            .background(Color(0xFF090314))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("full_screen_notification_view")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Executive Full-Screen Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF140529),
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Back / Dismiss Button
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape)
                                    .testTag("notification_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🔔 নোটিফিকেশন সেন্টার",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    if (mergedNotifications.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = GoldPrimary,
                                            modifier = Modifier.padding(top = 1.dp)
                                        ) {
                                            Text(
                                                text = "${mergedNotifications.size}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF130724),
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "LIVE ORDER APPROVALS & STATUS ALERTS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight.copy(alpha = 0.88f),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Top Action: Clear all notifications button
                        if (mergedNotifications.isNotEmpty()) {
                            Surface(
                                onClick = onClearNotifications,
                                shape = RoundedCornerShape(10.dp),
                                color = DangerRed.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.6f)),
                                modifier = Modifier.testTag("notification_clear_all_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = null,
                                        tint = Color(0xFFF87171),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "সব মুছুন",
                                        fontSize = 11.sp,
                                        color = Color(0xFFF87171),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Gold Neon Accent Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        GoldPrimary.copy(alpha = 0.8f),
                                        NeonMagenta.copy(alpha = 0.7f),
                                        NeonPurple.copy(alpha = 0.7f),
                                        GoldPrimary.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                }
            }

            // 2. Filter Pills Row (All, Approved, Processing, Rejected)
            Surface(
                color = Color(0xFF100423),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NotificationFilterChip(
                        label = "সকল (${mergedNotifications.size})",
                        isSelected = selectedFilter == "ALL",
                        activeColor = GoldPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedFilter = "ALL" }
                    )
                    NotificationFilterChip(
                        label = "Approved ($approvedCount)",
                        isSelected = selectedFilter == "APPROVED",
                        activeColor = SuccessGreen,
                        modifier = Modifier.weight(1.1f),
                        onClick = { selectedFilter = "APPROVED" }
                    )
                    NotificationFilterChip(
                        label = "Processing ($processingCount)",
                        isSelected = selectedFilter == "PROCESSING",
                        activeColor = WarningYellow,
                        modifier = Modifier.weight(1.1f),
                        onClick = { selectedFilter = "PROCESSING" }
                    )
                    NotificationFilterChip(
                        label = "ভুল TrxID ($cancelledCount)",
                        isSelected = selectedFilter == "REJECTED",
                        activeColor = DangerRed,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedFilter = "REJECTED" }
                    )
                }
            }

            // 3. Main Full Screen Scrollable Notifications List
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 420.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF15072B),
                        border = BorderStroke(1.2.dp, Color(0xFF38155E)),
                        tonalElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.15f))
                                    .border(1.5.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🔔", fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (cleanUserEmail.isEmpty()) {
                                    "আপনার একাউন্টে লগইন করা নেই"
                                } else if (mergedNotifications.isEmpty()) {
                                    "এখনো কোনো অর্ডার নোটিফিকেশন নেই"
                                } else {
                                    "এই ক্যাটাগরিতে কোনো নোটিফিকেশন নেই"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (cleanUserEmail.isEmpty()) {
                                    "আপনার অর্ডারের Processing এবং Approved নোটিফিকেশন দেখতে প্রোফাইল থেকে লগইন করুন। প্রতিটি আইডির নোটিফিকেশন ও ট্রানজেকশন সম্পূর্ণ সুরক্ষিত।"
                                } else {
                                    "আপনি নতুন অর্ডার সাবমিট করলে কিংবা এডমিন আপনার অর্ডার Approve / Reject করলে সাথে সাথে এখানে লাইভ নোটিফিকেশন ও মেসেজ চলে আসবে।"
                                },
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { notif ->
                        val matchingOrder = userOrders.find { it.orderNumber == notif.orderNumber }
                        OrderNotificationCard(
                            notif = notif,
                            onViewReceipt = {
                                if (matchingOrder != null) {
                                    onDismiss()
                                    onOpenOrderReceipt(matchingOrder)
                                }
                            },
                            onContactSupport = onWhatsAppClick,
                            hasMatchingOrder = matchingOrder != null
                        )
                    }
                }
            }
        }
    }
}

/**
 * Filter Chip Composable for Notification Status Filtering
 */
@Composable
private fun NotificationFilterChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(9.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(9.dp),
        color = if (isSelected) activeColor else Color(0xFF1E0B38),
        border = BorderStroke(
            1.dp,
            if (isSelected) activeColor else Color(0xFF38155E)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                color = if (isSelected) Color(0xFF100423) else Color(0xFFCBD5E1),
                maxLines = 1
            )
        }
    }
}

/**
 * 2. APP LAUNCH RULES & OFFICIAL NOTICE DIALOG (জরুরি নিয়মাবলী ও নোটিশ)
 * Per user instructions: App opens -> ONLY THEN this dialog shows!
 * It is completely deleted from notifications.
 */
@Composable
fun AppLaunchRulesDialog(
    onDismiss: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onTelegramClick: () -> Unit,
    adminConfig: AdminConfig = AdminConfig()
) {
    BackHandler(onBack = onDismiss)

    val backdropInteraction = remember { MutableInteractionSource() }
    val cardInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(55f)
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable(
                interactionSource = backdropInteraction,
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 440.dp)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable(
                    interactionSource = cardInteraction,
                    indication = null,
                    onClick = {}
                )
                .border(
                    1.5.dp,
                    Brush.linearGradient(
                        listOf(
                            GoldLight.copy(alpha = 0.9f),
                            GoldPrimary.copy(alpha = 0.6f),
                            NeonMagenta.copy(alpha = 0.6f),
                            NeonPurple.copy(alpha = 0.6f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                ),
            color = Color(0xFF0E051C),
            tonalElevation = 14.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Executive Luxury Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1E0638),
                                    Color(0xFF3B0764),
                                    Color(0xFF58094F)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 13.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = 36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            GoldPrimary.copy(alpha = 0.25f),
                                            NeonMagenta.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    GoldPrimary.copy(alpha = 0.65f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GppGood,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "জরুরি নিয়মাবলী ও অফিসিয়াল নোটিশ",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "OFFICIAL TERMS & SERVICE GUIDELINES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight.copy(alpha = 0.88f),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterEnd)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .border(0.8.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Thin Gold Accent Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    GoldPrimary.copy(alpha = 0.8f),
                                    NeonMagenta.copy(alpha = 0.7f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Scrollable Content Area: Dedicated to Official Rules & Notice
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Brand Authority Identity Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF17092E),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    GoldPrimary.copy(alpha = 0.45f),
                                    NeonPurple.copy(alpha = 0.35f)
                                )
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF05010D))
                                        .border(1.dp, GoldPrimary.copy(alpha = 0.65f), CircleShape),
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Chamet Top Up BD",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GoldPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Text(
                                        text = "বিশ্বস্ত, দ্রুত ও ১০০% নিরাপদ ডিজিটাল সার্ভিস",
                                        fontSize = 10.sp,
                                        color = Color(0xFFCBD5E1),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Surface(
                                color = SuccessGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(0.8.dp, SuccessGreen.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(SuccessGreen)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "VERIFIED",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SuccessGreen
                                    )
                                }
                            }
                        }
                    }

                    // Rule Card 1
                    ProfessionalRuleCard(
                        stepNumber = "01",
                        icon = "🇧🇩",
                        title = "BD Account & Currency Exchange",
                        badgeText = "FAST SETTLEMENT",
                        desc = adminConfig.noticeRule1,
                        accentColor = Color(0xFF10B981),
                        cardBgColor = Color(0xFF0A1F1A)
                    )

                    // Rule Card 2
                    ProfessionalRuleCard(
                        stepNumber = "02",
                        icon = "💸",
                        title = "EPAY Dollar Exchange Policy",
                        badgeText = "LIVE RATE",
                        desc = adminConfig.noticeRule2,
                        accentColor = Color(0xFF38BDF8),
                        cardBgColor = Color(0xFF0A1B2E)
                    )

                    // Rule Card 3
                    ProfessionalRuleCard(
                        stepNumber = "03",
                        icon = "💳",
                        title = "Payment & TrxID Verification",
                        badgeText = "OFFICIAL GATEWAY",
                        desc = adminConfig.noticeRule3,
                        accentColor = GoldPrimary,
                        cardBgColor = Color(0xFF211507)
                    )

                    // Important Bullet List
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF16092B),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📋", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "অর্ডার ও লেনদেনের আবশ্যিক নির্দেশনা",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldLight
                                )
                            }

                            Spacer(modifier = Modifier.height(1.dp))

                            ProfessionalBulletItem(
                                text = "পেমেন্ট করার পূর্বে অ্যাপের লাইভ রেট ও সঠিক Amount ভালোভাবে মিলিয়ে নিন।"
                            )
                            ProfessionalBulletItem(
                                text = "পেমেন্ট সম্পন্ন হওয়ার পর সঠিক TrxID অথবা স্পষ্ট স্ক্রিনশট সাবমিট করুন।"
                            )
                            ProfessionalBulletItem(
                                text = "টপ-আপ অর্ডারের পূর্বে আপনার সঠিক Player UID / Account ID যাচাই করুন।"
                            )
                            ProfessionalBulletItem(
                                text = "থার্ড-পার্টি পেমেন্ট করার পূর্বে অবশ্যই অফিসিয়াল সাপোর্টে অনুমতি নিন।"
                            )
                        }
                    }

                    // Security Alert Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(13.dp),
                        color = Color(0xFF280712),
                        border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.55f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(DangerRed.copy(alpha = 0.18f))
                                    .border(0.8.dp, DangerRed.copy(alpha = 0.45f), RoundedCornerShape(9.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFFF87171),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(9.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "নিরাপত্তা সতর্কবার্তা (Security Alert)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFF87171)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = adminConfig.noticeAlert.ifBlank {
                                        "আপনার বিকাশ/নগদ পিন (PIN) বা ওটিপি (OTP) কখনোই কাউকে শেয়ার করবেন না।"
                                    },
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFFECACA),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    // Fast Support Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PremiumTelegramButton(
                            text = "Telegram Support",
                            onClick = onTelegramClick,
                            modifier = Modifier.weight(1f),
                            height = 36.dp,
                            cornerRadius = 10.dp,
                            fontSize = 11.sp,
                            iconSize = 16.dp
                        )

                        PremiumWhatsAppButton(
                            text = "WhatsApp Live",
                            onClick = onWhatsAppClick,
                            modifier = Modifier.weight(1f),
                            height = 36.dp,
                            cornerRadius = 10.dp,
                            fontSize = 11.sp,
                            iconSize = 16.dp
                        )
                    }
                }

                // Executive Bottom Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF140726))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text(
                            text = "আমি নিয়মাবলী পড়েছি ও সম্মত আছি ➔",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp,
                            color = Color(0xFF130724)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Backward compatibility wrapper for NoticeDialog
 */
@Composable
fun NoticeDialog(
    onDismiss: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onTelegramClick: () -> Unit,
    adminConfig: AdminConfig = AdminConfig(),
    orders: List<OrderRecord> = emptyList(),
    notifications: List<OrderNotification> = emptyList(),
    currentUserEmail: String = "",
    userClearedTimestamp: Long = 0L,
    initialTab: Int = 0,
    onOpenOrderReceipt: (OrderRecord) -> Unit = {},
    onMarkNotificationsRead: () -> Unit = {},
    onClearNotifications: () -> Unit = {}
) {
    if (initialTab == 1) {
        AppLaunchRulesDialog(
            onDismiss = onDismiss,
            onWhatsAppClick = onWhatsAppClick,
            onTelegramClick = onTelegramClick,
            adminConfig = adminConfig
        )
    } else {
        LaunchedEffect(Unit) {
            onMarkNotificationsRead()
        }
        FullScreenNotificationScreen(
            onDismiss = onDismiss,
            onWhatsAppClick = onWhatsAppClick,
            orders = orders,
            notifications = notifications,
            currentUserEmail = currentUserEmail,
            userClearedTimestamp = userClearedTimestamp,
            onOpenOrderReceipt = onOpenOrderReceipt,
            onClearNotifications = onClearNotifications
        )
    }
}

@Composable
private fun OrderNotificationCard(
    notif: OrderNotification,
    onViewReceipt: () -> Unit,
    onContactSupport: () -> Unit,
    hasMatchingOrder: Boolean
) {
    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateStr = remember(notif.timestamp) { sdf.format(Date(notif.timestamp)) }

    val (accentColor, bgColor, statusBadgeText) = when (notif.status) {
        "Completed", "Approved" -> Triple(SuccessGreen, Color(0xFF092218), "✅ APPROVED • অর্ডার সম্পন্ন")
        "Cancelled", "Incorrect TrxID" -> Triple(DangerRed, Color(0xFF260914), "❌ REJECTED • ভুল TrxID")
        else -> Triple(WarningYellow, Color(0xFF211708), "⏳ PROCESSING • যাচাই চলছে")
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(1.2.dp, accentColor.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Row: Status Badge + Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accentColor.copy(alpha = 0.18f),
                    border = BorderStroke(0.8.dp, accentColor)
                ) {
                    Text(
                        text = statusBadgeText,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Notification Title
            Text(
                text = notif.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Notification Message
            Text(
                text = notif.message,
                fontSize = 11.2.sp,
                color = Color(0xFFE2E8F0),
                lineHeight = 15.5.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Package, Price & TrxID Info Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(9.dp),
                color = Color.Black.copy(alpha = 0.32f),
                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📦 ${notif.itemName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        if (notif.trxId.isNotBlank()) {
                            Text(
                                text = "🔖 TrxID: ${notif.trxId}",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Text(
                        text = "৳${String.format(Locale.US, "%,.0f", notif.priceBdt)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                }
            }

            if (hasMatchingOrder || notif.status == "Cancelled" || notif.status == "Incorrect TrxID") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (hasMatchingOrder) {
                        Button(
                            onClick = onViewReceipt,
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor.copy(alpha = 0.22f)
                            ),
                            border = BorderStroke(1.dp, accentColor),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "ডিজিটাল রিসিট দেখুন",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    if (notif.status == "Cancelled" || notif.status == "Incorrect TrxID") {
                        Button(
                            onClick = onContactSupport,
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "💬 সাপোর্টে যোগাযোগ",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfessionalRuleCard(
    stepNumber: String,
    icon: String,
    title: String,
    badgeText: String,
    desc: String,
    accentColor: Color,
    cardBgColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),
        color = cardBgColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.38f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 11.dp, vertical = 9.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(0.8.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(9.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$stepNumber. $title",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.6.dp, accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ProfessionalBulletItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier
                .padding(top = 1.dp)
                .size(13.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = text,
            fontSize = 10.8.sp,
            color = Color(0xFFE2E8F0),
            lineHeight = 14.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
