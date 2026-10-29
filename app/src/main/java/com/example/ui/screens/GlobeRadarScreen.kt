package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveFlight
import com.example.data.model.AirlineEmpire
import com.example.data.model.GameEvent
import com.example.data.repository.AircraftCatalog
import com.example.ui.components.DynamicEventNewsTicker
import com.example.ui.components.DynamicEventsModal
import com.example.ui.components.VectorGlobeCanvas
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GlobeRadarScreen(
    empire: AirlineEmpire,
    activeFlights: List<ActiveFlight>,
    events: List<GameEvent>,
    latestNews: String?,
    onSpeedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFlight by remember { mutableStateOf<ActiveFlight?>(null) }
    var showEventsModal by remember { mutableStateOf(false) }

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E17))
    ) {
        // 1. 3D Spherical Vector Globe Canvas
        VectorGlobeCanvas(
            activeFlights = activeFlights,
            selectedFlight = selectedFlight,
            onFlightSelected = { selectedFlight = it },
            onAirportSelected = { /* Airport tap */ },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Tactical Operations HUD Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter)
        ) {
            // Airline Title & Metrics Bar
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121826).copy(alpha = 0.92f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2B374E), RoundedCornerShape(12.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    // Airline Info
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = empire.airlineName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "[${empire.iataCode}]",
                                color = Color(0xFF00E5FF),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Day ${empire.simulationDay} • ${empire.simulationHour.toString().padStart(2, '0')}:00 UTC",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    // Cash Balance & Reputation
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currencyFormatter.format(empire.cashBalance),
                            color = if (empire.cashBalance >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rep: ${empire.reputationScore}%",
                                color = Color(0xFFF59E0B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${activeFlights.size} Airborne",
                                color = Color(0xFF00E5FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dynamic Events News Ticker
            DynamicEventNewsTicker(
                latestNews = latestNews,
                activeEventsCount = events.size,
                onClickEvents = { showEventsModal = true }
            )
        }

        // 3. Time Warp Speed Multiplier Floating Bar
        Surface(
            color = Color(0xFF121826).copy(alpha = 0.90f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .border(1.dp, Color(0xFF2B374E), RoundedCornerShape(24.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                // Pause
                IconButton(
                    onClick = { onSpeedChange(0) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = if (empire.isSimulationPaused) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
                // 1x Normal
                SpeedButton(
                    label = "1x",
                    isSelected = !empire.isSimulationPaused && empire.timeSpeedMultiplier == 1,
                    onClick = { onSpeedChange(1) }
                )
                // 2x Fast
                SpeedButton(
                    label = "2x",
                    isSelected = !empire.isSimulationPaused && empire.timeSpeedMultiplier == 2,
                    onClick = { onSpeedChange(2) }
                )
                // 5x Warp
                SpeedButton(
                    label = "5x",
                    isSelected = !empire.isSimulationPaused && empire.timeSpeedMultiplier == 5,
                    onClick = { onSpeedChange(5) }
                )
            }
        }

        // 4. Live Flight Telemetry Card (shown when an aircraft is airborne or tapped)
        val currentFlight = selectedFlight ?: activeFlights.firstOrNull()
        AnimatedVisibility(
            visible = currentFlight != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 16.dp, bottom = 135.dp)
        ) {
            if (currentFlight != null) {
                val model = AircraftCatalog.getById(currentFlight.aircraftModelId)

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121826).copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlightTakeoff,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Flight ${currentFlight.flightNumber}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = model?.name ?: "Jetliner",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = "${(currentFlight.progress * 100).toInt()}% EN ROUTE",
                                color = Color(0xFF10B981),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Flight Origin -> Destination Telemetry
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(text = currentFlight.originIata, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
                                Text(text = "Origin Hub", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${currentFlight.altitudeFt} ft • ${currentFlight.speedKmh} km/h", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                LinearProgressIndicator(
                                    progress = { currentFlight.progress },
                                    color = Color(0xFF00E5FF),
                                    trackColor = Color(0xFF1E293B),
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = currentFlight.destIata, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
                                Text(text = "Destination", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Payload & Revenue Metrics
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Pax: ${currentFlight.passengersOnboard} | Cargo: ${String.format(Locale.US, "%.1f", currentFlight.cargoTonnesOnboard)}t",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            val profit = currentFlight.expectedRevenue - currentFlight.operatingCost
                            Text(
                                text = "Est. Profit: +${currencyFormatter.format(profit)}",
                                color = if (profit >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Active World Events Dialog Modal
        if (showEventsModal) {
            DynamicEventsModal(
                events = events,
                onDismiss = { showEventsModal = false }
            )
        }
    }
}

@Composable
private fun SpeedButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF00E5FF) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(24.dp)
        ) {
            Text(
                text = label,
                color = if (isSelected) Color(0xFF032630) else Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
