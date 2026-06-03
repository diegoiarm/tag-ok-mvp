package com.tagok.app.ui.historial.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun EmptyState()
{
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally)
        {
            Icon(
                Icons.Filled.History,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextSecondary.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay historial disponible",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tus viajes aparecerán aquí",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary.copy(alpha = 0.7f))
        }
    }
}