package com.tagok.app.data.mapper

import com.tagok.app.data.dto.boleta.ComparacionFacturaDto
import com.tagok.app.data.dto.boleta.ComparacionItemDto
import com.tagok.app.data.dto.boleta.FacturaExtraidaDto
import com.tagok.app.data.dto.boleta.FacturaItemDto
import com.tagok.app.domain.model.boleta.ComparacionFactura
import com.tagok.app.domain.model.boleta.ComparacionItem
import com.tagok.app.domain.model.boleta.EstadoComparacion
import com.tagok.app.domain.model.boleta.FacturaExtraida
import com.tagok.app.domain.model.boleta.FacturaItem

fun ComparacionFacturaDto.toDomain(): ComparacionFactura
{
    return ComparacionFactura(
        boletaApp = boletaApp.toDomain(),
        facturaCliente = facturaCliente.toDomain(),
        items = items.map { it.toDomain() },
        totalApp = totalApp,
        totalFactura = totalFactura,
        diferenciaTotal = diferenciaTotal,
        coincidencias = coincidencias,
        discrepancias = discrepancias,
        cuadra = cuadra)
}

fun FacturaExtraidaDto.toDomain(): FacturaExtraida
{
    return FacturaExtraida(
        patente = patente,
        total = total,
        items = items.map { it.toDomain() })
}

fun FacturaItemDto.toDomain(): FacturaItem
{
    return FacturaItem(
        fecha = fecha,
        hora = hora,
        portico = portico,
        autopista = autopista,
        valor = valor)
}

fun ComparacionItemDto.toDomain(): ComparacionItem
{
    return ComparacionItem(
        estado = EstadoComparacion.entries.firstOrNull { it.name == estado }
            ?: EstadoComparacion.MONTO_DIFERENTE,
        itemApp = itemApp?.toDomain(),
        itemFactura = itemFactura?.toDomain(),
        diferenciaValor = diferenciaValor ?: 0.0)
}
