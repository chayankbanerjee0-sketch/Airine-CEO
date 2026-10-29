package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActiveFlight
import com.example.data.model.AircraftModel
import com.example.data.model.AirlineEmpire
import com.example.data.model.AirlineRoute
import com.example.data.model.DifficultyPreset
import com.example.data.model.GameEvent
import com.example.data.model.LiveryDesign
import com.example.data.model.OwnedAircraft
import com.example.data.repository.AirlineRepository
import com.example.data.repository.FinancialLedgerEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppScreen {
    RADAR_GLOBE,
    FLEET_HANGAR,
    ROUTES_NETWORK,
    LIVERY_STUDIO,
    FINANCE_EXECUTIVE,
    NEW_GAME_SETUP
}

class AirlineViewModel(
    val repository: AirlineRepository = AirlineRepository()
) : ViewModel() {

    val empire: StateFlow<AirlineEmpire> = repository.empire
    val fleet: StateFlow<List<OwnedAircraft>> = repository.fleet
    val routes: StateFlow<List<AirlineRoute>> = repository.routes
    val activeFlights: StateFlow<List<ActiveFlight>> = repository.activeFlights
    val events: StateFlow<List<GameEvent>> = repository.events
    val ledger: StateFlow<List<FinancialLedgerEntry>> = repository.ledger
    val latestBreakingNews: StateFlow<String?> = repository.latestBreakingNews

    init {
        // Launch continuous simulation ticker loop
        viewModelScope.launch {
            while (isActive) {
                val empireState = repository.empire.value
                val delayTime = when {
                    empireState.isSimulationPaused -> 1000L
                    empireState.timeSpeedMultiplier == 5 -> 300L
                    empireState.timeSpeedMultiplier == 2 -> 650L
                    else -> 1200L // 1x normal speed
                }
                delay(delayTime)
                repository.tickSimulation()
            }
        }
    }

    fun startNewGame(
        airlineName: String,
        iata: String,
        icao: String,
        hubIata: String,
        preset: DifficultyPreset
    ) {
        repository.startNewGame(airlineName, iata, icao, hubIata, preset)
    }

    fun buyAircraft(modelId: String, customName: String, asLease: Boolean): Boolean {
        return repository.buyAircraft(modelId, customName, asLease)
    }

    fun performMaintenance(planeId: String): Boolean {
        return repository.performMaintenance(planeId)
    }

    fun createRoute(
        originIata: String,
        destIata: String,
        aircraftId: String?,
        dailyFrequencies: Int
    ): Boolean {
        return repository.createRoute(originIata, destIata, aircraftId, dailyFrequencies)
    }

    fun updateRoutePricing(
        routeId: String,
        ecoFare: Int,
        bizFare: Int,
        firstFare: Int,
        cargoRate: Double,
        dailyFrequencies: Int
    ) {
        repository.updateRoutePricing(routeId, ecoFare, bizFare, firstFare, cargoRate, dailyFrequencies)
    }

    fun deleteRoute(routeId: String) {
        repository.deleteRoute(routeId)
    }

    fun updateLivery(newLivery: LiveryDesign) {
        repository.updateLivery(newLivery)
    }

    fun takeLoan(amount: Long): Boolean {
        return repository.takeLoan(amount)
    }

    fun repayLoan(amount: Long): Boolean {
        return repository.repayLoan(amount)
    }

    fun setSimulationSpeed(speed: Int) {
        repository.setSimulationSpeed(speed)
    }
}
