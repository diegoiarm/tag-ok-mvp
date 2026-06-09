package com.tagok.app.data.repository

import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.mapper.toRequest
import com.tagok.app.data.remote.VehiculoApi
import com.tagok.app.data.remote.interfaces.IVehiculoApi
import com.tagok.app.domain.interfaces.IVehiculoRepository
import com.tagok.app.domain.model.vehiculo.NuevoVehiculo
import com.tagok.app.domain.model.vehiculo.Vehiculo

class VehiculoRepository(private val vehiculoApi: IVehiculoApi) : IVehiculoRepository
{
    override suspend fun getVehiculos(): List<Vehiculo> =
        vehiculoApi.getVehiculos().map { it.toDomain() }

    override suspend fun insertVehiculo(nuevo: NuevoVehiculo) =
        vehiculoApi.insertVehiculo(nuevo.toRequest())

    override suspend fun deleteVehiculo(id: String) =
        vehiculoApi.deleteVehiculo(id)

    companion object
    {
        private const val TAG = "VehiculoRepository"
    }
}