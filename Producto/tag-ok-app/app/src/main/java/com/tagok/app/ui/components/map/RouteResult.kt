package com.tagok.app.ui.components.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.ui.components.routes.TollItem
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun RouteResult(
    vehiculo: String,
    route: Route?,
    onFlyToPortico: (lat: Double, lon: Double) -> Unit,
    modifier: Modifier = Modifier)
{
    Column(modifier = modifier)
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically)
        {
            val tollCount = route?.tolls?.size ?: 0

            Spacer(Modifier.width(6.dp))
            Text(
                text = if (tollCount > 0) "· $tollCount pórtico(s) en ruta" else "· sin pórticos en ruta",
                style = MaterialTheme.typography.bodySmall,
                color = if (tollCount > 0) Blue40 else TextSecondary,
                fontWeight = if (tollCount > 0) FontWeight.Medium else FontWeight.Normal,)
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically)
        {
            Column(modifier = Modifier.weight(1f))
            {
                Text("Tarifa estimada", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Text("Vehículo: $vehiculo", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Text(
                text = "${"%.0f".format(route?.totalCost)} CLP",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Blue40)
        }

        if (route?.tolls?.isNotEmpty() == true)
        {
            Spacer(Modifier.height(6.dp))
            Column(
                modifier = Modifier
                    .heightIn(max = 100.dp)
                    .verticalScroll(rememberScrollState()))
            {
                route.tolls.forEach { toll ->
                    TollItem(toll = toll, onFlyToPortico = onFlyToPortico)
                    HorizontalDivider(color = InputBackground)
                }
            }
        }
    }
}