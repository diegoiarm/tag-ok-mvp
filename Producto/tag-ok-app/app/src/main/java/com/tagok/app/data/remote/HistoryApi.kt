package com.tagok.app.data.remote

import com.tagok.app.data.dto.history.DetalleDiaDTO
import com.tagok.app.data.dto.history.DetalleMensualDTO
import com.tagok.app.data.dto.history.ResumenAnualDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class HistoryApi(private val client: HttpClient)
{
    suspend fun getAvailableYears(usuarioId: String): List<Int> =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/years").body()

    suspend fun getResumenAnual(usuarioId: String): List<ResumenAnualDTO> =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/resumen").body()

    suspend fun getDetalleAnual(usuarioId: String, año: Int): ResumenAnualDTO =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/year/${año}").body()

    suspend fun getDetalleMensual(usuarioId: String, año: Int, mes: Int): DetalleMensualDTO =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/year/${año}/month/${mes}").body()

    suspend fun getDetalleDia(usuarioId: String, año: Int, mes: Int, dia: Int): DetalleDiaDTO =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/year/${año}/month/${mes}/day/${dia}").body()

    companion object
    {
        private var BASE_URL = ApiConfig.HISTORY_API
    }
}