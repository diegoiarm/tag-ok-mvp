package com.tagok.app.ui.map.portico

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Toll
import com.tagok.app.domain.model.routes.Tramo
import com.tagok.app.ui.theme.NavyBlue
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// ---------------------------------------------------------------------------
// Adaptación al patrón NavyBlue + blanco
// ---------------------------------------------------------------------------

@Composable
fun PorticoRouteDetail(
    toll: Toll,
    onDismiss: () -> Unit
) {
    val title = when (toll) {
        is Portico -> "${toll.nombre} (${toll.codigo})"
        is Tramo   -> "${toll.nombreEntrada} (${toll.codigoEntrada}) → ${toll.nombreSalida} (${toll.codigoSalida})"
    }

    BasePorticoBottomSheet(title = title, onDismiss = onDismiss) {
        when (toll) {
            is Portico -> PorticoDetailContent(toll)
            is Tramo   -> TramoDetailContent(toll)
        }
    }
}

@Composable
private fun PorticoDetailContent(portico: Portico) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tarjeta: autopista y tarifa
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyBlue)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetalleItem("Autopista", portico.autopista)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                )
                DetalleItem("Tarifa", portico.tarifa)
            }
        }

        // Tarjeta: total
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyBlue)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "$${portico.valor}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        fechaHoraEstimadaLayer(portico.fechaHora)
    }
}

@Composable
private fun TramoDetailContent(tramo: Tramo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tarjeta: autopista y tarifa
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyBlue)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetalleItem("Autopista", tramo.autopista)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                )
                DetalleItem("Tarifa", tramo.tarifa)
            }
        }

        // Tarjeta: total
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyBlue)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "$${tramo.valor}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        fechaHoraEstimadaLayer(tramo.fechaHora)
    }
}

@Composable
private fun fechaHoraEstimadaLayer(fechaHora: LocalDateTime) {
    val textoFormateado = remember(fechaHora) { fechaHora.formatoBonito() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBlue)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Fecha de cruce estimada",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = textoFormateado,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Reemplazo de DetalleRow con colores fijos del nuevo estándar
// ---------------------------------------------------------------------------
@Composable
private fun DetalleItem(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

// ---------------------------------------------------------------------------
// Formateo de fecha (sin cambios)
// ---------------------------------------------------------------------------
private fun LocalDateTime.formatoBonito(): String {
    val javaDateTime = this.toJavaLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy, HH:mm", Locale("es"))
    return javaDateTime.format(formatter)
}