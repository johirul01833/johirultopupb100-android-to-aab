package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.data.model.TopupPackage
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WhatsAppGreen
import java.util.Locale

@Composable
fun PackageContactChooserDialog(
    packageItem: TopupPackage,
    onDismiss: () -> Unit,
    onWhatsAppClick: (TopupPackage) -> Unit,
    onTelegramClick: (TopupPackage) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    1.5.dp,
                    Brush.linearGradient(listOf(GoldPrimary, NeonMagenta, Color(0xFF6B21A8))),
                    RoundedCornerShape(24.dp)
                ),
            color = Color(0xFF140924),
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row with Close Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = packageItem.category.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "অর্ডার করার মাধ্যম নির্বাচন করুন",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selected Package Summary Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E0E35),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
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
                                text = "${packageItem.category.displayName} • ${packageItem.subtitle}",
                                fontSize = 11.sp,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = packageItem.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "মূল্য (Price)",
                                fontSize = 10.sp,
                                color = Color(0xFF9CA3AF)
                            )
                            Text(
                                text = "৳${String.format(Locale.US, "%,.0f", packageItem.priceBdt)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "আপনি কোন মাধ্যমে যোগাযোগ করে প্যাকেজটি নিতে চান?",
                    fontSize = 12.5.sp,
                    color = Color(0xFFE5E7EB),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // WhatsApp Option Button (Official Vector Logo)
                PremiumWhatsAppButton(
                    text = "WhatsApp-এ অর্ডার করুন",
                    subtitle = "+880 1880-467335",
                    onClick = { onWhatsAppClick(packageItem) },
                    modifier = Modifier.fillMaxWidth(),
                    height = 56.dp,
                    cornerRadius = 14.dp,
                    fontSize = 14.sp,
                    iconSize = 32.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Telegram Option Button (Official Vector Logo)
                PremiumTelegramButton(
                    text = "Telegram-এ অর্ডার করুন",
                    subtitle = "@johirul0188",
                    onClick = { onTelegramClick(packageItem) },
                    modifier = Modifier.fillMaxWidth(),
                    height = 56.dp,
                    cornerRadius = 14.dp,
                    fontSize = 14.sp,
                    iconSize = 32.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Notice / Security Note
                Text(
                    text = "🔒 ১০০% সুরক্ষিত ও বিশ্বস্ত লেনদেন • ২-৫ মিনিটে ডেলিভারি",
                    fontSize = 11.sp,
                    color = GoldLight,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
