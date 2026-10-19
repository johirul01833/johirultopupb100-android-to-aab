package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdminConfig
import com.example.data.model.ServiceCategory
import com.example.data.model.SocialBoostCategory
import com.example.data.model.SocialBoostPlatform
import com.example.data.model.SocialBoostService
import com.example.data.model.TopupPackage
import com.example.ui.components.SocialBoostReferenceSquircle
import com.example.ui.components.TikTokReferenceSquircle
import com.example.ui.theme.BrandDarkBg
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import java.util.Locale

private const val PLATFORM_HOME_ALL_ID = "__HOME_ALL__"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialBoostScreen(
    adminConfig: AdminConfig,
    onSubmitOrder: (pkg: TopupPackage, targetLink: String) -> Unit,
    onWhatsAppSupport: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Dynamically load enabled Platforms, Categories, and Services sorted by displayOrder
    val activePlatforms = remember(adminConfig.socialBoostPlatforms) {
        adminConfig.socialBoostPlatforms
            .filter { it.isEnabled }
            .sortedBy { it.displayOrder }
    }

    var selectedPlatformId by remember(activePlatforms) {
        mutableStateOf(activePlatforms.firstOrNull()?.id ?: PLATFORM_HOME_ALL_ID)
    }

    LaunchedEffect(activePlatforms) {
        if (selectedPlatformId != PLATFORM_HOME_ALL_ID && activePlatforms.none { it.id == selectedPlatformId }) {
            selectedPlatformId = activePlatforms.firstOrNull()?.id ?: PLATFORM_HOME_ALL_ID
        }
    }

    val selectedPlatform = remember(activePlatforms, selectedPlatformId) {
        if (selectedPlatformId == PLATFORM_HOME_ALL_ID) {
            null
        } else {
            activePlatforms.find { it.id == selectedPlatformId } ?: activePlatforms.firstOrNull()
        }
    }

    val activeCategoriesForSelection = remember(adminConfig.socialBoostCategories, activePlatforms, selectedPlatformId, selectedPlatform?.id) {
        val enabledPlatformIds = activePlatforms.map { it.id }.toSet()
        if (selectedPlatformId == PLATFORM_HOME_ALL_ID) {
            adminConfig.socialBoostCategories
                .filter { it.isEnabled && it.platformId in enabledPlatformIds }
                .sortedWith(compareBy({ it.displayOrder }, { it.name }))
        } else {
            val pid = selectedPlatform?.id ?: ""
            adminConfig.socialBoostCategories
                .filter { it.isEnabled && it.platformId == pid }
                .sortedBy { it.displayOrder }
        }
    }

    var selectedCategoryId by remember(activeCategoriesForSelection) {
        mutableStateOf(activeCategoriesForSelection.firstOrNull()?.id ?: "")
    }

    LaunchedEffect(activeCategoriesForSelection) {
        if (activeCategoriesForSelection.none { it.id == selectedCategoryId }) {
            selectedCategoryId = activeCategoriesForSelection.firstOrNull()?.id ?: ""
        }
    }

    val selectedCategory = remember(activeCategoriesForSelection, selectedCategoryId) {
        activeCategoriesForSelection.find { it.id == selectedCategoryId }
            ?: activeCategoriesForSelection.firstOrNull()
    }

    val activeServicesForCategory = remember(adminConfig.socialBoostServices, selectedCategory?.id) {
        val cid = selectedCategory?.id ?: ""
        adminConfig.socialBoostServices
            .filter { it.isEnabled && it.categoryId == cid }
            .sortedBy { it.displayOrder }
    }

    var selectedServiceId by remember(activeServicesForCategory) {
        mutableStateOf(activeServicesForCategory.firstOrNull()?.id ?: "")
    }

    LaunchedEffect(activeServicesForCategory) {
        if (activeServicesForCategory.none { it.id == selectedServiceId }) {
            selectedServiceId = activeServicesForCategory.firstOrNull()?.id ?: ""
        }
    }

    val selectedService = remember(activeServicesForCategory, selectedServiceId) {
        activeServicesForCategory.find { it.id == selectedServiceId }
            ?: activeServicesForCategory.firstOrNull()
    }

    val resolvedPlatformForOrder = remember(activePlatforms, selectedPlatform, selectedCategory, selectedService) {
        selectedPlatform
            ?: activePlatforms.find { it.id == selectedService?.platformId }
            ?: activePlatforms.find { it.id == selectedCategory?.platformId }
            ?: activePlatforms.firstOrNull()
    }

    // BottomSheet Dropdown States (Real Dropdowns for Category and Service)
    var showCategoryDropdownSheet by remember { mutableStateOf(false) }
    var categorySearchQuery by remember { mutableStateOf("") }

    var showServiceDropdownSheet by remember { mutableStateOf(false) }
    var serviceSearchQuery by remember { mutableStateOf("") }

    var targetLink by remember { mutableStateOf("") }
    var quantityInput by remember(selectedService?.id) {
        mutableStateOf((selectedService?.minQuantity ?: 100).toString())
    }

    val parsedQty = quantityInput.toIntOrNull() ?: 0
    val minQty = selectedService?.minQuantity ?: 100
    val maxQty = selectedService?.maxQuantity ?: 100000
    val ratePer1000 = selectedService?.pricePerThousandBdt ?: 0.0
    val requiresLink = selectedService?.requiresLink ?: true

    val calculatedCharge = remember(parsedQty, ratePer1000) {
        if (parsedQty <= 0 || ratePer1000 <= 0.0) {
            0.0
        } else {
            (parsedQty.toDouble() / 1000.0) * ratePer1000
        }
    }

    val isBelowMin = quantityInput.isNotBlank() && parsedQty < minQty
    val isAboveMax = quantityInput.isNotBlank() && parsedQty > maxQty
    val isQuantityValid = parsedQty in minQty..maxQty

    val cleanLink = targetLink.trim()
    val isLinkFormatValid = !requiresLink || (cleanLink.length >= 4 && (cleanLink.contains(".") || cleanLink.contains("/") || cleanLink.startsWith("@")))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandDarkBg)
            .testTag("social_boost_screen"),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // =====================================================================
        // 1. SOCIAL BOOST BANNER (Compact Header)
        // =====================================================================
        item {
            SocialBoostCompactBanner(
                platformCount = activePlatforms.size,
                serviceCount = adminConfig.socialBoostServices.count { it.isEnabled }
            )
        }

        if (activePlatforms.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF16082C),
                    border = BorderStroke(1.dp, Color(0xFF3B1566))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SocialBoostReferenceSquircle(size = 42.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Social Boost Services Updating",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "New services are being updated. Please check back shortly.",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // =================================================================
            // 2. COMPACT PLATFORM SELECTOR (Horizontal Scrollable Small Buttons)
            // =================================================================
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF140728),
                    border = BorderStroke(1.dp, Color(0xFF311356))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // "Home" compact button (shows all categories across platforms)
                            item(key = PLATFORM_HOME_ALL_ID) {
                                val isHomeSelected = selectedPlatformId == PLATFORM_HOME_ALL_ID
                                CompactHorizontalPlatformButton(
                                    label = "Home",
                                    iconKey = "HOME",
                                    isSelected = isHomeSelected,
                                    onClick = {
                                        selectedPlatformId = PLATFORM_HOME_ALL_ID
                                    },
                                    testTag = "social_boost_platform_home"
                                )
                            }

                            // Dynamic platforms from Firebase / Admin Panel
                            items(activePlatforms, key = { it.id }) { platform ->
                                val isSelected = selectedPlatformId == platform.id
                                CompactHorizontalPlatformButton(
                                    label = platform.name,
                                    iconKey = platform.iconKey,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedPlatformId = platform.id
                                    },
                                    testTag = "social_boost_platform_${platform.id}"
                                )
                            }
                        }
                    }
                }
            }

            // =================================================================
            // 3. MAIN ORDERING CARD (Category Dropdown -> Service Dropdown -> Link -> Quantity -> Summary -> Submit)
            // =================================================================
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF15082B),
                    border = BorderStroke(1.dp, Color(0xFF38155E))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(13.dp),
                        verticalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        // -----------------------------------------------------
                        // CATEGORY DROPDOWN
                        // -----------------------------------------------------
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CATEGORY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldLight,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = resolvedPlatformForOrder?.name ?: "All Platforms",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            }

                            Surface(
                                onClick = {
                                    categorySearchQuery = ""
                                    showCategoryDropdownSheet = true
                                },
                                shape = RoundedCornerShape(11.dp),
                                color = Color(0xFF0E041D),
                                border = BorderStroke(1.1.dp, Color(0xFF4C1D95)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("social_boost_category_selector")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 11.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        PlatformBrandSquircle(
                                            iconKey = resolvedPlatformForOrder?.iconKey ?: "GENERIC",
                                            platformName = resolvedPlatformForOrder?.name ?: "Social",
                                            size = 20.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = selectedCategory?.name ?: "Select Category",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Open Category Dropdown",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // -----------------------------------------------------
                        // SERVICE DROPDOWN
                        // -----------------------------------------------------
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SERVICE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldLight,
                                    letterSpacing = 0.5.sp
                                )
                                if (selectedService != null) {
                                    Text(
                                        text = "৳${formatBdtAmount(selectedService.pricePerThousandBdt)} / 1,000",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary
                                    )
                                }
                            }

                            Surface(
                                onClick = {
                                    serviceSearchQuery = ""
                                    showServiceDropdownSheet = true
                                },
                                shape = RoundedCornerShape(11.dp),
                                color = Color(0xFF0E041D),
                                border = BorderStroke(
                                    1.1.dp,
                                    Brush.horizontalGradient(listOf(Color(0xFF5B21B6), GoldPrimary.copy(alpha = 0.7f)))
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("social_boost_service_selector")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 11.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (selectedService != null) {
                                            "${selectedService.serviceCode} — ${selectedService.name}"
                                        } else {
                                            "Select Service"
                                        },
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Open Service Dropdown",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Compact Selected Service Description / Instructions (Only for the 1 selected service)
                            if (selectedService != null && selectedService.description.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0E162B), RoundedCornerShape(9.dp))
                                        .padding(horizontal = 9.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier
                                            .size(13.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = selectedService.description,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFFCBD5E1),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        // -----------------------------------------------------
                        // LINK INPUT
                        // -----------------------------------------------------
                        if (requiresLink) {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = "LINK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldLight,
                                    letterSpacing = 0.5.sp
                                )
                                OutlinedTextField(
                                    value = targetLink,
                                    onValueChange = { targetLink = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("social_boost_link_input"),
                                    placeholder = {
                                        Text(
                                            text = "Paste your profile / page / post / video link",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF6B7280),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Link,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (targetLink.isNotEmpty()) {
                                                IconButton(onClick = { targetLink = "" }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Clear Link",
                                                        tint = Color(0xFF9CA3AF),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                    val clipText = clipboard?.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString()
                                                    if (!clipText.isNullOrBlank()) {
                                                        targetLink = clipText.trim()
                                                    } else {
                                                        Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentPaste,
                                                    contentDescription = "Paste Link",
                                                    tint = GoldPrimary,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(11.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (targetLink.isBlank() || isLinkFormatValid) GoldPrimary else DangerRed,
                                        unfocusedBorderColor = if (targetLink.isBlank() || isLinkFormatValid) Color(0xFF4C1D95) else DangerRed,
                                        focusedContainerColor = Color(0xFF0E041D),
                                        unfocusedContainerColor = Color(0xFF0E041D),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                                if (targetLink.isNotBlank() && !isLinkFormatValid) {
                                    Text(
                                        text = "Please enter a valid public URL or link (e.g., https://...)",
                                        fontSize = 10.sp,
                                        color = DangerRed,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = Color(0xFF0F241D),
                                border = BorderStroke(0.8.dp, SuccessGreen.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "✓ No link required for this service",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // -----------------------------------------------------
                        // QUANTITY INPUT + MIN / MAX INFORMATION
                        // -----------------------------------------------------
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "QUANTITY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldLight,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Min: $minQty   •   Max: $maxQty",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isQuantityValid) Color(0xFF4ADE80) else DangerRed
                                )
                            }

                            OutlinedTextField(
                                value = quantityInput,
                                onValueChange = { input ->
                                    quantityInput = input.filter { it.isDigit() }.take(9)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("social_boost_quantity_input"),
                                placeholder = {
                                    Text(
                                        text = "$minQty",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(11.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isQuantityValid) GoldPrimary else DangerRed,
                                    unfocusedBorderColor = if (isQuantityValid) Color(0xFF4C1D95) else DangerRed,
                                    focusedContainerColor = Color(0xFF0E041D),
                                    unfocusedContainerColor = Color(0xFF0E041D),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            if (isBelowMin) {
                                Text(
                                    text = "⚠️ Minimum quantity for this service is $minQty.",
                                    fontSize = 10.5.sp,
                                    color = DangerRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else if (isAboveMax) {
                                Text(
                                    text = "⚠️ Maximum quantity for this service is $maxQty.",
                                    fontSize = 10.5.sp,
                                    color = DangerRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Compact Quick Quantity Preset Pills
                            val quickPresets = remember(minQty, maxQty) {
                                val list = mutableListOf<Pair<String, Int>>()
                                list.add("Min ($minQty)" to minQty)
                                listOf(
                                    "500" to 500,
                                    "1K" to 1000,
                                    "2K" to 2000,
                                    "5K" to 5000,
                                    "10K" to 10000
                                ).forEach { (lbl, v) ->
                                    if (v in minQty..maxQty && v != minQty) {
                                        list.add(lbl to v)
                                    }
                                }
                                list
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(quickPresets, key = { it.first }) { (label, presetVal) ->
                                    val isPresetSelected = parsedQty == presetVal
                                    Surface(
                                        onClick = { quantityInput = presetVal.toString() },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isPresetSelected) NeonPurple else Color(0xFF1A0A33),
                                        border = BorderStroke(
                                            0.9.dp,
                                            if (isPresetSelected) GoldPrimary else Color(0xFF3B1566)
                                        )
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPresetSelected) Color.White else GoldLight,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // -----------------------------------------------------
                        // AVERAGE TIME & TOTAL CHARGE + COMPACT ORDER SUMMARY
                        // -----------------------------------------------------
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF100520),
                            border = BorderStroke(
                                1.dp,
                                Brush.horizontalGradient(listOf(Color(0xFF4C1D95), GoldPrimary.copy(alpha = 0.65f)))
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = "ORDER SUMMARY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldLight,
                                    letterSpacing = 0.6.sp
                                )

                                CompactSummaryRow(
                                    label = "Platform & Category",
                                    value = "${resolvedPlatformForOrder?.name ?: "-"} • ${selectedCategory?.name ?: "-"}"
                                )
                                CompactSummaryRow(
                                    label = "Service",
                                    value = selectedService?.let { "${it.serviceCode} — ${it.name}" } ?: "-"
                                )
                                if (requiresLink && cleanLink.isNotBlank()) {
                                    CompactSummaryRow(
                                        label = "Link",
                                        value = cleanLink
                                    )
                                }
                                CompactSummaryRow(
                                    label = "Min / Max",
                                    value = "$minQty / $maxQty"
                                )

                                // Average Time Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Average Time",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    Text(
                                        text = selectedService?.averageTime ?: "15 - 45 Minutes",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF7DD3FC)
                                    )
                                }

                                HorizontalDivider(color = Color(0xFF28104A))

                                // Rate & Quantity calculation row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rate: ৳${formatBdtAmount(ratePer1000)} / 1,000",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFCBD5E1)
                                    )
                                    Text(
                                        text = "Quantity: $parsedQty",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }

                                // Total Charge Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Charge",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldLight
                                    )
                                    Text(
                                        text = "৳${formatBdtAmount(calculatedCharge)} BDT",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary
                                    )
                                }
                            }
                        }

                        // -----------------------------------------------------
                        // SUBMIT ORDER BUTTON
                        // -----------------------------------------------------
                        Button(
                            onClick = {
                                val plat = resolvedPlatformForOrder
                                val cat = selectedCategory
                                val srv = selectedService
                                if (plat == null) {
                                    Toast.makeText(context, "Please select a Platform", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (cat == null) {
                                    Toast.makeText(context, "Please select a Category", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (srv == null) {
                                    Toast.makeText(context, "Please select a Service", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (requiresLink && (!isLinkFormatValid || cleanLink.isBlank())) {
                                    Toast.makeText(context, "Please enter a valid profile / page / post / video link", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (isBelowMin) {
                                    Toast.makeText(context, "Minimum quantity is ${srv.minQuantity}", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (isAboveMax || !isQuantityValid) {
                                    Toast.makeText(context, "Quantity must be between ${srv.minQuantity} and ${srv.maxQuantity}", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (calculatedCharge <= 0.0) {
                                    Toast.makeText(context, "Invalid total charge", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val finalCharge = if (calculatedCharge < 1.0) 1.0 else calculatedCharge
                                val resolvedLink = if (requiresLink) cleanLink else "No Link Required (${plat.name})"
                                val orderPackage = TopupPackage(
                                    id = "sb_${srv.id}_$parsedQty",
                                    category = ServiceCategory.SOCIAL_BOOST,
                                    name = "${plat.name} - ${srv.name}",
                                    subtitle = cat.name,
                                    amount = "$parsedQty Qty (${srv.averageTime})",
                                    priceBdt = finalCharge,
                                    badge = plat.name
                                )
                                onSubmitOrder(orderPackage, resolvedLink)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("social_boost_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF140526),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit Order",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF140526)
                            )
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // CATEGORY SELECTION MODAL BOTTOM SHEET (REAL DROPDOWN)
    // =========================================================================
    if (showCategoryDropdownSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val filteredCategories = remember(activeCategoriesForSelection, categorySearchQuery) {
            if (categorySearchQuery.isBlank()) {
                activeCategoriesForSelection
            } else {
                activeCategoriesForSelection.filter {
                    it.name.contains(categorySearchQuery, ignoreCase = true) ||
                        it.description.contains(categorySearchQuery, ignoreCase = true)
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showCategoryDropdownSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF15072A),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Category",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "${filteredCategories.size} categories available",
                            fontSize = 11.sp,
                            color = GoldLight
                        )
                    }
                    IconButton(onClick = { showCategoryDropdownSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFCBD5E1)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search inside Category Bottom Sheet
                OutlinedTextField(
                    value = categorySearchQuery,
                    onValueChange = { categorySearchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search category...", fontSize = 12.sp, color = Color(0xFF6B7280))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF3B1566),
                        focusedContainerColor = Color(0xFF0E041D),
                        unfocusedContainerColor = Color(0xFF0E041D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredCategories, key = { it.id }) { cat ->
                        val isSelected = cat.id == selectedCategory?.id
                        val catPlat = activePlatforms.find { it.id == cat.platformId }
                        Surface(
                            onClick = {
                                selectedCategoryId = cat.id
                                showCategoryDropdownSheet = false
                            },
                            shape = RoundedCornerShape(11.dp),
                            color = if (isSelected) Color(0xFF28104E) else Color(0xFF100420),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) GoldPrimary else Color(0xFF311356)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("social_boost_cat_chip_${cat.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    PlatformBrandSquircle(
                                        iconKey = catPlat?.iconKey ?: "GENERIC",
                                        platformName = catPlat?.name ?: "Social",
                                        size = 22.dp
                                    )
                                    Spacer(modifier = Modifier.width(9.dp))
                                    Column {
                                        Text(
                                            text = cat.name,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else Color(0xFFE2E8F0)
                                        )
                                        if (cat.description.isNotBlank()) {
                                            Text(
                                                text = cat.description,
                                                fontSize = 10.sp,
                                                color = Color(0xFF9CA3AF),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // SERVICE SELECTION MODAL BOTTOM SHEET (REAL DROPDOWN)
    // =========================================================================
    if (showServiceDropdownSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val filteredServices = remember(activeServicesForCategory, serviceSearchQuery) {
            if (serviceSearchQuery.isBlank()) {
                activeServicesForCategory
            } else {
                activeServicesForCategory.filter {
                    it.name.contains(serviceSearchQuery, ignoreCase = true) ||
                        it.serviceCode.toString().contains(serviceSearchQuery) ||
                        it.description.contains(serviceSearchQuery, ignoreCase = true)
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showServiceDropdownSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF15072A),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Select Service",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = selectedCategory?.name ?: "Available Services",
                            fontSize = 11.sp,
                            color = GoldLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { showServiceDropdownSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFCBD5E1)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search field inside Service Bottom Sheet
                OutlinedTextField(
                    value = serviceSearchQuery,
                    onValueChange = { serviceSearchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search service by name or ID...", fontSize = 12.sp, color = Color(0xFF6B7280))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0xFF3B1566),
                        focusedContainerColor = Color(0xFF0E041D),
                        unfocusedContainerColor = Color(0xFF0E041D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(filteredServices, key = { it.id }) { srv ->
                        val isSelected = srv.id == selectedService?.id
                        Surface(
                            onClick = {
                                selectedServiceId = srv.id
                                showServiceDropdownSheet = false
                            },
                            shape = RoundedCornerShape(11.dp),
                            color = if (isSelected) Color(0xFF28104E) else Color(0xFF100420),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) GoldPrimary else Color(0xFF311356)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("social_boost_service_item_${srv.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "${srv.serviceCode} — ${srv.name}",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "৳${formatBdtAmount(srv.pricePerThousandBdt)} / 1K • Min ${srv.minQuantity} • Max ${srv.maxQuantity}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = srv.averageTime,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF7DD3FC)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialBoostCompactBanner(
    platformCount: Int,
    serviceCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF180832),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(GoldPrimary, NeonMagenta, Color(0xFF00E5FF)))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                SocialBoostReferenceSquircle(size = 34.dp)
                Spacer(modifier = Modifier.width(9.dp))
                Column {
                    Text(
                        text = "Social Boost",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Fast • Non-Drop • Instant Social Growth",
                        fontSize = 10.sp,
                        color = Color(0xFFCBD5E1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = SuccessGreen.copy(alpha = 0.14f),
                border = BorderStroke(0.8.dp, SuccessGreen)
            ) {
                Text(
                    text = "$platformCount Platforms • $serviceCount Services",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun CompactHorizontalPlatformButton(
    label: String,
    iconKey: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val accentColor = getPlatformAccentColor(iconKey, label)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF28104E) else Color(0xFF0F041E),
        border = BorderStroke(
            width = if (isSelected) 1.3.dp else 0.9.dp,
            brush = if (isSelected) {
                Brush.linearGradient(listOf(accentColor, GoldPrimary))
            } else {
                Brush.linearGradient(listOf(Color(0xFF2D124D), Color(0xFF1F0B38)))
            }
        ),
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PlatformBrandSquircle(
                iconKey = iconKey,
                platformName = label,
                size = 18.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CompactSummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF9CA3AF)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

private fun getPlatformAccentColor(iconKey: String, platformName: String): Color {
    val key = "${iconKey.uppercase()} ${platformName.uppercase()}"
    return when {
        key.contains("HOME") -> GoldPrimary
        key.contains("FACEBOOK") -> Color(0xFF1877F2)
        key.contains("INSTAGRAM") -> Color(0xFFE1306C)
        key.contains("YOUTUBE") -> Color(0xFFFF0033)
        key.contains("TIKTOK") -> Color(0xFF00F2FE)
        key.contains("TELEGRAM") -> Color(0xFF2AABEE)
        key.contains("SPOTIFY") -> Color(0xFF1DB954)
        key.contains("LINKEDIN") -> Color(0xFF0A66C2)
        key.contains("TWITTER") || iconKey.uppercase() == "X" || platformName.trim().equals("X", ignoreCase = true) -> Color(0xFFE2E8F0)
        key.contains("VIDEO") -> Color(0xFFFF6B00)
        key.contains("WEBSITE") || key.contains("WEB") -> Color(0xFF38BDF8)
        key.contains("DISCORD") -> Color(0xFF5865F2)
        else -> GoldPrimary
    }
}

@Composable
fun PlatformBrandSquircle(
    iconKey: String,
    platformName: String = "",
    size: Dp
) {
    val combined = "${iconKey.uppercase()} ${platformName.uppercase()}"
    when {
        combined.contains("HOME") -> HomeBrandSquircle(size = size)
        combined.contains("TIKTOK") -> TikTokReferenceSquircle(size = size)
        combined.contains("FACEBOOK") -> FacebookBrandSquircle(size = size)
        combined.contains("INSTAGRAM") -> InstagramBrandSquircle(size = size)
        combined.contains("YOUTUBE") -> YouTubeBrandSquircle(size = size)
        combined.contains("TELEGRAM") -> TelegramBrandSquircle(size = size)
        combined.contains("SPOTIFY") -> SpotifyBrandSquircle(size = size)
        combined.contains("LINKEDIN") -> LinkedInBrandSquircle(size = size)
        combined.contains("TWITTER") || iconKey.uppercase() == "X" || platformName.trim().equals("X", ignoreCase = true) -> XBrandSquircle(size = size)
        combined.contains("VIDEO") -> VideoPlatformSquircle(size = size)
        combined.contains("WEBSITE") || combined.contains("WEB") -> WebsiteBrandSquircle(size = size)
        combined.contains("DISCORD") -> DiscordBrandSquircle(size = size)
        else -> SocialBoostReferenceSquircle(size = size)
    }
}

@Composable
private fun HomeBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFF59E0B), Color(0xFFB45309))
            ),
            cornerRadius = cr
        )

        val roofPath = Path().apply {
            moveTo(w * 0.50f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.48f)
            lineTo(w * 0.68f, h * 0.48f)
            lineTo(w * 0.68f, h * 0.78f)
            lineTo(w * 0.32f, h * 0.78f)
            lineTo(w * 0.32f, h * 0.48f)
            lineTo(w * 0.20f, h * 0.48f)
            close()
        }
        drawPath(path = roofPath, color = Color.White, style = Fill)
    }
}

@Composable
private fun FacebookBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1877F2), Color(0xFF0C52B8))
            ),
            cornerRadius = cr
        )
        drawRoundRect(
            color = Color(0xFF60A5FA),
            cornerRadius = cr,
            style = Stroke(width = w * 0.04f)
        )

        val fPath = Path().apply {
            moveTo(w * 0.58f, h * 0.84f)
            lineTo(w * 0.58f, h * 0.54f)
            lineTo(w * 0.69f, h * 0.54f)
            lineTo(w * 0.71f, h * 0.41f)
            lineTo(w * 0.58f, h * 0.41f)
            lineTo(w * 0.58f, h * 0.33f)
            cubicTo(
                w * 0.58f, h * 0.28f,
                w * 0.61f, h * 0.26f,
                w * 0.67f, h * 0.26f
            )
            lineTo(w * 0.72f, h * 0.26f)
            lineTo(w * 0.72f, h * 0.15f)
            lineTo(w * 0.63f, h * 0.15f)
            cubicTo(
                w * 0.49f, h * 0.15f,
                w * 0.44f, h * 0.22f,
                w * 0.44f, h * 0.34f
            )
            lineTo(w * 0.44f, h * 0.41f)
            lineTo(w * 0.34f, h * 0.41f)
            lineTo(w * 0.34f, h * 0.54f)
            lineTo(w * 0.44f, h * 0.54f)
            lineTo(w * 0.44f, h * 0.84f)
            close()
        }
        drawPath(path = fPath, color = Color.White, style = Fill)
    }
}

@Composable
private fun InstagramBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF833AB4),
                    Color(0xFFFD1D1D),
                    Color(0xFFFCAF45)
                ),
                start = Offset(w * 0.85f, h * 0.10f),
                end = Offset(w * 0.15f, h * 0.90f)
            ),
            cornerRadius = cr
        )

        val camInset = w * 0.22f
        val camSize = w - camInset * 2f
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(camInset, camInset),
            size = Size(camSize, camSize),
            cornerRadius = CornerRadius(w * 0.16f, h * 0.16f),
            style = Stroke(width = w * 0.065f)
        )

        drawCircle(
            color = Color.White,
            radius = w * 0.135f,
            center = Offset(w * 0.50f, h * 0.50f),
            style = Stroke(width = w * 0.065f)
        )

        drawCircle(
            color = Color.White,
            radius = w * 0.038f,
            center = Offset(w * 0.65f, h * 0.35f)
        )
    }
}

@Composable
private fun YouTubeBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF280B18), Color(0xFF120614))
            ),
            cornerRadius = cr
        )
        drawRoundRect(
            color = Color(0xFFFF0033),
            cornerRadius = cr,
            style = Stroke(width = w * 0.04f)
        )

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFF1A40), Color(0xFFCC0000))
            ),
            topLeft = Offset(w * 0.17f, h * 0.26f),
            size = Size(w * 0.66f, h * 0.48f),
            cornerRadius = CornerRadius(w * 0.14f, h * 0.14f)
        )

        val playPath = Path().apply {
            moveTo(w * 0.43f, h * 0.37f)
            lineTo(w * 0.62f, h * 0.50f)
            lineTo(w * 0.43f, h * 0.63f)
            close()
        }
        drawPath(path = playPath, color = Color.White, style = Fill)
    }
}

@Composable
private fun XBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1F2937), Color(0xFF090D16))
            ),
            cornerRadius = cr
        )
        drawRoundRect(
            color = Color(0xFF94A3B8),
            cornerRadius = cr,
            style = Stroke(width = w * 0.04f)
        )

        drawLine(
            color = Color.White,
            start = Offset(w * 0.28f, h * 0.26f),
            end = Offset(w * 0.72f, h * 0.74f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.72f, h * 0.26f),
            end = Offset(w * 0.28f, h * 0.74f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun TelegramBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
            ),
            cornerRadius = cr
        )

        val planePath = Path().apply {
            moveTo(w * 0.20f, h * 0.52f)
            lineTo(w * 0.76f, h * 0.26f)
            lineTo(w * 0.64f, h * 0.76f)
            lineTo(w * 0.46f, h * 0.60f)
            lineTo(w * 0.36f, h * 0.68f)
            lineTo(w * 0.38f, h * 0.54f)
            close()
        }
        drawPath(path = planePath, color = Color.White, style = Fill)
    }
}

@Composable
private fun SpotifyBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF22C55E), Color(0xFF15803D))
            ),
            cornerRadius = cr
        )

        drawLine(
            color = Color.White,
            start = Offset(w * 0.26f, h * 0.38f),
            end = Offset(w * 0.74f, h * 0.38f),
            strokeWidth = w * 0.075f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.30f, h * 0.52f),
            end = Offset(w * 0.70f, h * 0.52f),
            strokeWidth = w * 0.07f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.35f, h * 0.66f),
            end = Offset(w * 0.65f, h * 0.66f),
            strokeWidth = w * 0.065f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun LinkedInBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0A66C2), Color(0xFF004182))
            ),
            cornerRadius = cr
        )

        drawCircle(
            color = Color.White,
            radius = w * 0.055f,
            center = Offset(w * 0.32f, h * 0.32f)
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.32f, h * 0.45f),
            end = Offset(w * 0.32f, h * 0.72f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.White,
            start = Offset(w * 0.50f, h * 0.45f),
            end = Offset(w * 0.50f, h * 0.72f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.50f, h * 0.47f),
            end = Offset(w * 0.68f, h * 0.47f),
            strokeWidth = w * 0.085f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.68f, h * 0.47f),
            end = Offset(w * 0.68f, h * 0.72f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun VideoPlatformSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF6B00), Color(0xFFE11D48))
            ),
            cornerRadius = cr
        )

        val playPath = Path().apply {
            moveTo(w * 0.36f, h * 0.28f)
            lineTo(w * 0.72f, h * 0.50f)
            lineTo(w * 0.36f, h * 0.72f)
            close()
        }
        drawPath(path = playPath, color = Color.White, style = Fill)
    }
}

