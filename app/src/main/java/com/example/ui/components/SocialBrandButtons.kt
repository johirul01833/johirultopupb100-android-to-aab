package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Official Brand Colors
val OfficialWhatsAppLightGreen = Color(0xFF25D366)
val OfficialWhatsAppDarkGreen = Color(0xFF128C7E)
val OfficialWhatsAppDeepGreen = Color(0xFF075E54)

val OfficialTelegramLightBlue = Color(0xFF37AEE2)
val OfficialTelegramDarkBlue = Color(0xFF1E96C8)
val OfficialTelegramDeepBlue = Color(0xFF229ED9)

// Exact Official WhatsApp SVG Path (24x24 viewport)
private const val WHATSAPP_OFFICIAL_SVG_PATH =
    "M12.04 2C6.58 2 2.13 6.45 2.13 11.91C2.13 13.66 2.59 15.36 3.45 16.86L2.05 22L7.3 20.62C8.75 21.41 10.38 21.83 12.04 21.83C17.5 21.83 21.95 17.38 21.95 11.92C21.95 9.27 20.92 6.78 19.05 4.91C17.18 3.03 14.69 2 12.04 2M12.05 3.67C14.25 3.67 16.31 4.53 17.87 6.09C19.42 7.65 20.28 9.72 20.28 11.92C20.28 16.46 16.58 20.15 12.04 20.15C10.56 20.15 9.11 19.76 7.85 19L7.55 18.83L4.43 19.65L5.26 16.61L5.06 16.29C4.24 15 3.8 13.47 3.8 11.91C3.81 7.37 7.5 3.67 12.05 3.67M8.53 7.33C8.37 7.33 8.1 7.39 7.87 7.64C7.65 7.89 7 8.5 7 9.71C7 10.93 7.89 12.1 8 12.27C8.14 12.44 9.76 14.94 12.25 16C12.84 16.27 13.3 16.42 13.66 16.53C14.25 16.72 14.79 16.69 15.22 16.63C15.7 16.56 16.68 16.03 16.89 15.45C17.1 14.87 17.1 14.38 17.04 14.27C16.97 14.17 16.81 14.11 16.56 14C16.31 13.86 15.09 13.26 14.87 13.18C14.64 13.1 14.5 13.06 14.31 13.3C14.15 13.55 13.67 14.11 13.53 14.27C13.38 14.44 13.24 14.46 13 14.34C12.74 14.21 11.94 13.95 11 13.11C10.26 12.45 9.77 11.64 9.62 11.39C9.5 11.15 9.61 11 9.73 10.89C9.84 10.78 10 10.6 10.1 10.45C10.23 10.31 10.27 10.2 10.35 10.04C10.43 9.87 10.39 9.73 10.33 9.61C10.27 9.5 9.77 8.26 9.56 7.77C9.36 7.29 9.16 7.35 9 7.34C8.86 7.34 8.7 7.33 8.53 7.33Z"

// Exact Official Telegram Paper Plane SVG Path (24x24 viewport)
private const val TELEGRAM_OFFICIAL_SVG_PATH =
    "M20.665 3.717l-17.73 6.837c-1.21.486-1.203 1.161-.222 1.462l4.552 1.42 10.532-6.645c.498-.303.953-.14.579.192l-8.533 7.701h-.002l.002.001-.314 4.692c.46 0 .663-.211.921-.46l2.211-2.15 4.599 3.397c.848.467 1.457.227 1.668-.785l3.019-14.228c.309-1.239-.473-1.8-1.282-1.434z"

/**
 * Renders the exact official WhatsApp vector glyph.
 */
@Composable
fun WhatsAppOfficialIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    withBrandCircle: Boolean = true,
    circleSize: Dp = 28.dp,
    glyphSize: Dp = 18.dp
) {
    if (withBrandCircle) {
        Box(
            modifier = modifier
                .size(circleSize)
                .shadow(4.dp, CircleShape, spotColor = OfficialWhatsAppLightGreen)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF2CE670), OfficialWhatsAppLightGreen, OfficialWhatsAppDarkGreen)
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            WhatsAppVectorGlyph(modifier = Modifier.size(glyphSize), tint = tint)
        }
    } else {
        WhatsAppVectorGlyph(modifier = modifier.size(glyphSize), tint = tint)
    }
}

