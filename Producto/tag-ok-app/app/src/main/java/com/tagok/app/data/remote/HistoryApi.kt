package com.tagok.app.data.remote

import android.util.Log
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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class HistoryApi(private val client: HttpClient) {
    suspend fun getAvailableYears(): List<Int> =
        client.get("${BASE_URL}/v1/historial/years").body()

    suspend fun getResumenAnual(): List<ResumenAnualDTO> =
        client.get("${BASE_URL}/v1/historial/resumen").body()

    suspend fun getDetalleAnual(año: Int): ResumenAnualDTO =
        client.get("${BASE_URL}/v1/historial/year/${año}").body()

    suspend fun getDetalleMensual(año: Int, mes: Int): DetalleMensualDTO =
        client.get("${BASE_URL}/v1/historial/year/${año}/month/${mes}").body()

    suspend fun getDetalleDia(año: Int, mes: Int, dia: Int): DetalleDiaDTO =
        client.get("${BASE_URL}/v1/historial/year/${año}/month/${mes}/day/${dia}")
            .body()

    suspend fun getPatentes(): List<String> {
        val response = client.get("$BASE_URL/v1/historial/patentes")
        if (response.status.isSuccess())
        {
            return response.body()
        }
        else
        {
            Log.e(TAG, "Error ${response.status}: ${response.bodyAsText()}")
            throw Exception("Error ${response.status}")
        }
    }

    suspend fun getAutopistas(): List<String> =
        client.get("$BASE_URL/v1/historial/autopistas")
        {
        }.body()

    suspend fun getResumenAnualFiltrado(filtro: FiltroHistorialRequest): List<ResumenAnualDTO>
    {
        return client.post("${BASE_URL}/v1/historial/resumen-filtrado") {
            contentType(ContentType.Application.Json)
            setBody(filtro)
        }.body()
    }

    companion object
    {
        private var BASE_URL = ApiConfig.HISTORY_API
        private var TAG: String = "HistoryApi"
    }
}