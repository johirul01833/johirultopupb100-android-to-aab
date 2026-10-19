package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
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
import com.example.data.local.TopupRepository
import com.example.data.model.TopupPackage
import com.example.ui.components.FreeFireGamingSquircle
import com.example.ui.components.TopupPackageCard
import com.example.ui.theme.GoldPrimary

@Composable
fun FreeFireScreen(
    onPackageClick: (TopupPackage) -> Unit,
    packages: List<TopupPackage> = TopupRepository.FREEFIRE_PACKAGES,
    modifier: Modifier = Modifier
) {
    val orangeAccent = Color(0xFFFF6D00)
    val roseAccent = Color(0xFFF43F5E)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Luxury Neon Glassmorphism Gaming Hero Header with Official FreeFireGamingSquircle
        item {
            val heroShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, heroShape, spotColor = orangeAccent.copy(alpha = 0.45f))
                    .clip(heroShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2D101E),
                                Color(0xFF170B28),
                                Color(0xFF250E1B)
                            )
                        )
                    )
                    .border(
                        width = 1.4.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                GoldPrimary.copy(alpha = 0.85f),
                                orangeAccent.copy(alpha = 0.80f),
                                roseAccent.copy(alpha = 0.75f)
                            )
                        ),
                        shape = heroShape
                    )
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    orangeAccent.copy(alpha = 0.25f),
                                    roseAccent.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.width * 0.50f,
                            center = Offset(size.width * 0.85f, size.height * 0.22f)
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
                            FreeFireGamingSquircle(size = 50.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                                            append("Gaming ")
                                        }
                                        withStyle(
                                            SpanStyle(
                                                brush = Brush.horizontalGradient(
                                                    listOf(GoldPrimary, orangeAccent, roseAccent)
                                                ),
                                                fontWeight = FontWeight.Black
                                            )
                                        ) {
                                            append("Top-Up (Free Fire)")
                                        }
                                    },
                                    fontSize = 17.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Player UID Instant Top-Up • Memberships",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFDBA74)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = orangeAccent.copy(alpha = 0.20f),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.65f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "UID ONLY",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ফ্রি ফায়ার ডায়মন্ড, সাপ্তাহিক ও মাসিক মেম্বারশিপ টপ-আপ করুন শুধুমাত্র আপনার Player UID দিয়ে ২ থেকে ৫ মিনিটে!",
                        fontSize = 11.5.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 17.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 2. Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FreeFireGamingSquircle(size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                                append("ডায়মন্ড ও মেম্বারশিপ ")
                            }
                            withStyle(SpanStyle(color = GoldPrimary, fontWeight = FontWeight.Black)) {
                                append("(Packages)")
                            }
                        },
                        fontSize = 15.sp
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = orangeAccent.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, orangeAccent.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${packages.size} DEALS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 3. Grid of Gaming packages (2 columns)
        val chunked = packages.chunked(2)

        items(chunked) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                TopupPackageCard(
                    pkg = pair[0],
                    onBuyClick = onPackageClick,
                    modifier = Modifier.weight(1f)
                )
                if (pair.size > 1) {
                    TopupPackageCard(
                        pkg = pair[1],
                        onBuyClick = onPackageClick,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(11.dp))
        }
    }
}
