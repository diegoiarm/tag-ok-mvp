package com.tagok.app.data.remote

import android.util.Log
import com.tagok.app.data.remote.exceptions.ApiErrorType
import com.tagok.app.data.remote.exceptions.ApiException
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
            Log.w(tag, "$action cancelado", e)
            throw e
        }
        catch (e: ClientRequestException)
        {
            Log.e(tag, "$action: Error ${e.response.status}", e)

            throw ApiException(
                message = "Error del cliente",
                statusCode = e.response.status.value,
                type = ApiErrorType.CLIENT,
                cause = e)
        }
        catch (e: ServerResponseException)
        {
            Log.e(tag, "$action: Error ${e.response.status}", e)

            throw ApiException(
                message = "Error del servidor",
                statusCode = e.response.status.value,
                type = ApiErrorType.SERVER,
                cause = e)
        }
        catch (e: ConnectException)
        {
            Log.e(tag, "$action: Sin conexión", e)

            throw ApiException(
                message = "Sin conexión",
                type = ApiErrorType.NETWORK,
                cause = e)
        }
        catch (e: SocketTimeoutException)
        {
            Log.e(tag, "$action: Timeout", e)

            throw ApiException(
                message = "Timeout",
                type = ApiErrorType.TIMEOUT,
                cause = e)
        }
        catch (e: IOException)
        {
            Log.e(tag, "$action: Error de red", e)

            throw ApiException(
                message = "Error de red",
                type = ApiErrorType.NETWORK,
                cause = e)
        }
        catch (e: Exception)
        {
            Log.e(tag, "$action: Error inesperado", e)

            throw ApiException(
                message = "Error inesperado",
                type = ApiErrorType.UNKNOWN,
                cause = e)
        }
    }
}