@Composable
fun WhatsAppVectorGlyph(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    val basePath = remember {
        PathParser().parsePathString(WHATSAPP_OFFICIAL_SVG_PATH).toPath().apply {
            fillType = PathFillType.EvenOdd
        }
    }
    Canvas(modifier = modifier) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val transformedPath = androidx.compose.ui.graphics.Path().apply {
            addPath(basePath)
            transform(Matrix().apply {
                scale(scaleX, scaleY)
            })
        }
        drawPath(path = transformedPath, color = tint)
    }
}

/**
 * Renders the exact official Telegram vector glyph.
 */
@Composable
fun TelegramOfficialIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    withBrandCircle: Boolean = true,
    circleSize: Dp = 28.dp,
    glyphSize: Dp = 17.dp
) {
    if (withBrandCircle) {
        Box(
            modifier = modifier
                .size(circleSize)
                .shadow(4.dp, CircleShape, spotColor = OfficialTelegramLightBlue)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF42BDF2), OfficialTelegramLightBlue, OfficialTelegramDarkBlue)
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            TelegramVectorGlyph(modifier = Modifier.size(glyphSize), tint = tint)
        }
    } else {
        TelegramVectorGlyph(modifier = modifier.size(glyphSize), tint = tint)
    }
}

@Composable
fun TelegramVectorGlyph(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    val basePath = remember {
        PathParser().parsePathString(TELEGRAM_OFFICIAL_SVG_PATH).toPath()
    }
    Canvas(modifier = modifier) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val transformedPath = androidx.compose.ui.graphics.Path().apply {
            addPath(basePath)
            transform(Matrix().apply {
                scale(scaleX, scaleY)
            })
        }
        drawPath(path = transformedPath, color = tint)
    }
}

/**
 * Ultra-Premium Official WhatsApp Brand Button.
 */
