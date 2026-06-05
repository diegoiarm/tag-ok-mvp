package com.tagok.app.ui.historial.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier)
{
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp))
        {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error)

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp))
            {
                TextButton(onClick = onDismiss)
                {
                    Text("Descartar")
                }

                Button(onClick = onRetry)
                {
                    Text("Reintentar")
                }
            }
        }
    }
}