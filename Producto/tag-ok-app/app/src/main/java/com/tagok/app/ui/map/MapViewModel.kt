// ui/map/MapViewModel.kt
package com.tagok.app.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.dto.PorticoCruzadoRequest
import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.data.remote.RouteApi
import com.tagok.app.data.repository.PorticoRepository
import com.tagok.app.data.repository.RouteRepository
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.tarifa.TarifaCalculada
import com.tagok.app.domain.vehiculo.TipoVehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class MapUiState(
    val porticos: List<PorticoResumen> = emptyList(),
    val tarifaCalculada: TarifaCalculada? = null,
    val isCalculating: Boolean = false,
    val error: String? = null)

class MapViewModel(
    private val porticoRepository: PorticoRepository,
    private val routeRepository: RouteRepository) : ViewModel()
{
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadPorticos()
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    fun clearTarifa()
    {
        _uiState.update { it.copy(tarifaCalculada = null) }
    }

    fun simularCruceAleatorio(vehiculo: TipoVehiculo)
    {
        val porticos = _uiState.value.porticos
        if (porticos.isEmpty())
        {
            _uiState.update { it.copy(error = "No hay pórticos disponibles") }
            return
        }

        val porticoAleatorio = porticos.random()
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, error = null) }

            try
            {
                val request = TarifaRequest(
                    references = listOf(
                        PorticoCruzadoRequest(
                            porticoId = porticoAleatorio.id,
                            porticoHoraFechaCruce = now.format(formatter),
                            null,
                            null)),
                    vehiculo = vehiculo.name,
                    patente = "ABCD-33")

                Log.d(TAG, "simularCruceAleatorio: enviando request $request")

                val response = routeRepository.calculateTarifa(request)

                Log.d(TAG, "simularCruceAleatorio: respuesta -> total=${response.total}, cruces=${response.cruces.size}")

                _uiState.update {
                    it.copy(
                        tarifaCalculada = response,
                        isCalculating = false)
                }
            }
            catch (e: Exception)
            {
                Log.e(TAG, "simularCruceAleatorio: error", e)
                _uiState.update {
                    it.copy(
                        error = "Error al calcular tarifa: ${e.message}",
                        isCalculating = false)
                }
            }
        }
    }

    private fun loadPorticos()
    {
        viewModelScope.launch {
            Log.d(TAG, "loadPorticos: iniciando...")

            runCatching {
                porticoRepository.getPorticos()
            }.onSuccess { list ->
                Log.d(TAG, "loadPorticos OK — ${list.size} pórticos")
                _uiState.update { it.copy(porticos = list) }
            }.onFailure { e ->
                Log.e(TAG, "loadPorticos FAIL: ${e.message}", e)
                _uiState.update { it.copy(error = "Error cargando pórticos: ${e.message}") }
            }
        }
    }

    companion object
    {
        private const val TAG = "MapViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory
        {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val routeApi = RouteApi(HttpClientProvider.client)
                val routeRepo = RouteRepository(routeApi)

                val porticoApi = PorticoApi(HttpClientProvider.client)
                val porticoRepo = PorticoRepository(porticoApi)

                return MapViewModel(porticoRepo, routeRepo) as T
            }
        }
    }
}