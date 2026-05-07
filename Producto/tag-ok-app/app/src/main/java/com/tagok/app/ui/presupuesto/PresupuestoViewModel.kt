package com.tagok.app.ui.presupuesto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.NuevoPresupuesto
import com.tagok.app.data.Presupuesto
import com.tagok.app.data.PresupuestoRepository
import com.tagok.app.data.Vehiculo
import com.tagok.app.data.VehiculoRepository
import com.tagok.app.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PresupuestoUiState(
    val presupuestos: List<Presupuesto> = emptyList(),
    val vehiculos: List<Vehiculo> = emptyList(),
    val vehiculoIdFiltro: String? = null, // null = global
    val isLoading: Boolean = false,
    val showEditSheet: Boolean = false,
    val formMonto: String = "",
    val formUmbral1: Float = 75f,
    val formUmbral2: Float = 90f,
    val isSaving: Boolean = false,
    val errorMsg: String? = null,
) {
    val presupuestoActual: Presupuesto?
        get() = presupuestos.find { it.vehiculoId == vehiculoIdFiltro }
}

class PresupuestoViewModel : ViewModel() {

    private val repo         = PresupuestoRepository()
    private val vehiculoRepo = VehiculoRepository()

    private val _state = MutableStateFlow(PresupuestoUiState())
    val state: StateFlow<PresupuestoUiState> = _state.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val presupuestos = repo.getAll()
                val vehiculos    = vehiculoRepo.getVehiculos()
                _state.update { it.copy(presupuestos = presupuestos, vehiculos = vehiculos) }
            }.onFailure { e ->
                _state.update { it.copy(errorMsg = e.message) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun seleccionarVehiculo(vehiculoId: String?) =
        _state.update { it.copy(vehiculoIdFiltro = vehiculoId) }

    fun abrirEditSheet() {
        val actual = _state.value.presupuestoActual
        _state.update {
            it.copy(
                showEditSheet = true,
                formMonto     = actual?.montoMensual?.toString() ?: "",
                formUmbral1   = actual?.umbralAlerta1?.toFloat() ?: 75f,
                formUmbral2   = actual?.umbralAlerta2?.toFloat() ?: 90f,
            )
        }
    }

    fun cerrarEditSheet() = _state.update { it.copy(showEditSheet = false) }

    fun updateFormMonto(v: String)   = _state.update { it.copy(formMonto = v) }
    fun updateUmbral1(v: Float) = _state.update { it.copy(formUmbral1 = v) }
    fun updateUmbral2(v: Float) = _state.update { it.copy(formUmbral2 = v) }

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
                repo.save(
                    NuevoPresupuesto(
                        userId        = userId,
                        vehiculoId    = s.vehiculoIdFiltro,
                        montoMensual  = monto,
                        umbralAlerta1 = s.formUmbral1.toInt(),
                        umbralAlerta2 = s.formUmbral2.toInt(),
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
}
