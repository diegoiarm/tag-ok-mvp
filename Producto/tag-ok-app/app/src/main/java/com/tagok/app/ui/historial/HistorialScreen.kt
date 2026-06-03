// HistorialScreen.kt
package com.tagok.app.ui.historial

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.ui.historial.components.*
import com.tagok.app.ui.historial.components.calendar.CalendarioMensual
import com.tagok.app.ui.historial.components.day.DetalleDiaContent
import com.tagok.app.ui.historial.components.month.VistaMeses
import com.tagok.app.ui.historial.components.shared.FilterChips
import com.tagok.app.ui.historial.components.year.VistaAnual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onBack: () -> Unit = {},
    viewModel: HistorialViewModel = viewModel(factory = HistorialViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    val detalleDia = uiState.detalleDia
    val detalleMensual = uiState.detalleMensual
    val detalleAnual = uiState.detalleAnual

    Scaffold(
        topBar = {
            HistorialTopBar(
                detalleDia = detalleDia,
                detalleMensual = detalleMensual,
                selectedYear = uiState.selectedYear,
                onBack = {
                    when
                    {
                        detalleDia != null -> viewModel.clearDayDetail()
                        detalleMensual != null -> viewModel.clearMonthDetail()
                        uiState.selectedYear != null -> viewModel.clearYearDetail()
                        else -> onBack()
                    }
                })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding))
        {
            when
            {
                uiState.isLoading -> LoadingContent()

                uiState.error != null -> ErrorContent(
                    message = uiState.error ?: "Error desconocido",
                    onRetry = { viewModel.loadInitialData() })

                detalleDia != null -> DetalleDiaContent(
                    detalle = detalleDia,
                    onBack = { viewModel.clearDayDetail() })

                detalleMensual != null -> CalendarioMensual(
                    detalle = detalleMensual,
                    onDayClick = { dia ->
                        viewModel.selectDay(detalleMensual.mes, dia)
                    })

                uiState.selectedYear != null && detalleAnual != null -> VistaMeses(
                    detalle = detalleAnual,
                    onMonthClick = { viewModel.selectMonth(it) })

                else -> Column {
                    FilterChips(
                        currentSort = uiState.currentSort,
                        onSortSelected = { viewModel.setSortOption(it) })

                    VistaAnual(
                        resumen = uiState.resumenAnual,
                        onYearClick = { viewModel.selectYear(it) })
                }
            }
        }
    }
}