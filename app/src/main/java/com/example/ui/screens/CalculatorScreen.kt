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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.CalculateReferenceSquircle
import com.example.ui.components.ChametReferenceSquircle
import com.example.ui.components.EpayReferenceSquircle
import com.example.ui.components.ExchangeReferenceSquircle
import com.example.ui.components.PremiumTelegramButton
import com.example.ui.components.PremiumWhatsAppButton
import com.example.ui.components.TikTokReferenceSquircle
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import java.util.Locale

enum class CalculatorCategory(val title: String) {
    EPAY("EPAY"),
    TIKTOK("TikTok"),
    CHAMET("Chamet"),
    EXCHANGE("BD➔IN")
}

@Composable
fun CalculatorScreen(
    onWhatsAppDirect: (String) -> Unit,
    onTelegramDirect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCat by remember { mutableStateOf(CalculatorCategory.EPAY) }

    // EPAY state
    var epayUsdInput by remember { mutableStateOf("10") }

    // TikTok state
    var tikTokCoinsInput by remember { mutableStateOf("100") }

    // Chamet state
    var chametDiamondsInput by remember { mutableStateOf("15000") }

    // Exchange BD -> IN state
    var exchangeBdtInput by remember { mutableStateOf("1000") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Luxury Neon Glassmorphism Hero Banner with Official CalculateReferenceSquircle
        item {
            val heroShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, heroShape, spotColor = GoldPrimary.copy(alpha = 0.4f))
                    .clip(heroShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF241438),
                                Color(0xFF130B26),
                                Color(0xFF1A0D2E)
                            )
                        )
                    )
                    .border(
                        1.4.dp,
                        Brush.linearGradient(
                            listOf(
                                GoldPrimary.copy(alpha = 0.85f),
                                NeonMagenta.copy(alpha = 0.75f),
                                Color(0xFF38BDF8).copy(alpha = 0.70f)
                            )
                        ),
                        heroShape
                    )
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GoldPrimary.copy(alpha = 0.22f),
                                    NeonMagenta.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.width * 0.50f,
                            center = Offset(size.width * 0.85f, size.height * 0.20f)
                        )
                    }
                    .padding(16.dp)
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
                        CalculateReferenceSquircle(size = 50.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                                        append("All-In-One ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            brush = Brush.horizontalGradient(
                                                listOf(GoldPrimary, Color(0xFFFFE57F), NeonMagenta)
                                            ),
                                            fontWeight = FontWeight.Black
                                        )
                                    ) {
                                        append("Rate Calculator")
                                    }
                                },
                                fontSize = 17.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "যেকোনো সার্ভিসের সঠিক রেট ও মোট মূল্য এক ক্লিকে বের করুন।",
                                fontSize = 11.5.sp,
                                color = Color(0xFFD8B4FE),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = GoldPrimary.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.65f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "AUTO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. Calculator Category Selection Tabs with Official Brand Squircle Logos
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                CalculatorCategory.values().forEach { cat ->
                    val isSelected = selectedCat == cat
                    val accentColor = when (cat) {
                        CalculatorCategory.EPAY -> Color(0xFF0099FF)
                        CalculatorCategory.TIKTOK -> Color(0xFF00F2FE)
                        CalculatorCategory.CHAMET -> Color(0xFFFF2CF3)
                        CalculatorCategory.EXCHANGE -> Color(0xFF22C55E)
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(
                                elevation = if (isSelected) 10.dp else 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                spotColor = accentColor.copy(alpha = 0.45f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedCat = cat },
                        color = if (isSelected) Color(0xFF1F113B) else Color(0xFF0F0820),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            if (isSelected) 1.4.dp else 1.dp,
                            if (isSelected) {
                                Brush.linearGradient(listOf(accentColor, GoldPrimary))
                            } else {
                                Brush.linearGradient(listOf(Color(0xFF2E1A4D), Color(0xFF1E1235)))
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (cat) {
                                CalculatorCategory.EPAY -> EpayReferenceSquircle(size = 26.dp)
                                CalculatorCategory.TIKTOK -> TikTokReferenceSquircle(size = 26.dp)
                                CalculatorCategory.CHAMET -> ChametReferenceSquircle(size = 26.dp)
                                CalculatorCategory.EXCHANGE -> ExchangeReferenceSquircle(size = 26.dp)
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = cat.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) GoldPrimary else Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Active Calculator Panel
        item {
            when (selectedCat) {
                CalculatorCategory.EPAY -> {
                    EpayCalculatorCard(
                        usdInput = epayUsdInput,
                        onUsdChange = { epayUsdInput = it },
                        onWhatsApp = { msg -> onWhatsAppDirect(msg) },
                        onTelegram = { msg -> onTelegramDirect(msg) }
                    )
                }

                CalculatorCategory.TIKTOK -> {
                    TikTokCalculatorCard(
                        coinsInput = tikTokCoinsInput,
                        onCoinsChange = { tikTokCoinsInput = it },
                        onWhatsApp = { msg -> onWhatsAppDirect(msg) },
                        onTelegram = { msg -> onTelegramDirect(msg) }
                    )
                }

                CalculatorCategory.CHAMET -> {
                    ChametCalculatorCard(
                        diamondsInput = chametDiamondsInput,
                        onDiamondsChange = { chametDiamondsInput = it },
                        onWhatsApp = { msg -> onWhatsAppDirect(msg) },
                        onTelegram = { msg -> onTelegramDirect(msg) }
                    )
                }

                CalculatorCategory.EXCHANGE -> {
                    ExchangeCalculatorCard(
                        bdtInput = exchangeBdtInput,
                        onBdtChange = { exchangeBdtInput = it },
                        onWhatsApp = { msg -> onWhatsAppDirect(msg) },
                        onTelegram = { msg -> onTelegramDirect(msg) }
                    )
                }
            }
        }

        // 4. Verified Payment Accounts Box
        item {
            Spacer(modifier = Modifier.height(18.dp))

            val accShape = RoundedCornerShape(18.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, accShape, spotColor = GoldPrimary.copy(alpha = 0.3f))
                    .clip(accShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1D0D36), Color(0xFF0E0720))
                        )
                    )
                    .border(
                        1.2.dp,
                        Brush.horizontalGradient(
                            listOf(GoldPrimary.copy(alpha = 0.7f), NeonMagenta.copy(alpha = 0.6f))
                        ),
                        accShape
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💳 অফিশিয়াল পেমেন্ট নাম্বার (বিকাশ ও নগদ)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0A0518),
                            border = BorderStroke(1.dp, Color(0xFFEC4899).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "🔴 bKash Personal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF472B6))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "01733256802", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0A0518),
                            border = BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "🟠 Nagad Personal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFB923C))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "01874-954130", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EpayCalculatorCard(
    usdInput: String,
    onUsdChange: (String) -> Unit,
    onWhatsApp: (String) -> Unit,
    onTelegram: (String) -> Unit
) {
    val usd = usdInput.toDoubleOrNull() ?: 0.0
    val (bdt, _) = TopupRepository.calculateEpayBdt(usd)

    val rateNote = when {
        usd in 9.69..9.71 -> "Fixed package: 9.70 USD = ৳1,170"
        usd in 9.99..10.01 -> "Fixed package: 10 USD = ৳1,200"
        usd < 30.0 -> "Applied rate: ৳121 / USD"
        usd < 500.0 -> "Applied rate: ৳122 / USD (Bulk)"
        else -> "Applied rate: ৳123 / USD (Wholesale)"
    }

    val cardShape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, cardShape, spotColor = Color(0xFF0099FF).copy(alpha = 0.4f))
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0E1D42), Color(0xFF0A1128), Color(0xFF110A24))
                )
            )
            .border(
                1.4.dp,
                Brush.linearGradient(
                    listOf(Color(0xFF0099FF).copy(alpha = 0.85f), Color(0xFF28E040).copy(alpha = 0.6f), GoldPrimary.copy(alpha = 0.7f))
                ),
                cardShape
            )
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EpayReferenceSquircle(size = 38.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "EPAY Dollar ➔ bKash/Nagad BDT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Official Live USD Exchange Rate",
                        fontSize = 10.5.sp,
                        color = Color(0xFF93C5FD)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "ডলার পরিমাণ (USD Amount):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = usdInput,
                onValueChange = { onUsdChange(it.filter { c -> c.isDigit() || c == '.' }) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 10, 20, 50, 100", color = Color(0xFF64748B)) },
                leadingIcon = { EpayReferenceSquircle(size = 24.dp) },
                trailingIcon = { Text("USD", fontWeight = FontWeight.Black, color = Color(0xFF38BDF8), modifier = Modifier.padding(end = 12.dp)) },
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF1E3A5F),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF070D1D),
                    unfocusedContainerColor = Color(0xFF070D1D)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                color = Color(0xFF070E1F),
                border = BorderStroke(1.2.dp, Color(0xFF0099FF).copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "মোট টাকার পরিমাণ (BDT):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "৳${String.format(Locale.US, "%,.0f", bdt)} BDT",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = rateNote, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumWhatsAppButton(
                    text = "WhatsApp",
                    onClick = {
                        val msg = "💸 EPAY Dollar Exchange Request:\nডলার পরিমাণ: $usd USD\nমোট টাকা: ৳${String.format(Locale.US, "%,.0f", bdt)}\nরেট: $rateNote\n\nআমি এক্সচেঞ্জ করতে চাই।"
                        onWhatsApp(msg)
                    },
                    modifier = Modifier.weight(1f),
                    height = 48.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )

                PremiumTelegramButton(
                    text = "Telegram",
                    onClick = {
                        val msg = "💸 EPAY Dollar Exchange Request:\nডলার পরিমাণ: $usd USD\nমোট টাকা: ৳${String.format(Locale.US, "%,.0f", bdt)}\nরেট: $rateNote\n\nআমি এক্সচেঞ্জ করতে চাই।"
                        onTelegram(msg)
                    },
                    modifier = Modifier.weight(1f),
                    height = 48.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )
            }
        }
    }
}

