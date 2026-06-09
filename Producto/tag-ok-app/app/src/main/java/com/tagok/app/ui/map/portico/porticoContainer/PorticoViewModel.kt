package com.tagok.app.ui.map.portico.porticoContainer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.di.ServiceLocator
import com.tagok.app.domain.exceptions.ApplicationError
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.services.PorticoService
import com.tagok.app.domain.services.interfaces.IPorticoService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PorticosUiState(
    val porticos: List<PorticoResumen> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null)

class PorticosViewModel(
    private val porticoService: IPorticoService) : ViewModel()
{
    private val _uiState = MutableStateFlow(PorticosUiState())
    val uiState: StateFlow<PorticosUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        val errorMessage = when (exception)
        {
            is ApplicationError -> exception.message ?: "Error desconocido"
            else -> "Error inesperado: ${exception.message}"
        }

        Log.e(TAG, "Error: $errorMessage", exception)

        _uiState.update {
            it.copy(
                error = errorMessage,
                isLoading = false)
        }
    }

    init
    {
        cargarPorticos()
    }

    fun cargarPorticos()
    {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val porticos = porticoService.obtenerPorticos()

            _uiState.update {
                it.copy(
                    porticos = porticos,
                    isLoading = false)
            }
        }
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    companion object {
        private const val TAG = "PorticosViewModel"
        val Factory = ServiceLocator.viewModels.porticosViewModelFactory()
    }
}