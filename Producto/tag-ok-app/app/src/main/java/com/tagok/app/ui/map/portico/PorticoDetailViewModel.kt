package com.tagok.app.ui.map.portico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.data.repository.PorticoRepository
import com.tagok.app.domain.model.portico.TollType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PorticoDetailUiState(
    val detalle: TollType? = null,
    val isLoading: Boolean = false,
    val error: String? = null)

class PorticoDetailViewModel(private val porticoRepository: PorticoRepository) : ViewModel()
{

    private val _uiState = MutableStateFlow(PorticoDetailUiState())
    val uiState: StateFlow<PorticoDetailUiState> = _uiState.asStateFlow()

    fun load(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { porticoRepository.getPorticoById(id) }
                .onSuccess { detalle -> _uiState.update { it.copy(detalle = detalle, isLoading = false) } }
                .onFailure { e ->    _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val api = PorticoApi(HttpClientProvider.client)
                return PorticoDetailViewModel(PorticoRepository(api)) as T
            }
        }
    }
}