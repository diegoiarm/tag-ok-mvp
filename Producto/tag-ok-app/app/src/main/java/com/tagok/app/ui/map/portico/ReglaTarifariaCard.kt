package com.tagok.app.ui.map.portico

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.portico.CalendarioTarifario
import com.tagok.app.domain.model.portico.ReglaTarifaria
import com.tagok.app.domain.model.portico.ReglaTemporal
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun ReglasTarifariasCard(
    reglas: List<ReglaTarifaria>,
    calendario: CalendarioTarifario,
    tipoTarifaActual: String,
    tipoAuto: String)
{
    val calendarioPorTipo: Map<String, List<ReglaTemporal>> = remember(calendario) {
        calendario.reglas.groupBy { it.tipoTarifa }
    }

    Text("Tarifas", style = MaterialTheme.typography.labelMedium, color = TextSecondary)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = InputBackground))
    {
        Column(
            modifier = Modifier
                .heightIn(max = 200.dp)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            reglas.forEachIndexed { index, regla ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp))
                {
                    Text(
                        text = regla.aplicaA.joinToString(", ").toVehiculoDisplay(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary)

                    regla.valores.forEach { valor ->
                        TarifaRow(
                            tipoTarifa = valor.tipoTarifa,
                            monto = valor.valor,
                            reglasCalendario = calendarioPorTipo[valor.tipoTarifa] ?: emptyList(),
                            esActual = valor.tipoTarifa == tipoTarifaActual && regla.aplicaA.contains(tipoAuto))
                    }
                }
                if (index < reglas.lastIndex)
                    HorizontalDivider(color = InputBackground.copy(alpha = 0.5f))
            }
        }
    }
}

private fun String.toVehiculoDisplay() = when (this)
{
    "CAMION_REMOLQUE" -> "Camión con remolque"
    "CAMION" -> "Camión"
    "CAMIONETA" -> "Camioneta"
    "AUTO" -> "Auto"
    "MOTO" -> "Moto"
    "BUS" -> "Bus"
    else -> this.lowercase().replaceFirstChar { it.uppercase() }
}

@Composable
private fun TarifaRow(
    tipoTarifa: String,
    monto: Double,
    reglasCalendario: List<ReglaTemporal>,
    esActual: Boolean)
{
    var expandido by remember { mutableStateOf(esActual) }
    val tieneCalendario = reglasCalendario.isNotEmpty()

    val bgModifier = if (esActual)
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Blue40.copy(alpha = 0.08f))
    else Modifier

    Column(modifier = bgModifier)
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (tieneCalendario) Modifier.clickable { expandido = !expandido } else Modifier)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp))
            {
                Text(
                    text = tipoTarifa,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (esActual) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (esActual) MaterialTheme.colorScheme.onSurface else TextSecondary)

                if (tieneCalendario)
                    Icon(
                        imageVector = if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = if (esActual) Blue40 else TextSecondary,
                        modifier = Modifier.size(14.dp))
            }

            Text(
                text = "${monto.toInt()} CLP",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (esActual) FontWeight.Bold else FontWeight.SemiBold,
                color = Blue40)
        }

        AnimatedVisibility(visible = expandido) {
            CalendarioTarifaContent(
                reglas = reglasCalendario,
                tipoTarifaActual = if (esActual) tipoTarifa else null)
        }
    }
}

@Composable
private fun CalendarioTarifaContent(
    reglas: List<ReglaTemporal>,
    tipoTarifaActual: String?)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 4.dp, bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp))
    {
        reglas.forEach { regla ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp))
            {
                Text(
                    text = regla.tipoDia,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (tipoTarifaActual != null) FontWeight.Bold else FontWeight.Medium,
                    color = Blue40
                )

                regla.tramos.forEach { tramo ->
                    Text(
                        text = "${tramo.horaInicio} – ${tramo.horaFin}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary)
                }
            }
        }

        Spacer(Modifier.height(2.dp))
    }
}