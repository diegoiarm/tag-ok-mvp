package com.tagok.app.ui.historial

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.repository.HistoryRepository
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.historial.model.AutopistaFilter
import com.tagok.app.ui.historial.model.PatenteFilter
import com.tagok.app.ui.historial.model.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistorialViewModel(
    private val historyRepository: HistoryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    private val usuarioId: String = "portico-cruzado"

    init
    {
        loadInitialData()
    }

    // ============ Carga de datos ============

    fun loadInitialData()
    {
        executeWithLoading { state ->
            try
            {
                val years = historyRepository.getAvailableYears(usuarioId)
                val resumen = historyRepository.getResumenAnual(usuarioId)
                val patentes = historyRepository.getPatentes(usuarioId)
                val autopistas = historyRepository.getAutopistas(usuarioId)

                state.copy(
                    listState = ListState(
                        years = years,
                        resumenAnual = resumen,
                        resumenAnualOriginal = resumen),
                    filterState = FilterState(
                        patentes = patentes.map { PatenteFilter(patente = it) },
                        autopistas = autopistas.map { AutopistaFilter(autopista = it) }),
                    error = null)
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error cargando datos iniciales", e)
                state.copy(error = e.message)
            }
        }
    }

    private fun <T> loadDetail(
        action: suspend () -> T,
        onSuccess: (HistorialUiState, T) -> HistorialUiState)
    {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loadingState = it.loadingState.copy(isLoadingDetail = true))
            }
            try
            {
                val result: T = action()
                _uiState.update { state ->
                    onSuccess(state, result).copy(
                        loadingState = state.loadingState.copy(isLoadingDetail = false))
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error cargando detalle", e)
                _uiState.update {
                    it.copy(
                        error = e.message,
                        loadingState = it.loadingState.copy(isLoadingDetail = false))
                }
            }
        }
    }

    // ============ Navegación ============

    fun navigateTo(destination: HistorialDestination)
    {
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

    // ============ Filtros de patentes ============

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
        loadInitialData()
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
            loadInitialData()
            return
        }

        executeWithLoading { state ->
            try
            {
                val filtro = FiltroHistorialRequest(
                    patentes = patentesSeleccionadas,
                    autopistas = autopistasSeleccionadas)
                val resumen = historyRepository.getResumenAnualFiltrado(usuarioId, filtro)
                state.copy(
                    listState = ListState(
                        resumenAnual = resumen,
                        resumenAnualOriginal = resumen),
                    filterState = state.filterState,
                    error = null)
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error filtrando", e)
                state.copy(error = e.message)
            }
        }
    }

    // ============ Selección de período ============

    private fun selectYear(año: Int)
    {
        loadDetail(
            action = { historyRepository.getDetalleAnual(usuarioId, año) },
            onSuccess = { state, result ->
                state.copy(
                    detailState = DetailState(
                        selectedYear = año,
                        detalleAnual = result))
            })
    }

    private fun selectDay(year: Int, month: Int, day: Int)
    {
        val currentDetail = _uiState.value.detailState

        if (currentDetail?.detalleMensual == null ||
            currentDetail.detalleMensual.mes != month)
        {
            loadDetail(
                action = { historyRepository.getDetalleMensual(usuarioId, year, month) },
                onSuccess = { state, result ->
                    val mensualDetail = result as DetalleMensual
                    loadDayDetail(year, month, day, mensualDetail)
                    state.copy(
                        detailState = state.detailState?.copy(
                            detalleMensual = mensualDetail))
                })
        }
        else
        {
            loadDayDetail(year, month, day, currentDetail.detalleMensual!!)
        }
    }

    private fun loadDayDetail(year: Int, month: Int, day: Int, mensualDetail: DetalleMensual)
    {
        loadDetail(
            action = { historyRepository.getDetalleDia(usuarioId, year, month, day) },
            onSuccess = { state, result ->
                state.copy(
                    detailState = DetailState(
                        selectedYear = year,
                        detalleAnual = state.detailState?.detalleAnual,
                        detalleMensual = mensualDetail,
                        detalleDia = result as DetalleDia))
            })
    }

    fun selectMonth(mes: Int)
    {
        val año = _uiState.value.detailState?.selectedYear ?: return

        loadDetail(
            action = { historyRepository.getDetalleMensual(usuarioId, año, mes) },
            onSuccess = { state, result ->
                state.copy(
                    detailState = state.detailState?.copy(
                        detalleMensual = result,
                        detalleDia = null))
            })
    }

    // ============ Utilidades ============

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    private fun executeWithLoading(block: suspend (HistorialUiState) -> HistorialUiState)
    {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loadingState = LoadingState(isLoading = true))
            }
            try
            {
                _uiState.update { state ->
                    block(state).copy(
                        loadingState = state.loadingState.copy(isLoading = false))
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error inesperado", e)
                _uiState.update {
                    it.copy(
                        error = e.message,
                        loadingState = it.loadingState.copy(isLoading = false))
                }
            }
        }
    }

    companion object
    {
        private const val TAG = "HistorialViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory
        {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val api = HistoryApi(HttpClientProvider.client)
                val repository = HistoryRepository(api)
                return HistorialViewModel(repository) as T
            }
        }
    }
}