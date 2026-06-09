package com.tagok.app.domain.services.interfaces

import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.domain.model.boleta.Boleta

interface IBoletaService
{
    suspend fun generarBoleta(request: BoletaRequest): Boleta
}