package com.tagok.app.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

private val SANTIAGO = Point.fromLngLat(-70.6483, -33.4569)

internal fun vectorToBitmap(context: android.content.Context, @DrawableRes resId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, resId) ?: return null
    val w = drawable.intrinsicWidth.takeIf { it > 0 } ?: 64
    val h = drawable.intrinsicHeight.takeIf { it > 0 } ?: 64
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, w, h)
    drawable.draw(canvas)
    return bitmap
}

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
fun MapScreen(
    vehiculo: String = "AUTO",
    viewModel: MapViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var currentZoom by remember { mutableStateOf(12.5) }

    LaunchedEffect(Unit) { viewModel.setVehiculo(vehiculo) }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetMap() }
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(SANTIAGO)
            zoom(12.5)
            pitch(0.0)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val bitmapNormal = remember { vectorToBitmap(context, R.drawable.ic_portico) }
    val bitmapActivo = remember { vectorToBitmap(context, R.drawable.ic_portico_activo) }

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
        val centerLon = (lons.min() + lons.max()) / 2.0
        val centerLat = (lats.min() + lats.max()) / 2.0
        val span = maxOf(lons.max() - lons.min(), lats.max() - lats.min())
        val zoom = when {
            span < 0.01 -> 14.0
            span < 0.03 -> 13.0
            span < 0.06 -> 12.0
            span < 0.12 -> 11.0
            span < 0.3  -> 10.0
            else        -> 9.0
        }
        mapViewportState.easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(centerLon, centerLat))
                .zoom(zoom)
                .build()
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

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
                    PointAnnotation(
                        point = Point.fromLngLat(portico.longitud, portico.latitud),
                    ) {
                        iconImage = IconImage(bitmap)
                        iconSize = if (activo) 1.5 else 1.0
                    }
                }
            }
        }

        // Controles flotantes — columna derecha
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
                    val newZoom = (currentZoom + 1.0).coerceAtMost(20.0)
                    mapViewportState.easeTo(CameraOptions.Builder().zoom(newZoom).build())
                },
            )
            MapControlButton(
                icon = Icons.Filled.Remove,
                contentDescription = "Alejar",
                onClick = {
                    val newZoom = (currentZoom - 1.0).coerceAtLeast(1.0)
                    mapViewportState.easeTo(CameraOptions.Builder().zoom(newZoom).build())
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

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Calcular ruta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(InputBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = uiState.vehiculo,
                            style = MaterialTheme.typography.labelSmall,
                            color = Blue40,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                if (uiState.routePoints.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text("●", color = Blue40, fontSize = 10.sp)
                        Text(
                            text = "${uiState.routePoints.size} segmentos trazados",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                        if (uiState.porticosCruzados.isEmpty() && !uiState.isLoadingTarifa) {
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
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = InputBackground)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Tarifa estimada",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                            )
                            Text(
                                text = "${"%.0f".format(tarifa.total)} CLP",
                                style = MaterialTheme.typography.titleMedium,
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
                                        .padding(vertical = 2.dp),
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

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = viewModel::calculateTestRoute,
                    enabled = !uiState.isLoadingRoute,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue40),
                ) {
                    if (uiState.isLoadingRoute) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(text = "Calcular ruta de prueba", fontWeight = FontWeight.SemiBold)
                    }
                }

                if (uiState.porticos.isNotEmpty() && !uiState.isLoadingTarifa && !uiState.isLoadingRoute) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = viewModel::calculateTestTarifa,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = InputBackground),
                    ) {
                        Text(
                            text = "Probar API de tarifas",
                            fontWeight = FontWeight.SemiBold,
                            color = Blue40,
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
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
