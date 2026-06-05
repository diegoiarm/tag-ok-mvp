package com.tagok.app.data.remote

import com.tagok.app.data.dto.history.DetalleDiaDTO
import com.tagok.app.data.dto.history.DetalleMensualDTO
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.data.dto.history.ResumenAnualDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HistoryApi(private val client: HttpClient) {
    suspend fun getAvailableYears(usuarioId: String): List<Int> =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/years").body()

    suspend fun getResumenAnual(usuarioId: String): List<ResumenAnualDTO> =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/resumen").body()

    suspend fun getDetalleAnual(usuarioId: String, año: Int): ResumenAnualDTO =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/year/${año}").body()

    suspend fun getDetalleMensual(usuarioId: String, año: Int, mes: Int): DetalleMensualDTO =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/year/${año}/month/${mes}").body()

    suspend fun getDetalleDia(usuarioId: String, año: Int, mes: Int, dia: Int): DetalleDiaDTO =
        client.get("${BASE_URL}/v1/historial/${usuarioId}/year/${año}/month/${mes}/day/${dia}")
            .body()

    suspend fun getPatentes(usuarioId: String): List<String> =
        client.get("$BASE_URL/v1/historial/patentes")
        {
            parameter("usuarioId", usuarioId)
        }.body()

    suspend fun getAutopistas(usuarioId: String): List<String> =
        client.get("$BASE_URL/v1/historial/autopistas")
        {
            parameter("usuarioId", usuarioId)
        }.body()

    suspend fun getResumenAnualFiltrado(
        usuarioId: String,
        filtro: FiltroHistorialRequest): List<ResumenAnualDTO>
    {
        return client.post("${BASE_URL}/v1/historial/${usuarioId}/resumen-filtrado") {
            contentType(ContentType.Application.Json)
            setBody(filtro)
        }.body()
    }

    companion object
    {
        private var BASE_URL = ApiConfig.HISTORY_API
    }
}