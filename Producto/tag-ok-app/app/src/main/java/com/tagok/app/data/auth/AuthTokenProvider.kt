// data/auth/AuthTokenProvider.kt
package com.tagok.app.data.auth

import android.util.Log
import com.tagok.app.supabase
import io.github.jan.supabase.auth.auth
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
            Log.d("AuthTokenProvider", "Token completo: $token")
            Log.d("AuthTokenProvider", "Largo del token: ${token?.length}")
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

    fun hasSession(): Boolean {
        return supabase.auth.currentSessionOrNull() != null
    }

    val sessionFlow: Flow<Boolean> = supabase.auth.sessionStatus
        .map { supabase.auth.currentSessionOrNull() != null }
}