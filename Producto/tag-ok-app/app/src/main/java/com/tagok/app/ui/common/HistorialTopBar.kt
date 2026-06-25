// HistorialTopBar.kt
package com.tagok.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.tagok.app.ui.historial.HistorialDestination
import com.tagok.app.ui.theme.NavyBlue
import com.tagok.app.ui.theme.PageBg
import com.tagok.app.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialTopBar(
    navigationStack: List<HistorialDestination>,
    onBack: () -> Unit)
{
    val currentDestination = navigationStack.lastOrNull()

    TopAppBar(
        title = {
            Text(getTitle(currentDestination), fontWeight = FontWeight.Bold, color = TextDark)
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = NavyBlue)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBg))
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