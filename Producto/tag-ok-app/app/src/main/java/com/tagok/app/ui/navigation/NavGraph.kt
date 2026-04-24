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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tagok.app.supabase
import com.tagok.app.ui.auth.AuthViewModel
import com.tagok.app.ui.auth.LoginScreen
import com.tagok.app.ui.home.HomeScreen
import com.tagok.app.ui.register.RegisterScreen
import com.tagok.app.ui.register.RegisterViewModel
import com.tagok.app.ui.map.MapScreen
import com.tagok.app.ui.perfil.PerfilScreen
import com.tagok.app.ui.planificar.PlanificarViajeScreen
import com.tagok.app.ui.presupuesto.PresupuestoScreen
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

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
    val scope = rememberCoroutineScope()

    val hasSession = supabase.auth.currentSessionOrNull() != null
    val startDestination = if (hasSession) Screen.Home.route else "login"

    // Reacciona a cambios de sesión (deep link de confirmación de email, logout, etc.)
    LaunchedEffect(Unit) {
        supabase.auth.sessionStatus.collect {
            val isAuthenticated = supabase.auth.currentSessionOrNull() != null
            val route = navController.currentDestination?.route
            if (isAuthenticated && (route == "login" || route == "register")) {
                navController.navigate(Screen.Home.route) {
                    popUpTo("login") { inclusive = true }
                }
            } else if (!isAuthenticated && route != "login" && route != "register") {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("login") {
                val authViewModel: AuthViewModel = viewModel()
                val uiState by authViewModel.uiState.collectAsState()

                // Navega a Home al autenticarse exitosamente
                androidx.compose.runtime.LaunchedEffect(uiState) {
                    if (uiState is com.tagok.app.ui.auth.LoginUiState.Success) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }

                LoginScreen(
                    uiState = uiState,
                    onSignInWithEmail = authViewModel::signInWithEmail,
                    onSignInWithGoogle = authViewModel::signInWithGoogle,
                    onNavigateToRegister = { navController.navigate("register") },
                    onClearError = authViewModel::clearError,
                )
            }

            composable("register") {
                val regViewModel: RegisterViewModel = viewModel()
                RegisterScreen(
                    onSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    viewModel = regViewModel,
                )
            }

            composable(Screen.Home.route) {
                val nombre = supabase.auth.currentUserOrNull()
                    ?.userMetadata?.get("nombre")?.jsonPrimitive?.contentOrNull
                    ?: "Usuario"
                HomeScreen(
                    nombre = nombre,
                    onPlanificarViaje = { v -> navController.navigate("planificar/$v") },
                    onHistorialViajes = { /* TODO: HistorialScreen */ },
                    onIrARuta = { v -> navController.navigate("map/$v") },
                    onBoletaMensual = { /* TODO: BoletaScreen */ },
                    onLogout = {
                        scope.launch {
                            supabase.auth.signOut()
                            // La observación de sessionStatus navega a login automáticamente
                        }
                    },
                )
            }
            composable(Screen.Presupuesto.route) { PresupuestoScreen() }
            composable(Screen.Perfil.route) { PerfilScreen() }

            // Planificación de viaje: input de origen/destino + estimación de tarifa
            composable(
                route = "planificar/{vehiculo}",
                arguments = listOf(navArgument("vehiculo") {
                    type = NavType.StringType
                    defaultValue = "AUTO"
                }),
            ) { backStack ->
                val vehiculo = backStack.arguments?.getString("vehiculo") ?: "AUTO"
                PlanificarViajeScreen(
                    vehiculo = vehiculo,
                    onBack = { navController.popBackStack() },
                )
            }

            // Reservado para flujo en tiempo real
            composable(
                route = "map/{vehiculo}",
                arguments = listOf(navArgument("vehiculo") {
                    type = NavType.StringType
                    defaultValue = "AUTO"
                }),
            ) { backStack ->
                val vehiculo = backStack.arguments?.getString("vehiculo") ?: "AUTO"
                MapScreen(vehiculo = vehiculo)
            }
        }
    }
}
