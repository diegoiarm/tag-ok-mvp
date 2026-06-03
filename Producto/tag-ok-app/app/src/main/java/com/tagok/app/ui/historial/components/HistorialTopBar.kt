package com.tagok.app.ui.historial.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.ui.historial.utils.getMonthName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialTopBar(
    detalleDia: DetalleDia?,
    detalleMensual: DetalleMensual?,
    selectedYear: Int?,
    onBack: () -> Unit)
{
    TopAppBar(
        title = {
            Text(
                text = when {
                    detalleDia != null ->
                        "${detalleDia.dia} de ${getMonthName(detalleDia.mes)} ${detalleDia.año}"
                    detalleMensual != null ->
                        "${getMonthName(detalleMensual.mes)} ${detalleMensual.año}"
                    selectedYear != null ->
                        "Año $selectedYear"
                    else -> "Historial de Viajes"
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Volver")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface))
}