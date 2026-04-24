package com.tagok.app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class RegisterFormState(
    val nombre: String = "",
    val apellidos: String = "",
    val fechaNacimiento: String = "",
    val ciudad: String = "",
    val comuna: String = "",
    val email: String = "",
    val celular: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val tipoVehiculo: String = "",
    val categoria: String = "",
    val patente: String = "",
    val numeroTag: String = "",
)

sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data object Success : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

class RegisterViewModel : ViewModel() {

    private val _form = MutableStateFlow(RegisterFormState())
    val form: StateFlow<RegisterFormState> = _form.asStateFlow()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateNombre(v: String) = _form.update { it.copy(nombre = v) }
    fun updateApellidos(v: String) = _form.update { it.copy(apellidos = v) }
    fun updateFechaNacimiento(v: String) = _form.update { it.copy(fechaNacimiento = v) }
    fun updateCiudad(v: String) = _form.update { it.copy(ciudad = v) }
    fun updateComuna(v: String) = _form.update { it.copy(comuna = v) }
    fun updateEmail(v: String) = _form.update { it.copy(email = v) }
    fun updateCelular(v: String) = _form.update { it.copy(celular = v) }
    fun updatePassword(v: String) = _form.update { it.copy(password = v) }
    fun updateRepeatPassword(v: String) = _form.update { it.copy(repeatPassword = v) }
    fun updateTipoVehiculo(v: String) = _form.update { it.copy(tipoVehiculo = v) }
    fun updateCategoria(v: String) = _form.update { it.copy(categoria = v) }
    fun updatePatente(v: String) = _form.update { it.copy(patente = v) }
    fun updateNumeroTag(v: String) = _form.update { it.copy(numeroTag = v) }

    fun resetVehiculo() = _form.update {
        it.copy(tipoVehiculo = "", categoria = "", patente = "", numeroTag = "")
    }

    fun register() {
        val f = _form.value
        if (f.password != f.repeatPassword) {
            _uiState.value = RegisterUiState.Error("Las contraseñas no coinciden")
            return
        }
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            runCatching {
                supabase.auth.signUpWith(Email) {
                    email = f.email
                    password = f.password
                    data = buildJsonObject {
                        put("nombre", f.nombre)
                        put("apellidos", f.apellidos)
                        put("fecha_nacimiento", f.fechaNacimiento)
                        put("ciudad", f.ciudad)
                        put("comuna", f.comuna)
                        put("celular", f.celular)
                        put("tipo_vehiculo", f.tipoVehiculo)
                        put("categoria", f.categoria)
                        put("patente", f.patente)
                        put("numero_tag", f.numeroTag)
                    }
                }
            }.onSuccess {
                _uiState.value = RegisterUiState.Success
            }.onFailure { e ->
                _uiState.value = RegisterUiState.Error(e.message ?: "Error al registrarse")
            }
        }
    }

    fun clearError() { _uiState.value = RegisterUiState.Idle }
}
