package com.tagok.app.data.mapper

import com.tagok.app.data.dto.portico.CalendarioTarifarioResponse
import com.tagok.app.data.dto.portico.PorticoResponse
import com.tagok.app.data.dto.portico.PorticoResumenResponse
import com.tagok.app.data.dto.portico.PorticoTramoResponse
import com.tagok.app.data.dto.portico.RangoHorarioResponse
import com.tagok.app.data.dto.portico.ReglaTarifariaResponse
import com.tagok.app.data.dto.portico.ReglaTemporalResponse
import com.tagok.app.data.dto.portico.TollResponse
import com.tagok.app.data.dto.portico.TramoResponse
import com.tagok.app.data.dto.portico.ValorTarifaResponse
import com.tagok.app.data.dto.route.CobroPorticoResponse
import com.tagok.app.data.dto.route.CobroRutaResponse
import com.tagok.app.data.dto.route.CobroTramoResponse
import com.tagok.app.domain.model.portico.CalendarioTarifario
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.portico.PorticoTramoType
import com.tagok.app.domain.model.portico.PorticoType
import com.tagok.app.domain.model.portico.RangoHorario
import com.tagok.app.domain.model.portico.ReglaTarifaria
import com.tagok.app.domain.model.portico.ReglaTemporal
import com.tagok.app.domain.model.portico.TollType
import com.tagok.app.domain.model.portico.TramoPortico
import com.tagok.app.domain.model.portico.ValorTarifa
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

fun TollResponse.toDomain(): TollType
{
    return when (this)
    {
        is PorticoResponse -> {
            PorticoType(
                id,
                codigo,
                nombre,
                sentido,
                latitud,
                longitud,
                autopista,
                reglas.map { it.toDomain() },
                calendario.toDomain()
            )
        }

        is PorticoTramoResponse -> {
            PorticoTramoType(
                id,
                codigo,
                nombre,
                latitud,
                longitud,
                autopista,
                tramos.map { it.toDomain() }
            )
        }
    }
}

fun TramoResponse.toDomain(): TramoPortico
{
    return TramoPortico(
        entrada,
        salida,
        reglas.map { it.toDomain() },
        calendario.toDomain()
    )
}

fun ReglaTarifariaResponse.toDomain(): ReglaTarifaria
{
    return ReglaTarifaria(
        aplicaA,
        valores.map { it.toDomain() }
    )
}

fun ValorTarifaResponse.toDomain(): ValorTarifa
{
    return ValorTarifa(
        tipoTarifa,
        valor
    )
}

fun CalendarioTarifarioResponse.toDomain(): CalendarioTarifario
{
    return CalendarioTarifario(
        reglas.map { it.toDomain() }
    )
}

fun ReglaTemporalResponse.toDomain(): ReglaTemporal
{
    return ReglaTemporal(
        tipoTarifa,
        tipoDia,
        tramos.map { it.toDomain() }
    )
}

fun RangoHorarioResponse.toDomain(): RangoHorario
{
    return RangoHorario(
        horaInicio,
        horaFin
    )
}