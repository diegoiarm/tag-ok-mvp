package com.tagok.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.tagok.app.supabase
import com.tagok.app.ui.auth.AuthViewModel
import com.tagok.app.ui.auth.LoginScreen
import com.tagok.app.ui.home.HomeScreen
import com.tagok.app.ui.register.RegisterScreen
import com.tagok.app.ui.register.RegisterViewModel
import com.tagok.app.ui.map.MapScreen
import com.tagok.app.ui.perfil.PerfilScreen
import com.tagok.app.ui.vehiculos.VehiculosScreen
import com.tagok.app.ui.planificar.PlanificarViajeScreen
import com.tagok.app.ui.boleta.BoletaScreen
import com.tagok.app.ui.presupuesto.PresupuestoScreen
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Modificador para aplicar gradiente basado en el tamaño real del componente.
 */
fun Modifier.gradientTint(colors: List<Color>): Modifier =
    this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithCache {
            val brush = Brush.linearGradient(
                colors = colors,
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )
            onDrawWithContent {
                drawContent()
                drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
            }
        }

private sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home        : Screen("home",        "Home",        Icons.Filled.Home)
    data object Presupuesto : Screen("presupuesto", "Presupuesto", Icons.Filled.MonetizationOn)
    data object Boleta      : Screen("boleta",      "Boleta",      Icons.Filled.Description)
    data object Perfil      : Screen("perfil",      "Perfil",      Icons.Filled.Person)
}

private val bottomNavScreens = listOf(Screen.Home, Screen.Presupuesto, Screen.Boleta, Screen.Perfil)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStack?.destination
    val scope = rememberCoroutineScope()

    val hasSession = supabase.auth.currentSessionOrNull() != null
    val startDestination = if (hasSession) Screen.Home.route else "login"

    // Colores Gradiente
    val purpleGradient = listOf(Color(0xFF3D257B), Color(0xFF6750A4))
    val bottomBarBackground = Color(0xFFF1EEFF)

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

    val routesSinBottomBar = setOf("login", "register")
    val showBottomBar = currentDestination?.route !in routesSinBottomBar

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp) // Más ancho para evitar que el texto se corra
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .height(80.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(32.dp),
                                ambientColor = Color.Black.copy(alpha = 0.4f),
                                spotColor = purpleGradient[0].copy(alpha = 0.3f)
                            )
                            .clip(RoundedCornerShape(32.dp)),
                        containerColor = bottomBarBackground,
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
                                        contentDescription = screen.label,
                                        modifier = if (selected) {
                                            Modifier.gradientTint(purpleGradient)
                                        } else {
                                            Modifier
                                        },
                                        tint = if (selected) Color.White else Color.Gray
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.label,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Visible,
                                        softWrap = false,
                                        style = if (selected) {
                                            TextStyle(
                                                brush = Brush.linearGradient(colors = purpleGradient),
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            TextStyle(color = Color.Gray)
                                        }
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Transparent,
                                    selectedTextColor = Color.Transparent,
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray,
                                    indicatorColor = Color.Transparent,
                                ),
                            )
                        }
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

                LaunchedEffect(uiState) {
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
                    onAgregarVehiculo = { navController.navigate("vehiculos") },
                    onLogout = {
                        scope.launch {
                            supabase.auth.signOut()
                        }
                    },
                )
            }
            composable(Screen.Presupuesto.route) { PresupuestoScreen() }
            composable(Screen.Boleta.route)      { BoletaScreen() }
            composable(Screen.Perfil.route) {
                PerfilScreen(
                    onVehiculos = { navController.navigate("vehiculos") },
                    onMisRutas  = { /* TODO: historial */ },
                )
            }
            composable("vehiculos") {
                VehiculosScreen(onBack = { navController.popBackStack() })
            }

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
