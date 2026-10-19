package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.AdminConfig
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import com.example.util.TrxValidator
import java.util.Locale

@Composable
fun ExchangeOrderDialog(
    sendMethod: String,
    receiveMethod: String,
    amountBdt: Double,
    estimatedInr: Double,
    inrRate: Double,
    adminConfig: AdminConfig,
    onDismiss: () -> Unit,
    onSubmitOrder: (
        sendMethod: String,
        receiveMethod: String,
        bdt: Double,
        inr: Double,
        phonePeNumber: String,
        upiUrl: String,
        screenshotUri: String,
        trxId: String
    ) -> Unit,
    onCopyText: (label: String, text: String) -> Unit
) {
    val context = LocalContext.current

    var currentSendMethod by remember { mutableStateOf(sendMethod) }
    var phonePeNumberInput by remember { mutableStateOf("") }
    var upiUrlInput by remember { mutableStateOf("") }
    var screenshotUriString by remember { mutableStateOf("") }
    var trxIdInput by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            screenshotUriString = uri.toString()
            Toast.makeText(context, "✅ কিউআর কোড/স্ক্রিনশট যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    // Determine admin receiving account based on selected BD Send Method
    val adminNumber = when {
        currentSendMethod.contains("Nagad", ignoreCase = true) -> adminConfig.nagadNumber
        currentSendMethod.contains("Rocket", ignoreCase = true) && adminConfig.rocketNumber.isNotBlank() -> adminConfig.rocketNumber
        else -> adminConfig.bkashNumber
    }

    val adminMethodLabel = when {
        currentSendMethod.contains("Nagad", ignoreCase = true) -> "Nagad Personal"
        currentSendMethod.contains("Rocket", ignoreCase = true) -> "Rocket Personal"
        else -> "bKash Personal"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 450.dp)
                .heightIn(max = 730.dp)
                .clip(RoundedCornerShape(22.dp)),
            color = Color(0xFF0C1726),
            tonalElevation = 10.dp
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
                        .background(Brush.horizontalGradient(listOf(Color(0xFF0F382A), Color(0xFF1E5B42))))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🇮🇳", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PhonePe / UPI Exchange Order",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "BD 🇧🇩 ➔ India 🇮🇳 ইনস্ট্যান্ট মানি এক্সচেঞ্জ",
                                fontSize = 10.5.sp,
                                color = Color(0xFF86EFAC)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterEnd)
                            .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Amount & Calculation Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF132235),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💵 পাঠানোর পরিমাণ (BDT):", fontSize = 12.5.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = "৳${String.format(Locale.US, "%,.0f", amountBdt)} BDT ($sendMethod)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📊 এক্সচেঞ্জ রেট:", fontSize = 12.5.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = "৳${String.format(Locale.US, "%.2f", inrRate)} BDT = ₹1.00 INR",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF38BDF8)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFF334155))
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🇮🇳 ভারতে পাবেন ($receiveMethod):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "₹${String.format(Locale.US, "%,.0f", estimatedInr)} INR",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF86EFAC)
                                )
                            }
                        }
                    }

                    // 2. Step 1: BD Payment To Our Account Card (bKash/Nagad number & Send Money amount)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E1528),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Method selector chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "টাকা পাঠানোর মাধ্যম:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("bKash", "Nagad").forEach { m ->
                                        val isSel = currentSendMethod.contains(m, ignoreCase = true)
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .clickable { currentSendMethod = m },
                                            color = if (isSel) (if (m == "bKash") Color(0xFFD8226A) else Color(0xFFE65100)) else Color(0xFF334155),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (m == "bKash") "বিকাশ" else "নগদ",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Admin receiving number
                            Text(
                                text = "টাকা পাঠানোর একাউন্ট ($adminMethodLabel):",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F0818))
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = adminNumber,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            onCopyText(adminMethodLabel, adminNumber)
                                            Toast.makeText(context, "$adminMethodLabel নাম্বার কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                    color = GoldPrimary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = Color.Black,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "কপি",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Send Money Amount
                            Text(
                                text = "সেন্ড মানি করার পরিমাণ (Send Money Amount):",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F0818))
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "৳ ${String.format(Locale.US, "%,.0f", amountBdt)} BDT",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            val amtStr = String.format(Locale.US, "%.0f", amountBdt)
                                            onCopyText("Send Money Amount", amtStr)
                                            Toast.makeText(context, "পরিমাণ ৳$amtStr কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                    color = Color(0xFF334155),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "কপি",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. QR Code Screenshot / Photo Upload Button (ছবি দেওয়ার বাটন)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "১. PhonePe / UPI QR কোডের স্ক্রিনশট (ছবি দিন)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "QR Screenshot",
                                fontSize = 10.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        if (screenshotUriString.isNotBlank()) {
                            // Display Thumbnail Preview
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.2.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp)),
                                color = Color(0xFF0F2018)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AsyncImage(
                                            model = screenshotUriString,
                                            contentDescription = "QR Screenshot",
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = "✅ QR কোড ইমেজ সংযুক্ত হয়েছে",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF86EFAC)
                                            )
                                            Text(
                                                text = "এডমিন স্ক্রিনশট দেখে পেমেন্ট সম্পন্ন করবে",
                                                fontSize = 10.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AddPhotoAlternate,
                                                contentDescription = "Change Image",
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { screenshotUriString = "" },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove Image",
                                                tint = Color(0xFFF87171),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Upload Button Placeholder
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        1.dp,
                                        Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF86EFAC))),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                color = Color(0xFF0F1E2E)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 14.dp, horizontal = 14.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Upload QR",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "📷 PhonePe QR কোডের স্ক্রিনশট দিন (ছবি আপলোড)",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "গ্যালারি থেকে আপনার QR Code এর ছবি সিলেক্ট করুন",
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Payment Link / URL (সে নিচে ইউ আর এল)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "২. পেমেন্ট URL বা লিংক (Optional / ঐচ্ছিক)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBD5E1)
                        )
                        OutlinedTextField(
                            value = upiUrlInput,
                            onValueChange = { upiUrlInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "যেমন: upi://pay?pa=... অথবা পেমেন্ট লিংক",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF132235),
                                unfocusedContainerColor = Color(0xFF132235),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true
                        )
                    }

                    // 5. PhonePe / UPI Number or UPI ID Field (তার Phonepe নাম্বার দিতে হবে)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "৩. PhonePe / UPI নাম্বার বা UPI ID *",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "আবশ্যক",
                                fontSize = 10.5.sp,
                                color = Color(0xFFF87171),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedTextField(
                            value = phonePeNumberInput,
                            onValueChange = { phonePeNumberInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "PhonePe নাম্বার বা UPI ID লিখুন",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF86EFAC),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF132235),
                                unfocusedContainerColor = Color(0xFF132235),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true
                        )
                        Text(
                            text = "💡 ভারতীয় প্রাপকের PhonePe নাম্বার, Google Pay নাম্বার বা UPI ID লিখুন।",
                            fontSize = 10.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // 6. Sender TrxID (Transaction ID) (তারপর ট্রানজেকশন আইডি)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "৪. টাকা পাঠানোর Transaction ID (TrxID) *",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "আবশ্যক",
                                fontSize = 10.5.sp,
                                color = Color(0xFFF87171),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedTextField(
                            value = trxIdInput,
                            onValueChange = { trxIdInput = it.uppercase().filter { ch -> ch.isLetterOrDigit() }.take(14) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "আপনার বিকাশ/নগদ Transaction ID (TrxID) লিখুন",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF132235),
                                unfocusedContainerColor = Color(0xFF132235),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true
                        )
                    }

                    // Security Note
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF132235), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "অর্ডার সাবমিট করার পর এডমিন QR কোড ও TrxID যাচাই করে ৫-১০ মিনিটে পেমেন্ট সম্পন্ন করবে।",
                            fontSize = 10.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 7. Submit Exchange Order Button (দিয়ে ভেরিফাই)
                    Button(
                        onClick = {
                            val phonePe = phonePeNumberInput.trim()
                            val trxId = trxIdInput.trim()

                            if (phonePe.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "অনুগ্রহ করে PhonePe / UPI নাম্বার বা UPI ID লিখুন",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            if (trxId.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "অনুগ্রহ করে বিকাশ/নগদ এর Transaction ID (TrxID) লিখুন",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            val valRes = TrxValidator.validate(currentSendMethod, trxId)
                            if (!valRes.isValid) {
                                Toast.makeText(
                                    context,
                                    "ভেরিফাই ব্যর্থ! ${valRes.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }

                            onSubmitOrder(
                                currentSendMethod,
                                receiveMethod,
                                amountBdt,
                                estimatedInr,
                                phonePe,
                                upiUrlInput.trim(),
                                screenshotUriString,
                                trxId
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text(
                            text = "✅ ভেরিফাই ও এক্সচেঞ্জ অর্ডার নিশ্চিত করুন",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
