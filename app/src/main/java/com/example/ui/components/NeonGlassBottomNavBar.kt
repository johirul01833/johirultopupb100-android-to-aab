package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppNavTab

// Neon Glassmorphism Palette matching the reference image
private val PillBgTop = Color(0xF508091E)
private val PillBgCenter = Color(0xF8050616)
private val PillBgBottom = Color(0xFA040410)

private val NeonHotPink = Color(0xFFFF2CF3)
private val NeonMagenta = Color(0xFFE028F0)
private val NeonPurple = Color(0xFF9333EA)
private val NeonDeepViolet = Color(0xFF5B21B6)
private val NeonElectricBlue = Color(0xFF1D6BFF)
private val NeonCyanBlue = Color(0xFF00B4FF)

/**
 * Professional Neon Glassmorphism Bottom Navigation Bar for JOHIRUL TOPUP BD.
 * Visually matches the exact reference image:
 * 1. Home (Active squircle card with strong pink/purple glow, white Home icon, label & white pill bar)
 * 2. TikTok (Dark neon-bordered squircle with 3D cyan/pink/white TikTok note)
 * 3. Chamet (Official Chamet logo: white rounded square + pink-purple circle + white wavy mascot ring + purple eye)
 * 4. EPAY (Official EPAY logo: royal-blue squircle + lime-green winged 'E' + ® symbol)
 * 5. Exchange (Dark neon-bordered squircle with curved green top arrow & golden-yellow bottom arrow)
 * 6. Calculate (Dark neon-bordered squircle with golden-yellow calculator & dark screen/keypad cutouts)
 * 7. Support (Dark neon-bordered squircle with glowing purple-pink headset & 3-dot chat bubble)
 */
