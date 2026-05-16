package com.tagok.app.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.dto.portico.PorticoResumenResponse
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.data.remote.RouteApi
import com.tagok.app.data.repository.PorticoRepository
import com.tagok.app.data.repository.RouteRepository
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.routes.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapUiState(
    val porticos: List<PorticoResumen> = emptyList(),
    val error: String? = null)

class MapViewModel(private val porticoRepository: PorticoRepository) : ViewModel()
{
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init
    {
        loadPorticos()
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadPorticos()
    {
        viewModelScope.launch {

            Log.d(TAG, "loadPorticos: iniciando...")

            runCatching {
                porticoRepository.getPorticos()
            }.onSuccess { list ->

                Log.d(TAG, "loadPorticos OK — ${list.size} pórticos")

                _uiState.update {
                    it.copy(porticos = list)
                }

            }.onFailure { e ->
                Log.e(TAG, "loadPorticos FAIL: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "MapViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val porticoApi = PorticoApi(HttpClientProvider.client)
                val portico = PorticoRepository(porticoApi)

                return MapViewModel(portico) as T
            }
        }
    }
}