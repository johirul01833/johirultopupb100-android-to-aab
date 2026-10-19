package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.local.AdminConfig
import com.example.data.model.OrderRecord
import com.example.data.model.ServiceCategory
import com.example.data.model.TopupPackage
import com.example.ui.theme.GoldPrimary
import com.example.util.TrxValidator
import java.util.Locale

enum class GatewayStep {
    SELECT_METHOD,
    ENTER_DETAILS,
    TIKTOK_CREDENTIALS,
    TIKTOK_VERIFIED_WHATSAPP
}

enum class MobileBankingMethod(
    val title: String,
    val bengaliTitle: String,
    val dialCode: String,
    val primaryColor: Color,
    val cardBgColor: Color
) {
    BKASH("bKash", "বিকাশ", "*247#", Color(0xFFD8226A), Color(0xFFCF1E6A)),
    UPAY("upay", "উপায়", "*268#", Color(0xFF0073BC), Color(0xFF0B588C)),
    ROCKET("Rocket", "রকেট", "*322#", Color(0xFF8C2686), Color(0xFF701C6B)),
    NAGAD("Nagad", "নগদ", "*167#", Color(0xFFE65100), Color(0xFFC73E00))
}

@Composable
fun PaymentGatewayDialog(
    packageItem: TopupPackage,
    adminConfig: AdminConfig,
    initialAccountId: String = "",
    walletBalance: Double = 0.0,
    existingOrders: List<OrderRecord> = emptyList(),
    isWalletDeposit: Boolean = false,
    onDismiss: () -> Unit,
    onSubmitOrder: (pkg: TopupPackage, accountId: String, trxId: String, method: String) -> Unit,
    onPayWithWallet: ((pkg: TopupPackage, accountId: String) -> Unit)? = null,
    onOpenWalletAddMoney: (() -> Unit)? = null,
    onOpenLiveChat: () -> Unit = {},
    onCopyText: (String, String) -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(GatewayStep.SELECT_METHOD) }
    var selectedMethod by remember { mutableStateOf(MobileBankingMethod.BKASH) }
    var showWalletQuickPayInput by remember { mutableStateOf(false) }
    var paidViaWalletInDialog by remember { mutableStateOf(false) }

    var depositAmountInput by remember(packageItem.priceBdt) {
        mutableStateOf(
            if (packageItem.priceBdt > 0.0) String.format(Locale.US, "%.0f", packageItem.priceBdt) else "500"
        )
    }
    val effectiveAmountBdt = remember(isWalletDeposit, depositAmountInput, packageItem.priceBdt) {
        if (isWalletDeposit) {
            depositAmountInput.toDoubleOrNull()?.takeIf { it > 0.0 } ?: packageItem.priceBdt
        } else {
            packageItem.priceBdt
        }
    }
    val effectivePackage = remember(isWalletDeposit, effectiveAmountBdt, packageItem) {
        if (isWalletDeposit) {
            packageItem.copy(
                id = "wallet_add_${effectiveAmountBdt.toInt()}",
                name = "৳${String.format(Locale.US, "%,.0f", effectiveAmountBdt)} ওয়ালেট ব্যালেন্স",
                subtitle = "প্রোফাইল ওয়ালেট অ্যাড মানি",
                amount = "৳${String.format(Locale.US, "%,.0f", effectiveAmountBdt)} BDT",
                priceBdt = effectiveAmountBdt
            )
        } else {
            packageItem
        }
    }

    var accountIdInput by remember(initialAccountId) { mutableStateOf(initialAccountId) }
    var trxIdInput by remember { mutableStateOf("") }
    var trxIdError by remember { mutableStateOf<String?>(null) }

    val isTikTokCoinOrder = !isWalletDeposit && packageItem.category == ServiceCategory.TIKTOK
    var tiktokUsernameInput by remember(initialAccountId) { mutableStateOf(initialAccountId) }
    var tiktokPasswordInput by remember { mutableStateOf("") }
    var showTiktokPassword by remember { mutableStateOf(false) }
    var tiktokCredError by remember { mutableStateOf<String?>(null) }

    // 3-Second Auto Redirect Countdown on Pending Step -> Live Support Chat
    var pendingCountdownSec by remember { mutableIntStateOf(3) }
    LaunchedEffect(currentStep) {
        if (currentStep == GatewayStep.TIKTOK_VERIFIED_WHATSAPP) {
            pendingCountdownSec = 3
            while (pendingCountdownSec > 0) {
                kotlinx.coroutines.delay(1000L)
                pendingCountdownSec -= 1
            }
            onDismiss()
            onOpenLiveChat()
        }
    }

    val idFieldLabel = when (packageItem.category) {
        ServiceCategory.CHAMET -> "Chamet ID (চ্যামেট আইডি)"
        ServiceCategory.TIKTOK -> "TikTok Username / UID"
        ServiceCategory.FREEFIRE -> "Player UID (প্লেয়ার আইডি)"
        ServiceCategory.SOCIAL_BOOST -> "Target Link / URL (লিঙ্ক)"
        else -> "Account / User ID"
    }

    val idPlaceholder = when (packageItem.category) {
        ServiceCategory.CHAMET -> "আপনার Chamet ID লিখুন"
        ServiceCategory.TIKTOK -> "আপনার TikTok UID লিখুন"
        ServiceCategory.FREEFIRE -> "আপনার Free Fire Player UID লিখুন"
        ServiceCategory.SOCIAL_BOOST -> "আপনার প্রোফাইল / পেজ / ভিডিও লিঙ্ক দিন"
        else -> "আপনার একাউন্ট আইডি লিখুন"
    }

    val methodPaymentNumber = when (selectedMethod) {
        MobileBankingMethod.BKASH -> adminConfig.bkashNumber
        MobileBankingMethod.NAGAD -> adminConfig.nagadNumber
        MobileBankingMethod.ROCKET -> if (adminConfig.rocketNumber.isNotBlank()) adminConfig.rocketNumber else adminConfig.bkashNumber
        MobileBankingMethod.UPAY -> adminConfig.nagadNumber
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 440.dp)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(22.dp)),
            color = Color.White,
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
            ) {
                // Top Purple Navigation Bar (Matching Screenshot 1 & 2)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF860555))
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStep == GatewayStep.ENTER_DETAILS || currentStep == GatewayStep.TIKTOK_CREDENTIALS) {
                            IconButton(
                                onClick = {
                                    currentStep = if (currentStep == GatewayStep.TIKTOK_CREDENTIALS && !paidViaWalletInDialog) {
                                        GatewayStep.ENTER_DETAILS
                                    } else {
                                        GatewayStep.SELECT_METHOD
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "নিরাপদ পেমেন্ট - Chamet Top Up BD",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterEnd)
                            .background(Color.Black.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Main Animated Body
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState == GatewayStep.ENTER_DETAILS) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    modifier = Modifier.weight(1f, fill = false),
                    label = "GatewayStepTransition"
                ) { step ->
                    when (step) {
                        GatewayStep.SELECT_METHOD -> {
                            // Step 1: Select Payment Method (Matching Screenshot 1)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 18.dp, vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Centered Brand Logo
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF05010D))
                                        .border(2.dp, GoldPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_chamet_topup_logo_1790587594025),
                                        contentDescription = "Chamet Top Up BD Logo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Chamet Top Up BD",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E293B)
                                )

                                Text(
                                    text = if (isWalletDeposit) "প্রোফাইল ওয়ালেট অ্যাড মানি • ৳${String.format(Locale.US, "%,.0f", effectiveAmountBdt)}" else "${packageItem.category.displayName} • ${packageItem.name}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Direct Wallet Add Money Amount Selector inside Gateway Dialog
                                if (isWalletDeposit) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                        border = BorderStroke(1.5.dp, Color(0xFF10B981))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "💰 কত টাকা ওয়ালেটে অ্যাড করবেন? (সর্বনিম্ন ৳${String.format(Locale.US, "%.0f", adminConfig.minWalletAddMoneyBdt)})",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF065F46)
                                            )
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                listOf("100", "200", "300", "500", "1000", "2000", "5000").forEach { preset ->
                                                    val isSel = depositAmountInput == preset
                                                    Surface(
                                                        onClick = { depositAmountInput = preset },
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSel) Color(0xFF059669) else Color.White,
                                                        border = BorderStroke(
                                                            1.dp,
                                                            if (isSel) Color(0xFF059669) else Color(0xFF10B981)
                                                        )
                                                    ) {
                                                        Text(
                                                            text = "৳$preset",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = if (isSel) Color.White else Color(0xFF065F46),
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            OutlinedTextField(
                                                value = depositAmountInput,
                                                onValueChange = { depositAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                                label = { Text("টাকার পরিমাণ লিখুন (BDT)", fontSize = 11.5.sp) },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Done
                                                ),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF059669),
                                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                                    focusedTextColor = Color(0xFF0F172A),
                                                    unfocusedTextColor = Color(0xFF0F172A),
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // Profile Wallet Balance Quick-Pay Card
                                if (onPayWithWallet != null && !isWalletDeposit) {
                                    val canAfford = walletBalance >= packageItem.priceBdt
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (canAfford) Color(0xFFECFDF5) else Color(0xFFF8FAFC)
                                        ),
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (canAfford) Color(0xFF10B981) else Color(0xFFCBD5E1)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "💳 প্রোফাইল ওয়ালেট ব্যালেন্স",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color(0xFF065F46)
                                                    )
                                                    Text(
                                                        text = "বর্তমান ব্যালেন্স: ৳${String.format(Locale.US, "%.0f", walletBalance)} • চার্জ: ৳${String.format(Locale.US, "%.0f", packageItem.priceBdt)}",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (canAfford) Color(0xFF047857) else Color(0xFF64748B)
                                                    )
                                                }

                                                if (canAfford) {
                                                    Button(
                                                        onClick = {
                                                            if (isTikTokCoinOrder) {
                                                                paidViaWalletInDialog = true
                                                                currentStep = GatewayStep.TIKTOK_CREDENTIALS
                                                            } else if (accountIdInput.trim().isNotBlank()) {
                                                                paidViaWalletInDialog = true
                                                                onPayWithWallet(packageItem, accountIdInput.trim())
                                                                currentStep = GatewayStep.TIKTOK_VERIFIED_WHATSAPP
                                                            } else {
                                                                showWalletQuickPayInput = true
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(0xFF10B981),
                                                            contentColor = Color.White
                                                        ),
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                            horizontal = 12.dp,
                                                            vertical = 6.dp
                                                        )
                                                    ) {
                                                        Text(
                                                            text = "ওয়ালেট পেমেন্ট ⚡",
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.ExtraBold
                                                        )
                                                    }
                                                } else if (onOpenWalletAddMoney != null) {
                                                    OutlinedButton(
                                                        onClick = {
                                                            onDismiss()
                                                            onOpenWalletAddMoney()
                                                        },
                                                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                            horizontal = 10.dp,
                                                            vertical = 5.dp
                                                        )
                                                    ) {
                                                        Text(
                                                            text = "+ টাকা এড করুন",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF0284C7)
                                                        )
                                                    }
                                                }
                                            }

                                            if (showWalletQuickPayInput && canAfford) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                OutlinedTextField(
                                                    value = accountIdInput,
                                                    onValueChange = { accountIdInput = it },
                                                    placeholder = {
                                                        Text(
                                                            text = idPlaceholder,
                                                            fontSize = 12.sp,
                                                            color = Color(0xFF94A3B8)
                                                        )
                                                    },
                                                    label = {
                                                        Text(
                                                            text = idFieldLabel,
                                                            fontSize = 11.5.sp
                                                        )
                                                    },
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFF10B981),
                                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                                        focusedTextColor = Color(0xFF0F172A),
                                                        unfocusedTextColor = Color(0xFF0F172A),
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White
                                                    ),
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    onClick = {
                                                        val trimmedId = accountIdInput.trim()
                                                        if (trimmedId.isBlank()) {
                                                            Toast.makeText(
                                                                context,
                                                                "⚠️ দয়া করে $idFieldLabel লিখুন",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            paidViaWalletInDialog = true
                                                            onPayWithWallet(packageItem, trimmedId)
                                                            currentStep = GatewayStep.TIKTOK_VERIFIED_WHATSAPP
                                                        }
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF059669),
                                                        contentColor = Color.White
                                                    )
                                                ) {
                                                    Text(
                                                        text = "৳${String.format(Locale.US, "%.0f", packageItem.priceBdt)} ওয়ালেট থেকে পেমেন্ট নিশ্চিত করুন",
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // Blue Pill Header: "মোবাইল ব্যাংকিং"
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF005BAC))
                                        .padding(vertical = 9.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "মোবাইল ব্যাংকিং (যেটা দিয়ে পেমেন্ট করবেন ক্লিক করুন)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // 2x2 Grid for Payment Methods — Clicking any method directly opens Step 2 (TrxID & Verify)
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        PaymentOptionTile(
                                            method = MobileBankingMethod.BKASH,
                                            isSelected = selectedMethod == MobileBankingMethod.BKASH,
                                            onSelect = {
                                                selectedMethod = MobileBankingMethod.BKASH
                                                currentStep = GatewayStep.ENTER_DETAILS
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                        PaymentOptionTile(
                                            method = MobileBankingMethod.UPAY,
                                            isSelected = selectedMethod == MobileBankingMethod.UPAY,
                                            onSelect = {
                                                selectedMethod = MobileBankingMethod.UPAY
                                                currentStep = GatewayStep.ENTER_DETAILS
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        PaymentOptionTile(
                                            method = MobileBankingMethod.ROCKET,
                                            isSelected = selectedMethod == MobileBankingMethod.ROCKET,
                                            onSelect = {
                                                selectedMethod = MobileBankingMethod.ROCKET
                                                currentStep = GatewayStep.ENTER_DETAILS
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                        PaymentOptionTile(
                                            method = MobileBankingMethod.NAGAD,
                                            isSelected = selectedMethod == MobileBankingMethod.NAGAD,
                                            onSelect = {
                                                selectedMethod = MobileBankingMethod.NAGAD
                                                currentStep = GatewayStep.ENTER_DETAILS
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Security Note
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF005BAC),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "১০০% নিরাপদ ও ভেরিফাইড পেমেন্ট গেটওয়ে",
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        GatewayStep.ENTER_DETAILS -> {
                            // Step 2: Enter Chamet ID & TrxID with Instructions (Matching Screenshot 2)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Top White Selected Method Card with Amount
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    shadowElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp, horizontal = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        MethodBrandLogo(method = selectedMethod)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "৳ ${String.format(Locale.US, "%,.0f", effectiveAmountBdt)}",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Themed Pink/Colored Instruction & Input Box
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = selectedMethod.cardBgColor
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp)
                                    ) {
                                        // 1. Chamet ID / Account ID Input Box OR Add Money Amount Input Box
                                        if (isWalletDeposit) {
                                            Text(
                                                text = "কত টাকা অ্যাড করবেন লিখুন (BDT)",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                listOf("100", "200", "500", "1000", "2000", "5000").forEach { preset ->
                                                    val isSel = depositAmountInput == preset
                                                    Surface(
                                                        onClick = { depositAmountInput = preset },
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSel) GoldPrimary else Color.Black.copy(alpha = 0.25f),
                                                        border = BorderStroke(
                                                            1.dp,
                                                            if (isSel) Color.White else Color.White.copy(alpha = 0.35f)
                                                        )
                                                    ) {
                                                        Text(
                                                            text = "৳$preset",
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = if (isSel) Color(0xFF0F172A) else Color.White,
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = depositAmountInput,
                                                onValueChange = { depositAmountInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                                modifier = Modifier.fillMaxWidth(),
                                                placeholder = {
                                                    Text(
                                                        text = "টাকার পরিমাণ লিখুন (যেমন: 500)",
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = GoldPrimary,
                                                    unfocusedBorderColor = Color.Transparent,
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    focusedTextColor = Color(0xFF0F172A),
                                                    unfocusedTextColor = Color(0xFF0F172A)
                                                ),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next
                                                ),
                                                singleLine = true
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                        } else if (!isTikTokCoinOrder) {
                                            Text(
                                                text = "$idFieldLabel দিন",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedTextField(
                                                value = accountIdInput,
                                                onValueChange = { accountIdInput = it },
                                                modifier = Modifier.fillMaxWidth(),
                                                placeholder = {
                                                    Text(
                                                        text = idPlaceholder,
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 12.sp
                                                    )
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = GoldPrimary,
                                                    unfocusedBorderColor = Color.Transparent,
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    focusedTextColor = Color(0xFF0F172A),
                                                    unfocusedTextColor = Color(0xFF0F172A)
                                                ),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Text,
                                                    imeAction = ImeAction.Next
                                                ),
                                                singleLine = true
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))
                                        }

                                        // 2. Transaction ID Input Box
                                        Text(
                                            text = "ট্রানজেকশন আইডি দিন",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))

                                        val currentValidation = remember(selectedMethod, trxIdInput, existingOrders) {
                                            if (trxIdInput.isBlank()) null
                                            else TrxValidator.validate(
                                                selectedMethod.title,
                                                trxIdInput,
                                                existingOrders.map { it.transactionTrxId }.toSet()
                                            )
                                        }

                                        OutlinedTextField(
                                            value = trxIdInput,
                                            onValueChange = { input ->
                                                val clean = input.uppercase().filter { it.isLetterOrDigit() }.take(14)
                                                trxIdInput = clean
                                                trxIdError = null
                                            },
                                            isError = trxIdError != null || (currentValidation != null && !currentValidation.isValid),
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = {
                                                Text(
                                                    text = if (selectedMethod == MobileBankingMethod.BKASH) "১০ অক্ষরের বিকাশ TrxID লিখুন" else "ট্রানজেকশন আইডি (TrxID) লিখুন",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 12.sp
                                                )
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = if (trxIdError != null || (currentValidation != null && !currentValidation.isValid)) Color(0xFFEF4444) else GoldPrimary,
                                                unfocusedBorderColor = if (trxIdError != null || (currentValidation != null && !currentValidation.isValid)) Color(0xFFEF4444) else Color.Transparent,
                                                errorBorderColor = Color(0xFFEF4444),
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedTextColor = Color(0xFF0F172A),
                                                unfocusedTextColor = Color(0xFF0F172A)
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Done
                                            ),
                                            singleLine = true
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        if (trxIdError != null) {
                                            Surface(
                                                color = Color(0xFF7F1D1D).copy(alpha = 0.92f),
                                                shape = RoundedCornerShape(8.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(15.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = trxIdError!!,
                                                        color = Color.White,
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        } else if (trxIdInput.isNotBlank()) {
                                            val validation = currentValidation
                                            if (validation != null) {
                                                Surface(
                                                    color = if (validation.isValid) Color(0xFF064E3B).copy(alpha = 0.92f) else Color(0xFF78350F).copy(alpha = 0.92f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (validation.isValid) Color(0xFF10B981) else Color(0xFFF59E0B)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = if (validation.isValid) Icons.Default.Check else Icons.Default.HourglassTop,
                                                            contentDescription = null,
                                                            tint = if (validation.isValid) Color(0xFF6EE7B7) else Color(0xFFFCD34D),
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = validation.message,
                                                            color = Color.White,
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = if (selectedMethod == MobileBankingMethod.BKASH) "💡 বিকাশ TrxID ঠিক ১০ অক্ষরের হতে হবে। ভুল TrxID দিলে ভেরিফাই হবে না।" else if (selectedMethod == MobileBankingMethod.NAGAD) "💡 নগদ TrxID ৮ বা ১০ অক্ষরের হতে হবে। ভুল TrxID দিলে ভেরিফাই হবে না।" else "💡 পেমেন্ট এসএমএস থেকে সঠিক TrxID দেখে লিখুন। ভুল হলে ভেরিফাই হবে না।",
                                                fontSize = 11.sp,
                                                color = Color(0xFFCBD5E1),
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Step-by-Step Payment Instructions (Matching Screenshot 2)
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            InstructionBulletPoint(
                                                text = "${selectedMethod.dialCode} ডায়াল করে আপনার ${selectedMethod.title} মোবাইল মেনুতে যান অথবা ${selectedMethod.title} অ্যাপ খুলুন।"
                                            )
                                            InstructionBulletPoint(
                                                text = "সেন্ড মানি -এ ক্লিক করুন।"
                                            )

                                            // Target Number with Copy Badge
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "• ",
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "উপরের নম্বরে টাকা পাঠানঃ ",
                                                    fontSize = 12.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = methodPaymentNumber,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))

                                                Surface(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .clickable {
                                                            onCopyText("${selectedMethod.title} Number", methodPaymentNumber)
                                                            Toast.makeText(context, "${selectedMethod.title} নাম্বার কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                                        },
                                                    color = Color.Black.copy(alpha = 0.25f),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ContentCopy,
                                                            contentDescription = "Copy",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(
                                                            text = "কপি",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                            }

                                            InstructionBulletPoint(
                                                text = "টাকার পরিমাণঃ ${String.format(Locale.US, "%,.0f", effectiveAmountBdt)}"
                                            )
                                            InstructionBulletPoint(
                                                text = "নিশ্চিত করে এখন আপনার ${selectedMethod.title} PIN লিখুন।"
                                            )
                                            InstructionBulletPoint(
                                                text = "টাকা পাঠান এবং ট্রানজ্যাকশন আইডি কপি করুন।"
                                            )
                                            InstructionBulletPoint(
                                                text = "ট্রানজ্যাকশন আইডি লিখে ভেরিফাই চাপুন।"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        GatewayStep.TIKTOK_CREDENTIALS -> {
                            // Step 2.5: Clean, Minimal TikTok Username & Password Page (After TrxID Verification)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    shadowElevation = 3.dp
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp)
                                    ) {
                                        Text(
                                            text = "ইউজারনেম (Username)",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = tiktokUsernameInput,
                                            onValueChange = {
                                                tiktokUsernameInput = it
                                                tiktokCredError = null
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("tiktok_username_input"),
                                            placeholder = {
                                                Text(
                                                    text = "ইউজারনেম লিখুন",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 13.sp
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = Color(0xFFEC4899)
                                                )
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFFEC4899),
                                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                                focusedContainerColor = Color(0xFFF8FAFC),
                                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                                focusedTextColor = Color(0xFF0F172A),
                                                unfocusedTextColor = Color(0xFF0F172A)
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            ),
                                            singleLine = true
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = "পাসওয়ার্ড (Password)",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = tiktokPasswordInput,
                                            onValueChange = {
                                                tiktokPasswordInput = it
                                                tiktokCredError = null
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("tiktok_password_input"),
                                            placeholder = {
                                                Text(
                                                    text = "পাসওয়ার্ড লিখুন",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 13.sp
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = Color(0xFFEC4899)
                                                )
                                            },
                                            trailingIcon = {
                                                IconButton(onClick = { showTiktokPassword = !showTiktokPassword }) {
                                                    Icon(
                                                        imageVector = if (showTiktokPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = "Toggle password",
                                                        tint = Color(0xFF64748B)
                                                    )
                                                }
                                            },
                                            visualTransformation = if (showTiktokPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFFEC4899),
                                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                                focusedContainerColor = Color(0xFFF8FAFC),
                                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                                focusedTextColor = Color(0xFF0F172A),
                                                unfocusedTextColor = Color(0xFF0F172A)
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Password,
                                                imeAction = ImeAction.Done
                                            ),
                                            singleLine = true
                                        )

                                        if (tiktokCredError != null) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Surface(
                                                color = Color(0xFFFEF2F2),
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "⚠️ ${tiktokCredError!!}",
                                                    color = Color(0xFFB91C1C),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        GatewayStep.TIKTOK_VERIFIED_WHATSAPP -> {
                            // Step 3: Post-Verify Pending Screen (3-Second Auto Redirect to In-App Live Chat)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Pending Status Badge
                                Surface(
                                    modifier = Modifier.size(68.dp),
                                    shape = CircleShape,
                                    color = Color(0xFFFEF3C7),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF59E0B))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.HourglassTop,
                                            contentDescription = "Pending",
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "পেন্ডিং (Pending)",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFD97706)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (isWalletDeposit) "আপনার অ্যাড মানি রিকোয়েস্টটি বর্তমানে পেন্ডিং রয়েছে।" else "আপনার অর্ডারটি বর্তমানে পেন্ডিং রয়েছে।",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Live 3-Second Auto-Redirect Countdown Pill
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFF1E1B4B),
                                    border = BorderStroke(1.5.dp, Color(0xFF8B5CF6))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = Color(0xFFA78BFA),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "⏱️ ${pendingCountdownSec.coerceAtLeast(1)} সেকেন্ড পর অটোমেটিক লাইভ চ্যাটে প্রবেশ করছে...",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Order Summary Card
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "স্ট্যাটাস:", fontSize = 12.sp, color = Color(0xFF64748B))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFFEF3C7),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                                            ) {
                                                Text(
                                                    text = "⏳ পেন্ডিং (Pending)",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = if (isWalletDeposit) "সার্ভিস:" else "প্যাকেজ:", fontSize = 12.sp, color = Color(0xFF64748B))
                                            Text(
                                                text = if (isWalletDeposit) "প্রোফাইল ওয়ালেট অ্যাড মানি" else "${effectivePackage.name} (${effectivePackage.amount})",
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                        }
                                        if (!isWalletDeposit && accountIdInput.isNotBlank()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "একাউন্ট / UID:", fontSize = 12.sp, color = Color(0xFF64748B))
                                                Text(
                                                    text = accountIdInput.trim(),
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F172A)
                                                )
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "পেমেন্ট মাধ্যম:", fontSize = 12.sp, color = Color(0xFF64748B))
                                            Text(
                                                text = if (paidViaWalletInDialog) "Profile Wallet Balance" else "${selectedMethod.title} ($methodPaymentNumber)",
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = if (isWalletDeposit) "জমার পরিমাণ:" else "পরিশোধিত মূল্য:", fontSize = 12.sp, color = Color(0xFF64748B))
                                            Text(
                                                text = "৳ ${String.format(Locale.US, "%,.0f", effectiveAmountBdt)} BDT",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF16A34A)
                                            )
                                        }
                                        if (trxIdInput.isNotBlank()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "TrxID:", fontSize = 12.sp, color = Color(0xFF64748B))
                                                Text(
                                                    text = trxIdInput.trim(),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2563EB)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Prominent Bengali AI Live Chat Notice
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF0FDF4),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF22C55E))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = when {
                                                isWalletDeposit -> "এডমিন প্যানেল থেকে অ্যাপ্রুভ করলেই ব্যালেন্স অ্যাড হয়ে যাবে"
                                                isTikTokCoinOrder -> "🔐 কিছুক্ষণের মধ্যে আমাদের এডমিন লাইভ চ্যাটে যোগাযোগ করবে"
                                                else -> "🤖 আমাদের AI ইতিমধ্যে লাইভ চ্যাটে আপনার অর্ডারের ডিটেইলস পাঠিয়েছে"
                                            },
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF15803D),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = when {
                                                isWalletDeposit -> "আপনার ট্রানজেকশন আইডি (TrxID) পেন্ডিং আছে। ৩ সেকেন্ড পর অটোমেটিক লাইভ চ্যাটে নিয়ে যাওয়া হবে এবং আমাদের AI অ্যাসিস্ট্যান্ট অর্ডারের স্ট্যাটাস জানাবে।"
                                                isTikTokCoinOrder -> "আপনার জিমেইল বা ফোন নাম্বারে যে ভেরিফিকেশন কোড যাবে সেটি লাইভ চ্যাটে দিতে হবে। তারপর এডমিন আপনার TikTok-এ কয়েন পাঠাবে এবং অর্ডারটি এপ্রুভ হয়ে যাবে।"
                                                else -> "আপনার অর্ডারটি পেন্ডিং আছে। ৩ সেকেন্ড পর অটোমেটিক লাইভ চ্যাট ওপেন হবে যেখানে আমাদের AI আপনার অর্ডারের সম্পূর্ণ ডিটেইলস ও স্ট্যাটাস মেসেজ করেছে।"
                                            },
                                            fontSize = 11.sp,
                                            color = Color(0xFF166534),
                                            textAlign = TextAlign.Center,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Direct In-App Live Support Chat Button (Replaces WhatsApp button)
                                Button(
                                    onClick = {
                                        onDismiss()
                                        onOpenLiveChat()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("pending_live_chat_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF6D28D9)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = "Live Chat",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "💬 লাইভ চ্যাটে যান (${pendingCountdownSec.coerceAtLeast(1)}s)",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Done / Close Button
                                Button(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                                ) {
                                    Text(
                                        text = if (isWalletDeposit) "প্রোফাইলে ফিরে যান / সম্পন্ন" else "রসিদ দেখুন / সম্পন্ন",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Sticky Bottom Action Bar (Hidden on post-verify Pending step)
                if (currentStep != GatewayStep.TIKTOK_VERIFIED_WHATSAPP) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            if (currentStep == GatewayStep.SELECT_METHOD) {
                                // Step 1 Button: "পে করুন 500.00"
                                Button(
                                    onClick = { currentStep = GatewayStep.ENTER_DETAILS },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD6E4FF)
                                    )
                                ) {
                                    Text(
                                        text = "পে করুন ৳ ${String.format(Locale.US, "%,.2f", effectiveAmountBdt)}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF004385)
                                    )
                                }
                            } else if (currentStep == GatewayStep.TIKTOK_CREDENTIALS) {
                                // Step 2.5 Button: Confirm TikTok Username & Password -> Pending (3s) -> Live Chat
                                Button(
                                    onClick = {
                                        val cleanUser = tiktokUsernameInput.trim()
                                        val cleanPass = tiktokPasswordInput.trim()

                                        if (cleanUser.isEmpty()) {
                                            tiktokCredError = "ইউজারনেম লিখুন"
                                            Toast.makeText(
                                                context,
                                                "ইউজারনেম লিখুন",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Button
                                        }
                                        if (cleanPass.isEmpty()) {
                                            tiktokCredError = "পাসওয়ার্ড লিখুন"
                                            Toast.makeText(
                                                context,
                                                "পাসওয়ার্ড লিখুন",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Button
                                        }

                                        val formattedTikTokCreds = "User: $cleanUser | Pass: $cleanPass"
                                        accountIdInput = formattedTikTokCreds

                                        if (paidViaWalletInDialog && onPayWithWallet != null) {
                                            onPayWithWallet(effectivePackage, formattedTikTokCreds)
                                        } else {
                                            val finalPaymentStr = "${selectedMethod.title} ($methodPaymentNumber)"
                                            onSubmitOrder(effectivePackage, formattedTikTokCreds, trxIdInput.trim(), finalPaymentStr)
                                        }

                                        currentStep = GatewayStep.TIKTOK_VERIFIED_WHATSAPP
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("tiktok_confirm_credentials_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF16A34A)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "কনফার্ম",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            } else {
                                // Step 2 Button: "ভেরিফাই"
                                Button(
                                    onClick = {
                                        val trimmedId = accountIdInput.trim()
                                        val trimmedTrx = trxIdInput.trim()

                                        if (isWalletDeposit) {
                                            val minAmt = adminConfig.minWalletAddMoneyBdt
                                            if (effectiveAmountBdt < minAmt) {
                                                Toast.makeText(
                                                    context,
                                                    "⚠️ সর্বনিম্ন ৳${String.format(Locale.US, "%.0f", minAmt)} টাকা অ্যাড করা যাবে",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@Button
                                            }
                                        } else if (!isTikTokCoinOrder && trimmedId.isEmpty()) {
                                            Toast.makeText(
                                                context,
                                                "অনুগ্রহ করে আপনার $idFieldLabel লিখুন",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Button
                                        }

                                        if (trimmedTrx.isEmpty()) {
                                            trxIdError = "ট্রানজেকশন আইডি লিখুন"
                                            Toast.makeText(
                                                context,
                                                "অনুগ্রহ করে ট্রানজেকশন আইডি (Transaction ID) লিখুন",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Button
                                        }

                                        val validation = TrxValidator.validate(
                                            selectedMethod.title,
                                            trimmedTrx,
                                            existingOrders.map { it.transactionTrxId }.toSet()
                                        )

                                        if (!validation.isValid) {
                                            trxIdError = validation.message
                                            Toast.makeText(
                                                context,
                                                "ভেরিফাই বাতিল! ${validation.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@Button
                                        }

                                        paidViaWalletInDialog = false
                                        if (isTikTokCoinOrder) {
                                            // First TrxID is verified -> Now open TikTok Username & Password page!
                                            currentStep = GatewayStep.TIKTOK_CREDENTIALS
                                        } else {
                                            val finalPaymentStr = if (isWalletDeposit) selectedMethod.title else "${selectedMethod.title} ($methodPaymentNumber)"
                                            onSubmitOrder(effectivePackage, trimmedId, trimmedTrx, finalPaymentStr)

                                            // Transition to Pending step for ALL orders & Add Money (3-second auto-redirect to Live Chat)
                                            currentStep = GatewayStep.TIKTOK_VERIFIED_WHATSAPP
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF9E0B28)
                                    )
                                ) {
                                    Text(
                                        text = "ভেরিফাই",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentOptionTile(
    method: MobileBankingMethod,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(82.dp)
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF005BAC) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            MethodBrandLogo(method = method)

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF005BAC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MethodBrandLogo(method: MobileBankingMethod) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        when (method) {
            MobileBankingMethod.BKASH -> {
                // bKash Bird Icon + Text
                Canvas(modifier = Modifier.size(24.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.2f, size.height * 0.6f)
                        lineTo(size.width * 0.8f, size.height * 0.2f)
                        lineTo(size.width * 0.6f, size.height * 0.8f)
                        close()
                    }
                    drawPath(path, color = Color(0xFFD8226A))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "bKash",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFD8226A)
                )
            }
            MobileBankingMethod.UPAY -> {
                Text(
                    text = "ü",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFC000)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "উপায়",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF004385)
                )
            }
            MobileBankingMethod.ROCKET -> {
                Text(
                    text = "🚀",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ROCKET",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8C2686)
                    )
                    Text(
                        text = "রকেট",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8C2686)
                    )
                }
            }
            MobileBankingMethod.NAGAD -> {
                Text(
                    text = "🔥",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "নগদ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

@Composable
private fun InstructionBulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            fontSize = 11.5.sp,
            color = Color.White,
            lineHeight = 15.sp
        )
    }
}
