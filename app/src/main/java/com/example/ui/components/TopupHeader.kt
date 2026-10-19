package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import java.util.Locale
import com.example.data.local.TopupRepository
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import kotlinx.coroutines.delay

@Composable
fun TopupHeader(
    onNoticeClick: () -> Unit,
    onPaymentAccountsClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onAdminClick: () -> Unit = {},
    onAccountClick: () -> Unit = onAdminClick,
    loggedInUserEmail: String = "",
    loggedInUserName: String = "",
    loggedInUserPhotoUri: String = "",
    userWalletBalance: Double = 0.0,
    isAdminLoggedIn: Boolean = false,
    storeOverride: String = "AUTO",
    promoBanners: List<com.example.data.model.PromoBanner> = emptyList(),
    onNavigateTab: (com.example.ui.viewmodel.AppNavTab) -> Unit = {},
    unreadNotificationCount: Int = 0,
    showBannerCarousel: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isStoreOpen by remember { mutableStateOf(TopupRepository.isStoreOpen()) }

    LaunchedEffect(storeOverride) {
        while (true) {
            isStoreOpen = when (storeOverride) {
                "OPEN" -> true
                "CLOSED" -> false
                else -> TopupRepository.isStoreOpen()
            }
            delay(30000)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF070318),
                            Color(0xFF12062C),
                            Color(0xFF09041A)
                        )
                    )
                )
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    // Center purple/pink radial aura behind the "J" logo & title
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFA855F7).copy(alpha = 0.28f),
                                Color(0xFFEC4899).copy(alpha = 0.14f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.5f, h * 0.34f),
                            radius = w * 0.46f
                        ),
                        center = Offset(w * 0.5f, h * 0.34f),
                        radius = w * 0.46f
                    )

                    // Left diagonal magenta-purple neon ribbon wave (matching reference image)
                    val leftRibbon = Path().apply {
                        moveTo(0f, h * 0.25f)
                        cubicTo(
                            w * 0.12f, h * 0.32f,
                            w * 0.22f, h * 0.48f,
                            w * 0.34f, h * 0.62f
                        )
                        lineTo(0f, h * 0.66f)
                        close()
                    }
                    drawPath(
                        path = leftRibbon,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF2CF3).copy(alpha = 0.26f),
                                Color(0xFF7C3AED).copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            start = Offset(0f, h * 0.25f),
                            end = Offset(w * 0.35f, h * 0.62f)
                        )
                    )

                    // Right diagonal electric-blue & purple neon ribbon wave (matching reference image)
                    val rightRibbon = Path().apply {
                        moveTo(w, h * 0.24f)
                        cubicTo(
                            w * 0.88f, h * 0.32f,
                            w * 0.78f, h * 0.48f,
                            w * 0.66f, h * 0.62f
                        )
                        lineTo(w, h * 0.66f)
                        close()
                    }
                    drawPath(
                        path = rightRibbon,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00B4FF).copy(alpha = 0.24f),
                                Color(0xFFA855F7).copy(alpha = 0.14f),
                                Color.Transparent
                            ),
                            start = Offset(w, h * 0.24f),
                            end = Offset(w * 0.65f, h * 0.62f)
                        )
                    )
                }
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. TOP ROW: Status Badge (Left) + Crowned "J" Brand Logo (Center) + Notification & Profile Icons (Right)
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // LEFT: Clean Premium Glassmorphism Status Badge ("OPEN NOW / 9:00 AM - 10:00 PM")
                    val statusBorderColor = if (isStoreOpen) Color(0xFF10B981) else WarningYellow
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(22.dp),
                                spotColor = statusBorderColor
                            )
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { onPaymentAccountsClick() },
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xCC061A17),
                        border = BorderStroke(
                            1.1.dp,
                            Brush.horizontalGradient(
                                colors = if (isStoreOpen) {
                                    listOf(
                                        Color(0xFF34D399).copy(alpha = 0.8f),
                                        Color(0xFF059669).copy(alpha = 0.5f)
                                    )
                                } else {
                                    listOf(
                                        WarningYellow.copy(alpha = 0.8f),
                                        WarningYellow.copy(alpha = 0.4f)
                                    )
                                }
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Crisp Alarm Clock Vector Icon
                            AlarmClockGlassIcon(
                                modifier = Modifier.size(18.dp),
                                tint = Color.White.copy(alpha = 0.92f)
                            )

                            Spacer(modifier = Modifier.width(7.dp))

                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Glowing green dot
                                    Box(
                                        modifier = Modifier
                                            .size(6.5.dp)
                                            .shadow(4.dp, CircleShape, spotColor = SuccessGreen)
                                            .clip(CircleShape)
                                            .background(if (isStoreOpen) Color(0xFF34D399) else WarningYellow)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isStoreOpen) "OPEN NOW" else "CLOSED NOW",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isStoreOpen) Color(0xFF34D399) else WarningYellow,
                                        letterSpacing = 0.4.sp,
                                        lineHeight = 11.sp
                                    )
                                }
                                Text(
                                    text = "9:00 AM - 10:00 PM",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 10.sp
                                )
                            }
                        }
                    }

                    // CENTER: Official Chamet Top Up BD Logo (Circular emblem with neon gold + purple glow)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 2.dp)
                            .size(56.dp)
                            .drawBehind {
                                // Outer purple + pink + gold neon glow ring
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            GoldPrimary.copy(alpha = 0.45f),
                                            Color(0xFFEC4899).copy(alpha = 0.42f),
                                            Color(0xFFA855F7).copy(alpha = 0.28f),
                                            Color.Transparent
                                        )
                                    ),
                                    radius = size.width * 0.68f
                                )
                                // Outer subtle purple neon ring
                                drawCircle(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFF47F8).copy(alpha = 0.7f),
                                            Color(0xFFA855F7).copy(alpha = 0.5f),
                                            GoldPrimary.copy(alpha = 0.6f)
                                        )
                                    ),
                                    radius = size.width * 0.51f,
                                    style = Stroke(width = 1.5.dp.toPx())
                                )
                            }
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF05010D))
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFF08A),
                                        GoldPrimary,
                                        Color(0xFFD97706),
                                        GoldLight
                                    )
                                ),
                                shape = CircleShape
                            )
                            .clickable { onAdminClick() },
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

                    // RIGHT: Notification Bell (with Red Badge) & Glowing Login / Sign Up / Profile Pill
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Notification Bell Icon with Red Badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(
                                    elevation = if (unreadNotificationCount > 0) 6.dp else 2.dp,
                                    shape = CircleShape,
                                    spotColor = if (unreadNotificationCount > 0) Color(0xFFEF4444) else NeonPurple
                                )
                                .clip(CircleShape)
                                .background(Color(0xFF15092C))
                                .border(
                                    width = 1.1.dp,
                                    brush = Brush.linearGradient(
                                        colors = if (unreadNotificationCount > 0) {
                                            listOf(Color(0xFFF87171), GoldPrimary)
                                        } else {
                                            listOf(Color(0xFFA855F7).copy(alpha = 0.7f), GoldPrimary.copy(alpha = 0.5f))
                                        }
                                    ),
                                    shape = CircleShape
                                )
                                .clickable { onNoticeClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Order Notifications",
                                tint = if (unreadNotificationCount > 0) GoldPrimary else Color.White,
                                modifier = Modifier.size(21.dp)
                            )
                            if (unreadNotificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 1.dp, end = 1.dp)
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                        .border(1.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        lineHeight = 8.5.sp
                                    )
                                }
                            }
                        }

                        // Login / Sign Up / Profile Pill Button (No visible Admin text)
                        val isUserLoggedIn = loggedInUserEmail.isNotBlank() || isAdminLoggedIn
                        val accountLabel = if (isUserLoggedIn) {
                            val shortName = loggedInUserName.ifBlank { loggedInUserEmail.substringBefore("@") }
                            if (shortName.length > 8) shortName.take(8) else shortName
                        } else {
                            "লগইন"
                        }
                        val accountSubLabel = if (isUserLoggedIn) {
                            "৳${String.format(Locale.US, "%.0f", userWalletBalance)} • প্রোফাইল"
                        } else {
                            "সাইন আপ"
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isAdminLoggedIn) Color(0xFF0A261B) else Color(0xFF170932),
                            border = BorderStroke(
                                1.1.dp,
                                Brush.horizontalGradient(
                                    colors = if (isAdminLoggedIn) {
                                        listOf(Color(0xFF34D399), GoldPrimary)
                                    } else if (isUserLoggedIn) {
                                        listOf(Color(0xFF38BDF8), Color(0xFFA855F7))
                                    } else {
                                        listOf(GoldPrimary.copy(alpha = 0.9f), NeonMagenta.copy(alpha = 0.8f))
                                    }
                                )
                            ),
                            modifier = Modifier
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = if (isAdminLoggedIn) SuccessGreen else NeonPurple
                                )
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onAccountClick() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 4.dp, end = 8.dp, top = 3.dp, bottom = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFF4C1D95),
                                                    Color(0xFF1E0B3B),
                                                    Color(0xFF0E0520)
                                                )
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF34D399).copy(alpha = 0.85f),
                                                    GoldPrimary.copy(alpha = 0.85f)
                                                )
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isUserLoggedIn && loggedInUserPhotoUri.isNotBlank()) {
                                        AsyncImage(
                                            model = loggedInUserPhotoUri,
                                            contentDescription = "Profile Photo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        NeonProfileAvatarGlyph(modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                Column(
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = accountLabel,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isAdminLoggedIn) Color(0xFF4ADE80) else GoldLight,
                                        lineHeight = 10.sp
                                    )
                                    Text(
                                        text = accountSubLabel,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 8.5.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2. BRAND NAME: "Chamet Top Up BD" (Bold modern white + gold gradient with subtle glow)
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                shadow = Shadow(
                                    color = Color(0xFFA855F7).copy(alpha = 0.55f),
                                    offset = Offset(0f, 2f),
                                    blurRadius = 10f
                                )
                            )
                        ) {
                            append("Chamet Top Up ")
                        }
                        withStyle(
                            SpanStyle(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFF3A1),
                                        Color(0xFFFFD54F),
                                        Color(0xFFF59E0B)
                                    )
                                ),
                                shadow = Shadow(
                                    color = GoldPrimary.copy(alpha = 0.6f),
                                    offset = Offset(0f, 2f),
                                    blurRadius = 10f
                                )
                            )
                        ) {
                            append("BD")
                        }
                    },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.7.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                // 3. SUBTITLE: "TRUSTED RATE • FAST PAYMENT • SECURE DEAL" with fading side lines
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.35f)
                                    )
                                )
                            )
                    )
                    Text(
                        text = "TRUSTED RATE  •  FAST PAYMENT  •  SECURE DEAL",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0),
                        letterSpacing = 0.4.sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))

                // 4. WHATSAPP + TELEGRAM: Equal height & equal width neon glass buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PremiumWhatsAppChip(
                        text = "WhatsApp Direct",
                        onClick = onWhatsAppClick,
                        modifier = Modifier.weight(1f)
                    )

                    PremiumTelegramChip(
                        text = "Telegram Channel",
                        onClick = onTelegramClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (showBannerCarousel && promoBanners.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    AutoRotatingBannerCarousel(
                        banners = promoBanners,
                        onBannerClick = {},
                        onNavigateTab = onNavigateTab
                    )
                }
            }
        }
    }
}

