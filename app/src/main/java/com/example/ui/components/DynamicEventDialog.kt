package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameEvent
import com.example.data.model.GameEventType

@Composable
fun DynamicEventNewsTicker(
    latestNews: String?,
    activeEventsCount: Int,
    onClickEvents: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (latestNews == null && activeEventsCount == 0) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F1A2A))
            .clickable { onClickEvents() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = latestNews ?: "Global Aviation Market: Stable Operations",
                    color = Color(0xFFA5F3FC),
                    fontSize = 12.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium
                )
            }
            if (activeEventsCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$activeEventsCount Active Events",
                        color = Color(0xFFF59E0B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DynamicEventsModal(
    events: List<GameEvent>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121826),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Global Aviation Events",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            if (events.isEmpty()) {
                Text(
                    text = "No active world disruptions or economic events at this time. All global flight routes operating normally.",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(events) { event ->
                        EventCard(event)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss Briefing", color = Color(0xFF00E5FF))
            }
        }
    )
}

@Composable
fun EventCard(event: GameEvent) {
    val badgeColor = Color(event.type.badgeColorHex)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2234)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = event.type.label,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${event.durationDaysRemaining} days remaining",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = event.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.description,
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Display active modifiers
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (event.demandMultiplier != 1.0) {
                    val isPos = event.demandMultiplier > 1.0
                    val pct = ((event.demandMultiplier - 1.0) * 100).toInt()
                    ModifierBadge(
                        label = "Passenger Demand ${if (isPos) "+$pct%" else "$pct%"}",
                        isPositive = isPos
                    )
                }
                if (event.quadJetCostMultiplier != 1.0) {
                    val surcharge = ((event.quadJetCostMultiplier - 1.0) * 100).toInt()
                    ModifierBadge(
                        label = "Quad-Jet Cost +$surcharge%",
                        isPositive = false
                    )
                }
                if (event.airportCapacityMultiplier != 1.0) {
                    val capRed = ((1.0 - event.airportCapacityMultiplier) * 100).toInt()
                    ModifierBadge(
                        label = "${event.affectedAirportIata} Slots -$capRed%",
                        isPositive = false
                    )
                }
                if (event.aircraftPurchaseDiscount > 0.0) {
                    val disc = (event.aircraftPurchaseDiscount * 100).toInt()
                    ModifierBadge(
                        label = "New Orders -$disc%",
                        isPositive = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { event.progressPercent },
                color = badgeColor,
                trackColor = Color(0xFF2A374F),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun ModifierBadge(label: String, isPositive: Boolean) {
    val bg = if (isPositive) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
    val fg = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = label, color = fg, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
