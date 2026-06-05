package com.tagok.app.data.dto.history

import kotlinx.serialization.Serializable

@Serializable
data class ResumenAnualDTO(
    val año: Int? = null,
    val cantidadCruces: Int? = null,
    val totalAño: Double? = null,
    val mesesDisponibles: List<Int>? = null,
    val cargadoCompleto: Boolean? = null)

@Serializable
data class DetalleMensualDTO(
    val año: Int,
    val mes: Int,
    val dias: List<DiaResumenDTO>,
    val totalMes: Double)

@Serializable
data class DiaResumenDTO(
    val dia: Int,
    val cantidadCruces: Int,
    val totalDia: Double)

@Serializable
data class DetalleDiaDTO(
    val año: Int,
    val mes: Int,
    val dia: Int,
    val totalDia: Double,
    val cantidadCruces: Int,
    val cruces: List<CruceDetalleDTO>)

@Serializable
data class CruceDetalleDTO(
    val codigo: String,
    val nombre: String,
    val autopista: String,
    val tipoTarifa: String,
    val valor: Double,
    val tipoVehiculo: String,
    val horaFechaCruce: String)

@Serializable
data class FiltroHistorialRequest(
    val patentes: List<String> = emptyList(),
    val autopistas: List<String> = emptyList())