@Composable
fun PremiumWhatsAppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    height: Dp = 48.dp,
    cornerRadius: Dp = 14.dp,
    fontSize: TextUnit = 13.sp,
    iconSize: Dp = 26.dp
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF1FAF54),
            OfficialWhatsAppLightGreen,
            OfficialWhatsAppDarkGreen
        )
    )

    Surface(
        modifier = modifier
            .height(height)
            .shadow(8.dp, RoundedCornerShape(cornerRadius), spotColor = OfficialWhatsAppLightGreen)
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    WhatsAppVectorGlyph(
                        modifier = Modifier.size(iconSize * 0.68f),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(horizontalAlignment = if (subtitle != null) Alignment.Start else Alignment.CenterHorizontally) {
                    Text(
                        text = text,
                        fontSize = fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.92f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ultra-Premium Official Telegram Brand Button.
 */
@Composable
fun PremiumTelegramButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    height: Dp = 48.dp,
    cornerRadius: Dp = 14.dp,
    fontSize: TextUnit = 13.sp,
    iconSize: Dp = 26.dp
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF2AABEE),
            OfficialTelegramLightBlue,
            OfficialTelegramDarkBlue
        )
    )

    Surface(
        modifier = modifier
            .height(height)
            .shadow(8.dp, RoundedCornerShape(cornerRadius), spotColor = OfficialTelegramLightBlue)
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    TelegramVectorGlyph(
                        modifier = Modifier.size(iconSize * 0.65f),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(horizontalAlignment = if (subtitle != null) Alignment.Start else Alignment.CenterHorizontally) {
                    Text(
                        text = text,
                        fontSize = fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.92f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact Header Chip for WhatsApp with Official Logo Badge & Neon Glassmorphism.
 */
@Composable
fun PremiumWhatsAppHeaderChip(
    text: String = "WhatsApp Direct",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(50)
    Surface(
        modifier = modifier
            .height(40.dp)
            .shadow(10.dp, pillShape, spotColor = Color(0xFF10B981))
            .clip(pillShape)
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = pillShape,
        border = BorderStroke(
            1.3.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF34D399),
                    Color(0xFF10B981),
                    Color(0xFF6EE7B7)
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
                            Color(0xFF043927),
                            Color(0xFF076646),
                            Color(0xFF054A33)
                        )
                    )
                )
                .padding(start = 7.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2CE670), OfficialWhatsAppLightGreen)
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    WhatsAppVectorGlyph(
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "›",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.88f),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/**
 * Compact Header Chip for Telegram with Official Logo Badge & Neon Glassmorphism.
 */
@Composable
fun PremiumTelegramHeaderChip(
    text: String = "Telegram Channel",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(50)
    Surface(
        modifier = modifier
            .height(40.dp)
            .shadow(10.dp, pillShape, spotColor = Color(0xFF38BDF8))
            .clip(pillShape)
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = pillShape,
        border = BorderStroke(
            1.3.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF38BDF8),
                    Color(0xFF0EA5E9),
                    Color(0xFF7DD3FC)
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
                            Color(0xFF072F54),
                            Color(0xFF0D5C9E),
                            Color(0xFF1D4ED8)
                        )
                    )
                )
                .padding(start = 7.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF38BDF8), OfficialTelegramDeepBlue)
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    TelegramVectorGlyph(
                        modifier = Modifier.size(15.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "›",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.88f),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun WhatsAppIconBadge(
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    WhatsAppOfficialIcon(
        modifier = modifier,
        withBrandCircle = true,
        circleSize = size,
        glyphSize = size * 0.62f
    )
}

@Composable
fun TelegramIconBadge(
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    TelegramOfficialIcon(
        modifier = modifier,
        withBrandCircle = true,
        circleSize = size,
        glyphSize = size * 0.60f
    )
}

@Composable
fun PremiumWhatsAppChip(
    text: String = "WhatsApp Direct",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumWhatsAppHeaderChip(
        text = text,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun PremiumTelegramChip(
    text: String = "Telegram Channel",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumTelegramHeaderChip(
        text = text,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun PremiumWhatsAppFabPill(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = OfficialWhatsAppLightGreen)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1FAF54), OfficialWhatsAppLightGreen, OfficialWhatsAppDarkGreen)
                    )
                )
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                WhatsAppVectorGlyph(
                    modifier = Modifier.size(18.dp),
                    tint = OfficialWhatsAppLightGreen
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun PremiumTelegramFabPill(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = OfficialTelegramLightBlue)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2AABEE), OfficialTelegramLightBlue, OfficialTelegramDarkBlue)
                    )
                )
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                TelegramVectorGlyph(
                    modifier = Modifier.size(17.dp),
                    tint = OfficialTelegramDeepBlue
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

private const val LIVE_CHAT_OFFICIAL_SVG_PATH =
    "M20 2H4C2.9 2 2 2.9 2 4V22L6 18H20C21.1 18 22 17.1 22 16V4C22 2.9 21.1 2 20 2ZM6 9H18V11H6V9ZM14 14H6V12H14V14ZM18 8H6V6H18V8Z"

@Composable
fun LiveSupportVectorGlyph(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    val rawPath = remember {
        PathParser().parsePathString(LIVE_CHAT_OFFICIAL_SVG_PATH).toPath().apply {
            fillType = PathFillType.EvenOdd
        }
    }
    Canvas(modifier = modifier) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val scaledPath = androidx.compose.ui.graphics.Path().apply {
            addPath(rawPath)
            transform(Matrix().apply { scale(scaleX, scaleY) })
        }
        drawPath(path = scaledPath, color = tint)
    }
}

@Composable
fun PremiumLiveSupportFabPill(
    title: String = "লাইভ সাপোর্ট চ্যাট",
    subtitle: String = "সরাসরি মেসেজ পাঠান",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFFFFD700))
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(
                listOf(
                    Color(0xFFFFF8B8),
                    Color(0xFFFFD700),
                    Color(0xFFD946EF)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4C1D95),
                            Color(0xFF6D28D9),
                            Color(0xFF3B0764)
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFF08A), Color(0xFFFFD700), Color(0xFFD97706))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                LiveSupportVectorGlyph(
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF1A0736)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFE57F)
                )
            }
        }
    }
}

