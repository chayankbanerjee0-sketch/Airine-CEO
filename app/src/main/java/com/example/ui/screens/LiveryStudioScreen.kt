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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.CheatlineStyle
import com.example.data.model.LiveryDesign
import com.example.data.model.TailLogoType
import com.example.ui.components.LiveryPainterCanvas

@Composable
fun LiveryStudioScreen(
    currentLivery: LiveryDesign,
    airlineName: String,
    onSaveLivery: (LiveryDesign) -> Unit,
    modifier: Modifier = Modifier
) {
    var livery by remember { mutableStateOf(currentLivery) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    val fuselageColors = listOf(
        0xFFF8FAFC to "Aerospace White",
        0xFFE2E8F0 to "Titanium Silver",
        0xFF0F172A to "Midnight Dark",
        0xFF1E3A8A to "Empire Navy",
        0xFFFEF3C7 to "Champagne Pearl",
        0xFFFFEDD5 to "Sunset Glow"
    )

    val tailColors = listOf(
        0xFF0284C7 to "Cobalt Blue",
        0xFF10B981 to "Emerald Green",
        0xFFEF4444 to "Crimson Red",
        0xFFF59E0B to "Aviation Gold",
        0xFF8B5CF6 to "Imperial Violet",
        0xFF00E5FF to "Radar Cyan"
    )

    val cheatlineColors = listOf(
        0xFF00E5FF to "Radar Cyan",
        0xFFF59E0B to "Goldenrod",
        0xFFEF4444 to "Racing Red",
        0xFF10B981 to "Jade Green",
        0xFFFFFFFF to "Pure White"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E17))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Livery & Branding Studio",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Design bespoke exterior paint schemes for your fleet",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = {
                    onSaveLivery(livery)
                    saveMessage = "Custom livery applied across your entire fleet!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF032630))
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Livery", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        saveMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = msg, color = Color(0xFFA7F3D0), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 1. Live 2D Aircraft Livery Preview Canvas
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(1.dp, Color(0xFF2B374E), RoundedCornerShape(14.dp))
        ) {
            LiveryPainterCanvas(
                livery = livery,
                airlineName = airlineName,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Fuselage Base Color Palette
        Text(text = "Fuselage Base Coating", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(fuselageColors) { (colorHex, name) ->
                ColorSwatch(
                    color = Color(colorHex),
                    label = name,
                    isSelected = livery.fuselageColor == colorHex,
                    onClick = { livery = livery.copy(fuselageColor = colorHex) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Vertical Tail Fin Color
        Text(text = "Tail Fin & Cowling Accent", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tailColors) { (colorHex, name) ->
                ColorSwatch(
                    color = Color(colorHex),
                    label = name,
                    isSelected = livery.tailFinColor == colorHex,
                    onClick = {
                        livery = livery.copy(
                            tailFinColor = colorHex,
                            engineCowlingColor = colorHex
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Cheatline Accent Color
        Text(text = "Fuselage Stripe Color", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(cheatlineColors) { (colorHex, name) ->
                ColorSwatch(
                    color = Color(colorHex),
                    label = name,
                    isSelected = livery.cheatlineColor == colorHex,
                    onClick = { livery = livery.copy(cheatlineColor = colorHex) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Cheatline Pattern Style
        Text(text = "Cheatline Pattern Style", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CheatlineStyle.entries.toTypedArray()) { style ->
                FilterChip(
                    selected = livery.cheatlineStyle == style,
                    onClick = { livery = livery.copy(cheatlineStyle = style) },
                    label = { Text(style.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00E5FF),
                        selectedLabelColor = Color(0xFF032630),
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFCBD5E1)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Tail Logo Insignia
        Text(text = "Tail Fin Insignia & Decal", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(TailLogoType.entries.toTypedArray()) { logo ->
                FilterChip(
                    selected = livery.tailLogo == logo,
                    onClick = { livery = livery.copy(tailLogo = logo) },
                    label = { Text(logo.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF59E0B),
                        selectedLabelColor = Color(0xFF451A03),
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFCBD5E1)
                    )
                )
            }
        }
    }
}

@Composable
fun ColorSwatch(
    color: Color,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF475569),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (color == Color.White) Color.Black else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp)
    }
}
