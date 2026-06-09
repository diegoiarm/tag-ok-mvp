package com.tagok.app.data.mapper

import com.tagok.app.data.dto.tarifa.CrucePorticoResponse
import com.tagok.app.data.dto.tarifa.CruceResponse
import com.tagok.app.data.dto.tarifa.CruceTramoResponse
import com.tagok.app.data.dto.tarifa.TarifaCalculadaResponse
import com.tagok.app.domain.model.tarifa.Cruce
import com.tagok.app.domain.model.tarifa.CrucePortico
import com.tagok.app.domain.model.tarifa.CruceTramo
import com.tagok.app.domain.model.tarifa.TarifaCalculada

fun TarifaCalculadaResponse.toDomain(): TarifaCalculada
{
    return TarifaCalculada(
        total = total,
        cruces = cruces.map { it.toDomain() },
        vehiculo = vehiculo)
}

fun CruceResponse.toDomain(): Cruce
{
    return when (this)
    {
        is CrucePorticoResponse -> CrucePortico(
            porticoId = porticoId,
            codigo = codigo,
            nombre = nombre,
            autopista = autopista,
            latitud = latitud,
            longitud = longitud,
            tipoTarifa = tipoTarifa,
            valor = valor,
            horaFechaCruce = horaFechaCruce)
        is CruceTramoResponse -> CruceTramo(
            codigo = codigo,
            nombre = nombre,
            autopista = autopista,
            tipoTarifa = tipoTarifa,
            valor = valor,
            horaFechaCruce = horaFechaCruce,
            codigoEntrada = codigoEntrada,
            codigoSalida = codigoSalida,
            entradaId = entradaId,
            salidaId = salidaId,
            nombreEntrada = nombreEntrada,
            nombreSalida = nombreSalida,
            latitudEntrada = latitudEntrada,
            longitudEntrada = longitudEntrada,
            latitudSalida = latitudSalida,
            longitudSalida = longitudSalida)
    }
}