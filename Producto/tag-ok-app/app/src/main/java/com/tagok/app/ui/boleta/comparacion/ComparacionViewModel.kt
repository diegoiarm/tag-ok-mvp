package com.tagok.app.ui.boleta.comparacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.di.ServiceLocator
import com.tagok.app.domain.exceptions.ApplicationError
import com.tagok.app.domain.model.boleta.ArchivoFactura
import com.tagok.app.domain.services.BoletaService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class ComparacionViewModel(
    private val boletaService: BoletaService,
    patente: String,
    fechaDesde: LocalDate,
    fechaHasta: LocalDate,
    autopistas: List<String>) : ViewModel()
{
    private val _uiState = MutableStateFlow(
        ComparacionUiState(
            patente = patente,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta,
            autopistas = autopistas))
    val uiState: StateFlow<ComparacionUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        val errorMessage = when (exception)
        {
            is ApplicationError -> exception.message ?: "Error desconocido"
            else -> "Error inesperado: ${exception.message}"
        }

        _uiState.update {
            it.copy(
                error = errorMessage,
                isComparando = false,
                cargandoArchivo = false)
        }
    }

    fun onArchivoSeleccionado(archivo: ArchivoFactura)
    {
        _uiState.update {
            it.copy(archivo = archivo, resultado = null, cargandoArchivo = false)
        }
    }

    fun quitarArchivo()
    {
        _uiState.update { it.copy(archivo = null) }
    }

    fun setCargandoArchivo(cargando: Boolean)
    {
        _uiState.update { it.copy(cargandoArchivo = cargando) }
    }

    fun mostrarError(mensaje: String)
    {
        _uiState.update { it.copy(error = mensaje, cargandoArchivo = false) }
    }

    fun comparar()
    {
        val state = _uiState.value
        val archivo = state.archivo ?: return

        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isComparando = true, error = null) }

            val resultado = boletaService.compararFacturaValidada(
                patente = state.patente,
                autopistasSeleccionadas = state.autopistas,
                fechaDesde = state.fechaDesde,
                fechaHasta = state.fechaHasta,
                archivo = archivo)

            _uiState.update {
                it.copy(resultado = resultado, isComparando = false)
            }
        }
    }

    fun nuevaComparacion()
    {
        _uiState.update { it.copy(archivo = null, resultado = null, error = null) }
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    companion object
    {
        fun factory(
            patente: String,
            fechaDesde: LocalDate,
            fechaHasta: LocalDate,
            autopistas: List<String>) =
            ServiceLocator.viewModels.comparacionViewModelFactory(
                patente, fechaDesde, fechaHasta, autopistas)
    }
}
