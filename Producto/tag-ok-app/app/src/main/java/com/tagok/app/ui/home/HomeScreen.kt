package com.tagok.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.ui.theme.InputBackground

/**
 * Modificador para aplicar gradiente a los iconos
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

@Composable
fun HomeScreen(
    nombre: String = "Usuario",
    onPlanificarViaje: (vehiculo: String) -> Unit,
    onHistorialViajes: () -> Unit,
    onIrARuta: (vehiculo: String) -> Unit,
    onAgregarVehiculo: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory))
{
    val vehiculos by viewModel.vehiculos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(null) }

    // Gradiente del tema
    val purpleGradient = listOf(Color(0xFF3D257B), Color(0xFF6750A4))

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.cargar()
        }
    }

    LaunchedEffect(vehiculos) {
        if (vehiculoSeleccionado == null || vehiculoSeleccionado !in vehiculos) {
            vehiculoSeleccionado = vehiculos.firstOrNull { it.esPrincipal } ?: vehiculos.firstOrNull()
        }
    }

    val tipoVehiculo = vehiculoSeleccionado?.tipoVehiculo ?: "AUTO"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(Modifier.height(52.dp))

        // Header
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    modifier = Modifier
                        .size(32.dp)
                        .gradientTint(purpleGradient),
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Hola $nombre",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            // Botón de Notificaciones con Gradiente
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(purpleGradient))
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, null, tint = Color.White)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Tarjeta Vehículo
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF1EEFF)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Vehículo de hoy",
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(brush = Brush.linearGradient(purpleGradient))
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = purpleGradient[0]
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(vehiculos) { v ->
                                VehiculoMiniChip(
                                    vehiculo = v,
                                    selected = v.id == vehiculoSeleccionado?.id,
                                    gradient = purpleGradient
                                ) {
                                    vehiculoSeleccionado = v
                                }
                            }
                            item { AddMiniChip(purpleGradient, onAgregarVehiculo) }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botones en Fila
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                label = "Planificar",
                icon = Icons.Default.Map,
                modifier = Modifier.weight(1f),
                gradient = purpleGradient,
                onClick = { onPlanificarViaje(tipoVehiculo) }
            )
            ActionButton(
                label = "Historial",
                icon = Icons.Default.History,
                modifier = Modifier.weight(1f),
                gradient = purpleGradient,
                onClick = onHistorialViajes
            )
        }


        Spacer(Modifier.weight(1f))

        // Botón Inferior "Ir a la ruta"
        Button(
            onClick = { onIrARuta(tipoVehiculo) },
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE54D35))
        ) {
            Text("Ir a la ruta", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ActionButton(label: String, icon: ImageVector, modifier: Modifier, gradient: List<Color>, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradient))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun VehiculoMiniChip(vehiculo: Vehiculo, selected: Boolean, gradient: List<Color>, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) Brush.linearGradient(gradient)
                else SolidColor(Color(0xFFDED9F5))
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = if (selected) Color.White else Color.Unspecified,
                modifier = if (!selected) Modifier.gradientTint(gradient) else Modifier
            )
            Text(
                text = vehiculo.patente.take(7),
                fontSize = 10.sp,
                color = if (selected) Color.White else Color.Unspecified,
                fontWeight = FontWeight.Bold,
                style = if (!selected) TextStyle(brush = Brush.linearGradient(gradient)) else TextStyle()
            )
        }
    }
}

@Composable
fun AddMiniChip(gradient: List<Color>, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFDED9F5))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.gradientTint(gradient)
        )
    }
}
