package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TopupRepository
import com.example.data.model.PromoBanner
import com.example.data.model.TopupPackage
import com.example.ui.components.AutoRotatingBannerCarousel
import com.example.ui.components.CalculateReferenceSquircle
import com.example.ui.components.ChametReferenceSquircle
import com.example.ui.components.EpayReferenceSquircle
import com.example.ui.components.ExchangeReferenceSquircle
import com.example.ui.components.FreeFireGamingSquircle
import com.example.ui.components.SupportReferenceSquircle
import com.example.ui.components.TikTokReferenceSquircle
import com.example.ui.theme.BlueEpay
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppNavTab

@Composable
fun HomeScreen(
    onNavigateTab: (AppNavTab) -> Unit,
    onPackageClick: (TopupPackage) -> Unit,
    onPaymentAccountsClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onAccountClick: () -> Unit = {},
    onNoticeClick: () -> Unit = {},
    onSupportChatClick: () -> Unit = {},
    onAdminPanelClick: () -> Unit = {},
    loggedInUserEmail: String = "",
    loggedInUserName: String = "",
    isAdminLoggedIn: Boolean = false,
    unreadNotificationCount: Int = 0,
    approvedOrderCount: Int = 0,
    tiktokPackages: List<TopupPackage> = TopupRepository.TIKTOK_PACKAGES,
    chametPackages: List<TopupPackage> = TopupRepository.CHAMET_PACKAGES,
    freeFirePackages: List<TopupPackage> = TopupRepository.FREEFIRE_PACKAGES,
    promoBanners: List<PromoBanner> = emptyList(),
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 32.dp)
    ) {
        // 1. Promotional Hero Banner Slider at top
        if (promoBanners.isNotEmpty()) {
            item {
                AutoRotatingBannerCarousel(
                    banners = promoBanners,
                    onBannerClick = {},
                    onNavigateTab = onNavigateTab
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // 2. Premium Glassmorphism Welcome Hero Card with Login/Sign Up & Live Notification Buttons
        item {
            WelcomeFintechHeroCard(
                onNavigateTab = onNavigateTab,
                onAccountClick = onAccountClick,
                onNoticeClick = onNoticeClick,
                onSupportChatClick = onSupportChatClick,
                onAdminPanelClick = onAdminPanelClick,
                loggedInUserEmail = loggedInUserEmail,
                loggedInUserName = loggedInUserName,
                isAdminLoggedIn = isAdminLoggedIn,
                unreadNotificationCount = unreadNotificationCount,
                approvedOrderCount = approvedOrderCount
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. Section Header: Our Premium Services (আমাদের সার্ভিস সমূহ)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Glowing Neon Bolt Icon Badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF3B1566), Color(0xFF1A0B36))
                                )
                            )
                            .border(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(GoldPrimary.copy(alpha = 0.85f), NeonMagenta.copy(alpha = 0.7f))
                                ),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(9.dp))

                    Column {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                ) {
                                    append("আমাদের সার্ভিস সমূহ ")
                                }
                                withStyle(
                                    SpanStyle(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFFFE57F), Color(0xFFFFB300))
                                        ),
                                        fontWeight = FontWeight.Black
                                    )
                                ) {
                                    append("(Our Services)")
                                }
                            },
                            fontSize = 15.5.sp
                        )
                    }
                }

                // Live Active Badge
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF0A281E).copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
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
                            text = "INSTANT ACTIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF86EFAC),
                            letterSpacing = 0.4.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 4. Premium Service Hub Cards Grid with Official Brand Logos
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Row 1: TikTok Coins + Chamet Diamond
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    PremiumServiceHubCard(
                        title = "TikTok Coins",
                        subtitle = "Coin Package (৳180 - ৳9,000)",
                        rateHighlight = "100 Coins = ৳180",
                        badge = "Auto Calc",
                        primaryAccent = Color(0xFF00F2FE),
                        secondaryAccent = GoldPrimary,
                        cardGradient = listOf(
                            Color(0xFF17133A),
                            Color(0xFF0D0A24),
                            Color(0xFF09071A)
                        ),
                        logoContent = {
                            TikTokReferenceSquircle(size = 46.dp)
                        },
                        onClick = { onNavigateTab(AppNavTab.TIKTOK) },
                        testTag = "service_card_tiktok",
                        modifier = Modifier.weight(1f)
                    )

                    PremiumServiceHubCard(
                        title = "Chamet Diamond",
                        subtitle = "Diamond Package (৳360 - ৳11,350)",
                        rateHighlight = "15,000 💎 = ৳360",
                        badge = "VIP Rates",
                        primaryAccent = Color(0xFFFF9100),
                        secondaryAccent = NeonMagenta,
                        cardGradient = listOf(
                            Color(0xFF281236),
                            Color(0xFF140A24),
                            Color(0xFF0B0618)
                        ),
                        logoContent = {
                            ChametReferenceSquircle(size = 46.dp)
                        },
                        onClick = { onNavigateTab(AppNavTab.CHAMET) },
                        testTag = "service_card_chamet",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: EPAY Dollar + Payment Exchange
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    PremiumServiceHubCard(
                        title = "EPAY Dollar",
                        subtitle = "Dollar ➔ bKash / Nagad",
                        rateHighlight = "$1 USD = ৳121-123",
                        badge = "Live Rate",
                        primaryAccent = Color(0xFF0099FF),
                        secondaryAccent = Color(0xFF28E040),
                        cardGradient = listOf(
                            Color(0xFF0E1D42),
                            Color(0xFF0A122C),
                            Color(0xFF070B1E)
                        ),
                        logoContent = {
                            EpayReferenceSquircle(size = 46.dp)
                        },
                        onClick = { onNavigateTab(AppNavTab.EPAY) },
                        testTag = "service_card_epay",
                        modifier = Modifier.weight(1f)
                    )

                    PremiumServiceHubCard(
                        title = "Payment Exchange",
                        subtitle = "BD 🇧🇩 ➔ India 🇮🇳 (PhonePe)",
                        rateHighlight = "bKash/Nagad ➔ UPI",
                        badge = "Fast UPI",
                        primaryAccent = Color(0xFF22C55E),
                        secondaryAccent = GoldPrimary,
                        cardGradient = listOf(
                            Color(0xFF0D282A),
                            Color(0xFF0A1726),
                            Color(0xFF070C1A)
                        ),
                        logoContent = {
                            ExchangeReferenceSquircle(size = 46.dp)
                        },
                        onClick = { onNavigateTab(AppNavTab.EXCHANGE) },
                        testTag = "service_card_exchange",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Quick Rate Calculator + 24/7 Support Hub Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    PremiumServiceHubCard(
                        title = "Rate Calculator",
                        subtitle = "Coin, Diamond & USD Calc",
                        rateHighlight = "Instant BDT Quote",
                        badge = "Smart Tool",
                        primaryAccent = Color(0xFFFFB300),
                        secondaryAccent = Color(0xFFA855F7),
                        cardGradient = listOf(
                            Color(0xFF261B14),
                            Color(0xFF140E22),
                            Color(0xFF090716)
                        ),
                        logoContent = {
                            CalculateReferenceSquircle(size = 46.dp)
                        },
                        onClick = { onNavigateTab(AppNavTab.CALCULATOR) },
                        testTag = "service_card_calculator",
                        modifier = Modifier.weight(1f)
                    )

                    PremiumServiceHubCard(
                        title = "Orders & Support",
                        subtitle = "Track Order & Live Help",
                        rateHighlight = "WhatsApp & Telegram",
                        badge = "24/7 Live",
                        primaryAccent = Color(0xFFD946EF),
                        secondaryAccent = Color(0xFF8B5CF6),
                        cardGradient = listOf(
                            Color(0xFF24103E),
                            Color(0xFF120928),
                            Color(0xFF080518)
                        ),
                        logoContent = {
                            SupportReferenceSquircle(size = 46.dp)
                        },
                        onClick = { onNavigateTab(AppNavTab.SUPPORT) },
                        testTag = "service_card_support",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 4: Full-Width Gaming Top-Up (Free Fire & Others) with Official Gaming Squircle Logo
                PremiumGamingFullCard(
                    title = "Gaming Top-Up (Free Fire & Others)",
                    subtitle = "UID Instant Topup • Weekly / Monthly Memberships",
                    badge = "Instant UID",
                    onClick = { onNavigateTab(AppNavTab.FREEFIRE) }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 5. EPAY & Exchange Quick Live Rates Card with Official Brand Logos
        item {
            LiveDollarExchangeCard(
                onCalculateEpayClick = { onNavigateTab(AppNavTab.EPAY) }
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 6. Security & Service Guarantee Badges (Premium Neon Glass Design)
        item {
            ServiceGuaranteesCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Premium Welcome Hero Card with Official Mini Brand Logos inside the Quick Stat Pills.
 */
@Composable
private fun WelcomeFintechHeroCard(
    onNavigateTab: (AppNavTab) -> Unit,
    onAccountClick: () -> Unit = {},
    onNoticeClick: () -> Unit = {},
    onSupportChatClick: () -> Unit = {},
    onAdminPanelClick: () -> Unit = {},
    loggedInUserEmail: String = "",
    loggedInUserName: String = "",
    isAdminLoggedIn: Boolean = false,
    unreadNotificationCount: Int = 0,
    approvedOrderCount: Int = 0
) {
    val cardShape = RoundedCornerShape(22.dp)
    val isUserLoggedIn = loggedInUserEmail.isNotBlank() || isAdminLoggedIn
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = cardShape,
                spotColor = Color(0xFF9333EA).copy(alpha = 0.45f)
            )
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF220E42),
                        Color(0xFF111530),
                        Color(0xFF17092E)
                    )
                )
            )
            .border(
                width = 1.4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GoldPrimary.copy(alpha = 0.75f),
                        NeonMagenta.copy(alpha = 0.65f),
                        Color(0xFF0099FF).copy(alpha = 0.70f)
                    )
                ),
                shape = cardShape
            )
            .drawBehind {
                // Top-right soft magenta-gold ambient glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonMagenta.copy(alpha = 0.20f),
                            GoldPrimary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    radius = size.width * 0.48f,
                    center = Offset(size.width * 0.88f, size.height * 0.18f)
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
                // Fast delivery glowing badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = GoldPrimary.copy(alpha = 0.14f),
                    border = BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(GoldPrimary.copy(alpha = 0.7f), Color(0xFFFF9100).copy(alpha = 0.5f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "2-5 Mins Fast Delivery",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFE57F)
                        )
                    }
                }

                // BD -> IN Verified Exchange Pill
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF0E1D3A).copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇧🇩 ➔ 🇮🇳",
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(11.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    ) {
                        append("Welcome to ")
                    }
                    withStyle(
                        SpanStyle(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFF3B0),
                                    Color(0xFFFFD54F),
                                    Color(0xFFFFAB00)
                                )
                            ),
                            fontWeight = FontWeight.Black
                        )
                    ) {
                        append("CHAMET TOP UP BD")
                    }
                },
                fontSize = 17.5.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "TikTok Coin, Chamet Diamond, Gaming Top-up, EPAY Dollar Exchange এবং Bangladesh → India Payment Exchange এর বিশ্বস্ত প্ল্যাটফর্ম।",
                fontSize = 11.5.sp,
                color = Color(0xFFD8B4FE).copy(alpha = 0.9f),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Action Row: Login / Sign Up Button + Order Approval Notifications Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Login / Sign Up OR Logged-In Account Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onAccountClick() }
                        .testTag("home_login_signup_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isUserLoggedIn) Color(0xFF0A2419) else Color(0xFF1D0D3B),
                    border = BorderStroke(
                        1.2.dp,
                        Brush.horizontalGradient(
                            if (isUserLoggedIn) {
                                listOf(Color(0xFF34D399), GoldPrimary)
                            } else {
                                listOf(GoldPrimary, NeonMagenta)
                            }
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    if (isUserLoggedIn) Color(0xFF10B981).copy(alpha = 0.22f)
                                    else GoldPrimary.copy(alpha = 0.18f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isUserLoggedIn) Icons.Default.Person else Icons.Default.PersonAdd,
                                contentDescription = "Login or Sign Up",
                                tint = if (isUserLoggedIn) Color(0xFF4ADE80) else GoldPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isUserLoggedIn) {
                                    loggedInUserName.ifBlank { loggedInUserEmail.substringBefore("@") }
                                } else {
                                    "লগইন / সাইন আপ"
                                },
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isUserLoggedIn) "Logged In • প্রোফাইল দেখুন" else "Login & Create Account",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isUserLoggedIn) Color(0xFF86EFAC) else GoldLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // 2. Live Order Approval & Status Notifications Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNoticeClick() }
                        .testTag("home_order_notifications_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF160B30),
                    border = BorderStroke(
                        1.2.dp,
                        Brush.horizontalGradient(
                            if (unreadNotificationCount > 0) {
                                listOf(Color(0xFF34D399), Color(0xFF38BDF8))
                            } else {
                                listOf(Color(0xFF38BDF8).copy(alpha = 0.8f), NeonMagenta.copy(alpha = 0.7f))
                            }
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Color(0xFF38BDF8).copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Order Notifications",
                                tint = if (unreadNotificationCount > 0) Color(0xFF4ADE80) else Color(0xFF38BDF8),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "অর্ডার নোটিফিকেশন",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (unreadNotificationCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFFEF4444))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "$unreadNotificationCount",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (approvedOrderCount > 0) {
                                    "✅ $approvedOrderCount টি অর্ডার Approved"
                                } else {
                                    "এপ্রুভ ও স্ট্যাটাস দেখুন"
                                },
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (approvedOrderCount > 0) Color(0xFF4ADE80) else Color(0xFFBAE6FD),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Simple Support Chat Button (Sends messages to Firebase Firestore backend)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .clickable { onSupportChatClick() }
                    .testTag("home_support_chat_button"),
                shape = RoundedCornerShape(13.dp),
                color = Color(0xFF140B2D),
                border = BorderStroke(
                    1.1.dp,
                    Brush.horizontalGradient(
                        listOf(GoldPrimary.copy(alpha = 0.85f), Color(0xFF34D399).copy(alpha = 0.75f))
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💬", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "লাইভ সাপোর্ট চ্যাট (Support Chat)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "সরাসরি অ্যাপ থেকে সাপোর্ট টিমে মেসেজ পাঠান",
                                fontSize = 9.5.sp,
                                color = GoldLight
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldPrimary
                    ) {
                        Text(
                            text = "মেসেজ দিন",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Service Stats Grid in Hero with Official Mini Logos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeroBrandStatPill(
                    title = "TikTok Coins",
                    subtitle = "From ৳180",
                    borderColor = Color(0xFF00F2FE),
                    logo = { TikTokReferenceSquircle(size = 22.dp) },
                    onClick = { onNavigateTab(AppNavTab.TIKTOK) },
                    modifier = Modifier.weight(1f)
                )
                HeroBrandStatPill(
                    title = "Chamet VIP",
                    subtitle = "15K ৳360",
                    borderColor = Color(0xFFFF9100),
                    logo = { ChametReferenceSquircle(size = 22.dp) },
                    onClick = { onNavigateTab(AppNavTab.CHAMET) },
                    modifier = Modifier.weight(1f)
                )
                HeroBrandStatPill(
                    title = "EPAY USD",
                    subtitle = "৳121-123",
                    borderColor = Color(0xFF0099FF),
                    logo = { EpayReferenceSquircle(size = 22.dp) },
                    onClick = { onNavigateTab(AppNavTab.EPAY) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeroBrandStatPill(
    title: String,
    subtitle: String,
    borderColor: Color,
    logo: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(13.dp),
        color = Color(0xFF0C071D).copy(alpha = 0.88f),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(borderColor.copy(alpha = 0.6f), Color(0xFF4C1D95).copy(alpha = 0.5f))
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            logo()
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldPrimary,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Premium Neon Glassmorphism Service Hub Card with Official Brand Squircle Logo,
 * Rate Highlight Pill, and Interactive Action Footer.
 */
@Composable
private fun PremiumServiceHubCard(
    title: String,
    subtitle: String,
    rateHighlight: String,
    badge: String,
    primaryAccent: Color,
    secondaryAccent: Color,
    cardGradient: List<Color>,
    logoContent: @Composable () -> Unit,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = cardShape,
                spotColor = primaryAccent.copy(alpha = 0.45f)
            )
            .clip(cardShape)
            .background(
                brush = Brush.linearGradient(
                    colors = cardGradient,
                    start = Offset(0f, 0f),
                    end = Offset(300f, 360f)
                )
            )
            .border(
                width = 1.4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        primaryAccent.copy(alpha = 0.80f),
                        secondaryAccent.copy(alpha = 0.55f),
                        primaryAccent.copy(alpha = 0.30f)
                    )
                ),
                shape = cardShape
            )
            .drawBehind {
                // Ambient corner neon glow behind the logo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryAccent.copy(alpha = 0.22f),
                            secondaryAccent.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    radius = size.width * 0.55f,
                    center = Offset(size.width * 0.20f, size.height * 0.22f)
                )
                // Subtle inner glass rim highlight
                val inset = 2.5.dp.toPx()
                val cr = 20.dp.toPx() - inset
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.Transparent,
                            primaryAccent.copy(alpha = 0.12f)
                        )
                    ),
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2, size.height - inset * 2),
                    cornerRadius = CornerRadius(cr, cr),
                    style = Stroke(width = 0.8.dp.toPx())
                )
            }
            .testTag(testTag)
            .clickable { onClick() }
            .padding(13.dp)
    ) {
        Column {
            // Top Row: Official Brand Logo Squircle + Glowing Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Official Logo with subtle halo
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    logoContent()
                }

                // Neon Glass Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = primaryAccent.copy(alpha = 0.16f),
                    border = BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(primaryAccent.copy(alpha = 0.8f), secondaryAccent.copy(alpha = 0.6f))
                        )
                    )
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(11.dp))

            // Service Title
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Service Subtitle
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFCBD5E1),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(9.dp))

            // Bottom Rate Highlight Pill + Action Arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF070514).copy(alpha = 0.72f))
                    .border(
                        width = 0.9.dp,
                        color = primaryAccent.copy(alpha = 0.32f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rateHighlight,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(primaryAccent.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Full-Width Premium Gaming Top-Up Card with Official Free Fire Flame & Diamond Squircle Logo.
 */
@Composable
private fun PremiumGamingFullCard(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)
    val accentOrange = Color(0xFFFF6D00)
    val accentGold = Color(0xFFFFD54F)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = cardShape,
                spotColor = accentOrange.copy(alpha = 0.45f)
            )
            .clip(cardShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2B101E),
                        Color(0xFF180C29),
                        Color(0xFF240E1C)
                    )
                )
            )
            .border(
                width = 1.4.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentGold.copy(alpha = 0.85f),
                        accentOrange.copy(alpha = 0.75f),
                        Color(0xFFF43F5E).copy(alpha = 0.70f)
                    )
                ),
                shape = cardShape
            )
            .testTag("service_card_freefire")
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Official Free Fire Flame & Diamond Squircle Logo
            FreeFireGamingSquircle(size = 50.dp)

            Spacer(modifier = Modifier.width(13.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = accentOrange.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, accentGold.copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentGold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = Color(0xFFE2E8F0),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "💎 25 ➔ ৳25  •  115 ➔ ৳85  •  Weekly ➔ ৳165",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentGold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Glowing CTA Arrow Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFFD54F), Color(0xFFFF6D00))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF14080E),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Live Dollar & Exchange Rates Card featuring Official EPAY & Exchange Squircle Logos.
 */
