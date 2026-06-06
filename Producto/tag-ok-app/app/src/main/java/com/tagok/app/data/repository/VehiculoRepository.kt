package com.tagok.app.data.repository

import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.mapper.toRequest
import com.tagok.app.data.remote.VehiculoApi
import com.tagok.app.domain.model.vehiculo.NuevoVehiculo
import com.tagok.app.domain.model.vehiculo.Vehiculo

class VehiculoRepository(private val vehiculoApi: VehiculoApi)
{
    suspend fun getVehiculos(): List<Vehiculo> =
        vehiculoApi.getVehiculos().map { it.toDomain() }

    suspend fun insertVehiculo(nuevo: NuevoVehiculo) =
        vehiculoApi.insertVehiculo(nuevo.toRequest())

    suspend fun deleteVehiculo(id: String) =
        vehiculoApi.deleteVehiculo(id)

    companion object
    {
        private const val TAG = "VehiculoRepository"
    }
}