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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TopupRepository
import com.example.data.model.ServiceCategory
import com.example.data.model.TopupPackage
import com.example.ui.components.CalculateReferenceSquircle
import com.example.ui.components.ChametReferenceSquircle
import com.example.ui.components.TopupPackageCard
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import java.util.Locale

@Composable
fun ChametScreen(
    customDiamondsInput: String,
    onCustomDiamondsChange: (String) -> Unit,
    onPackageClick: (TopupPackage) -> Unit,
    packages: List<TopupPackage> = TopupRepository.CHAMET_PACKAGES,
    diamondRate: Double = 0.023,
    modifier: Modifier = Modifier
) {
    val diamondsDouble = customDiamondsInput.toDoubleOrNull() ?: 0.0
    val customPrice = diamondsDouble * diamondRate
    val quickDiamondPresets = listOf("15000", "25000", "50000", "100000", "250000", "500000")
    val orangeAccent = Color(0xFFFF9100)
    val pinkAccent = Color(0xFFFF2CF3)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Luxury Neon Glassmorphism Chamet Hero Banner with Official Chamet Squircle Logo
        item {
            val heroShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 14.dp,
                        shape = heroShape,
                        spotColor = pinkAccent.copy(alpha = 0.45f)
                    )
                    .clip(heroShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2B103A),
                                Color(0xFF150826),
                                Color(0xFF1F0A2E)
                            )
                        )
                    )
                    .border(
                        width = 1.4.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                orangeAccent.copy(alpha = 0.85f),
                                pinkAccent.copy(alpha = 0.80f),
                                GoldPrimary.copy(alpha = 0.75f)
                            )
                        ),
                        shape = heroShape
                    )
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    pinkAccent.copy(alpha = 0.24f),
                                    orangeAccent.copy(alpha = 0.10f),
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
                            ChametReferenceSquircle(size = 50.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                                            append("Chamet ")
                                        }
                                        withStyle(
                                            SpanStyle(
                                                brush = Brush.horizontalGradient(
                                                    listOf(orangeAccent, Color(0xFFFFE57F), pinkAccent)
                                                ),
                                                fontWeight = FontWeight.Black
                                            )
                                        ) {
                                            append("VIP Diamonds")
                                        }
                                    },
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Direct Chamet ID Top-Up • Streamer Rates",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF472B6)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = pinkAccent.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, pinkAccent.copy(alpha = 0.65f))
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
                                    text = "VIP DEAL",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "চ্যামেট ডায়মন্ড দ্রুত টপ-আপের জন্য শুধুমাত্র আপনার Chamet User ID প্রদান করুন। লাইভ স্ট্রিমিং ও গিফটিংয়ের জন্য বাংলাদেশের সেরা রেট।",
                        fontSize = 11.5.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mini Stat Pills Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ChametInfoPill(
                            label = "Starter Deal",
                            value = "15K 💎 = ৳360",
                            accent = orangeAccent,
                            modifier = Modifier.weight(1f)
                        )
                        ChametInfoPill(
                            label = "VIP Pack",
                            value = "100K 💎 = ৳2,300",
                            accent = pinkAccent,
                            modifier = Modifier.weight(1f)
                        )
                        ChametInfoPill(
                            label = "Method",
                            value = "⚡ User ID Only",
                            accent = GoldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Premium Custom Diamond Calculator Box with Official Squircles & Preset Chips
        item {
            val calcShape = RoundedCornerShape(20.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, calcShape, spotColor = NeonMagenta.copy(alpha = 0.38f))
                    .clip(calcShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF220D38),
                                Color(0xFF120822),
                                Color(0xFF1A0A2E)
                            )
                        )
                    )
                    .border(
                        width = 1.3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                pinkAccent.copy(alpha = 0.80f),
                                orangeAccent.copy(alpha = 0.65f),
                                NeonPurple.copy(alpha = 0.70f)
                            )
                        ),
                        shape = calcShape
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CalculateReferenceSquircle(size = 34.dp)
                            Spacer(modifier = Modifier.width(9.dp))
                            Column {
                                Text(
                                    text = "কাস্টম ডায়মন্ড ক্যালকুলেটর",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "পছন্দমতো ডায়মন্ড সংখ্যা বসিয়ে দাম দেখুন",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFFF472B6)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = orangeAccent.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, orangeAccent.copy(alpha = 0.65f))
                        ) {
                            Text(
                                text = "Auto Calc",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldLight,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customDiamondsInput,
                            onValueChange = { onCustomDiamondsChange(it.filter { ch -> ch.isDigit() }) },
                            modifier = Modifier.weight(1.1f),
                            label = { Text("Diamond Count", color = Color(0xFFCBD5E1), fontSize = 11.sp) },
                            placeholder = { Text("e.g. 75000", color = Color(0xFF64748B)) },
                            leadingIcon = { ChametReferenceSquircle(size = 24.dp) },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = pinkAccent,
                                unfocusedBorderColor = Color(0xFF431C68),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0B0518),
                                unfocusedContainerColor = Color(0xFF0B0518)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Surface(
                            modifier = Modifier.weight(0.9f),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF100620),
                            border = BorderStroke(
                                1.2.dp,
                                Brush.linearGradient(listOf(pinkAccent.copy(alpha = 0.75f), GoldPrimary.copy(alpha = 0.6f)))
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 11.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "আনুমানিক মূল্য",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFCBD5E1)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "৳${String.format(Locale.US, "%,.0f", customPrice)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Preset Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        quickDiamondPresets.forEach { preset ->
                            val isSelected = customDiamondsInput == preset
                            val shortLabel = "${(preset.toIntOrNull() ?: 0) / 1000}K"
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable { onCustomDiamondsChange(preset) },
                                shape = RoundedCornerShape(9.dp),
                                color = if (isSelected) pinkAccent.copy(alpha = 0.25f) else Color(0xFF0E0720),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) pinkAccent else Color(0xFF361956)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = shortLabel,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color.White else Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }
                    }

                    if (diamondsDouble > 0) {
                        Spacer(modifier = Modifier.height(12.dp))

                        val btnShape = RoundedCornerShape(14.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .shadow(10.dp, btnShape, spotColor = pinkAccent)
                                .clip(btnShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFF6D00),
                                            Color(0xFFEC4899),
                                            Color(0xFF9333EA)
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(GoldPrimary.copy(alpha = 0.85f), Color.White.copy(alpha = 0.6f))
                                    ),
                                    btnShape
                                )
                                .clickable {
                                    onPackageClick(
                                        TopupPackage(
                                            id = "ch_custom_${diamondsDouble.toInt()}",
                                            category = ServiceCategory.CHAMET,
                                            name = "${diamondsDouble.toInt()} Diamonds",
                                            subtitle = "Chamet Custom Pack",
                                            amount = "${diamondsDouble.toInt()} Diamonds",
                                            priceBdt = customPrice,
                                            badge = "Custom"
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "অর্ডার করুন (${diamondsDouble.toInt()} 💎 = ৳${String.format(Locale.US, "%,.0f", customPrice)})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
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
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. Section Header: Available Chamet Packages
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChametReferenceSquircle(size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                                append("সব চ্যামেট প্যাকেজ ")
                            }
                            withStyle(SpanStyle(color = GoldPrimary, fontWeight = FontWeight.Black)) {
                                append("(Chamet Deals)")
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
                            text = "${packages.size} PACKS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 4. Grid of Chamet packages (2 columns)
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

@Composable
private fun ChametInfoPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0A0516).copy(alpha = 0.85f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = value,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}
