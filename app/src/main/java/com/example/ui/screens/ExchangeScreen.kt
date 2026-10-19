package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CalculateReferenceSquircle
import com.example.ui.components.EpayReferenceSquircle
import com.example.ui.components.ExchangeReferenceSquircle
import com.example.ui.components.PremiumTelegramButton
import com.example.ui.components.PremiumWhatsAppButton
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WhatsAppGreen
import java.util.Locale

/**
 * Supported currency corridors for exchange
 */
enum class ExchangeCorridor(
    val title: String,
    val fromFlag: String,
    val toFlag: String,
    val fromCur: String,
    val toCur: String,
    val subtitle: String
) {
    BDT_TO_INR("BD ➔ India", "🇧🇩", "🇮🇳", "BDT", "INR", "PhonePe, GPay, Paytm, UPI"),
    INR_TO_BDT("India ➔ BD", "🇮🇳", "🇧🇩", "INR", "BDT", "বিকাশ, নগদ, রকেট"),
    USD_TO_BDT("USD ➔ BD", "💵", "🇧🇩", "USD", "BDT", "EPAY USD, Payeer, USDT"),
    BDT_TO_USD("BD ➔ USD", "🇧🇩", "💵", "BDT", "USD", "EPAY USD ক্রয়")
}

data class LiveExchangeItem(
    val title: String,
    val details: String,
    val timeAgo: String,
    val status: String = "সম্পন্ন"
)