@Composable
private fun TikTokCalculatorCard(
    coinsInput: String,
    onCoinsChange: (String) -> Unit,
    onWhatsApp: (String) -> Unit,
    onTelegram: (String) -> Unit
) {
    val coins = coinsInput.toLongOrNull() ?: 0L
    val bdt = coins * 1.80
    val cardShape = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, cardShape, spotColor = Color(0xFF00F2FE).copy(alpha = 0.4f))
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1A103C), Color(0xFF0E0A24), Color(0xFF190A2D))
                )
            )
            .border(
                1.4.dp,
                Brush.linearGradient(
                    listOf(Color(0xFF00F2FE).copy(alpha = 0.85f), GoldPrimary.copy(alpha = 0.75f), NeonMagenta.copy(alpha = 0.75f))
                ),
                cardShape
            )
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TikTokReferenceSquircle(size = 38.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "TikTok Coin Calculator",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "100 Coins = ৳180 • Instant UID Top-Up",
                        fontSize = 10.5.sp,
                        color = GoldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "কয়েন সংখ্যা (Coin Amount):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = coinsInput,
                onValueChange = { onCoinsChange(it.filter { c -> c.isDigit() }) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 100, 500, 1000", color = Color(0xFF64748B)) },
                leadingIcon = { TikTokReferenceSquircle(size = 24.dp) },
                trailingIcon = { Text("Coins", fontWeight = FontWeight.Black, color = GoldPrimary, modifier = Modifier.padding(end = 12.dp)) },
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00F2FE),
                    unfocusedBorderColor = Color(0xFF3B1E62),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF090618),
                    unfocusedContainerColor = Color(0xFF090618)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                color = Color(0xFF0A061C),
                border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "মোট মূল্য (BDT):", fontSize = 11.sp, color = Color(0xFFD1D5DB))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "৳${String.format(Locale.US, "%,.0f", bdt)} BDT",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "রেট: 100 Coins = ৳180 (৳1.80 / Coin)", fontSize = 11.sp, color = Color(0xFF00F2FE))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumWhatsAppButton(
                    text = "WhatsApp",
                    onClick = {
                        val msg = "🪙 TikTok Coin Top-up Request:\nকয়েন পরিমাণ: $coins Coins\nমোট টাকা: ৳${String.format(Locale.US, "%,.0f", bdt)}\nরেট: ৳1.80/Coin\n\nআমি কয়েন টপআপ করতে চাই।"
                        onWhatsApp(msg)
                    },
                    modifier = Modifier.weight(1f),
                    height = 48.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )

                PremiumTelegramButton(
                    text = "Telegram",
                    onClick = {
                        val msg = "🪙 TikTok Coin Top-up Request:\nকয়েন পরিমাণ: $coins Coins\nমোট টাকা: ৳${String.format(Locale.US, "%,.0f", bdt)}\nরেট: ৳1.80/Coin\n\nআমি কয়েন টপআপ করতে চাই।"
                        onTelegram(msg)
                    },
                    modifier = Modifier.weight(1f),
                    height = 48.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )
            }
        }
    }
}

