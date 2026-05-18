package com.tagok.app.data.remote

import com.tagok.app.data.dto.portico.PorticoResumenResponse
import com.tagok.app.data.dto.portico.TollResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PorticoApi(private val client: HttpClient)
{
    suspend fun getPorticos(): List<PorticoResumenResponse> =
        client.get("${BASE_URL}/porticos").body()

    suspend fun getPorticoDetails(id: Long): TollResponse =
        client.get("${BASE_URL}/porticos/${id}").body()

    companion object
    {
        private const val BASE_URL = "http://10.0.2.2:8000"
    }
}