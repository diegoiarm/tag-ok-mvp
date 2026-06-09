package com.tagok.app.data.repository

import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.BoletaApi
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.data.remote.interfaces.IBoletaApi
import com.tagok.app.domain.interfaces.IBoletaRepository
import com.tagok.app.domain.model.boleta.Boleta

class BoletaRepository(private val boletaApi: IBoletaApi) : IBoletaRepository
{
    override suspend fun generarBoleta(request: BoletaRequest): Boleta
    {
        return boletaApi.generarBoleta(request).toDomain()
    }
}