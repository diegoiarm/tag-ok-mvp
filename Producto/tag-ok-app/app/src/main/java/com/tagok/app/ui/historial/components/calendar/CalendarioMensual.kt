package com.tagok.app.ui.historial.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.ui.historial.components.shared.InfoItem
import com.tagok.app.ui.historial.utils.formatCurrency
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarioMensual(
    detalle: DetalleMensual,
    onDayClick: (Int) -> Unit)
{
    val yearMonth = YearMonth.of(detalle.año, detalle.mes)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value
    val diasConDatos = detalle.dias.associate { it.dia to it }
    val maxCruces = detalle.dias.maxOfOrNull { it.cantidadCruces } ?: 1
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp))
    {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp))
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Blue40.copy(alpha = 0.1f),
                                Blue40.copy(alpha = 0.05f))))
                    .padding(16.dp))
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly)
                {
                    InfoItem(
                        icon = Icons.Filled.MonetizationOn,
                        label = "Total Mes",
                        value = detalle.totalMes.formatCurrency())
                    InfoItem(
                        icon = Icons.Filled.Toll,
                        label = "Cruces",
                        value = "${detalle.dias.sumOf { it.cantidadCruces }}")
                    InfoItem(
                        icon = Icons.Filled.Today,
                        label = "Días Activos",
                        value = "${detalle.dias.size}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CalendarLegend()

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth())
        {
            listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (day == "Dom") MaterialTheme.colorScheme.error else TextSecondary,
                    fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp))
        {
            items(firstDayOfWeek - 1) {
                Box(modifier = Modifier.size(44.dp))
            }

            items(daysInMonth) { index ->
                val day = index + 1
                val diaData = diasConDatos[day]
                val intensidad = if (diaData != null && maxCruces > 0)
                {
                    diaData.cantidadCruces.toFloat() / maxCruces
                }
                else 0f
                val isToday = day == today.dayOfMonth &&
                        yearMonth.year == today.year &&
                        yearMonth.monthValue == today.monthValue

                EnhancedDiaCalendario(
                    day = day,
                    tieneDatos = diaData != null,
                    cantidad = diaData?.cantidadCruces ?: 0,
                    intensidad = intensidad,
                    isToday = isToday,
                    onClick = { if (diaData != null) onDayClick(day) })
            }
        }
    }
}