package com.tagok.app.data.mapper

import com.tagok.app.data.dto.history.CruceDetalleDTO
import com.tagok.app.data.dto.history.DetalleDiaDTO
import com.tagok.app.data.dto.history.DetalleMensualDTO
import com.tagok.app.data.dto.history.DiaResumenDTO
import com.tagok.app.data.dto.history.ResumenAnualDTO
import com.tagok.app.domain.model.history.CruceDetalle
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.DiaResumen
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.domain.vehiculo.TipoVehiculo

fun ResumenAnualDTO.toDomain(): ResumenAnual
{
    return ResumenAnual(
        año = año,
        cantidadCruces = cantidadCruces,
        totalAño = totalAño,
        mesesDisponibles = mesesDisponibles ?: emptyList(),
        cargadoCompleto = cargadoCompleto)
}

fun DetalleMensualDTO.toDomain(): DetalleMensual
{
    return DetalleMensual(
        año = año,
        mes = mes,
        dias = dias.map { it.toDomain() },
        totalMes = totalMes)
}

fun DiaResumenDTO.toDomain(): DiaResumen
{
    return DiaResumen(
        dia = dia,
        cantidadCruces = cantidadCruces,
        totalDia = totalDia)
}

fun DetalleDiaDTO.toDomain(): DetalleDia
{
    return DetalleDia(
        año = año,
        mes = mes,
        dia = dia,
        totalDia = totalDia,
        cantidadCruces = cantidadCruces,
        cruces = cruces.map { it.toDomain() }
    )
}

fun CruceDetalleDTO.toDomain(): CruceDetalle {
    val tipoVehiculoDisplay = try
    {
        TipoVehiculo.valueOf(tipoVehiculo).displayName
    } catch (e: IllegalArgumentException)
    {
        tipoVehiculo
    }

    return CruceDetalle(
        codigo = codigo,
        nombre = nombre,
        autopista = autopista,
        tipoTarifa = tipoTarifa,
        valor = valor,
        tipoVehiculo = tipoVehiculoDisplay,
        horaFechaCruce = horaFechaCruce
    )
}