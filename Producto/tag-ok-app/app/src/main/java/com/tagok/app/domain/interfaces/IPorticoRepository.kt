package com.tagok.app.domain.interfaces

import com.tagok.app.domain.model.portico.PorticoResumen

interface IPorticoRepository
{
    suspend fun getPorticos(): List<PorticoResumen>
}