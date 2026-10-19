package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdminConfig
import com.example.data.model.SocialBoostCategory
import com.example.data.model.SocialBoostPlatform
import com.example.data.model.SocialBoostService
import com.example.ui.screens.PlatformBrandSquircle
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import java.util.Locale

@Composable
fun AdminSocialBoostTab(
    config: AdminConfig,
    onSaveConfig: (AdminConfig) -> Unit
) {
    val context = LocalContext.current
    // 0 = Services, 1 = Categories, 2 = Platforms
    var activeSection by remember { mutableIntStateOf(0) }

    val platforms = remember(config.socialBoostPlatforms) {
        config.socialBoostPlatforms.sortedBy { it.displayOrder }
    }
    val categories = remember(config.socialBoostCategories) {
        config.socialBoostCategories.sortedBy { it.displayOrder }
    }
    val services = remember(config.socialBoostServices) {
        config.socialBoostServices.sortedBy { it.displayOrder }
    }

    // Filter state for Categories & Services
    var selectedPlatformFilter by remember { mutableStateOf<String?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var adminServiceSearchQuery by remember { mutableStateOf("") }

    // Platform Editor Dialog State
    var showPlatformEditor by remember { mutableStateOf(false) }
    var isEditingExistingPlatform by remember { mutableStateOf(false) }
    var editPlatformId by remember { mutableStateOf("") }
    var editPlatformName by remember { mutableStateOf("") }
    var editPlatformIconKey by remember { mutableStateOf("FACEBOOK") }
    var editPlatformDesc by remember { mutableStateOf("") }
    var editPlatformOrder by remember { mutableStateOf("1") }
    var editPlatformEnabled by remember { mutableStateOf(true) }

    // Category Editor Dialog State
    var showCategoryEditor by remember { mutableStateOf(false) }
    var isEditingExistingCategory by remember { mutableStateOf(false) }
    var editCategoryId by remember { mutableStateOf("") }
    var editCategoryPlatformId by remember { mutableStateOf("") }
    var editCategoryName by remember { mutableStateOf("") }
    var editCategoryDesc by remember { mutableStateOf("") }
    var editCategoryOrder by remember { mutableStateOf("1") }
    var editCategoryEnabled by remember { mutableStateOf(true) }

    // Service Editor Dialog State
    var showServiceEditor by remember { mutableStateOf(false) }
    var isEditingExistingService by remember { mutableStateOf(false) }
    var editServiceOriginalId by remember { mutableStateOf("") }
    var editServiceId by remember { mutableStateOf("") }
    var editServicePlatformId by remember { mutableStateOf("") }
    var editServiceCategoryId by remember { mutableStateOf("") }
    var editServiceName by remember { mutableStateOf("") }
    var editServicePrice by remember { mutableStateOf("160") }
    var editServiceMin by remember { mutableStateOf("100") }
    var editServiceMax by remember { mutableStateOf("100000") }
    var editServiceAvgTime by remember { mutableStateOf("15 – 45 Minutes") }
    var editServiceDesc by remember { mutableStateOf("") }
    var editServiceOrder by remember { mutableStateOf("1") }
    var editServiceRequiresLink by remember { mutableStateOf(true) }
    var editServiceEnabled by remember { mutableStateOf(true) }
    var editServiceCreatedAt by remember { mutableLongStateOf(0L) }

    // Delete Confirmation Dialog State
    var pendingDeleteLabel by remember { mutableStateOf<String?>(null) }
    var pendingDeleteAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    fun openPlatformDialog(platform: SocialBoostPlatform?) {
        if (platform != null) {
            isEditingExistingPlatform = true
            editPlatformId = platform.id
            editPlatformName = platform.name
            editPlatformIconKey = platform.iconKey
            editPlatformDesc = platform.description
            editPlatformOrder = platform.displayOrder.toString()
            editPlatformEnabled = platform.isEnabled
        } else {
            isEditingExistingPlatform = false
            editPlatformId = "plat_${System.currentTimeMillis()}"
            editPlatformName = ""
            editPlatformIconKey = "FACEBOOK"
            editPlatformDesc = ""
            editPlatformOrder = (platforms.size + 1).toString()
            editPlatformEnabled = true
        }
        showPlatformEditor = true
    }

    fun openCategoryDialog(category: SocialBoostCategory?) {
        if (category != null) {
            isEditingExistingCategory = true
            editCategoryId = category.id
            editCategoryPlatformId = category.platformId
            editCategoryName = category.name
            editCategoryDesc = category.description
            editCategoryOrder = category.displayOrder.toString()
            editCategoryEnabled = category.isEnabled
        } else {
            isEditingExistingCategory = false
            editCategoryId = "cat_${System.currentTimeMillis()}"
            editCategoryPlatformId = selectedPlatformFilter ?: platforms.firstOrNull()?.id.orEmpty()
            editCategoryName = ""
            editCategoryDesc = ""
            editCategoryOrder = (categories.size + 1).toString()
            editCategoryEnabled = true
        }
        showCategoryEditor = true
    }

    fun openServiceDialog(service: SocialBoostService?) {
        if (service != null) {
            isEditingExistingService = true
            editServiceOriginalId = service.id
            editServiceId = service.id
            editServicePlatformId = service.platformId
            editServiceCategoryId = service.categoryId
            editServiceName = service.name
            editServicePrice = if (service.pricePerThousandBdt % 1.0 == 0.0) {
                service.pricePerThousandBdt.toInt().toString()
            } else {
                service.pricePerThousandBdt.toString()
            }
            editServiceMin = service.minQuantity.toString()
            editServiceMax = service.maxQuantity.toString()
            editServiceAvgTime = service.averageTime
            editServiceDesc = service.description
            editServiceOrder = service.displayOrder.toString()
            editServiceRequiresLink = service.requiresLink
            editServiceEnabled = service.isEnabled
            editServiceCreatedAt = service.createdAt
        } else {
            isEditingExistingService = false
            val defaultPlatId = selectedPlatformFilter ?: platforms.firstOrNull()?.id.orEmpty()
            val defaultCatId = selectedCategoryFilter
                ?: categories.firstOrNull { it.platformId == defaultPlatId }?.id
                ?: categories.firstOrNull()?.id.orEmpty()
            val newId = "srv_${(1000..9999).random()}"
            editServiceOriginalId = newId
            editServiceId = newId
            editServicePlatformId = defaultPlatId
            editServiceCategoryId = defaultCatId
            editServiceName = ""
            editServicePrice = "160"
            editServiceMin = "100"
            editServiceMax = "100000"
            editServiceAvgTime = "15 – 45 Minutes"
            editServiceDesc = "⚡ Fast Start: 15–45 Minutes\n🔒 100% Safe & Public Link Required\n♾️ High Retention Guaranteed"
            editServiceOrder = (services.size + 1).toString()
            editServiceRequiresLink = true
            editServiceEnabled = true
            editServiceCreatedAt = System.currentTimeMillis()
        }
        showServiceEditor = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_social_boost_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. COMPACT CONTROL PANEL HEADER + SUMMARY STATS
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF140B29),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(NeonMagenta.copy(alpha = 0.55f), GoldPrimary.copy(alpha = 0.45f))
                    )
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🚀 Social Boost Control Panel",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Real-Time Firebase CRUD • Platforms • Categories • Services",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "LIVE SYNC",
                                color = SuccessGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Summary Tabs: Services | Categories | Platforms
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminSectionTabCard(
                            title = "Services",
                            totalCount = services.size,
                            activeCount = services.count { it.isEnabled },
                            isSelected = activeSection == 0,
                            accentColor = GoldPrimary,
                            onClick = { activeSection = 0 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_sb_tab_services")
                        )
                        AdminSectionTabCard(
                            title = "Categories",
                            totalCount = categories.size,
                            activeCount = categories.count { it.isEnabled },
                            isSelected = activeSection == 1,
                            accentColor = NeonMagenta,
                            onClick = { activeSection = 1 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_sb_tab_categories")
                        )
                        AdminSectionTabCard(
                            title = "Platforms",
                            totalCount = platforms.size,
                            activeCount = platforms.count { it.isEnabled },
                            isSelected = activeSection == 2,
                            accentColor = Color(0xFF38BDF8),
                            onClick = { activeSection = 2 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_sb_tab_platforms")
                        )
                    }
                }
            }
        }

        // ==========================================
        // SECTION 0: SERVICE MANAGEMENT
        // ==========================================
        if (activeSection == 0) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Service Management",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Add, edit, price, reorder, or disable Social Boost services",
                                color = Color.White.copy(alpha = 0.55f),
                                fontSize = 10.sp
                            )
                        }
                        Button(
                            onClick = { openServiceDialog(null) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = Color(0xFF090414)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("admin_add_sb_service_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Service", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                    }

                    // Quick Admin Search for Services
                    OutlinedTextField(
                        value = adminServiceSearchQuery,
                        onValueChange = { adminServiceSearchQuery = it },
                        placeholder = {
                            Text(
                                text = "Filter services by name or ID...",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            if (adminServiceSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { adminServiceSearchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.14f),
                            focusedContainerColor = Color(0xFF120A24),
                            unfocusedContainerColor = Color(0xFF120A24),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_sb_service_search_input")
                    )

                    // Filter by Platform Pills
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = selectedPlatformFilter == null,
                                onClick = {
                                    selectedPlatformFilter = null
                                    selectedCategoryFilter = null
                                },
                                label = { Text("All Platforms", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonMagenta.copy(alpha = 0.25f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(platforms, key = { it.id }) { p ->
                            FilterChip(
                                selected = selectedPlatformFilter == p.id,
                                onClick = {
                                    selectedPlatformFilter = p.id
                                    selectedCategoryFilter = null
                                },
                                label = { Text(p.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonMagenta.copy(alpha = 0.25f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Filter by Category Pills
                    val visibleCategories = if (selectedPlatformFilter == null) {
                        categories
                    } else {
                        categories.filter { it.platformId == selectedPlatformFilter }
                    }
                    if (visibleCategories.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedCategoryFilter == null,
                                    onClick = { selectedCategoryFilter = null },
                                    label = { Text("All Categories", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary.copy(alpha = 0.22f),
                                        selectedLabelColor = GoldLight
                                    )
                                )
                            }
                            items(visibleCategories, key = { it.id }) { c ->
                                FilterChip(
                                    selected = selectedCategoryFilter == c.id,
                                    onClick = { selectedCategoryFilter = c.id },
                                    label = {
                                        Text(
                                            text = c.name,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary.copy(alpha = 0.22f),
                                        selectedLabelColor = GoldLight
                                    )
                                )
                            }
                        }
                    }
                }
            }

            val filteredServices = services.filter { s ->
                val matchesPlatform = selectedPlatformFilter == null || s.platformId == selectedPlatformFilter
                val matchesCategory = selectedCategoryFilter == null || s.categoryId == selectedCategoryFilter
                val q = adminServiceSearchQuery.trim()
                val matchesQuery = q.isEmpty() ||
                    s.name.contains(q, ignoreCase = true) ||
                    s.id.contains(q, ignoreCase = true) ||
                    s.displayCode().contains(q, ignoreCase = true)
                matchesPlatform && matchesCategory && matchesQuery
            }

            if (filteredServices.isEmpty()) {
                item {
                    EmptyAdminListNotice("No services match this filter. Tap 'Add Service' to create one.")
                }
            } else {
                items(filteredServices, key = { it.id }) { service ->
                    val parentPlat = platforms.find { it.id == service.platformId }
                    val parentCat = categories.find { it.id == service.categoryId }
                    val priceFormatted = if (service.pricePerThousandBdt % 1.0 == 0.0) {
                        String.format(Locale.US, "%,.0f", service.pricePerThousandBdt)
                    } else {
                        String.format(Locale.US, "%,.2f", service.pricePerThousandBdt)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_sb_service_row_${service.id}"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF120B24),
                        border = BorderStroke(
                            1.dp,
                            if (service.isEnabled) Color.White.copy(alpha = 0.1f) else DangerRed.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PlatformBrandSquircle(
                                        iconKey = parentPlat?.iconKey ?: "OTHER",
                                        size = 30.dp
                                    )
                                    Spacer(modifier = Modifier.width(9.dp))
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(5.dp),
                                                color = NeonMagenta.copy(alpha = 0.18f),
                                                border = BorderStroke(0.5.dp, NeonMagenta.copy(alpha = 0.45f))
                                            ) {
                                                Text(
                                                    text = "#${service.displayCode()}",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 9.sp,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                )
                                            }
                                            Text(
                                                text = "${parentPlat?.name ?: "Platform"} • ${parentCat?.name ?: "Category"}",
                                                color = Color.White.copy(alpha = 0.6f),
                                                fontSize = 10.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = service.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Rate badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldPrimary.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.45f))
                                ) {
                                    Text(
                                        text = "৳$priceFormatted / 1K",
                                        color = GoldLight,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Min: ${service.minQuantity} • Max: ${service.maxQuantity} • Avg: ${service.averageTime} • Order #${service.displayOrder}",
                                color = Color.White.copy(alpha = 0.62f),
                                fontSize = 10.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = service.isEnabled,
                                        onCheckedChange = { enabled ->
                                            val now = System.currentTimeMillis()
                                            val updated = config.socialBoostServices.map {
                                                if (it.id == service.id) it.copy(isEnabled = enabled, updatedAt = now) else it
                                            }
                                            onSaveConfig(config.copy(socialBoostServices = updated))
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = SuccessGreen
                                        ),
                                        modifier = Modifier.scale(0.75f)
                                    )
                                    Text(
                                        text = if (service.isEnabled) "Active" else "Disabled",
                                        color = if (service.isEnabled) SuccessGreen else DangerRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            val reordered = moveServiceOrder(config.socialBoostServices, service.id, -1)
                                            onSaveConfig(config.copy(socialBoostServices = reordered))
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowUpward,
                                            contentDescription = "Move Up",
                                            tint = Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val reordered = moveServiceOrder(config.socialBoostServices, service.id, 1)
                                            onSaveConfig(config.copy(socialBoostServices = reordered))
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowDownward,
                                            contentDescription = "Move Down",
                                            tint = Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { openServiceDialog(service) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Service",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            pendingDeleteLabel = "Service '${service.name}'"
                                            pendingDeleteAction = {
                                                val updated = config.socialBoostServices.filterNot { it.id == service.id }
                                                onSaveConfig(config.copy(socialBoostServices = updated))
                                                Toast.makeText(context, "Service deleted & synced", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Service",
                                            tint = DangerRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 1: CATEGORY MANAGEMENT
        // ==========================================
        if (activeSection == 1) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Category Management",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Organize categories under each Social Boost platform",
                                color = Color.White.copy(alpha = 0.55f),
                                fontSize = 10.sp
                            )
                        }
                        Button(
                            onClick = { openCategoryDialog(null) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonMagenta,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("admin_add_sb_category_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Category", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                    }

                    // Filter by Platform Pills
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = selectedPlatformFilter == null,
                                onClick = { selectedPlatformFilter = null },
                                label = { Text("All Platforms", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonMagenta.copy(alpha = 0.25f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(platforms, key = { it.id }) { p ->
                            FilterChip(
                                selected = selectedPlatformFilter == p.id,
                                onClick = { selectedPlatformFilter = p.id },
                                label = { Text(p.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonMagenta.copy(alpha = 0.25f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            val filteredCategories = if (selectedPlatformFilter == null) {
                categories
            } else {
                categories.filter { it.platformId == selectedPlatformFilter }
            }

            if (filteredCategories.isEmpty()) {
                item {
                    EmptyAdminListNotice("No categories found. Tap 'Add Category' to create one.")
                }
            } else {
                items(filteredCategories, key = { it.id }) { category ->
                    val parentPlat = platforms.find { it.id == category.platformId }
                    val categoryServiceCount = services.count { it.categoryId == category.id }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_sb_category_row_${category.id}"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF120B24),
                        border = BorderStroke(
                            1.dp,
                            if (category.isEnabled) Color.White.copy(alpha = 0.1f) else DangerRed.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PlatformBrandSquircle(
                                    iconKey = parentPlat?.iconKey ?: "OTHER",
                                    size = 30.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = category.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${parentPlat?.name ?: "Platform"} • $categoryServiceCount Services • Order #${category.displayOrder}",
                                        color = Color.White.copy(alpha = 0.58f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = category.isEnabled,
                                    onCheckedChange = { enabled ->
                                        val updated = config.socialBoostCategories.map {
                                            if (it.id == category.id) it.copy(isEnabled = enabled) else it
                                        }
                                        onSaveConfig(config.copy(socialBoostCategories = updated))
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = SuccessGreen
                                    ),
                                    modifier = Modifier.scale(0.72f)
                                )
                                IconButton(
                                    onClick = {
                                        val reordered = moveCategoryOrder(config.socialBoostCategories, category.id, -1)
                                        onSaveConfig(config.copy(socialBoostCategories = reordered))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = "Move Up",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val reordered = moveCategoryOrder(config.socialBoostCategories, category.id, 1)
                                        onSaveConfig(config.copy(socialBoostCategories = reordered))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = "Move Down",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { openCategoryDialog(category) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Category",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        pendingDeleteLabel = "Category '${category.name}'"
                                        pendingDeleteAction = {
                                            val updatedCats = config.socialBoostCategories.filterNot { it.id == category.id }
                                            val updatedSrvs = config.socialBoostServices.filterNot { it.categoryId == category.id }
                                            onSaveConfig(
                                                config.copy(
                                                    socialBoostCategories = updatedCats,
                                                    socialBoostServices = updatedSrvs
                                                )
                                            )
                                            Toast.makeText(context, "Category deleted & synced", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Category",
                                        tint = DangerRed,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 2: PLATFORM MANAGEMENT
        // ==========================================
        if (activeSection == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Platform Management",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Manage platforms shown in the compact horizontal selector",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 10.sp
                        )
                    }
                    Button(
                        onClick = { openPlatformDialog(null) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF38BDF8),
                            contentColor = Color(0xFF090414)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("admin_add_sb_platform_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Platform", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }
            }

            if (platforms.isEmpty()) {
                item {
                    EmptyAdminListNotice("No platforms configured. Tap 'Add Platform' to create one.")
                }
            } else {
                items(platforms, key = { it.id }) { platform ->
                    val catCount = categories.count { it.platformId == platform.id }
                    val srvCount = services.count { it.platformId == platform.id }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_sb_platform_row_${platform.id}"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF120B24),
                        border = BorderStroke(
                            1.dp,
                            if (platform.isEnabled) Color.White.copy(alpha = 0.1f) else DangerRed.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PlatformBrandSquircle(
                                    iconKey = platform.iconKey,
                                    size = 34.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = platform.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "$catCount Categories • $srvCount Services • Order #${platform.displayOrder}",
                                        color = Color.White.copy(alpha = 0.58f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = platform.isEnabled,
                                    onCheckedChange = { enabled ->
                                        val updated = config.socialBoostPlatforms.map {
                                            if (it.id == platform.id) it.copy(isEnabled = enabled) else it
                                        }
                                        onSaveConfig(config.copy(socialBoostPlatforms = updated))
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = SuccessGreen
                                    ),
                                    modifier = Modifier.scale(0.72f)
                                )
                                IconButton(
                                    onClick = {
                                        val reordered = movePlatformOrder(config.socialBoostPlatforms, platform.id, -1)
                                        onSaveConfig(config.copy(socialBoostPlatforms = reordered))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = "Move Up",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val reordered = movePlatformOrder(config.socialBoostPlatforms, platform.id, 1)
                                        onSaveConfig(config.copy(socialBoostPlatforms = reordered))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = "Move Down",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { openPlatformDialog(platform) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Platform",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        pendingDeleteLabel = "Platform '${platform.name}'"
                                        pendingDeleteAction = {
                                            val updatedPlats = config.socialBoostPlatforms.filterNot { it.id == platform.id }
                                            val updatedCats = config.socialBoostCategories.filterNot { it.platformId == platform.id }
                                            val updatedSrvs = config.socialBoostServices.filterNot { it.platformId == platform.id }
                                            onSaveConfig(
                                                config.copy(
                                                    socialBoostPlatforms = updatedPlats,
                                                    socialBoostCategories = updatedCats,
                                                    socialBoostServices = updatedSrvs
                                                )
                                            )
                                            Toast.makeText(context, "Platform deleted & synced", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Platform",
                                        tint = DangerRed,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // DELETE CONFIRMATION DIALOG
    // ==========================================
    if (pendingDeleteLabel != null && pendingDeleteAction != null) {
        AlertDialog(
            onDismissRequest = {
                pendingDeleteLabel = null
                pendingDeleteAction = null
            },
            containerColor = Color(0xFF160D2E),
            title = {
                Text(
                    text = "Confirm Delete",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ${pendingDeleteLabel ?: ""}? This change will sync immediately to Firebase.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingDeleteAction?.invoke()
                        pendingDeleteLabel = null
                        pendingDeleteAction = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        pendingDeleteLabel = null
                        pendingDeleteAction = null
                    }
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    // ==========================================
    // ADD / EDIT PLATFORM DIALOG
    // ==========================================
    if (showPlatformEditor) {
        val iconPresets = listOf(
            "FACEBOOK", "INSTAGRAM", "YOUTUBE", "X", "SPOTIFY",
            "TIKTOK", "TELEGRAM", "LINKEDIN", "VIDEO", "WEBSITE", "DISCORD", "OTHER"
        )
        AlertDialog(
            onDismissRequest = { showPlatformEditor = false },
            containerColor = Color(0xFF150C2B),
            title = {
                Text(
                    text = if (isEditingExistingPlatform) "Edit Platform" else "Add New Platform",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editPlatformName,
                        onValueChange = { editPlatformName = it },
                        label = { Text("Platform Name (e.g., Facebook)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Select Platform Icon",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(iconPresets, key = { it }) { preset ->
                            val isSelected = editPlatformIconKey.equals(preset, ignoreCase = true)
                            Surface(
                                onClick = { editPlatformIconKey = preset },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) NeonMagenta.copy(alpha = 0.25f) else Color(0xFF0D071B),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.14f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    PlatformBrandSquircle(iconKey = preset, size = 26.dp)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = preset,
                                        color = if (isSelected) GoldLight else Color.White.copy(alpha = 0.7f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editPlatformOrder,
                        onValueChange = { editPlatformOrder = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Display Order") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editPlatformDesc,
                        onValueChange = { editPlatformDesc = it },
                        label = { Text("Short Tagline (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Platform Enabled", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Switch(
                            checked = editPlatformEnabled,
                            onCheckedChange = { editPlatformEnabled = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedName = editPlatformName.trim()
                        if (trimmedName.isEmpty()) {
                            Toast.makeText(context, "Enter platform name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val order = editPlatformOrder.toIntOrNull() ?: (platforms.size + 1)
                        val newPlatform = SocialBoostPlatform(
                            id = editPlatformId,
                            name = trimmedName,
                            iconKey = editPlatformIconKey,
                            description = editPlatformDesc.trim(),
                            displayOrder = order,
                            isEnabled = editPlatformEnabled
                        )
                        val exists = config.socialBoostPlatforms.any { it.id == editPlatformId }
                        val updatedList = if (exists) {
                            config.socialBoostPlatforms.map { if (it.id == editPlatformId) newPlatform else it }
                        } else {
                            config.socialBoostPlatforms + newPlatform
                        }
                        onSaveConfig(config.copy(socialBoostPlatforms = updatedList))
                        showPlatformEditor = false
                        Toast.makeText(context, "Platform saved & synced to Firebase", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF090414))
                ) {
                    Text("Save Platform", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPlatformEditor = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    // ==========================================
    // ADD / EDIT CATEGORY DIALOG
    // ==========================================
    if (showCategoryEditor) {
        AlertDialog(
            onDismissRequest = { showCategoryEditor = false },
            containerColor = Color(0xFF150C2B),
            title = {
                Text(
                    text = if (isEditingExistingCategory) "Edit Category" else "Add New Category",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Select Platform",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(platforms, key = { it.id }) { p ->
                            FilterChip(
                                selected = editCategoryPlatformId == p.id,
                                onClick = { editCategoryPlatformId = p.id },
                                label = { Text(p.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonMagenta.copy(alpha = 0.28f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editCategoryName,
                        onValueChange = { editCategoryName = it },
                        label = { Text("Category Name (e.g., Facebook Followers | High-Quality)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editCategoryOrder,
                        onValueChange = { editCategoryOrder = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Display Order") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editCategoryDesc,
                        onValueChange = { editCategoryDesc = it },
                        label = { Text("Category Subtitle / Note (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Category Enabled", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Switch(
                            checked = editCategoryEnabled,
                            onCheckedChange = { editCategoryEnabled = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedName = editCategoryName.trim()
                        if (trimmedName.isEmpty() || editCategoryPlatformId.isBlank()) {
                            Toast.makeText(context, "Select platform and enter category name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val order = editCategoryOrder.toIntOrNull() ?: (categories.size + 1)
                        val newCat = SocialBoostCategory(
                            id = editCategoryId,
                            platformId = editCategoryPlatformId,
                            name = trimmedName,
                            description = editCategoryDesc.trim(),
                            displayOrder = order,
                            isEnabled = editCategoryEnabled
                        )
                        val exists = config.socialBoostCategories.any { it.id == editCategoryId }
                        val updatedList = if (exists) {
                            config.socialBoostCategories.map { if (it.id == editCategoryId) newCat else it }
                        } else {
                            config.socialBoostCategories + newCat
                        }
                        onSaveConfig(config.copy(socialBoostCategories = updatedList))
                        showCategoryEditor = false
                        Toast.makeText(context, "Category saved & synced to Firebase", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF090414))
                ) {
                    Text("Save Category", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCategoryEditor = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    // ==========================================
    // ADD / EDIT SERVICE DIALOG
    // ==========================================
    if (showServiceEditor) {
        val categoriesForSelectedPlat = categories.filter { it.platformId == editServicePlatformId }
        AlertDialog(
            onDismissRequest = { showServiceEditor = false },
            containerColor = Color(0xFF150C2B),
            title = {
                Text(
                    text = if (isEditingExistingService) "Edit Social Boost Service" else "Add Social Boost Service",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Text(
                        text = "1. Select Platform",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(platforms, key = { it.id }) { p ->
                            FilterChip(
                                selected = editServicePlatformId == p.id,
                                onClick = {
                                    editServicePlatformId = p.id
                                    val firstCat = categories.firstOrNull { it.platformId == p.id }
                                    if (firstCat != null) {
                                        editServiceCategoryId = firstCat.id
                                    }
                                },
                                label = { Text(p.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonMagenta.copy(alpha = 0.28f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Text(
                        text = "2. Select Category",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    if (categoriesForSelectedPlat.isEmpty()) {
                        Text(
                            text = "No categories for this platform yet. Create a category first.",
                            color = DangerRed,
                            fontSize = 11.sp
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(categoriesForSelectedPlat, key = { it.id }) { c ->
                                FilterChip(
                                    selected = editServiceCategoryId == c.id,
                                    onClick = { editServiceCategoryId = c.id },
                                    label = { Text(c.name, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary.copy(alpha = 0.25f),
                                        selectedLabelColor = GoldLight
                                    )
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editServiceId,
                            onValueChange = { editServiceId = it },
                            label = { Text("Service ID") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editServiceOrder,
                            onValueChange = { editServiceOrder = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Order #") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(0.7f)
                        )
                    }

                    OutlinedTextField(
                        value = editServiceName,
                        onValueChange = { editServiceName = it },
                        label = { Text("Service Name") },
                        singleLine = false,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editServicePrice,
                            onValueChange = { editServicePrice = it },
                            label = { Text("Price / 1000 (৳)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editServiceAvgTime,
                            onValueChange = { editServiceAvgTime = it },
                            label = { Text("Average Time") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editServiceMin,
                            onValueChange = { editServiceMin = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Min Quantity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editServiceMax,
                            onValueChange = { editServiceMax = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Max Quantity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = editServiceDesc,
                        onValueChange = { editServiceDesc = it },
                        label = { Text("Service Details / Instructions") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Requires Link Input", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Switch(
                            checked = editServiceRequiresLink,
                            onCheckedChange = { editServiceRequiresLink = it }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Service Enabled", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Switch(
                            checked = editServiceEnabled,
                            onCheckedChange = { editServiceEnabled = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedName = editServiceName.trim()
                        val price = editServicePrice.toDoubleOrNull()
                        val minQ = editServiceMin.toIntOrNull() ?: 100
                        val maxQ = editServiceMax.toIntOrNull() ?: 100000
                        if (trimmedName.isEmpty() || editServicePlatformId.isBlank() || editServiceCategoryId.isBlank() || price == null || price < 0.0) {
                            Toast.makeText(context, "Complete all required fields with valid price", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (minQ <= 0 || maxQ < minQ) {
                            Toast.makeText(context, "Max quantity must be >= Min quantity", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val now = System.currentTimeMillis()
                        val finalId = editServiceId.trim().ifEmpty { "srv_$now" }
                        val newService = SocialBoostService(
                            id = finalId,
                            platformId = editServicePlatformId,
                            categoryId = editServiceCategoryId,
                            name = trimmedName,
                            pricePerThousandBdt = price,
                            minQuantity = minQ,
                            maxQuantity = maxQ,
                            averageTime = editServiceAvgTime.trim().ifEmpty { "15 – 45 Minutes" },
                            description = editServiceDesc.trim(),
                            displayOrder = editServiceOrder.toIntOrNull() ?: (services.size + 1),
                            requiresLink = editServiceRequiresLink,
                            isEnabled = editServiceEnabled,
                            createdAt = if (editServiceCreatedAt > 0L) editServiceCreatedAt else now,
                            updatedAt = now
                        )
                        val exists = config.socialBoostServices.any { it.id == editServiceOriginalId }
                        val updatedList = if (exists) {
                            config.socialBoostServices.map { if (it.id == editServiceOriginalId) newService else it }
                        } else {
                            config.socialBoostServices + newService
                        }
                        onSaveConfig(config.copy(socialBoostServices = updatedList))
                        showServiceEditor = false
                        Toast.makeText(context, "Service saved & synced to Firebase", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color(0xFF090414))
                ) {
                    Text("Save Service", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showServiceEditor = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun AdminSectionTabCard(
    title: String,
    totalCount: Int,
    activeCount: Int,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.2f) else Color(0xFF0B0616),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) accentColor else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$activeCount / $totalCount Active",
                color = if (isSelected) accentColor else Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun EmptyAdminListNotice(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF120A24),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}

private fun movePlatformOrder(
    list: List<SocialBoostPlatform>,
    id: String,
    direction: Int
): List<SocialBoostPlatform> {
    val sorted = list.sortedBy { it.displayOrder }.toMutableList()
    val index = sorted.indexOfFirst { it.id == id }
    if (index == -1) return list
    val target = index + direction
    if (target !in sorted.indices) return list
    val temp = sorted[index]
    sorted[index] = sorted[target]
    sorted[target] = temp
    return sorted.mapIndexed { idx, item -> item.copy(displayOrder = idx + 1) }
}

private fun moveCategoryOrder(
    list: List<SocialBoostCategory>,
    id: String,
    direction: Int
): List<SocialBoostCategory> {
    val sorted = list.sortedBy { it.displayOrder }.toMutableList()
    val index = sorted.indexOfFirst { it.id == id }
    if (index == -1) return list
    val target = index + direction
    if (target !in sorted.indices) return list
    val temp = sorted[index]
    sorted[index] = sorted[target]
    sorted[target] = temp
    return sorted.mapIndexed { idx, item -> item.copy(displayOrder = idx + 1) }
}

private fun moveServiceOrder(
    list: List<SocialBoostService>,
    id: String,
    direction: Int
): List<SocialBoostService> {
    val sorted = list.sortedBy { it.displayOrder }.toMutableList()
    val index = sorted.indexOfFirst { it.id == id }
    if (index == -1) return list
    val target = index + direction
    if (target !in sorted.indices) return list
    val temp = sorted[index]
    sorted[index] = sorted[target]
    sorted[target] = temp
    return sorted.mapIndexed { idx, item -> item.copy(displayOrder = idx + 1) }
}
