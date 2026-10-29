package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AirlineViewModel
import com.example.ui.AppScreen
import com.example.ui.screens.FleetHangarScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.GlobeRadarScreen
import com.example.ui.screens.LiveryStudioScreen
import com.example.ui.screens.RouteNetworkScreen
import com.example.ui.screens.ScenarioSetupScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AirlineCeoApp()
            }
        }
    }
}

@Composable
fun AirlineCeoApp(
    viewModel: AirlineViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(AppScreen.RADAR_GLOBE) }

    val empire by viewModel.empire.collectAsStateWithLifecycle()
    val fleet by viewModel.fleet.collectAsStateWithLifecycle()
    val routes by viewModel.routes.collectAsStateWithLifecycle()
    val activeFlights by viewModel.activeFlights.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val ledger by viewModel.ledger.collectAsStateWithLifecycle()
    val latestNews by viewModel.latestBreakingNews.collectAsStateWithLifecycle()

    BackHandler(enabled = currentScreen != AppScreen.RADAR_GLOBE) {
        currentScreen = AppScreen.RADAR_GLOBE
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Color(0xFF0A0E17),
        bottomBar = {
            if (currentScreen != AppScreen.NEW_GAME_SETUP) {
                AirlineBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    AppScreen.RADAR_GLOBE -> {
                        GlobeRadarScreen(
                            empire = empire,
                            activeFlights = activeFlights,
                            events = events,
                            latestNews = latestNews,
                            onSpeedChange = { viewModel.setSimulationSpeed(it) }
                        )
                    }
                    AppScreen.FLEET_HANGAR -> {
                        FleetHangarScreen(
                            fleet = fleet,
                            playerCash = empire.cashBalance,
                            onBuyAircraft = { modelId, name, asLease ->
                                viewModel.buyAircraft(modelId, name, asLease)
                            },
                            onMaintenance = { planeId ->
                                viewModel.performMaintenance(planeId)
                            }
                        )
                    }
                    AppScreen.ROUTES_NETWORK -> {
                        RouteNetworkScreen(
                            routes = routes,
                            fleet = fleet,
                            playerHubIata = empire.hubAirportIata,
                            onCreateRoute = { origin, dest, planeId, freq ->
                                viewModel.createRoute(origin, dest, planeId, freq)
                            },
                            onDeleteRoute = { routeId ->
                                viewModel.deleteRoute(routeId)
                            },
                            onUpdateRoutePricing = { routeId, eco, biz, first, cargo, freq ->
                                viewModel.updateRoutePricing(routeId, eco, biz, first, cargo, freq)
                            }
                        )
                    }
                    AppScreen.LIVERY_STUDIO -> {
                        LiveryStudioScreen(
                            currentLivery = empire.livery,
                            airlineName = empire.airlineName,
                            onSaveLivery = { newLivery ->
                                viewModel.updateLivery(newLivery)
                            }
                        )
                    }
                    AppScreen.FINANCE_EXECUTIVE -> {
                        FinanceScreen(
                            empire = empire,
                            ledger = ledger,
                            onTakeLoan = { amount -> viewModel.takeLoan(amount) },
                            onRepayLoan = { amount -> viewModel.repayLoan(amount) },
                            onOpenNewGame = { currentScreen = AppScreen.NEW_GAME_SETUP }
                        )
                    }
                    AppScreen.NEW_GAME_SETUP -> {
                        ScenarioSetupScreen(
                            canCancel = true,
                            onCancel = { currentScreen = AppScreen.RADAR_GLOBE },
                            onStartGame = { name, iata, icao, hub, preset ->
                                viewModel.startNewGame(name, iata, icao, hub, preset)
                                currentScreen = AppScreen.RADAR_GLOBE
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AirlineBottomNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF121826),
        contentColor = Color(0xFF00E5FF),
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.RADAR_GLOBE,
            onClick = { onNavigate(AppScreen.RADAR_GLOBE) },
            icon = { Icon(imageVector = Icons.Default.Public, contentDescription = "Radar") },
            label = { Text("Radar", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF032630),
                selectedTextColor = Color(0xFF00E5FF),
                indicatorColor = Color(0xFF00E5FF),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.FLEET_HANGAR,
            onClick = { onNavigate(AppScreen.FLEET_HANGAR) },
            icon = { Icon(imageVector = Icons.Default.Flight, contentDescription = "Fleet") },
            label = { Text("Fleet", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF032630),
                selectedTextColor = Color(0xFF00E5FF),
                indicatorColor = Color(0xFF00E5FF),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.ROUTES_NETWORK,
            onClick = { onNavigate(AppScreen.ROUTES_NETWORK) },
            icon = { Icon(imageVector = Icons.Default.AltRoute, contentDescription = "Routes") },
            label = { Text("Routes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF032630),
                selectedTextColor = Color(0xFF00E5FF),
                indicatorColor = Color(0xFF00E5FF),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.LIVERY_STUDIO,
            onClick = { onNavigate(AppScreen.LIVERY_STUDIO) },
            icon = { Icon(imageVector = Icons.Default.ColorLens, contentDescription = "Livery") },
            label = { Text("Livery", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF032630),
                selectedTextColor = Color(0xFF00E5FF),
                indicatorColor = Color(0xFF00E5FF),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.FINANCE_EXECUTIVE,
            onClick = { onNavigate(AppScreen.FINANCE_EXECUTIVE) },
            icon = { Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "Finance") },
            label = { Text("Finance", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF032630),
                selectedTextColor = Color(0xFF00E5FF),
                indicatorColor = Color(0xFF00E5FF),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )
    }
}
