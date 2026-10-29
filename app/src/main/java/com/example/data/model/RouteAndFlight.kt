package com.example.data.model

data class AirlineRoute(
    val id: String,
    val originIata: String,
    val destIata: String,
    val distanceKm: Int,
    val assignedAircraftId: String?,
    val dailyFrequencies: Int = 2, // Frequencies per day (1 to 6)
    val economyFare: Int,
    val businessFare: Int,
    val firstFare: Int,
    val cargoRatePerKg: Double, // in USD per kg
    val estimatedLoadFactorPercent: Int = 85,
    val lastFlightRevenue: Long = 0,
    val lastFlightOperatingCost: Long = 0,
    val totalPassengersCarried: Long = 0,
    val totalCargoCarriedTonnes: Double = 0.0,
    val isSuspended: Boolean = false
) {
    val netFlightProfit: Long get() = lastFlightRevenue - lastFlightOperatingCost
    val netProfit: Long get() = netFlightProfit
    val destinationIata: String get() = destIata
}

data class ActiveFlight(
    val flightNumber: String,
    val routeId: String,
    val aircraftId: String,
    val aircraftModelId: String,
    val originIata: String,
    val destIata: String,
    val originLat: Double,
    val originLon: Double,
    val destLat: Double,
    val destLon: Double,
    val progress: Float, // 0.0 to 1.0
    val currentLat: Double,
    val currentLon: Double,
    val speedKmh: Int,
    val altitudeFt: Int,
    val passengersOnboard: Int,
    val cargoTonnesOnboard: Double,
    val expectedRevenue: Long,
    val operatingCost: Long,
    val headingDegrees: Float
) {
    val destinationIata: String get() = destIata
}