@Composable
fun NeonGlassBottomNavBar(
    currentTab: AppNavTab,
    onSelectTab: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val navTabs = remember {
        listOf(
            AppNavTab.HOME,
            AppNavTab.TIKTOK,
            AppNavTab.CHAMET,
            AppNavTab.EPAY,
            AppNavTab.EXCHANGE,
            AppNavTab.CALCULATOR,
            AppNavTab.SOCIAL_BOOST
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "neon_pill_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.86f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xCC04040E),
                        Color(0xF803030A)
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 650.dp),
            contentAlignment = Alignment.Center
        ) {
            val isCompact = maxWidth < 365.dp
            val pillCorner = 44.dp
            val pillShape = RoundedCornerShape(pillCorner)

            // Outer Rounded Pill-Shaped Container with Pink/Purple/Blue Neon Tube Border & Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val r = pillCorner.toPx()

                        // Outer soft neon bloom (Hot Pink on left -> Electric Blue in middle -> Violet on right)
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    NeonHotPink.copy(alpha = 0.48f * glowPulse),
                                    NeonPurple.copy(alpha = 0.34f * glowPulse),
                                    NeonElectricBlue.copy(alpha = 0.42f * glowPulse),
                                    NeonElectricBlue.copy(alpha = 0.38f * glowPulse),
                                    NeonPurple.copy(alpha = 0.38f * glowPulse)
                                )
                            ),
                            topLeft = Offset(-3.5.dp.toPx(), -3.dp.toPx()),
                            size = Size(size.width + 7.dp.toPx(), size.height + 6.dp.toPx()),
                            cornerRadius = CornerRadius(r + 4.dp.toPx(), r + 4.dp.toPx())
                        )

                        // Secondary tight neon tube glow
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    NeonHotPink.copy(alpha = 0.78f),
                                    NeonPurple.copy(alpha = 0.65f),
                                    NeonCyanBlue.copy(alpha = 0.72f),
                                    NeonElectricBlue.copy(alpha = 0.72f),
                                    Color(0xFF7C4DFF).copy(alpha = 0.70f)
                                )
                            ),
                            topLeft = Offset(-1.dp.toPx(), -1.dp.toPx()),
                            size = Size(size.width + 2.dp.toPx(), size.height + 2.dp.toPx()),
                            cornerRadius = CornerRadius(r + 1.dp.toPx(), r + 1.dp.toPx()),
                            style = Stroke(width = 2.8.dp.toPx())
                        )
                    }
                    .clip(pillShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PillBgTop,
                                PillBgCenter,
                                PillBgBottom
                            )
                        )
                    )
                    .border(
                        width = 1.8.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF55F8),
                                Color(0xFFD946EF),
                                Color(0xFF7C3AED),
                                Color(0xFF0099FF),
                                Color(0xFF2962FF),
                                Color(0xFF7C4DFF)
                            )
                        ),
                        shape = pillShape
                    )
                    .drawBehind {
                        val r = pillCorner.toPx()
                        val inset = 3.5.dp.toPx()
                        // Inner glassmorphic neon rim line (matching double-rim look in reference image)
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    NeonHotPink.copy(alpha = 0.45f),
                                    NeonPurple.copy(alpha = 0.28f),
                                    NeonCyanBlue.copy(alpha = 0.42f),
                                    NeonElectricBlue.copy(alpha = 0.35f),
                                    NeonPurple.copy(alpha = 0.35f)
                                )
                            ),
                            topLeft = Offset(inset, inset),
                            size = Size(size.width - inset * 2, size.height - inset * 2),
                            cornerRadius = CornerRadius(r - inset, r - inset),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                    .padding(
                        horizontal = if (isCompact) 6.dp else 8.dp,
                        vertical = if (isCompact) 6.dp else 7.dp
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navTabs.forEach { tab ->
                        val isSelected = currentTab == tab
                        val displayLabel = when (tab) {
                            AppNavTab.HOME -> "Home"
                            AppNavTab.TIKTOK -> "TikTok"
                            AppNavTab.CHAMET -> "Chamet"
                            AppNavTab.EPAY -> "EPAY"
                            AppNavTab.EXCHANGE -> "Exchange"
                            AppNavTab.CALCULATOR -> "Calculate"
                            AppNavTab.SOCIAL_BOOST -> "Social Boost"
                            AppNavTab.SUPPORT -> "Support"
                            else -> tab.title
                        }

                        NeonGlassTabItem(
                            tab = tab,
                            label = displayLabel,
                            isSelected = isSelected,
                            isCompact = isCompact,
                            onClick = { onSelectTab(tab) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NeonGlassTabItem(
    tab: AppNavTab,
    label: String,
    isSelected: Boolean,
    isCompact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.91f
            isSelected -> 1.02f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tab_scale"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFFF1F5F9),
        animationSpec = tween(200),
        label = "label_color"
    )

    val squircleSize = if (isCompact) 38.dp else 42.dp
    val activeCardCorner = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .semantics {
                selected = isSelected
                contentDescription = label
            }
            .testTag("bottom_nav_${tab.name.lowercase()}")
            .clip(activeCardCorner)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = NeonHotPink
                ),
                role = Role.Tab,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // ACTIVE TAB: Full glowing Purple/Pink Squircle Card containing Icon + Label + White Pill Indicator
            // (Matches the active Home button on the left of the reference image)
            Box(
                modifier = Modifier
                    .padding(horizontal = 1.dp)
                    .drawBehind {
                        val cr = 16.dp.toPx()
                        // Strong pink/purple outer neon halo
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    NeonHotPink.copy(alpha = 0.65f),
                                    NeonPurple.copy(alpha = 0.38f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.45f, size.height * 0.5f),
                                radius = size.width * 0.85f
                            ),
                            topLeft = Offset(-4.dp.toPx(), -4.dp.toPx()),
                            size = Size(size.width + 8.dp.toPx(), size.height + 8.dp.toPx()),
                            cornerRadius = CornerRadius(cr + 4.dp.toPx(), cr + 4.dp.toPx())
                        )
                    }
                    .clip(activeCardCorner)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF3BF0),
                                Color(0xFFC026D3),
                                Color(0xFF7E22CE),
                                Color(0xFF4C1D95),
                                Color(0xFF9333EA)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(120f, 160f)
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF99F8),
                                Color(0xFFFF3BF0),
                                Color(0xFFA855F7),
                                Color(0xFFFF66F5)
                            )
                        ),
                        shape = activeCardCorner
                    )
                    .padding(
                        horizontal = if (isCompact) 4.dp else 6.dp,
                        vertical = if (isCompact) 5.dp else 6.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (tab == AppNavTab.HOME) {
                        HomeActiveWhiteIcon(size = if (isCompact) 24.dp else 26.dp)
                    } else {
                        NavTabSquircleIcon(
                            tab = tab,
                            isSelected = true,
                            boxSize = if (isCompact) 28.dp else 30.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = label,
                        fontSize = if (label.length > 9) {
                            if (isCompact) 8.sp else 9.sp
                        } else {
                            if (isCompact) 9.sp else 10.sp
                        },
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Visible,
                        textAlign = TextAlign.Center,
                        letterSpacing = if (label.length > 9) (-0.3).sp else 0.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Small glowing white pill indicator inside the bottom of the active squircle
                    Box(
                        modifier = Modifier
                            .width(if (isCompact) 20.dp else 24.dp)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        } else {
            // INACTIVE TAB: Rounded-square (squircle) icon container on top + Crisp white label below
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                NavTabSquircleIcon(
                    tab = tab,
                    isSelected = false,
                    boxSize = squircleSize
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = label,
                    fontSize = if (label.length > 9) {
                        if (isCompact) 8.sp else 9.sp
                    } else {
                        if (isCompact) 9.sp else 10.sp
                    },
                    fontWeight = FontWeight.Bold,
                    color = labelColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Visible,
                    textAlign = TextAlign.Center,
                    letterSpacing = if (label.length > 9) (-0.35).sp else (-0.1).sp
                )
            }
        }
    }
}

/**
 * Renders the individual rounded-square (squircle) icon box for each navigation item,
 * matching the reference image pixel-for-pixel.
 */
