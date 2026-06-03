package com.tagok.app.ui.historial.components.month

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.historial.components.shared.TotalItem
import com.tagok.app.ui.historial.utils.formatCurrency
import com.tagok.app.ui.theme.Blue40

@Composable
fun VistaMeses(
    detalle: ResumenAnual,
    onMonthClick: (Int) -> Unit)
{
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Blue40, Blue40.copy(alpha = 0.8f))))
                    .padding(20.dp))
            {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Text(
                        text = "${detalle.año}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly)
                    {
                        TotalItem(
                            icon = Icons.Filled.Toll,
                            value = "${detalle.cantidadCruces}",
                            label = "Cruces")
                        TotalItem(
                            icon = Icons.Filled.MonetizationOn,
                            value = detalle.totalAño.formatCurrency(),
                            label = "Total")
                        TotalItem(
                            icon = Icons.Filled.CalendarMonth,
                            value = "${detalle.mesesDisponibles.size}",
                            label = "Meses")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            items(12) { index ->
                val mes = index + 1
                val tieneDatos = detalle.mesesDisponibles.contains(mes)

                AnimatedMonthCard(
                    mes = mes,
                    activo = tieneDatos,
                    onClick = { if (tieneDatos) onMonthClick(mes) })
            }
        }
    }
}