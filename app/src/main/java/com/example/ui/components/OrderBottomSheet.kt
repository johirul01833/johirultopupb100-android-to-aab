package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TopupRepository
import com.example.data.model.ServiceCategory
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.OrderFormState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(
    formState: OrderFormState,
    onDismiss: () -> Unit,
    onUidChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPaymentChange: (String) -> Unit,
    onTrxChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveUidToggle: (Boolean) -> Unit,
    onSubmitOrderWhatsApp: () -> Unit,
    onSubmitOrderTelegram: () -> Unit,
    onCopyPaymentNumber: (String, String) -> Unit
) {
    val pkg = formState.packageItem ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val idLabel = when (pkg.category) {
        ServiceCategory.TIKTOK -> "TikTok Username / UID"
        ServiceCategory.CHAMET -> "Chamet User ID"
        ServiceCategory.FREEFIRE -> "Player UID"
        else -> "Account / User ID"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF140A22),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary.copy(alpha = 0.6f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🛒 Complete Your Order",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${pkg.category.displayName} • ${pkg.subtitle}",
                        fontSize = 12.sp,
                        color = GoldLight
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = pkg.category.iconEmoji,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Package Price Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1032),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.6f), NeonMagenta.copy(alpha = 0.6f)))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = pkg.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Amount: ${pkg.amount}",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    Text(
                        text = "৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // UID Field
            Text(
                text = "$idLabel *",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = formState.userAccountId,
                onValueChange = onUidChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter $idLabel", color = Color(0xFF6B7280)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF3B1E60),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF160B24),
                    unfocusedContainerColor = Color(0xFF160B24)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Save UID Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSaveUidToggle(!formState.saveUidForFuture) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = formState.saveUidForFuture,
                    onCheckedChange = { onSaveUidToggle(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GoldPrimary,
                        checkmarkColor = Color.Black,
                        uncheckedColor = Color(0xFF6B7280)
                    )
                )
                Text(
                    text = "পরবর্তী অর্ডারের জন্য এই UID সংরক্ষণ করুন (Save UID)",
                    fontSize = 12.sp,
                    color = Color(0xFFD1D5DB)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Method Selector
            Text(
                text = "পেমেন্ট মাধ্যম (Select Payment Method)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TopupRepository.PAYMENT_ACCOUNTS.forEach { account ->
                    val isSelected = formState.paymentMethod.startsWith(account.method)
                    val borderColor = if (isSelected) GoldPrimary else Color(0xFF2E1748)
                    val bgColor = if (isSelected) Color(0xFF26123D) else Color(0xFF160B24)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPaymentChange("${account.method} (${account.number})") },
                        shape = RoundedCornerShape(12.dp),
                        color = bgColor,
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(account.colorHex))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = account.method,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) GoldPrimary else Color.White
                                    )
                                    Text(
                                        text = account.number,
                                        fontSize = 12.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onCopyPaymentNumber(account.method, account.number) },
                                    color = Color(0xFF381B5E),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = GoldLight,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy", fontSize = 10.sp, color = GoldLight)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transaction ID (TrxID) Field (Optional)
            Text(
                text = "Transaction ID / TrxID (যদি পেমেন্ট করে থাকেন)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = formState.transactionTrxId,
                onValueChange = onTrxChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 9J87K56L or Last 4 Digits", color = Color(0xFF6B7280)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF3B1E60),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF160B24),
                    unfocusedContainerColor = Color(0xFF160B24)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (pkg.category == ServiceCategory.TIKTOK) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF14532D).copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
                ) {
                    Text(
                        text = "হোয়াটসঅ্যাপ পাঠানো ক্লিক করে যোগাযোগ করুন কয়েন নিতে",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF86EFAC),
                        modifier = Modifier.padding(10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Order dispatch actions
            Text(
                text = "📲 অর্ডার সম্পন্ন করতে যেকোনো একটি মাধ্যম বেছে নিন:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GoldLight
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumWhatsAppButton(
                    text = "WhatsApp",
                    subtitle = "সরাসরি অর্ডার",
                    onClick = onSubmitOrderWhatsApp,
                    modifier = Modifier.weight(1f),
                    height = 54.dp,
                    cornerRadius = 14.dp,
                    fontSize = 13.sp,
                    iconSize = 26.dp
                )

                PremiumTelegramButton(
                    text = "Telegram",
                    subtitle = "কপি ও চ্যাট",
                    onClick = onSubmitOrderTelegram,
                    modifier = Modifier.weight(1f),
                    height = 54.dp,
                    cornerRadius = 14.dp,
                    fontSize = 13.sp,
                    iconSize = 26.dp
                )
            }
        }
    }
}
