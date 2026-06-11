package com.tagok.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.domain.vehiculo.TipoVehiculo
import com.tagok.app.ui.map.portico.porticoContainer.PorticosContainer
import com.tagok.app.ui.theme.InputBackground

private val SANTIAGO = Point.fromLngLat(-70.6483, -33.4569)

private val NavyBlue = Color(0xFF172955)
private val PageBg = Color(0xFFF4F6FB)
private val IndicatorBlue  = Color(0xFFEEF2FF)

@Composable
fun HomeScreen(
    nombre: String = "Usuario",
    onPlanificarViaje: (vehiculo: String) -> Unit,
    onHistorialViajes: () -> Unit,
    onIrARuta: (vehiculo: String) -> Unit,
    onAgregarVehiculo: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val vehiculos by viewModel.vehiculos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(null) }

    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.cargar()
        }
    }

    LaunchedEffect(vehiculos) {
        if (vehiculoSeleccionado == null || !vehiculos.any { it.id == vehiculoSeleccionado?.id }) {
            vehiculoSeleccionado = vehiculos.firstOrNull { it.esPrincipal } ?: vehiculos.firstOrNull()
        }
    }

    val tipoVehiculo = vehiculoSeleccionado?.tipoVehiculo ?: "AUTO"

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(SANTIAGO)
            zoom(10.0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        Spacer(Modifier.height(16.dp))

        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(InputBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = NavyBlue
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "¡Hola $nombre!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Text(
                    text = "¿Dónde vamos hoy?",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.weight(1f))
            // Botón campana
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(IndicatorBlue)
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF1C42B1),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Tarjeta Vehículo Activo ──────────────────────────────────────────
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 29.dp, vertical = 12.dp)) {
                Text(
                    text = "VEHÍCULO ACTIVO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C42B1),
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(12.dp))
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = NavyBlue
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(vehiculos) { v ->
                            VehiculoMiniChip(
                                vehiculo = v,
                                selected = v.id == vehiculoSeleccionado?.id,
                                color = NavyBlue
                            ) { vehiculoSeleccionado = v }
                        }
                        item { AddMiniChip(onAgregarVehiculo) }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Botones de acción ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                label = "Planificar",
                icon = Icons.AutoMirrored.Filled.AltRoute,
                color = Color(0xFF1C42B1),
                modifier = Modifier.weight(1f),
                onClick = { onPlanificarViaje(tipoVehiculo) }
            )
            ActionButton(
                label = "Historial",
                icon = Icons.Default.History,
                color = Color(0xFF1C42B1),
                modifier = Modifier.weight(1f),
                onClick = onHistorialViajes
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Mapa con botón flotante ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState)
            {
                PorticosContainer(
                    context = context,
                    vehiculo = TipoVehiculo.AUTO)
            }


            Button(
                onClick = { onIrARuta(tipoVehiculo) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 28.dp, vertical = 28.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Iniciar ruta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── ActionButton ──────────────────────────────────────────────────────────────
@Composable
fun ActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── VehiculoMiniChip ──────────────────────────────────────────────────────────
@Composable
fun VehiculoMiniChip(vehiculo: Vehiculo, selected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color else Color(0xFFF0F4FF))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = if (selected) Color.White else color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = vehiculo.patente.take(7),
                fontSize = 10.sp,
                color = if (selected) Color.White else color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── AddMiniChip ───────────────────────────────────────────────────────────────
@Composable
fun AddMiniChip(onClick: () -> Unit) {
    val dashColor = Color(0xFF3260D8).copy(alpha = 0.42f)
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                    )
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = dashColor,
            modifier = Modifier.size(28.dp)
        )
    }
}