@Composable
private fun LiveDollarExchangeCard(
    onCalculateEpayClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = cardShape,
                spotColor = BlueEpay.copy(alpha = 0.4f)
            )
            .clip(cardShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF121A3A),
                        Color(0xFF0C1026),
                        Color(0xFF100A22)
                    )
                )
            )
            .border(
                width = 1.4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0099FF).copy(alpha = 0.8f),
                        Color(0xFF28E040).copy(alpha = 0.55f),
                        Color(0xFF7C3AED).copy(alpha = 0.65f)
                    )
                ),
                shape = cardShape
            )
            .padding(16.dp)
    ) {
        Column {
            // Header Row with Official EPAY & Exchange Logos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EpayReferenceSquircle(size = 34.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    ExchangeReferenceSquircle(size = 34.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Live Dollar & Exchange Rates",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Official EPAY USD ➔ bKash / Nagad",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF93C5FD)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = GoldPrimary.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.65f))
                ) {
                    Text(
                        text = "TODAY'S RATE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Glassmorphic Rate Rows
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                RateRowItem("9.70 USD Fix", "➔", "৳1,170 BDT", GoldPrimary)
                RateRowItem("10.00 USD Fix", "➔", "৳1,200 BDT", GoldPrimary)
                RateRowItem("20.00 USD", "➔", "৳121 / USD", Color(0xFF38BDF8))
                RateRowItem("30+ USD (Bulk)", "➔", "৳122 / USD", Color(0xFF4ADE80))
                RateRowItem("500+ USD (Wholesale)", "➔", "৳123 / USD", Color(0xFF4ADE80))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Premium Gradient Button with Official EPAY Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Color(0xFF0099FF)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0066FF),
                                Color(0xFF0099FF),
                                Color(0xFF00C853)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color(0xFF69F0AE).copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onCalculateEpayClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    EpayReferenceSquircle(size = 24.dp)
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = "ডলার ভাঙাতে ক্যালকুলেট করুন",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RateRowItem(usd: String, arrow: String, bdt: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF080C1E).copy(alpha = 0.75f))
            .border(0.8.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
            .padding(horizontal = 11.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Mini glowing USD Coin Badge
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF28E040).copy(alpha = 0.3f), Color(0xFF0066FF).copy(alpha = 0.2f))
                        )
                    )
                    .border(1.dp, Color(0xFF28E040).copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4ADE80)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = usd,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = arrow,
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = bdt,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

