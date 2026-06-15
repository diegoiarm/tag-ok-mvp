package com.tagok.app.data

import com.tagok.app.data.dto.presupuesto.ActualizarPresupuestoRequest
import com.tagok.app.data.dto.presupuesto.NuevoPresupuestoRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.interfaces.IPresupuestoApi
import com.tagok.app.domain.interfaces.IPresupuestoRepository
import com.tagok.app.domain.model.presupuesto.Presupuesto


class PresupuestoRepository(private val api: IPresupuestoApi) : IPresupuestoRepository
{
    override suspend fun getAll(): List<Presupuesto>
    {
        return api.getAll().map { it.toDomain() }
    }

    override suspend fun save(nuevo: NuevoPresupuesto)
    {
        val existing = api.getByUserAndVehicle(
            userId = nuevo.userId,
            vehiculoId = nuevo.vehiculoId
        ).map { it.toDomain() }.firstOrNull()

        if (existing != null)
        {
            api.update(
                id = existing.id,
                request = ActualizarPresupuestoRequest(
                    montoMensual   = nuevo.montoMensual,
                    umbralAlerta1  = nuevo.umbralAlerta1,
                    umbralAlerta2  = nuevo.umbralAlerta2,
                    alertasActivas = nuevo.alertasActivas,))
        }
        else
        {
            api.insert(
                NuevoPresupuestoRequest(
                    userId         = nuevo.userId,
                    vehiculoId     = nuevo.vehiculoId,
                    montoMensual   = nuevo.montoMensual,
                    umbralAlerta1  = nuevo.umbralAlerta1,
                    umbralAlerta2  = nuevo.umbralAlerta2,
                    alertasActivas = nuevo.alertasActivas,))
        }
    }

    override suspend fun delete(id: String)
    {
        api.delete(id)
    }
}