// HistorialScreen.kt
package com.tagok.app.ui.historial

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.ui.historial.components.ErrorContent
import com.tagok.app.ui.historial.components.HistorialTopBar
import com.tagok.app.ui.historial.components.LoadingContent
import com.tagok.app.ui.historial.components.calendar.CalendarioMensual
import com.tagok.app.ui.historial.components.day.DetalleDiaContent
import com.tagok.app.ui.historial.components.month.VistaMeses
import com.tagok.app.ui.historial.components.shared.FilterChips
import com.tagok.app.ui.historial.components.year.VistaAnual
import com.tagok.app.ui.historial.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onBack: () -> Unit = {},
    viewModel: HistorialViewModel = viewModel(factory = HistorialViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            HistorialTopBar(
                navigationStack = uiState.navigationStack,
                onBack = {
                    if (uiState.navigationStack.size > 1)
                    {
                        viewModel.navigateBack()
                    }
                    else
                    {
                        onBack()
                    }
                })
        })
    { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding))
        {
            when
            {
                uiState.loadingState.isLoading -> LoadingContent()

                uiState.error != null -> ErrorContent(
                    message = uiState.error ?: "Error desconocido",
                    onRetry = { viewModel.loadInitialData() },
                    onDismiss = { viewModel.clearError() })

                else -> HistorialContent(
                    uiState = uiState,
                    onNavigate = { destination -> viewModel.navigateTo(destination) },
                    onSortSelected = { viewModel.setSortOption(it) },
                    onPatenteToggle = { viewModel.togglePatente(it) },
                    onClearPatentes = { viewModel.clearPatenteFilter() })
            }
        }
    }
}

@Composable
private fun HistorialContent(
    uiState: HistorialUiState,
    onNavigate: (HistorialDestination) -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onPatenteToggle: (String) -> Unit,
    onClearPatentes: () -> Unit)
{
    val currentDestination = uiState.navigationStack.lastOrNull()

    when (currentDestination)
    {
        is HistorialDestination.YearList -> YearListContent(
            listState = uiState.listState,
            filterState = uiState.filterState,
            onSortSelected = onSortSelected,
            onPatenteToggle = onPatenteToggle,
            onClearPatentes = onClearPatentes,
            onYearClick = { year -> onNavigate(HistorialDestination.MonthView(year)) })

        is HistorialDestination.MonthView -> MonthViewContent(
            detailState = uiState.detailState,
            onMonthClick = { month ->
                currentDestination.let {
                    onNavigate(HistorialDestination.DayDetail(it.year, month, 1))
                }
            },
            onDayClick = { month, day ->
                onNavigate(HistorialDestination.DayDetail(currentDestination.year, month, day))
            })

        is HistorialDestination.DayDetail -> DayDetailContent(
            detailState = uiState.detailState)

        null -> Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun YearListContent(
    listState: ListState,
    filterState: FilterState,
    onSortSelected: (SortOption) -> Unit,
    onPatenteToggle: (String) -> Unit,
    onClearPatentes: () -> Unit,
    onYearClick: (Int) -> Unit)
{
    Column(modifier = Modifier.fillMaxSize())
    {
        FilterChips(
            currentSort = listState.currentSort,
            patentes = filterState.patentes,
            onSortSelected = onSortSelected,
            onPatenteToggle = onPatenteToggle,
            onClearPatentes = onClearPatentes)

        if (listState.resumenAnual.isEmpty())
        {
            EmptyStateContent()
        }
        else
        {
            VistaAnual(
                resumen = listState.resumenAnual,
                onYearClick = onYearClick)
        }
    }
}

@Composable
private fun MonthViewContent(
    detailState: DetailState?,
    onMonthClick: (Int) -> Unit,
    onDayClick: (Int, Int) -> Unit)
{
    detailState?.let { state ->
        when
        {
            state.detalleDia != null -> DetalleDiaContent(
                detalle = state.detalleDia)

            state.detalleMensual != null -> CalendarioMensual(
                detalle = state.detalleMensual,
                onDayClick = { day -> onDayClick(state.detalleMensual.mes, day) })

            state.detalleAnual != null -> VistaMeses(
                detalle = state.detalleAnual,
                onMonthClick = onMonthClick)
        }
    }
}

@Composable
private fun DayDetailContent(detailState: DetailState?)
{
    detailState?.detalleDia?.let { detalle ->
        DetalleDiaContent(detalle = detalle)
    }
}

@Composable
private fun EmptyStateContent()
{
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center)
    {
        Text("No hay datos disponibles")
    }
}