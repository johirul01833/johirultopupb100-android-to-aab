package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.PromoBanner
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.viewmodel.AppNavTab
import kotlinx.coroutines.delay

@Composable
fun AutoRotatingBannerCarousel(
    banners: List<PromoBanner>,
    onBannerClick: (PromoBanner) -> Unit,
    onNavigateTab: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // Ensure EPAY banner is presented first so the hero banner matches the reference design on launch
    val activeBanners = remember(banners) {
        val enabled = banners.filter { it.isEnabled }
        val epayFirst = enabled.sortedByDescending { it.serviceTarget.equals("EPAY", ignoreCase = true) }
        epayFirst
    }
    if (activeBanners.isEmpty()) return

    val context = LocalContext.current
    var currentIndex by remember { mutableIntStateOf(0) }
    var isUserInteracting by remember { mutableStateOf(false) }

    // Auto rotate every 5 seconds
    LaunchedEffect(activeBanners.size, isUserInteracting) {
        while (!isUserInteracting && activeBanners.size > 1) {
            delay(5000)
            currentIndex = (currentIndex + 1) % activeBanners.size
        }
    }

    val safeIndex = currentIndex % activeBanners.size
    val currentBanner = activeBanners.getOrNull(safeIndex) ?: return

    fun handleBannerAction(banner: PromoBanner) {
        if (banner.actionUrl.isNotBlank()) {
            try {
                var url = banner.actionUrl.trim()
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://$url"
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (_: Exception) {
            }
        } else {
            when (banner.serviceTarget.uppercase()) {
                "TIKTOK" -> onNavigateTab(AppNavTab.TIKTOK)
                "CHAMET" -> onNavigateTab(AppNavTab.CHAMET)
                "FREEFIRE" -> onNavigateTab(AppNavTab.FREEFIRE)
                "EPAY" -> onNavigateTab(AppNavTab.EPAY)
                "EXCHANGE" -> onNavigateTab(AppNavTab.EXCHANGE)
                "SOCIAL_BOOST", "SOCIALBOOST" -> onNavigateTab(AppNavTab.SOCIAL_BOOST)
                else -> onBannerClick(banner)
            }
        }
    }

    val bannerShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val cr = 20.dp.toPx()
                    // Outer neon purple/pink/blue glow
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFD946EF).copy(alpha = 0.36f),
                                Color(0xFF3B82F6).copy(alpha = 0.30f),
                                Color(0xFFEC4899).copy(alpha = 0.36f)
                            )
                        ),
                        topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                        size = Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                        cornerRadius = CornerRadius(cr + 2.dp.toPx(), cr + 2.dp.toPx())
                    )
                }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(152.dp)
                    .clip(bannerShape)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFE879F9),
                                    Color(0xFF8B5CF6),
                                    Color(0xFF38BDF8),
                                    Color(0xFFC026D3),
                                    Color(0xFFF472B6)
                                )
                            )
                        ),
                        bannerShape
                    )
                    .pointerInput(activeBanners.size) {
                        detectHorizontalDragGestures(
                            onDragStart = { isUserInteracting = true },
                            onDragEnd = { isUserInteracting = false },
                            onDragCancel = { isUserInteracting = false }
                        ) { change, dragAmount ->
                            change.consume()
                            if (dragAmount < -30) {
                                currentIndex = (currentIndex + 1) % activeBanners.size
                            } else if (dragAmount > 30) {
                                currentIndex = if (currentIndex - 1 < 0) activeBanners.size - 1 else currentIndex - 1
                            }
                        }
                    }
                    .clickable { handleBannerAction(currentBanner) },
                shape = bannerShape,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0A1033),
                                    Color(0xFF16134A),
                                    Color(0xFF280E54),
                                    Color(0xFF0D1D47)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(900f, 400f)
                            )
                        )
                        .drawBehind {
                            val w = size.width
                            val h = size.height

                            // Subtle neon fintech light streaks & diagonal glow inside banner
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF0077FF).copy(alpha = 0.28f),
                                        Color.Transparent
                                    )
                                ),
                                radius = w * 0.42f,
                                center = Offset(w * 0.65f, h * 0.38f)
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF2CF3).copy(alpha = 0.22f),
                                        Color.Transparent
                                    )
                                ),
                                radius = w * 0.38f,
                                center = Offset(w * 0.48f, h * 0.88f)
                            )

                            // Diagonal neon magenta laser line at bottom center
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFFFF2CF3).copy(alpha = 0.65f),
                                        Color(0xFF00E5FF).copy(alpha = 0.45f),
                                        Color.Transparent
                                    )
                                ),
                                start = Offset(w * 0.22f, h * 0.92f),
                                end = Offset(w * 0.78f, h * 0.68f),
                                strokeWidth = 1.6.dp.toPx()
                            )
                        }
                ) {
                    // Custom uploaded background image if provided by admin
                    if (currentBanner.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(currentBanner.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = currentBanner.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(bannerShape)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xE6080B24),
                                            Color(0xB3120934),
                                            Color(0x6606091E)
                                        )
                                    )
                                )
                        )
                    }

                    // Smooth Animated Banner Content
                    AnimatedContent(
                        targetState = currentBanner,
                        transitionSpec = {
                            (slideInHorizontally(animationSpec = tween(320)) { width -> width / 3 } +
                                fadeIn(animationSpec = tween(320))).togetherWith(
                                slideOutHorizontally(animationSpec = tween(280)) { width -> -width / 3 } +
                                    fadeOut(animationSpec = tween(240))
                            )
                        },
                        label = "HeroBannerSlideAnimation"
                    ) { banner ->
                        val isEpayBanner = banner.serviceTarget.equals("EPAY", ignoreCase = true)

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 18.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // LEFT COLUMN: Badge + Heading + Subtitle + CTA Button
                            Column(
                                modifier = Modifier
                                    .weight(1.32f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start
                            ) {
                                // 1. Top Pill Badge: "🔥 Best Exchange Rate"
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xCC0A1332),
                                    border = BorderStroke(
                                        1.dp,
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                GoldPrimary.copy(alpha = 0.7f),
                                                Color(0xFF38BDF8).copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🔥",
                                            fontSize = 10.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isEpayBanner) {
                                                "Best Exchange Rate"
                                            } else {
                                                banner.badge.replace("🔥", "").trim().ifBlank { "Special Offer" }
                                            },
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFDE047),
                                            maxLines = 1
                                        )
                                    }
                                }

                                // 2. Main Heading & Clean Subtitle
                                Column {
                                    if (isEpayBanner) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(color = Color(0xFF8CF51E), fontWeight = FontWeight.Black)) {
                                                    append("E")
                                                }
                                                withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                                                    append("PAY Dollar → BDT")
                                                }
                                            },
                                            fontSize = 18.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Fast  •  Secure  •  Trusted Exchange",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE2E8F0),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    } else {
                                        Text(
                                            text = banner.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = banner.subtitle,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFE2E8F0),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // 3. Golden-Yellow Pill Button: "Exchange Now →"
                                Surface(
                                    modifier = Modifier
                                        .shadow(8.dp, RoundedCornerShape(50), spotColor = GoldPrimary)
                                        .clip(RoundedCornerShape(50))
                                        .clickable { handleBannerAction(banner) },
                                    shape = RoundedCornerShape(50),
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, Color(0xFFFFF7B2))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFFFFE44D),
                                                        Color(0xFFFACC15),
                                                        Color(0xFFEAB308)
                                                    )
                                                )
                                            )
                                            .padding(horizontal = 14.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = when (banner.serviceTarget.uppercase()) {
                                                "EPAY" -> "Exchange Now  →"
                                                "TIKTOK" -> "Buy Coins Now  →"
                                                "CHAMET" -> "Top-Up Now  →"
                                                "EXCHANGE" -> "Exchange Now  →"
                                                else -> "Order Now  →"
                                            },
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF140C1C),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // RIGHT COLUMN: Official Brand Logo + Fintech Currency Artwork
                            Box(
                                modifier = Modifier
                                    .weight(0.95f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                when (banner.serviceTarget.uppercase()) {
                                    "EPAY" -> EpayHeroBannerRightArtwork()
                                    "TIKTOK" -> BrandHeroBannerRightArtwork(
                                        label = "TikTok",
                                        logoContent = { TikTokReferenceSquircle(size = 54.dp) }
                                    )
                                    "CHAMET" -> BrandHeroBannerRightArtwork(
                                        label = "Chamet",
                                        logoContent = { ChametReferenceSquircle(size = 54.dp) }
                                    )
                                    else -> EpayHeroBannerRightArtwork()
                                }
                            }
                        }
                    }

                    // Subtle Left `<` and Right `>` Side Slider Chevrons (matching reference image)
                    if (activeBanners.size > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(16.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable {
                                    currentIndex = if (currentIndex - 1 < 0) activeBanners.size - 1 else currentIndex - 1
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "‹",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(16.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable {
                                    currentIndex = (currentIndex + 1) % activeBanners.size
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "›",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }

        // Pagination Dots Pill Tray underneath the Hero Banner (matching reference image)
        if (activeBanners.size > 1) {
            Row(
                modifier = Modifier
                    .offset(y = (-5).dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xEE08091C))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFD946EF).copy(alpha = 0.45f),
                                Color(0xFF38BDF8).copy(alpha = 0.45f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                activeBanners.forEachIndexed { idx, _ ->
                    val isSelected = idx == safeIndex
                    val dotSize by animateDpAsState(
                        targetValue = if (isSelected) 8.5.dp else 5.5.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "banner_dot_size"
                    )

                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .then(
                                if (isSelected) {
                                    Modifier.shadow(6.dp, CircleShape, spotColor = Color(0xFFFF2CF3))
                                } else {
                                    Modifier
                                }
                            )
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFF7AF6),
                                            Color(0xFFF43F5E),
                                            Color(0xFFD946EF)
                                        )
                                    )
                                } else {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF64748B).copy(alpha = 0.8f),
                                            Color(0xFF475569).copy(alpha = 0.6f)
                                        )
                                    )
                                }
                            )
                            .clickable { currentIndex = idx }
                    )
                }
            }
        }
    }
}

