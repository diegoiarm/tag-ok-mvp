package com.tagok.app.ui.perfil

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun PerfilScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Perfil — próximamente",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
        )
    }
}
