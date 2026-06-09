package com.tagok.app.ui.boleta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.remote.BoletaApi
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.repository.BoletaRepository
import com.tagok.app.data.repository.HistoryRepository
import com.tagok.app.di.ServiceLocator
import com.tagok.app.domain.exceptions.ApplicationError
import com.tagok.app.domain.services.BoletaService
import com.tagok.app.ui.common.RefreshableViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class BoletaViewModel(
    private val boletaService: BoletaService) : ViewModel(), RefreshableViewModel
{
    private val _uiState = MutableStateFlow(BoletaUiState())
    val uiState: StateFlow<BoletaUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        val errorMessage = when (exception)
        {
            is ApplicationError -> exception.message ?: "Error desconocido"
            else -> "Error inesperado: ${exception.message}"
        }

        _uiState.update {
            it.copy(
                error = errorMessage,
                isLoading = false)
        }
    }

    init
    {
        refreshData()
    }

    override fun refreshData()
    {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val datos = boletaService.cargarDatosIniciales()

            _uiState.update { state ->
                state.copy(
                    patentes = datos.patentes,
                    autopistas = datos.autopistas,
                    fechaDesde = datos.fechaDesde,
                    fechaHasta = datos.fechaHasta,
                    patenteSeleccionada = state.patenteSeleccionada.ifEmpty {
                        datos.patentePorDefecto
                    },
                    isLoading = false)
            }
        }
    }

    fun generarBoleta()
    {
        val state = _uiState.value

        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val boleta = boletaService.generarBoletaValidada(
                patente = state.patenteSeleccionada,
                autopistasSeleccionadas = state.autopistasSeleccionadas,
                fechaDesde = state.fechaDesde,
                fechaHasta = state.fechaHasta)

            _uiState.update {
                it.copy(boleta = boleta, isLoading = false)
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

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    companion object
    {
        val Factory = ServiceLocator.viewModels.boletaViewModelFactory()
    }
}