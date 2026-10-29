package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AircraftCategory
import com.example.data.model.AircraftModel
import com.example.data.model.OwnedAircraft
import com.example.data.repository.AircraftCatalog
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FleetHangarScreen(
    fleet: List<OwnedAircraft>,
    playerCash: Long,
    onBuyAircraft: (modelId: String, name: String, asLease: Boolean) -> Boolean,
    onMaintenance: (planeId: String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Catalog (70+ planes), 1: Active Fleet
    var selectedCategoryFilter by remember { mutableStateOf<AircraftCategory?>(null) }
    var orderFeedbackMessage by remember { mutableStateOf<String?>(null) }

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E17))
            .padding(16.dp)
    ) {
        // Top Header
        Text(
            text = "Hangar & Fleet Procurement",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Over 70 authentic commercial aircraft • Zero fuel grind",
            color = Color(0xFF00E5FF),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Row: Catalog vs Owned
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF121826),
            contentColor = Color(0xFF00E5FF),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF00E5FF)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Aircraft Catalog (${AircraftCatalog.allAircraft.size})", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Active Fleet (${fleet.size})", fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (orderFeedbackMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF00E5FF).copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = orderFeedbackMessage!!, color = Color(0xFFA5F3FC), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (selectedTab == 0) {
            // Category Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All (70+)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00E5FF),
                            selectedLabelColor = Color(0xFF032630),
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFFCBD5E1)
                        )
                    )
                }
                items(AircraftCategory.entries.toTypedArray()) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00E5FF),
                            selectedLabelColor = Color(0xFF032630),
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Catalog List
            val filteredCatalog = remember(selectedCategoryFilter) {
                if (selectedCategoryFilter == null) AircraftCatalog.allAircraft
                else AircraftCatalog.allAircraft.filter { it.category == selectedCategoryFilter }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCatalog, key = { it.id }) { model ->
                    AircraftCatalogCard(
                        model = model,
                        playerCash = playerCash,
                        currencyFormatter = currencyFormatter,
                        onBuy = { asLease ->
                            val success = onBuyAircraft(model.id, model.name, asLease)
                            orderFeedbackMessage = if (success) {
                                if (asLease) "Successfully leased ${model.name} into active hangar!"
                                else "Purchased ${model.name}! Added to fleet."
                            } else {
                                "Insufficient cash balance for this transaction."
                            }
                        }
                    )
                }
            }
        } else {
            // Active Fleet List
            if (fleet.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No aircraft currently in hangar. Acquire one from the catalog above!",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(fleet, key = { it.id }) { plane ->
                        val model = AircraftCatalog.getById(plane.modelId)
                        OwnedAircraftCard(
                            plane = plane,
                            model = model,
                            currencyFormatter = currencyFormatter,
                            onMaintenance = {
                                val success = onMaintenance(plane.id)
                                orderFeedbackMessage = if (success) "A-Check maintenance overhaul completed for ${plane.registration}!"
                                else "Insufficient capital for heavy maintenance."
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AircraftCatalogCard(
    model: AircraftModel,
    playerCash: Long,
    currencyFormatter: NumberFormat,
    onBuy: (asLease: Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2B374E), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = model.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${model.manufacturer} • ${model.category.displayName}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = model.engineConfig.displayName, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Specs Grid
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SpecItem(title = "Range", value = "${model.rangeKm} km")
                SpecItem(title = "Speed", value = "${model.cruisingSpeedKmh} km/h")
                SpecItem(
                    title = if (model.isFreighterOnly) "Payload" else "Capacity",
                    value = if (model.isFreighterOnly) "${model.cargoCapacityTonnes}t Cargo" else "${model.totalMaxPax} Pax"
                )
                SpecItem(title = "Runway", value = "${model.runwayRequiredM} m")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = model.description, color = Color(0xFFCBD5E1), fontSize = 12.sp, lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Financial Options
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Lease Option
                OutlinedButton(
                    onClick = { onBuy(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Dry Lease", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${currencyFormatter.format(model.leasePricePerDay)}/day", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }
                // Purchase Option
                Button(
                    onClick = { onBuy(false) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF032630))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Buy Outright", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = currencyFormatter.format(model.purchasePrice), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OwnedAircraftCard(
    plane: OwnedAircraft,
    model: AircraftModel?,
    currencyFormatter: NumberFormat,
    onMaintenance: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2B374E), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = plane.customName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "[${plane.registration}]", color = Color(0xFF00E5FF), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                    Text(text = model?.name ?: "Commercial Airliner", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (plane.isLeased) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (plane.isLeased) "Leased" else "Owned",
                        color = if (plane.isLeased) Color(0xFFF59E0B) else Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Condition Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Airframe Health", color = Color(0xFF94A3B8), fontSize = 12.sp)
                Text(
                    text = "${plane.conditionPercent}%",
                    color = if (plane.conditionPercent > 70) Color(0xFF10B981) else Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { plane.conditionPercent / 100f },
                color = if (plane.conditionPercent > 70) Color(0xFF10B981) else Color(0xFFEF4444),
                trackColor = Color(0xFF1E293B),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Flight Hours: ${String.format(Locale.US, "%.1f", plane.totalFlightHours)} hrs",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp
                )
                if (plane.conditionPercent < 90) {
                    Button(
                        onClick = onMaintenance,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF451A03))
                    ) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "A-Check Service", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SpecItem(title: String, value: String) {
    Column {
        Text(text = title, color = Color(0xFF64748B), fontSize = 10.sp)
        Text(text = value, color = Color(0xFFE2E8F0), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