/**
 * Renders the existing JOHIRUL TOPUP BD "J" emblem with its golden crown above the stem,
 * matching the reference image.
 */
@Composable
private fun CrownedJBrandEmblem(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val goldBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFF3A1),
                Color(0xFFFFCC15),
                Color(0xFFD97706)
            )
        )

        // 1. Small 3-point crown perched above the right stem of the "J"
        val crownPath = Path().apply {
            moveTo(w * 0.43f, h * 0.27f)
            lineTo(w * 0.40f, h * 0.15f)
            lineTo(w * 0.50f, h * 0.21f)
            lineTo(w * 0.56f, h * 0.11f)
            lineTo(w * 0.62f, h * 0.21f)
            lineTo(w * 0.72f, h * 0.15f)
            lineTo(w * 0.69f, h * 0.27f)
            close()
        }
        drawPath(path = crownPath, brush = goldBrush, style = Fill)

        // Three tiny pearl dots on crown peaks
        val dotR = w * 0.025f
        drawCircle(brush = goldBrush, radius = dotR, center = Offset(w * 0.40f, h * 0.125f))
        drawCircle(brush = goldBrush, radius = dotR, center = Offset(w * 0.56f, h * 0.085f))
        drawCircle(brush = goldBrush, radius = dotR, center = Offset(w * 0.72f, h * 0.125f))

        // 2. Bold sculpted letter "J" below the crown
        val jPath = Path().apply {
            // Top of vertical stem
            moveTo(w * 0.48f, h * 0.31f)
            lineTo(w * 0.66f, h * 0.31f)
            lineTo(w * 0.66f, h * 0.67f)
            // Bottom curve of J
            cubicTo(
                w * 0.66f, h * 0.83f,
                w * 0.54f, h * 0.90f,
                w * 0.41f, h * 0.90f
            )
            cubicTo(
                w * 0.27f, h * 0.90f,
                w * 0.20f, h * 0.81f,
                w * 0.20f, h * 0.69f
            )
            lineTo(w * 0.37f, h * 0.69f)
            cubicTo(
                w * 0.37f, h * 0.74f,
                w * 0.39f, h * 0.76f,
                w * 0.43f, h * 0.76f
            )
            cubicTo(
                w * 0.46f, h * 0.76f,
                w * 0.48f, h * 0.73f,
                w * 0.48f, h * 0.66f
            )
            close()
        }
        drawPath(path = jPath, brush = goldBrush, style = Fill)
    }
}

