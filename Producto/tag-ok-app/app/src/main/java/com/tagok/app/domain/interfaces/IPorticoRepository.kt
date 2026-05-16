package com.tagok.app.domain.interfaces

import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.portico.TollType

interface IPorticoRepository
{
    suspend fun getPorticos(): List<PorticoResumen>
    suspend fun getPorticoById(id: Long): TollType
}