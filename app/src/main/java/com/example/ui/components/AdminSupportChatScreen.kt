package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CustomerProfile
import com.example.data.model.OrderRecord
import com.example.data.model.SupportChatMessage
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.WhatsAppGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CustomerConversationSummary(
    val conversationId: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val lastMessageText: String,
    val lastMessageSenderRole: String,
    val lastTimestamp: Long,
    val unreadCount: Int,
    val chatMode: String, // AI_AUTO, HANDOFF_REQUESTED, ADMIN_TAKEOVER
    val messages: List<SupportChatMessage>
)

data class AdminChatOpenRequest(
    val conversationId: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String
)

@Composable
fun AdminSupportMessagesTab(
    messages: List<SupportChatMessage>,
    orders: List<OrderRecord> = emptyList(),
    customers: List<CustomerProfile> = emptyList(),
    isAiMasterEnabled: Boolean = true,
    supportHandoffMap: Map<String, String> = emptyMap(),
    adminCustomAiInstructions: String = "",
    onSendAdminAiCommand: (String) -> Unit = {},
    onClearAdminAiInstructions: () -> Unit = {},
    targetConversationRequest: AdminChatOpenRequest? = null,
    onTargetConversationConsumed: () -> Unit = {},
    onToggleMasterAi: (Boolean) -> Unit = {},
    onSetConversationMode: (
        conversationId: String,
        customerName: String,
        customerContact: String,
        customerEmail: String,
        mode: String
    ) -> Unit = { _, _, _, _, _ -> },
    onSendAdminMessage: (
        conversationId: String,
        customerName: String,
        customerContact: String,
        customerEmail: String,
        messageText: String,
        imageUrl: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    onMarkConversationRead: (String) -> Unit = {},
    onReply: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onDeleteConversation: (String) -> Unit = {},
    onContactWhatsApp: (String) -> Unit,
    onCopyText: (String, String) -> Unit,
    onConversationOpenChanged: (Boolean) -> Unit = {}
) {
    var selectedConversationId by remember { mutableStateOf<String?>(null) }
    var directOpenRequest by remember { mutableStateOf<AdminChatOpenRequest?>(null) }
    var adminAiCommandInput by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("ALL") } // ALL, UNREAD, HANDOFF, ADMIN, AI
    var fullScreenImageData by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(targetConversationRequest) {
        if (targetConversationRequest != null) {
            directOpenRequest = targetConversationRequest
            selectedConversationId = targetConversationRequest.conversationId.trim().lowercase()
            onTargetConversationConsumed()
        }
    }

    LaunchedEffect(selectedConversationId) {
        onConversationOpenChanged(selectedConversationId != null)
    }

    // Group all messages by normalized email / conversation key
    val conversations = remember(messages, customers, orders, supportHandoffMap, isAiMasterEnabled) {
        val grouped = messages.groupBy { msg ->
            val email = msg.senderEmail.trim().lowercase()
            val conv = msg.conversationId.trim().lowercase()
                .removePrefix("email_")
                .removePrefix("user_")
            when {
                email.isNotEmpty() -> email
                conv.isNotEmpty() -> conv
                else -> msg.senderContact.ifBlank { msg.senderName.ifBlank { msg.id } }.trim().lowercase()
            }
        }
        grouped.map { (convId, convMsgs) ->
            val sorted = convMsgs.sortedBy { it.timestamp }
            val latestUserMsg = sorted.lastOrNull { it.senderRole.equals("USER", ignoreCase = true) } ?: sorted.last()
            val lastMsg = sorted.last()

            // Unread count: Customer USER messages not yet read by Admin, plus unread handoff request alerts
            val unread = sorted.count {
                val role = it.senderRole.uppercase()
                (role == "USER" || it.adminReply == "MODE:HANDOFF_REQUESTED") && !it.isReadByAdmin
            }

            val rawEmail = sorted.lastOrNull { it.senderEmail.isNotBlank() }?.senderEmail?.trim() ?: ""
            val rawPhone = sorted.lastOrNull { it.senderContact.isNotBlank() }?.senderContact?.trim() ?: ""
            val rawName = latestUserMsg.senderName.trim()

            val matchedCustomer = customers.find { c ->
                (rawEmail.isNotEmpty() && c.email.equals(rawEmail, ignoreCase = true)) ||
                    (rawPhone.isNotEmpty() && (c.phone == rawPhone || c.accountId == rawPhone)) ||
                    c.accountId.equals(convId, ignoreCase = true)
            }
            val matchedOrder = orders.firstOrNull { o ->
                (rawEmail.isNotEmpty() && o.customerEmail.equals(rawEmail, ignoreCase = true)) ||
                    (rawPhone.isNotEmpty() && o.customerPhone == rawPhone)
            }

            val resolvedEmail = rawEmail
                .ifBlank { matchedCustomer?.email ?: "" }
                .ifBlank { matchedOrder?.customerEmail ?: "" }
                .ifBlank { if (convId.contains("@")) convId else "" }

            val resolvedPhone = rawPhone
                .ifBlank { matchedCustomer?.phone ?: "" }
                .ifBlank { matchedOrder?.customerPhone ?: "" }

            val resolvedName = rawName
                .takeIf { it.isNotBlank() && !it.equals("গ্রাহক", ignoreCase = true) && !it.equals("Customer", ignoreCase = true) }
                ?: matchedCustomer?.displayName?.takeIf { it.isNotBlank() }
                ?: matchedOrder?.customerName?.takeIf { it.isNotBlank() }
                ?: resolvedEmail.substringBefore("@").takeIf { it.isNotBlank() }
                ?: resolvedPhone.ifBlank { "গ্রাহক (${convId.takeLast(6)})" }

            val preview = when {
                lastMsg.senderRole.equals("ADMIN", ignoreCase = true) ->
                    "👨‍💻 আপনি: ${lastMsg.message.ifBlank { "📷 [ছবি পাঠিয়েছেন]" }}"
                lastMsg.senderRole.equals("AI", ignoreCase = true) ->
                    "🤖 AI: ${lastMsg.message.replace("\n", " ").take(65)}"
                lastMsg.adminReply.isNotBlank() && !lastMsg.adminReply.startsWith("MODE:") && lastMsg.adminReply != "SHOW_TALK_TO_ADMIN" ->
                    "👨‍💻 আপনি: ${lastMsg.adminReply}"
                lastMsg.message.isNotBlank() ->
                    "👤 ${lastMsg.message}"
                lastMsg.imageUrl.isNotBlank() ->
                    "📷 [স্ক্রিনশট পাঠিয়েছেন]"
                else -> "মেসেজ"
            }

            // Determine conversation mode (AI_AUTO, HANDOFF_REQUESTED, ADMIN_TAKEOVER)
            val mapMode = supportHandoffMap[convId.lowercase()]?.uppercase()
            val latestMarker = sorted.lastOrNull { it.adminReply.startsWith("MODE:") }
                ?.adminReply
                ?.removePrefix("MODE:")
                ?.trim()
                ?.uppercase()
            val resolvedMode = when {
                !isAiMasterEnabled -> "ADMIN_TAKEOVER"
                mapMode == "HANDOFF_REQUESTED" || mapMode == "ADMIN_TAKEOVER" -> mapMode
                latestMarker == "HANDOFF_REQUESTED" || latestMarker == "ADMIN_TAKEOVER" -> latestMarker
                else -> "AI_AUTO"
            }

            CustomerConversationSummary(
                conversationId = convId,
                customerName = resolvedName,
                customerPhone = resolvedPhone,
                customerEmail = resolvedEmail,
                lastMessageText = preview,
                lastMessageSenderRole = lastMsg.senderRole,
                lastTimestamp = lastMsg.timestamp,
                unreadCount = unread,
                chatMode = resolvedMode,
                messages = sorted
            )
        }.sortedWith(
            compareByDescending<CustomerConversationSummary> { it.chatMode == "HANDOFF_REQUESTED" }
                .thenByDescending { it.unreadCount > 0 }
                .thenByDescending { it.lastTimestamp }
        )
    }

    val filteredConversations = remember(conversations, searchQuery, filterMode) {
        conversations.filter { conv ->
            val matchesMode = when (filterMode) {
                "UNREAD" -> conv.unreadCount > 0
                "HANDOFF" -> conv.chatMode == "HANDOFF_REQUESTED"
                "ADMIN" -> conv.chatMode == "ADMIN_TAKEOVER"
                "AI" -> conv.chatMode == "AI_AUTO"
                else -> true
            }
            val q = searchQuery.trim()
            val matchesSearch = q.isEmpty() ||
                conv.customerName.contains(q, ignoreCase = true) ||
                conv.customerPhone.contains(q, ignoreCase = true) ||
                conv.customerEmail.contains(q, ignoreCase = true) ||
                conv.lastMessageText.contains(q, ignoreCase = true)
            matchesMode && matchesSearch
        }
    }

    val activeConversation = remember(conversations, selectedConversationId, directOpenRequest) {
        val targetId = selectedConversationId?.trim()?.lowercase()
        if (targetId.isNullOrBlank()) {
            null
        } else {
            conversations.find { conv ->
                conv.conversationId.equals(targetId, ignoreCase = true) ||
                    (conv.customerEmail.isNotBlank() && conv.customerEmail.equals(targetId, ignoreCase = true)) ||
                    (conv.customerPhone.isNotBlank() && conv.customerPhone.equals(targetId, ignoreCase = true)) ||
                    (directOpenRequest?.customerEmail?.isNotBlank() == true &&
                        conv.customerEmail.equals(directOpenRequest?.customerEmail, ignoreCase = true)) ||
                    (directOpenRequest?.customerPhone?.isNotBlank() == true &&
                        conv.customerPhone == directOpenRequest?.customerPhone)
            } ?: directOpenRequest?.let { req ->
                CustomerConversationSummary(
                    conversationId = req.customerEmail.ifBlank { req.conversationId }.trim().lowercase(),
                    customerName = req.customerName.ifBlank { req.customerEmail.ifBlank { req.conversationId } },
                    customerPhone = req.customerPhone,
                    customerEmail = req.customerEmail,
                    lastMessageText = "সরাসরি কাস্টমারকে মেসেজ পাঠান",
                    lastMessageSenderRole = "ADMIN",
                    lastTimestamp = System.currentTimeMillis(),
                    unreadCount = 0,
                    chatMode = "ADMIN_TAKEOVER",
                    messages = emptyList()
                )
            }
        }
    }

    if (activeConversation != null) {
        BackHandler {
            selectedConversationId = null
            directOpenRequest = null
        }
        AdminCustomerChatThreadView(
            conversation = activeConversation,
            onBack = {
                selectedConversationId = null
                directOpenRequest = null
            },
            onSendAdminMessage = { text, imgBase64 ->
                onSendAdminMessage(
                    activeConversation.conversationId,
                    activeConversation.customerName,
                    activeConversation.customerPhone,
                    activeConversation.customerEmail,
                    text,
                    imgBase64
                )
            },
            onSetConversationMode = { newMode ->
                onSetConversationMode(
                    activeConversation.conversationId,
                    activeConversation.customerName,
                    activeConversation.customerPhone,
                    activeConversation.customerEmail,
                    newMode
                )
            },
            onMarkRead = { onMarkConversationRead(activeConversation.conversationId) },
            onDeleteMessage = onDelete,
            onDeleteEntireConversation = {
                onDeleteConversation(activeConversation.conversationId)
                selectedConversationId = null
            },
            onContactWhatsApp = onContactWhatsApp,
            onCopyText = onCopyText,
            onOpenImage = { fullScreenImageData = it }
        )
    } else {
        // INBOX CONVERSATION LIST VIEW
        val totalUnreadChats = conversations.count { it.unreadCount > 0 }
        val handoffCount = conversations.count { it.chatMode == "HANDOFF_REQUESTED" }
        val adminModeCount = conversations.count { it.chatMode == "ADMIN_TAKEOVER" }
        val aiModeCount = conversations.count { it.chatMode == "AI_AUTO" }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                // Header & Master AI Control Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF1B0933),
                    border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "💬 লাইভ সাপোর্ট ও AI Control Center",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )
                                Text(
                                    text = "মোট কথোপকথন: ${conversations.size} টি • অপঠিত: $totalUnreadChats টি • Admin Call: $handoffCount টি",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFD1D5DB)
                                )
                            }
                        }

                        // Master AI Auto-Reply Switch Row
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF110422),
                            border = BorderStroke(
                                1.dp,
                                if (isAiMasterEnabled) NeonCyan.copy(alpha = 0.5f) else WarningYellow.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (isAiMasterEnabled) "🤖" else "⏸️",
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isAiMasterEnabled) {
                                                "AI Auto-Reply সিস্টেম চালু আছে"
                                            } else {
                                                "AI Auto-Reply সাময়িকভাবে বন্ধ আছে"
                                            },
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAiMasterEnabled) NeonCyan else WarningYellow
                                        )
                                        Text(
                                            text = "কাস্টমার মেসেজ করলে AI প্রাইস, অর্ডার স্ট্যাটাস ও তথ্য জানাবে",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                }
                                Switch(
                                    checked = isAiMasterEnabled,
                                    onCheckedChange = onToggleMasterAi,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF06151F),
                                        checkedTrackColor = NeonCyan,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFF374151)
                                    )
                                )
                            }
                        }

                        // Admin Boss AI Command & Training Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF14062B),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "👑 এডমিন AI আদেশ ও নির্দেশনা (AI আপনার কথা মেনে চলবে)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary
                                    )
                                    if (adminCustomAiInstructions.isNotBlank()) {
                                        Surface(
                                            onClick = onClearAdminAiInstructions,
                                            shape = RoundedCornerShape(8.dp),
                                            color = DangerRed.copy(alpha = 0.2f),
                                            border = BorderStroke(0.8.dp, DangerRed)
                                        ) {
                                            Text(
                                                text = "🧹 ক্লিয়ার",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFCA5A5),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "আপনি আমাদের AI-কে যে নিয়ম, প্রাইস বা তথ্য দেবেন সে সেটাই কাস্টমারদের বলবে এবং পালন করবে:",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                if (adminCustomAiInstructions.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF0B0218),
                                        border = BorderStroke(0.8.dp, NeonCyan.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "🧠 বর্তমান মেমোরি:\n$adminCustomAiInstructions",
                                            fontSize = 11.sp,
                                            color = NeonCyan,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = adminAiCommandInput,
                                        onValueChange = { adminAiCommandInput = it },
                                        placeholder = {
                                            Text(
                                                "যেমন: কাস্টমারদের বলবে আজকে টিকটক কয়েন ২ মিনিটে ডেলিভারি হবে...",
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        maxLines = 2,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldPrimary,
                                            unfocusedBorderColor = Color(0xFF4C1D95),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedContainerColor = Color(0xFF0B0218),
                                            unfocusedContainerColor = Color(0xFF0B0218)
                                        )
                                    )
                                    Button(
                                        onClick = {
                                            if (adminAiCommandInput.isNotBlank()) {
                                                onSendAdminAiCommand(adminAiCommandInput.trim())
                                                adminAiCommandInput = ""
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                                    ) {
                                        Text(
                                            text = "🤖 সেভ",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF140526)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Search & Filter Bar
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("নাম, মোবাইল, ইমেইল বা মেসেজ খুঁজুন...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF42206B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF140726),
                            unfocusedContainerColor = Color(0xFF140726)
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val chips = listOf(
                            "ALL" to "সব (${conversations.size})",
                            "UNREAD" to "অপঠিত ($totalUnreadChats)",
                            "HANDOFF" to "🚨 Admin Call ($handoffCount)",
                            "ADMIN" to "👨‍💻 Admin Mode ($adminModeCount)",
                            "AI" to "🤖 AI Mode ($aiModeCount)"
                        )
                        chips.forEach { (key, label) ->
                            FilterChip(
                                selected = filterMode == key,
                                onClick = { filterMode = key },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Color(0xFF140526),
                                    containerColor = Color(0xFF1B0933),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            if (filteredConversations.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF160729),
                        border = BorderStroke(1.dp, Color(0xFF331559)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📭", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "কোনো কাস্টমার চ্যাট পাওয়া যায়নি",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                }
            } else {
                items(filteredConversations, key = { it.conversationId }) { conv ->
                    CustomerConversationRow(
                        conv = conv,
                        onClick = {
                            selectedConversationId = conv.conversationId
                            onMarkConversationRead(conv.conversationId)
                        },
                        onToggleTakeover = {
                            val nextMode = if (conv.chatMode == "AI_AUTO") "ADMIN_TAKEOVER" else "AI_AUTO"
                            onSetConversationMode(
                                conv.conversationId,
                                conv.customerName,
                                conv.customerPhone,
                                conv.customerEmail,
                                nextMode
                            )
                        }
                    )
                }
            }
        }
    }

    if (fullScreenImageData != null) {
        FullScreenChatImageDialog(
            imageData = fullScreenImageData!!,
            onDismiss = { fullScreenImageData = null }
        )
    }
}

@Composable
private fun CustomerConversationRow(
    conv: CustomerConversationSummary,
    onClick: () -> Unit,
    onToggleTakeover: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()) }
    val timeStr = remember(conv.lastTimestamp) { sdf.format(Date(conv.lastTimestamp)) }
    val isHandoffRequested = conv.chatMode == "HANDOFF_REQUESTED"
    val isAdminTakeover = conv.chatMode == "ADMIN_TAKEOVER"

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = when {
            isHandoffRequested -> Color(0xFF2B1218)
            conv.unreadCount > 0 -> Color(0xFF240D47)
            else -> Color(0xFF16072B)
        },
        border = BorderStroke(
            width = if (isHandoffRequested || conv.unreadCount > 0) 1.5.dp else 1.dp,
            color = when {
                isHandoffRequested -> DangerRed
                conv.unreadCount > 0 -> GoldPrimary
                else -> Color(0xFF34185A)
            }
        ),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(13.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                when {
                                    isHandoffRequested -> listOf(DangerRed, GoldPrimary)
                                    isAdminTakeover -> listOf(Color(0xFF00A884), GoldPrimary)
                                    else -> listOf(NeonPurple, NeonCyan)
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conv.customerName.take(1).uppercase(),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = conv.customerName,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = timeStr,
                            fontSize = 10.sp,
                            color = if (conv.unreadCount > 0) GoldPrimary else Color(0xFF9CA3AF),
                            fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    val contactSub = listOf(conv.customerPhone, conv.customerEmail)
                        .filter { it.isNotBlank() }
                        .joinToString(" • ")

                    if (contactSub.isNotBlank()) {
                        Text(
                            text = contactSub,
                            fontSize = 11.sp,
                            color = GoldLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = conv.lastMessageText,
                            fontSize = 12.sp,
                            color = if (conv.unreadCount > 0) Color.White else Color(0xFFD1D5DB),
                            fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (conv.unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF00A884)
                            ) {
                                Text(
                                    text = "${conv.unreadCount}",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mode Status Badge + Quick Takeover/Release Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when {
                        isHandoffRequested -> DangerRed.copy(alpha = 0.2f)
                        isAdminTakeover -> SuccessGreen.copy(alpha = 0.18f)
                        else -> NeonCyan.copy(alpha = 0.16f)
                    },
                    border = BorderStroke(
                        0.8.dp,
                        when {
                            isHandoffRequested -> DangerRed
                            isAdminTakeover -> SuccessGreen
                            else -> NeonCyan
                        }
                    )
                ) {
                    Text(
                        text = when {
                            isHandoffRequested -> "🚨 কাস্টমার Admin ডেকেছেন (AI Paused)"
                            isAdminTakeover -> "👨‍💻 Admin Takeover (Human Mode)"
                            else -> "🤖 AI Auto-Reply সক্রিয়"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isHandoffRequested -> Color(0xFFFCA5A5)
                            isAdminTakeover -> SuccessGreen
                            else -> NeonCyan
                        },
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    onClick = onToggleTakeover,
                    shape = RoundedCornerShape(50),
                    color = if (conv.chatMode == "AI_AUTO") Color(0xFF2B124C) else Color(0xFF102A3B),
                    border = BorderStroke(
                        1.dp,
                        if (conv.chatMode == "AI_AUTO") GoldPrimary else NeonCyan
                    )
                ) {
                    Text(
                        text = if (conv.chatMode == "AI_AUTO") {
                            "👨‍💻 Takeover Chat"
                        } else {
                            "🤖 Release to AI"
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (conv.chatMode == "AI_AUTO") GoldPrimary else NeonCyan,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminCustomerChatThreadView(
    conversation: CustomerConversationSummary,
    onBack: () -> Unit,
    onSendAdminMessage: (String, String) -> Unit,
    onSetConversationMode: (String) -> Unit,
    onMarkRead: () -> Unit,
    onDeleteMessage: (String) -> Unit,
    onDeleteEntireConversation: () -> Unit,
    onContactWhatsApp: (String) -> Unit,
    onCopyText: (String, String) -> Unit,
    onOpenImage: (String) -> Unit
) {
    val context = LocalContext.current
    var replyText by remember { mutableStateOf("") }
    var selectedImageBase64 by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val isAiActive = conversation.chatMode == "AI_AUTO"
    val isHandoffRequested = conversation.chatMode == "HANDOFF_REQUESTED"

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val encoded = ImageBase64Util.uriToCompressedBase64(context, uri)
            if (encoded != null) {
                selectedImageBase64 = encoded
            }
        }
    }

    LaunchedEffect(conversation.messages.size) {
        onMarkRead()
        if (conversation.messages.isNotEmpty()) {
            listState.animateScrollToItem(conversation.messages.size - 1)
        }
    }

    val quickSnippets = listOf(
        "✅ আপনার অর্ডারটি সম্পন্ন করা হয়েছে, চেক করুন!",
        "⏳ আপনার অর্ডারটি প্রসেসিংয়ে আছে, ৫-১০ মিনিট অপেক্ষা করুন।",
        "📸 অনুগ্রহ করে আপনার পেমেন্টের স্ক্রিনশট বা TrxID পাঠান।",
        "🙏 ধন্যবাদ! যেকোনো প্রয়োজনে আমাদের মেসেজ করুন।"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0217))
            .imePadding()
    ) {
        // 1. TOP CUSTOMER THREAD HEADER
        Surface(
            color = Color(0xFF1D0B36),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Inbox",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = conversation.customerName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val subInfo = listOf(conversation.customerPhone, conversation.customerEmail)
                                .filter { it.isNotBlank() }
                                .joinToString(" • ")
                            if (subInfo.isNotBlank()) {
                                Text(
                                    text = subInfo,
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val copyValue = conversation.customerPhone.ifBlank { conversation.customerEmail }
                        if (copyValue.isNotBlank()) {
                            IconButton(
                                onClick = { onCopyText("কাস্টমার তথ্য", copyValue) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF2D124D), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Contact",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (conversation.customerPhone.isNotBlank()) {
                            Surface(
                                onClick = { onContactWhatsApp(conversation.customerPhone) },
                                shape = RoundedCornerShape(50),
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                                modifier = Modifier.shadow(6.dp, RoundedCornerShape(50), spotColor = OfficialWhatsAppLightGreen)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(Color(0xFF1FAF54), OfficialWhatsAppLightGreen, OfficialWhatsAppDarkGreen)
                                            )
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        WhatsAppVectorGlyph(
                                            modifier = Modifier.size(11.dp),
                                            tint = OfficialWhatsAppLightGreen
                                        )
                                    }
                                    Text(
                                        text = "WA",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDeleteEntireConversation,
                            modifier = Modifier
                                .size(36.dp)
                                .background(DangerRed.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Thread",
                                tint = DangerRed,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                // AI / Human Admin Takeover & Release Control Strip
                Surface(
                    color = when {
                        isHandoffRequested -> Color(0xFF321119)
                        isAiActive -> Color(0xFF101935)
                        else -> Color(0xFF0D2821)
                    },
                    border = BorderStroke(
                        0.8.dp,
                        when {
                            isHandoffRequested -> DangerRed.copy(alpha = 0.6f)
                            isAiActive -> NeonCyan.copy(alpha = 0.4f)
                            else -> SuccessGreen.copy(alpha = 0.4f)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    isHandoffRequested -> "🚨 কাস্টমার Admin-এর সাথে কথা বলতে চেয়েছেন"
                                    isAiActive -> "🤖 এই চ্যাটে AI Auto-Reply চালু আছে"
                                    else -> "👨‍💻 আপনি চ্যাটটি Takeover করেছেন (AI বন্ধ)"
                                },
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isHandoffRequested -> Color(0xFFFCA5A5)
                                    isAiActive -> NeonCyan
                                    else -> SuccessGreen
                                }
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (isAiActive || isHandoffRequested) {
                                Surface(
                                    onClick = { onSetConversationMode("ADMIN_TAKEOVER") },
                                    shape = RoundedCornerShape(50),
                                    color = Color.Transparent,
                                    border = BorderStroke(
                                        1.dp,
                                        Brush.linearGradient(listOf(Color(0xFFFFF8B8), GoldPrimary))
                                    ),
                                    modifier = Modifier
                                        .shadow(8.dp, RoundedCornerShape(50), spotColor = GoldPrimary)
                                        .testTag("admin_takeover_chat_button")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFFFFE55C), GoldPrimary, Color(0xFFF59E0B))
                                                )
                                            )
                                            .padding(horizontal = 11.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = Color(0xFF140526),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Takeover Chat",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF140526)
                                        )
                                    }
                                }
                            }
                            if (!isAiActive) {
                                Surface(
                                    onClick = { onSetConversationMode("AI_AUTO") },
                                    shape = RoundedCornerShape(50),
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.8f)),
                                    modifier = Modifier
                                        .shadow(6.dp, RoundedCornerShape(50), spotColor = NeonCyan)
                                        .testTag("admin_release_to_ai_button")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF0F2942), Color(0xFF164E63))
                                                )
                                            )
                                            .padding(horizontal = 11.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Release to AI",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
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

        // 2. MESSAGES LIST
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(conversation.messages, key = { it.id }) { msg ->
                AdminChatBubbleItem(
                    msg = msg,
                    onImageClick = onOpenImage,
                    onDeleteMsg = { onDeleteMessage(msg.id) }
                )
            }
        }

        // 3. QUICK REPLY SNIPPETS BAR (Professional 3D Pill Buttons)
        Surface(
            color = Color(0xFF0E051D),
            border = BorderStroke(0.6.dp, Color(0xFF2E1454))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                quickSnippets.forEach { snippet ->
                    Surface(
                        onClick = { replyText = snippet },
                        shape = RoundedCornerShape(50),
                        color = Color.Transparent,
                        border = BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    GoldPrimary.copy(alpha = 0.65f),
                                    NeonPurple.copy(alpha = 0.55f)
                                )
                            )
                        ),
                        modifier = Modifier.shadow(6.dp, RoundedCornerShape(50), spotColor = GoldPrimary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF28104E),
                                            Color(0xFF1A0936)
                                        )
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = snippet,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 4. SELECTED IMAGE ATTACHMENT PREVIEW
        AnimatedVisibility(
            visible = selectedImageBase64.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = Color(0xFF1F0D3D),
                border = BorderStroke(1.dp, GoldPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val bmp = remember(selectedImageBase64) {
                        ImageBase64Util.base64ToBitmap(selectedImageBase64)
                    }
                    if (bmp != null) {
                        AsyncImage(
                            model = bmp,
                            contentDescription = "Selected Reply Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "📷 কাস্টমারকে পাঠানোর জন্য ছবি যুক্ত হয়েছে",
                        fontSize = 11.5.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { selectedImageBase64 = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = DangerRed)
                    }
                }
            }
        }

        // 5. ADMIN INPUT BAR (Professional Glossy Studio Dock)
        Surface(
            color = Color(0xFF120624),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        GoldPrimary.copy(alpha = 0.4f),
                        Color(0xFF4C1D95),
                        NeonCyan.copy(alpha = 0.35f)
                    )
                )
            ),
            shadowElevation = 14.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(listOf(Color(0xFFFFF8B8), GoldPrimary))
                    ),
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(8.dp, CircleShape, spotColor = GoldPrimary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF3B156B), Color(0xFF1F0A3D))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Attach Image",
                            tint = GoldPrimary,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = {
                        Text(
                            text = "কাস্টমারকে সরাসরি উত্তর লিখুন (AI অটো-বন্ধ হবে)...",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF4C1D95),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0A0316),
                        unfocusedContainerColor = Color(0xFF0A0316)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                val canSend = replyText.trim().isNotEmpty() || selectedImageBase64.isNotEmpty()

                Surface(
                    onClick = {
                        if (!canSend) return@Surface
                        onSendAdminMessage(replyText.trim(), selectedImageBase64)
                        replyText = ""
                        selectedImageBase64 = ""
                    },
                    enabled = canSend,
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.2.dp,
                        if (canSend) {
                            Brush.linearGradient(listOf(Color(0xFFA7F3D0), Color(0xFF10B981)))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFF4B5563), Color(0xFF374151)))
                        }
                    ),
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(
                            elevation = if (canSend) 10.dp else 2.dp,
                            shape = CircleShape,
                            spotColor = if (canSend) OfficialWhatsAppLightGreen else Color.Transparent
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (canSend) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF22C55E),
                                            Color(0xFF10B981),
                                            Color(0xFF059669)
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF27272A), Color(0xFF18181B))
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Admin Reply",
                            tint = if (canSend) Color.White else Color(0xFF71717A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminChatBubbleItem(
    msg: SupportChatMessage,
    onImageClick: (String) -> Unit,
    onDeleteMsg: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()) }
    val timeStr = remember(msg.timestamp) { sdf.format(Date(msg.timestamp)) }
    val role = msg.senderRole.trim().uppercase()
    val isAdminMsg = role == "ADMIN"
    val isAiMsg = role == "AI"

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (!isAdminMsg && !isAiMsg) {
            // Customer Message (Left Aligned in Admin View)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    ),
                    color = Color(0xFF231042),
                    border = BorderStroke(1.dp, Color(0xFF4D2485)),
                    modifier = Modifier.widthIn(max = 290.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "👤 ${msg.senderName.ifBlank { "গ্রাহক" }}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))

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
                        Text(
                            text = timeStr,
                            fontSize = 9.5.sp,
                            color = Color(0xFF9CA3AF),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteMsg,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Legacy inline reply if present
            if (msg.adminReply.isNotBlank() && !msg.adminReply.startsWith("MODE:") && msg.adminReply != "SHOW_TALK_TO_ADMIN") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 4.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        ),
                        color = Color(0xFF0A3D31),
                        border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.widthIn(max = 290.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.adminReply,
                                fontSize = 13.5.sp,
                                color = Color.White,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "আপনি • $timeStr",
                                fontSize = 9.5.sp,
                                color = Color(0xFFA7F3D0),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        } else if (isAiMsg) {
            // AI Auto-Reply Message (Right Aligned with Cyan/Purple AI Badge in Admin View)
            val isHandoffAlert = msg.adminReply == "MODE:HANDOFF_REQUESTED"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(
                    onClick = onDeleteMsg,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(15.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 4.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    ),
                    color = if (isHandoffAlert) Color(0xFF38131A) else Color(0xFF14183B),
                    border = BorderStroke(
                        1.dp,
                        if (isHandoffAlert) DangerRed else NeonCyan.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.widthIn(max = 295.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isHandoffAlert) "🚨 Admin Handoff Request" else "🤖 AI Auto-Reply",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isHandoffAlert) Color(0xFFFCA5A5) else NeonCyan
                            )
                            Text(
                                text = if (msg.isReadByUser) "✓✓ Seen" else "✓ Sent",
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = msg.message,
                            fontSize = 12.5.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AI • $timeStr",
                            fontSize = 9.5.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        } else {
            // Admin Sent Message (Right Aligned in Admin View)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(
                    onClick = onDeleteMsg,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(15.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 4.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    ),
                    color = Color(0xFF0A3D31),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.widthIn(max = 290.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
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
                        Text(
                            text = "👨‍💻 আপনি (Admin) • $timeStr • ${if (msg.isReadByUser) "✓✓ Seen" else "✓ Sent"}",
                            fontSize = 9.5.sp,
                            color = Color(0xFFA7F3D0),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}
