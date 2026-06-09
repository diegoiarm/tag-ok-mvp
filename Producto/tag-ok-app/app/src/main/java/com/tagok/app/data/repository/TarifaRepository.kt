package com.tagok.app.data.repository

import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.TarifaApi
import com.tagok.app.data.remote.interfaces.ITarifaApi
import com.tagok.app.domain.interfaces.ITarifaRepository
import com.tagok.app.domain.model.tarifa.TarifaCalculada

class TarifaRepository(private val api: ITarifaApi) : ITarifaRepository
{
    override suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculada
    {
        return api.calculateTarifa(request).toDomain()
    }
}