package com.example.data.model

enum class AircraftStatus(val label: String) {
    IDLE("Idle in Hangar"),
    SCHEDULED("On Active Route"),
    IN_FLIGHT("Airborne"),
    MAINTENANCE("In Heavy Check")
}

data class OwnedAircraft(
    val id: String,
    val modelId: String,
    val registration: String,
    val customName: String,
    val isLeased: Boolean,
    val conditionPercent: Int = 100, // 0-100%
    val totalFlightHours: Double = 0.0,
    val status: AircraftStatus = AircraftStatus.IDLE,
    val assignedRouteId: String? = null,
    // Custom cabin config (can be customized from model baseline)
    val configuredEconomyPax: Int,
    val configuredBusinessPax: Int,
    val configuredFirstPax: Int
) {
    val needsMaintenance: Boolean get() = conditionPercent < 60
    val totalConfiguredPax: Int get() = configuredEconomyPax + configuredBusinessPax + configuredFirstPax
}