/**
 * Crisp Alarm Clock icon for the "OPEN NOW" glass badge.
 */
@Composable
private fun AlarmClockGlassIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        // Clock face circle
        drawCircle(
            color = tint,
            radius = w * 0.38f,
            center = Offset(w * 0.50f, h * 0.54f),
            style = Stroke(width = stroke)
        )

        // Top-left & Top-right alarm bell ears
        drawLine(
            color = tint,
            start = Offset(w * 0.14f, h * 0.24f),
            end = Offset(w * 0.30f, h * 0.12f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.70f, h * 0.12f),
            end = Offset(w * 0.86f, h * 0.24f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        // Clock hands (12 o'clock to center to 4 o'clock)
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.34f),
            end = Offset(w * 0.50f, h * 0.54f),
            strokeWidth = stroke * 0.9f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.54f),
            end = Offset(w * 0.64f, h * 0.64f),
            strokeWidth = stroke * 0.9f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Glowing purple-pink profile avatar glyph for the top-right profile button.
 */
@Composable
private fun NeonProfileAvatarGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val avatarBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF472B6),
                Color(0xFFC084FC),
                Color(0xFF9333EA)
            )
        )

        // Head circle
        drawCircle(
            brush = avatarBrush,
            radius = w * 0.22f,
            center = Offset(w * 0.50f, h * 0.36f)
        )

        // Shoulders / torso dome
        val bodyPath = Path().apply {
            moveTo(w * 0.18f, h * 0.86f)
            cubicTo(
                w * 0.20f, h * 0.62f,
                w * 0.80f, h * 0.62f,
                w * 0.82f, h * 0.86f
            )
            close()
        }
        drawPath(path = bodyPath, brush = avatarBrush, style = Fill)
    }
}
