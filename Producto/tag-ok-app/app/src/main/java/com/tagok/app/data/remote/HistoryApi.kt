package com.tagok.app.data.remote

import com.tagok.app.data.dto.history.DetalleDiaDTO
import com.tagok.app.data.dto.history.DetalleMensualDTO
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.data.dto.history.ResumenAnualDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HistoryApi(client: HttpClient) : ApiClient(client, TAG)
{
    suspend fun getAvailableYears(): List<Int> = apiCall("Obtener años disponibles")
    {
        client.get("$BASE_URL/v1/historial/years").body()
    }

    suspend fun getResumenAnual(): List<ResumenAnualDTO> = apiCall("Obtener resumen anual")
    {
        client.get("$BASE_URL/v1/historial/resumen").body()
    }

    suspend fun getDetalleAnual(año: Int): ResumenAnualDTO = apiCall("Obtener detalle anual $año")
    {
        client.get("$BASE_URL/v1/historial/year/$año").body()
    }

    suspend fun getDetalleMensual(año: Int, mes: Int): DetalleMensualDTO = apiCall("Obtener detalle mensual $mes/$año")
    {
        client.get("$BASE_URL/v1/historial/year/$año/month/$mes").body()
    }

    suspend fun getDetalleDia(año: Int, mes: Int, dia: Int): DetalleDiaDTO = apiCall("Obtener detalle día $dia/$mes/$año")
    {
        client.get("$BASE_URL/v1/historial/year/$año/month/$mes/day/$dia").body()
    }

    suspend fun getPatentes(): List<String> = apiCall("Obtener patentes")
    {
        client.get("$BASE_URL/v1/historial/patentes").body()
    }

    suspend fun getAutopistas(): List<String> = apiCall("Obtener autopistas")
    {
        client.get("$BASE_URL/v1/historial/autopistas").body()
    }

    suspend fun getResumenAnualFiltrado(filtro: FiltroHistorialRequest): List<ResumenAnualDTO> = apiCall("Filtrar resumen anual") {
        client.post("$BASE_URL/v1/historial/resumen-filtrado")
        {
            contentType(ContentType.Application.Json)
            setBody(filtro)
        }.body()
    }

    companion object
    {
        private var BASE_URL = ApiConfig.HISTORY_API
        private const val TAG = "HistoryApi"
    }
}