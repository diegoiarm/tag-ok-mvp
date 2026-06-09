package com.tagok.app.ui.historial.components.year

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.historial.utils.formatCurrency
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun AnimatedYearCard(
    anual: ResumenAnual,
    maxCruces: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier)
{
    val intensidad = if (maxCruces > 0) (anual.cantidadCruces.toFloat() / maxCruces) else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${anual.año}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Blue40)
                    if (intensidad > 0.7f) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Blue40.copy(alpha = 0.1f))
                        {
                            Text(
                                text = "Más activo",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Blue40,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        Icons.Filled.Toll,
                        contentDescription = "Cruces",
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${anual.cantidadCruces} cruces",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Filled.MonetizationOn,
                        contentDescription = "Total",
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = anual.totalAño.formatCurrency(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary)
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Ver meses",
                tint = Blue40.copy(alpha = 0.5f))
        }
    }
}