package com.tagok.app.ui.historial.components.day

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun DetalleDiaContent(
    detalle: DetalleDia,
    onBack: () -> Unit)
{
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp))
    {
        item {
            DayHeaderCard(detalle = detalle)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Detalle de cruces",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Blue40.copy(alpha = 0.1f))
                {
                    Text(
                        text = "${detalle.cruces.size} cruces",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Blue40,
                        fontWeight = FontWeight.Bold)
                }
            }
        }

        items(detalle.cruces) { cruce ->
            CruceItem(cruce = cruce)
        }
    }
}