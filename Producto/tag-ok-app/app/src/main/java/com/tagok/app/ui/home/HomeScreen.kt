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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

private enum class TipoVehiculo(val label: String, val icon: ImageVector) {
    AUTO("Auto", Icons.Filled.DirectionsCar),
    MOTO("Moto", Icons.Filled.TwoWheeler),
    CAMIONETA("Camioneta", Icons.Filled.LocalShipping),
    BUS("Bus", Icons.Filled.DirectionsBus),
}

@Composable
fun HomeScreen(
    nombre: String = "Usuario",
    onPlanificarViaje: (vehiculo: String) -> Unit,
    onHistorialViajes: () -> Unit,
    onIrARuta: (vehiculo: String) -> Unit,
    onBoletaMensual: () -> Unit,
    onLogout: () -> Unit = {},
) {
    var vehiculoSeleccionado by remember { mutableStateOf(TipoVehiculo.AUTO) }

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
            androidx.compose.material3.IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Cerrar sesión",
                    tint = TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // Selector de vehículo
        Text(
            text = "Vehículo de hoy",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TipoVehiculo.entries.forEach { tipo ->
                val selected = tipo == vehiculoSeleccionado
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) Blue40 else InputBackground)
                        .then(
                            if (!selected) Modifier.border(
                                width = 1.dp,
                                color = InputBackground,
                                shape = RoundedCornerShape(14.dp),
                            ) else Modifier
                        )
                        .clickable { vehiculoSeleccionado = tipo },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = tipo.icon,
                        contentDescription = tipo.label,
                        tint = if (selected) androidx.compose.ui.graphics.Color.White else TextSecondary,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        // Acciones principales
        ActionButton(
            label = "Planificar viaje",
            icon = Icons.Filled.Map,
            onClick = { onPlanificarViaje(vehiculoSeleccionado.name) },
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
            onClick = { onIrARuta(vehiculoSeleccionado.name) },
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
