// ui/map/TestRealTime.kt
package com.tagok.app.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.tarifa.Cruce
import com.tagok.app.domain.model.tarifa.CrucePortico
import com.tagok.app.domain.model.tarifa.CruceTramo
import com.tagok.app.domain.model.tarifa.TarifaCalculada
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.ui.historial.utils.formatCurrency

@Composable
fun TestRealTime(
    tarifaCalculada: TarifaCalculada?,
    isCalculating: Boolean,
    isTracking: Boolean,
    vehiculo: Vehiculo?,
    onSimularCruce: () -> Unit,
    onToggleTracking: () -> Unit,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier)
{
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp))
    {
        Column(
            modifier = Modifier.padding(16.dp))
        {
            // Cabecera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Simular Cruce",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                if (tarifaCalculada != null)
                {
                    IconButton(onClick = onCerrar)
                    {
                        Icon(Icons.Filled.Close, "Cerrar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Vehículo: ${vehiculo?.alias}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.Numbers,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                vehiculo?.patente?.let {
                    Text(
                        text = it ,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSimularCruce,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCalculating,
                shape = RoundedCornerShape(12.dp))
            {
                if (isCalculating)
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculando...")
                }
                else
                {
                    Icon(Icons.Filled.Casino, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simular cruce aleatorio")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onToggleTracking,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = if (isTracking)
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                else
                    ButtonDefaults.buttonColors())
            {
                Icon(
                    imageVector = if (isTracking) Icons.Filled.GpsOff else Icons.Filled.GpsFixed,
                    contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTracking) "Detener verificación" else "Verificar cruces en tiempo real")
            }

            AnimatedVisibility(visible = isTracking)
            {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verificando cruces con tu ubicación...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            AnimatedVisibility(
                visible = tarifaCalculada != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically())
            {
                tarifaCalculada?.let { tarifa ->
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            Text(
                                text = "Total calculado",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = tarifa.total.formatCurrency(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Cruces (${tarifa.cruces.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))

                        tarifa.cruces.forEach { cruce ->
                            CruceItem(cruce = cruce)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CruceItem(cruce: Cruce)
{
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp))
    {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            Icon(
                imageVector = when (cruce)
                {
                    is CrucePortico -> Icons.Filled.LocationOn
                    is CruceTramo -> Icons.Filled.SwapHoriz
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f))
            {
                Text(
                    text = cruce.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold)
                Text(
                    text = "${cruce.autopista} • ${cruce.codigo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                when (cruce)
                {
                    is CruceTramo -> {
                        Text(
                            text = "${cruce.nombreEntrada} → ${cruce.nombreSalida}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    is CrucePortico -> {
                        Text(
                            text = "${cruce.nombre}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End)
            {
                Text(
                    text = cruce.valor.formatCurrency(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
                Text(
                    text = cruce.tipoTarifa,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}