package com.tagok.app.ui.historial.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun LoadingContent()
{
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally)
        {
            CircularProgressIndicator(color = Blue40)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando historial...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary)
        }
    }
}