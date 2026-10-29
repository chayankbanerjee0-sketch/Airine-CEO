package com.example.data.model

enum class AirportTier(val label: String, val slotCapacity: Int) {
    MEGA_HUB("Global Mega Hub", 180),
    MAJOR_INTL("Major International", 120),
    REGIONAL("Regional Gateway", 60),
    DOMESTIC("Domestic Hub", 30)
}

data class Airport(
    val iata: String,
    val icao: String,
    val name: String,
    val city: String,
    val country: String,
    val region: String, // North America, Europe, Asia-Pacific, Latin America, Middle East, Africa, Oceania
    val latitude: Double,
    val longitude: Double,
    val runwayLengthM: Int,
    val tier: AirportTier,
    val passengerDemandIndex: Int, // 1 to 100
    val cargoDemandIndex: Int      // 1 to 100
)
