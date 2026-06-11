// data/auth/AuthTokenProvider.kt
package com.tagok.app.data.auth

import android.util.Log
import com.tagok.app.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object AuthTokenProvider
{
    private const val TAG = "AuthTokenProvider"

    suspend fun getAccessToken(): String?
    {
        return try
        {
            val token = supabase.auth.currentAccessTokenOrNull()
            token
        }
        catch (e: Exception)
        {
            Log.e("AuthTokenProvider", "Error getting access token", e)
            null
        }
    }

    suspend fun getUserId(): String?
    {
        return try
        {
            supabase.auth.currentUserOrNull()?.id
        }
        catch (e: Exception)
        {
            Log.e(TAG, "Error getting user id", e)
            null
        }
    }

    fun hasSession(): Boolean
    {
        return supabase.auth.currentSessionOrNull() != null
    }

    /**
     * Estado de sesión para guiar la navegación:
     *   true  -> autenticado, false -> sin sesión, null -> aún sin decidir.
     *
     * Tras una muerte de proceso (p.ej. al abrir el selector de archivos), Supabase
     * restaura la sesión de disco de forma asíncrona y emite `Initializing` primero.
     * Mapear ese estado transitorio a `false` provocaba un rebote a login/Home y la
     * pérdida de la pantalla actual; por eso lo dejamos en `null` (no navegar).
     */
    val sessionFlow: Flow<Boolean?> = supabase.auth.sessionStatus.map { status ->
        when (status)
        {
            is SessionStatus.Authenticated    -> true
            is SessionStatus.NotAuthenticated -> false
            else                              -> null   // Initializing / RefreshFailure
        }
    }
}