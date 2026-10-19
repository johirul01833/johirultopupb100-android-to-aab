package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.OrderRecord
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow

@Composable
fun AdminLoginDialog(
    defaultAdminEmail: String = "johirul958423@gmail.com",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    hasRegisteredAdmin: Boolean = true,
    startInRegisterMode: Boolean = false,
    loggedInUserEmail: String = "",
    loggedInUserName: String = "",
    loggedInUserPhone: String = "",
    isAdminLoggedIn: Boolean = false,
    orders: List<OrderRecord> = emptyList(),
    onDismiss: () -> Unit,
    onSignInWithFirebase: (email: String, password: String) -> Unit,
    onRegisterAdminWithFirebase: (email: String, password: String) -> Unit,
    onRegisterUserWithFirebase: (name: String, phone: String, email: String, password: String, referralCode: String) -> Unit = { _, _, email, pass, _ ->
        onRegisterAdminWithFirebase(email, pass)
    },
    onSendPasswordReset: (email: String) -> Unit,
    onOpenNotifications: () -> Unit = {},
    onOpenAdminPanel: () -> Unit = {},
    onOpenFullProfile: () -> Unit = {},
    onLogoutUser: () -> Unit = {}
) {
    var showProfileView by remember(loggedInUserEmail) {
        mutableStateOf(loggedInUserEmail.isNotBlank())
    }
    var isRegisterMode by remember(startInRegisterMode) {
        mutableStateOf(startInRegisterMode)
    }
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var referralCodeInput by remember { mutableStateOf("") }
    var emailInput by remember {
        mutableStateOf(if (startInRegisterMode) "" else "")
    }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localValidationError by remember { mutableStateOf<String?>(null) }

    val handleSubmit = {
        val trimmedName = nameInput.trim()
        val trimmedPhone = phoneInput.trim()
        val trimmedEmail = emailInput.trim()
        val trimmedPass = passwordInput.trim()
        val trimmedRef = referralCodeInput.trim().uppercase()
        when {
            isRegisterMode && trimmedName.length < 2 -> {
                localValidationError = "অনুগ্রহ করে আপনার পূর্ণ নাম লিখুন"
            }
            isRegisterMode && trimmedPhone.length < 6 -> {
                localValidationError = "অনুগ্রহ করে আপনার সচল মোবাইল / WhatsApp নাম্বার লিখুন"
            }
            trimmedEmail.isEmpty() || !trimmedEmail.contains("@") -> {
                localValidationError = "অনুগ্রহ করে সঠিক ইমেইল এড্রেস লিখুন"
            }
            trimmedPass.length < 6 -> {
                localValidationError = "পাসওয়ার্ড অন্তত ৬ অক্ষরের হতে হবে"
            }
            isRegisterMode && trimmedPass != confirmPasswordInput.trim() -> {
                localValidationError = "উভয় পাসওয়ার্ড মিলছে না!"
            }
            else -> {
                localValidationError = null
                if (isRegisterMode) {
                    onRegisterUserWithFirebase(trimmedName, trimmedPhone, trimmedEmail, trimmedPass, trimmedRef)
                } else {
                    onSignInWithFirebase(trimmedEmail, trimmedPass)
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 650.dp)
                .clip(RoundedCornerShape(26.dp))
                .border(
                    1.5.dp,
                    Brush.linearGradient(listOf(GoldPrimary, NeonMagenta, NeonPurple)),
                    RoundedCornerShape(26.dp)
                ),
            color = Color(0xFF130724),
            tonalElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF05010D))
                                .border(1.2.dp, GoldPrimary, CircleShape),
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

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (showProfileView) "আমার একাউন্ট প্রোফাইল" else "লগইন ও সাইন আপ",
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SuccessGreen.copy(alpha = 0.15f),
                                    border = BorderStroke(0.8.dp, SuccessGreen.copy(alpha = 0.6f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = null,
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = "Firebase",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Chamet Top Up BD • Secure Online Panel",
                                fontSize = 10.5.sp,
                                color = GoldLight
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF26123D), CircleShape)
                            .testTag("admin_login_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (showProfileView && loggedInUserEmail.isNotBlank()) {
                    // -------------------------------------------------------------------
                    // LOGGED-IN USER / ADMIN ACCOUNT PROFILE VIEW
                    // -------------------------------------------------------------------
                    val cleanEmail = loggedInUserEmail.trim().lowercase()
                    val userOrders = if (isAdminLoggedIn) {
                        orders
                    } else {
                        orders.filter { it.customerEmail.trim().lowercase() == cleanEmail }
                    }
                    val totalOrdersCount = userOrders.size
                    val approvedOrdersCount = userOrders.count { it.status == "Completed" || it.status == "Approved" }
                    val processingOrdersCount = userOrders.count { it.status == "Processing" || it.status == "Pending" }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1D0B36),
                        border = BorderStroke(
                            1.2.dp,
                            Brush.horizontalGradient(listOf(GoldPrimary, NeonPurple))
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFF581C87), Color(0xFF1E0B36))
                                        )
                                    )
                                    .border(1.5.dp, GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isAdminLoggedIn) "👑" else "👤",
                                    fontSize = 26.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = loggedInUserName.ifBlank { loggedInUserEmail.substringBefore("@") },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = loggedInUserEmail,
                                fontSize = 12.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Medium
                            )

                            if (loggedInUserPhone.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "📱 $loggedInUserPhone",
                                    fontSize = 11.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isAdminLoggedIn) GoldPrimary.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, if (isAdminLoggedIn) GoldPrimary else SuccessGreen)
                            ) {
                                Text(
                                    text = if (isAdminLoggedIn) "👑 Authorized Admin Account" else "✅ Verified Customer Account",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAdminLoggedIn) GoldPrimary else SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Order Status Summary Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileStatMiniCard(
                            label = "মোট অর্ডার",
                            value = "$totalOrdersCount টি",
                            color = GoldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatMiniCard(
                            label = "এপ্রুভড (Approved)",
                            value = "$approvedOrdersCount টি",
                            color = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatMiniCard(
                            label = "প্রসেসিং",
                            value = "$processingOrdersCount টি",
                            color = WarningYellow,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Open Full Profile & Wallet Screen Button
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenFullProfile()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_full_profile_button"),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF052E16),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "👤 ফুল প্রোফাইল, ওয়ালেট ও রেফারেল পেজ ➔",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF052E16)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Open Order Approval Notifications Button
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenNotifications()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF281147)),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.7f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔔 অর্ডার এপ্রুভাল ও নোটিফিকেশন দেখুন",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    if (isAdminLoggedIn) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenAdminPanel()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(13.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFF130724),
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "👑 কন্ট্রোল প্যানেলে যান ➔",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF130724)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            onLogoutUser()
                            showProfileView = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.65f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "লগআউট করুন (Sign Out)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF87171)
                        )
                    }
                } else {
                    // -------------------------------------------------------------------
                    // SIGN IN (লগইন) & SIGN UP (সাইন আপ) FORM
                    // -------------------------------------------------------------------
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0B0315),
                        border = BorderStroke(1.dp, Color(0xFF38155E)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable {
                                        isRegisterMode = false
                                        localValidationError = null
                                    },
                                shape = RoundedCornerShape(9.dp),
                                color = if (!isRegisterMode) GoldPrimary else Color.Transparent
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "🔑 লগইন (Log In)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (!isRegisterMode) Color(0xFF130724) else Color(0xFFD1D5DB)
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable {
                                        isRegisterMode = true
                                        localValidationError = null
                                    },
                                shape = RoundedCornerShape(9.dp),
                                color = if (isRegisterMode) GoldPrimary else Color.Transparent
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "📝 সাইন আপ (Sign Up)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isRegisterMode) Color(0xFF130724) else Color(0xFFD1D5DB)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Info Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E0A38),
                        border = BorderStroke(1.dp, Color(0xFF38155E)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isRegisterMode) "📝" else "🔐", fontSize = 19.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isRegisterMode) "নতুন একাউন্ট তৈরি করুন (Sign Up)" else "আপনার একাউন্টে লগইন করুন (Log In)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isRegisterMode) {
                                        "আপনার নাম, ইমেইল এবং পাসওয়ার্ড দিয়ে নতুন একাউন্ট রেজিস্টার করুন।"
                                    } else {
                                        "আপনার ইমেইল এবং পাসওয়ার্ড দিয়ে একাউন্টে লগইন করুন।"
                                    },
                                    fontSize = 10.5.sp,
                                    color = Color(0xFFC084FC),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name & Phone Fields (Sign Up Mode)
                    if (isRegisterMode) {
                        Text(
                            text = "👤 আপনার নাম (Full Name)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                localValidationError = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_input"),
                            placeholder = {
                                Text("আপনার পূর্ণ নাম লিখুন...", fontSize = 12.5.sp, color = Color(0xFF6B7280))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(13.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0C0317),
                                unfocusedContainerColor = Color(0xFF0C0317)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "📱 মোবাইল / WhatsApp নাম্বার (সাপোর্ট ও চ্যাটের জন্য)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = {
                                phoneInput = it
                                localValidationError = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_phone_input"),
                            placeholder = {
                                Text("01XXXXXXXXX", fontSize = 12.5.sp, color = Color(0xFF6B7280))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(13.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0C0317),
                                unfocusedContainerColor = Color(0xFF0C0317)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Email Input Header
                    Text(
                        text = "📧 ইমেইল এড্রেস (Email Address)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            localValidationError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_email_input"),
                        placeholder = {
                            Text("আপনার ইমেইল লিখুন (e.g. user@gmail.com)", fontSize = 12.5.sp, color = Color(0xFF6B7280))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(13.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0C0317),
                            unfocusedContainerColor = Color(0xFF0C0317)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password Field
                    Text(
                        text = "🔑 পাসওয়ার্ড (Password)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            localValidationError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_password_input"),
                        placeholder = {
                            Text("আপনার পাসওয়ার্ড লিখুন (৬+ অক্ষর)...", fontSize = 12.5.sp, color = Color(0xFF6B7280))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                                    tint = Color(0xFFA855F7),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isRegisterMode) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { if (!isRegisterMode) handleSubmit() }),
                        singleLine = true,
                        shape = RoundedCornerShape(13.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0xFF38155E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0C0317),
                            unfocusedContainerColor = Color(0xFF0C0317)
                        )
                    )

                    if (isRegisterMode) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🔐 পাসওয়ার্ড নিশ্চিত করুন (Confirm Password)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = {
                                confirmPasswordInput = it
                                localValidationError = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_confirm_password_input"),
                            placeholder = {
                                Text("পুনরায় পাসওয়ার্ড লিখুন...", fontSize = 12.5.sp, color = Color(0xFF6B7280))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(13.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0C0317),
                                unfocusedContainerColor = Color(0xFF0C0317)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🎁 রেফার কোড (ঐচ্ছিক / Optional Referral Code)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldLight,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = referralCodeInput,
                            onValueChange = {
                                referralCodeInput = it.uppercase()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_referral_code_input"),
                            placeholder = {
                                Text("কারো রেফার কোড থাকলে লিখুন (Optional)", fontSize = 12.5.sp, color = Color(0xFF6B7280))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { handleSubmit() }),
                            singleLine = true,
                            shape = RoundedCornerShape(13.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0xFF38155E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0C0317),
                                unfocusedContainerColor = Color(0xFF0C0317)
                            )
                        )
                    }

                    // Error or Success Message
                    val displayedError = localValidationError ?: errorMessage
                    AnimatedVisibility(
                        visible = !displayedError.isNullOrBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DangerRed.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = displayedError ?: "",
                                    color = Color(0xFFFF8A8A),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = !successMessage.isNullOrBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SuccessGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = successMessage ?: "",
                                    color = SuccessGreen,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Submit Button
                    Button(
                        onClick = { handleSubmit() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("admin_login_submit_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color(0xFF130724),
                                strokeWidth = 2.2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "যাচাই করা হচ্ছে...",
                                color = Color(0xFF130724),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        } else {
                            Icon(
                                imageVector = if (isRegisterMode) Icons.Default.PersonAdd else Icons.Default.Key,
                                contentDescription = null,
                                tint = Color(0xFF130724),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRegisterMode) "সাইন আপ ও একাউন্ট তৈরি করুন ➔" else "লগইন করুন (Sign In) ➔",
                                color = Color(0xFF130724),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (!isRegisterMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onSendPasswordReset(emailInput.trim()) }
                            ) {
                                Text(
                                    text = "পাসওয়ার্ড ভুলে গেছেন? Reset",
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            TextButton(
                                onClick = { isRegisterMode = true }
                            ) {
                                Text(
                                    text = "নতুন ইউজার? সাইন আপ করুন",
                                    fontSize = 11.sp,
                                    color = Color(0xFFC084FC),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        TextButton(
                            onClick = { isRegisterMode = false }
                        ) {
                            Text(
                                text = "আগে থেকেই একাউন্ট আছে? লগইন করুন ➔",
                                fontSize = 11.5.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatMiniCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F041E),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.5.sp,
                color = Color(0xFFCBD5E1),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
