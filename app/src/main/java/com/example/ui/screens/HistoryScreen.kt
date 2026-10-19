package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderRecord
import com.example.ui.components.ChametReferenceSquircle
import com.example.ui.components.EpayReferenceSquircle
import com.example.ui.components.ExchangeReferenceSquircle
import com.example.ui.components.FreeFireGamingSquircle
import com.example.ui.components.TikTokReferenceSquircle
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    orders: List<OrderRecord>,
    onOrderClick: (OrderRecord) -> Unit,
    onClearAllOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showClearDialog by remember { mutableStateOf(false) }

    val filteredOrders = orders.filter { order ->
        val matchesSearch = searchQuery.isBlank() ||
                order.orderNumber.contains(searchQuery, ignoreCase = true) ||
                order.userAccountId.contains(searchQuery, ignoreCase = true) ||
                order.itemName.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Processing" -> order.status == "Processing"
            "Completed" -> order.status == "Completed"
            else -> true
        }

        matchesSearch && matchesFilter
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Orders?", color = Color.White) },
            text = { Text("Are you sure you want to remove all saved order records?", color = Color(0xFFD1D5DB)) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllOrders()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = GoldPrimary)
                }
            },
            containerColor = Color(0xFF1E1032)
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Luxury Neon Glassmorphism Header Card
        item {
            val headerShape = RoundedCornerShape(20.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 14.dp,
                        shape = headerShape,
                        spotColor = Color(0xFFA855F7).copy(alpha = 0.45f)
                    )
                    .clip(headerShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF230F42),
                                Color(0xFF130928),
                                Color(0xFF0A0F24)
                            )
                        )
                    )
                    .border(
                        width = 1.3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                GoldPrimary.copy(alpha = 0.85f),
                                Color(0xFFA855F7).copy(alpha = 0.70f),
                                Color(0xFF00F2FE).copy(alpha = 0.65f)
                            )
                        ),
                        shape = headerShape
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ExchangeReferenceSquircle(size = 44.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "অর্ডার হিস্টোরি • ORDER HISTORY",
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "মোট অর্ডার: ${orders.size} টি • Real-Time Cloud Sync",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldLight
                            )
                        }
                    }

                    if (orders.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by Order ID, UID or Package...", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0xFF3B1D68),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF120924),
                    unfocusedContainerColor = Color(0xFF120924)
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Processing", "Completed").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedFilter = filter },
                        color = if (isSelected) GoldPrimary else Color(0xFF150A2A),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.1.dp,
                            if (isSelected) GoldPrimary else Color(0xFF3B1D68)
                        )
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Empty state
        if (filteredOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ExchangeReferenceSquircle(size = 56.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোনো অর্ডার পাওয়া যায়নি",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "আপনার নতুন অর্ডার এখানে স্বয়ংক্রিয়ভাবে সেভ হবে।",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        } else {
            items(filteredOrders, key = { it.id }) { order ->
                OrderHistoryCard(order = order, onClick = { onOrderClick(order) })
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(
    order: OrderRecord,
    onClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = sdf.format(Date(order.timestamp))

    val statusColor = when (order.status) {
        "Completed" -> SuccessGreen
        "Processing" -> WarningYellow
        "Cancelled" -> Color(0xFFEF4444)
        else -> Color(0xFF60A5FA)
    }

    val statusDisplay = when (order.status) {
        "Completed" -> "✅ Approved"
        "Processing" -> "⏳ Processing"
        "Cancelled" -> "❌ ভুল TrxID"
        else -> order.status
    }

    val catLower = (order.category + " " + order.itemName).lowercase(Locale.US)
    val cardShape = RoundedCornerShape(18.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = cardShape,
                spotColor = Color(0xFFA855F7).copy(alpha = 0.35f)
            )
            .clip(cardShape)
            .border(
                width = 1.1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFA855F7).copy(alpha = 0.65f),
                        Color(0xFF3B82F6).copy(alpha = 0.45f),
                        GoldPrimary.copy(alpha = 0.55f)
                    )
                ),
                shape = cardShape
            )
            .clickable { onClick() },
        color = Color(0xFF120924).copy(alpha = 0.94f),
        shape = cardShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1D0E38).copy(alpha = 0.70f),
                            Color(0xFF0E071D).copy(alpha = 0.92f)
                        )
                    )
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                catLower.contains("tiktok") -> TikTokReferenceSquircle(size = 42.dp)
                catLower.contains("chamet") -> ChametReferenceSquircle(size = 42.dp)
                catLower.contains("epay") -> EpayReferenceSquircle(size = 42.dp)
                catLower.contains("free") || catLower.contains("fire") -> FreeFireGamingSquircle(size = 42.dp)
                else -> ExchangeReferenceSquircle(size = 42.dp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderNumber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.18f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, statusColor.copy(alpha = 0.65f))
                    ) {
                        Text(
                            text = statusDisplay,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${order.itemName} • ${order.amountOrQuantity}",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "ID: ${order.userAccountId} • $dateStr",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "৳${String.format(Locale.US, "%,.0f", order.priceBdt)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF7C3AED).copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(0.7.dp, Color(0xFFA855F7).copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "View Slip ➔",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
