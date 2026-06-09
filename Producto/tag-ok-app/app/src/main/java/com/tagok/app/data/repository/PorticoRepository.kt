package com.tagok.app.data.repository

import com.tagok.app.data.dto.portico.PorticoResponse
import com.tagok.app.data.dto.portico.PorticoTramoResponse
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.data.remote.interfaces.IPorticoApi
import com.tagok.app.domain.interfaces.IPorticoRepository
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.portico.PorticoTipo
import com.tagok.app.domain.model.portico.TollType

class PorticoRepository(private val api: IPorticoApi) : IPorticoRepository
{
    override suspend fun getPorticos(): List<PorticoResumen>
    {
        return api.getPorticos().map { it.toDomain() }
    }

    override suspend fun getPorticoById(id: Long): TollType
    {
        return api.getPorticoDetails(id).toDomain()
    }

    override suspend fun getPorticoTipo(id: Long): PorticoTipo
    {
        val response = api.getPorticoDetails(id)
        return when (response)
        {
            is PorticoResponse -> PorticoTipo.PORTICO
            is PorticoTramoResponse -> PorticoTipo.TRAMO
        }
    }

    override suspend fun getSalidasTramo(id: Long): PorticoTramoResponse
    {
        val response = api.getPorticoDetails(id)
        return response as PorticoTramoResponse
    }
}