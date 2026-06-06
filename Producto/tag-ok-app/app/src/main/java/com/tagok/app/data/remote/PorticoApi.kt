package com.tagok.app.data.remote

import com.tagok.app.data.dto.portico.PorticoResumenResponse
import com.tagok.app.data.dto.portico.TollResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PorticoApi(client: HttpClient) : ApiClient(client, TAG)
{
    suspend fun getPorticos(): List<PorticoResumenResponse> = apiCall("obtener Porticos")
    {
        client.get("${BASE_URL}/v1/porticos").body()
    }

    suspend fun getPorticoDetails(id: Long): TollResponse = apiCall("Obtener detalles portico ${id}")
    {
        client.get("${BASE_URL}/v1/porticos/${id}").body()
    }

    companion object
    {
        private var BASE_URL = ApiConfig.ROUTES_API
        private const val TAG = "PorticoApi"
    }
}