@Composable
private fun ChametCalculatorCard(
    diamondsInput: String,
    onDiamondsChange: (String) -> Unit,
    onWhatsApp: (String) -> Unit,
    onTelegram: (String) -> Unit
) {
    val diamonds = diamondsInput.toLongOrNull() ?: 0L
    val bdt = (diamonds / 1000.0) * 23.50
    val cardShape = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, cardShape, spotColor = NeonMagenta.copy(alpha = 0.45f))
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF28103A), Color(0xFF140824), Color(0xFF1E0A2E))
                )
            )
            .border(
                1.4.dp,
                Brush.linearGradient(
                    listOf(Color(0xFFFF9100).copy(alpha = 0.85f), NeonMagenta.copy(alpha = 0.8f), GoldPrimary.copy(alpha = 0.7f))
                ),
                cardShape
            )
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChametReferenceSquircle(size = 38.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Chamet Diamond Calculator",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "VIP Streamer Rates • Direct Chamet ID",
                        fontSize = 10.5.sp,
                        color = Color(0xFFF472B6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "ডায়মন্ড পরিমাণ (Diamond Amount):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = diamondsInput,
                onValueChange = { onDiamondsChange(it.filter { c -> c.isDigit() }) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 15000, 25000, 50000", color = Color(0xFF64748B)) },
                leadingIcon = { ChametReferenceSquircle(size = 24.dp) },
                trailingIcon = { Text("Diamonds", fontWeight = FontWeight.Black, color = Color(0xFFF472B6), modifier = Modifier.padding(end = 12.dp)) },
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonMagenta,
                    unfocusedBorderColor = Color(0xFF4C2176),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF0B0518),
                    unfocusedContainerColor = Color(0xFF0B0518)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                color = Color(0xFF0E051E),
                border = BorderStroke(1.2.dp, NeonMagenta.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "মোট মূল্য (BDT):", fontSize = 11.sp, color = Color(0xFFD1D5DB))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "৳${String.format(Locale.US, "%,.0f", bdt)} BDT",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "রেট: 15K = ৳360 | 100K = ৳2,300 (Instant UID)", fontSize = 11.sp, color = Color(0xFFF472B6))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumWhatsAppButton(
                    text = "WhatsApp",
                    onClick = {
                        val msg = "💎 Chamet Diamond Top-up Request:\nডায়মন্ড পরিমাণ: $diamonds Diamonds\nমোট টাকা: ৳${String.format(Locale.US, "%,.0f", bdt)}\n\nআমি ডায়মন্ড টপআপ করতে চাই।"
                        onWhatsApp(msg)
                    },
                    modifier = Modifier.weight(1f),
                    height = 48.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )

                PremiumTelegramButton(
                    text = "Telegram",
                    onClick = {
                        val msg = "💎 Chamet Diamond Top-up Request:\nডায়মন্ড পরিমাণ: $diamonds Diamonds\nমোট টাকা: ৳${String.format(Locale.US, "%,.0f", bdt)}\n\nআমি ডায়মন্ড টপআপ করতে চাই।"
                        onTelegram(msg)
                    },
                    modifier = Modifier.weight(1f),
                    height = 48.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )
            }
        }
    }
}

