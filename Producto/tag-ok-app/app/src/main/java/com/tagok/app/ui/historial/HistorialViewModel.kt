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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialUiState(
    val years: List<Int> = emptyList(),
    val resumenAnual: List<ResumenAnual> = emptyList(),
    val detalleAnual: ResumenAnual? = null,
    val detalleMensual: DetalleMensual? = null,
    val detalleDia: DetalleDia? = null,
    val selectedYear: Int? = null,
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val error: String? = null)

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

                _uiState.update {
                    it.copy(
                        years = years,
                        resumenAnual = resumen,
                        isLoading = false
                    )
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
            catch (e: Exception) {
                Log.e(TAG, "Error cargando detalle mensual", e)
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

    fun clearYearDetail() {
        _uiState.update {
            it.copy(
                selectedYear = null,
                detalleAnual = null,
                detalleMensual = null
            )
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

    fun clearDayDetail()
    {
        _uiState.update { it.copy(detalleDia = null) }
    }

    fun clearMonthDetail()
    {
        _uiState.update {
            it.copy(detalleMensual = null, detalleDia = null)
        }
    }

    companion object {
        private const val TAG = "HistorialViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val api = HistoryApi(HttpClientProvider.client)
                val repository = HistoryRepository(api)
                return HistorialViewModel(repository) as T
            }
        }
    }
}