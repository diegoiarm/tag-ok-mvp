package com.tagok.app.data.mapper

import com.tagok.app.data.dto.boleta.BoletaDto
import com.tagok.app.data.dto.boleta.BoletaItemDto
import com.tagok.app.domain.model.boleta.Boleta
import com.tagok.app.domain.model.boleta.BoletaItem

fun BoletaDto.toDomain(): Boleta
{
    return Boleta(
        patente,
        fechaDesde,
        fechaHasta,
        items.map { it.toDomain() },
        total)
}

fun BoletaItemDto.toDomain(): BoletaItem
{
    return BoletaItem(
        fecha,
        autopista,
        nombre,
        tipoTarifa,
        valor,
        horaCruce)
}