package com.tagok.app.data.remote

import com.tagok.app.data.dto.boleta.BoletaDto
import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.dto.boleta.ComparacionFacturaDto
import com.tagok.app.data.remote.exceptions.ApiErrorType
import com.tagok.app.data.remote.exceptions.ApiException
import com.tagok.app.data.remote.interfaces.IBoletaApi
import com.tagok.app.domain.model.boleta.ArchivoFactura
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BoletaApi(client: HttpClient) : IBoletaApi, ApiClient(client, TAG)
{
    override suspend fun generarBoleta(request: BoletaRequest): BoletaDto = apiCall("Generar boleta")
    {
        client.post("$BASE_URL/v1/boleta/obtener")
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun compararFactura(
        request: BoletaRequest,
        archivo: ArchivoFactura): ComparacionFacturaDto = apiCall("Comparar factura")
    {
        val response = client.post("$BASE_URL/v1/boleta/comparar")
        {
            // La extracción con IA puede tardar bastante más que el resto de la API
            timeout {
                requestTimeoutMillis = TIMEOUT_COMPARACION_MS
                socketTimeoutMillis = TIMEOUT_COMPARACION_MS
            }
            setBody(MultiPartFormDataContent(formData {
                append("archivo", archivo.bytes, Headers.build {
                    append(HttpHeaders.ContentType, archivo.mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"${archivo.nombre}\"")
                })
                append("patente", request.patente)
                append("fechaDesde", request.fechaDesde.toString())
                append("fechaHasta", request.fechaHasta.toString())
                request.autopistas.forEach { append("autopistas", it) }
            }))
        }

        if (!response.status.isSuccess())
        {
            // El backend responde {"error": "..."} con mensajes accionables
            // (archivo inválido, IA no configurada, cuota de Gemini agotada)
            val status = response.status.value
            throw ApiException(
                message = extraerMensajeError(response) ?: "Error del servidor ($status)",
                statusCode = status,
                type = if (status >= 500) ApiErrorType.SERVER else ApiErrorType.CLIENT)
        }

        response.body()
    }

    private suspend fun extraerMensajeError(response: HttpResponse): String?
    {
        return try
        {
            val cuerpo = response.bodyAsText()
            Json.parseToJsonElement(cuerpo).jsonObject["error"]?.jsonPrimitive?.content
        }
        catch (_: Exception)
        {
            null
        }
    }

    companion object
    {
        private var BASE_URL = ApiConfig.BOLETA_API
        private const val TAG = "BoletaApi"
        private const val TIMEOUT_COMPARACION_MS = 120_000L
    }
}
