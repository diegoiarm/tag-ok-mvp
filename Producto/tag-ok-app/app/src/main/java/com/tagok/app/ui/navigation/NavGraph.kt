package com.tagok.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.tagok.app.data.auth.AuthTokenProvider
import com.tagok.app.domain.vehiculo.TipoVehiculo
import com.tagok.app.supabase
import com.tagok.app.ui.auth.AuthViewModel
import com.tagok.app.ui.auth.LoginScreen
import com.tagok.app.ui.auth.LoginUiState
import com.tagok.app.ui.boleta.BoletaScreen
import com.tagok.app.ui.historial.HistorialScreen
import com.tagok.app.ui.home.HomeScreen
import com.tagok.app.ui.map.MapScreen
import com.tagok.app.ui.perfil.PerfilScreen
import com.tagok.app.ui.planificar.PlanificarViajeScreen
import com.tagok.app.ui.presupuesto.PresupuestoScreen
import com.tagok.app.ui.register.RegisterScreen
import com.tagok.app.ui.register.RegisterViewModel
import com.tagok.app.ui.vehiculos.VehiculosScreen
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

private sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home        : Screen("home",        "Home",        Icons.Filled.Home)
    data object Presupuesto : Screen("presupuesto", "Presupuesto", Icons.Filled.MonetizationOn)
    data object Boleta      : Screen("boleta",      "Boleta",      Icons.Filled.Description)
    data object Perfil      : Screen("perfil",      "Perfil",      Icons.Filled.Person)
}

private val bottomNavScreens = listOf(Screen.Home, Screen.Presupuesto, Screen.Boleta, Screen.Perfil)
private val routesSinBottomBar = setOf("login", "register")

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStack?.destination
    val scope = rememberCoroutineScope()

    val hasSession = remember { AuthTokenProvider.hasSession() }
    val startDestination = if (hasSession) Screen.Home.route else "login"

    val selectedColor = Color(0xFF3D3DBF)
    val unselectedColor = Color(0xFF9E9E9E)

    LaunchedEffect(Unit) {
        AuthTokenProvider.sessionFlow.collect { isAuthenticated ->
            val route = navController.currentDestination?.route
            if (isAuthenticated && route in routesSinBottomBar) {
                navController.navigate(Screen.Home.route) {
                    popUpTo("login") { inclusive = true }
                }
            } else if (!isAuthenticated && route !in routesSinBottomBar) {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val showBottomBar = currentDestination?.route !in routesSinBottomBar

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets(0)
                ) {
                    bottomNavScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == screen.route } == true

                        NavigationBarItem(
                            selected = selected,
                            alwaysShowLabel = true,
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
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = selectedColor,
                                selectedTextColor = selectedColor,
                                unselectedIconColor = unselectedColor,
                                unselectedTextColor = unselectedColor,
                                indicatorColor = Color.Transparent,
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                val authViewModel: AuthViewModel = viewModel()
                val uiState by authViewModel.uiState.collectAsState()

                LaunchedEffect(uiState) {
                    if (uiState is LoginUiState.Success) {
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
                    onClearError = authViewModel::clearError
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
                    viewModel = regViewModel
                )
            }

            composable(Screen.Home.route) {
                val nombre = remember {
                    supabase.auth.currentUserOrNull()
                        ?.userMetadata?.get("nombre")?.jsonPrimitive?.contentOrNull
                        ?: "Usuario"
                }

                HomeScreen(
                    nombre = nombre,
                    onPlanificarViaje = { v -> navController.navigate("planificar/$v") },
                    onHistorialViajes = { navController.navigate("historial") },
                    onIrARuta = { v -> navController.navigate("map/$v") },
                    onAgregarVehiculo = { navController.navigate("vehiculos") },
                    onLogout = {
                        scope.launch {
                            supabase.auth.signOut()
                        }
                    }
                )
            }

            composable(Screen.Presupuesto.route) {
                PresupuestoScreen()
            }

            composable(Screen.Boleta.route) {
                BoletaScreen()
            }

            composable(Screen.Perfil.route) {
                PerfilScreen(
                    onVehiculos = { navController.navigate("vehiculos") },
                    onMisRutas = { navController.navigate("historial") }
                )
            }

            composable("vehiculos") {
                VehiculosScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = "planificar/{vehiculo}",
                arguments = listOf(
                    navArgument("vehiculo") {
                        type = NavType.StringType
                        defaultValue = "AUTO"
                    }
                )
            ) { backStack ->
                val vehiculoString = backStack.arguments?.getString("vehiculo") ?: "AUTO"
                val vehiculo = try {
                    TipoVehiculo.valueOf(vehiculoString)
                } catch (e: IllegalArgumentException) {
                    TipoVehiculo.AUTO
                }

                PlanificarViajeScreen(
                    vehiculo = vehiculo,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("historial") {
                if (AuthTokenProvider.hasSession()) {
                    HistorialScreen(onBack = { navController.popBackStack() })
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }

            composable(
                route = "map/{vehiculo}",
                arguments = listOf(
                    navArgument("vehiculo") {
                        type = NavType.StringType
                        defaultValue = "AUTO"
                    }
                ))
            { backStack ->
                val vehiculoString = backStack.arguments?.getString("vehiculo") ?: "AUTO"
                val vehiculo = try
                {
                    TipoVehiculo.valueOf(vehiculoString)
                }
                catch (e: IllegalArgumentException)
                {
                    TipoVehiculo.AUTO
                }

                MapScreen(vehiculo = vehiculo)
            }
        }
    }
}