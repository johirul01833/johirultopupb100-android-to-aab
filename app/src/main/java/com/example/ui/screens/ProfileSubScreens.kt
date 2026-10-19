package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdminConfig
import com.example.data.model.OrderNotification
import com.example.data.model.OrderRecord
import com.example.data.model.SavedServiceAccount
import com.example.data.model.ServiceCategory
import com.example.data.model.TopupPackage
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================================================
// CONSISTENT PREMIUM BUTTON SYSTEM (Normal, Pressed, Disabled, Loading states)
// ============================================================================

@Composable
fun ProfilePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    gradientColors: List<Color> = listOf(Color(0xFFFFE082), Color(0xFFFFB300), Color(0xFFF57C00)),
    contentColor: Color = Color(0xFF120724)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isLoading) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "primary_btn_scale"
    )
    val shape = RoundedCornerShape(14.dp)
    val spotGlowColor = gradientColors.firstOrNull() ?: GoldPrimary

    Box(
        modifier = modifier
            .scale(scale)
            .height(50.dp)
            .then(
                if (enabled) {
                    Modifier.shadow(
                        elevation = 10.dp,
                        shape = shape,
                        ambientColor = spotGlowColor.copy(alpha = 0.55f),
                        spotColor = spotGlowColor.copy(alpha = 0.85f)
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(gradientColors)
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
                }
            )
            .border(
                width = 1.3.dp,
                brush = if (enabled) {
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.7f),
                            spotGlowColor.copy(alpha = 0.4f)
                        )
                    )
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF334155)))
                },
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = contentColor),
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // 3D Glass Top Inner Sheen Highlight
        if (enabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(22.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.32f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = contentColor,
                    strokeWidth = 2.2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (enabled) contentColor else Color(0xFF94A3B8),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (enabled) contentColor else Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    borderColor: Color = GoldPrimary.copy(alpha = 0.8f),
    contentColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isLoading) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "secondary_btn_scale"
    )
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .height(50.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = shape,
                ambientColor = borderColor.copy(alpha = 0.35f),
                spotColor = borderColor.copy(alpha = 0.6f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    if (isPressed) {
                        listOf(Color(0xFF2D1657), Color(0xFF180A33))
                    } else {
                        listOf(Color(0xFF221042), Color(0xFF120726))
                    }
                )
            )
            .border(
                width = 1.3.dp,
                brush = if (enabled) {
                    Brush.linearGradient(
                        listOf(
                            borderColor,
                            NeonMagenta.copy(alpha = 0.6f),
                            borderColor.copy(alpha = 0.7f)
                        )
                    )
                } else {
                    Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF334155)))
                },
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = borderColor),
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // 3D Glass Top Inner Sheen
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(20.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (enabled) borderColor else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (enabled) contentColor else Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "danger_btn_scale"
    )
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .height(50.dp)
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color(0xFFEF4444).copy(alpha = 0.35f),
                spotColor = Color(0xFFEF4444).copy(alpha = 0.6f)
            )
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF3B0A1E), Color(0xFF240612))
                )
            )
            .border(
                width = 1.3.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFEF4444), Color(0xFFF43F5E).copy(alpha = 0.7f))
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color(0xFFEF4444)),
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFF87171),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFCA5A5)
            )
        }
    }
}

@Composable
fun ProfileEmptyStateCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
        border = BorderStroke(1.dp, Color(0xFF2A1650))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF221142))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )
            if (!actionText.isNullOrBlank() && onActionClick != null) {
                Spacer(modifier = Modifier.height(14.dp))
                ProfileSecondaryButton(
                    text = actionText,
                    onClick = onActionClick,
                    borderColor = GoldPrimary
                )
            }
        }
    }
}

// ============================================================================
// SUB-PAGE 1: SAVED IDS (Chamet, TikTok, Free Fire, EPAY, etc.)
// ============================================================================

