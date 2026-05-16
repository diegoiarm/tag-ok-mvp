package com.tagok.app.data.mapper

import com.tagok.app.data.dto.portico.PorticoResumenResponse
import com.tagok.app.data.dto.route.CobroPorticoResponse
import com.tagok.app.data.dto.route.CobroRutaResponse
import com.tagok.app.data.dto.route.CobroTramoResponse
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Toll
import com.tagok.app.domain.model.routes.Tramo

fun CobroRutaResponse.toDomain(): Toll
{
    return when (this)
    {
        is CobroPorticoResponse -> {
            val id = requireNotNull(porticoId) { "porticoId no puede ser nulo para un Portico" }

            Portico(
                porticoId = id,
                nombre = nombre ?: "No asignado",
                codigo = codigo ?: "No asignado",
                autopista = autopista,
                latitud = latitud,
                longitud = longitud,
                tarifa = tarifa,
                valor = valor,
                fechaHora = fechaHora
            )
        }

        is CobroTramoResponse -> {
            val entradaId = requireNotNull(entradaId) { "El id de la entrada no puede ser nulo" }
            val salidaId = requireNotNull(salidaId) { "El id de la salida no puede ser nulo" }

            Tramo(
                entradaId,
                salidaId,
                nombreEntrada,
                nombreSalida,
                autopista,
                latitudEntrada,
                longitudEntrada,
                latitudSalida,
                longitudSalida,
                tarifa,
                valor,
                fechaHora
            )
        }
    }
}

fun PorticoResumenResponse.toDomain(): PorticoResumen
{
    return PorticoResumen(
        id,
        latitud,
        longitud
    )
}