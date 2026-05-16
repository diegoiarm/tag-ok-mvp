package com.tagok.app.domain.interfaces

import com.tagok.app.data.dto.PorticoResumen

interface IPorticoRepository
{
    suspend fun getPorticos(): List<PorticoResumen>
}