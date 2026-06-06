package com.tagok.app.data.repository

import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.BoletaApi
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.domain.model.boleta.Boleta

class BoletaRepository(private val boletaApi: BoletaApi)
{
    suspend fun generarBoleta(request: BoletaRequest): Boleta
    {
        return boletaApi.generarBoleta(request).toDomain()
    }
}