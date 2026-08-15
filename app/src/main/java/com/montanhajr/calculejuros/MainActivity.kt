package com.montanhajr.calculejuros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
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
import com.montanhajr.calculejuros.feature.favorites.FavoritesScreen
import com.montanhajr.calculejuros.feature.favorites.FavoritesViewModel
import com.montanhajr.calculejuros.feature.history.HistoryScreen
import com.montanhajr.calculejuros.feature.history.HistoryViewModel
import com.montanhajr.calculejuros.feature.home.HomeScreen
import com.montanhajr.calculejuros.feature.home.HomeViewModel
import com.montanhajr.calculejuros.feature.profile.ProfileScreen
import com.montanhajr.calculejuros.feature.profile.ProfileViewModel
import com.montanhajr.calculejuros.feature.simulator.SimulatorScreen
import com.montanhajr.calculejuros.feature.simulator.SimulatorViewModel
import com.montanhajr.calculejuros.ui.theme.CashWiseTheme
import com.montanhajr.calculejuros.ui.theme.BrandPurple
import com.montanhajr.calculejuros.ui.theme.DmSansFont
import com.montanhajr.calculejuros.ui.theme.SoraFont
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CashWiseTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            val items = listOf(
                                Triple("home", "Início", Icons.Default.Home),
                                Triple("simulator", "Simulações", Icons.Default.Calculate),
                                Triple("history", "Histórico", Icons.Default.History),
                                Triple("favorites", "Favoritos", Icons.Default.Star),
                                Triple("profile", "Perfil", Icons.Default.Person)
                            )
                            items.forEach { (route, label, icon) ->
                                NavigationBarItem(
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label, fontFamily = DmSansFont) },
                                    selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                                    onClick = {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
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
                ) {
padding ->
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
                                    viewModel = viewModel
                                ) { navController.navigate("simulator") }
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
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("simulations") { /* Placeholder */ }
                            composable("history") {
                                val viewModel: HistoryViewModel = hiltViewModel()
                                HistoryScreen(
                                    viewModel = viewModel,
                                    onNavigateToSimulator = { id ->
                                        val route = if (id != null) "simulator?simulationId=$id" else "simulator"
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            // Se estivermos reutilizando, não restauramos o estado anterior para garantir 
                                            // que o novo simulationId seja processado pelo ViewModel.
                                            restoreState = id == null
                                        }
                                    }
                                )
                            }
                            composable("favorites") {
                                val viewModel: FavoritesViewModel = hiltViewModel()
                                FavoritesScreen(
                                    viewModel = viewModel,
                                    onNavigateToSimulator = { id ->
                                        val route = if (id != null) "simulator?simulationId=$id" else "simulator"
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = id == null
                                        }
                                    }
                                )
                            }
                            composable("profile") {
                                val viewModel: ProfileViewModel = hiltViewModel()
                                ProfileScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
