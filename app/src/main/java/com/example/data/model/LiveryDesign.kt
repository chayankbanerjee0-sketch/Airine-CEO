package com.example.data.model

enum class CheatlineStyle(val label: String) {
    CLASSIC_STRIPE("Classic Stripe"),
    MODERN_SWOOP("Modern Swoop"),
    EUROWHITE_MINIMAL("Eurowhite Clean"),
    BILLBOARD_BOLD("Billboard Bold"),
    GRADIENT_TWILIGHT("Twilight Wave")
}

enum class TailLogoType(val label: String) {
    GOLDEN_WINGS("Golden Wings"),
    RADAR_STAR("Radar Star"),
    AERO_CHEVRON("Aero Chevron"),
    GLOBAL_COMPASS("Global Compass"),
    SPEED_BIRD("Speed Falcon")
}

data class LiveryDesign(
    val fuselageColor: Long = 0xFFF8FAFC, // Clean aerospace white
    val bellyColor: Long = 0xFF1E293B,    // Dark metallic navy belly
    val tailFinColor: Long = 0xFF0284C7,  // Vibrant cobalt blue tail
    val engineCowlingColor: Long = 0xFF0284C7,
    val cheatlineColor: Long = 0xFF00E5FF, // Radar cyan stripe
    val cheatlineStyle: CheatlineStyle = CheatlineStyle.MODERN_SWOOP,
    val tailLogo: TailLogoType = TailLogoType.GOLDEN_WINGS,
    val callsignSuffix: String = "AIR"
)