@Composable
private fun NavTabSquircleIcon(
    tab: AppNavTab,
    isSelected: Boolean,
    boxSize: Dp
) {
    when (tab) {
        AppNavTab.HOME -> {
            val shape = RoundedCornerShape(boxSize * 0.28f)
            Box(
                modifier = Modifier
                    .size(boxSize)
                    .clip(shape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1A103C), Color(0xFF0B0720))
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(NeonHotPink.copy(alpha = 0.8f), NeonPurple.copy(alpha = 0.7f))
                        ),
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                HomeActiveWhiteIcon(size = boxSize * 0.62f)
            }
        }

        AppNavTab.TIKTOK -> {
            TikTokReferenceSquircle(size = boxSize)
        }

        AppNavTab.CHAMET -> {
            ChametReferenceSquircle(size = boxSize)
        }

        AppNavTab.EPAY -> {
            EpayReferenceSquircle(size = boxSize)
        }

        AppNavTab.EXCHANGE -> {
            ExchangeReferenceSquircle(size = boxSize)
        }

        AppNavTab.CALCULATOR -> {
            CalculateReferenceSquircle(size = boxSize)
        }

        AppNavTab.SOCIAL_BOOST -> {
            SocialBoostReferenceSquircle(size = boxSize)
        }

        AppNavTab.SUPPORT -> {
            SupportReferenceSquircle(size = boxSize)
        }

        else -> {
            HomeActiveWhiteIcon(size = boxSize * 0.62f)
        }
    }
}

/**
 * 1. HOME ICON (Crisp white house with roof overhang and rounded door cutout, matching reference image).
 */
@Composable
private fun HomeActiveWhiteIcon(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Subtle pink-white glow behind house
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.35f),
                    NeonHotPink.copy(alpha = 0.25f),
                    Color.Transparent
                )
            ),
            radius = w * 0.60f,
            center = Offset(w * 0.5f, h * 0.5f)
        )

        val path = Path().apply {
            // Roof peak
            moveTo(w * 0.50f, h * 0.12f)
            // Right roof slope to overhang
            lineTo(w * 0.90f, h * 0.48f)
            lineTo(w * 0.78f, h * 0.48f)
            // Right wall down
            lineTo(w * 0.78f, h * 0.84f)
            // Bottom right wall corner
            lineTo(w * 0.58f, h * 0.84f)
            // Door right side up
            lineTo(w * 0.58f, h * 0.58f)
            // Door top arch
            cubicTo(
                w * 0.58f, h * 0.52f,
                w * 0.42f, h * 0.52f,
                w * 0.42f, h * 0.58f
            )
            // Door left side down
            lineTo(w * 0.42f, h * 0.84f)
            // Bottom left wall
            lineTo(w * 0.22f, h * 0.84f)
            // Left wall up
            lineTo(w * 0.22f, h * 0.48f)
            // Left roof overhang
            lineTo(w * 0.10f, h * 0.48f)
            close()
        }

        drawPath(
            path = path,
            color = Color.White,
            style = Fill
        )
    }
}

/**
 * 2. TIKTOK SQUIRCLE ICON (Matches reference image: dark navy squircle with glowing electric-blue border
 * and 3D cyan/pink/white TikTok musical note).
 */
@Composable
fun TikTokReferenceSquircle(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        // Subtle outer blue neon glow
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyanBlue.copy(alpha = 0.28f), Color.Transparent)
            ),
            topLeft = Offset(-w * 0.06f, -h * 0.06f),
            size = Size(w * 1.12f, h * 1.12f),
            cornerRadius = CornerRadius(w * 0.32f, h * 0.32f)
        )

        // Dark navy-black glass background
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D1536),
                    Color(0xFF060919),
                    Color(0xFF040612)
                )
            ),
            cornerRadius = cr
        )

        // Glowing blue/cyan squircle border
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF2979FF),
                    Color(0xFF00E5FF),
                    Color(0xFF1565C0)
                )
            ),
            cornerRadius = cr,
            style = Stroke(width = w * 0.038f)
        )

        fun notePath(dx: Float, dy: Float): Path = Path().apply {
            // Vertical stem + right flag + bottom circular loop
            moveTo(w * 0.53f + dx, h * 0.19f + dy)
            lineTo(w * 0.65f + dx, h * 0.19f + dy)
            cubicTo(
                w * 0.66f + dx, h * 0.30f + dy,
                w * 0.73f + dx, h * 0.36f + dy,
                w * 0.82f + dx, h * 0.38f + dy
            )
            lineTo(w * 0.82f + dx, h * 0.50f + dy)
            cubicTo(
                w * 0.74f + dx, h * 0.49f + dy,
                w * 0.68f + dx, h * 0.46f + dy,
                w * 0.65f + dx, h * 0.42f + dy
            )
            lineTo(w * 0.65f + dx, h * 0.63f + dy)
            cubicTo(
                w * 0.65f + dx, h * 0.76f + dy,
                w * 0.54f + dx, h * 0.84f + dy,
                w * 0.41f + dx, h * 0.84f + dy
            )
            cubicTo(
                w * 0.28f + dx, h * 0.84f + dy,
                w * 0.19f + dx, h * 0.75f + dy,
                w * 0.19f + dx, h * 0.62f + dy
            )
            cubicTo(
                w * 0.19f + dx, h * 0.50f + dy,
                w * 0.28f + dx, h * 0.41f + dy,
                w * 0.41f + dx, h * 0.41f + dy
            )
            lineTo(w * 0.45f + dx, h * 0.41f + dy)
            lineTo(w * 0.45f + dx, h * 0.53f + dy)
            lineTo(w * 0.41f + dx, h * 0.53f + dy)
            cubicTo(
                w * 0.34f + dx, h * 0.53f + dy,
                w * 0.31f + dx, h * 0.57f + dy,
                w * 0.31f + dx, h * 0.62f + dy
            )
            cubicTo(
                w * 0.31f + dx, h * 0.68f + dy,
                w * 0.35f + dx, h * 0.72f + dy,
                w * 0.41f + dx, h * 0.72f + dy
            )
            cubicTo(
                w * 0.48f + dx, h * 0.72f + dy,
                w * 0.53f + dx, h * 0.68f + dy,
                w * 0.53f + dx, h * 0.61f + dy
            )
            close()
        }

        val shift = w * 0.034f
        // Cyan glitch layer (top-left)
        drawPath(path = notePath(-shift, -shift * 0.75f), color = Color(0xFF00F2FE))
        // Pink-red glitch layer (bottom-right)
        drawPath(path = notePath(shift, shift * 0.75f), color = Color(0xFFFE2C55))
        // Crisp White center note
        drawPath(path = notePath(0f, 0f), color = Color.White)
    }
}

