package com.tagok.app.ui.historial.components.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.history.CruceDetalle
import com.tagok.app.ui.historial.utils.formatCurrency
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun CruceItem(cruce: CruceDetalle)
{
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp))
    {
        Row(modifier = Modifier.padding(12.dp))
        {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Blue40.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center)
            {
                Icon(
                    imageVector = when
                    {
                        cruce.tipoVehiculo.contains("MOTO", ignoreCase = true) -> Icons.Filled.TwoWheeler
                        cruce.tipoVehiculo.contains("BUS", ignoreCase = true) -> Icons.Filled.DirectionsBus
                        cruce.tipoVehiculo.contains("CAMION", ignoreCase = true) -> Icons.Filled.LocalShipping
                        else -> Icons.Filled.DirectionsCar
                    },
                    contentDescription = cruce.tipoVehiculo,
                    tint = Blue40,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f))
            {
                Text(
                    text = cruce.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${cruce.autopista} • ${cruce.codigo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween)
                {
                    Row(verticalAlignment = Alignment.CenterVertically)
                    {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = TextSecondary)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = cruce.horaFechaCruce.takeLast(5),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary)
                    }
                    Text(
                        text = "Tarifa: ${cruce.tipoTarifa}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End)
            {
                Text(
                    text = cruce.valor.formatCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Blue40,
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = cruce.tipoVehiculo,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary)
            }
        }
    }
}