package com.tagok.app.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

data class PerfilState(
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val celular: String = "",
    val ciudad: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMsg: String? = null,
)

class PerfilViewModel : ViewModel() {

    private val _state = MutableStateFlow(PerfilState())
    val state: StateFlow<PerfilState> = _state.asStateFlow()

    init { cargar() }

    private fun cargar() {
        val user = supabase.auth.currentUserOrNull() ?: return
        val meta = user.userMetadata
        _state.update {
            it.copy(
                nombre   = meta?.get("nombre")?.jsonPrimitive?.contentOrNull ?: "",
                apellidos = meta?.get("apellidos")?.jsonPrimitive?.contentOrNull ?: "",
                email    = user.email ?: "",
                celular  = meta?.get("celular")?.jsonPrimitive?.contentOrNull ?: "",
                ciudad   = meta?.get("ciudad")?.jsonPrimitive?.contentOrNull ?: "",
            )
        }
    }

    fun startEditing() = _state.update { it.copy(isEditing = true) }

    fun cancelEditing() {
        cargar()
        _state.update { it.copy(isEditing = false, errorMsg = null) }
    }

    fun updateNombre(v: String)    = _state.update { it.copy(nombre = v) }
    fun updateApellidos(v: String) = _state.update { it.copy(apellidos = v) }
    fun updateCelular(v: String)   = _state.update { it.copy(celular = v) }
    fun updateCiudad(v: String)    = _state.update { it.copy(ciudad = v) }

    fun guardar() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMsg = null) }
            runCatching {
                val s = _state.value
                supabase.auth.updateUser {
                    data = buildJsonObject {
                        put("nombre",    s.nombre)
                        put("apellidos", s.apellidos)
                        put("celular",   s.celular)
                        put("ciudad",    s.ciudad)
                    }
                }
            }.onSuccess {
                _state.update { it.copy(isSaving = false, isEditing = false) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, errorMsg = e.message ?: "Error al guardar") }
            }
        }
    }

    fun clearError() = _state.update { it.copy(errorMsg = null) }
}
