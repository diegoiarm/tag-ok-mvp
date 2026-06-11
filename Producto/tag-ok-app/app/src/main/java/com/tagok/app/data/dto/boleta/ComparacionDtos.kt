package com.tagok.app.data.dto.boleta

import kotlinx.serialization.Serializable

@Serializable
data class ComparacionFacturaDto(
    val boletaApp: BoletaDto,
    val facturaCliente: FacturaExtraidaDto,
    val items: List<ComparacionItemDto> = emptyList(),
    val totalApp: Double,
    val totalFactura: Double,
    val diferenciaTotal: Double,
    val coincidencias: Int,
    val discrepancias: Int,
    val cuadra: Boolean)

@Serializable
data class FacturaExtraidaDto(
    val patente: String? = null,
    val total: Double? = null,
    val items: List<FacturaItemDto> = emptyList())

@Serializable
data class FacturaItemDto(
    val fecha: String? = null,
    val hora: String? = null,
    val portico: String? = null,
    val autopista: String? = null,
    val valor: Double? = null)

@Serializable
data class ComparacionItemDto(
    val estado: String,
    val itemApp: BoletaItemDto? = null,
    val itemFactura: FacturaItemDto? = null,
    val diferenciaValor: Double? = null)
