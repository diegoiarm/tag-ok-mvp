package com.tagok.app.data.remote

import com.tagok.app.data.dto.boleta.BoletaDto
import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.remote.interfaces.IBoletaApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

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

    companion object
    {
        private var BASE_URL = ApiConfig.BOLETA_API
        private const val TAG = "BoletaApi"
    }
}