/**
 * 3. OFFICIAL CHAMET LOGO SQUIRCLE (Exact 1:1 match with the user's reference image!):
 * - Outer neon-bordered dark squircle rim
 * - Inner bright White rounded square (#FFFFFF)
 * - Centered Pink-to-Purple gradient circle (#FF409A at top -> #8C28EA at bottom)
 * - Inside the circle: White mascot ring with 3 wavy scallops at the bottom
 * - Center: Purple circular eye/camera lens dot (#8C28EA)
 */
@Composable
fun ChametReferenceSquircle(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val outerCr = CornerRadius(w * 0.28f, h * 0.28f)

        // 1. Outer dark frame with glowing purple-blue neon border
        drawRoundRect(
            color = Color(0xFF0B0A20),
            cornerRadius = outerCr
        )
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFA855F7),
                    Color(0xFF6366F1),
                    Color(0xFF9333EA)
                )
            ),
            cornerRadius = outerCr,
            style = Stroke(width = w * 0.036f)
        )

        // 2. Inner bright WHITE rounded square (fills the squircle inside the thin neon rim)
        val inset = w * 0.075f
        val whiteSize = w - inset * 2f
        val whiteCr = CornerRadius(w * 0.22f, h * 0.22f)
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(inset, inset),
            size = Size(whiteSize, whiteSize),
            cornerRadius = whiteCr
        )

        // 3. Pink-to-Purple vertical gradient circle in the center of the white square
        val center = Offset(w * 0.50f, h * 0.50f)
        val outerCircleRadius = w * 0.335f
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFF3D9A), // Bright hot pink at top
                    Color(0xFFD633E8), // Magenta-purple middle
                    Color(0xFF8B2CF5)  // Vibrant purple at bottom
                ),
                startY = center.y - outerCircleRadius,
                endY = center.y + outerCircleRadius
            ),
            radius = outerCircleRadius,
            center = center
        )

        // 4. White Chamet mascot silhouette inside the pink-purple circle:
        // Circular white head/body that flares at the bottom into 3 rounded waves/scallops
        val mascotPath = Path().apply {
            // Start at left side of the circular head (9 o'clock)
            val headR = w * 0.195f
            val cx = w * 0.50f
            val cy = h * 0.465f

            // Upper semi-circle from left (175 deg) over top to right (5 deg)
            arcTo(
                rect = Rect(
                    left = cx - headR,
                    top = cy - headR,
                    right = cx + headR,
                    bottom = cy + headR
                ),
                startAngleDegrees = 172f,
                sweepAngleDegrees = 196f,
                forceMoveTo = true
            )

            // Right side curving down to right scallop foot
            cubicTo(
                w * 0.695f, h * 0.57f,
                w * 0.685f, h * 0.675f,
                w * 0.630f, h * 0.695f
            )
            // Valley between right scallop and center scallop
            cubicTo(
                w * 0.590f, h * 0.708f,
                w * 0.575f, h * 0.640f,
                w * 0.550f, h * 0.640f
            )
            // Center scallop lobe dipping down in the middle
            cubicTo(
                w * 0.530f, h * 0.640f,
                w * 0.525f, h * 0.735f,
                w * 0.500f, h * 0.735f
            )
            cubicTo(
                w * 0.475f, h * 0.735f,
                w * 0.470f, h * 0.640f,
                w * 0.450f, h * 0.640f
            )
            // Valley between center scallop and left scallop
            cubicTo(
                w * 0.425f, h * 0.640f,
                w * 0.410f, h * 0.708f,
                w * 0.370f, h * 0.695f
            )
            // Left scallop foot curving back up to left side of head
            cubicTo(
                w * 0.315f, h * 0.675f,
                w * 0.305f, h * 0.57f,
                cx - headR * 0.99f, cy + headR * 0.14f
            )
            close()
        }
        drawPath(
            path = mascotPath,
            color = Color.White,
            style = Fill
        )

        // 5. Central purple-magenta circular eye/lens dot inside the white head
        val eyeCenter = Offset(w * 0.50f, h * 0.465f)
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFD938E5),
                    Color(0xFF8B2CF5)
                )
            ),
            radius = w * 0.088f,
            center = eyeCenter
        )
    }
}

