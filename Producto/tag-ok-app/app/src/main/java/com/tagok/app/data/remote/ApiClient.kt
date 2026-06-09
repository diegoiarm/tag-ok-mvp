package com.tagok.app.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

abstract class ApiClient(
    protected val client: HttpClient,
    private val tag: String)
{

    protected suspend fun <T> apiCall(
        action: String,
        call: suspend () -> T): T
    {
        return try
        {
            Log.d(tag, "$action...")
            val result = call()
            Log.d(tag, "$action completado")
            result
        }
        catch (e: CancellationException)
        {
            Log.w(tag, "$action cancelado")
            throw e
        }
        catch (e: ClientRequestException)
        {
            Log.e(tag, "$action: Error ${e.response.status} - ${e.message}")
            throw ApiException("Error en $action: ${e.message}", e.response.status.value)
        }
        catch (e: ServerResponseException)
        {
            Log.e(tag, "$action: Error ${e.response.status} - ${e.message}")
            throw ApiException("Error en $action: ${e.message}", e.response.status.value)
        }
        catch (e: ConnectException)
        {
            Log.e(tag, "$action: Sin conexión")
            throw ApiException("No se pudo conectar al servidor", 0)
        }
        catch (e: SocketTimeoutException)
        {
            Log.e(tag, "$action: Timeout")
            throw ApiException("Tiempo de espera agotado", 0)
        }
        catch (e: IOException)
        {
            Log.e(tag, "$action: Error de red - ${e.message}")
            throw ApiException("Error de red", 0)
        }
        catch (e: Exception)
        {
            Log.e(tag, "$action: Error inesperado", e)
            throw ApiException("Error inesperado: ${e.message}", 0)
        }
    }
}

class ApiException(
    message: String,
    val statusCode: Int = 0) : Exception(message)