package com.tagok.app.ui.boleta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.remote.BoletaApi
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.repository.BoletaRepository
import com.tagok.app.data.repository.HistoryRepository
import com.tagok.app.ui.common.RefreshableViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class BoletaViewModel(
    private val boletaRepository: BoletaRepository,
    private val historyRepository: HistoryRepository): ViewModel(), RefreshableViewModel
{
    private val _uiState = MutableStateFlow(BoletaUiState())
    val uiState: StateFlow<BoletaUiState> = _uiState.asStateFlow()

    init
    {
        refreshData()
    }

    /**
     * Refresca los datos necesarios para la pantalla.
     * Se llama automáticamente cuando la pantalla vuelve a estar en primer plano.
     */
    override fun refreshData()
    {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try
            {
                val patentesDeferred = async { historyRepository.getPatentes() }
                val autopistasDeferred = async { historyRepository.getAutopistas() }

                val patentes = patentesDeferred.await()
                val autopistas = autopistasDeferred.await()

                val hoy = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date

                val desde = LocalDate(
                    year = hoy.year,
                    monthNumber = hoy.monthNumber,
                    dayOfMonth = 5
                ).minus(1, DateTimeUnit.MONTH)

                val hasta = LocalDate(
                    year = hoy.year,
                    monthNumber = hoy.monthNumber,
                    dayOfMonth = 5)

                _uiState.update {
                    it.copy(
                        patentes = patentes,
                        autopistas = autopistas,
                        fechaDesde = desde,
                        fechaHasta = hasta,
                        patenteSeleccionada = if (it.patenteSeleccionada.isEmpty())
                        {
                            patentes.firstOrNull() ?: ""
                        }
                        else
                        {
                            it.patenteSeleccionada
                        },
                        isLoading = false,
                        // No limpiar la boleta generada para mantener el estado
                        // boleta = null  // Descomenta si quieres limpiar la boleta al refrescar
                    )
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error refreshing data", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar datos: ${e.message}")
                }
            }
        }
    }

    fun setPatente(patente: String)
    {
        _uiState.update { it.copy(patenteSeleccionada = patente) }
    }

    fun setFechaDesde(fecha: LocalDate)
    {
        _uiState.update { it.copy(fechaDesde = fecha) }
    }

    fun setFechaHasta(fecha: LocalDate)
    {
        _uiState.update { it.copy(fechaHasta = fecha) }
    }

    fun toggleAutopista(autopista: String)
    {
        _uiState.update { state ->
            val nuevasSeleccionadas = if (autopista in state.autopistasSeleccionadas)
            {
                state.autopistasSeleccionadas - autopista
            }
            else
            {
                state.autopistasSeleccionadas + autopista
            }
            state.copy(autopistasSeleccionadas = nuevasSeleccionadas)
        }
    }

    fun generarBoleta()
    {
        val state = _uiState.value

        if (state.patenteSeleccionada.isEmpty())
        {
            _uiState.update { it.copy(error = "Seleccione una patente") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try
            {
                val request = BoletaRequest(
                    patente = state.patenteSeleccionada,
                    autopistas = state.autopistasSeleccionadas,
                    fechaDesde = state.fechaDesde,
                    fechaHasta = state.fechaHasta)

                val boleta = boletaRepository.generarBoleta(request)
                _uiState.update {
                    it.copy(boleta = boleta, isLoading = false)
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error generando boleta", e)
                _uiState.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    companion object
    {
        private const val TAG = "BoletaViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory
        {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val client = HttpClientProvider.client
                val apiH = HistoryApi(client)
                val apiB = BoletaApi(client)
                val rH = HistoryRepository(apiH)
                val rB = BoletaRepository(apiB)
                return BoletaViewModel(rB, rH) as T
            }
        }
    }
}