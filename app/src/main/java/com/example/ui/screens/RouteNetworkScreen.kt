package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.AirlineRoute
import com.example.data.model.Airport
import com.example.data.model.OwnedAircraft
import com.example.data.repository.AircraftCatalog
import com.example.data.repository.AirportDatabase
import com.example.simulation.FlightMath
import com.example.simulation.YieldEngine
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RouteNetworkScreen(
    routes: List<AirlineRoute>,
    fleet: List<OwnedAircraft>,
    playerHubIata: String,
    onCreateRoute: (originIata: String, destIata: String, planeId: String?, frequencies: Int) -> Boolean,
    onDeleteRoute: (routeId: String) -> Unit,
    onUpdateRoutePricing: (routeId: String, eco: Int, biz: Int, first: Int, cargo: Double, freq: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var routeToEdit by remember { mutableStateOf<AirlineRoute?>(null) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E17))
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Global Route Network",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Network Hub: $playerHubIata • ${routes.size} Active Corridors",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF032630))
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "New Route", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (routes.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        tint = Color(0xFF334155),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No routes currently established.",
                        color = Color(0xFF94A3B8),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap 'New Route' to connect your hub to over 2,230 airports worldwide!",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(routes, key = { it.id }) { route ->
                    val plane = fleet.find { it.id == route.assignedAircraftId }
                    val model = plane?.let { AircraftCatalog.getById(it.modelId) }

                    RouteCard(
                        route = route,
                        plane = plane,
                        currencyFormatter = currencyFormatter,
                        onEdit = { routeToEdit = route },
                        onDelete = { onDeleteRoute(route.id) }
                    )
                }
            }
        }

        // New Route Modal
        if (showCreateDialog) {
            NewRouteDialog(
                fleet = fleet,
                playerHubIata = playerHubIata,
                onDismiss = { showCreateDialog = false },
                onConfirm = { origin, dest, planeId, freq ->
                    val success = onCreateRoute(origin, dest, planeId, freq)
                    if (success) showCreateDialog = false
                }
            )
        }

        // Edit Pricing Modal
        if (routeToEdit != null) {
            EditPricingDialog(
                route = routeToEdit!!,
                onDismiss = { routeToEdit = null },
                onSave = { eco, biz, first, cargo, freq ->
                    onUpdateRoutePricing(routeToEdit!!.id, eco, biz, first, cargo, freq)
                    routeToEdit = null
                }
            )
        }
    }
}

@Composable
fun RouteCard(
    route: AirlineRoute,
    plane: OwnedAircraft?,
    currencyFormatter: NumberFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2B374E), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Origin -> Destination Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = route.originIata, color = Color(0xFF00E5FF), fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.FlightTakeoff, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = route.destIata, color = Color(0xFF00E5FF), fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "${route.distanceKm} km", color = Color(0xFF64748B), fontSize = 12.sp)
                }

                Row {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { onEdit() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Pricing", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                            .clickable { onDelete() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Close", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Assigned Aircraft & Frequencies
            Text(
                text = "Aircraft: ${plane?.customName ?: "None Assigned"} (${plane?.registration ?: "Unscheduled"}) • ${route.dailyFrequencies}x Daily",
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Yield & Financial Summary
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "Eco Fare: \$${route.economyFare}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(text = "Biz Fare: \$${route.businessFare}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
                Column {
                    Text(text = "Cargo: \$${String.format(Locale.US, "%.2f", route.cargoRatePerKg)}/kg", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(text = "Total Pax: ${route.totalPassengersCarried}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Net Flight Profit",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "+${currencyFormatter.format(route.netProfit)}",
                        color = if (route.netProfit >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NewRouteDialog(
    fleet: List<OwnedAircraft>,
    playerHubIata: String,
    onDismiss: () -> Unit,
    onConfirm: (origin: String, dest: String, planeId: String?, freq: Int) -> Unit
) {
    var originText by remember { mutableStateOf(playerHubIata) }
    var destText by remember { mutableStateOf("") }
    var selectedPlaneId by remember { mutableStateOf<String?>(fleet.firstOrNull()?.id) }
    var dailyFreq by remember { mutableIntStateOf(2) }

    val majorAirports = remember { AirportDatabase.getMajorAirports() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121826),
        title = {
            Text(text = "Inaugurate New Route", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = originText,
                    onValueChange = { originText = it.uppercase().take(3) },
                    label = { Text("Origin IATA (e.g. JFK)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF2B374E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = destText,
                    onValueChange = { destText = it.uppercase().take(3) },
                    label = { Text("Destination IATA (e.g. LHR, HND, ORD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF2B374E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick airport suggestion buttons
                Text(text = "Popular Gateway Destinations:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("LHR", "CDG", "HND", "LAX", "DXB").forEach { iata ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                                .clickable { destText = iata }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = iata, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Daily Frequencies: ${dailyFreq}x", color = Color.White, fontSize = 12.sp)
                Slider(
                    value = dailyFreq.toFloat(),
                    onValueChange = { dailyFreq = it.toInt() },
                    valueRange = 1f..6f,
                    steps = 4,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (originText.isNotBlank() && destText.isNotBlank()) {
                        onConfirm(originText, destText, selectedPlaneId, dailyFreq)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF032630))
            ) {
                Text("Launch Route", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}

@Composable
fun EditPricingDialog(
    route: AirlineRoute,
    onDismiss: () -> Unit,
    onSave: (eco: Int, biz: Int, first: Int, cargo: Double, freq: Int) -> Unit
) {
    var ecoFare by remember { mutableFloatStateOf(route.economyFare.toFloat()) }
    var bizFare by remember { mutableFloatStateOf(route.businessFare.toFloat()) }
    var firstFare by remember { mutableFloatStateOf(route.firstFare.toFloat()) }
    var cargoRate by remember { mutableFloatStateOf(route.cargoRatePerKg.toFloat()) }
    var freq by remember { mutableIntStateOf(route.dailyFrequencies) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121826),
        title = {
            Text(text = "Yield Management: ${route.originIata} ↔ ${route.destIata}", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Economy Ticket: \$${ecoFare.toInt()}", color = Color.White, fontSize = 13.sp)
                Slider(
                    value = ecoFare,
                    onValueChange = { ecoFare = it },
                    valueRange = 30f..800f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                )

                Text(text = "Business Class: \$${bizFare.toInt()}", color = Color.White, fontSize = 13.sp)
                Slider(
                    value = bizFare,
                    onValueChange = { bizFare = it },
                    valueRange = 100f..2500f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8))
                )

                Text(text = "Air Freight Rate: \$${String.format(Locale.US, "%.2f", cargoRate)}/kg", color = Color.White, fontSize = 13.sp)
                Slider(
                    value = cargoRate,
                    onValueChange = { cargoRate = it },
                    valueRange = 0.5f..8.0f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFF59E0B), activeTrackColor = Color(0xFFF59E0B))
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(ecoFare.toInt(), bizFare.toInt(), firstFare.toInt(), cargoRate.toDouble(), freq) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF032630))
            ) {
                Text("Apply Fares", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}
