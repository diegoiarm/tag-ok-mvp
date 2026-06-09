package com.tagok.app.domain.services

import com.tagok.app.domain.interfaces.IPorticoRepository
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.services.interfaces.IPorticoService

class PorticoService(
    private val porticoRepository: IPorticoRepository) : IPorticoService, ApplicationService()
{
    override suspend fun obtenerPorticos(): List<PorticoResumen> = execute("Obtener pórticos")
    {
        porticoRepository.getPorticos()
    }
}