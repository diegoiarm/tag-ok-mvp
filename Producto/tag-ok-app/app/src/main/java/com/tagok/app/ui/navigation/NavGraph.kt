package com.tagok.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.tagok.app.ui.boleta.comparacion.ComparacionScreen
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
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

private data class NavItem(
    val route: String,
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
)

private val bottomNavItems = listOf(
    NavItem("home",        "Home",        Icons.Filled.Home,                 Icons.Outlined.Home),
    NavItem("presupuesto", "Presupuesto", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    NavItem("boleta",      "Boleta",      Icons.Filled.Description,          Icons.Outlined.Description),
    NavItem("perfil",      "Perfil",      Icons.Filled.Person,               Icons.Outlined.Person),
)

private val routesSinBottomBar = setOf("login", "register")

private val SelectedBlue   = Color(0xFF1C42B1)
private val UnselectedGray = Color(0xFF9E9E9E)
private val IndicatorBlue  = Color(0xFFEEF2FF)  // más sutil que antes (0xFFDDE5FF)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStack?.destination
    val scope = rememberCoroutineScope()

    val hasSession = remember { AuthTokenProvider.hasSession() }
    val startDestination = if (hasSession) "home" else "login"

    LaunchedEffect(Unit) {
        AuthTokenProvider.sessionFlow.collect { isAuthenticated ->
            // null = sesión aún cargando (p.ej. tras recrear el proceso al volver del
            // selector de archivos): no tocar la navegación hasta que se resuelva.
            if (isAuthenticated == null) return@collect
            val route = navController.currentDestination?.route
            if (isAuthenticated && route in routesSinBottomBar) {
                navController.navigate("home") {
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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    tonalElevation = 0.dp
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()  // respeta el home indicator
                            .height(80.dp),
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets(0)
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy
                                ?.any { it.route == item.route } == true

                            NavigationBarItem(
                                selected = selected,
                                alwaysShowLabel = true,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.iconFilled else item.iconOutlined,
                                        contentDescription = item.label,
                                        modifier = Modifier.height(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor   = SelectedBlue,
                                    selectedTextColor   = SelectedBlue,
                                    unselectedIconColor = UnselectedGray,
                                    unselectedTextColor = UnselectedGray,
                                    indicatorColor      = IndicatorBlue,
                                )
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                val authViewModel: AuthViewModel = viewModel()
                val uiState by authViewModel.uiState.collectAsState()

                LaunchedEffect(uiState) {
                    if (uiState is LoginUiState.Success) {
                        navController.navigate("home") {
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
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    viewModel = regViewModel
                )
            }

            composable("home") {
                val nombre = remember {
                    supabase.auth.currentUserOrNull()
                        ?.userMetadata?.get("nombre")?.jsonPrimitive?.contentOrNull
                        ?: supabase.auth.currentUserOrNull()
                            ?.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
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

            composable("presupuesto") {
                PresupuestoScreen()
            }

            composable("boleta") {
                BoletaScreen(
                    onVerificarFactura = { patente, desde, hasta, autopistas ->
                        val autopistasArg = android.net.Uri.encode(autopistas.joinToString("|"))
                        navController.navigate(
                            "comparacion?patente=${android.net.Uri.encode(patente)}" +
                                "&desde=$desde&hasta=$hasta&autopistas=$autopistasArg")
                    })
            }

            composable(
                route = "comparacion?patente={patente}&desde={desde}&hasta={hasta}&autopistas={autopistas}",
                arguments = listOf(
                    navArgument("patente") { type = NavType.StringType; defaultValue = "" },
                    navArgument("desde") { type = NavType.StringType; defaultValue = "" },
                    navArgument("hasta") { type = NavType.StringType; defaultValue = "" },
                    navArgument("autopistas") { type = NavType.StringType; defaultValue = "" }))
            { backStack ->
                val args = backStack.arguments
                val hoy = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date

                val fechaDesde = try {
                    LocalDate.parse(args?.getString("desde") ?: "")
                } catch (e: Exception) { hoy }

                val fechaHasta = try {
                    LocalDate.parse(args?.getString("hasta") ?: "")
                } catch (e: Exception) { hoy }

                ComparacionScreen(
                    patente = args?.getString("patente") ?: "",
                    fechaDesde = fechaDesde,
                    fechaHasta = fechaHasta,
                    autopistas = (args?.getString("autopistas") ?: "")
                        .split("|")
                        .filter { it.isNotBlank() },
                    onBack = { navController.popBackStack() })
            }

            composable("perfil") {
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
                )
            ) { backStack ->
                val vehiculoString = backStack.arguments?.getString("vehiculo") ?: "AUTO"
                val vehiculo = try {
                    TipoVehiculo.valueOf(vehiculoString)
                } catch (e: IllegalArgumentException) {
                    TipoVehiculo.AUTO
                }

                MapScreen()
            }
        }
    }
}