/**
 * Security & Service Guarantee Card with Glowing Neon Vector Badges.
 */
@Composable
private fun ServiceGuaranteesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF110822),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(
                    GoldPrimary.copy(alpha = 0.55f),
                    NeonMagenta.copy(alpha = 0.45f),
                    Color(0xFF0099FF).copy(alpha = 0.55f)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "আমাদের সেবার নিশ্চয়তা (Our Guarantees)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldLight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GuaranteePill(
                    icon = Icons.Default.Bolt,
                    accentColor = GoldPrimary,
                    title = "দ্রুত ডেলিভারি",
                    subtitle = "২-৫ মিনিটে সম্পন্ন",
                    modifier = Modifier.weight(1f)
                )
                GuaranteePill(
                    icon = Icons.Default.Shield,
                    accentColor = Color(0xFF22C55E),
                    title = "নিরাপদ পেমেন্ট",
                    subtitle = "১০০% সিকিউরড ডিল",
                    modifier = Modifier.weight(1f)
                )
                GuaranteePill(
                    icon = Icons.Default.SupportAgent,
                    accentColor = Color(0xFFD946EF),
                    title = "২৪/৭ সাপোর্ট",
                    subtitle = "হোয়াটসঅ্যাপ ও টেলিগ্রাম",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GuaranteePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF090516))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.16f))
                .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            maxLines = 1
        )
        Text(
            text = subtitle,
            fontSize = 8.5.sp,
            color = Color(0xFF94A3B8),
            maxLines = 1
        )
    }
}
