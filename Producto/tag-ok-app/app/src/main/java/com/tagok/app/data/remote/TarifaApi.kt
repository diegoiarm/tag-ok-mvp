package com.tagok.app.data.remote

import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.dto.tarifa.TarifaCalculadaResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TarifaApi(client: HttpClient) : ApiClient(client, TAG)
{
    suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculadaResponse = apiCall("Obtener tarifa")
    {
        client.post("${BASE_URL}/v1/tarifas")
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    companion object
    {
        private var BASE_URL = ApiConfig.TARIFA_API
        private const val TAG = "TarifaApi"
    }
}