/**
 * 4. OFFICIAL EPAY LOGO SQUIRCLE (Exact 1:1 match with the user's reference image!):
 * - Royal Blue / Electric Blue gradient squircle background (#007BFF top -> #003899 bottom)
 * - Glowing cyan-blue neon border
 * - Iconic Lime-Green (#7CE818 -> #68C814) slanted winged "E" emblem in the center
 *   with two signature left-jutting horizontal speed tabs
 * - Crisp white "®" registered trademark symbol at the upper-right of the "E"
 */
@Composable
fun EpayReferenceSquircle(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        // 1. Royal Blue / Cerulean gradient squircle background matching reference image
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0088FF),
                    Color(0xFF0055CC),
                    Color(0xFF00368C)
                ),
                center = Offset(w * 0.55f, h * 0.22f),
                radius = w * 0.95f
            ),
            cornerRadius = cr
        )

        // Glowing cyan-blue squircle border
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF38BDF8),
                    Color(0xFF0077FF),
                    Color(0xFF1D4ED8)
                )
            ),
            cornerRadius = cr,
            style = Stroke(width = w * 0.038f)
        )

        // 2. Official EPAY Lime-Green slanted winged "E" logo
        val epayGreenBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF8CF51E), // Bright lime green at top
                Color(0xFF72D616), // Vibrant green middle
                Color(0xFF5AB810)  // Rich green at bottom
            )
        )

        // Build the exact slanted winged "E" with the two left speed-tabs shown in the reference image
        val ePath = Path().apply {
            // Start at top-right of the top bar
            moveTo(w * 0.72f, h * 0.25f)
            // Top bar going left to rounded top-left corner
            lineTo(w * 0.45f, h * 0.25f)
            cubicTo(
                w * 0.36f, h * 0.25f,
                w * 0.31f, h * 0.30f,
                w * 0.29f, h * 0.39f
            )
            // Upper-left speed wing tab jutting out to the left
            lineTo(w * 0.19f, h * 0.39f)
            lineTo(w * 0.17f, h * 0.46f)
            lineTo(w * 0.275f, h * 0.46f)
            // Small gap between upper-left tab and lower-left tab on the left of spine
            lineTo(w * 0.265f, h * 0.51f)
            // Lower-left speed wing tab jutting out to the left
            lineTo(w * 0.20f, h * 0.51f)
            lineTo(w * 0.185f, h * 0.58f)
            lineTo(w * 0.255f, h * 0.58f)
            // Lower spine curving into the bottom bar
            cubicTo(
                w * 0.24f, h * 0.69f,
                w * 0.28f, h * 0.75f,
                w * 0.38f, h * 0.75f
            )
            // Bottom horizontal bar to bottom-right
            lineTo(w * 0.64f, h * 0.75f)
            lineTo(w * 0.67f, h * 0.63f)
            // Inside bottom bar going left to inner spine
            lineTo(w * 0.42f, h * 0.63f)
            cubicTo(
                w * 0.39f, h * 0.63f,
                w * 0.38f, h * 0.60f,
                w * 0.39f, h * 0.55f
            )
            // Middle bar extending to the right
            lineTo(w * 0.64f, h * 0.55f)
            lineTo(w * 0.665f, h * 0.44f)
            // Inside upper cavity going left to inner spine
            lineTo(w * 0.41f, h * 0.44f)
            cubicTo(
                w * 0.42f, h * 0.39f,
                w * 0.44f, h * 0.37f,
                w * 0.49f, h * 0.37f
            )
            // Underside of top bar going to top-right
            lineTo(w * 0.695f, h * 0.37f)
            close()
        }

        drawPath(
            path = ePath,
            brush = epayGreenBrush,
            style = Fill
        )

        // 3. Tiny white "®" symbol at the top-right of the "E" (matching reference image)
        val rCenter = Offset(w * 0.80f, h * 0.27f)
        val rRadius = w * 0.055f
        drawCircle(
            color = Color.White,
            radius = rRadius,
            center = rCenter,
            style = Stroke(width = w * 0.018f)
        )
        // Tiny 'R' strokes inside the circle
        drawLine(
            color = Color.White,
            start = Offset(rCenter.x - rRadius * 0.35f, rCenter.y - rRadius * 0.45f),
            end = Offset(rCenter.x - rRadius * 0.35f, rCenter.y + rRadius * 0.45f),
            strokeWidth = w * 0.016f,
            cap = StrokeCap.Round
        )
        drawArc(
            color = Color.White,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(rCenter.x - rRadius * 0.35f, rCenter.y - rRadius * 0.45f),
            size = Size(rRadius * 0.65f, rRadius * 0.48f),
            style = Stroke(width = w * 0.015f, cap = StrokeCap.Round)
        )
        drawLine(
            color = Color.White,
            start = Offset(rCenter.x - rRadius * 0.05f, rCenter.y + rRadius * 0.02f),
            end = Offset(rCenter.x + rRadius * 0.35f, rCenter.y + rRadius * 0.45f),
            strokeWidth = w * 0.015f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * 5. EXCHANGE SQUIRCLE ICON (Matches reference image: dark navy squircle with glowing blue border,
 * curved bright-green top arrow pointing right & curved golden-yellow bottom arrow pointing left).
 */
@Composable
fun ExchangeReferenceSquircle(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        // Dark navy glass background
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0E1638),
                    Color(0xFF060A1C),
                    Color(0xFF040612)
                )
            ),
            cornerRadius = cr
        )

        // Glowing blue/cyan squircle border
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF2979FF),
                    Color(0xFF00B4FF),
                    Color(0xFF38BDF8)
                )
            ),
            cornerRadius = cr,
            style = Stroke(width = w * 0.038f)
        )

        // 1. Top Curved GREEN Arrow (swooshing from left up to right arrow-head)
        val greenArrowPath = Path().apply {
            // Tail at mid-left
            moveTo(w * 0.20f, h * 0.52f)
            // Outer top curve up to arrowhead base
            cubicTo(
                w * 0.20f, h * 0.28f,
                w * 0.38f, h * 0.21f,
                w * 0.58f, h * 0.24f
            )
            // Top wing of arrowhead
            lineTo(w * 0.58f, h * 0.14f)
            // Arrow tip pointing right
            lineTo(w * 0.80f, h * 0.33f)
            // Bottom wing of arrowhead
            lineTo(w * 0.58f, h * 0.48f)
            lineTo(w * 0.58f, h * 0.38f)
            // Inner curve back to tail
            cubicTo(
                w * 0.42f, h * 0.36f,
                w * 0.30f, h * 0.41f,
                w * 0.20f, h * 0.52f
            )
            close()
        }
        drawPath(
            path = greenArrowPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF52FF6E),
                    Color(0xFF14D947),
                    Color(0xFF059E33)
                ),
                start = Offset(w * 0.2f, h * 0.15f),
                end = Offset(w * 0.8f, h * 0.5f)
            )
        )

        // 2. Bottom Curved GOLDEN-YELLOW Arrow (swooshing from right down to left arrow-head)
        val yellowArrowPath = Path().apply {
            // Tail at mid-right
            moveTo(w * 0.80f, h * 0.48f)
            // Outer bottom curve down to arrowhead base
            cubicTo(
                w * 0.80f, h * 0.72f,
                w * 0.62f, h * 0.79f,
                w * 0.42f, h * 0.76f
            )
            // Bottom wing of arrowhead
            lineTo(w * 0.42f, h * 0.86f)
            // Arrow tip pointing left
            lineTo(w * 0.20f, h * 0.67f)
            // Top wing of arrowhead
            lineTo(w * 0.42f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.62f)
            // Inner curve back to tail
            cubicTo(
                w * 0.58f, h * 0.64f,
                w * 0.70f, h * 0.59f,
                w * 0.80f, h * 0.48f
            )
            close()
        }
        drawPath(
            path = yellowArrowPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFEA3B),
                    Color(0xFFFFB300),
                    Color(0xFFFF8000)
                ),
                start = Offset(w * 0.2f, h * 0.5f),
                end = Offset(w * 0.8f, h * 0.85f)
            )
        )
    }
}

