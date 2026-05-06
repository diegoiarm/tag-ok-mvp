package com.tagok.app.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.tagok.app.R
import androidx.core.graphics.createBitmap

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

@SuppressLint("RememberReturnType")
@Composable
fun MapScreen(
    vehiculo: String = "AUTO",
    viewModel: MapViewModel = viewModel(factory = MapViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var currentZoom by remember { mutableStateOf(12.5) }
    var userLocation by remember { mutableStateOf<Point?>(null) }

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

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission())
    { granted ->
        if (granted)
            flyToCurrentLocation(context, mapViewportState)
    }

    fun requestLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
        {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            location?.let {
                val point = Point.fromLngLat(it.longitude, it.latitude)
                userLocation = point
                mapViewportState.easeTo(
                    CameraOptions.Builder().center(point).zoom(15.0).build()
                )
            }
        }
        else
        {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val bitmapNormal = remember { vectorToBitmap(context, R.drawable.ic_portico) }

    val bitmapUsuario = remember {
        val bmp = createBitmap(48, 48)
        val canvas = Canvas(bmp)
        val paint = Paint().apply {
            color = android.graphics.Color.BLUE
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        canvas.drawCircle(24f, 24f, 20f, paint)
        bmp
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState)
        {
            MapEffect(Unit) { mapView ->
                mapView.mapboxMap.subscribeCameraChanged {
                    currentZoom = mapView.mapboxMap.cameraState.zoom
                }
            }

            uiState.porticos.forEach { portico ->
                val bitmap = bitmapNormal
                if (bitmap != null) {
                    PointAnnotation(point = Point.fromLngLat(portico.longitud, portico.latitud)) {
                        iconImage = IconImage(bitmap)
                        iconSize = 1.0
                    }
                }
            }

            if (userLocation != null)
            {
                PointAnnotation(point = userLocation!!) {
                    iconImage = IconImage(bitmapUsuario)
                    iconSize = 1.0
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapControlButton(
                icon = Icons.Filled.MyLocation,
                contentDescription = "Mi ubicación",
                onClick = { requestLocation() }
            )
            MapControlButton(
                icon = Icons.Filled.Add,
                contentDescription = "Acercar",
                onClick = {
                    val newZoom = (currentZoom + 1.0).coerceAtMost(20.0)
                    mapViewportState.easeTo(CameraOptions.Builder().zoom(newZoom).build())
                }
            )
            MapControlButton(
                icon = Icons.Filled.Remove,
                contentDescription = "Alejar",
                onClick = {
                    val newZoom = (currentZoom - 1.0).coerceAtLeast(1.0)
                    mapViewportState.easeTo(CameraOptions.Builder().zoom(newZoom).build())
                }
            )
        }
    }
}