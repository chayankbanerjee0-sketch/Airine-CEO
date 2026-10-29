package com.example.data.model

enum class GameEventType(val label: String, val badgeColorHex: Long) {
    GLOBAL_ECONOMIC_BOOM("Economic Boom", 0xFF10B981),
    FUEL_PRICE_SPIKE("Fuel Cost Spike", 0xFFF59E0B),
    AIRPORT_EXPANSION("Airport Works", 0xFFEF4444),
    TOURISM_SURGE("Tourism Surge", 0xFF38BDF8),
    TECH_SUMMIT("Global Tech Expo", 0xFF8B5CF6),
    VOLCANIC_ASH("Airspace Advisory", 0xFF64748B),
    MANUFACTURER_REBATE("Fleet Subsidy", 0xFF00E5FF)
}

data class GameEvent(
    val id: String,
    val type: GameEventType,
    val title: String,
    val headline: String,
    val description: String,
    val totalDurationDays: Int,
    val durationDaysRemaining: Int,
    val affectedAirportIata: String? = null,
    val demandMultiplier: Double = 1.0,
    val quadJetCostMultiplier: Double = 1.0,
    val triJetCostMultiplier: Double = 1.0,
    val twinJetCostMultiplier: Double = 1.0,
    val turbopropCostMultiplier: Double = 1.0,
    val airportCapacityMultiplier: Double = 1.0,
    val aircraftPurchaseDiscount: Double = 0.0
) {
    val isExpired: Boolean get() = durationDaysRemaining <= 0
    val progressPercent: Float get() = (totalDurationDays - durationDaysRemaining).toFloat() / totalDurationDays.coerceAtLeast(1)
}