/**
 * 6. CALCULATE SQUIRCLE ICON (Matches reference image: dark glass squircle with warm golden/amber glow,
 * solid golden-yellow rounded calculator body with dark display bar and 6 dark keypad cutouts).
 */
@Composable
fun CalculateReferenceSquircle(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        // Dark charcoal/navy glass background with warm amber radial glow
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF332211),
                    Color(0xFF100C1E),
                    Color(0xFF070612)
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.65f
            ),
            cornerRadius = cr
        )

        // Warm gold/amber & violet squircle border matching reference image
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFA855F7).copy(alpha = 0.7f),
                    Color(0xFFFFB300).copy(alpha = 0.85f),
                    Color(0xFFFF8F00).copy(alpha = 0.9f)
                )
            ),
            cornerRadius = cr,
            style = Stroke(width = w * 0.036f)
        )

        // Solid Golden-Yellow Calculator Body
        val calcLeft = w * 0.23f
        val calcTop = h * 0.16f
        val calcW = w * 0.54f
        val calcH = h * 0.68f
        val calcCorner = CornerRadius(w * 0.09f, h * 0.09f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFE54C),
                    Color(0xFFFFB700),
                    Color(0xFFFF9000)
                ),
                startY = calcTop,
                endY = calcTop + calcH
            ),
            topLeft = Offset(calcLeft, calcTop),
            size = Size(calcW, calcH),
            cornerRadius = calcCorner
        )

        // Dark cutout for Calculator Display Screen at top
        val cutoutColor = Color(0xFF140E1E)
        drawRoundRect(
            color = cutoutColor,
            topLeft = Offset(w * 0.30f, h * 0.24f),
            size = Size(w * 0.40f, h * 0.14f),
            cornerRadius = CornerRadius(w * 0.035f, h * 0.035f)
        )

        // 2 rows x 3 columns (6 dark rounded-square button cutouts) matching reference image
        val btnW = w * 0.095f
        val btnH = h * 0.095f
        val btnCr = CornerRadius(w * 0.025f, h * 0.025f)
        val cols = listOf(w * 0.30f, w * 0.4525f, w * 0.605f)
        val rows = listOf(h * 0.46f, h * 0.61f)

        for (ry in rows) {
            for (cx in cols) {
                drawRoundRect(
                    color = cutoutColor,
                    topLeft = Offset(cx, ry),
                    size = Size(btnW, btnH),
                    cornerRadius = btnCr
                )
            }
        }
    }
}

