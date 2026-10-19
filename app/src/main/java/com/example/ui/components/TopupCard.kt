package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceCategory
import com.example.data.model.TopupPackage
import java.util.Locale

@Composable
fun TopupPackageCard(
    pkg: TopupPackage,
    onBuyClick: (TopupPackage) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(18.dp)
    val subtitleText = if (
        pkg.category == ServiceCategory.TIKTOK &&
        pkg.id in listOf("tt_100", "tt_200", "tt_300", "tt_400")
    ) {
        "TikTok Standard Pack"
    } else if (pkg.category == ServiceCategory.TIKTOK && !pkg.subtitle.startsWith("TikTok")) {
        "TikTok ${pkg.subtitle}"
    } else {
        pkg.subtitle
    }

    // For home preview matching the reference image, only show badge on Starter ("tt_100") or popular/badged items
    val badgeText = pkg.badge

    val (borderColors, spotGlowColor, bgGradient) = when (pkg.category) {
        ServiceCategory.TIKTOK -> Triple(
            listOf(
                Color(0xFF00F2FE).copy(alpha = 0.85f),
                Color(0xFF7C3AED).copy(alpha = 0.70f),
                Color(0xFFFF2CF3).copy(alpha = 0.80f),
                Color(0xFFFFD54F).copy(alpha = 0.75f)
            ),
            Color(0xFF00F2FE).copy(alpha = 0.45f),
            listOf(Color(0xFF17113A), Color(0xFF0D0824), Color(0xFF120B2E))
        )
        ServiceCategory.CHAMET -> Triple(
            listOf(
                Color(0xFFFF9100).copy(alpha = 0.85f),
                Color(0xFFFF2CF3).copy(alpha = 0.80f),
                Color(0xFF9333EA).copy(alpha = 0.75f),
                Color(0xFFFFD54F).copy(alpha = 0.80f)
            ),
            Color(0xFFFF2CF3).copy(alpha = 0.48f),
            listOf(Color(0xFF260F38), Color(0xFF120722), Color(0xFF190A2D))
        )
        ServiceCategory.FREEFIRE -> Triple(
            listOf(
                Color(0xFFFFD54F).copy(alpha = 0.85f),
                Color(0xFFFF6D00).copy(alpha = 0.80f),
                Color(0xFFF43F5E).copy(alpha = 0.75f),
                Color(0xFFA855F7).copy(alpha = 0.70f)
            ),
            Color(0xFFFF6D00).copy(alpha = 0.48f),
            listOf(Color(0xFF281020), Color(0xFF13081C), Color(0xFF1E0B1A))
        )
        else -> Triple(
            listOf(
                Color(0xFFE879F9).copy(alpha = 0.80f),
                Color(0xFF7C3AED).copy(alpha = 0.65f),
                Color(0xFF3B82F6).copy(alpha = 0.65f),
                Color(0xFFD946EF).copy(alpha = 0.80f)
            ),
            Color(0xFFA855F7).copy(alpha = 0.45f),
            listOf(Color(0xFF170D34), Color(0xFF0C0720), Color(0xFF120B2B))
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = cardShape,
                spotColor = spotGlowColor
            )
            .clip(cardShape)
            .border(
                width = 1.4.dp,
                brush = Brush.linearGradient(colors = borderColors),
                shape = cardShape
            )
            .clickable { onBuyClick(pkg) },
        color = Color.Transparent,
        shape = cardShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = bgGradient,
                        start = Offset(0f, 0f),
                        end = Offset(350f, 420f)
                    )
                )
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    // Top-left ambient glow behind logo
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                spotGlowColor.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        radius = w * 0.52f,
                        center = Offset(w * 0.22f, h * 0.20f)
                    )

                    // Subtle diagonal glassmorphism light streak
                    val sheenPath = Path().apply {
                        moveTo(w * 0.15f, 0f)
                        lineTo(w * 0.55f, 0f)
                        lineTo(w * 0.20f, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(
                        path = sheenPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color(0xFFA855F7).copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
                }
                .padding(horizontal = 13.dp, vertical = 13.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Top Row: Official Brand Squircle Icon on Left + Optional Pink Neon Pill Badge on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    when (pkg.category) {
                        ServiceCategory.TIKTOK -> TikTokReferenceSquircle(size = 40.dp)
                        ServiceCategory.CHAMET -> ChametReferenceSquircle(size = 40.dp)
                        ServiceCategory.FREEFIRE -> FreeFireGamingSquircle(size = 40.dp)
                        else -> TikTokReferenceSquircle(size = 40.dp)
                    }

                    if (!badgeText.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Transparent,
                            border = BorderStroke(0.8.dp, Color(0xFFFF8AF8).copy(alpha = 0.65f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFFFF2CF3),
                                                Color(0xFFEC4899),
                                                Color(0xFFC026D3)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 9.dp, vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Package Title (e.g., "100 Coins")
                Text(
                    text = pkg.name,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Package Subtitle (e.g., "TikTok Standard Pack")
                Text(
                    text = subtitleText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFCBD5E1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(7.dp))

                // Price Hierarchy (e.g., "৳ 180" in bright gold)
                Text(
                    text = "৳ ${String.format(Locale.US, "%,.0f", pkg.priceBdt)}",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFACC15)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Attractive Neon Purple-Pink Pill "Buy Now" Button with Crisp Vector Cart Icon
                val btnShape = RoundedCornerShape(50)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(37.dp)
                        .shadow(8.dp, btnShape, spotColor = Color(0xFFFF2CF3))
                        .clip(btnShape)
                        .clickable { onBuyClick(pkg) },
                    shape = btnShape,
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFC084FC).copy(alpha = 0.7f),
                                Color(0xFFFF8AF8).copy(alpha = 0.85f)
                            )
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF7C3AED),
                                        Color(0xFFB829E3),
                                        Color(0xFFEC4899),
                                        Color(0xFFFF2CF3)
                                    )
                                )
                            )
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ShoppingCartVectorIcon(
                            modifier = Modifier.size(15.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buy Now",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Crisp White Shopping Cart Vector Icon for the "Buy Now" button.
 */
@Composable
private fun ShoppingCartVectorIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.11f

        // Cart handle + basket outline
        val cartPath = Path().apply {
            moveTo(w * 0.08f, h * 0.18f)
            lineTo(w * 0.24f, h * 0.18f)
            lineTo(w * 0.36f, h * 0.64f)
            lineTo(w * 0.82f, h * 0.64f)
            lineTo(w * 0.92f, h * 0.30f)
            lineTo(w * 0.28f, h * 0.30f)
        }
        drawPath(
            path = cartPath,
            color = tint,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Two wheels at bottom
        drawCircle(
            color = tint,
            radius = w * 0.085f,
            center = Offset(w * 0.41f, h * 0.84f)
        )
        drawCircle(
            color = tint,
            radius = w * 0.085f,
            center = Offset(w * 0.77f, h * 0.84f)
        )
    }
}

/**
 * Sleek Neon Gaming Squircle Icon for Free Fire / Gaming packages.
 */
@Composable
private fun GamingCardSquircleIcon(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2A0F1D), Color(0xFF0E0618))
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF97316), Color(0xFFEC4899))
                ),
                shape = RoundedCornerShape(size * 0.28f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🔥", fontSize = 18.sp)
    }
}
