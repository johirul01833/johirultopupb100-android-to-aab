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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
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
import com.example.data.model.EpayRateTier
import com.example.ui.components.CalculateReferenceSquircle
import com.example.ui.components.EpayReferenceSquircle
import com.example.ui.components.ExchangeReferenceSquircle
import com.example.ui.components.PremiumTelegramButton
import com.example.ui.components.PremiumWhatsAppButton
import com.example.ui.theme.BlueEpay
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SuccessGreen
import java.util.Locale

@Composable
fun EpayScreen(
    epayUsdInput: String,
    onEpayUsdChange: (String) -> Unit,
    onOpenOrderDialog: () -> Unit,
    onSubmitEpayWhatsApp: () -> Unit,
    onSubmitEpayTelegram: () -> Unit,
    epayEmail: String = "johirulagency01833@gmail.com",
    onCopyEmail: (String) -> Unit = {},
    epayTiers: List<EpayRateTier> = TopupRepository.EPAY_TIERS,
    onCalculateRate: (Double) -> Pair<Double, Double> = { TopupRepository.calculateEpayBdt(it) },
    modifier: Modifier = Modifier
) {
    val usd = epayUsdInput.toDoubleOrNull() ?: 0.0
    val (bdtAmount, appliedRate) = onCalculateRate(usd)
    val quickAmounts = listOf("10", "20", "50", "100", "200", "500")
    val epayBlue = Color(0xFF0099FF)
    val epayGreen = Color(0xFF28E040)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Luxury Neon Glassmorphism EPAY Hero Card with Official EpayReferenceSquircle Logo
        item {
            val heroShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, heroShape, spotColor = epayBlue.copy(alpha = 0.45f))
                    .clip(heroShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0E1D42),
                                Color(0xFF0A122C),
                                Color(0xFF100B26)
                            )
                        )
                    )
                    .border(
                        width = 1.4.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                epayBlue.copy(alpha = 0.85f),
                                epayGreen.copy(alpha = 0.65f),
                                GoldPrimary.copy(alpha = 0.80f)
                            )
                        ),
                        shape = heroShape
                    )
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    epayBlue.copy(alpha = 0.25f),
                                    epayGreen.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.width * 0.52f,
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
                            EpayReferenceSquircle(size = 50.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                                            append("EPAY ")
                                        }
                                        withStyle(
                                            SpanStyle(
                                                brush = Brush.horizontalGradient(
                                                    listOf(Color(0xFF38BDF8), epayGreen, GoldPrimary)
                                                ),
                                                fontWeight = FontWeight.Black
                                            )
                                        ) {
                                            append("Dollar Exchange")
                                        }
                                    },
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ইপে ডলার ভাঙিয়ে ইনস্ট্যান্ট বিকাশ / নগদ পেমেন্ট নিন",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF93C5FD)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0A281E).copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, epayGreen.copy(alpha = 0.65f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = epayGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "LIVE RATE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF86EFAC)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(13.dp),
                            color = Color(0xFF080E22).copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, epayBlue.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
                                Text(text = "সর্বনিম্ন এক্সচেঞ্জ", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "$9.70 USD", fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(13.dp),
                            color = Color(0xFF080E22).copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
                                Text(text = "পেমেন্ট মাধ্যম", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "bKash / Nagad", fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold, color = GoldPrimary)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Official EPAY Email Card with 1-click Copy & Official Squircle
        item {
            val emailShape = RoundedCornerShape(20.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, emailShape, spotColor = epayBlue.copy(alpha = 0.35f))
                    .clip(emailShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0F1B38), Color(0xFF0A1024))
                        )
                    )
                    .border(
                        width = 1.3.dp,
                        brush = Brush.horizontalGradient(
                            listOf(epayBlue.copy(alpha = 0.8f), GoldPrimary.copy(alpha = 0.7f))
                        ),
                        shape = emailShape
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(epayBlue.copy(alpha = 0.18f))
                                    .border(1.dp, epayBlue.copy(alpha = 0.55f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Official EPAY Account Email",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "ডলার পাঠানোর জন্য এই ইমেইলটি ব্যবহার করুন",
                                    fontSize = 11.sp,
                                    color = Color(0xFF93C5FD)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = SuccessGreen.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.65f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Verified",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Copy Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onCopyEmail(epayEmail) },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF060B18),
                        border = BorderStroke(
                            1.1.dp,
                            Brush.horizontalGradient(listOf(epayBlue.copy(alpha = 0.7f), GoldPrimary.copy(alpha = 0.5f)))
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "EPAY RECEIVER GMAIL",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    letterSpacing = 0.6.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = epayEmail,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0066FF), Color(0xFF0099FF))
                                        )
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 11.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Email",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Copy",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. Live Rate Tiers Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EpayReferenceSquircle(size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                                append("বর্তমান EPAY ডলার রেট ")
                            }
                            withStyle(SpanStyle(color = GoldPrimary, fontWeight = FontWeight.Black)) {
                                append("(Live Rates)")
                            }
                        },
                        fontSize = 14.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = GoldPrimary.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "TODAY'S RATE",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 4. Rate Tier Cards
        items(epayTiers) { tier ->
            val tierShape = RoundedCornerShape(15.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .shadow(
                        elevation = if (tier.isPremium) 10.dp else 6.dp,
                        shape = tierShape,
                        spotColor = if (tier.isPremium) GoldPrimary.copy(alpha = 0.35f) else epayBlue.copy(alpha = 0.25f)
                    )
                    .clip(tierShape)
                    .background(
                        Brush.horizontalGradient(
                            if (tier.isPremium) {
                                listOf(Color(0xFF1E294B), Color(0xFF121832))
                            } else {
                                listOf(Color(0xFF101930), Color(0xFF0A1022))
                            }
                        )
                    )
                    .border(
                        width = if (tier.isPremium) 1.3.dp else 1.dp,
                        brush = Brush.horizontalGradient(
                            if (tier.isPremium) {
                                listOf(GoldPrimary.copy(alpha = 0.85f), epayGreen.copy(alpha = 0.6f))
                            } else {
                                listOf(epayBlue.copy(alpha = 0.55f), Color(0xFF3B82F6).copy(alpha = 0.35f))
                            }
                        ),
                        shape = tierShape
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EpayReferenceSquircle(size = 34.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tier.rangeLabel,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                if (tier.isPremium) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = GoldPrimary.copy(alpha = 0.2f),
                                        border = BorderStroke(0.8.dp, GoldPrimary.copy(alpha = 0.7f))
                                    ) {
                                        Text(
                                            text = "VIP",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GoldPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Rate: ${tier.rateText}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF93C5FD)
                            )
                        }
                    }

                    Text(
                        text = "৳${String.format(Locale.US, "%,.0f", tier.bdtCalculated ?: 0.0)}",
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (tier.isPremium) GoldPrimary else Color(0xFF38BDF8)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 5. Interactive Live Converter Box with Official Squircles
        item {
            val convShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, convShape, spotColor = epayBlue.copy(alpha = 0.4f))
                    .clip(convShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF171038),
                                Color(0xFF0E132C),
                                Color(0xFF110924)
                            )
                        )
                    )
                    .border(
                        width = 1.4.dp,
                        brush = Brush.linearGradient(
                            listOf(GoldPrimary.copy(alpha = 0.85f), epayBlue.copy(alpha = 0.8f), epayGreen.copy(alpha = 0.65f))
                        ),
                        shape = convShape
                    )
                    .padding(18.dp)
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
                                    text = "লাইভ ডলার এক্সচেঞ্জ ক্যালকুলেটর",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "ডলার পরিমাণ বসিয়ে ইনস্ট্যান্ট হিসাব দেখুন",
                                    fontSize = 10.5.sp,
                                    color = GoldLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "কত ডলার এক্সচেঞ্জ করতে চান? (USD Amount)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = epayUsdInput,
                        onValueChange = onEpayUsdChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("উদাহরণ: 10, 20, 50...", color = Color(0xFF64748B)) },
                        leadingIcon = { EpayReferenceSquircle(size = 24.dp) },
                        trailingIcon = {
                            Text(
                                "USD",
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = epayBlue,
                            unfocusedBorderColor = Color(0xFF381B5E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF090618),
                            unfocusedContainerColor = Color(0xFF090618)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Selection Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickAmounts.forEach { amount ->
                            val isSelected = epayUsdInput == amount
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable { onEpayUsdChange(amount) },
                                shape = RoundedCornerShape(9.dp),
                                color = if (isSelected) GoldPrimary else Color(0xFF140D2B),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldPrimary else Color(0xFF321D58)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$$amount",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color.Black else Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calculation Summary Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        color = Color(0xFF0B071E),
                        border = BorderStroke(
                            1.2.dp,
                            Brush.horizontalGradient(listOf(epayBlue.copy(alpha = 0.6f), GoldPrimary.copy(alpha = 0.6f)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Applied Rate:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = "৳${String.format(Locale.US, "%.1f", appliedRate)} / USD",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "You Will Receive (মোট টাকা পাবেন):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "৳${String.format(Locale.US, "%,.0f", bdtAmount)} BDT",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Action: Neon Gradient Button with Official EPAY Squircle
                    val btnShape = RoundedCornerShape(14.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(10.dp, btnShape, spotColor = epayBlue)
                            .clip(btnShape)
                            .background(
                                Brush.horizontalGradient(
                                    if (usd >= 9.70) {
                                        listOf(Color(0xFF0066FF), Color(0xFF0099FF), Color(0xFF00C853))
                                    } else {
                                        listOf(Color(0xFF334155), Color(0xFF475569))
                                    }
                                )
                            )
                            .border(
                                1.dp,
                                Brush.horizontalGradient(
                                    listOf(Color.White.copy(alpha = 0.6f), GoldPrimary.copy(alpha = 0.7f))
                                ),
                                btnShape
                            )
                            .clickable { onOpenOrderDialog() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            EpayReferenceSquircle(size = 24.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "এক্সচেঞ্জ অর্ডার করুন (৳${String.format(Locale.US, "%,.0f", bdtAmount)} BDT পান)",
                                fontSize = 13.5.sp,
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "বা সরাসরি যোগাযোগ করতে পারেন:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PremiumWhatsAppButton(
                            text = "WhatsApp",
                            onClick = onSubmitEpayWhatsApp,
                            modifier = Modifier.weight(1f),
                            height = 46.dp,
                            cornerRadius = 12.dp,
                            fontSize = 13.sp,
                            iconSize = 22.dp
                        )

                        PremiumTelegramButton(
                            text = "Telegram",
                            onClick = onSubmitEpayTelegram,
                            modifier = Modifier.weight(1f),
                            height = 46.dp,
                            cornerRadius = 12.dp,
                            fontSize = 13.sp,
                            iconSize = 22.dp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 6. Instructions Note Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0E0920),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(epayBlue.copy(alpha = 0.45f), GoldPrimary.copy(alpha = 0.45f)))
                )
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ExchangeReferenceSquircle(size = 24.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EPAY Dollar Exchange নিয়মাবলী:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldLight
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "১. প্রথমে আপনার EPAY ডলার পরিমাণ নির্বাচন বা টাইপ করুন।\n" +
                               "২. আমাদের অফিসিয়াল ইমেইলে ($epayEmail) ডলার ট্রান্সফার করুন।\n" +
                               "৩. অর্ডার ফর্মে ২৪-ডিজিট অর্ডার নাম্বার ও বিকাশ/নগদ নাম্বার সাবমিট করুন।\n" +
                               "৪. ৫-১০ মিনিটের মধ্যে আপনার একাউন্টে টাকা পাঠিয়ে দেওয়া হবে।",
                        fontSize = 11.5.sp,
                        color = Color(0xFFD1D5DB),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
