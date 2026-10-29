package com.example.simulation

import com.example.data.model.GameEvent
import com.example.data.model.GameEventType
import kotlin.random.Random

object EventEngine {

    private val eventTemplates = listOf(
        { id: String, airportIata: String ->
            GameEvent(
                id = id,
                type = GameEventType.GLOBAL_ECONOMIC_BOOM,
                title = "Global Economic Boom",
                headline = "Surging Global Markets Spark Unprecedented Passenger & Freight Demand",
                description = "Consumer confidence reaches record highs worldwide. Corporate business travel and air cargo shipments are flourishing across all regional and intercontinental sectors.",
                totalDurationDays = 14,
                durationDaysRemaining = 14,
                demandMultiplier = 1.35,
                quadJetCostMultiplier = 1.0,
                twinJetCostMultiplier = 1.0
            )
        },
        { id: String, airportIata: String ->
            GameEvent(
                id = id,
                type = GameEventType.FUEL_PRICE_SPIKE,
                title = "Global Fuel Price Surge",
                headline = "Geopolitical Tensions Drive Crude Oil Surges Across Refineries",
                description = "While direct refueling is handled by ground handlers, operational fuel cost surcharges increase sharply—especially penalizing older quad-engine and tri-jet aircraft. Next-gen composite twins retain a major competitive edge.",
                totalDurationDays = 10,
                durationDaysRemaining = 10,
                quadJetCostMultiplier = 1.45,
                triJetCostMultiplier = 1.30,
                twinJetCostMultiplier = 1.08,
                turbopropCostMultiplier = 1.02
            )
        },
        { id: String, airportIata: String ->
            GameEvent(
                id = id,
                type = GameEventType.AIRPORT_EXPANSION,
                title = "Hub Expansion at $airportIata",
                headline = "Major Terminal & Runway Renovation Underway at $airportIata",
                description = "Civil aviation authorities have commenced taxiway widening and terminal apron redevelopment. Gate slot capacity is reduced by 40% during construction, causing scheduling pressure.",
                totalDurationDays = 12,
                durationDaysRemaining = 12,
                affectedAirportIata = airportIata,
                airportCapacityMultiplier = 0.60
            )
        },
        { id: String, airportIata: String ->
            GameEvent(
                id = id,
                type = GameEventType.TOURISM_SURGE,
                title = "Summer Holiday Travel Surge",
                headline = "Vacation Travel Fever Drives Record Leisure Bookings",
                description = "Beach, tropical, and cultural holiday destinations are experiencing intense consumer demand. Economy passenger load factors surge significantly.",
                totalDurationDays = 9,
                durationDaysRemaining = 9,
                demandMultiplier = 1.25
            )
        },
        { id: String, airportIata: String ->
            GameEvent(
                id = id,
                type = GameEventType.TECH_SUMMIT,
                title = "World AI & Aviation Summit at $airportIata",
                headline = "Global Executives & Tech Leaders Converge on $airportIata",
                description = "Over 150,000 corporate delegates and venture executives are flying in for the premier annual tech convention. Business and First Class seat demand surges drastically.",
                totalDurationDays = 7,
                durationDaysRemaining = 7,
                affectedAirportIata = airportIata,
                demandMultiplier = 1.50
            )
        },
        { id: String, airportIata: String ->
            GameEvent(
                id = id,
                type = GameEventType.MANUFACTURER_REBATE,
                title = "Global Fleet Modernization Subsidy",
                headline = "Aviation Consortium Announces 15% Purchase Rebate on New Aircraft",
                description = "Major aerospace manufacturers have launched a promotional fleet renewal campaign offering significant capital expenditure incentives on brand new aircraft orders.",
                totalDurationDays = 8,
                durationDaysRemaining = 8,
                aircraftPurchaseDiscount = 0.15
            )
        }
    )

    fun rollNewEvent(existingEvents: List<GameEvent>, day: Int, candidateHubs: List<String>): GameEvent? {
        // Limit max active events to 3 to keep UI focused
        if (existingEvents.size >= 3) return null

        val randomHub = if (candidateHubs.isNotEmpty()) candidateHubs.random() else "JFK"
        val template = eventTemplates.random()
        val eventId = "evt_${day}_${Random.nextInt(1000, 9999)}"

        return template(eventId, randomHub)
    }

    /**
     * Advance event duration by 1 day and return active non-expired events
     */
    fun tickEvents(events: List<GameEvent>): List<GameEvent> {
        return events.mapNotNull { event ->
            val remaining = event.durationDaysRemaining - 1
            if (remaining > 0) {
                event.copy(durationDaysRemaining = remaining)
            } else {
                null // Expired
            }
        }
    }

    /**
     * Compute aggregate demand multiplier across all active events
     */
    fun calculateEffectiveDemandMultiplier(events: List<GameEvent>, originIata: String, destIata: String): Double {
        var multiplier = 1.0
        for (event in events) {
            if (event.affectedAirportIata != null) {
                if (event.affectedAirportIata == originIata || event.affectedAirportIata == destIata) {
                    multiplier *= event.demandMultiplier
                }
            } else {
                multiplier *= event.demandMultiplier
            }
        }
        return multiplier.coerceIn(0.5, 2.5)
    }

    /**
     * Compute cost multiplier based on aircraft engine configuration during fuel price spike
     */
    fun calculateOperatingCostMultiplier(events: List<GameEvent>, engineConfig: String): Double {
        var costMult = 1.0
        for (event in events) {
            when (engineConfig) {
                "Quad Jet" -> costMult *= event.quadJetCostMultiplier
                "Tri Jet" -> costMult *= event.triJetCostMultiplier
                "Twin Jet" -> costMult *= event.twinJetCostMultiplier
                "Twin Turboprop" -> costMult *= event.turbopropCostMultiplier
            }
        }
        return costMult.coerceIn(0.7, 2.0)
    }
}
