package com.tagok.app.ui.planificar

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation
import com.tagok.app.R
import com.tagok.app.data.GeocodeSuggestion
import com.tagok.app.data.GeocodingRepository
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Tramo
import com.tagok.app.ui.components.map.MapControls
import com.tagok.app.ui.components.routes.TollItem
import com.tagok.app.ui.map.MapViewModel
import com.tagok.app.ui.map.PorticosContainer
import com.tagok.app.ui.map.vectorToBitmap
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private val SANTIAGO = Point.fromLngLat(-70.6483, -33.4569)

private val EJEMPLO_ORIGEN = GeocodeSuggestion(placeName = "Inicio", lon = -70.701013, lat = -33.596694)
private val EJEMPLO_DESTINO = GeocodeSuggestion(placeName = "Fin", lon = -70.565223, lat = -33.430858)

@SuppressLint("MissingPermission")
private fun flyToCurrentLocation(context: Context, mapViewportState: com.mapbox.maps.extension.compose.animation.viewport.MapViewportState)
{
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    location?.let {
        mapViewportState.easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(it.longitude, it.latitude))
                .zoom(15.0)
                .build()
        )
    }
}

@Composable
fun PlanificarViajeScreen(
    vehiculo: String = "AUTO",
    onBack: () -> Unit = {},
    viewModel: MapViewModel = viewModel(factory = MapViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var currentZoom by remember { mutableStateOf(11.5) }
    var isMinimized by remember { mutableStateOf(false) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(SANTIAGO)
            zoom(11.5)
            pitch(0.0)
        }
    }

    val route = uiState.route
    val routePoints = remember(route) {
        route?.points?.map { Point.fromLngLat(it.lon, it.lat) } ?: emptyList()
    }

    // ── Geocoding state ──────────────────────────────────────────────────────
    var origenText by rememberSaveable { mutableStateOf("") }
    var origenSeleccionado by remember { mutableStateOf<GeocodeSuggestion?>(null) }
    var origenSugerencias by remember { mutableStateOf<List<GeocodeSuggestion>>(emptyList()) }
    var buscandoOrigen by remember { mutableStateOf(false) }

    var destinoText by rememberSaveable { mutableStateOf("") }
    var destinoSeleccionado by remember { mutableStateOf<GeocodeSuggestion?>(null) }
    var destinoSugerencias by remember { mutableStateOf<List<GeocodeSuggestion>>(emptyList()) }
    var buscandoDestino by remember { mutableStateOf(false) }

    // ── Permissions ──────────────────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission())
    { granted -> if (granted) flyToCurrentLocation(context, mapViewportState) }

    fun requestLocation()
    {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
            flyToCurrentLocation(context, mapViewportState)
        else
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // ── Camera helpers ───────────────────────────────────────────────────────
    fun flyToPortico(lat: Double, lon: Double)
    {
        mapViewportState.easeTo(
            CameraOptions.Builder().center(Point.fromLngLat(lon, lat)).zoom(16.0).build())
    }

    fun fitRoute()
    {
        val points = route?.points?.takeIf { it.isNotEmpty() } ?: return
        val lons = points.map { it.lon }
        val lats = points.map { it.lat }
        val span = maxOf(lons.max() - lons.min(), lats.max() - lats.min())
        mapViewportState.easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat((lons.min() + lons.max()) / 2.0, (lats.min() + lats.max()) / 2.0))
                .zoom(when {
                    span < 0.01 -> 14.0
                    span < 0.03 -> 13.0
                    span < 0.06 -> 12.0
                    span < 0.12 -> 11.0
                    span < 0.3  -> 10.0
                    else        -> 9.0
                })
                .build())
    }

    // ── Side effects ─────────────────────────────────────────────────────────
    LaunchedEffect(Unit) { viewModel.setVehiculo(vehiculo) }
    DisposableEffect(Unit) { onDispose { viewModel.resetMap() } }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(origenText)
    {
        if (origenSeleccionado != null)
            return@LaunchedEffect

        origenSugerencias = emptyList()

        if (origenText.length >= 3)
        {
            delay(600)
            buscandoOrigen = true
            runCatching { GeocodingRepository.buscar(origenText) }.onSuccess { origenSugerencias = it }
            buscandoOrigen = false
        }
    }

    LaunchedEffect(destinoText)
    {
        if (destinoSeleccionado != null)
            return@LaunchedEffect

        destinoSugerencias = emptyList()

        if (destinoText.length >= 3)
        {
            delay(600)
            buscandoDestino = true
            runCatching { GeocodingRepository.buscar(destinoText) }.onSuccess { destinoSugerencias = it }
            buscandoDestino = false
        }
    }

    // ── Bitmaps ───────────────────────────────────────────────────────────────
    val bitmapNormal = remember { vectorToBitmap(context, R.drawable.ic_portico) }
    val bitmapActivo = remember { vectorToBitmap(context, R.drawable.ic_portico_activo) }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize())
    {

        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,)
        {
            MapEffect(Unit) { mapView ->
                mapView.mapboxMap.subscribeCameraChanged {
                    currentZoom = mapView.mapboxMap.cameraState.zoom
                }
            }

            if (routePoints.size >= 2)
                PolylineAnnotation(points = routePoints) {
                    lineColor = Blue40
                    lineWidth = 5.0
                    lineOpacity = 0.9
                }

            val crossedIds: Set<Long> = remember(route?.tolls) {
                route?.tolls?.flatMap { toll ->
                    when (toll)
                    {
                        is Portico -> listOf(toll.porticoId)
                        is Tramo  -> listOf(toll.entradaId, toll.salidaId)
                    }
                }?.toSet() ?: emptySet()
            }

            PorticosContainer(
                context = context,
                porticos = uiState.porticos,
                route = route)
        }

        MapControls(
            mapViewportState = mapViewportState,
            currentZoom = currentZoom,
            hasRoute = routePoints.isNotEmpty(),
            onMyLocation = { requestLocation() },
            onFitRoute = { fitRoute() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 12.dp))

        RouteBottomCard(
            vehiculo = vehiculo,
            isMinimized = isMinimized,
            onToggleMinimized = { isMinimized = !isMinimized },
            onBack = onBack,
            origenText = origenText,
            onOrigenChange = { nuevo ->
                origenText = nuevo
                if (origenSeleccionado?.placeName != nuevo) origenSeleccionado = null
            },
            origenSugerencias = origenSugerencias,
            onOrigenSugerenciaClick = { s ->
                origenText = s.placeName
                origenSeleccionado = s
                origenSugerencias = emptyList()
            },
            origenSeleccionado = origenSeleccionado,
            buscandoOrigen = buscandoOrigen,
            destinoText = destinoText,
            onDestinoChange = { nuevo ->
                destinoText = nuevo
                if (destinoSeleccionado?.placeName != nuevo) destinoSeleccionado = null
            },
            destinoSugerencias = destinoSugerencias,
            onDestinoSugerenciaClick = { s ->
                destinoText = s.placeName
                destinoSeleccionado = s
                destinoSugerencias = emptyList()
            },
            destinoSeleccionado = destinoSeleccionado,
            buscandoDestino = buscandoDestino,
            route = route,
            hasRoutePoints = routePoints.isNotEmpty(),
            isLoadingRoute = uiState.isLoadingRoute,
            onCalcular = {
                val o = origenSeleccionado ?: return@RouteBottomCard
                val d = destinoSeleccionado ?: return@RouteBottomCard
                viewModel.calculateRoute(lon1 = o.lon, lat1 = o.lat, lon2 = d.lon, lat2 = d.lat)
            },
            onUsarEjemplo = {
                origenText = EJEMPLO_ORIGEN.placeName
                origenSeleccionado = EJEMPLO_ORIGEN
                destinoText = EJEMPLO_DESTINO.placeName
                destinoSeleccionado = EJEMPLO_DESTINO
            },
            onFlyToPortico = { lat, lon -> flyToPortico(lat, lon) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp))
    }
}