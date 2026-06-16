package com.tagok.app.ui.notificaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.di.modules.ViewModelModule
import com.tagok.app.domain.interfaces.INotificacionRepository
import com.tagok.app.domain.model.notificacion.Notificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificacionesUiState(
    val isLoading: Boolean = false,
    val items: List<Notificacion> = emptyList(),
    val errorMsg: String? = null)

class NotificacionesViewModel(
    private val notificacionRepository: INotificacionRepository) : ViewModel()
{
    private val _state = MutableStateFlow(NotificacionesUiState())
    val state: StateFlow<NotificacionesUiState> = _state.asStateFlow()

    init { cargar() }

    fun cargar()
    {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            runCatching { notificacionRepository.getAll() }
                .onSuccess { items -> _state.update { it.copy(items = items, isLoading = false) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMsg = e.message) } }
        }
    }

    fun marcarLeida(id: String)
    {
        // Optimista: marca local y persiste en segundo plano.
        _state.update { st ->
            st.copy(items = st.items.map { if (it.id == id) it.copy(leida = true) else it })
        }
        viewModelScope.launch {
            runCatching { notificacionRepository.marcarLeida(id) }
        }
    }

    companion object
    {
        val Factory: ViewModelProvider.Factory = ViewModelModule.notificacionesViewModelFactory()
    }
}
