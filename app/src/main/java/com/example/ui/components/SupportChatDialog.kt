package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.SupportChatMessage
import com.example.ui.theme.BrandDarkBg
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun copyChatTextToClipboard(context: Context, text: String) {
    if (text.isBlank()) return
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("SupportChatMessage", text))
    Toast.makeText(context, "✅ টেক্সট কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
}

private data class QuickSupportMessageChipData(
    val emoji: String,
    val label: String,
    val promptText: String,
    val accentColor: Color
)

@Composable
fun SupportChatDialog(
    messages: List<SupportChatMessage>,
    loggedInUserName: String = "",
    loggedInUserEmail: String = "",
    loggedInUserPhone: String = "",
    savedSupportName: String = "",
    savedSupportPhone: String = "",
    savedSupportEmail: String = "",
    deviceId: String = "",
    isSending: Boolean,
    isAiTyping: Boolean = false,
    conversationMode: String = "AI_AUTO",
    isAiMasterEnabled: Boolean = true,
    isAuthLoading: Boolean = false,
    authErrorMessage: String? = null,
    onSignInUser: (email: String, pass: String) -> Unit = { _, _ -> },
    onRegisterUser: (name: String, phone: String, email: String, pass: String) -> Unit = { _, _, _, _ -> },
    onDismiss: () -> Unit,
    onSaveUserProfile: (name: String, phone: String, email: String) -> Unit = { _, _, _ -> },
    onRequestAdminHandoff: () -> Unit = {},
    onResumeAiAutoReply: () -> Unit = {},
    onMarkMessagesReadByUser: (String) -> Unit = {},
    onSendMessage: (
        senderName: String,
        senderContact: String,
        senderEmail: String,
        messageText: String,
        imageUrl: String
    ) -> Unit
) {
    val context = LocalContext.current
    val cleanUserEmail = loggedInUserEmail.trim().lowercase()
        .ifBlank { savedSupportEmail.trim().lowercase() }
        .ifBlank { deviceId.trim().lowercase() }

    // Inline Login / Sign Up form states when user is not logged in
    var isRegisterMode by remember { mutableStateOf(false) }
    var authName by remember { mutableStateOf(savedSupportName) }
    var authPhone by remember { mutableStateOf(savedSupportPhone) }
    var authEmail by remember { mutableStateOf(savedSupportEmail) }
    var authPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var localAuthError by remember { mutableStateOf<String?>(null) }

    var messageInput by remember { mutableStateOf("") }
    var selectedImageBase64 by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var fullScreenImageData by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            val encoded = ImageBase64Util.uriToCompressedBase64(context, uri)
            if (encoded != null) {
                selectedImageBase64 = encoded
            }
        }
    }

    // Strictly isolate chat history by authenticated email (Security Requirement #12)
    val userConversationMessages = remember(messages, cleanUserEmail) {
        if (cleanUserEmail.isEmpty()) {
            emptyList()
        } else {
            messages.filter { msg ->
                val msgEmail = msg.senderEmail.trim().lowercase()
                val msgConv = msg.conversationId.trim().lowercase()
                    .removePrefix("email_")
                    .removePrefix("user_")
                msgEmail == cleanUserEmail || msgConv == cleanUserEmail
            }.sortedBy { it.timestamp }
        }
    }

    val isUserAuthenticated = loggedInUserEmail.trim().isNotBlank() || userConversationMessages.isNotEmpty()

    // Determine effective conversation mode (AI_AUTO vs HANDOFF_REQUESTED vs ADMIN_TAKEOVER)
    val effectiveMode = remember(conversationMode, userConversationMessages, isAiMasterEnabled) {
        if (!isAiMasterEnabled) {
            "ADMIN_TAKEOVER"
        } else {
            val latestMarker = userConversationMessages
                .lastOrNull { it.adminReply.startsWith("MODE:") }
                ?.adminReply
                ?.removePrefix("MODE:")
                ?.trim()
                ?.uppercase()
            when {
                conversationMode == "HANDOFF_REQUESTED" || conversationMode == "ADMIN_TAKEOVER" -> conversationMode
                latestMarker == "HANDOFF_REQUESTED" || latestMarker == "ADMIN_TAKEOVER" -> latestMarker
                else -> "AI_AUTO"
            }
        }
    }
    val isHumanHandoffActive = effectiveMode == "HANDOFF_REQUESTED" || effectiveMode == "ADMIN_TAKEOVER"

    val listState = rememberLazyListState()

    LaunchedEffect(userConversationMessages.size, isAiTyping) {
        if (cleanUserEmail.isNotEmpty()) {
            onMarkMessagesReadByUser(cleanUserEmail)
        }
        val totalItems = userConversationMessages.size + (if (isAiTyping) 1 else 0)
        if (totalItems > 0) {
            listState.animateScrollToItem(totalItems - 1)
        }
    }

    val quickReplyChips = remember {
        listOf(
            QuickSupportMessageChipData("📦", "আমার অর্ডার কোথায়?", "আমার অর্ডার কোথায়?", GoldPrimary),
            QuickSupportMessageChipData("🪙", "TikTok Coins প্রাইস", "TikTok Coins প্রাইস কত?", Color(0xFF00F2FE)),
            QuickSupportMessageChipData("💎", "Chamet Diamonds রেট", "Chamet Diamonds প্যাকেজ ও প্রাইস কত?", NeonMagenta),
            QuickSupportMessageChipData("💵", "EPAY Dollar Sell", "EPAY Dollar Sell রেট ও নিয়ম কি?", Color(0xFF38BDF8)),
            QuickSupportMessageChipData("🔄", "BD Exchange রেট", "BD to India Exchange রেট কত?", SuccessGreen),
            QuickSupportMessageChipData("🚀", "Social Boost সার্ভিস", "Social Boost সার্ভিস ও প্রাইস সম্পর্কে জানতে চাই", Color(0xFFA855F7)),
            QuickSupportMessageChipData("💳", "পেমেন্ট নাম্বার", "বিকাশ ও নগদ পেমেন্ট নাম্বার দিন", Color(0xFFF472B6)),
            QuickSupportMessageChipData("👛", "ওয়ালেট ও রেফারেল", "ওয়ালেটে টাকা অ্যাড ও রেফারেল বোনাস কিভাবে পাবো?", GoldLight)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF090D16)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF090D16), Color(0xFF060911))
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // 1. ULTRA-MODERN SLEEK TOP BAR
            Surface(
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                onClick = onDismiss,
                                shape = CircleShape,
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("support_chat_back_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(contentAlignment = Alignment.BottomEnd) {
                                SupportReferenceSquircle(size = 40.dp)
                                Box(
                                    modifier = Modifier
                                        .size(11.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                        .border(1.5.dp, Color(0xFF0F172A), CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Chamet Top Up BD",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isHumanHandoffActive) {
                                        "👨‍💻 Official Admin Online"
                                    } else {
                                        "🟢 AI Assistant & Live Support"
                                    },
                                    fontSize = 11.sp,
                                    color = if (isHumanHandoffActive) Color(0xFFFBBF24) else Color(0xFF34D399),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Modern Header Action Button
                        if (isUserAuthenticated) {
                            if (!isHumanHandoffActive) {
                                Surface(
                                    onClick = onRequestAdminHandoff,
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                                    modifier = Modifier.testTag("header_talk_to_admin_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "এডমিন চ্যাট",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    onClick = onResumeAiAutoReply,
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f)),
                                    modifier = Modifier.testTag("header_resume_ai_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🤖", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "AI চালু করুন",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF34D399)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Active Handoff Banner when customer is connected to Human Admin
                    AnimatedVisibility(visible = isUserAuthenticated && isHumanHandoffActive) {
                        Surface(
                            color = Color(0xFF1E293B),
                            border = BorderStroke(0.5.dp, Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "👨‍💻", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (effectiveMode == "ADMIN_TAKEOVER") {
                                        "Official Admin আপনার চ্যাটে যুক্ত আছেন।"
                                    } else {
                                        "এডমিন সরাসরি আপনার মেসেজের উত্তর দেবেন।"
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFFFDE68A),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            if (!isUserAuthenticated) {
                // REQUIRE LOGIN OR SIGN UP BEFORE CHATTING (Security Requirement #12)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFF1A0933),
                        border = BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.55f)),
                        shadowElevation = 12.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.15f))
                                    .border(1.5.dp, GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🔐", fontSize = 26.sp)
                            }

                            Text(
                                text = if (isRegisterMode) "নতুন একাউন্ট খুলুন (Sign Up)" else "সাপোর্ট চ্যাটে লগইন করুন",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "আপনার ব্যক্তিগত চ্যাট এবং অর্ডার স্ট্যাটাস সুরক্ষিত রাখতে আপনার ইমেইল দিয়ে লগইন করুন।",
                                fontSize = 12.sp,
                                color = Color(0xFFD1D5DB),
                                textAlign = TextAlign.Center,
                                lineHeight = 17.sp
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0E041D))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = {
                                        isRegisterMode = false
                                        localAuthError = null
                                    },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(9.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!isRegisterMode) GoldPrimary else Color.Transparent
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "লগইন (Login)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isRegisterMode) Color(0xFF120424) else Color.White
                                    )
                                }
                                Button(
                                    onClick = {
                                        isRegisterMode = true
                                        localAuthError = null
                                    },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(9.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isRegisterMode) GoldPrimary else Color.Transparent
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "রেজিস্টার (Sign Up)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isRegisterMode) Color(0xFF120424) else Color.White
                                    )
                                }
                            }

                            if (isRegisterMode) {
                                OutlinedTextField(
                                    value = authName,
                                    onValueChange = { authName = it },
                                    label = { Text("আপনার নাম", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = Color(0xFF4B2C70),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF110522),
                                        unfocusedContainerColor = Color(0xFF110522)
                                    )
                                )
                                OutlinedTextField(
                                    value = authPhone,
                                    onValueChange = { authPhone = it },
                                    label = { Text("মোবাইল / WhatsApp নাম্বার", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = Color(0xFF4B2C70),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF110522),
                                        unfocusedContainerColor = Color(0xFF110522)
                                    )
                                )
                            }

                            OutlinedTextField(
                                value = authEmail,
                                onValueChange = { authEmail = it },
                                label = { Text("আপনার ইমেইল (Gmail)", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GoldPrimary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0xFF4B2C70),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF110522),
                                    unfocusedContainerColor = Color(0xFF110522)
                                )
                            )

                            OutlinedTextField(
                                value = authPassword,
                                onValueChange = { authPassword = it },
                                label = { Text("পাসওয়ার্ড", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password",
                                            tint = Color(0xFF9CA3AF)
                                        )
                                    }
                                },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0xFF4B2C70),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF110522),
                                    unfocusedContainerColor = Color(0xFF110522)
                                )
                            )

                            val displayErr = localAuthError ?: authErrorMessage
                            if (!displayErr.isNullOrBlank()) {
                                Text(
                                    text = displayErr,
                                    fontSize = 11.5.sp,
                                    color = DangerRed,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Button(
                                onClick = {
                                    localAuthError = null
                                    val cleanMail = authEmail.trim()
                                    val cleanPass = authPassword.trim()
                                    if (cleanMail.isEmpty() || !cleanMail.contains("@")) {
                                        localAuthError = "অনুগ্রহ করে সঠিক ইমেইল দিন"
                                        return@Button
                                    }
                                    if (cleanPass.length < 6) {
                                        localAuthError = "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে"
                                        return@Button
                                    }
                                    if (isRegisterMode) {
                                        if (authName.trim().isEmpty()) {
                                            localAuthError = "অনুগ্রহ করে আপনার নাম লিখুন"
                                            return@Button
                                        }
                                        onRegisterUser(authName.trim(), authPhone.trim(), cleanMail, cleanPass)
                                    } else {
                                        onSignInUser(cleanMail, cleanPass)
                                    }
                                },
                                enabled = !isAuthLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A884))
                            ) {
                                if (isAuthLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = if (isRegisterMode) "✅ একাউন্ট তৈরি করে চ্যাট শুরু করুন" else "🚀 লগইন করে চ্যাট শুরু করুন",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 2. SUBTLE INFO STRIP (With Long-Press Copy Hint)
                Surface(
                    color = Color(0xFF0D1322),
                    border = BorderStroke(0.5.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👤 ${loggedInUserName.ifBlank { cleanUserEmail.substringBefore("@") }}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "💡 মেসেজ চেপে ধরলে কপি অপশন আসবে",
                            fontSize = 10.5.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // 3. CHAT MESSAGE LIST
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (userConversationMessages.isEmpty() && !isAiTyping) {
                        item {
                            WelcomeSupportAiCard(
                                userName = loggedInUserName.ifBlank { cleanUserEmail.substringBefore("@") },
                                isHumanHandoffActive = isHumanHandoffActive,
                                onQuickQuestionClick = { queryText ->
                                    onSendMessage(
                                        loggedInUserName,
                                        loggedInUserPhone,
                                        cleanUserEmail,
                                        queryText,
                                        ""
                                    )
                                },
                                onTalkToAdminClick = onRequestAdminHandoff
                            )
                        }
                    } else {
                        items(userConversationMessages, key = { it.id }) { msg ->
                            CustomerChatThreadBubble(
                                msg = msg,
                                isHumanHandoffActive = isHumanHandoffActive,
                                onImageClick = { imgData -> fullScreenImageData = imgData },
                                onTalkToAdminClick = onRequestAdminHandoff
                            )
                        }
                    }

                    // Animated AI Typing Indicator Bubble
                    if (isAiTyping) {
                        item(key = "ai_typing_indicator") {
                            AiTypingIndicatorBubble()
                        }
                    }
                }

                // 4. SELECTED IMAGE PREVIEW BAR (IF ANY)
                AnimatedVisibility(
                    visible = selectedImageBase64.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = Color(0xFF1F0D3D),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val bmp = remember(selectedImageBase64) {
                                ImageBase64Util.base64ToBitmap(selectedImageBase64)
                            }
                            if (bmp != null) {
                                AsyncImage(
                                    model = bmp,
                                    contentDescription = "Selected Screenshot",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, GoldPrimary, RoundedCornerShape(10.dp))
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📸 স্ক্রিনশট সংযুক্ত করা হয়েছে",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                                Text(
                                    text = "মেসেজ পাঠালে ছবিটি সহ সেন্ড হবে",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFFD1D5DB)
                                )
                            }
                            IconButton(
                                onClick = {
                                    selectedImageBase64 = ""
                                    selectedImageUri = null
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove image",
                                    tint = DangerRed
                                )
                            }
                        }
                    }
                }

                // 5. MODERN PROFESSIONAL BOTTOM COMPOSER DOCK (Quick Chips + Photo Upload + Capsule Text Field + Send)
                Surface(
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .padding(top = 8.dp, bottom = 10.dp)
                    ) {
                        // 5A. Modern Quick Reply Chips Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!isHumanHandoffActive) {
                                LuxuryQuickActionPillButton(
                                    emoji = "💬",
                                    label = "Talk to Admin",
                                    isPrimaryGold = true,
                                    accentColor = Color(0xFF38BDF8),
                                    onClick = onRequestAdminHandoff,
                                    modifier = Modifier.testTag("quick_chip_talk_to_admin")
                                )
                            } else {
                                LuxuryQuickActionPillButton(
                                    emoji = "🤖",
                                    label = "AI চালু করুন",
                                    isPrimaryGold = false,
                                    accentColor = Color(0xFF10B981),
                                    onClick = onResumeAiAutoReply,
                                    modifier = Modifier.testTag("quick_chip_resume_ai")
                                )
                            }

                            quickReplyChips.forEach { chip ->
                                LuxuryQuickActionPillButton(
                                    emoji = chip.emoji,
                                    label = chip.label,
                                    isPrimaryGold = false,
                                    accentColor = chip.accentColor,
                                    onClick = {
                                        onSendMessage(
                                            loggedInUserName,
                                            loggedInUserPhone,
                                            cleanUserEmail,
                                            chip.promptText,
                                            ""
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 5B. Ultra-Modern Message Composer Row (Photo Upload + Capsule Input + Send Button)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1. Modern Sleek Photo / Screenshot Upload Button
                            Surface(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = CircleShape,
                                color = if (selectedImageBase64.isNotEmpty()) Color(0xFF065F46) else Color(0xFF1E293B),
                                border = BorderStroke(
                                    1.dp,
                                    if (selectedImageBase64.isNotEmpty()) Color(0xFF10B981) else Color(0xFF334155)
                                ),
                                modifier = Modifier
                                    .size(46.dp)
                                    .testTag("support_chat_attach_photo_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Attach Screenshot",
                                        tint = if (selectedImageBase64.isNotEmpty()) Color(0xFF34D399) else Color(0xFF38BDF8),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // 2. Modern Borderless-Feel Capsule Message Input Field
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 46.dp, max = 110.dp),
                                shape = RoundedCornerShape(24.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (messageInput.isNotEmpty()) Color(0xFF38BDF8).copy(alpha = 0.7f) else Color(0xFF334155)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 11.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (messageInput.isEmpty()) {
                                            Text(
                                                text = if (isHumanHandoffActive) {
                                                    "এডমিনকে মেসেজ বা কোড পাঠান..."
                                                } else {
                                                    "মেসেজ লিখুন..."
                                                },
                                                fontSize = 13.5.sp,
                                                color = Color(0xFF94A3B8),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        BasicTextField(
                                            value = messageInput,
                                            onValueChange = { messageInput = it },
                                            textStyle = TextStyle(
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 19.sp
                                            ),
                                            cursorBrush = SolidColor(Color(0xFF38BDF8)),
                                            maxLines = 4,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("support_chat_message_input")
                                        )
                                    }

                                    if (messageInput.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF334155))
                                                .clickable { messageInput = "" },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = Color(0xFFCBD5E1),
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 3. Modern Animated Circular/Pill Send Button
                            val canSend = (messageInput.trim().isNotEmpty() || selectedImageBase64.isNotEmpty()) && !isSending
                            val sendInteraction = remember { MutableInteractionSource() }
                            val isSendPressed by sendInteraction.collectIsPressedAsState()
                            val sendScale by animateFloatAsState(
                                targetValue = if (isSendPressed) 0.92f else 1f,
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                label = "send_btn_scale"
                            )

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .scale(sendScale)
                                    .shadow(
                                        elevation = if (canSend) 8.dp else 0.dp,
                                        shape = CircleShape,
                                        spotColor = Color(0xFF3B82F6)
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        brush = if (canSend) {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF2563EB),
                                                    Color(0xFF4F46E5),
                                                    Color(0xFF7C3AED)
                                                )
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF1E293B),
                                                    Color(0xFF1E293B)
                                                )
                                            )
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (canSend) Color(0xFF60A5FA).copy(alpha = 0.6f) else Color(0xFF334155),
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        interactionSource = sendInteraction,
                                        indication = ripple(color = Color.White),
                                        enabled = canSend
                                    ) {
                                        onSendMessage(
                                            loggedInUserName,
                                            loggedInUserPhone,
                                            cleanUserEmail,
                                            messageInput.trim(),
                                            selectedImageBase64
                                        )
                                        messageInput = ""
                                        selectedImageBase64 = ""
                                        selectedImageUri = null
                                    }
                                    .testTag("support_chat_send_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSending) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send Message",
                                        tint = if (canSend) Color.White else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Full-screen image viewer overlay
    if (fullScreenImageData != null) {
        FullScreenChatImageDialog(
            imageData = fullScreenImageData!!,
            onDismiss = { fullScreenImageData = null }
        )
    }
}

/**
 * Luxury Neon-Glass Quick Message Pill Button used in the bottom message bar.
 */
@Composable
fun LuxuryQuickActionPillButton(
    emoji: String,
    label: String,
    isPrimaryGold: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "quick_pill_scale"
    )
    val pillShape = RoundedCornerShape(50)

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = pillShape,
        color = if (isPrimaryGold) Color(0xFF1E3A8A) else Color(0xFF1E293B),
        border = BorderStroke(
            width = 1.dp,
            color = if (isPrimaryGold) Color(0xFF38BDF8) else Color(0xFF334155)
        ),
        modifier = modifier.scale(scale)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPrimaryGold) Color.White else Color(0xFFE2E8F0),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun WelcomeSupportAiCard(
    userName: String,
    isHumanHandoffActive: Boolean,
    onQuickQuestionClick: (String) -> Unit,
    onTalkToAdminClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF180830),
        border = BorderStroke(
            1.3.dp,
            Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.7f), NeonMagenta.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.6f)))
        ),
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF210C40), Color(0xFF130626))
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SupportReferenceSquircle(size = 44.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "আসসালামু আলাইকুম, $userName! 👋",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                    Text(
                        text = "CHAMET TOP UP BD • AI Smart Support & Live Admin",
                        fontSize = 11.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = "আমাদের চ্যাটে মেসেজ করলে AI তাৎক্ষণিক আপনাকে লাইভ প্রাইস, পেমেন্ট নাম্বার ও অর্ডার স্ট্যাটাস জানাবে। আর সরাসরি এডমিনের সাথে কথা বলতে চাইলে যেকোনো সময় \"💬 Talk to Admin\" বাটনে ট্যাপ করুন।",
                fontSize = 12.sp,
                color = Color(0xFFE5E7EB),
                lineHeight = 17.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: আমার অর্ডার কোথায়?
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .shadow(6.dp, RoundedCornerShape(13.dp), spotColor = NeonPurple)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF341463), Color(0xFF210B42))
                            )
                        )
                        .border(
                            1.1.dp,
                            Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)),
                            RoundedCornerShape(13.dp)
                        )
                        .clickable { onQuickQuestionClick("আমার অর্ডার কোথায়?") }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📦 আমার অর্ডার কোথায়?",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Button 2: Talk to Admin
                if (!isHumanHandoffActive) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .shadow(8.dp, RoundedCornerShape(13.dp), spotColor = GoldPrimary)
                            .clip(RoundedCornerShape(13.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFD54F), GoldPrimary, Color(0xFFFF8F00))
                                )
                            )
                            .border(
                                1.1.dp,
                                Color.White.copy(alpha = 0.65f),
                                RoundedCornerShape(13.dp)
                            )
                            .clickable { onTalkToAdminClick() }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💬 Talk to Admin",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF140526),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiTypingIndicatorBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_typing")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot2"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
            color = Color(0xFF151236),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "🤖", fontSize = 14.sp)
                Text(
                    text = "CHAMET AI লিখছে",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(dot1Alpha)
                        .clip(CircleShape)
                        .background(NeonCyan)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(dot2Alpha)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(dot1Alpha)
                        .clip(CircleShape)
                        .background(NeonCyan)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomerChatThreadBubble(
    msg: SupportChatMessage,
    isHumanHandoffActive: Boolean,
    onImageClick: (String) -> Unit,
    onTalkToAdminClick: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val sdf = remember { SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()) }
    val timeStr = remember(msg.timestamp) { sdf.format(Date(msg.timestamp)) }
    val role = msg.senderRole.trim().uppercase()
    val isAdminMsg = role == "ADMIN"
    val isAiMsg = role == "AI"
    var showUserCopyMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (!isAdminMsg && !isAiMsg) {
            // USER SENT BUBBLE (Right Aligned - Long-press to show Copy button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 4.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        ),
                        color = Color(0xFF3730A3),
                        border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.55f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .widthIn(max = 310.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 4.dp,
                                    bottomStart = 18.dp,
                                    bottomEnd = 18.dp
                                )
                            )
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    if (msg.message.isNotBlank()) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showUserCopyMenu = true
                                    }
                                }
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF4338CA), Color(0xFF312E81))
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            if (msg.imageUrl.isNotBlank()) {
                                ChatBubbleImageAttachment(
                                    imageData = msg.imageUrl,
                                    onClick = { onImageClick(msg.imageUrl) }
                                )
                                if (msg.message.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }

                            if (msg.message.isNotBlank()) {
                                Text(
                                    text = msg.message,
                                    fontSize = 13.5.sp,
                                    color = Color.White,
                                    lineHeight = 19.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.align(Alignment.End),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = timeStr,
                                    fontSize = 9.5.sp,
                                    color = Color(0xFFC7D2FE)
                                )
                                Text(
                                    text = if (msg.isReadByAdmin) "✓✓ Seen" else "✓ Sent",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.isReadByAdmin) Color(0xFF34D399) else Color(0xFFC7D2FE)
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = showUserCopyMenu,
                        onDismissRequest = { showUserCopyMenu = false },
                        containerColor = Color(0xFF1E293B)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "কপি করুন (Copy)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            },
                            onClick = {
                                showUserCopyMenu = false
                                copyChatTextToClipboard(context, msg.message)
                            }
                        )
                    }
                }
            }

            // Legacy inline adminReply compatibility if present on a USER message
            if (msg.adminReply.isNotBlank() && !msg.adminReply.startsWith("MODE:") && msg.adminReply != "SHOW_TALK_TO_ADMIN") {
                AdminReplyBubbleItem(
                    replyText = msg.adminReply,
                    imageUrl = "",
                    timeStr = timeStr,
                    onImageClick = onImageClick
                )
            }
        } else if (isAiMsg) {
            val showTalkToAdminCta = !isHumanHandoffActive && (
                msg.adminReply == "SHOW_TALK_TO_ADMIN" ||
                    msg.message.contains("Talk to Admin", ignoreCase = true)
                )
            AiReplyBubbleItem(
                replyText = msg.message,
                timeStr = timeStr,
                isHandoffNotice = msg.adminReply == "MODE:HANDOFF_REQUESTED",
                showTalkToAdminButton = showTalkToAdminCta,
                onTalkToAdminClick = onTalkToAdminClick
            )
        } else {
            AdminReplyBubbleItem(
                replyText = msg.message,
                imageUrl = msg.imageUrl,
                timeStr = timeStr,
                onImageClick = onImageClick
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AiReplyBubbleItem(
    replyText: String,
    timeStr: String,
    isHandoffNotice: Boolean,
    showTalkToAdminButton: Boolean,
    onTalkToAdminClick: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showAiCopyMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd = 18.dp
                ),
                color = if (isHandoffNotice) Color(0xFF281509) else Color(0xFF151F32),
                border = BorderStroke(
                    1.dp,
                    if (isHandoffNotice) WarningYellow.copy(alpha = 0.6f) else Color(0xFF334155)
                ),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            if (replyText.isNotBlank()) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showAiCopyMenu = true
                            }
                        }
                    )
            ) {
                Column(modifier = Modifier.padding(13.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isHandoffNotice) "👨‍💻 Admin Handoff" else "🤖 AI Assistant",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isHandoffNotice) GoldPrimary else Color(0xFF38BDF8)
                        )
                        Surface(
                            onClick = { copyChatTextToClipboard(context, replyText) },
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.8.dp, Color(0xFF475569))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "কপি",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = replyText,
                        fontSize = 13.sp,
                        color = Color(0xFFF1F5F9),
                        lineHeight = 19.sp
                    )

                    if (showTalkToAdminButton) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onTalkToAdminClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("bubble_talk_to_admin_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text(
                                text = "💬 সরাসরি এডমিনের সাথে কথা বলুন",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = timeStr,
                            fontSize = 9.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "• ✓✓ Delivered",
                            fontSize = 9.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = showAiCopyMenu,
                onDismissRequest = { showAiCopyMenu = false },
                containerColor = Color(0xFF1E293B)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "কপি করুন (Copy)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    },
                    onClick = {
                        showAiCopyMenu = false
                        copyChatTextToClipboard(context, replyText)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AdminReplyBubbleItem(
    replyText: String,
    imageUrl: String,
    timeStr: String,
    onImageClick: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showAdminCopyMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd = 18.dp
                ),
                color = Color(0xFF0F2922),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.55f)),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .widthIn(max = 315.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            if (replyText.isNotBlank()) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showAdminCopyMenu = true
                            }
                        }
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "👨‍💻 Official Admin",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF34D399)
                        )
                        if (replyText.isNotBlank()) {
                            Surface(
                                onClick = { copyChatTextToClipboard(context, replyText) },
                                color = Color(0xFF064E3B),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color(0xFF6EE7B7),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "কপি",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    if (imageUrl.isNotBlank()) {
                        ChatBubbleImageAttachment(
                            imageData = imageUrl,
                            onClick = { onImageClick(imageUrl) }
                        )
                        if (replyText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (replyText.isNotBlank()) {
                        Text(
                            text = replyText,
                            fontSize = 13.5.sp,
                            color = Color.White,
                            lineHeight = 19.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = timeStr,
                            fontSize = 9.5.sp,
                            color = Color(0xFFA7F3D0)
                        )
                        Text(
                            text = "• ✓✓ Official",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = showAdminCopyMenu,
                onDismissRequest = { showAdminCopyMenu = false },
                containerColor = Color(0xFF1E293B)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "কপি করুন (Copy)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    },
                    onClick = {
                        showAdminCopyMenu = false
                        copyChatTextToClipboard(context, replyText)
                    }
                )
            }
        }
    }
}

@Composable
fun ChatBubbleImageAttachment(
    imageData: String,
    onClick: () -> Unit
) {
    val decodedBitmap = remember(imageData) {
        if (imageData.startsWith("data:image")) {
            ImageBase64Util.base64ToBitmap(imageData)
        } else {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF090214))
            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (decodedBitmap != null) {
            AsyncImage(
                model = decodedBitmap,
                contentDescription = "Chat Image Attachment",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = imageData,
                contentDescription = "Chat Image Attachment",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Surface(
            color = Color.Black.copy(alpha = 0.55f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
        ) {
            Text(
                text = "🔍 বড় করে দেখুন",
                fontSize = 9.5.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun FullScreenChatImageDialog(
    imageData: String,
    onDismiss: () -> Unit
) {
    val decodedBitmap = remember(imageData) {
        if (imageData.startsWith("data:image")) {
            ImageBase64Util.base64ToBitmap(imageData)
        } else {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable { onDismiss() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (decodedBitmap != null) {
            AsyncImage(
                model = decodedBitmap,
                contentDescription = "Full Screen Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            )
        } else {
            AsyncImage(
                model = imageData,
                contentDescription = "Full Screen Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp)
                .size(42.dp)
                .background(Color(0xFF1F0A3A), CircleShape)
                .border(1.dp, GoldPrimary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}

object ImageBase64Util {
    fun uriToCompressedBase64(context: android.content.Context, uri: Uri, maxDim: Int = 720): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return null

            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = if (width > height && width > maxDim) {
                maxDim.toFloat() / width.toFloat()
            } else if (height >= width && height > maxDim) {
                maxDim.toFloat() / height.toFloat()
            } else {
                1.0f
            }

            val scaledBitmap = if (scale < 1.0f) {
                android.graphics.Bitmap.createScaledBitmap(
                    originalBitmap,
                    (width * scale).toInt().coerceAtLeast(1),
                    (height * scale).toInt().coerceAtLeast(1),
                    true
                )
            } else {
                originalBitmap
            }

            val outputStream = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 72, outputStream)
            val bytes = outputStream.toByteArray()
            val encoded = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
            "data:image/jpeg;base64,$encoded"
        } catch (_: Exception) {
            null
        }
    }

    fun base64ToBitmap(dataUriOrBase64: String): android.graphics.Bitmap? {
        return try {
            val rawBase64 = if (dataUriOrBase64.contains(",")) {
                dataUriOrBase64.substringAfter(",")
            } else {
                dataUriOrBase64
            }
            val bytes = android.util.Base64.decode(rawBase64, android.util.Base64.DEFAULT)
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    }
}
