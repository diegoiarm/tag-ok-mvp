package com.tagok.app.data.repository

import com.tagok.app.data.dto.PorticoResumen
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.domain.interfaces.IPorticoRepository

class PorticoRepository(private val api: PorticoApi) : IPorticoRepository
{
    override suspend fun getPorticos(): List<PorticoResumen>
    {
        return api.getPorticos()
    }
}