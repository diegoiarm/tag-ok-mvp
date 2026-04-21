package com.tagok.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tagok.app.ui.home.HomeScreen
import com.tagok.app.ui.map.MapScreen
import com.tagok.app.ui.perfil.PerfilScreen
import com.tagok.app.ui.presupuesto.PresupuestoScreen
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary

private sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Filled.Home)
    data object Presupuesto : Screen("presupuesto", "Presupuesto", Icons.Filled.MonetizationOn)
    data object Perfil : Screen("perfil", "Perfil", Icons.Filled.Person)
}

private val bottomNavScreens = listOf(Screen.Home, Screen.Presupuesto, Screen.Perfil)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStack?.destination

    val showBottomBar = bottomNavScreens.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    bottomNavScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(imageVector = screen.icon, contentDescription = screen.label)
                            },
                            label = { Text(screen.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue40,
                                selectedTextColor = Blue40,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color(0xFFEEF2FF),
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onPlanificarViaje = { navController.navigate("map") },
                    onHistorialViajes = { /* TODO: HistorialScreen */ },
                    onIrARuta = { navController.navigate("map") },
                    onBoletaMensual = { /* TODO: BoletaScreen */ },
                )
            }
            composable(Screen.Presupuesto.route) { PresupuestoScreen() }
            composable(Screen.Perfil.route) { PerfilScreen() }
            composable("map") { MapScreen() }
        }
    }
}
