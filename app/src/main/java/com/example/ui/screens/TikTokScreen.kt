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
import com.example.ui.components.TikTokReferenceSquircle
import com.example.ui.components.TopupPackageCard
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import java.util.Locale

@Composable
fun TikTokScreen(
    customCoinsInput: String,
    onCustomCoinsChange: (String) -> Unit,
    onPackageClick: (TopupPackage) -> Unit,
    packages: List<TopupPackage> = TopupRepository.TIKTOK_PACKAGES,
    coinRate: Double = 1.80,
    modifier: Modifier = Modifier
) {
    val coinsDouble = customCoinsInput.toDoubleOrNull() ?: 0.0
    val customPrice = coinsDouble * coinRate
    val quickCoinPresets = listOf("100", "250", "500", "1000", "2500", "5000")
    val cyanAccent = Color(0xFF00F2FE)
    val pinkAccent = Color(0xFFFF2CF3)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Luxury Neon Glassmorphism TikTok Hero Banner with Official Squircle Logo
        item {
            val heroShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 14.dp,
                        shape = heroShape,
                        spotColor = cyanAccent.copy(alpha = 0.45f)
                    )
                    .clip(heroShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1A103C),
                                Color(0xFF0E0B26),
                                Color(0xFF19092C)
                            )
                        )
                    )
                    .border(
                        width = 1.4.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                cyanAccent.copy(alpha = 0.85f),
                                pinkAccent.copy(alpha = 0.75f),
                                GoldPrimary.copy(alpha = 0.80f)
                            )
                        ),
                        shape = heroShape
                    )
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    cyanAccent.copy(alpha = 0.22f),
                                    pinkAccent.copy(alpha = 0.10f),
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
                            TikTokReferenceSquircle(size = 50.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                                            append("TikTok ")
                                        }
                                        withStyle(
                                            SpanStyle(
                                                brush = Brush.horizontalGradient(
                                                    listOf(cyanAccent, Color(0xFFFFE57F), pinkAccent)
                                                ),
                                                fontWeight = FontWeight.Black
                                            )
                                        ) {
                                            append("Coin Store")
                                        }
                                    },
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "UID / Username Instant Top-Up • 100% Safe",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF93C5FD)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF0A281E).copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.6f))
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
                                    text = "INSTANT",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF86EFAC)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "টিকটক কয়েন রিচার্জ করতে আপনার TikTok Username বা UID দিন। কোনো পাসওয়ার্ড প্রয়োজন নেই — ২ থেকে ৫ মিনিটে দ্রুত ডেলিভারি।",
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
                        TikTokInfoPill(
                            label = "Base Rate",
                            value = "100 🪙 = ৳180",
                            accent = GoldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        TikTokInfoPill(
                            label = "Delivery",
                            value = "⚡ 2-5 Mins",
                            accent = cyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                        TikTokInfoPill(
                            label = "Security",
                            value = "🛡️ No Login",
                            accent = pinkAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Premium Custom Coin Calculator Box with Official Squircles & Preset Chips
        item {
            val calcShape = RoundedCornerShape(20.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, calcShape, spotColor = GoldPrimary.copy(alpha = 0.35f))
                    .clip(calcShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E1038),
                                Color(0xFF110924),
                                Color(0xFF170B2C)
                            )
                        )
                    )
                    .border(
                        width = 1.3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                GoldPrimary.copy(alpha = 0.80f),
                                cyanAccent.copy(alpha = 0.55f),
                                pinkAccent.copy(alpha = 0.65f)
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
                                    text = "কাস্টম কয়েন ক্যালকুলেটর",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "যেকোনো পরিমাণ কয়েন হিসাব করে অর্ডার করুন",
                                    fontSize = 10.5.sp,
                                    color = GoldLight
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = GoldPrimary.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.65f))
                        ) {
                            Text(
                                text = "৳${String.format(Locale.US, "%.2f", coinRate)} / Coin",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary,
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
                            value = customCoinsInput,
                            onValueChange = { onCustomCoinsChange(it.filter { ch -> ch.isDigit() }) },
                            modifier = Modifier.weight(1.1f),
                            label = { Text("Coin Amount", color = Color(0xFFCBD5E1), fontSize = 11.sp) },
                            placeholder = { Text("e.g. 750", color = Color(0xFF64748B)) },
                            leadingIcon = { TikTokReferenceSquircle(size = 24.dp) },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cyanAccent,
                                unfocusedBorderColor = Color(0xFF3B1D64),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0A0518),
                                unfocusedContainerColor = Color(0xFF0A0518)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Surface(
                            modifier = Modifier.weight(0.9f),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0C061E),
                            border = BorderStroke(
                                1.2.dp,
                                Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.7f), pinkAccent.copy(alpha = 0.5f)))
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 11.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "মোট মূল্য (BDT)",
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickCoinPresets.forEach { preset ->
                            val isSelected = customCoinsInput == preset
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable { onCustomCoinsChange(preset) },
                                shape = RoundedCornerShape(9.dp),
                                color = if (isSelected) cyanAccent.copy(alpha = 0.22f) else Color(0xFF0D0720),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) cyanAccent else Color(0xFF2E1A4D)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = preset,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) cyanAccent else Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }
                    }

                    if (coinsDouble > 0) {
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
                                            Color(0xFF7C3AED),
                                            Color(0xFFD946EF),
                                            Color(0xFFFF2CF3)
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(Color.White.copy(alpha = 0.6f), GoldPrimary.copy(alpha = 0.8f))
                                    ),
                                    btnShape
                                )
                                .clickable {
                                    onPackageClick(
                                        TopupPackage(
                                            id = "tt_custom_${coinsDouble.toInt()}",
                                            category = ServiceCategory.TIKTOK,
                                            name = "${coinsDouble.toInt()} Custom Coins",
                                            subtitle = "TikTok Custom Pack",
                                            amount = "${coinsDouble.toInt()} Coins",
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
                                    text = "অর্ডার করুন (${coinsDouble.toInt()} Coins = ৳${String.format(Locale.US, "%,.0f", customPrice)})",
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

        // 3. Section Header: Available Packages
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TikTokReferenceSquircle(size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                                append("সব কয়েন প্যাকেজ ")
                            }
                            withStyle(SpanStyle(color = GoldPrimary, fontWeight = FontWeight.Black)) {
                                append("(TikTok Packages)")
                            }
                        },
                        fontSize = 15.sp
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = cyanAccent.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, cyanAccent.copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = cyanAccent,
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

        // 4. Grid of TikTok packages (2 columns)
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
private fun TikTokInfoPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF090618).copy(alpha = 0.85f),
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
