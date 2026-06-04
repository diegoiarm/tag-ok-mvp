package com.tagok.app.ui.historial

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.repository.HistoryRepository
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.historial.model.PatenteFilter
import com.tagok.app.ui.historial.model.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialUiState(
    val years: List<Int> = emptyList(),
    val resumenAnual: List<ResumenAnual> = emptyList(),
    val resumenAnualOriginal: List<ResumenAnual> = emptyList(),
    val detalleAnual: ResumenAnual? = null,
    val detalleMensual: DetalleMensual? = null,
    val detalleDia: DetalleDia? = null,
    val selectedYear: Int? = null,
    val patentes: List<PatenteFilter> = emptyList(),
    val patentesSeleccionadas: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val error: String? = null,
    val currentSort: SortOption = SortOption.DEFAULT)

class HistorialViewModel(
    private val historyRepository: HistoryRepository) : ViewModel()
{

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    private val usuarioId: String = "portico-cruzado"

    fun loadInitialData()
    {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try
            {
                val years = historyRepository.getAvailableYears(usuarioId)
                val resumen = historyRepository.getResumenAnual(usuarioId)
                val patentes = historyRepository.getPatentes(usuarioId)

                val patenteFilters = patentes.map { PatenteFilter(patente = it) }

                _uiState.update {
                    it.copy(
                        years = years,
                        resumenAnual = resumen,
                        resumenAnualOriginal = resumen,
                        patentes = patenteFilters,
                        isLoading = false)
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error cargando datos iniciales", e)
                _uiState.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }

    fun togglePatente(patente: String)
    {
        _uiState.update { state ->
            val updatedPatentes = state.patentes.map { filter ->
                if (filter.patente == patente)
                {
                    filter.copy(isSelected = !filter.isSelected)
                }
                else
                {
                    filter
                }
            }

            val seleccionadas = updatedPatentes
                .filter { it.isSelected }
                .map { it.patente }

            state.copy(
                patentes = updatedPatentes,
                patentesSeleccionadas = seleccionadas)
        }
    }

    fun clearPatenteFilter()
    {
        _uiState.update { state ->
            state.copy(
                patentes = state.patentes.map { it.copy(isSelected = false) },
                patentesSeleccionadas = emptyList())
        }
    }

    fun setSortOption(option: SortOption)
    {
        _uiState.update { state ->
            val sorted = when (option)
            {
                SortOption.DEFAULT -> state.resumenAnualOriginal
                SortOption.MOST_CRUCES -> state.resumenAnualOriginal.sortedByDescending { it.cantidadCruces }
                SortOption.LEAST_CRUCES -> state.resumenAnualOriginal.sortedBy { it.cantidadCruces }
                SortOption.HIGHEST_AMOUNT -> state.resumenAnualOriginal.sortedByDescending { it.totalAño }
                SortOption.LOWEST_AMOUNT -> state.resumenAnualOriginal.sortedBy { it.totalAño }
                SortOption.NEWEST -> state.resumenAnualOriginal.sortedByDescending { it.año }
                SortOption.OLDEST -> state.resumenAnualOriginal.sortedBy { it.año }
            }
            state.copy(resumenAnual = sorted, currentSort = option)
        }
    }

    fun selectYear(año: Int)
    {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedYear = año, isLoadingDetail = true) }

            try
            {
                val detalle = historyRepository.getDetalleAnual(usuarioId, año)
                _uiState.update {
                    it.copy(detalleAnual = detalle, isLoadingDetail = false)
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error cargando detalle anual", e)
                _uiState.update {
                    it.copy(error = e.message, isLoadingDetail = false)
                }
            }
        }
    }

    fun selectMonth(mes: Int)
    {
        val año = _uiState.value.selectedYear ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true) }

            try
            {
                val detalle = historyRepository.getDetalleMensual(usuarioId, año, mes)
                _uiState.update {
                    it.copy(detalleMensual = detalle, isLoadingDetail = false)
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error cargando detalle mensual", e)
                _uiState.update {
                    it.copy(error = e.message, isLoadingDetail = false)
                }
            }
        }
    }

    fun selectDay(mes: Int, dia: Int)
    {
        val año = _uiState.value.selectedYear ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true) }

            try
            {
                val detalle = historyRepository.getDetalleDia(usuarioId, año, mes, dia)
                _uiState.update {
                    it.copy(detalleDia = detalle, isLoadingDetail = false)
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error cargando detalle del día", e)
                _uiState.update {
                    it.copy(error = e.message, isLoadingDetail = false)
                }
            }
        }
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    fun clearYearDetail()
    {
        _uiState.update {
            it.copy(
                selectedYear = null,
                detalleAnual = null,
                detalleMensual = null,
                detalleDia = null)
        }
    }

    fun clearMonthDetail()
    {
        _uiState.update {
            it.copy(detalleMensual = null, detalleDia = null)
        }
    }

    fun clearDayDetail()
    {
        _uiState.update { it.copy(detalleDia = null) }
    }

    companion object {
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