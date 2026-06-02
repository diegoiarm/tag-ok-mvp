package com.tagok.app.data.remote

import com.tagok.app.data.dto.portico.PorticoResumenResponse
import com.tagok.app.data.dto.portico.TollResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PorticoApi(private val client: HttpClient)
{
    suspend fun getPorticos(): List<PorticoResumenResponse> =
        client.get("${BASE_URL}/v1/porticos").body()

    suspend fun getPorticoDetails(id: Long): TollResponse =
        client.get("${BASE_URL}/v1/porticos/${id}").body()

    companion object
    {
        private var BASE_URL = ApiConfig.ROUTES_API
    }
}