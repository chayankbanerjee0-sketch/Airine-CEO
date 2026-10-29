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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyPreset

@Composable
fun ScenarioSetupScreen(
    canCancel: Boolean,
    onCancel: () -> Unit,
    onStartGame: (name: String, iata: String, icao: String, hub: String, preset: DifficultyPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    var airlineName by remember { mutableStateOf("AeroGlobal") }
    var iataCode by remember { mutableStateOf("AG") }
    var icaoCode by remember { mutableStateOf("AGL") }
    var selectedHub by remember { mutableStateOf("JFK") }
    var selectedPreset by remember { mutableStateOf(DifficultyPreset.MODERN_STARTUP) }

    val majorHubs = listOf("JFK", "LHR", "HND", "DXB", "CDG", "SIN", "FRA", "SYD")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E17))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (canCancel) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Header
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "Airline CEO: Empire Setup",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose your starting difficulty, global hub, and brand identity",
                color = Color(0xFF00E5FF),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Airline Details Input
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF2B374E), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = airlineName,
                    onValueChange = { airlineName = it },
                    label = { Text("Airline Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF2B374E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = iataCode,
                        onValueChange = { iataCode = it.uppercase().take(2) },
                        label = { Text("IATA Code (2-letter)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF2B374E)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = icaoCode,
                        onValueChange = { icaoCode = it.uppercase().take(3) },
                        label = { Text("ICAO Code (3-letter)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF2B374E)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Home Hub Selection
                Text(text = "Primary Network Mega-Hub:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    majorHubs.take(5).forEach { hub ->
                        val isSelected = selectedHub == hub
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF00E5FF) else Color(0xFF1E293B))
                                .clickable { selectedHub = hub }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = hub,
                                color = if (isSelected) Color(0xFF032630) else Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Scenario & Difficulty Presets
        Text(text = "Select Starting Scenario", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DifficultyPreset.entries.forEach { preset ->
                val isSelected = selectedPreset == preset
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF16253B) else Color(0xFF121826)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPreset = preset }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF2B374E),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = preset.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "\$${preset.startingCash / 1_000_000}M Capital",
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = preset.subtitle, color = Color(0xFF00E5FF), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = preset.description, color = Color(0xFF94A3B8), fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Launch Button
        Button(
            onClick = {
                onStartGame(
                    airlineName.ifBlank { "AeroGlobal" },
                    iataCode.ifBlank { "AG" },
                    icaoCode.ifBlank { "AGL" },
                    selectedHub,
                    selectedPreset
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF032630)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Launch Airline Empire", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