@Composable
fun ProfileSavedIdsSubPage(
    savedAccounts: List<SavedServiceAccount>,
    onSaveAccount: (serviceName: String, accountId: String, label: String, isDefault: Boolean, editId: String?) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onSetDefaultAccount: (String) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editingItemId by remember { mutableStateOf<String?>(null) }
    var selectedService by remember { mutableStateOf("CHAMET") }
    var accountIdInput by remember { mutableStateOf("") }
    var labelInput by remember { mutableStateOf("") }
    var setAsDefault by remember { mutableStateOf(true) }

    val supportedServices = remember {
        listOf(
            "CHAMET" to "💎 Chamet ID",
            "TIKTOK" to "🪙 TikTok Account",
            "FREEFIRE" to "🔥 Free Fire UID",
            "EPAY" to "💸 EPAY Email",
            "EXCHANGE" to "🔄 UPI / PhonePe"
        )
    }

    val fieldLabelForService = when (selectedService) {
        "CHAMET" -> "Chamet Numeric User ID"
        "TIKTOK" -> "TikTok Username / Profile ID"
        "FREEFIRE" -> "Free Fire Player UID"
        "EPAY" -> "EPAY Account Email"
        else -> "Account ID / Number"
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF150A2B)),
            border = BorderStroke(1.dp, Color(0xFF331B63))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "💾 Saved Service IDs",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "আপনার নিয়মিত ব্যবহৃত আইডি সেভ করে রাখলে অর্ডারের সময় অটো-ফিল হবে।",
                        fontSize = 11.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                ProfilePrimaryButton(
                    text = if (showForm) "বন্ধ করুন" else "➕ Add ID",
                    onClick = {
                        editingItemId = null
                        accountIdInput = ""
                        labelInput = ""
                        setAsDefault = true
                        showForm = !showForm
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showForm,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF180C34)),
                border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.75f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (editingItemId == null) "➕ নতুন সার্ভিস আইডি যোগ করুন" else "✏️ সেভ করা আইডি এডিট করুন",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldPrimary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        supportedServices.forEach { (key, title) ->
                            val isSelected = selectedService == key
                            Surface(
                                onClick = { selectedService = key },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF34186B) else Color(0xFF110724),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldPrimary else Color(0xFF2D1657)
                                )
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) GoldPrimary else Color(0xFFCBD5E1),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = accountIdInput,
                        onValueChange = { accountIdInput = it },
                        label = { Text(fieldLabelForService, fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF3B1F6E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0E061F),
                            unfocusedContainerColor = Color(0xFF0E061F)
                        )
                    )

                    OutlinedTextField(
                        value = labelInput,
                        onValueChange = { labelInput = it },
                        label = { Text("Nickname / নোট (ঐচ্ছিক, যেমন: My Main ID)", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF3B1F6E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0E061F),
                            unfocusedContainerColor = Color(0xFF0E061F)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐ এই সার্ভিসের জন্য Default ID হিসেবে সেট করুন",
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = setAsDefault,
                            onCheckedChange = { setAsDefault = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF120724),
                                checkedTrackColor = GoldPrimary
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileSecondaryButton(
                            text = "বাতিল",
                            onClick = { showForm = false },
                            modifier = Modifier.weight(1f)
                        )
                        ProfilePrimaryButton(
                            text = "সেভ করুন",
                            enabled = accountIdInput.trim().isNotBlank(),
                            onClick = {
                                onSaveAccount(
                                    selectedService,
                                    accountIdInput.trim(),
                                    labelInput.trim(),
                                    setAsDefault,
                                    editingItemId
                                )
                                showForm = false
                                editingItemId = null
                                accountIdInput = ""
                                labelInput = ""
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (savedAccounts.isEmpty()) {
            ProfileEmptyStateCard(
                icon = Icons.Default.BookmarkBorder,
                title = "No saved IDs yet",
                subtitle = "আপনার Chamet ID, TikTok Username বা Free Fire UID সেভ করে রাখুন যাতে দ্রুত অর্ডার করতে পারেন।",
                actionText = "➕ Add First ID",
                onActionClick = { showForm = true }
            )
        } else {
            savedAccounts.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF140A29)),
                    border = BorderStroke(
                        1.dp,
                        if (item.isDefault) GoldPrimary.copy(alpha = 0.7f) else Color(0xFF2B1654)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF291454)
                                ) {
                                    Text(
                                        text = item.serviceName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = NeonCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                if (item.isDefault) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = GoldPrimary.copy(alpha = 0.18f),
                                        border = BorderStroke(0.8.dp, GoldPrimary)
                                    ) {
                                        Text(
                                            text = "⭐ Default",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.accountId,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            if (item.label.isNotBlank()) {
                                Text(
                                    text = item.label,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { onSetDefaultAccount(item.id) },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.isDefault) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Set Default",
                                    tint = if (item.isDefault) GoldPrimary else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    editingItemId = item.id
                                    selectedService = item.serviceName
                                    accountIdInput = item.accountId
                                    labelInput = item.label
                                    setAsDefault = item.isDefault
                                    showForm = true
                                },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit ID",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteAccount(item.id) },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete ID",
                                    tint = Color(0xFFF87171),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// SUB-PAGE 2: MY ORDERS (All | Processing | Completed | Cancelled)
// ============================================================================

@Composable
fun ProfileMyOrdersSubPage(
    userOrders: List<OrderRecord>,
    onOpenOrderReceipt: (OrderRecord) -> Unit,
    onBuyServiceClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Processing", "Completed", "Cancelled")

    val filteredOrders = remember(userOrders, selectedFilter) {
        when (selectedFilter) {
            "Processing" -> userOrders.filter { it.status.equals("Processing", ignoreCase = true) || it.status.equals("Pending", ignoreCase = true) }
            "Completed" -> userOrders.filter { it.status.equals("Completed", ignoreCase = true) || it.status.equals("Approved", ignoreCase = true) }
            "Cancelled" -> userOrders.filter { it.status.equals("Cancelled", ignoreCase = true) }
            else -> userOrders
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF130926))
                .border(1.dp, Color(0xFF2D1657), RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filters.forEach { tab ->
                val isSelected = selectedFilter == tab
                Surface(
                    onClick = { selectedFilter = tab },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF35176E) else Color.Transparent,
                    border = if (isSelected) BorderStroke(1.dp, GoldPrimary) else null
                ) {
                    Text(
                        text = tab,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (isSelected) GoldPrimary else Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 9.dp)
                    )
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            ProfileEmptyStateCard(
                icon = Icons.Default.ReceiptLong,
                title = "No orders found",
                subtitle = "এই ক্যাটাগরিতে এখনো কোনো অর্ডার নেই। নতুন সার্ভিস অর্ডার করলে এখানে লাইভ স্ট্যাটাস দেখতে পাবেন।",
                actionText = "🛍️ সার্ভিস কিনুন",
                onActionClick = onBuyServiceClick
            )
        } else {
            filteredOrders.forEach { order ->
                val isCompleted = order.status == "Completed" || order.status == "Approved"
                val isCancelled = order.status == "Cancelled"
                val statusLabel = when {
                    isCompleted -> "🟢 Completed"
                    isCancelled -> "🔴 Cancelled"
                    else -> "🟡 Processing"
                }
                val statusColor = when {
                    isCompleted -> Color(0xFF4ADE80)
                    isCancelled -> Color(0xFFF87171)
                    else -> GoldPrimary
                }
                val dateText = remember(order.timestamp) {
                    SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.US).format(Date(order.timestamp))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenOrderReceipt(order) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF140A29)),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.38f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order #${order.orderNumber}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldLight
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = statusColor.copy(alpha = 0.14f),
                                border = BorderStroke(0.8.dp, statusColor.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = statusLabel,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = order.itemName,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "${order.category} • ${order.amountOrQuantity}",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                            Text(
                                text = "৳${String.format(Locale.US, "%,.0f", order.priceBdt)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateText,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onOpenOrderReceipt(order) }
                            ) {
                                Text(
                                    text = "View Details",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// SUB-PAGE 3: NOTIFICATION CENTER
// ============================================================================

@Composable
fun ProfileNotificationsSubPage(
    notifications: List<OrderNotification>,
    onMarkSingleRead: (String) -> Unit,
    onMarkAllRead: () -> Unit
) {
    val unreadCount = remember(notifications) { notifications.count { !it.isRead } }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF150A2B)),
            border = BorderStroke(1.dp, Color(0xFF331B63))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🔔 Notification Center",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (unreadCount > 0) "$unreadCount টি অপঠিত আপডেট রয়েছে" else "সকল নোটিফিকেশন পড়া হয়েছে",
                        fontSize = 11.5.sp,
                        color = if (unreadCount > 0) GoldPrimary else Color(0xFF94A3B8)
                    )
                }
                if (unreadCount > 0) {
                    Surface(
                        onClick = onMarkAllRead,
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF2A1454),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Mark all as read",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        }
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            ProfileEmptyStateCard(
                icon = Icons.Default.NotificationsNone,
                title = "No notifications yet",
                subtitle = "আপনার অর্ডার স্ট্যাটাস, পেমেন্ট আপডেট, ওয়ালেট ব্যালেন্স ও রেফারেল বোনাসের সকল মেসেজ এখানে পাওয়া যাবে।"
            )
        } else {
            notifications.forEach { notif ->
                val dateStr = remember(notif.timestamp) {
                    SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.US).format(Date(notif.timestamp))
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMarkSingleRead(notif.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!notif.isRead) Color(0xFF1C0D3B) else Color(0xFF130926)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (!notif.isRead) GoldPrimary.copy(alpha = 0.65f) else Color(0xFF28144F)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (!notif.isRead) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(GoldPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = notif.title,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            if (!notif.isRead) {
                                Text(
                                    text = "Mark as read",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    modifier = Modifier.clickable { onMarkSingleRead(notif.id) }
                                )
                            }
                        }
                        Text(
                            text = notif.message,
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 17.sp
                        )
                        Text(
                            text = dateStr,
                            fontSize = 10.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// SUB-PAGE 4: VIP SUPPORT
// ============================================================================

@Composable
fun ProfileVipSupportSubPage(
    adminConfig: AdminConfig,
    onOpenSupportChat: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    onOpenTelegram: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF150A2C)),
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(listOf(GoldPrimary, NeonMagenta))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GoldPrimary.copy(alpha = 0.16f))
                        .border(1.dp, GoldPrimary, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🎧 VIP Support",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "অ্যাপের ভিতর থেকে সরাসরি Admin Support-এ যোগাযোগ করুন।",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }

            ProfilePrimaryButton(
                text = "💬 Chat Support (Live In-App)",
                icon = Icons.Default.Chat,
                onClick = onOpenSupportChat,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileSecondaryButton(
                    text = "📱 WhatsApp",
                    icon = Icons.Default.PhoneInTalk,
                    borderColor = Color(0xFF22C55E),
                    onClick = onOpenWhatsApp,
                    modifier = Modifier.weight(1f)
                )
                ProfileSecondaryButton(
                    text = "✈️ Telegram",
                    icon = Icons.AutoMirrored.Filled.Send,
                    borderColor = Color(0xFF38BDF8),
                    onClick = onOpenTelegram,
                    modifier = Modifier.weight(1f)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0E0520),
                border = BorderStroke(1.dp, Color(0xFF2A1650)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "অফিসিয়াল সাপোর্ট তথ্য:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Text(
                        text = "• WhatsApp: ${adminConfig.whatsappDisplay.ifBlank { adminConfig.whatsappNumber }}",
                        fontSize = 11.5.sp,
                        color = Color(0xFFE2E8F0)
                    )
                    Text(
                        text = "• Telegram: ${adminConfig.telegramUsername}",
                        fontSize = 11.5.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}

// ============================================================================
// SUB-PAGE 5: STEP-BY-STEP SERVICE SELECTION PAGE (Section 24)
// Category selector -> Service selector -> Service details -> UID -> Price -> Payment
// ============================================================================

@Composable
fun ProfileStepByStepServiceBuyer(
    walletBalance: Double,
    allPackages: List<TopupPackage>,
    savedAccounts: List<SavedServiceAccount>,
    onSelectPackageForCheckout: (TopupPackage, String) -> Unit,
    onNavigateToSpecialTab: (ServiceCategory) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ServiceCategory.CHAMET) }
    val categories = remember {
        listOf(
            ServiceCategory.CHAMET,
            ServiceCategory.TIKTOK,
            ServiceCategory.FREEFIRE,
            ServiceCategory.SOCIAL_BOOST,
            ServiceCategory.EPAY,
            ServiceCategory.EXCHANGE
        )
    }

    val packagesForCategory = remember(allPackages, selectedCategory) {
        allPackages.filter { it.category == selectedCategory }
    }

    var selectedPackage by remember(packagesForCategory) {
        mutableStateOf(packagesForCategory.firstOrNull())
    }

    val defaultSavedUid = remember(savedAccounts, selectedCategory) {
        savedAccounts.firstOrNull {
            it.serviceName.equals(selectedCategory.name, ignoreCase = true) && it.isDefault
        }?.accountId ?: savedAccounts.firstOrNull {
            it.serviceName.equals(selectedCategory.name, ignoreCase = true)
        }?.accountId ?: ""
    }

    var targetUidInput by remember(defaultSavedUid, selectedCategory) {
        mutableStateOf(defaultSavedUid)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14092A)),
        border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🛍️ সার্ভিস সিলেকশন (Step-by-Step Checkout)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = GoldPrimary
            )

            // Step 1: Category Selector
            Text(
                text = "১. ক্যাটাগরি নির্বাচন করুন:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF35176E) else Color(0xFF0E0520),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) GoldPrimary else Color(0xFF2B1654)
                        )
                    ) {
                        Text(
                            text = "${cat.iconEmoji} ${cat.displayName}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isSelected) GoldPrimary else Color(0xFFCBD5E1),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                        )
                    }
                }
            }

            if (packagesForCategory.isEmpty()) {
                Text(
                    text = "${selectedCategory.displayName} সার্ভিসের কাস্টম অর্ডার পেইজে যেতে নিচের বাটনে ট্যাপ করুন:",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1)
                )
                ProfilePrimaryButton(
                    text = "${selectedCategory.iconEmoji} ${selectedCategory.displayName} পেইজে যান",
                    onClick = { onNavigateToSpecialTab(selectedCategory) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Step 2: Service / Package Selector
                Text(
                    text = "২. প্যাকেজ নির্বাচন করুন:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    packagesForCategory.forEach { pkg ->
                        val isSelected = selectedPackage?.id == pkg.id
                        Surface(
                            onClick = { selectedPackage = pkg },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF2B1359) else Color(0xFF0E0520),
                            border = BorderStroke(
                                1.2.dp,
                                if (isSelected) GoldPrimary else Color(0xFF2B1654)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = pkg.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )
                            }
                        }
                    }
                }

                // Step 3: Selected Service Details, UID & Price Summary
                selectedPackage?.let { pkg ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0E0520),
                        border = BorderStroke(1.dp, Color(0xFF2D1657)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("সার্ভিস:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text(pkg.name, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("পরিমাণ (Quantity):", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text(pkg.amount, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("মূল্য (Price):", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text(
                                    "৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)} BDT",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("আপনার ওয়ালেট ব্যালেন্স:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text(
                                    "৳${String.format(Locale.US, "%,.2f", walletBalance)} BDT",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (walletBalance >= pkg.priceBdt) SuccessGreen else Color(0xFFF87171)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = targetUidInput,
                        onValueChange = { targetUidInput = it },
                        label = { Text("আপনার ${selectedCategory.displayName} Account ID / UID", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF3B1F6E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0E061F),
                            unfocusedContainerColor = Color(0xFF0E061F)
                        )
                    )

                    ProfilePrimaryButton(
                        text = "পেমেন্ট ও অর্ডার কনফার্ম করুন →",
                        icon = Icons.Default.ShoppingBag,
                        onClick = { onSelectPackageForCheckout(pkg, targetUidInput.trim()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
