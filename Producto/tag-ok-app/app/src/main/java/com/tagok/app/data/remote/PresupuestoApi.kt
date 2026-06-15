package com.tagok.app.data.remote

import com.tagok.app.data.dto.presupuesto.PresupuestoDto
import com.tagok.app.data.dto.presupuesto.NuevoPresupuestoRequest
import com.tagok.app.data.dto.presupuesto.ActualizarPresupuestoRequest
import com.tagok.app.data.remote.interfaces.IPresupuestoApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PresupuestoApi(client: HttpClient) : IPresupuestoApi, ApiClient(client, TAG)
{
    override suspend fun getAll(): List<PresupuestoDto> = apiCall("Obtener presupuestos")
    {
        // .body() usa el ContentNegotiation con ignoreUnknownKeys=true: tolera columnas
        // nuevas en la tabla (p.ej. alertas_activas) sin romper apps con DTO viejo.
        client.get("$BASE_URL/presupuesto")
        {
            contentType(ContentType.Application.Json)
            parameter("select", "*")
            parameter("order", "created_at.asc")
        }.body()
    }

    override suspend fun getByUserAndVehicle(
        userId: String,
        vehiculoId: String?): List<PresupuestoDto> = apiCall("Buscar presupuesto por usuario y vehículo")
    {
        client.get("$BASE_URL/presupuesto")
        {
            contentType(ContentType.Application.Json)
            parameter("select", "*")
            parameter("user_id", "eq.$userId")
            if (vehiculoId != null) {
                parameter("vehiculo_id", "eq.$vehiculoId")
            } else {
                parameter("vehiculo_id", "is.null")
            }
        }.body()
    }

    override suspend fun insert(request: NuevoPresupuestoRequest) = apiCall("Insertar presupuesto")
    {
        client.post("$BASE_URL/presupuesto")
        {
            contentType(ContentType.Application.Json)
            header("Prefer", "return=minimal")
            setBody(request)
        }
    }

    override suspend fun update(id: String, request: ActualizarPresupuestoRequest) = apiCall("Actualizar presupuesto")
    {
        client.patch("$BASE_URL/presupuesto")
        {
            contentType(ContentType.Application.Json)
            parameter("id", "eq.$id")
            header("Prefer", "return=minimal")
            setBody(request)
        }
    }

    override suspend fun delete(id: String) = apiCall("Borrar presupuesto: $id")
    {
        client.delete("$BASE_URL/presupuesto")
        {
            contentType(ContentType.Application.Json)
            parameter("id", "eq.$id")
        }
    }

    companion object
    {
        private var BASE_URL = ApiConfig.PRESUPUESTO_API
        private const val TAG = "PresupuestoApi"
    }
}