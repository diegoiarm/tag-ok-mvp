// HistorialTopBar.kt
package com.tagok.app.ui.historial.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.tagok.app.ui.historial.HistorialDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialTopBar(
    navigationStack: List<HistorialDestination>,
    onBack: () -> Unit)
{
    val currentDestination = navigationStack.lastOrNull()

    TopAppBar(
        title = {
            Text(getTitle(currentDestination))
        },
        navigationIcon = {
            if (navigationStack.size > 1) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        })
}

private fun getTitle(destination: HistorialDestination?): String
{
    return when (destination)
    {
        is HistorialDestination.YearList -> "Historial"
        is HistorialDestination.MonthView -> "Año ${destination.year}"
        is HistorialDestination.DayDetail -> "Detalle del día"
        null -> "Historial"
    }
}