package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AirlineCeoColorScheme = darkColorScheme(
    primary = RadarCyan,
    onPrimary = Color(0xFF032630),
    primaryContainer = Color(0xFF0E3E4B),
    onPrimaryContainer = Color(0xFFA5F3FC),
    secondary = SkyBlue,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF1E3A5F),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = GoldExecutive,
    onTertiary = Color(0xFF451A03),
    background = StratosphereDark,
    onBackground = TextLight,
    surface = AvionicsSurface,
    onSurface = TextLight,
    surfaceVariant = AvionicsCard,
    onSurfaceVariant = TextMuted,
    outline = CockpitBorder,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Use our signature aviation theme for immersion
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AirlineCeoColorScheme,
        typography = Typography,
        content = content
    )
}
