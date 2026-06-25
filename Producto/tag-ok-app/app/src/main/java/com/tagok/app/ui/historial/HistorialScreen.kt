// HistorialScreen.kt
package com.tagok.app.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.ui.common.ScreenLifecycle
import com.tagok.app.ui.common.ErrorContent
import com.tagok.app.ui.common.HistorialTopBar
import com.tagok.app.ui.common.LoadingState
import com.tagok.app.ui.historial.components.calendar.CalendarioMensual
import com.tagok.app.ui.historial.components.day.DetalleDiaContent
import com.tagok.app.ui.historial.components.month.VistaMeses
import com.tagok.app.ui.historial.components.shared.FilterChips
import com.tagok.app.ui.historial.components.year.VistaAnual
import com.tagok.app.ui.historial.model.SortOption
import com.tagok.app.ui.theme.PageBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onBack: () -> Unit = {},
    viewModel: HistorialViewModel = viewModel(factory = HistorialViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenLifecycle(viewModel = viewModel)

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
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(PageBg))
        {
            when
            {
                uiState.loadingState.isLoading -> LoadingState()

                uiState.error != null -> ErrorContent(
                    message = uiState.error ?: "Error desconocido",
                    onRetry = { viewModel.refreshData() },
                    onDismiss = { viewModel.clearError() })

                else -> HistorialContent(
                    uiState = uiState,
                    onNavigate = { destination -> viewModel.navigateTo(destination) },
                    onSortSelected = { viewModel.setSortOption(it) },
                    onPatenteToggle = { viewModel.togglePatente(it) },
                    onAutopistaToggle = { viewModel.toggleAutopista(it) },
                    onClearPatentes = { viewModel.clearPatenteFilter() },
                    onClearAutopistas = { viewModel.clearAutopistaFilter() },
                    onSelectMonth = { viewModel.selectMonth(it) },
                    onApplyFilters = { viewModel.applyFilters() },)
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
    onAutopistaToggle: (String) -> Unit,
    onClearPatentes: () -> Unit,
    onClearAutopistas: () -> Unit,
    onSelectMonth: (Int) -> Unit,
    onApplyFilters: () -> Unit)
{
    val currentDestination = uiState.navigationStack.lastOrNull()

    when (currentDestination) {
        is HistorialDestination.YearList -> YearListContent(
            listState = uiState.listState,
            filterState = uiState.filterState,
            onSortSelected = onSortSelected,
            onPatenteToggle = onPatenteToggle,
            onAutopistaToggle = onAutopistaToggle,
            onClearPatentes = onClearPatentes,
            onClearAutopistas = onClearAutopistas,
            onApplyFilters = onApplyFilters,
            onYearClick = { year -> onNavigate(HistorialDestination.MonthView(year)) })

        is HistorialDestination.MonthView -> MonthViewContent(
            detailState = uiState.detailState,
            onMonthClick = { month ->
                onSelectMonth(month)
            },
            onDayClick = { month, day ->
                onNavigate(HistorialDestination.DayDetail(currentDestination.year, month, day))
            }
        )

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
    onAutopistaToggle: (String) -> Unit,
    onClearPatentes: () -> Unit,
    onClearAutopistas: () -> Unit,
    onYearClick: (Int) -> Unit,
    onApplyFilters: () -> Unit,)
{
    Column(modifier = Modifier.fillMaxSize())
    {
        FilterChips(
            currentSort = listState.currentSort,
            patentes = filterState.patentes,
            autopistas = filterState.autopistas,
            onSortSelected = onSortSelected,
            onPatenteToggle = onPatenteToggle,
            onAutopistaToggle = onAutopistaToggle,
            onClearPatentes = onClearPatentes,
            onClearAutopistas = onClearAutopistas,
            onApplyFilters = onApplyFilters)

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

            state.detalleAnual != null -> {
                if (state.detalleAnual.mesesDisponibles.isEmpty())
                {
                    EmptyStateContent()
                }
                else
                {
                    VistaMeses(
                        detalle = state.detalleAnual,
                        onMonthClick = onMonthClick)
                }
            }

            else -> LoadingState()
        }
    } ?: LoadingState()
}

@Composable
private fun DayDetailContent(detailState: DetailState?)
{
    detailState?.let { state ->
        when
        {
            state.detalleDia != null -> DetalleDiaContent(detalle = state.detalleDia)
            state.detalleMensual != null -> CalendarioMensual(
                detalle = state.detalleMensual,
                onDayClick = { day ->
                })
            else -> LoadingState()
        }
    }
}

@Composable
private fun EmptyStateContent()
{
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        Text("No hay datos disponibles", color = Color.Gray)
    }
}