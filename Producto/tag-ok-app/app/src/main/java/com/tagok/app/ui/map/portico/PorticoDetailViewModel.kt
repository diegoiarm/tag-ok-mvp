package com.tagok.app.ui.map.portico

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.data.repository.PorticoRepository
import com.tagok.app.domain.model.portico.CalendarioTarifario
import com.tagok.app.domain.model.portico.PorticoTramoType
import com.tagok.app.domain.model.portico.PorticoType
import com.tagok.app.domain.model.portico.RangoHorario
import com.tagok.app.domain.model.portico.TollType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class PorticoDetailUiState(
    val detalle: TollType? = null,
    val tipoTarifaActual: String? = null,
    val tipoVehiculo: String? = null,
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
                .onSuccess { detalle ->
                    _uiState.update {
                        it.copy(
                            detalle = detalle,
                            tipoTarifaActual = resolverTipoTarifaActual(detalle),
                            tipoVehiculo = "AUTO",
                            isLoading = false,
                        )
                    }
                }
                .onFailure { e ->
                    Log.e("PorticoDetailVM", "Error al cargar detalle", e)
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    private fun resolverTipoTarifaActual(detalle: TollType): String
    {
        val ahora = Clock.System.now()
            .toLocalDateTime(TimeZone.of("America/Santiago"))
        val horaActual = ahora.time
        val diaSemana  = ahora.dayOfWeek

        val tipoDia = when
        {
            diaSemana.value == 7 -> "DOMINGO"
            diaSemana.value == 6 -> "SABADO_FESTIVO"
            else                 -> "LABORAL"
        }

        fun buscarEnCalendario(calendario: CalendarioTarifario): String?
        {
            return calendario.reglas
                .filter { it.tipoDia == tipoDia }
                .firstOrNull { regla -> regla.tramos.any { horaActual in it } }
                ?.tipoTarifa
        }

        return when (detalle)
        {
            is PorticoType -> {
                buscarEnCalendario(detalle.calendario) ?: "TBFP"
            }

            is PorticoTramoType -> {
                detalle.tramos.firstNotNullOfOrNull { tramo ->
                    buscarEnCalendario(tramo.calendario)
                } ?: "TBFP"
            }
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

private operator fun RangoHorario.contains(time: LocalTime): Boolean =
    time in horaInicio..<horaFin