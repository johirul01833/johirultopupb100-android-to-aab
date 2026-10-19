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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OrderRecord
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.WhatsAppGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptDialog(
    order: OrderRecord,
    onDismiss: () -> Unit,
    onCopyReceipt: (String) -> Unit,
    onDeleteOrder: (OrderRecord) -> Unit,
    onWhatsAppDirect: () -> Unit,
    onOpenLiveChat: () -> Unit = onWhatsAppDirect,
    onStatusChange: (OrderRecord, String) -> Unit = { _, _ -> }
) {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = sdf.format(Date(order.timestamp))

    val receiptText = """
        🧾 Chamet Top Up BD - ORDER SLIP
        ───────────────────────────────
        Order ID: ${order.orderNumber}
        Category: ${order.category}
        Item: ${order.itemName} (${order.amountOrQuantity})
        Price: ৳${String.format(Locale.US, "%,.0f", order.priceBdt)} BDT
        Account / UID: ${order.userAccountId}
        Payment: ${order.paymentMethod}
        ${if (order.transactionTrxId.isNotBlank()) "TrxID: ${order.transactionTrxId}\n" else ""}Status: ${order.status}
        Date: $dateStr
        ───────────────────────────────
        Support: WhatsApp +8801880467335 | Telegram @johirul0188
    """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Brush.linearGradient(listOf(GoldPrimary, NeonMagenta)), RoundedCornerShape(24.dp)),
            color = Color(0xFF140A22)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF26004E), Color(0xFF57008F))))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "🧾 Digital Order Receipt",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Status Badge & Order Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Order ID", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                            Text(text = order.orderNumber, fontSize = 16.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                        }

                        val statusColor = when (order.status) {
                            "Completed" -> SuccessGreen
                            "Processing" -> WarningYellow
                            "Cancelled" -> Color(0xFFEF4444)
                            else -> WarningYellow
                        }

                        val statusLabel = when (order.status) {
                            "Completed" -> "✅ Approved (এপ্রুভড)"
                            "Cancelled" -> "❌ ভুল TrxID"
                            else -> "⏳ Processing (প্রসেসিং)"
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusColor.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = statusLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slip Breakdown Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1C0E30),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B1E60))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            ReceiptRow("Service", order.category)
                            ReceiptRow("Package", order.itemName)
                            ReceiptRow("Amount", order.amountOrQuantity)
                            ReceiptRow("Price", "৳${String.format(Locale.US, "%,.0f", order.priceBdt)} BDT", isHighlighted = true)
                            ReceiptRow("Account / UID", order.userAccountId)
                            ReceiptRow("Payment Method", order.paymentMethod)
                            if (order.transactionTrxId.isNotBlank()) {
                                ReceiptRow("TrxID / Note", order.transactionTrxId)
                            }
                            ReceiptRow("Date & Time", dateStr)
                            ReceiptRow("Platform Sent", order.contactPlatform)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Read-only Live Admin Verification Status Banner (Only Admin Panel can change status)
                    val statusBannerBg = when (order.status) {
                        "Completed" -> Color(0xFF064E3B).copy(alpha = 0.85f)
                        "Cancelled" -> Color(0xFF450A0A).copy(alpha = 0.85f)
                        else -> Color(0xFF2E1F05).copy(alpha = 0.9f)
                    }
                    val statusBannerBorder = when (order.status) {
                        "Completed" -> SuccessGreen
                        "Cancelled" -> Color(0xFFEF4444)
                        else -> WarningYellow
                    }
                    val statusBannerTitle = when (order.status) {
                        "Completed" -> "✅ এডমিন প্যানেল থেকে এপ্রুভ (Approved) করা হয়েছে"
                        "Cancelled" -> "❌ ভুল TrxID! এডমিন প্যানেল থেকে বাতিল করা হয়েছে"
                        else -> "⏳ অর্ডার স্ট্যাটাস: প্রসেসিং (Processing)"
                    }
                    val statusBannerSub = when (order.status) {
                        "Completed" -> "আপনার অর্ডারটি সফলভাবে যাচাই ও এপ্রুভ হয়েছে। উপরের নোটিফিকেশন বেল (🔔) আইকনেও কনফার্মেশন মেসেজ পাঠানো হয়েছে।"
                        "Cancelled" -> "আপনার প্রদত্ত Transaction ID (TrxID) সঠিক নয়। অনুগ্রহ করে সঠিক তথ্য দিয়ে নিচের WhatsApp বাটনে যোগাযোগ করুন।"
                        else -> "আপনার অর্ডারটি বর্তমানে প্রসেসিং এ রয়েছে। এডমিন প্যানেল থেকে এপ্রুভ করা হলে উপরের নোটিফিকেশন (🔔) অপশনে মেসেজ চলে যাবে এবং এখানে এপ্রুভ দেখাবে।"
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = statusBannerBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusBannerBorder.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = statusBannerTitle,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = statusBannerBorder
                            )
                            Text(
                                text = statusBannerSub,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E1B4B).copy(alpha = 0.88f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অর্ডার স্ট্যাটাস ও ডেলিভারির জন্য নিচের লাইভ চ্যাট বাটনে ক্লিক করুন",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDDD6FE)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onCopyReceipt(receiptText) },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF381B5E))
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Slip", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onOpenLiveChat()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9))
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(17.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("লাইভ চ্যাট", fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onDeleteOrder(order)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete from History", fontSize = 12.sp, color = DangerRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF9CA3AF))
        Text(
            text = value,
            fontSize = if (isHighlighted) 14.sp else 12.sp,
            fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.SemiBold,
            color = if (isHighlighted) GoldPrimary else Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
