package com.tagok.app.ui.presupuesto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.NuevoPresupuesto
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.di.modules.ViewModelModule
import com.tagok.app.domain.interfaces.IHistoryRepository
import com.tagok.app.domain.interfaces.IPresupuestoRepository
import com.tagok.app.domain.interfaces.IVehiculoRepository
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.presupuesto.Presupuesto
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class PresupuestoUiState(
    val presupuestos: List<Presupuesto> = emptyList(),
    val vehiculos: List<Vehiculo> = emptyList(),
    val vehiculoIdFiltro: String? = null,
    val isLoading: Boolean = false,
    val gastoActual: Int = 0,
    val peajesCount: Int = 0,
    val isLoadingGasto: Boolean = false,
    val showEditSheet: Boolean = false,
    val formMonto: String = "",
    val formUmbral1: Float = 75f,
    val formUmbral2: Float = 90f,
    val formAlertasActivas: Boolean = true,
    val isSaving: Boolean = false,
    val errorMsg: String? = null)
{
    val presupuestoActual: Presupuesto?
        get() = presupuestos.find { it.vehiculoId == vehiculoIdFiltro }
}

class PresupuestoViewModel(
    private val vehiculoRepository: IVehiculoRepository,
    private val presupuestoRepository: IPresupuestoRepository,
    private val historyRepository: IHistoryRepository) : ViewModel()
{
    private val _state = MutableStateFlow(PresupuestoUiState())
    val state: StateFlow<PresupuestoUiState> = _state.asStateFlow()

    init { cargar() }

    fun cargar()
    {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val presupuestos = presupuestoRepository.getAll()
                val vehiculos    = vehiculoRepository.getVehiculos()
                _state.update { it.copy(presupuestos = presupuestos, vehiculos = vehiculos) }
            }.onFailure { e ->
                _state.update { it.copy(errorMsg = e.message) }
            }
            _state.update { it.copy(isLoading = false) }
            cargarGasto()
        }
    }

    fun seleccionarVehiculo(vehiculoId: String?)
    {
        _state.update { it.copy(vehiculoIdFiltro = vehiculoId) }
        viewModelScope.launch { cargarGasto() }
    }

    private suspend fun cargarGasto()
    {
        _state.update { it.copy(isLoadingGasto = true) }
        val s = _state.value
        runCatching {
            val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            if (s.vehiculoIdFiltro == null)
            {
                historyRepository.getDetalleMensual(hoy.year, hoy.monthNumber)
            }
            else
            {
                val patente = s.vehiculos.find { it.id == s.vehiculoIdFiltro }?.patente
                    ?: return@runCatching DetalleMensual(hoy.year, hoy.monthNumber, emptyList(), 0.0)
                historyRepository.getDetalleMensualFiltrado(
                    hoy.year, hoy.monthNumber, FiltroHistorialRequest(patentes = listOf(patente)))
            }
        }.onSuccess { detalle ->
            _state.update {
                it.copy(
                    gastoActual = detalle.totalMes.toInt(),
                    peajesCount = detalle.dias.sumOf { d -> d.cantidadCruces },
                )
            }
        }.onFailure {
            _state.update { it.copy(gastoActual = 0, peajesCount = 0) }
        }
        _state.update { it.copy(isLoadingGasto = false) }
    }

    fun abrirEditSheet() {
        val actual = _state.value.presupuestoActual
        _state.update {
            it.copy(
                showEditSheet      = true,
                formMonto          = actual?.montoMensual?.toString() ?: "",
                formUmbral1        = actual?.umbralAlerta1?.toFloat() ?: 75f,
                formUmbral2        = actual?.umbralAlerta2?.toFloat() ?: 90f,
                formAlertasActivas = actual?.alertasActivas ?: true,
            )
        }
    }

    fun cerrarEditSheet() = _state.update { it.copy(showEditSheet = false) }

    fun updateFormMonto(v: String)   = _state.update { it.copy(formMonto = v) }
    fun updateUmbral1(v: Float) = _state.update { it.copy(formUmbral1 = v) }
    fun updateUmbral2(v: Float) = _state.update { it.copy(formUmbral2 = v) }
    fun updateAlertasActivas(v: Boolean) = _state.update { it.copy(formAlertasActivas = v) }

    fun guardar() {
        val s      = _state.value
        val monto = s.formMonto.toIntOrNull() ?: run {
            _state.update { it.copy(errorMsg = "Ingresa un monto válido") }
            return
        }
        if (s.formUmbral1 >= s.formUmbral2) {
            _state.update { it.copy(errorMsg = "La primera alerta debe ser menor que la segunda") }
            return
        }
        val userId = supabase.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            runCatching {
                presupuestoRepository.save(
                    NuevoPresupuesto(
                        userId         = userId,
                        vehiculoId     = s.vehiculoIdFiltro,
                        montoMensual   = monto,
                        umbralAlerta1  = s.formUmbral1.toInt(),
                        umbralAlerta2  = s.formUmbral2.toInt(),
                        alertasActivas = s.formAlertasActivas,
                    )
                )
            }.onSuccess {
                cargar()
                _state.update { it.copy(isSaving = false, showEditSheet = false) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, errorMsg = e.message ?: "Error al guardar") }
            }
        }
    }

    fun clearError() = _state.update { it.copy(errorMsg = null) }

    companion object
    {
        private const val TAG = "PresupuestoViewModel"
        val Factory: ViewModelProvider.Factory = ViewModelModule.presupuestoViewModelFactory()
    }
}
