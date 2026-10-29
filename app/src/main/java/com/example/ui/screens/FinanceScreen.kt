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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.AirlineEmpire
import com.example.data.repository.FinancialLedgerEntry
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinanceScreen(
    empire: AirlineEmpire,
    ledger: List<FinancialLedgerEntry>,
    onTakeLoan: (Long) -> Boolean,
    onRepayLoan: (Long) -> Boolean,
    onOpenNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loanFeedback by remember { mutableStateOf<String?>(null) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E17))
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Executive & Financial Deck",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${empire.airlineName} • Scenario: ${empire.difficulty.title}",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp
                )
            }
            OutlinedButton(
                onClick = onOpenNewGame,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B))
            ) {
                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Empire", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Balance & Valuation Overview
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MetricCard(
                title = "Treasury Cash",
                value = currencyFormatter.format(empire.cashBalance),
                accentColor = if (empire.cashBalance >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Outstanding Debt",
                value = currencyFormatter.format(empire.totalLoansOutstanding),
                accentColor = Color(0xFFEF4444),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MetricCard(
                title = "Passengers Flown",
                value = NumberFormat.getNumberInstance().format(empire.totalPassengersTransported),
                accentColor = Color(0xFF00E5FF),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Air Freight Shipped",
                value = "${String.format(Locale.US, "%.1f", empire.totalCargoTransportedTonnes)} Tonnes",
                accentColor = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Commercial Banking Credit Facilities
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Aviation Credit Line", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text(
                        text = "Interest: ${String.format(Locale.US, "%.1f", empire.difficulty.loanInterestRate * 100)}% APR",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val success = onTakeLoan(10_000_000L)
                            loanFeedback = if (success) "Drew \$10,000,000 from credit line." else "Credit line limit reached."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color(0xFF00E5FF)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "+$10M Loan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val success = onTakeLoan(25_000_000L)
                            loanFeedback = if (success) "Drew \$25,000,000 from credit line." else "Credit line limit reached."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color(0xFF00E5FF)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "+$25M Loan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val success = onRepayLoan(10_000_000L)
                            loanFeedback = if (success) "Repaid \$10,000,000 principal." else "Insufficient balance or no debt."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981).copy(alpha = 0.2f), contentColor = Color(0xFF10B981)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Repay $10M", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                loanFeedback?.let { feedback ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = feedback, color = Color(0xFFA5F3FC), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Financial Ledger List
        Text(text = "Recent Operations Ledger", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(ledger) { entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF121826))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = entry.description, color = Color.White, fontSize = 12.sp, maxLines = 1)
                        Text(text = "Day ${entry.day} • ${entry.category}", color = Color(0xFF64748B), fontSize = 10.sp)
                    }
                    Text(
                        text = if (entry.isIncome) "+${currencyFormatter.format(entry.amount)}" else "-${currencyFormatter.format(entry.amount)}",
                        color = if (entry.isIncome) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, Color(0xFF2B374E), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
