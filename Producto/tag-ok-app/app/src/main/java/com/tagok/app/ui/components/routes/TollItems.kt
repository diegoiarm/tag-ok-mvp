package com.tagok.app.ui.planificar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Toll
import com.tagok.app.domain.model.routes.Tramo
import com.tagok.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PorticoRow(
    portico: Portico,
    onClick: () -> Unit,
    modifier: Modifier = Modifier)
{
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically)
    {
        Column(modifier = Modifier.weight(1f))
        {
            Text(
                text = portico.nombre.ifBlank { portico.codigo },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)

            if (portico.autopista.isNotBlank())
                Text(
                    portico.autopista,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                portico.valor.toCLP(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium)

            Text(
                "Tarifa: ${portico.tarifa}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary)
        }
    }
}

/**
 * Fila para un tramo (entrada → salida).
 */
@Composable
fun TramoRow(
    tramo: Tramo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier)
{
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically)
    {
        Column(modifier = Modifier.weight(1f))
        {
            Text(
                text = "${tramo.nombreEntrada} → ${tramo.nombreSalida}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)

            if (tramo.autopista.isNotBlank())
                Text(
                    tramo.autopista,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End)
        {
            Text(
                tramo.valor.toCLP(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium)

            Text(
                "Tarifa: ${tramo.tarifa}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary)
        }
    }
}

/**
 * Despachador: elige la fila correcta según el tipo de Toll.
 * [onFlyToPortico] recibe latitud y longitud a las que volar.
 */
@Composable
fun TollItem(
    toll: Toll,
    onFlyToPortico: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    when (toll)
    {
        is Portico -> PorticoRow(
            portico = toll,
            onClick = { onFlyToPortico(toll.latitud, toll.longitud) },
            modifier = modifier)

        is Tramo -> TramoRow(
            tramo = toll,
            onClick = { onFlyToPortico(toll.latitudEntrada, toll.longitudEntrada) },
            modifier = modifier)
    }
}

private fun Any?.toCLP(): String
{
    val number = when (this)
    {
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