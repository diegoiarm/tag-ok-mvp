package com.tagok.app.ui.planificar

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import java.text.NumberFormat
import java.util.Locale

private val SANTIAGO = Point.fromLngLat(-70.6483, -33.4569)

private val EJEMPLO_ORIGEN = GeocodeSuggestion(
    placeName = "Quilicura, Santiago",
    lon = -70.736149,
    lat = -33.360303,
)
private val EJEMPLO_DESTINO = GeocodeSuggestion(
    placeName = "Las Condes, Santiago",
    lon = -70.526799,
    lat = -33.389758,
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

    fun flyToPortico(lat: Double, lon: Double)
    {
        mapViewportState.easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(lon, lat))
                .zoom(16.0)
                .build()
        )
    }

    fun fitRoute()
    {
        val points = route?.points ?: return

        if (points.isEmpty())
            return

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

    Box(modifier = Modifier.fillMaxSize())
    {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState)
        {
            MapEffect(Unit) { mapView ->
                mapView.mapboxMap.subscribeCameraChanged {
                    currentZoom = mapView.mapboxMap.cameraState.zoom
                }
            }
            if (routePoints.size >= 2)
            {
                PolylineAnnotation(points = routePoints) {
                    lineColor = Blue40
                    lineWidth = 5.0
                    lineOpacity = 0.9
                }
            }
            val cruzadosKeys = remember(route?.porticos) {
                route?.porticos
                    ?.map { it.codigo to it.sentido }
                    ?.toSet()
                    ?: emptySet()
            }

            uiState.porticos.forEach { portico ->
                val activo = (portico.codigo to portico.sentido) in cruzadosKeys

                val bitmap = if (activo) bitmapActivo else bitmapNormal

                if (bitmap != null)
                {
                    PointAnnotation(point = Point.fromLngLat(portico.longitud, portico.latitud)) {
                        iconImage = IconImage(bitmap)
                        iconSize = if (activo) 1.5 else 1.0
                    }
                }
            }
        }

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
                        CameraOptions.Builder().zoom((currentZoom + 1.0).coerceAtMost(20.0)).build()
                    )
                },
            )
            MapControlButton(
                icon = Icons.Filled.Remove,
                contentDescription = "Alejar",
                onClick = {
                    mapViewportState.easeTo(
                        CameraOptions.Builder().zoom((currentZoom - 1.0).coerceAtLeast(1.0)).build()
                    )
                },
            )
            if (routePoints.isNotEmpty()) {
                MapControlButton(
                    icon = Icons.Filled.ZoomOutMap,
                    contentDescription = "Ver ruta completa",
                    onClick = { fitRoute() },
                )
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {

                // ------- HEADER siempre visible (barra de título y botón minimizar) -------
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = TextSecondary)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Planificar viaje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(InputBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = vehiculo,
                            style = MaterialTheme.typography.labelSmall,
                            color = Blue40,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Botón para minimizar/expandir
                    IconButton(onClick = { isMinimized = !isMinimized }) {
                        Icon(
                            imageVector = if (isMinimized) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (isMinimized) "Expandir" else "Minimizar",
                            tint = TextSecondary
                        )
                    }
                }

                // ------- CONTENIDO EXPANDIBLE -------
                AnimatedVisibility(visible = !isMinimized) {
                    Column {
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
                                    viewModel.calculateRoute(lon1 = o.lon, lat1 = o.lat, lon2 = d.lon, lat2 = d.lat)
                                },
                                enabled = origenSeleccionado != null && destinoSeleccionado != null && !uiState.isLoadingRoute,
                                modifier = Modifier.weight(2f).height(44.dp),
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

                        if (routePoints.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = InputBackground)
                            Spacer(Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                if (route?.porticos?.isNotEmpty() == true) {
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "· ${route.porticos.size} pórtico(s) en ruta",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Blue40,
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "· sin pórticos en ruta",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Tarifa estimada", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                Text("Vehículo: $vehiculo", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Text(
                                text = "${"%.0f".format(route?.totalCost)} CLP",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Blue40
                            )
                        }

                        if (route?.porticos?.isNotEmpty() == true) {
                            Spacer(Modifier.height(6.dp))
                            Column(
                                modifier = Modifier
                                    .heightIn(max = 100.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                route.porticos.forEach { cruce ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clickable { flyToPortico(cruce.latitud, cruce.longitud) },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = cruce.nombre ?: cruce.codigo,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (cruce.autopista.isNotBlank()) {
                                                Text(cruce.autopista, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                            }
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(cruce.valor.toCLP(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                            Text("Tarifa: ${cruce.tarifa}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                        }
                                    }
                                    HorizontalDivider(color = InputBackground)
                                }
                            }
                        }
                    } // fin Column expandible
                } // fin AnimatedVisibility

                // ------- VERSIÓN MINIMIZADA (cuando isMinimized = true) -------
                AnimatedVisibility(visible = isMinimized) {
                    // Solo se muestra si hay una ruta calculada (o al menos origen y destino seleccionados)
                    if (origenSeleccionado != null && destinoSeleccionado != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Columna con puntos y total
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${origenSeleccionado!!.placeName} → ${destinoSeleccionado!!.placeName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "${"%.0f".format(route?.totalCost)} CLP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Blue40
                                )
                            }

                            // Botón para recalcular (opcional)
                            Button(
                                onClick = {
                                    // Recalcular ruta (mismo código que el botón original)
                                    val o = origenSeleccionado ?: return@Button
                                    val d = destinoSeleccionado ?: return@Button
                                    viewModel.calculateRoute(lon1 = o.lon, lat1 = o.lat, lon2 = d.lon, lat2 = d.lat)
                                },
                                enabled = !uiState.isLoadingRoute,
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Blue40),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                if (uiState.isLoadingRoute) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.Refresh,
                                        contentDescription = "Recalcular",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Si no hay ruta, ocultamos la barra minimizada o mostramos un placeholder
                        Text("Completa origen y destino para calcular", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
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

private fun Any?.toCLP(): String
{
    val number = when (this) {
        is Double -> this
        is Float -> this.toDouble()
        is Int -> this.toDouble()
        is Long -> this.toDouble()
        is String -> this.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }
    val format = NumberFormat.getNumberInstance(Locale("es", "CL"))
    format.minimumFractionDigits = 0
    format.maximumFractionDigits = 0
    return "$${format.format(number)}"
}