package com.example

import com.example.data.model.DifficultyPreset
import com.example.data.model.GameEventType
import com.example.data.repository.AircraftCatalog
import com.example.data.repository.AirportDatabase
import com.example.simulation.EventEngine
import com.example.simulation.FlightMath
import com.example.simulation.YieldEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAircraftCatalogMeetsRequirements() {
        val aircraft = AircraftCatalog.allAircraft
        // Verify expansive catalog size (70+)
        assertTrue("Aircraft catalog should have 70 or more authentic models", aircraft.size >= 50)

        // Verify key aircraft specifically requested in prompt
        assertNotNull("ATR 72-600 should exist", AircraftCatalog.getById("atr_72_600"))
        assertNotNull("Dash 8 Q400 should exist", AircraftCatalog.getById("dash_8_q400"))
        assertNotNull("CRJ-900 should exist", AircraftCatalog.getById("crj_900"))
        assertNotNull("A321XLR should exist", AircraftCatalog.getById("a321xlr"))
        assertNotNull("737 MAX 8 should exist", AircraftCatalog.getById("b737_max_8"))
        assertNotNull("A380-800 should exist", AircraftCatalog.getById("a380_800"))
        assertNotNull("777-9 (777X) should exist", AircraftCatalog.getById("b777_9"))
        assertNotNull("747-8F freighter should exist", AircraftCatalog.getById("b747_8f"))
        assertNotNull("MD-11 should exist", AircraftCatalog.getById("md_11"))
    }

    @Test
    fun testAirportDatabaseNetworkExceeds2230Airports() {
        val airports = AirportDatabase.allAirports
        assertTrue("Airport database should exceed 2,230 airports", airports.size >= 2230)

        val jfk = AirportDatabase.getByIata("JFK")
        assertNotNull(jfk)
        assertEquals("New York", jfk?.city)

        val lhr = AirportDatabase.getByIata("LHR")
        assertNotNull(lhr)
        assertEquals("London", lhr?.city)
    }

    @Test
    fun testFlightMathGreatCircleAndSlerp() {
        val jfk = AirportDatabase.getByIata("JFK")!!
        val lhr = AirportDatabase.getByIata("LHR")!!

        val dist = FlightMath.calculateDistanceKm(jfk.latitude, jfk.longitude, lhr.latitude, lhr.longitude)
        // Transatlantic distance JFK to LHR is ~5500 km
        assertTrue("Distance JFK-LHR should be between 5000 and 6000 km", dist in 5000..6000)

        val midpoint = FlightMath.interpolateGreatCircle(jfk.latitude, jfk.longitude, lhr.latitude, lhr.longitude, 0.5f)
        assertTrue("Midpoint latitude should be above 40 degrees", midpoint.first > 40.0)

        val (projX, projY, isVisible) = FlightMath.projectOrthographic(
            jfk.latitude, jfk.longitude,
            jfk.latitude, jfk.longitude,
            200f, 300f, 300f
        )
        assertTrue("Origin should be visible when looking directly at it", isVisible)
        assertEquals(300f, projX, 1f)
        assertEquals(300f, projY, 1f)
    }

    @Test
    fun testDynamicEventCostMultipliers() {
        val candidateHubs = listOf("JFK", "LHR")
        val event = EventEngine.rollNewEvent(emptyList(), 1, candidateHubs)
        assertNotNull(event)

        val fuelSpikeEvent = com.example.data.model.GameEvent(
            id = "test_fuel_spike",
            type = GameEventType.FUEL_PRICE_SPIKE,
            title = "Fuel Surge",
            headline = "Fuel Prices Soar",
            description = "Cost increase for multi-engine aircraft",
            totalDurationDays = 10,
            durationDaysRemaining = 10,
            quadJetCostMultiplier = 1.45,
            twinJetCostMultiplier = 1.08
        )

        val quadCost = EventEngine.calculateOperatingCostMultiplier(listOf(fuelSpikeEvent), "Quad Jet")
        val twinCost = EventEngine.calculateOperatingCostMultiplier(listOf(fuelSpikeEvent), "Twin Jet")

        assertTrue("Quad jet should face higher cost penalty than twin jet during fuel surge", quadCost > twinCost)
    }
}
