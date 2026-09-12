package com.montanhajr.calculejuros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.montanhajr.calculejuros.core.data.repository.AdsPreferencesRepository
import com.montanhajr.calculejuros.core.util.AdManager
import com.montanhajr.calculejuros.feature.favorites.FavoritesScreen
import com.montanhajr.calculejuros.feature.favorites.FavoritesViewModel
import com.montanhajr.calculejuros.feature.education.EducationScreen
import com.montanhajr.calculejuros.feature.history.HistoryScreen
import com.montanhajr.calculejuros.feature.history.HistoryViewModel
import com.montanhajr.calculejuros.feature.home.HomeScreen
import com.montanhajr.calculejuros.feature.home.HomeViewModel
import com.montanhajr.calculejuros.feature.simulator.ResultScreen
import com.montanhajr.calculejuros.feature.simulator.ResultViewModel
import com.montanhajr.calculejuros.feature.simulator.SimulatorScreen
import com.montanhajr.calculejuros.feature.simulator.SimulatorViewModel
import com.montanhajr.calculejuros.ui.components.AdBanner
import com.montanhajr.calculejuros.ui.theme.CashWiseTheme
import com.montanhajr.calculejuros.ui.theme.DmSansFont
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adManager: AdManager

    @Inject
    lateinit var adsPreferencesRepository: AdsPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        adManager.loadInterstitial(this)
        adManager.loadRewarded(this)

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                adsPreferencesRepository.resetInterstitialCounter()
            }
        }

        setContent {
            CashWiseTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                var lastRoute by remember { mutableStateOf<String?>(null) }

                // Ad monitoring logic
                LaunchedEffect(currentDestination) {
                    val currentRoute = currentDestination?.route?.split("?")?.firstOrNull()

                    if (currentRoute != null) {
                        // Detect back from result to simulator
                        if (lastRoute == "simulation_result" && currentRoute == "simulator") {
                            val skip = adsPreferencesRepository.skipNextRewardedAd.first()
                            adsPreferencesRepository.setSkipNextRewardedAd(!skip)
                        }

                        // Interstitial logic
                        adsPreferencesRepository.incrementInterstitialCounter()
                        val counter = adsPreferencesRepository.interstitialCounter.first()
                        if (counter >= 5) {
                            adManager.showInterstitial(this@MainActivity)
                        }

                        lastRoute = currentRoute
                    }
                }

                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        Column {
                            AdBanner()
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                val items = listOf(
                                    Triple("home", stringResource(R.string.nav_label_home), Icons.Default.Home),
                                    Triple("simulator", stringResource(R.string.nav_label_simulator), Icons.Default.Calculate),
                                    Triple("history", stringResource(R.string.nav_label_history), Icons.Default.History),
                                    Triple("favorites", stringResource(R.string.nav_label_favorites), Icons.Default.Star)
                                )
                                items.forEach { (itemRoute, label, icon) ->
                                    val isSelected = currentDestination?.hierarchy?.any { 
                                        it.route?.split("?")?.firstOrNull() == itemRoute 
                                    } == true

                                    NavigationBarItem(
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label, fontFamily = DmSansFont) },
                                        selected = isSelected,
                                        onClick = {
                                            if (!isSelected) {
                                                navController.navigate(itemRoute) {
                                                    // Pop up to the start destination of the graph to
                                                    // avoid building up a large stack of destinations
                                                    // on the back stack as users select items
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    // Avoid multiple copies of the same destination when
                                                    // reselecting the same item
                                                    launchSingleTop = true
                                                    // Restore state when reselecting a previously selected item
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            indicatorColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
                                val viewModel: HomeViewModel = hiltViewModel()
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToSimulator = {
                                        navController.navigate("simulator") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    onNavigateToHistory = {
                                        navController.navigate("history") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    onNavigateToFavorites = {
                                        navController.navigate("favorites") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    onNavigateToSimulationDetail = { id ->
                                        navController.navigate("simulator?simulationId=$id") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = false
                                        }
                                    },
                                    onLearnToInvestClick = {
                                        navController.navigate("education")
                                    }
                                )
                            }
                            composable("education") {
                                EducationScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "simulator?simulationId={simulationId}",
                                arguments = listOf(
                                    navArgument("simulationId") {
                                        type = NavType.LongType
                                        defaultValue = -1L
                                    }
                                )
                            ) {
                                val viewModel: SimulatorViewModel = hiltViewModel()
                                SimulatorScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToResult = { id ->
                                        navController.navigate("simulation_result?simulationId=$id")
                                    }
                                )
                            }
                            composable(
                                route = "simulation_result?simulationId={simulationId}",
                                arguments = listOf(
                                    navArgument("simulationId") {
                                        type = NavType.LongType
                                        defaultValue = -1L
                                    }
                                )
                            ) {
                                val viewModel: ResultViewModel = hiltViewModel()
                                ResultScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("history") {
                                val viewModel: HistoryViewModel = hiltViewModel()
                                HistoryScreen(
                                    viewModel = viewModel,
                                    onNavigateToSimulator = { id ->
                                        val simulatorRoute = if (id != null) "simulator?simulationId=$id" else "simulator"
                                        navController.navigate(simulatorRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = id == null
                                        }
                                    },
                                    onNavigateToResult = { id ->
                                        navController.navigate("simulation_result?simulationId=$id")
                                    }
                                )
                            }
                            composable("favorites") {
                                val viewModel: FavoritesViewModel = hiltViewModel()
                                FavoritesScreen(
                                    viewModel = viewModel,
                                    onNavigateToSimulator = { id ->
                                        val simulatorRoute = if (id != null) "simulator?simulationId=$id" else "simulator"
                                        navController.navigate(simulatorRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = id == null
                                        }
                                    },
                                    onNavigateToResult = { id ->
                                        navController.navigate("simulation_result?simulationId=$id")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
