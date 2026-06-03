package com.tagok.app.ui.historial.components.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.ui.historial.components.shared.TotalItem
import com.tagok.app.ui.historial.utils.formatCurrency
import com.tagok.app.ui.historial.utils.getMonthName
import com.tagok.app.ui.theme.Blue40

@Composable
fun DayHeaderCard(detalle: DetalleDia)
{
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp))
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Blue40, Blue40.copy(alpha = 0.9f))))
                .padding(20.dp),
            contentAlignment = Alignment.Center)
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = "${detalle.dia}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White)
                Text(
                    text = "${getMonthName(detalle.mes)} ${detalle.año}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f))
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
                        value = detalle.totalDia.formatCurrency(),
                        label = "Total")
                }
            }
        }
    }
}