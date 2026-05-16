package com.tagok.app.data.repository

import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.domain.interfaces.IPorticoRepository
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.portico.TollType

class PorticoRepository(private val api: PorticoApi) : IPorticoRepository
{
    override suspend fun getPorticos(): List<PorticoResumen>
    {
        return api.getPorticos().map { it.toDomain() }
    }

    override suspend fun getPorticoById(id: Long): TollType
    {
        return api.getPorticoDetails(id).toDomain()
    }
}