/**
 * 7. SUPPORT SQUIRCLE ICON (Matches reference image: dark violet glass squircle with glowing purple border,
 * glowing pink-purple headset arch + earpieces + mic boom + central speech bubble with 3 dots).
 */
@Composable
fun SupportReferenceSquircle(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        // Dark purple/navy glass background
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF28104E),
                    Color(0xFF0E0722),
                    Color(0xFF060412)
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.65f
            ),
            cornerRadius = cr
        )

        // Glowing purple-pink squircle border
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFD946EF),
                    Color(0xFF9333EA),
                    Color(0xFF7C3AED)
                )
            ),
            cornerRadius = cr,
            style = Stroke(width = w * 0.038f)
        )

        val supportGradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF5A3FF),
                Color(0xFFD946EF),
                Color(0xFF8B5CF6)
            )
        )

        // 1. Headset top arch
        val archRect = Rect(
            left = w * 0.20f,
            top = h * 0.17f,
            right = w * 0.80f,
            bottom = h * 0.77f
        )
        drawArc(
            brush = supportGradient,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = archRect.topLeft,
            size = archRect.size,
            style = Stroke(width = w * 0.075f, cap = StrokeCap.Round)
        )

        // 2. Left & Right Ear Cups
        drawRoundRect(
            brush = supportGradient,
            topLeft = Offset(w * 0.14f, h * 0.43f),
            size = Size(w * 0.11f, h * 0.24f),
            cornerRadius = CornerRadius(w * 0.055f, h * 0.055f)
        )
        drawRoundRect(
            brush = supportGradient,
            topLeft = Offset(w * 0.75f, h * 0.43f),
            size = Size(w * 0.11f, h * 0.24f),
            cornerRadius = CornerRadius(w * 0.055f, h * 0.055f)
        )

        // 3. Microphone boom curving from right ear cup to bottom-center
        val boomPath = Path().apply {
            moveTo(w * 0.79f, h * 0.65f)
            cubicTo(
                w * 0.77f, h * 0.81f,
                w * 0.66f, h * 0.84f,
                w * 0.53f, h * 0.84f
            )
        }
        drawPath(
            path = boomPath,
            brush = supportGradient,
            style = Stroke(width = w * 0.055f, cap = StrokeCap.Round)
        )
        // Mic mouthpiece pill at bottom center
        drawRoundRect(
            brush = supportGradient,
            topLeft = Offset(w * 0.44f, h * 0.795f),
            size = Size(w * 0.13f, h * 0.085f),
            cornerRadius = CornerRadius(w * 0.045f, h * 0.045f)
        )

        // 4. Central Speech Bubble inside the headset
        val bubbleCenter = Offset(w * 0.50f, h * 0.52f)
        val bubbleRx = w * 0.20f
        val bubbleRy = h * 0.17f
        val bubblePath = Path().apply {
            addOval(
                Rect(
                    left = bubbleCenter.x - bubbleRx,
                    top = bubbleCenter.y - bubbleRy,
                    right = bubbleCenter.x + bubbleRx,
                    bottom = bubbleCenter.y + bubbleRy
                )
            )
            // Small speech bubble tail on bottom-left and bottom-right (matching headset chat icon in reference)
            moveTo(w * 0.36f, h * 0.63f)
            lineTo(w * 0.31f, h * 0.72f)
            lineTo(w * 0.44f, h * 0.67f)
            close()
        }
        drawPath(
            path = bubblePath,
            brush = supportGradient,
            style = Fill
        )

        // 5. Three dark horizontal dots (•••) inside the speech bubble
        val dotColor = Color(0xFF1A0936)
        val dotR = w * 0.028f
        val dotY = bubbleCenter.y
        drawCircle(color = dotColor, radius = dotR, center = Offset(w * 0.41f, dotY))
        drawCircle(color = dotColor, radius = dotR, center = Offset(w * 0.50f, dotY))
        drawCircle(color = dotColor, radius = dotR, center = Offset(w * 0.59f, dotY))
    }
}

/**
 * 8. FREE FIRE / GAMING TOP-UP SQUIRCLE LOGO:
 * Dark crimson-obsidian glass squircle with glowing fiery orange-gold border,
 * stylized Free Fire flame crest and glowing cyan-gold diamond core.
 */
@Composable
fun FreeFireGamingSquircle(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        // Dark fiery crimson-navy background
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF3B1219),
                    Color(0xFF18081C),
                    Color(0xFF090410)
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.68f
            ),
            cornerRadius = cr
        )

        // Glowing orange-gold-crimson neon border
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFD54F),
                    Color(0xFFFF6D00),
                    Color(0xFFF43F5E)
                )
            ),
            cornerRadius = cr,
            style = Stroke(width = w * 0.038f)
        )

        // Outer fiery flame aura path
        val flamePath = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            cubicTo(
                w * 0.72f, h * 0.26f,
                w * 0.84f, h * 0.48f,
                w * 0.76f, h * 0.68f
            )
            cubicTo(
                w * 0.70f, h * 0.81f,
                w * 0.58f, h * 0.86f,
                w * 0.50f, h * 0.86f
            )
            cubicTo(
                w * 0.42f, h * 0.86f,
                w * 0.30f, h * 0.81f,
                w * 0.24f, h * 0.68f
            )
            cubicTo(
                w * 0.16f, h * 0.48f,
                w * 0.28f, h * 0.26f,
                w * 0.50f, h * 0.14f
            )
            close()
        }
        drawPath(
            path = flamePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFEA00),
                    Color(0xFFFF6D00),
                    Color(0xFFE11D48)
                )
            ),
            style = Fill
        )

        // Inner faceted Free Fire Diamond in crisp cyan-white
        val diamondPath = Path().apply {
            moveTo(w * 0.50f, h * 0.34f)
            lineTo(w * 0.68f, h * 0.52f)
            lineTo(w * 0.50f, h * 0.76f)
            lineTo(w * 0.32f, h * 0.52f)
            close()
        }
        drawPath(
            path = diamondPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFF7DD3FC),
                    Color(0xFF00B4FF)
                )
            ),
            style = Fill
        )
        drawPath(
            path = diamondPath,
            color = Color.White,
            style = Stroke(width = w * 0.025f)
        )
    }
}