@Composable
private fun WebsiteBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0284C7), Color(0xFF1E3A8A))
            ),
            cornerRadius = cr
        )

        // Globe outline & meridians
        drawCircle(
            color = Color.White,
            radius = w * 0.28f,
            center = Offset(w * 0.50f, h * 0.50f),
            style = Stroke(width = w * 0.06f)
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.22f, h * 0.50f),
            end = Offset(w * 0.78f, h * 0.50f),
            strokeWidth = w * 0.055f
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.50f, h * 0.22f),
            end = Offset(w * 0.50f, h * 0.78f),
            strokeWidth = w * 0.055f
        )
    }
}

@Composable
private fun DiscordBrandSquircle(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cr = CornerRadius(w * 0.28f, h * 0.28f)

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF5865F2), Color(0xFF404EED))
            ),
            cornerRadius = cr
        )

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.22f, h * 0.30f),
            size = Size(w * 0.56f, h * 0.40f),
            cornerRadius = CornerRadius(w * 0.14f, h * 0.14f)
        )
        drawCircle(
            color = Color(0xFF5865F2),
            radius = w * 0.06f,
            center = Offset(w * 0.39f, h * 0.50f)
        )
        drawCircle(
            color = Color(0xFF5865F2),
            radius = w * 0.06f,
            center = Offset(w * 0.61f, h * 0.50f)
        )
    }
}

private fun formatBdtAmount(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        String.format(Locale.US, "%,.0f", amount)
    } else {
        String.format(Locale.US, "%,.2f", amount)
    }
}
