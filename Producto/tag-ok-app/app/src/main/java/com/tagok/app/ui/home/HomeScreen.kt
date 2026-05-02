package com.tagok.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.data.Vehiculo
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

private fun iconForTipo(tipo: String): ImageVector = when (tipo.uppercase()) {
    "MOTO" -> Icons.Filled.TwoWheeler
    "CAMIONETA" -> Icons.Filled.LocalShipping
    "BUS" -> Icons.Filled.DirectionsBus
    else -> Icons.Filled.DirectionsCar
}

@Composable
fun HomeScreen(
    nombre: String = "Usuario",
    onPlanificarViaje: (vehiculo: String) -> Unit,
    onHistorialViajes: () -> Unit,
    onIrARuta: (vehiculo: String) -> Unit,
    onBoletaMensual: () -> Unit,
    onAgregarVehiculo: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
) {
    val vehiculos by viewModel.vehiculos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(null) }

    // Recarga al volver desde cualquier pantalla que esté por encima (ej: VehiculosScreen)
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(52.dp))

        // Header: avatar + saludo + logout
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(InputBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Blue40,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = "Hola, $nombre",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "¿A dónde vas hoy?",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Cerrar sesión",
                    tint = TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Vehículo de hoy",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(10.dp))

        when {
            loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Blue40,
                    strokeWidth = 2.dp,
                )
            }

            vehiculos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Blue40.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .background(InputBackground)
                        .clickable { onAgregarVehiculo() }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Blue40,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sin vehículos registrados",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = "Toca aquí para agregar tu primero",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                        }
                    }
                }
            }

            else -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(vehiculos, key = { it.id }) { vehiculo ->
                        VehiculoChip(
                            vehiculo = vehiculo,
                            selected = vehiculo.id == vehiculoSeleccionado?.id,
                            onClick = { vehiculoSeleccionado = vehiculo },
                        )
                    }
                    item {
                        AddVehiculoChip(onClick = onAgregarVehiculo)
                    }
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        ActionButton(
            label = "Planificar viaje",
            icon = Icons.Filled.Map,
            onClick = { onPlanificarViaje(tipoVehiculo) },
        )
        Spacer(Modifier.height(12.dp))
        ActionButton(
            label = "Historial de viajes",
            icon = Icons.Filled.History,
            onClick = onHistorialViajes,
        )
        Spacer(Modifier.height(12.dp))
        ActionButton(
            label = "Ir a la ruta",
            icon = Icons.Filled.Navigation,
            onClick = { onIrARuta(tipoVehiculo) },
        )
        Spacer(Modifier.height(12.dp))
        ActionButton(
            label = "Boleta mensual",
            icon = Icons.Filled.Receipt,
            onClick = onBoletaMensual,
        )
    }
}

@Composable
private fun VehiculoChip(vehiculo: Vehiculo, selected: Boolean, onClick: () -> Unit) {
    val label = vehiculo.alias?.takeIf { it.isNotBlank() } ?: vehiculo.patente
    Column(
        modifier = Modifier
            .size(width = 72.dp, height = 76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Blue40 else InputBackground)
            .then(
                if (!selected) Modifier.border(1.dp, InputBackground, RoundedCornerShape(14.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = iconForTipo(vehiculo.tipoVehiculo),
            contentDescription = label,
            tint = if (selected) Color.White else TextSecondary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White else TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AddVehiculoChip(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(width = 72.dp, height = 76.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Blue40, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Agregar vehículo",
            tint = Blue40,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Agregar",
            style = MaterialTheme.typography.labelSmall,
            color = Blue40,
        )
    }
}

@Composable
private fun ActionButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Blue40),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
        )
    }
}