@Composable
fun ExchangeScreen(
    sendMethod: String,
    onSendMethodChange: (String) -> Unit,
    receiveMethod: String,
    onReceiveMethodChange: (String) -> Unit,
    amountBdt: String,
    onAmountBdtChange: (String) -> Unit,
    recipientUpi: String,
    onRecipientUpiChange: (String) -> Unit,
    onOpenOrderDialog: () -> Unit,
    onSubmitExchangeWhatsApp: () -> Unit,
    onSubmitExchangeTelegram: () -> Unit,
    inrRate: Double = 1.45,
    epayRate: Double = 121.0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Active corridor
    var selectedCorridor by remember { mutableStateOf(ExchangeCorridor.BDT_TO_INR) }

    // Recipient Account Name / Additional Info
    var recipientName by remember { mutableStateOf("") }

    // FAQ expansion state
    var expandedFaqIndex by remember { mutableStateOf(-1) }

    // Pulsing animation for live ticker
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Parse numeric send amount
    val sendAmountNum = amountBdt.toDoubleOrNull() ?: 0.0

    // Effective rates
    val effectiveInrRate = if (inrRate > 0) inrRate else 1.45
    val effectiveEpayRate = if (epayRate > 0) epayRate else 121.0

    // Calculation logic based on active corridor
    val (estimatedReceiveNum, rateFormulaText, sendCurSymbol, receiveCurSymbol) = remember(
        selectedCorridor,
        sendAmountNum,
        effectiveInrRate,
        effectiveEpayRate
    ) {
        when (selectedCorridor) {
            ExchangeCorridor.BDT_TO_INR -> {
                val inr = if (effectiveInrRate > 0) sendAmountNum / effectiveInrRate else sendAmountNum * 0.69
                val formula = "1 INR = ৳${String.format(Locale.US, "%.2f", effectiveInrRate)} BDT"
                Quadruple(inr, formula, "৳ BDT", "₹ INR")
            }
            ExchangeCorridor.INR_TO_BDT -> {
                val bdtRate = effectiveInrRate * 0.97
                val bdt = sendAmountNum * bdtRate
                val formula = "₹1 INR = ৳${String.format(Locale.US, "%.2f", bdtRate)} BDT"
                Quadruple(bdt, formula, "₹ INR", "৳ BDT")
            }
            ExchangeCorridor.USD_TO_BDT -> {
                val bdt = sendAmountNum * effectiveEpayRate
                val formula = "$1 USD = ৳${String.format(Locale.US, "%.2f", effectiveEpayRate)} BDT"
                Quadruple(bdt, formula, "$ USD", "৳ BDT")
            }
            ExchangeCorridor.BDT_TO_USD -> {
                val buyRate = effectiveEpayRate + 4.0
                val usd = if (buyRate > 0) sendAmountNum / buyRate else sendAmountNum / 125.0
                val formula = "$1 USD = ৳${String.format(Locale.US, "%.2f", buyRate)} BDT"
                Quadruple(usd, formula, "৳ BDT", "$ USD")
            }
        }
    }

    // Available Send & Receive Methods based on active corridor
    val currentSendMethods = when (selectedCorridor) {
        ExchangeCorridor.BDT_TO_INR, ExchangeCorridor.BDT_TO_USD ->
            listOf("bKash", "Nagad", "Rocket", "Upay / CellFin")
        ExchangeCorridor.INR_TO_BDT ->
            listOf("PhonePe (India)", "Google Pay", "Paytm UPI", "Indian Bank")
        ExchangeCorridor.USD_TO_BDT ->
            listOf("EPAY USD", "Payeer", "USDT (TRC20)", "Perfect Money")
    }

    val currentReceiveMethods = when (selectedCorridor) {
        ExchangeCorridor.BDT_TO_INR ->
            listOf("PhonePe (India)", "Google Pay", "Paytm / UPI", "Indian Bank Transfer")
        ExchangeCorridor.INR_TO_BDT, ExchangeCorridor.USD_TO_BDT ->
            listOf("bKash Personal", "Nagad Personal", "Rocket", "CellFin / Bank")
        ExchangeCorridor.BDT_TO_USD ->
            listOf("EPAY Account", "Payeer Wallet", "Binance Pay / USDT", "Direct USD")
    }

    // Quick presets for current corridor
    val quickPresets = when (selectedCorridor) {
        ExchangeCorridor.BDT_TO_INR, ExchangeCorridor.BDT_TO_USD ->
            listOf("500", "1000", "2000", "5000", "10000", "25000")
        ExchangeCorridor.INR_TO_BDT ->
            listOf("500", "1000", "2000", "3000", "5000", "10000")
        ExchangeCorridor.USD_TO_BDT ->
            listOf("10", "25", "50", "100", "200", "500")
    }

    // Recent Live Transactions Feed
    val recentExchanges = remember {
        listOf(
            LiveExchangeItem("৳10,000 BDT ➔ ₹6,896 INR", "PhonePe UPI • 01712***789", "২ মিনিট আগে"),
            LiveExchangeItem("₹4,500 INR ➔ ৳6,325 BDT", "bKash Personal • 01844***321", "৬ মিনিট আগে"),
            LiveExchangeItem("$50 USD ➔ ৳6,050 BDT", "Nagad Personal • 01911***456", "১১ মিনিট আগে"),
            LiveExchangeItem("৳3,500 BDT ➔ ₹2,413 INR", "Google Pay • +91 98***76", "১৫ মিনিট আগে"),
            LiveExchangeItem("৳25,000 BDT ➔ ₹17,241 INR", "Indian Bank Transfer • SBI", "২২ মিনিট আগে"),
            LiveExchangeItem("$120 USD ➔ ৳14,520 BDT", "bKash Personal • 01622***990", "৩১ মিনিট আগে")
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // 1. Luxury Neon Glassmorphism Header Banner with Official ExchangeReferenceSquircle Logo
        item {
            val heroShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, heroShape, spotColor = SuccessGreen.copy(alpha = 0.4f))
                    .clip(heroShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0B2622),
                                Color(0xFF0B1828),
                                Color(0xFF100E24)
                            )
                        )
                    )
                    .border(
                        1.4.dp,
                        Brush.horizontalGradient(
                            listOf(
                                SuccessGreen.copy(alpha = 0.85f),
                                GoldPrimary.copy(alpha = 0.80f),
                                Color(0xFF38BDF8).copy(alpha = 0.75f)
                            )
                        ),
                        heroShape
                    )
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SuccessGreen.copy(alpha = 0.22f),
                                    GoldPrimary.copy(alpha = 0.10f),
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
                            ExchangeReferenceSquircle(size = 50.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                                            append("Payment ")
                                        }
                                        withStyle(
                                            SpanStyle(
                                                brush = Brush.horizontalGradient(
                                                    listOf(Color(0xFF4ADE80), GoldPrimary, Color(0xFF38BDF8))
                                                ),
                                                fontWeight = FontWeight.Black
                                            )
                                        ) {
                                            append("Exchange Hub")
                                        }
                                    },
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "BD 🇧🇩 ⇄ India 🇮🇳 • PhonePe, UPI & Dollar",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF86EFAC)
                                )
                            }
                        }

                        // Pulsing Live Indicator
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF0A2E22),
                            border = BorderStroke(1.dp, SuccessGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .scale(pulseScale)
                                        .background(SuccessGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "লাইভ রেট",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF86EFAC)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "বাংলাদেশ (বিকাশ/নগদ) থেকে ভারতীয় PhonePe, Google Pay, Paytm, UPI একাউন্টে তাৎক্ষণিক টাকা পাঠান অথবা ডলার এক্সচেঞ্জ করুন নিরাপদে।",
                        fontSize = 11.5.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Three Key Guarantee Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GuaranteePill(
                            icon = Icons.Default.Speed,
                            label = "৫-১৫ মিনিট",
                            modifier = Modifier.weight(1f)
                        )
                        GuaranteePill(
                            icon = Icons.Default.Security,
                            label = "১০০% নিরাপদ",
                            modifier = Modifier.weight(1f)
                        )
                        GuaranteePill(
                            icon = Icons.Default.CheckCircle,
                            label = "০% হিডেন ফি",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 2. Exchange Corridor Selector Tabs (4 Real Corridors)
        item {
            Text(
                text = "এক্সচেঞ্জ করিডোর সিলেক্ট করুন:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldLight,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ExchangeCorridor.values().forEach { corridor ->
                    val isSelected = selectedCorridor == corridor
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedCorridor = corridor
                                // Reset send/receive defaults based on corridor
                                when (corridor) {
                                    ExchangeCorridor.BDT_TO_INR -> {
                                        onSendMethodChange("bKash")
                                        onReceiveMethodChange("PhonePe (India)")
                                        if (amountBdt.isBlank() || amountBdt == "10") onAmountBdtChange("1000")
                                    }
                                    ExchangeCorridor.INR_TO_BDT -> {
                                        onSendMethodChange("PhonePe (India)")
                                        onReceiveMethodChange("bKash Personal")
                                        if (amountBdt.isBlank() || amountBdt == "10") onAmountBdtChange("1000")
                                    }
                                    ExchangeCorridor.USD_TO_BDT -> {
                                        onSendMethodChange("EPAY USD")
                                        onReceiveMethodChange("bKash Personal")
                                        if (amountBdt.isBlank() || amountBdt.toDoubleOrNull() ?: 0.0 > 1000) onAmountBdtChange("25")
                                    }
                                    ExchangeCorridor.BDT_TO_USD -> {
                                        onSendMethodChange("bKash")
                                        onReceiveMethodChange("EPAY Account")
                                        if (amountBdt.isBlank() || amountBdt == "25") onAmountBdtChange("1500")
                                    }
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF143F2E) else Color(0xFF101B2B),
                        border = BorderStroke(
                            1.2.dp,
                            if (isSelected) SuccessGreen else Color(0xFF1E2D42)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${corridor.fromFlag}➔${corridor.toFlag}",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = corridor.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color(0xFF86EFAC) else Color(0xFF94A3B8),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3. Interactive Converter Engine (Send & Receive with Central Swap Button)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = SuccessGreen.copy(alpha = 0.35f))
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.3.dp,
                        Brush.linearGradient(
                            listOf(
                                SuccessGreen.copy(alpha = 0.8f),
                                GoldPrimary.copy(alpha = 0.65f),
                                Color(0xFF38BDF8).copy(alpha = 0.65f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    ),
                color = Color(0xFF0C1422)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title & Current Rate Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CalculateReferenceSquircle(size = 32.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "লাইভ কারেন্সি ক্যালকুলেটর",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF064E3B),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = rateFormulaText,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF86EFAC),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // SEND CARD
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF131F30),
                        border = BorderStroke(1.dp, Color(0xFF2A3A52))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "আপনি পাঠাবেন (You Send)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Text(
                                        text = "${selectedCorridor.fromFlag} $sendCurSymbol",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = amountBdt,
                                onValueChange = onAmountBdtChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("0.00", color = Color(0xFF64748B), fontSize = 18.sp) },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SuccessGreen,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF09111C),
                                    unfocusedContainerColor = Color(0xFF09111C)
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                trailingIcon = {
                                    if (amountBdt.isNotBlank()) {
                                        Text(
                                            text = "✕",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 14.sp,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .clickable { onAmountBdtChange("") }
                                                .padding(8.dp)
                                        )
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Send Method Pills
                            Text(
                                text = "মাধ্যম:",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                currentSendMethods.forEach { method ->
                                    val isSelected = sendMethod == method
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { onSendMethodChange(method) },
                                        color = if (isSelected) GoldPrimary else Color(0xFF1E2D40),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = method.substringBefore(" "),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else Color(0xFFCBD5E1),
                                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // CENTRAL DIRECTION SWAP BUTTON
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    // Smoothly invert corridor
                                    val nextCorridor = when (selectedCorridor) {
                                        ExchangeCorridor.BDT_TO_INR -> ExchangeCorridor.INR_TO_BDT
                                        ExchangeCorridor.INR_TO_BDT -> ExchangeCorridor.BDT_TO_INR
                                        ExchangeCorridor.USD_TO_BDT -> ExchangeCorridor.BDT_TO_USD
                                        ExchangeCorridor.BDT_TO_USD -> ExchangeCorridor.USD_TO_BDT
                                    }
                                    selectedCorridor = nextCorridor
                                    // Invert send/receive methods
                                    val oldSend = sendMethod
                                    val oldReceive = receiveMethod
                                    onSendMethodChange(oldReceive.substringBefore(" "))
                                    onReceiveMethodChange(oldSend)
                                },
                            shape = CircleShape,
                            color = Color(0xFF1B3D2F),
                            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(SuccessGreen, GoldPrimary)))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Swap Direction",
                                    tint = Color(0xFF86EFAC),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "দিক পরিবর্তন করুন (Swap)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // RECEIVE CARD
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF10281F),
                        border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "প্রাপক পাবেন (You / Recipient Gets)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF86EFAC)
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF0A3A27)
                                ) {
                                    Text(
                                        text = "${selectedCorridor.toFlag} $receiveCurSymbol",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF86EFAC),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Converted Display Value
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF071F17),
                                border = BorderStroke(1.dp, Color(0xFF1E5B42))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (estimatedReceiveNum > 0)
                                            String.format(Locale.US, "%,.2f", estimatedReceiveNum)
                                        else "0.00",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF86EFAC)
                                    )

                                    Text(
                                        text = receiveCurSymbol,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Receive Method Pills
                            Text(
                                text = "পাওয়ার মাধ্যম:",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                currentReceiveMethods.forEach { method ->
                                    val isSelected = receiveMethod == method
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { onReceiveMethodChange(method) },
                                        color = if (isSelected) SuccessGreen else Color(0xFF123829),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = method.substringBefore(" "),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else Color(0xFFE2E8F0),
                                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Preset Chips
                    Text(
                        text = "দ্রুত টাকার পরিমাণ সিলেক্ট করুন:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickPresets.forEach { preset ->
                            val isSelected = amountBdt == preset
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onAmountBdtChange(preset) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) SuccessGreen else Color(0xFF192538),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) SuccessGreen else Color(0xFF2B3C55)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = preset,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4. Remittance Cost & Breakdown Card (Invoice preview)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF1E3A2F), RoundedCornerShape(16.dp)),
                color = Color(0xFF0F1A24)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📋 বিস্তারিত হিসাব বিবরণী",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Text(
                            text = "ভ্যাট ও সার্ভিস চার্জ অন্তর্ভুক্ত",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    BreakdownRow(
                        label = "পাঠানো হচ্ছে:",
                        value = "${String.format(Locale.US, "%,.2f", sendAmountNum)} $sendCurSymbol ($sendMethod)"
                    )
                    BreakdownRow(
                        label = "এক্সচেঞ্জ রেট:",
                        value = rateFormulaText,
                        valueColor = Color(0xFF38BDF8)
                    )
                    BreakdownRow(
                        label = "প্ল্যাটফর্ম ফি:",
                        value = "৳০.০০ (ফ্রি / ০% ফি)",
                        valueColor = SuccessGreen
                    )
                    BreakdownRow(
                        label = "ডেলিভারি সময়:",
                        value = "⚡ ৫-১৫ মিনিট (ইনস্ট্যান্ট)",
                        valueColor = GoldLight
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(1.dp)
                            .background(Color(0xFF273549))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "প্রাপক পাবেন:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${String.format(Locale.US, "%,.2f", estimatedReceiveNum)} $receiveCurSymbol",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF86EFAC)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 5. Recipient Account Form (PhonePe / UPI / bKash)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF27364D), RoundedCornerShape(16.dp)),
                color = Color(0xFF0F1826)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👤 প্রাপকের একাউন্ট বা UPI তথ্য",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Text(
                                text = "সঠিক দিন",
                                fontSize = 10.sp,
                                color = GoldLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val labelText = when (selectedCorridor) {
                        ExchangeCorridor.BDT_TO_INR -> "PhonePe / GPay নাম্বার বা UPI ID"
                        ExchangeCorridor.INR_TO_BDT -> "বিকাশ / নগদ / রকেট পার্সোনাল নাম্বার"
                        ExchangeCorridor.USD_TO_BDT -> "বিকাশ / নগদ নাম্বার"
                        ExchangeCorridor.BDT_TO_USD -> "EPAY একাউন্ট ইমেইল / Payeer নং"
                    }

                    val placeholderText = when (selectedCorridor) {
                        ExchangeCorridor.BDT_TO_INR -> "e.g. +91 9876543210 বা name@okhdfcbank"
                        ExchangeCorridor.INR_TO_BDT, ExchangeCorridor.USD_TO_BDT -> "e.g. 017XXXXXXXX বা 018XXXXXXXX"
                        ExchangeCorridor.BDT_TO_USD -> "e.g. your_epay_email@gmail.com"
                    }

                    OutlinedTextField(
                        value = recipientUpi,
                        onValueChange = onRecipientUpiChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(labelText, color = Color(0xFF94A3B8), fontSize = 12.sp) },
                        placeholder = { Text(placeholderText, color = Color(0xFF64748B), fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SuccessGreen,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF09111C),
                            unfocusedContainerColor = Color(0xFF09111C)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("প্রাপকের নাম / Account Holder Name (ঐচ্ছিক)", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                        placeholder = { Text("e.g. Rahul Sharma বা Md Johirul", color = Color(0xFF64748B), fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF09111C),
                            unfocusedContainerColor = Color(0xFF09111C)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // QR Code hint
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0A1F18), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "QR Code",
                            tint = Color(0xFF86EFAC),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "টিপস: অর্ডারের ভেতরে ভারতীয় PhonePe QR কোড স্ক্রিনশট আপলোড করার সুবিধা রয়েছে।",
                            fontSize = 11.sp,
                            color = Color(0xFF86EFAC),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 6. ACTION CTA BUTTONS
        item {
            // Primary In-App Direct Order Button
            Button(
                onClick = {
                    if (sendAmountNum < 10.0 && amountBdt.isBlank()) {
                        onAmountBdtChange("1000")
                    }
                    onOpenOrderDialog()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "এক্সচেঞ্জ অর্ডার করুন (${String.format(Locale.US, "%,.0f", estimatedReceiveNum)} $receiveCurSymbol পান)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Direct WhatsApp & Telegram Priority Escalation Buttons (Official Vector Logos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PremiumWhatsAppButton(
                    text = "WhatsApp অর্ডার",
                    onClick = onSubmitExchangeWhatsApp,
                    modifier = Modifier.weight(1f),
                    height = 46.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )

                PremiumTelegramButton(
                    text = "Telegram অর্ডার",
                    onClick = onSubmitExchangeTelegram,
                    modifier = Modifier.weight(1f),
                    height = 46.dp,
                    cornerRadius = 12.dp,
                    fontSize = 12.5.sp,
                    iconSize = 22.dp
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 7. Live Recent Exchanges (Trust & Social Proof Feed)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF1E332A), RoundedCornerShape(16.dp)),
                color = Color(0xFF0A1916)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔥", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সাম্প্রতিক সফল এক্সচেঞ্জ লেনদেন",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SuccessGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "আজকের আপডেট",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF86EFAC),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    recentExchanges.take(4).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color(0xFF0F2620), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE2E8F0)
                                )
                                Text(
                                    text = item.details,
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF0A3D28)
                                ) {
                                    Text(
                                        text = "✓ ${item.status}",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF86EFAC),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = item.timeAgo,
                                    fontSize = 9.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 24H Volume Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ExchangeStatItem(label = "আজকের লেনদেন", value = "৪৮+ টি")
                        ExchangeStatItem(label = "মোট ভলিউম", value = "৳৬,৪০,০০০+")
                        ExchangeStatItem(label = "সফলতার রেট", value = "৯৯.৯%")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 8. 4 Step Remittance Guide
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF223145), RoundedCornerShape(16.dp)),
                color = Color(0xFF0F1724)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🚀 কীভাবে এক্সচেঞ্জ করবেন? (সহজ ৪ ধাপ)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    ExchangeGuideStep(
                        step = "১",
                        title = "মাধ্যম ও টাকার পরিমাণ নির্বাচন করুন",
                        desc = "কোন মাধ্যমে পাঠাবেন ও কত রুপি/ডলার চান তা লিখুন।"
                    )
                    ExchangeGuideStep(
                        step = "২",
                        title = "আমাদের বিকাশ বা নগদ একাউন্টে টাকা পাঠান",
                        desc = "অর্ডার ডায়ালগে দেওয়া নাম্বারে Send Money করে TrxID কপি করুন।"
                    )
                    ExchangeGuideStep(
                        step = "৩",
                        title = "PhonePe নাম্বার বা QR কোড দিন",
                        desc = "আপনার ভারতীয় প্রাপকের PhonePe নাম্বার ও স্ক্রিনশট সাবমিট করুন।"
                    )
                    ExchangeGuideStep(
                        step = "৪",
                        title = "৫-১৫ মিনিটে একাউন্টে জমা হবে",
                        desc = "আমাদের প্রতিনিধি তাৎক্ষণিক ভেরিফাই করে সফল রসিদ প্রদান করবেন।"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 9. Interactive FAQ Accordion
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF1E2D40), RoundedCornerShape(16.dp)),
                color = Color(0xFF0C1622)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "❓ সচরাচর জিজ্ঞাসিত প্রশ্নাবলী (FAQ)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val faqs = listOf(
                        "টাকা পৌঁছাতে সাধারণত কত সময় লাগে?" to "সাধারণত ৫ থেকে ১৫ মিনিটের মধ্যে ভারতীয় PhonePe, Google Pay বা Paytm একাউন্টে সফলভাবে টাকা পৌঁছে যায়।",
                        "আমি কি QR কোড স্ক্রিনশট দিয়ে অর্ডার করতে পারব?" to "হ্যাঁ! 'এক্সচেঞ্জ অর্ডার করুন' বাটনে ক্লিক করলে আপনার ভারতীয় প্রাপকের PhonePe/Paytm QR Code স্ক্রিনশট আপলোড করার অপশন পাবেন।",
                        "মিনিমাম এবং ম্যাক্সিমাম এক্সচেঞ্জ লিমিট কত?" to "সর্বনিম্ন ১০০ টাকা থেকে সর্বোচ্চ ১,০০,০০০ টাকা পর্যন্ত এক অর্ডারে নিরাপদে পাঠাতে পারবেন। বড় অংকের জন্য সরাসরি WhatsApp সাপোর্ট নিন।",
                        "টাকা না পৌঁছালে রিফান্ডের নিয়ম কী?" to "যদি কোনো কারণে প্রাপকের একাউন্টে টাকা না পাঠানো সম্ভব হয়, তবে আপনার বিকাশ/নগদ নাম্বারে সম্পূর্ণ টাকা তাৎক্ষণিক ফেরত দেওয়া হবে।"
                    )

                    faqs.forEachIndexed { index, (question, answer) ->
                        val isExpanded = expandedFaqIndex == index
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    expandedFaqIndex = if (isExpanded) -1 else index
                                },
                            color = Color(0xFF131F30),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = question,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE2E8F0),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = answer,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ---------------------- Helper UI Components ----------------------

@Composable
private fun GuaranteePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0D2D20),
        border = BorderStroke(0.8.dp, Color(0xFF1E5B40))
    ) {
        Row(
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF86EFAC),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE2E8F0),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    valueColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.5.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun ExchangeStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF86EFAC)
        )
        Text(
            text = label,
            fontSize = 9.5.sp,
            color = Color(0xFF94A3B8)
        )
    }
}

@Composable
private fun ExchangeGuideStep(
    step: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = SuccessGreen,
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = step,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = desc,
                fontSize = 10.5.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 15.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
