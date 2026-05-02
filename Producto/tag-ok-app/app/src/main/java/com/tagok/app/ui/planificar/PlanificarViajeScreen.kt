package com.tagok.app.ui.planificar

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
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
import com.tagok.app.R
import com.tagok.app.data.GeocodeSuggestion
import com.tagok.app.data.GeocodingRepository
import com.tagok.app.ui.map.MapViewModel
import com.tagok.app.ui.map.vectorToBitmap
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private val SANTIAGO = Point.fromLngLat(-70.6483, -33.4569)

private val EJEMPLO_ORIGEN = GeocodeSuggestion(
    placeName = "Costanera Norte, Vitacura, Santiago",
    lon = -70.7000,
    lat = -33.5100,
)
private val EJEMPLO_DESTINO = GeocodeSuggestion(
    placeName = "Lo Barnechea, Santiago",
    lon = -70.8000,
    lat = -33.4200,
)

@SuppressLint("MissingPermission")
private fun flyToCurrentLocation(context: Context, mapViewportState: MapViewportState) {
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
private fun MapControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .shadow(4.dp, CircleShape),
        shape = CircleShape,
        color = Color.White,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.padding(10.dp),
            tint = Color(0xFF374151),
        )
    }
}

@Composable
fun PlanificarViajeScreen(
    vehiculo: String = "AUTO",
    onBack: () -> Unit = {},
    viewModel: MapViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var currentZoom by remember { mutableStateOf(11.5) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(SANTIAGO)
            zoom(11.5)
            pitch(0.0)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) flyToCurrentLocation(context, mapViewportState)
    }

    fun requestLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            flyToCurrentLocation(context, mapViewportState)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun fitRoute() {
        val points = uiState.routePoints
        if (points.isEmpty()) return
        val lons = points.map { it.longitude() }
        val lats = points.map { it.latitude() }
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
                .build()
        )
    }

    // Origen
    var origenText by rememberSaveable { mutableStateOf("") }
    var origenSeleccionado by remember { mutableStateOf<GeocodeSuggestion?>(null) }
    var origenSugerencias by remember { mutableStateOf<List<GeocodeSuggestion>>(emptyList()) }
    var buscandoOrigen by remember { mutableStateOf(false) }

    // Destino
    var destinoText by rememberSaveable { mutableStateOf("") }
    var destinoSeleccionado by remember { mutableStateOf<GeocodeSuggestion?>(null) }
    var destinoSugerencias by remember { mutableStateOf<List<GeocodeSuggestion>>(emptyList()) }
    var buscandoDestino by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.setVehiculo(vehiculo) }
    DisposableEffect(Unit) { onDispose { viewModel.resetMap() } }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Debounce geocoding — origen
    LaunchedEffect(origenText) {
        if (origenSeleccionado != null) return@LaunchedEffect
        origenSugerencias = emptyList()
        if (origenText.length >= 3) {
            delay(600)
            buscandoOrigen = true
            runCatching { GeocodingRepository.buscar(origenText) }
                .onSuccess { origenSugerencias = it }
            buscandoOrigen = false
        }
    }

    // Debounce geocoding — destino
    LaunchedEffect(destinoText) {
        if (destinoSeleccionado != null) return@LaunchedEffect
        destinoSugerencias = emptyList()
        if (destinoText.length >= 3) {
            delay(600)
            buscandoDestino = true
            runCatching { GeocodingRepository.buscar(destinoText) }
                .onSuccess { destinoSugerencias = it }
            buscandoDestino = false
        }
    }

    val bitmapNormal = remember { vectorToBitmap(context, R.drawable.ic_portico) }
    val bitmapActivo = remember { vectorToBitmap(context, R.drawable.ic_portico_activo) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Mapa base
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
        ) {
            MapEffect(Unit) { mapView ->
                mapView.mapboxMap.subscribeCameraChanged {
                    currentZoom = mapView.mapboxMap.cameraState.zoom
                }
            }
            if (uiState.routePoints.size >= 2) {
                PolylineAnnotation(points = uiState.routePoints) {
                    lineColor = Blue40
                    lineWidth = 5.0
                    lineOpacity = 0.9
                }
            }
            val cruzadosIds = uiState.porticosCruzados.map { it.id }.toSet()
            uiState.porticos.forEach { portico ->
                val activo = portico.id in cruzadosIds
                val bitmap = if (activo) bitmapActivo else bitmapNormal
                if (bitmap != null) {
                    PointAnnotation(point = Point.fromLngLat(portico.longitud, portico.latitud)) {
                        iconImage = IconImage(bitmap)
                        iconSize = if (activo) 1.5 else 1.0
                    }
                }
            }
        }

        // Controles de mapa — esquina superior derecha
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MapControlButton(
                icon = Icons.Filled.MyLocation,
                contentDescription = "Mi ubicación",
                onClick = { requestLocation() },
            )
            MapControlButton(
                icon = Icons.Filled.Add,
                contentDescription = "Acercar",
                onClick = {
                    mapViewportState.easeTo(
                        CameraOptions.Builder().zoom(currentZoom + 1.0).build()
                    )
                },
            )
            MapControlButton(
                icon = Icons.Filled.Remove,
                contentDescription = "Alejar",
                onClick = {
                    mapViewportState.easeTo(
                        CameraOptions.Builder().zoom(currentZoom - 1.0).build()
                    )
                },
            )
            if (uiState.routePoints.isNotEmpty()) {
                MapControlButton(
                    icon = Icons.Filled.ZoomOutMap,
                    contentDescription = "Ver ruta completa",
                    onClick = { fitRoute() },
                )
            }
        }

        // Panel inferior unificado: búsqueda + resultados
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {

                // Cabecera: volver + título + badge vehículo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextSecondary,
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Planificar viaje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(InputBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = vehiculo,
                            style = MaterialTheme.typography.labelSmall,
                            color = Blue40,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                DireccionField(
                    value = origenText,
                    onValueChange = { nuevo ->
                        origenText = nuevo
                        if (origenSeleccionado?.placeName != nuevo) origenSeleccionado = null
                    },
                    sugerencias = origenSugerencias,
                    onSugerenciaClick = { s ->
                        origenText = s.placeName
                        origenSeleccionado = s
                        origenSugerencias = emptyList()
                    },
                    label = "Origen",
                    leadingIcon = Icons.Filled.MyLocation,
                    cargando = buscandoOrigen,
                )

                Spacer(Modifier.height(8.dp))

                DireccionField(
                    value = destinoText,
                    onValueChange = { nuevo ->
                        destinoText = nuevo
                        if (destinoSeleccionado?.placeName != nuevo) destinoSeleccionado = null
                    },
                    sugerencias = destinoSugerencias,
                    onSugerenciaClick = { s ->
                        destinoText = s.placeName
                        destinoSeleccionado = s
                        destinoSugerencias = emptyList()
                    },
                    label = "Destino",
                    leadingIcon = Icons.Filled.Place,
                    cargando = buscandoDestino,
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            origenText = EJEMPLO_ORIGEN.placeName
                            origenSeleccionado = EJEMPLO_ORIGEN
                            destinoText = EJEMPLO_DESTINO.placeName
                            destinoSeleccionado = EJEMPLO_DESTINO
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary),
                    ) {
                        Text("Usar ejemplo", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = {
                            val o = origenSeleccionado ?: return@Button
                            val d = destinoSeleccionado ?: return@Button
                            viewModel.calculateRoute(
                                lon1 = o.lon, lat1 = o.lat,
                                lon2 = d.lon, lat2 = d.lat,
                            )
                        },
                        enabled = origenSeleccionado != null
                                && destinoSeleccionado != null
                                && !uiState.isLoadingRoute
                                && !uiState.isLoadingTarifa,
                        modifier = Modifier
                            .weight(2f)
                            .height(44.dp),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue40),
                    ) {
                        if (uiState.isLoadingRoute) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(text = "Calcular ruta", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }

                // Resultados de la ruta (aparecen bajo el formulario en la misma Card)
                if (uiState.routePoints.isNotEmpty() || uiState.isLoadingTarifa || uiState.tarifaCalculada != null) {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = InputBackground)
                    Spacer(Modifier.height(8.dp))

                    if (uiState.routePoints.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text("●", color = Blue40, fontSize = 10.sp)
                            Text(
                                text = "${uiState.routePoints.size} segmentos",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                            if (uiState.porticosCruzados.isNotEmpty()) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "· ${uiState.porticosCruzados.size} pórtico(s) en ruta",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Blue40,
                                    fontWeight = FontWeight.Medium,
                                )
                            } else if (!uiState.isLoadingTarifa) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "· sin pórticos en ruta",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                        }
                    }

                    when {
                        uiState.isLoadingTarifa -> {
                            Spacer(Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Blue40,
                                )
                                Text(
                                    text = "Calculando tarifa...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                        }

                        uiState.tarifaCalculada != null -> {
                            val tarifa = uiState.tarifaCalculada!!
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(
                                        text = "Tarifa estimada",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextSecondary,
                                    )
                                    Text(
                                        text = "Vehículo: ${tarifa.vehiculo}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                    )
                                }
                                Text(
                                    text = "${"%.0f".format(tarifa.total)} CLP",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Blue40,
                                )
                            }
                            if (tarifa.portico.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                tarifa.portico.take(4).forEach { cruce ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = "${cruce.codigo}  ·  ${cruce.autopista ?: "Autopista"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                        )
                                        Text(
                                            text = "${"%.0f".format(cruce.valor)} CLP",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                                if (tarifa.portico.size > 4) {
                                    Text(
                                        text = "+${tarifa.portico.size - 4} pórticos más",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 2.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF1F2937),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}

@Composable
private fun DireccionField(
    value: String,
    onValueChange: (String) -> Unit,
    sugerencias: List<GeocodeSuggestion>,
    onSugerenciaClick: (GeocodeSuggestion) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    cargando: Boolean,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            placeholder = { Text("Dirección, negocio o lugar", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Blue40,
                    modifier = Modifier.size(18.dp),
                )
            },
            trailingIcon = {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Blue40,
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue40,
                unfocusedBorderColor = InputBackground,
                focusedLabelColor = Blue40,
            ),
        )

        if (sugerencias.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column {
                    sugerencias.take(5).forEachIndexed { index, sugerencia ->
                        TextButton(
                            onClick = { onSugerenciaClick(sugerencia) },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = sugerencia.placeName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (index < sugerencias.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                color = InputBackground,
                            )
                        }
                    }
                }
            }
        }
    }
}
