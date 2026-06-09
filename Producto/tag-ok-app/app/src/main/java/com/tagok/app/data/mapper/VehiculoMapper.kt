package com.tagok.app.data.mapper

import com.tagok.app.data.dto.vehiculo.NuevoVehiculoRequest
import com.tagok.app.data.dto.vehiculo.VehiculoDto
import com.tagok.app.domain.model.vehiculo.NuevoVehiculo
import com.tagok.app.domain.model.vehiculo.Vehiculo

fun VehiculoDto.toDomain(): Vehiculo
{
    return Vehiculo(
        id = id,
        userId = userId,
        patente = patente,
        tipoVehiculo = tipoVehiculo,
        numeroTag = numeroTag,
        alias = alias,
        esPrincipal = esPrincipal,
        createdAt = createdAt)
}

fun NuevoVehiculo.toRequest(): NuevoVehiculoRequest
{
    return NuevoVehiculoRequest(
        userId = userId,
        patente = patente,
        tipoVehiculo = tipoVehiculo,
        numeroTag = numeroTag,
        alias = alias)
}