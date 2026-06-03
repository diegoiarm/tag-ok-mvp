package com.tagok.app.ui.historial.components.year

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.historial.components.shared.TotalItem
import com.tagok.app.ui.historial.utils.formatCurrency
import com.tagok.app.ui.theme.Blue40

@Composable
fun ResumenTotalCard(resumen: List<ResumenAnual>) {
    val totalCruces = resumen.sumOf { it.cantidadCruces }
    val totalGastado = resumen.sumOf { it.totalAño }
    val añosActivos = resumen.size

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blue40),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp))
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(
                text = "Resumen Total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly)
            {
                TotalItem(
                    icon = Icons.Filled.Toll,
                    value = "$totalCruces",
                    label = "Cruces Totales")
                TotalItem(
                    icon = Icons.Filled.MonetizationOn,
                    value = totalGastado.formatCurrency(),
                    label = "Total Gastado")
                TotalItem(
                    icon = Icons.Filled.DateRange,
                    value = "$añosActivos",
                    label = "Años")
            }
        }
    }
}