@Composable
private fun ExchangeCalculatorCard(
    bdtInput: String,
    onBdtChange: (String) -> Unit,
    onWhatsApp: (String) -> Unit,
    onTelegram: (String) -> Unit
) {
    val bdt = bdtInput.toDoubleOrNull() ?: 0.0
    val inr = bdt * 0.72
    val cardShape = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, cardShape, spotColor = SuccessGreen.copy(alpha = 0.4f))
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0D2824), Color(0xFF0A1724), Color(0xFF0E0C22))
                )
            )
            .border(
                1.4.dp,
                Brush.linearGradient(
                    listOf(SuccessGreen.copy(alpha = 0.85f), GoldPrimary.copy(alpha = 0.75f), Color(0xFF38BDF8).copy(alpha = 0.7f))
                ),
                cardShape
            )
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ExchangeReferenceSquircle(size = 38.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "BD 🇧🇩 ➔ India 🇮🇳 PhonePe/UPI Calc",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Instant bKash/Nagad to Indian UPI Transfer",
                        fontSize = 10.5.sp,
                        color = Color(0xFF86EFAC)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "টাকার পরিমাণ (BDT Amount):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = bdtInput,
                onValueChange = { onBdtChange(it.filter { c -> c.isDigit() || c == '.' }) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 1000, 5000, 10000", color = Color(0xFF64748B)) },
                leadingIcon = { ExchangeReferenceSquircle(size = 24.dp) },
                trailingIcon = { Text("BDT", fontWeight = FontWeight.Black, color = SuccessGreen, modifier = Modifier.padding(end = 12.dp)) },
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SuccessGreen,
                    unfocusedBorderColor = Color(0xFF1E4636),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF061612),
                    unfocusedContainerColor = Color(0xFF061612)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                color = Color(0xFF061914),
                border = BorderStroke(1.2.dp, SuccessGreen.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "ভারতে পাবেন (Estimated INR):", fontSize = 11.sp, color = Color(0xFFD1D5DB))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹${String.format(Locale.US, "%,.2f", inr)} INR",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF4ADE80)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "মার্কেট বেঞ্চমার্ক রেট: 1 BDT = ~0.72 INR (PhonePe / GPay / UPI)", fontSize = 11.sp, color = GoldLight)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumWhatsAppButton(
                    text = "WhatsApp",
                    onClick = {
                        val msg = "🔄 BD to India Money Exchange Request:\nটাকার পরিমাণ: ৳$bdt BDT\nআনুমানিক পাবেন: ₹${String.format(Locale.US, "%,.2f", inr)} INR\n\nআমি এক্সচেঞ্জ করতে চাই।"
                        onWhatsApp(msg)
                    },
                    modifier = Modifier.weight(1f),
                    height = 48.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )

                PremiumTelegramButton(
                    text = "Telegram",
                    onClick = {
                        val msg = "🔄 BD to India Money Exchange Request:\nটাকার পরিমাণ: ৳$bdt BDT\nআনুমানিক পাবেন: ₹${String.format(Locale.US, "%,.2f", inr)} INR\n\nআমি এক্সচেঞ্জ করতে চাই।"
                        onTelegram(msg)
                    },
                    modifier = Modifier.weight(1f),
                    height = 48.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )
            }
        }
    }
}
