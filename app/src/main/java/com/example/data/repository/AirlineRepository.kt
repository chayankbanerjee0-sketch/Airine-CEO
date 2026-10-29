package com.example.data.repository

import com.example.data.model.ActiveFlight
import com.example.data.model.AircraftModel
import com.example.data.model.AircraftStatus
import com.example.data.model.AirlineEmpire
import com.example.data.model.AirlineRoute
import com.example.data.model.Airport
import com.example.data.model.DifficultyPreset
import com.example.data.model.GameEvent
import com.example.data.model.LiveryDesign
import com.example.data.model.OwnedAircraft
import com.example.simulation.EventEngine
import com.example.simulation.FlightMath
import com.example.simulation.YieldEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class FinancialLedgerEntry(
    val day: Int,
    val description: String,
    val amount: Long,
    val isIncome: Boolean,
    val category: String
)

class AirlineRepository {

    private val _empire = MutableStateFlow(AirlineEmpire())
    val empire: StateFlow<AirlineEmpire> = _empire.asStateFlow()

    private val _fleet = MutableStateFlow<List<OwnedAircraft>>(emptyList())
    val fleet: StateFlow<List<OwnedAircraft>> = _fleet.asStateFlow()

    private val _routes = MutableStateFlow<List<AirlineRoute>>(emptyList())
    val routes: StateFlow<List<AirlineRoute>> = _routes.asStateFlow()

