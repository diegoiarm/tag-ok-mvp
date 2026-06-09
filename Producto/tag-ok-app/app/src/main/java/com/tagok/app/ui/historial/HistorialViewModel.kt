package com.tagok.app.ui.historial

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.di.ServiceLocator
import com.tagok.app.domain.exceptions.ApplicationError
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.services.interfaces.IHistoryService
import com.tagok.app.ui.common.RefreshableViewModel
import com.tagok.app.ui.historial.model.AutopistaFilter
import com.tagok.app.ui.historial.model.PatenteFilter
import com.tagok.app.ui.historial.model.SortOption
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistorialViewModel(
    private val historyService: IHistoryService) : ViewModel(), RefreshableViewModel
{
    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        val errorMessage = when (exception)
        {
            is ApplicationError -> exception.message ?: "Error desconocido"
            else -> "Error inesperado: ${exception.message}"
        }

        _uiState.update {
            it.copy(
                error = errorMessage,
                loadingState = it.loadingState.copy(
                    isLoading = false,
                    isLoadingDetail = false))
        }
    }

    init
    {
        refreshData()
    }

    // ============ Carga de datos ============

    override fun refreshData()
    {
        viewModelScope.launch(exceptionHandler)
        {
            _uiState.update { it.copy(loadingState = LoadingState(isLoading = true)) }
            loadInitialData()
        }
    }

    private suspend fun loadInitialData()
    {
        val years = historyService.getAvaliableYears()
        val resumen = historyService.getResumenAnual()
        val patentes = historyService.getPatentes()
        val autopistas = historyService.getAutopistas()

        _uiState.update { state ->
            state.copy(
                listState = ListState(
                    years = years,
                    resumenAnual = resumen,
                    resumenAnualOriginal = resumen),
                filterState = FilterState(
                    patentes = patentes.map { PatenteFilter(patente = it) },
                    autopistas = autopistas.map { AutopistaFilter(autopista = it) }),
                loadingState = state.loadingState.copy(isLoading = false),
                error = null)
        }
    }

    private fun loadDetail(
        action: suspend () -> Unit,
        onSuccess: suspend (HistorialUiState) -> HistorialUiState)
    {
        viewModelScope.launch(exceptionHandler)
        {
            _uiState.update { it.copy(loadingState = it.loadingState.copy(isLoadingDetail = true)) }

            action()

            _uiState.update { state ->
                onSuccess(state).copy(
                    loadingState = state.loadingState.copy(isLoadingDetail = false))
            }
        }
    }

    // ============ Navegación ============

    fun navigateTo(destination: HistorialDestination) {

        when (destination)
        {
            is HistorialDestination.YearList -> navigateBack()
            is HistorialDestination.MonthView -> selectYear(destination.year)
            is HistorialDestination.DayDetail -> selectDay(destination.year, destination.month, destination.day)
        }

        _uiState.update { state ->
            state.copy(navigationStack = state.navigationStack + destination)
        }
    }

    fun navigateBack()
    {
        _uiState.update { state ->
            val newStack = state.navigationStack.dropLast(1)
            if (newStack.isEmpty())
            {
                state
            }
            else
            {
                val destination = newStack.last()
                state.copy(
                    navigationStack = newStack,
                    detailState = when (destination)
                    {
                        is HistorialDestination.YearList -> null
                        is HistorialDestination.MonthView -> state.detailState?.copy(detalleDia = null)
                        else -> state.detailState
                    })
            }
        }
    }

    // ============ Ordenamiento ============

    fun setSortOption(option: SortOption)
    {
        _uiState.update { state ->
            val listState = state.listState
            val sorted = when (option)
            {
                SortOption.DEFAULT -> listState.resumenAnualOriginal
                SortOption.MOST_CRUCES -> listState.resumenAnualOriginal.sortedByDescending { it.cantidadCruces }
                SortOption.LEAST_CRUCES -> listState.resumenAnualOriginal.sortedBy { it.cantidadCruces }
                SortOption.HIGHEST_AMOUNT -> listState.resumenAnualOriginal.sortedByDescending { it.totalAño }
                SortOption.LOWEST_AMOUNT -> listState.resumenAnualOriginal.sortedBy { it.totalAño }
                SortOption.NEWEST -> listState.resumenAnualOriginal.sortedByDescending { it.año }
                SortOption.OLDEST -> listState.resumenAnualOriginal.sortedBy { it.año }
            }
            state.copy(
                listState = listState.copy(
                    resumenAnual = sorted,
                    currentSort = option))
        }
    }

    // ============ Filtros ============

    fun togglePatente(patente: String)
    {
        _uiState.update { state ->
            val updatedPatentes = state.filterState.patentes.map { filter ->
                if (filter.patente == patente) filter.copy(isSelected = !filter.isSelected)
                else filter
            }
            val seleccionadas = updatedPatentes.filter { it.isSelected }.map { it.patente }

            state.copy(
                filterState = state.filterState.copy(
                    patentes = updatedPatentes,
                    patentesSeleccionadas = seleccionadas))
        }
    }

    fun toggleAutopista(autopista: String)
    {
        _uiState.update { state ->
            val updatedAutopistas = state.filterState.autopistas.map { filter ->
                if (filter.autopista == autopista) filter.copy(isSelected = !filter.isSelected)
                else filter
            }
            val seleccionadas = updatedAutopistas.filter { it.isSelected }.map { it.autopista }

            state.copy(
                filterState = state.filterState.copy(
                    autopistas = updatedAutopistas,
                    autopistasSeleccionadas = seleccionadas))
        }
    }

    fun clearPatenteFilter()
    {
        _uiState.update { state ->
            state.copy(
                filterState = state.filterState.copy(
                    patentes = state.filterState.patentes.map { it.copy(isSelected = false) },
                    patentesSeleccionadas = emptyList()))
        }
        applyFilterIfNeeded()
    }

    fun clearAutopistaFilter()
    {
        _uiState.update { state ->
            state.copy(
                filterState = state.filterState.copy(
                    autopistas = state.filterState.autopistas.map { it.copy(isSelected = false) },
                    autopistasSeleccionadas = emptyList()))
        }
        applyFilterIfNeeded()
    }

    fun clearAllFilters()
    {
        _uiState.update { state ->
            state.copy(
                filterState = state.filterState.copy(
                    patentes = state.filterState.patentes.map { it.copy(isSelected = false) },
                    patentesSeleccionadas = emptyList(),
                    autopistas = state.filterState.autopistas.map { it.copy(isSelected = false) },
                    autopistasSeleccionadas = emptyList()))
        }
        loadInitialDataWrapper()
    }

    fun applyFilters()
    {
        applyFilterIfNeeded()
    }

    private fun applyFilterIfNeeded()
    {
        val patentesSeleccionadas = _uiState.value.filterState.patentesSeleccionadas
        val autopistasSeleccionadas = _uiState.value.filterState.autopistasSeleccionadas

        if (patentesSeleccionadas.isEmpty() && autopistasSeleccionadas.isEmpty())
        {
            loadInitialDataWrapper()
            return
        }

        viewModelScope.launch(exceptionHandler)
        {
            _uiState.update { it.copy(loadingState = LoadingState(isLoading = true)) }

            val filtro = FiltroHistorialRequest(
                patentes = patentesSeleccionadas,
                autopistas = autopistasSeleccionadas)

            val resumen = historyService.getResumenAnualFiltrado(filtro)

            _uiState.update { state ->
                state.copy(
                    listState = ListState(
                        resumenAnual = resumen,
                        resumenAnualOriginal = resumen),
                    loadingState = state.loadingState.copy(isLoading = false),
                    error = null)
            }
        }
    }

    private fun loadInitialDataWrapper()
    {
        viewModelScope.launch(exceptionHandler)
        {
            _uiState.update { it.copy(loadingState = LoadingState(isLoading = true)) }
            loadInitialData()
        }
    }

    // ============ Selección de período ============

    private fun selectYear(año: Int)
    {
        loadDetail(
            action = {
                val result = historyService.getDetalleAnual(año)
                _uiState.update { state ->
                    state.copy(
                        detailState = DetailState(
                            selectedYear = año,
                            detalleAnual = result))
                }
            },
            onSuccess = { it })
    }

    private fun selectDay(year: Int, month: Int, day: Int)
    {
        val currentDetail = _uiState.value.detailState

        if (currentDetail?.detalleMensual == null || currentDetail.detalleMensual.mes != month)
        {
            loadDetail(
                action = {
                    val mensualDetail = historyService.getDetalleMensual(year, month)
                    _uiState.update { state ->
                        state.copy(
                            detailState = state.detailState?.copy(detalleMensual = mensualDetail))
                    }
                    loadDayDetail(year, month, day)
                },
                onSuccess = { it })
        }
        else
        {
            loadDayDetailWrapper(year, month, day)
        }
    }

    private fun loadDayDetailWrapper(year: Int, month: Int, day: Int)
    {
        viewModelScope.launch(exceptionHandler)
        {
            _uiState.update { it.copy(loadingState = it.loadingState.copy(isLoadingDetail = true)) }
            loadDayDetail(year, month, day)
        }
    }

    private suspend fun loadDayDetail(year: Int, month: Int, day: Int)
    {
        val diaDetail = historyService.getDetalleDiario(year, month, day)
        _uiState.update { state ->
            state.copy(
                detailState = state.detailState?.copy(detalleDia = diaDetail),
                loadingState = state.loadingState.copy(isLoadingDetail = false))
        }
    }

    fun selectMonth(mes: Int)
    {
        val año = _uiState.value.detailState?.selectedYear ?: return

        loadDetail(
            action = {
                val result = historyService.getDetalleMensual(año, mes)
                _uiState.update { state ->
                    state.copy(
                        detailState = state.detailState?.copy(
                            detalleMensual = result,
                            detalleDia = null))
                }
            },
            onSuccess = { it })
    }

    // ============ Utilidades ============

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    companion object
    {
        val Factory = ServiceLocator.viewModels.historialViewModelFactory()
    }
}