/**
 * Right-side visual for the EPAY Dollar -> BDT Hero Banner, matching the reference image:
 * - Official EPAY logo squircle + "EPAY" label on the left-center
 * - Angled USD/BDT banknotes + curved neon-green arrow + glowing cyan BDT "৳" circle on the right
 */
@Composable
private fun EpayHeroBannerRightArtwork() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Official EPAY Logo + "EPAY" text (top-left of right pane)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EpayReferenceSquircle(
                size = 52.dp,
                modifier = Modifier.shadow(12.dp, RoundedCornerShape(15.dp), spotColor = Color(0xFF0088FF))
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "EPAY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }

        // 2. USD Banknotes + Green Exchange Arrow + Glowing BDT (৳) Coin on the right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(width = 96.dp, height = 115.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // A. Angled USD Dollar Bill at top-right
                rotate(degrees = 18f, pivot = Offset(w * 0.65f, h * 0.34f)) {
                    val billW = w * 0.58f
                    val billH = h * 0.28f
                    val billLeft = w * 0.34f
                    val billTop = h * 0.14f

                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFCBD5E1),
                                Color(0xFF86EFAC),
                                Color(0xFF34D399)
                            )
                        ),
                        topLeft = Offset(billLeft, billTop),
                        size = Size(billW, billH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFF064E3B).copy(alpha = 0.55f),
                        topLeft = Offset(billLeft + 3.dp.toPx(), billTop + 3.dp.toPx()),
                        size = Size(billW - 6.dp.toPx(), billH - 6.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF065F46).copy(alpha = 0.45f),
                        radius = billH * 0.32f,
                        center = Offset(billLeft + billW * 0.5f, billTop + billH * 0.5f)
                    )
                }

                // B. Angled BDT Banknote below it
                rotate(degrees = -12f, pivot = Offset(w * 0.45f, h * 0.66f)) {
                    val billW = w * 0.54f
                    val billH = h * 0.25f
                    val billLeft = w * 0.10f
                    val billTop = h * 0.54f

                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFDE68A),
                                Color(0xFFF59E0B),
                                Color(0xFFD97706)
                            )
                        ),
                        topLeft = Offset(billLeft, billTop),
                        size = Size(billW, billH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFF78350F).copy(alpha = 0.45f),
                        topLeft = Offset(billLeft + 3.dp.toPx(), billTop + 3.dp.toPx()),
                        size = Size(billW - 6.dp.toPx(), billH - 6.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // C. Glowing Curved Green Exchange Arrow
                val arrowPath = Path().apply {
                    moveTo(w * 0.14f, h * 0.54f)
                    cubicTo(
                        w * 0.16f, h * 0.36f,
                        w * 0.30f, h * 0.28f,
                        w * 0.48f, h * 0.30f
                    )
                }
                drawPath(
                    path = arrowPath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF4ADE80), Color(0xFF22C55E))
                    ),
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )
                // Arrowhead tip
                val headPath = Path().apply {
                    moveTo(w * 0.42f, h * 0.22f)
                    lineTo(w * 0.56f, h * 0.30f)
                    lineTo(w * 0.42f, h * 0.39f)
                    close()
                }
                drawPath(path = headPath, color = Color(0xFF4ADE80), style = Fill)

                // D. Glowing Cyan/Green BDT "৳" Circle Emblem at bottom-right
                val coinCenter = Offset(w * 0.64f, h * 0.72f)
                val coinRadius = w * 0.24f

                // Outer cyan-green neon halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E5FF).copy(alpha = 0.65f),
                            Color(0xFF10B981).copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    ),
                    radius = coinRadius * 1.45f,
                    center = coinCenter
                )

                // Dark teal-emerald circle core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF10B981),
                            Color(0xFF047857),
                            Color(0xFF064E3B)
                        )
                    ),
                    radius = coinRadius,
                    center = coinCenter
                )

                // Glowing cyan-white ring border
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFF00E5FF),
                            Color(0xFF34D399)
                        )
                    ),
                    radius = coinRadius,
                    center = coinCenter,
                    style = Stroke(width = 1.8.dp.toPx())
                )
            }

            // Crisp White "৳" symbol centered on the glowing BDT circle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 19.dp, bottom = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "৳",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun BrandHeroBannerRightArtwork(
    label: String,
    logoContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        logoContent()
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}
