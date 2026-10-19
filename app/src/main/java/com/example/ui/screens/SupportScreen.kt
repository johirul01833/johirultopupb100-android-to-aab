package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdminConfig
import com.example.data.local.TopupRepository
import com.example.ui.components.PremiumTelegramButton
import com.example.ui.components.PremiumWhatsAppButton
import com.example.ui.components.SupportReferenceSquircle
import com.example.ui.components.TelegramIconBadge
import com.example.ui.components.WhatsAppIconBadge
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen

@Composable
fun SupportScreen(
    onWhatsAppClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onPaymentAccountsClick: () -> Unit,
    onCopyContact: (String, String) -> Unit,
    onSupportChatClick: () -> Unit = {},
    onAdminLoginClick: () -> Unit = {},
    adminConfig: AdminConfig = AdminConfig(),
    modifier: Modifier = Modifier
) {
    val whatsappDisplay = adminConfig.whatsappDisplay.ifBlank { TopupRepository.WHATSAPP_DISPLAY }
    val telegramUser = adminConfig.telegramUsername.ifBlank { TopupRepository.TELEGRAM_USERNAME }
    val pinkAccent = Color(0xFFFF2CF3)
    val purpleAccent = Color(0xFF9333EA)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Luxury Neon Glassmorphism Support Hero Header with Official SupportReferenceSquircle
        item {
            val heroShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, heroShape, spotColor = pinkAccent.copy(alpha = 0.45f))
                    .clip(heroShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF26103E),
                                Color(0xFF130928),
                                Color(0xFF1B0B30)
                            )
                        )
                    )
                    .border(
                        1.4.dp,
                        Brush.linearGradient(
                            listOf(
                                GoldPrimary.copy(alpha = 0.85f),
                                pinkAccent.copy(alpha = 0.80f),
                                Color(0xFF38BDF8).copy(alpha = 0.70f)
                            )
                        ),
                        heroShape
                    )
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    pinkAccent.copy(alpha = 0.24f),
                                    GoldPrimary.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.width * 0.50f,
                            center = Offset(size.width * 0.85f, size.height * 0.20f)
                        )
                    }
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SupportReferenceSquircle(size = 50.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                                            append("24/7 কাস্টমার ")
                                        }
                                        withStyle(
                                            SpanStyle(
                                                brush = Brush.horizontalGradient(
                                                    listOf(GoldPrimary, Color(0xFFFFE57F), pinkAccent)
                                                ),
                                                fontWeight = FontWeight.Black
                                            )
                                        ) {
                                            append("সাপোর্ট হেল্পডেস্ক")
                                        }
                                    },
                                    fontSize = 17.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Chamet Top Up BD Official Help Desk",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldLight
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0A281E).copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.65f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color(0xFF4ADE80),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "ONLINE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF86EFAC)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "টপ-আপ, ডলার এক্সচেঞ্জ বা যেকোনো লেনদেন সংক্রান্ত প্রশ্ন বা সহায়তার জন্য আমাদের অফিসিয়াল সাপোর্ট চ্যানেলে সরাসরি যোগাযোগ করুন।",
                        fontSize = 11.5.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 17.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Direct Official Support Channels (In-App Live Support Chat + WhatsApp & Telegram Cards)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // In-App Firebase Firestore Support Chat Card
                val chatShape = RoundedCornerShape(16.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, chatShape, spotColor = GoldPrimary.copy(alpha = 0.35f))
                        .clip(chatShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF24103E), Color(0xFF140926), Color(0xFF1B0C32))
                            )
                        )
                        .border(
                            1.3.dp,
                            Brush.horizontalGradient(
                                listOf(GoldPrimary.copy(alpha = 0.9f), pinkAccent.copy(alpha = 0.75f))
                            ),
                            chatShape
                        )
                        .clickable { onSupportChatClick() }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                Text(text = "💬", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ইন-অ্যাপ সাপোর্ট চ্যাট (Support Chat)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = "অ্যাপের ভেতরেই সরাসরি সাপোর্ট টিমে মেসেজ পাঠান",
                                    fontSize = 11.sp,
                                    color = GoldLight
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldPrimary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "মেসেজ দিন",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // WhatsApp Compact Luxury Card
                val waShape = RoundedCornerShape(16.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, waShape, spotColor = Color(0xFF25D366).copy(alpha = 0.32f))
                        .clip(waShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0A2419), Color(0xFF071611), Color(0xFF09101D))
                            )
                        )
                        .border(
                            1.2.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFF25D366).copy(alpha = 0.80f), GoldPrimary.copy(alpha = 0.50f))
                            ),
                            waShape
                        )
                        .clickable { onWhatsAppClick() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            WhatsAppIconBadge(size = 38.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "WhatsApp Direct",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = whatsappDisplay,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF86EFAC)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { onCopyContact("WhatsApp Number", whatsappDisplay) },
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF25D366).copy(alpha = 0.16f),
                                        border = BorderStroke(0.7.dp, Color(0xFF25D366).copy(alpha = 0.45f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy WhatsApp",
                                                tint = Color(0xFF86EFAC),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Copy",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF86EFAC)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        PremiumWhatsAppButton(
                            text = "Chat",
                            onClick = onWhatsAppClick,
                            height = 33.dp,
                            cornerRadius = 50.dp,
                            fontSize = 11.sp,
                            iconSize = 16.dp
                        )
                    }
                }

                // Telegram Compact Luxury Card
                val tgShape = RoundedCornerShape(16.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, tgShape, spotColor = Color(0xFF2AABEE).copy(alpha = 0.32f))
                        .clip(tgShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0A1D33), Color(0xFF081322), Color(0xFF0A0E1E))
                            )
                        )
                        .border(
                            1.2.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2AABEE).copy(alpha = 0.80f), Color(0xFF8B5CF6).copy(alpha = 0.60f))
                            ),
                            tgShape
                        )
                        .clickable { onTelegramClick() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            TelegramIconBadge(size = 38.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Telegram Official",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "@$telegramUser",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF93C5FD)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { onCopyContact("Telegram Username", "@$telegramUser") },
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF2AABEE).copy(alpha = 0.16f),
                                        border = BorderStroke(0.7.dp, Color(0xFF2AABEE).copy(alpha = 0.45f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy Telegram",
                                                tint = Color(0xFF93C5FD),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Copy",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF93C5FD)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        PremiumTelegramButton(
                            text = "Open",
                            onClick = onTelegramClick,
                            height = 33.dp,
                            cornerRadius = 50.dp,
                            fontSize = 11.sp,
                            iconSize = 16.dp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3. Working Hours Card (Neon Glassmorphism)
        item {
            val hoursShape = RoundedCornerShape(18.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, hoursShape, spotColor = purpleAccent.copy(alpha = 0.3f))
                    .clip(hoursShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1C0D32), Color(0xFF100820))
                        )
                    )
                    .border(
                        1.2.dp,
                        Brush.horizontalGradient(listOf(GoldPrimary.copy(alpha = 0.65f), purpleAccent.copy(alpha = 0.6f))),
                        hoursShape
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(GoldPrimary.copy(alpha = 0.16f))
                                .border(1.dp, GoldPrimary.copy(alpha = 0.55f), RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "কাজের সময়সূচী (Business Hours)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Everyday 9:00 AM – 10:00 PM (BST)",
                                fontSize = 10.5.sp,
                                color = GoldLight
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "প্রতিদিন সকাল ৯:০০ টা থেকে রাত ১০:০০ টা পর্যন্ত (Bangladesh Standard Time)। কাজের সময়ের বাইরে পাঠানো মেসেজ সকাল ৯ টায় প্রাধান্য দিয়ে সমাধান করা হবে।",
                        fontSize = 11.5.sp,
                        color = Color(0xFFD1D5DB),
                        lineHeight = 17.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Payment Accounts Quick Access Button (Glowing Gold/Purple Gradient)
        item {
            val payShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(10.dp, payShape, spotColor = GoldPrimary.copy(alpha = 0.35f))
                    .clip(payShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2B124C), Color(0xFF4C1D95), Color(0xFF2B124C))
                        )
                    )
                    .border(
                        1.3.dp,
                        Brush.horizontalGradient(listOf(GoldPrimary, pinkAccent, GoldPrimary)),
                        payShape
                    )
                    .clickable { onPaymentAccountsClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "বিকাশ ও নগদ একাউন্ট নাম্বার দেখুন (Payment Accounts)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Fraud Warning Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF220C16),
                border = BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFEF4444).copy(alpha = 0.7f), Color(0xFF991B1B)))
                )
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "প্রতারণা থেকে সতর্ক থাকুন (Fraud Alert)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFF87171)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আমাদের নাম ব্যবহার করে কোনো ভুয়া গ্রুপ বা নাম্বার থেকে মেসেজ আসলে প্রতারিত হবেন না। শুধুমাত্র অ্যাপে দেওয়া বিকাশ ও নগদ নাম্বারেই লেনদেন করুন।",
                        fontSize = 11.5.sp,
                        color = Color(0xFFFCA5A5),
                        lineHeight = 17.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