/**
 * 9. SOCIAL BOOST SQUIRCLE ICON (Professional Social-Media Growth & Marketing Icon):
 * Dark violet-indigo glass squircle with glowing magenta-cyan-gold neon border,
 * ascending 3-bar social analytics growth chart + dynamic upward boost arrow/rocket trend +
 * glowing social engagement star badge.
 */
@Composable
fun SocialBoostReferenceSquircle(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        // Subtle outer neon pink-cyan bloom
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(NeonHotPink.copy(alpha = 0.26f), Color.Transparent)
            ),
            topLeft = Offset(-w * 0.05f, -h * 0.05f),
            size = Size(w * 1.10f, h * 1.10f),
            cornerRadius = CornerRadius(w * 0.32f, h * 0.32f)
        )

        // Dark purple/indigo glass background matching the Chamet Top Up BD bottom bar
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2B1055),
                    Color(0xFF12082B),
                    Color(0xFF070414)
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.68f
            ),
            cornerRadius = cr
        )

        // Glowing Magenta -> Cyan -> Purple neon squircle border
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFF2CF3),
                    Color(0xFF9333EA),
                    Color(0xFF00E5FF)
                )
            ),
            cornerRadius = cr,
            style = Stroke(width = w * 0.038f)
        )

        // 1. Ascending 3-Bar Social Growth Analytics Bars at bottom
        val barCorner = CornerRadius(w * 0.035f, h * 0.035f)
        val barWidth = w * 0.125f

        // Bar 1 (Left - Cyan/Blue)
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF38BDF8), Color(0xFF2563EB)),
                startY = h * 0.58f,
                endY = h * 0.80f
            ),
            topLeft = Offset(w * 0.22f, h * 0.58f),
            size = Size(barWidth, h * 0.22f),
            cornerRadius = barCorner
        )

        // Bar 2 (Middle - Purple/Magenta)
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE879F9), Color(0xFF9333EA)),
                startY = h * 0.46f,
                endY = h * 0.80f
            ),
            topLeft = Offset(w * 0.435f, h * 0.46f),
            size = Size(barWidth, h * 0.34f),
            cornerRadius = barCorner
        )

        // Bar 3 (Right - Gold/HotPink Peak)
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFEA00), Color(0xFFFF2CF3)),
                startY = h * 0.33f,
                endY = h * 0.80f
            ),
            topLeft = Offset(w * 0.65f, h * 0.33f),
            size = Size(barWidth, h * 0.47f),
            cornerRadius = barCorner
        )

        // 2. Dynamic Upward Social Boost Trend Arrowswoosh across the top
        val arrowBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF00F2FE),
                Color(0xFFFF66F5),
                Color(0xFFFFEA00)
            ),
            start = Offset(w * 0.18f, h * 0.52f),
            end = Offset(w * 0.80f, h * 0.16f)
        )

        val trendPath = Path().apply {
            moveTo(w * 0.20f, h * 0.48f)
            cubicTo(
                w * 0.34f, h * 0.44f,
                w * 0.50f, h * 0.34f,
                w * 0.72f, h * 0.20f
            )
        }
        drawPath(
            path = trendPath,
            brush = arrowBrush,
            style = Stroke(width = w * 0.072f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Arrowhead pointing up-right at (0.76, 0.17)
        val arrowHead = Path().apply {
            moveTo(w * 0.79f, h * 0.15f)
            lineTo(w * 0.59f, h * 0.16f)
            lineTo(w * 0.75f, h * 0.34f)
            close()
        }
        drawPath(
            path = arrowHead,
            color = Color(0xFFFFEA00),
            style = Fill
        )

        // 3. Glowing Social Sparkle/Node on top-left
        val starCx = w * 0.29f
        val starCy = h * 0.27f
        val starR = w * 0.085f
        val sparklePath = Path().apply {
            moveTo(starCx, starCy - starR)
            quadraticTo(starCx, starCy, starCx + starR, starCy)
            quadraticTo(starCx, starCy, starCx, starCy + starR)
            quadraticTo(starCx, starCy, starCx - starR, starCy)
            quadraticTo(starCx, starCy, starCx, starCy - starR)
            close()
        }
        drawPath(
            path = sparklePath,
            color = Color.White,
            style = Fill
        )
    }
}
