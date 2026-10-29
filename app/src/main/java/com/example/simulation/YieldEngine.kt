package com.example.simulation

import com.example.data.model.AircraftModel
import com.example.data.model.Airport
import com.example.data.model.GameEvent
import com.example.data.model.OwnedAircraft
import kotlin.math.pow

object YieldEngine {

    /**
     * Recommended baseline fares based on flight distance
     */
    fun getBenchmarkEconomyFare(distanceKm: Int): Int {
        return (45 + distanceKm * 0.115).toInt().coerceAtLeast(35)
    }

    fun getBenchmarkBusinessFare(distanceKm: Int): Int {
        return (getBenchmarkEconomyFare(distanceKm) * 2.75).toInt()
    }

    fun getBenchmarkFirstFare(distanceKm: Int): Int {
        return (getBenchmarkEconomyFare(distanceKm) * 5.2).toInt()
    }

    fun getBenchmarkCargoRate(distanceKm: Int): Double {
        return (1.20 + distanceKm * 0.00075).coerceAtLeast(0.85)
    }

    data class FlightSettlement(
        val economyPaxBoarded: Int,
        val businessPaxBoarded: Int,
        val firstPaxBoarded: Int,
        val cargoTonnesBoarded: Double,
        val ticketRevenue: Long,
        val cargoRevenue: Long,
        val totalRevenue: Long,
        val operatingCost: Long,
        val netProfit: Long,
        val loadFactorPercent: Int
    )

    fun calculateFlightYield(
        distanceKm: Int,
        origin: Airport,
        dest: Airport,
        plane: OwnedAircraft,
        model: AircraftModel,
        ecoFare: Int,
        bizFare: Int,
        firstFare: Int,
        cargoRatePerKg: Double,
        events: List<GameEvent>,
        airlineReputation: Int
    ): FlightSettlement {
        val eventDemandMult = EventEngine.calculateEffectiveDemandMultiplier(events, origin.iata, dest.iata)
        val repMult = 0.70 + (airlineReputation / 100.0) * 0.50 // 0.70 to 1.20

        // Benchmark comparison
        val benchEco = getBenchmarkEconomyFare(distanceKm)
        val benchBiz = getBenchmarkBusinessFare(distanceKm)
        val benchFirst = getBenchmarkFirstFare(distanceKm)
        val benchCargo = getBenchmarkCargoRate(distanceKm)

        // Elasticity ratios
        val ecoRatio = (benchEco.toDouble() / ecoFare.coerceAtLeast(10)).coerceIn(0.4, 1.8)
        val bizRatio = (benchBiz.toDouble() / bizFare.coerceAtLeast(20)).coerceIn(0.4, 1.8)
        val firstRatio = (benchFirst.toDouble() / firstFare.coerceAtLeast(50)).coerceIn(0.4, 1.8)
        val cargoRatio = (benchCargo / cargoRatePerKg.coerceAtLeast(0.2)).coerceIn(0.4, 1.8)

        // Airport traffic potential
        val avgAirportDemand = (origin.passengerDemandIndex + dest.passengerDemandIndex) / 200.0 // 0.3 to 1.0

        val ecoDemandFactor = (ecoRatio * 0.85 * avgAirportDemand * repMult * eventDemandMult).coerceIn(0.2, 1.0)
        val bizDemandFactor = (bizRatio * 0.80 * avgAirportDemand * repMult * eventDemandMult).coerceIn(0.1, 1.0)
        val firstDemandFactor = (firstRatio * 0.75 * avgAirportDemand * repMult * eventDemandMult).coerceIn(0.05, 1.0)
        val cargoDemandFactor = (cargoRatio * 0.82 * ((origin.cargoDemandIndex + dest.cargoDemandIndex) / 200.0) * eventDemandMult).coerceIn(0.2, 1.0)

        val ecoPax = if (model.isFreighterOnly) 0 else (plane.configuredEconomyPax * ecoDemandFactor).toInt()
        val bizPax = if (model.isFreighterOnly) 0 else (plane.configuredBusinessPax * bizDemandFactor).toInt()
        val firstPax = if (model.isFreighterOnly) 0 else (plane.configuredFirstPax * firstDemandFactor).toInt()
        val cargoTonnes = (model.cargoCapacityTonnes * cargoDemandFactor)

        val ticketRev = (ecoPax * ecoFare.toLong()) + (bizPax * bizFare.toLong()) + (firstPax * firstFare.toLong())
        val cargoRev = (cargoTonnes * 1000 * cargoRatePerKg).toLong()
        val totalRev = ticketRev + cargoRev

        // Operating Costs: Airport gate turnaround fees + flight hour maintenance + crew
        val flightHours = distanceKm.toDouble() / model.cruisingSpeedKmh.coerceAtLeast(300)
        val gateTurnaroundFee = 1200L + (origin.tier.slotCapacity * 12L) + (dest.tier.slotCapacity * 12L)
        val baseHourlyMaintenance = model.hourlyMaintenanceCost * flightHours
        val crewCost = flightHours * 350.0

        // Apply dynamic event cost multiplier (e.g. Fuel Price Spike on older quad/tri-jets)
        val eventCostMult = EventEngine.calculateOperatingCostMultiplier(events, model.engineConfig.displayName)
        val totalOperatingCost = ((gateTurnaroundFee + baseHourlyMaintenance + crewCost) * eventCostMult).toLong()

        val netProfit = totalRev - totalOperatingCost
        val totalCapacity = plane.totalConfiguredPax
        val totalPax = ecoPax + bizPax + firstPax
        val loadFactor = if (totalCapacity > 0) ((totalPax.toDouble() / totalCapacity) * 100).toInt() else ((cargoTonnes / model.cargoCapacityTonnes.coerceAtLeast(1.0)) * 100).toInt()

        return FlightSettlement(
            economyPaxBoarded = ecoPax,
            businessPaxBoarded = bizPax,
            firstPaxBoarded = firstPax,
            cargoTonnesBoarded = cargoTonnes,
            ticketRevenue = ticketRev,
            cargoRevenue = cargoRev,
            totalRevenue = totalRev,
            operatingCost = totalOperatingCost,
            netProfit = netProfit,
            loadFactorPercent = loadFactor.coerceIn(0, 100)
        )
    }
}