    private val _activeFlights = MutableStateFlow<List<ActiveFlight>>(emptyList())
    val activeFlights: StateFlow<List<ActiveFlight>> = _activeFlights.asStateFlow()

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())
    val events: StateFlow<List<GameEvent>> = _events.asStateFlow()

    private val _ledger = MutableStateFlow<List<FinancialLedgerEntry>>(emptyList())
    val ledger: StateFlow<List<FinancialLedgerEntry>> = _ledger.asStateFlow()

    private val _latestBreakingNews = MutableStateFlow<String?>(null)
    val latestBreakingNews: StateFlow<String?> = _latestBreakingNews.asStateFlow()

    private var flightCounter = 100

    init {
        // Initialize default game state
        startNewGame(
            airlineName = "AeroGlobal",
            iata = "AG",
            icao = "AGL",
            hubIata = "JFK",
            preset = DifficultyPreset.MODERN_STARTUP
        )
    }

    fun startNewGame(
        airlineName: String,
        iata: String,
        icao: String,
        hubIata: String,
        preset: DifficultyPreset
    ) {
        val initialEmpire = AirlineEmpire(
            airlineName = airlineName.ifBlank { "AeroGlobal" },
            iataCode = iata.ifBlank { "AG" }.uppercase(),
            icaoCode = icao.ifBlank { "AGL" }.uppercase(),
            callsign = (airlineName.ifBlank { "AEROGLOBAL" }).replace(" ", "").uppercase(),
            hubAirportIata = hubIata,
            difficulty = preset,
            cashBalance = preset.startingCash,
            reputationScore = preset.startingReputation,
            simulationDay = 1,
            simulationHour = 8,
            timeSpeedMultiplier = 1
        )
        _empire.value = initialEmpire

        // Initial fleet setup based on scenario
        val initialPlanes = mutableListOf<OwnedAircraft>()
        when (preset) {
            DifficultyPreset.SANDBOX -> {
                // Generous startup fleet
                val a321 = AircraftCatalog.getById("a321neo") ?: AircraftCatalog.allAircraft[15]
                val b787 = AircraftCatalog.getById("b787_9") ?: AircraftCatalog.allAircraft[25]
                initialPlanes.add(createOwnedAircraft(a321, "N101AG", "Manhattan", false))
                initialPlanes.add(createOwnedAircraft(b787, "N201AG", "Empire Pride", false))
            }
            DifficultyPreset.MODERN_STARTUP -> {
                // Two regional jets leased to get off the ground cleanly
                val e175 = AircraftCatalog.getById("embraer_e175") ?: AircraftCatalog.allAircraft[10]
                val a220 = AircraftCatalog.getById("a220_100") ?: AircraftCatalog.allAircraft[14]
                initialPlanes.add(createOwnedAircraft(e175, "N711AG", "Hudson Explorer", true))
                initialPlanes.add(createOwnedAircraft(a220, "N722AG", "Metro Pioneer", true))
            }
            DifficultyPreset.VETERAN_TYCOON -> {
                // Single regional turboprop starter
                val q400 = AircraftCatalog.getById("dash_8_q400") ?: AircraftCatalog.allAircraft[2]
                initialPlanes.add(createOwnedAircraft(q400, "N801AG", "Budget Feeder", true))
            }
            DifficultyPreset.CARGO_TITAN -> {
                // Two dedicated freighters
                val b737f = AircraftCatalog.getById("b737_800bcf") ?: AircraftCatalog.allAircraft[45]
                val b767f = AircraftCatalog.getById("b767_300f") ?: AircraftCatalog.allAircraft[47]
                initialPlanes.add(createOwnedAircraft(b737f, "N901CF", "Global Express 1", false))
                initialPlanes.add(createOwnedAircraft(b767f, "N902CF", "Titan Freighter", true))
            }
        }
        _fleet.value = initialPlanes
        _routes.value = emptyList()
        _activeFlights.value = emptyList()
        _ledger.value = listOf(
            FinancialLedgerEntry(1, "Initial Capital Investment (${preset.title})", preset.startingCash, true, "Capital")
        )

        // Seed an exciting initial dynamic event
        val initialEvent = EventEngine.rollNewEvent(emptyList(), 1, listOf(hubIata))
        if (initialEvent != null) {
            _events.value = listOf(initialEvent)
            _latestBreakingNews.value = "BREAKING NEWS: ${initialEvent.headline}"
        } else {
            _events.value = emptyList()
            _latestBreakingNews.value = null
        }

        // Auto-launch a tutorial route to give instant live flight action!
        if (initialPlanes.isNotEmpty()) {
            val starterPlane = initialPlanes.first()
            val destAirport = when (hubIata) {
                "JFK" -> "ORD"
                "LHR" -> "CDG"
                "HND" -> "ITM"
                else -> "LAX"
            }
            createRoute(
                originIata = hubIata,
                destIata = destAirport,
                aircraftId = starterPlane.id,
                dailyFrequencies = 2
            )
        }
    }

    private fun createOwnedAircraft(
        model: AircraftModel,
        registration: String,
        customName: String,
        isLeased: Boolean
    ): OwnedAircraft {
        return OwnedAircraft(
            id = "plane_${System.currentTimeMillis()}_${Random.nextInt(100, 999)}",
            modelId = model.id,
            registration = registration,
            customName = customName,
            isLeased = isLeased,
            conditionPercent = 100,
            configuredEconomyPax = model.maxEconomyPax,
            configuredBusinessPax = model.maxBusinessPax,
            configuredFirstPax = model.maxFirstPax
        )
    }

    fun buyAircraft(modelId: String, customName: String, asLease: Boolean): Boolean {
        val model = AircraftCatalog.getById(modelId) ?: return false
        val currentEmpire = _empire.value

        // Check for manufacturer rebate discount
        val rebateDiscount = _events.value.maxOfOrNull { it.aircraftPurchaseDiscount } ?: 0.0
        val effectivePurchasePrice = (model.purchasePrice * (1.0 - rebateDiscount)).toLong()
        val upfrontCost = if (asLease) model.leasePricePerDay * 3 else effectivePurchasePrice

        if (currentEmpire.cashBalance < upfrontCost) return false

        val prefix = currentEmpire.iataCode
        val regNum = Random.nextInt(100, 999)
        val registration = "$prefix-$regNum"

        val newPlane = createOwnedAircraft(
            model = model,
            registration = registration,
            customName = customName.ifBlank { "${model.name} Flight unit" },
            isLeased = asLease
        )

        _empire.value = currentEmpire.copy(cashBalance = currentEmpire.cashBalance - upfrontCost)
        _fleet.value = _fleet.value + newPlane

        addLedgerEntry(
            description = if (asLease) "Lease Deposit: ${model.name} ($registration)" else "Aircraft Purchase: ${model.name} ($registration)",
            amount = upfrontCost,
            isIncome = false,
            category = "Fleet"
        )
        return true
    }

    fun performMaintenance(planeId: String): Boolean {
        val plane = _fleet.value.find { it.id == planeId } ?: return false
        val model = AircraftCatalog.getById(plane.modelId) ?: return false
        val cost = (model.hourlyMaintenanceCost * 8L).coerceAtLeast(4000L)

        val currentEmpire = _empire.value
        if (currentEmpire.cashBalance < cost) return false

        _empire.value = currentEmpire.copy(cashBalance = currentEmpire.cashBalance - cost)
        _fleet.value = _fleet.value.map {
            if (it.id == planeId) it.copy(conditionPercent = 100, status = AircraftStatus.IDLE) else it
        }

        addLedgerEntry(
            description = "A-Check Maintenance Overhaul: ${plane.registration}",
            amount = cost,
            isIncome = false,
            category = "Maintenance"
        )
        return true
    }

    fun createRoute(
        originIata: String,
        destIata: String,
        aircraftId: String?,
        dailyFrequencies: Int = 2
    ): Boolean {
        val origin = AirportDatabase.getByIata(originIata) ?: return false
        val dest = AirportDatabase.getByIata(destIata) ?: return false
        if (origin.iata == dest.iata) return false

        val distance = FlightMath.calculateDistanceKm(origin.latitude, origin.longitude, dest.latitude, dest.longitude)

        // Verify aircraft range if assigned
        if (aircraftId != null) {
            val plane = _fleet.value.find { it.id == aircraftId } ?: return false
            val model = AircraftCatalog.getById(plane.modelId) ?: return false
            if (model.rangeKm < distance) return false
        }

        val benchEco = YieldEngine.getBenchmarkEconomyFare(distance)
        val benchBiz = YieldEngine.getBenchmarkBusinessFare(distance)
        val benchFirst = YieldEngine.getBenchmarkFirstFare(distance)
        val benchCargo = YieldEngine.getBenchmarkCargoRate(distance)

        val routeId = "rte_${origin.iata}_${dest.iata}_${System.currentTimeMillis()}"
        val newRoute = AirlineRoute(
            id = routeId,
            originIata = origin.iata,
            destIata = dest.iata,
            distanceKm = distance,
            assignedAircraftId = aircraftId,
            dailyFrequencies = dailyFrequencies.coerceIn(1, 6),
            economyFare = benchEco,
            businessFare = benchBiz,
            firstFare = benchFirst,
            cargoRatePerKg = benchCargo
        )

        // Assign aircraft status
        if (aircraftId != null) {
            _fleet.value = _fleet.value.map {
                if (it.id == aircraftId) it.copy(assignedRouteId = routeId, status = AircraftStatus.SCHEDULED) else it
            }
            // Spawn initial flight
            launchFlightForRoute(newRoute, aircraftId)
        }

        _routes.value = _routes.value + newRoute
        return true
    }

    fun updateRoutePricing(
        routeId: String,
        ecoFare: Int,
        bizFare: Int,
        firstFare: Int,
        cargoRate: Double,
        dailyFrequencies: Int
    ) {
        _routes.value = _routes.value.map { route ->
            if (route.id == routeId) {
                route.copy(
                    economyFare = ecoFare,
                    businessFare = bizFare,
                    firstFare = firstFare,
                    cargoRatePerKg = cargoRate,
                    dailyFrequencies = dailyFrequencies
                )
            } else route
        }
    }

    fun deleteRoute(routeId: String) {
        val route = _routes.value.find { it.id == routeId } ?: return
        if (route.assignedAircraftId != null) {
            _fleet.value = _fleet.value.map {
                if (it.id == route.assignedAircraftId) it.copy(assignedRouteId = null, status = AircraftStatus.IDLE) else it
            }
        }
        _activeFlights.value = _activeFlights.value.filter { it.routeId != routeId }
        _routes.value = _routes.value.filter { it.id != routeId }
    }

    fun updateLivery(newLivery: LiveryDesign) {
        _empire.value = _empire.value.copy(livery = newLivery)
    }

    fun takeLoan(amount: Long): Boolean {
        val empire = _empire.value
        if (empire.totalLoansOutstanding + amount > 100_000_000L) return false
        _empire.value = empire.copy(
            cashBalance = empire.cashBalance + amount,
            totalLoansOutstanding = empire.totalLoansOutstanding + amount
        )
        addLedgerEntry("Commercial Bank Credit Facility", amount, true, "Financing")
        return true
    }

    fun repayLoan(amount: Long): Boolean {
        val empire = _empire.value
        if (empire.cashBalance < amount || empire.totalLoansOutstanding < amount) return false
        _empire.value = empire.copy(
            cashBalance = empire.cashBalance - amount,
            totalLoansOutstanding = empire.totalLoansOutstanding - amount
        )
        addLedgerEntry("Bank Loan Principal Repayment", amount, false, "Financing")
        return true
    }

    fun setSimulationSpeed(multiplier: Int) {
        _empire.value = _empire.value.copy(timeSpeedMultiplier = multiplier, isSimulationPaused = multiplier == 0)
    }

    private fun launchFlightForRoute(route: AirlineRoute, planeId: String, reverse: Boolean = false) {
        val originIata = if (reverse) route.destIata else route.originIata
        val destIata = if (reverse) route.originIata else route.destIata
        val origin = AirportDatabase.getByIata(originIata) ?: return
        val dest = AirportDatabase.getByIata(destIata) ?: return
        val plane = _fleet.value.find { it.id == planeId } ?: return
        val model = AircraftCatalog.getById(plane.modelId) ?: return

        flightCounter++
        val flightNumber = "${_empire.value.iataCode}$flightCounter"

        val settlement = YieldEngine.calculateFlightYield(
            distanceKm = route.distanceKm,
            origin = origin,
            dest = dest,
            plane = plane,
            model = model,
            ecoFare = route.economyFare,
            bizFare = route.businessFare,
            firstFare = route.firstFare,
            cargoRatePerKg = route.cargoRatePerKg,
            events = _events.value,
            airlineReputation = _empire.value.reputationScore
        )

        val flight = ActiveFlight(
            flightNumber = flightNumber,
            routeId = route.id,
            aircraftId = plane.id,
            aircraftModelId = model.id,
            originIata = origin.iata,
            destIata = dest.iata,
            originLat = origin.latitude,
            originLon = origin.longitude,
            destLat = dest.latitude,
            destLon = dest.longitude,
            progress = 0f,
            currentLat = origin.latitude,
            currentLon = origin.longitude,
            speedKmh = model.cruisingSpeedKmh,
            altitudeFt = (28000 + Random.nextInt(0, 100) * 100),
            passengersOnboard = settlement.economyPaxBoarded + settlement.businessPaxBoarded + settlement.firstPaxBoarded,
            cargoTonnesOnboard = settlement.cargoTonnesBoarded,
            expectedRevenue = settlement.totalRevenue,
            operatingCost = settlement.operatingCost,
            headingDegrees = FlightMath.calculateBearing(origin.latitude, origin.longitude, dest.latitude, dest.longitude)
        )

        _activeFlights.value = _activeFlights.value + flight
    }

    /**
     * Primary Simulation Tick called by ViewModel ticker coroutine
     */
    fun tickSimulation() {
        val currentEmpire = _empire.value
        if (currentEmpire.isSimulationPaused) return

        val speed = currentEmpire.timeSpeedMultiplier
        // Advance hour
        var newHour = currentEmpire.simulationHour + 1
        var newDay = currentEmpire.simulationDay

        if (newHour >= 24) {
            newHour = 0
            newDay += 1
            // Daily operations settlement: leases, loans, event ticks
            processDailyMaintenanceAndLeases(newDay)
        }

        // Progress active flights
        val flights = _activeFlights.value
        val updatedFlights = mutableListOf<ActiveFlight>()

        for (flight in flights) {
            // Flight speed progress increment
            val deltaProgress = (0.04f * speed).coerceAtLeast(0.02f)
            val nextProgress = flight.progress + deltaProgress

            if (nextProgress >= 1.0f) {
                // Flight landed! Settle financials and turn around
                settleFlightArrival(flight)
                // Turnaround and launch return leg
                val route = _routes.value.find { it.id == flight.routeId }
                if (route != null && !route.isSuspended) {
                    val isReturn = flight.originIata != route.originIata
                    launchFlightForRoute(route, flight.aircraftId, reverse = !isReturn)
                }
            } else {
                val (lat, lon) = FlightMath.interpolateGreatCircle(
                    flight.originLat, flight.originLon,
                    flight.destLat, flight.destLon,
                    nextProgress
                )
                updatedFlights.add(
                    flight.copy(
                        progress = nextProgress,
                        currentLat = lat,
                        currentLon = lon
                    )
                )
            }
        }

        _activeFlights.value = updatedFlights
        _empire.value = currentEmpire.copy(simulationDay = newDay, simulationHour = newHour)
    }

    private fun settleFlightArrival(flight: ActiveFlight) {
        val profit = flight.expectedRevenue - flight.operatingCost
        val currentEmpire = _empire.value

        _empire.value = currentEmpire.copy(
            cashBalance = currentEmpire.cashBalance + profit,
            totalPassengersTransported = currentEmpire.totalPassengersTransported + flight.passengersOnboard,
            totalCargoTransportedTonnes = currentEmpire.totalCargoTransportedTonnes + flight.cargoTonnesOnboard,
            totalLifetimeRevenue = currentEmpire.totalLifetimeRevenue + flight.expectedRevenue,
            reputationScore = (currentEmpire.reputationScore + if (profit > 0) 1 else -1).coerceIn(10, 100)
        )

        // Wear and tear on aircraft condition
        _fleet.value = _fleet.value.map { plane ->
            if (plane.id == flight.aircraftId) {
                val newCond = (plane.conditionPercent - 1).coerceAtLeast(10)
                plane.copy(
                    conditionPercent = newCond,
                    totalFlightHours = plane.totalFlightHours + 2.5
                )
            } else plane
        }

        // Update route statistics
        _routes.value = _routes.value.map { r ->
            if (r.id == flight.routeId) {
                r.copy(
                    lastFlightRevenue = flight.expectedRevenue,
                    lastFlightOperatingCost = flight.operatingCost,
                    totalPassengersCarried = r.totalPassengersCarried + flight.passengersOnboard,
                    totalCargoCarriedTonnes = r.totalCargoCarriedTonnes + flight.cargoTonnesOnboard
                )
            } else r
        }

        addLedgerEntry(
            description = "Flight ${flight.flightNumber} Arrived ${flight.originIata}→${flight.destIata}",
            amount = profit,
            isIncome = profit >= 0,
            category = "Flights"
        )
    }

    private fun processDailyMaintenanceAndLeases(newDay: Int) {
        var dailyLeaseCosts = 0L
        for (plane in _fleet.value) {
            if (plane.isLeased) {
                val model = AircraftCatalog.getById(plane.modelId)
                if (model != null) {
                    dailyLeaseCosts += model.leasePricePerDay
                }
            }
        }

        // Loan interest
        val loanInterest = (_empire.value.totalLoansOutstanding * (_empire.value.difficulty.loanInterestRate / 365.0)).toLong()
        val totalDailyOverhead = dailyLeaseCosts + loanInterest

        if (totalDailyOverhead > 0) {
            _empire.value = _empire.value.copy(
                cashBalance = _empire.value.cashBalance - totalDailyOverhead
            )
            addLedgerEntry("Daily Aircraft Leases & Debt Service", totalDailyOverhead, false, "Overhead")
        }

        // Tick dynamic events
        val updatedEvents = EventEngine.tickEvents(_events.value)

        // Roll for potential new world events every few days
        val activeEventList = if (updatedEvents.size < 3 && Random.nextFloat() < 0.40f) {
            val candidateHubs = listOf(_empire.value.hubAirportIata, "LHR", "HND", "DXB", "FRA", "SIN")
            val newEvent = EventEngine.rollNewEvent(updatedEvents, newDay, candidateHubs)
            if (newEvent != null) {
                _latestBreakingNews.value = "WORLD NEWS: ${newEvent.headline}"
                updatedEvents + newEvent
            } else updatedEvents
        } else {
            updatedEvents
        }
        _events.value = activeEventList
    }

    private fun addLedgerEntry(description: String, amount: Long, isIncome: Boolean, category: String) {
        val entry = FinancialLedgerEntry(_empire.value.simulationDay, description, amount, isIncome, category)
        _ledger.value = (listOf(entry) + _ledger.value).take(50)
    }
}
