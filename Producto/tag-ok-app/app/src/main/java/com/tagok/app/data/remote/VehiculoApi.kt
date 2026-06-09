package com.tagok.app.data.remote

import com.tagok.app.data.dto.vehiculo.NuevoVehiculoRequest
import com.tagok.app.data.dto.vehiculo.VehiculoDto
import com.tagok.app.data.remote.interfaces.IVehiculoApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class VehiculoApi(client: HttpClient) : IVehiculoApi, ApiClient(client, TAG)
{

    override suspend fun getVehiculos(): List<VehiculoDto> = apiCall("Obtener vehiculos")
    {
        client.get("$BASE_URL/rest/v1/vehiculos")
        {
            contentType(ContentType.Application.Json)
            header("apikey", SUPABASE_KEY)
            parameter("select", "user_id,patente,tipo_vehiculo,numero_tag,alias,id,created_at")
            parameter("order", "created_at.asc")
        }.body()
    }

    override suspend fun insertVehiculo(request: NuevoVehiculoRequest) = apiCall("Insertar Vehiculo")
    {
        client.post("$BASE_URL/rest/v1/vehiculos")
        {
            contentType(ContentType.Application.Json)
            header("apikey", SUPABASE_KEY)
            header("Prefer", "return=minimal")
            setBody(request)
        }
    }

    override suspend fun deleteVehiculo(id: String) = apiCall("Borrar vehiculo: ${id}")
    {
        client.delete("$BASE_URL/rest/v1/vehiculos")
        {
            contentType(ContentType.Application.Json)
            header("apikey", SUPABASE_KEY)
            parameter("id", "eq.$id")
        }
    }

    companion object
    {
        private var BASE_URL = ApiConfig.VEHICULOS_API
        private var SUPABASE_KEY = ApiConfig.VEHICULOS_API_KEY
        private const val TAG = "VehiculoApi"
    }
}