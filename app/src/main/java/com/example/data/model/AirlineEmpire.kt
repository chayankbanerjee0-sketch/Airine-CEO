package com.example.data.model

enum class DifficultyPreset(
    val title: String,
    val subtitle: String,
    val startingCash: Long,
    val startingReputation: Int,
    val demandMultiplier: Double,
    val maintenanceCostMultiplier: Double,
    val loanInterestRate: Double,
    val description: String
) {
    SANDBOX(
        title = "Casual Sandbox",
        subtitle = "Unlimited Growth & High Demand",
        startingCash = 200_000_000L,
        startingReputation = 85,
        demandMultiplier = 1.30,
        maintenanceCostMultiplier = 0.70,
        loanInterestRate = 0.02,
        description = "Enjoy stress-free expansion with a massive war chest and forgiving operational margins."
    ),
    MODERN_STARTUP(
        title = "Modern Startup",
        subtitle = "Realistic Commercial Empire",
        startingCash = 50_000_000L,
        startingReputation = 50,
        demandMultiplier = 1.0,
        maintenanceCostMultiplier = 1.0,
        loanInterestRate = 0.045,
        description = "Start as an ambitious new airline with seed capital, building a profitable regional network before going intercontinental."
    ),
    VETERAN_TYCOON(
        title = "Veteran Tycoon",
        subtitle = "Strict Margins & Tight Cash Flow",
        startingCash = 20_000_000L,
        startingReputation = 35,
        demandMultiplier = 0.88,
        maintenanceCostMultiplier = 1.25,
        loanInterestRate = 0.08,
        description = "Demanding real-world yield management, strict airport gate slot competition, and expensive heavy maintenance checks."
    ),
    CARGO_TITAN(
        title = "Cargo Titan",
        subtitle = "Global Air Freight Specialist",
        startingCash = 65_000_000L,
        startingReputation = 60,
        demandMultiplier = 1.15,
        maintenanceCostMultiplier = 0.95,
        loanInterestRate = 0.04,
        description = "Focus on international supply chains and high-yield cargo freighters connecting industrial trade hubs worldwide."
    )
}

data class AirlineEmpire(
    val airlineName: String = "AeroGlobal",
    val iataCode: String = "AG",
    val icaoCode: String = "AGL",
    val callsign: String = "AEROGLOBAL",
    val hubAirportIata: String = "JFK",
    val difficulty: DifficultyPreset = DifficultyPreset.MODERN_STARTUP,
    val cashBalance: Long = 50_000_000L,
    val totalLoansOutstanding: Long = 0L,
    val reputationScore: Int = 50, // 0 - 100
    val totalPassengersTransported: Long = 0L,
    val totalCargoTransportedTonnes: Double = 0.0,
    val totalLifetimeRevenue: Long = 0L,
    val simulationDay: Int = 1,
    val simulationHour: Int = 8,
    val isSimulationPaused: Boolean = false,
    val timeSpeedMultiplier: Int = 1, // 1x, 2x, 5x
    val livery: LiveryDesign = LiveryDesign(),
    val unlockedAchievements: Set<String> = emptySet()
)
