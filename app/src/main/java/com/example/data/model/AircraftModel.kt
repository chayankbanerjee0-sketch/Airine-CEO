package com.example.data.model

enum class AircraftCategory(val displayName: String) {
    TURBOPROP("Regional Turboprop"),
    REGIONAL_JET("Regional Jet"),
    NARROWBODY("Narrowbody"),
    LONG_RANGE_NARROWBODY("LR Narrowbody"),
    WIDEBODY("Widebody"),
    TRI_JET("Historic Tri-Jet"),
    FREIGHTER("Dedicated Freighter")
}

enum class EngineConfiguration(val displayName: String, val baseFuelEfficiencyRating: String) {
    TURBOPROP("Twin Turboprop", "Eco Efficient"),
    TWIN_JET("Twin Jet", "Modern Standard"),
    TRI_JET("Tri Jet", "Heavy Classic"),
    QUAD_JET("Quad Jet", "Super Heavy")
}

data class AircraftModel(
    val id: String,
    val name: String,
    val manufacturer: String,
    val category: AircraftCategory,
    val engineConfig: EngineConfiguration,
    val rangeKm: Int,
    val cruisingSpeedKmh: Int,
    val maxEconomyPax: Int,
    val maxBusinessPax: Int,
    val maxFirstPax: Int,
    val cargoCapacityTonnes: Double,
    val purchasePrice: Long,
    val leasePricePerDay: Long,
    val hourlyMaintenanceCost: Int,
    val runwayRequiredM: Int,
    val isFreighterOnly: Boolean = false,
    val description: String = ""
) {
    val totalMaxPax: Int get() = maxEconomyPax + maxBusinessPax + maxFirstPax
}
