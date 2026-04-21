package com.tagok.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            runCatching {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            }.onSuccess {
                _uiState.value = LoginUiState.Success
            }.onFailure { e ->
                _uiState.value = LoginUiState.Error(
                    e.message ?: "Error al iniciar sesión. Revisa tus credenciales."
                )
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            runCatching {
                // Abre Chrome Custom Tab → redirige a tagok://auth-callback
                supabase.auth.signInWith(Google)
            }.onFailure { e ->
                _uiState.value = LoginUiState.Error(
                    e.message ?: "Error al iniciar sesión con Google."
                )
            }
        }
    }

    fun onDeepLinkAuthSuccess() {
        _uiState.value = LoginUiState.Success
    }

    fun clearError() {
        _uiState.value = LoginUiState.Idle
    }
}
