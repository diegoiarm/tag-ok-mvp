package com.tagok.app.domain.model.history

data class ResumenAnual(
    val año: Int,
    val cantidadCruces: Int,
    val totalAño: Double,
    val mesesDisponibles: List<Int> = emptyList(),
    val cargadoCompleto: Boolean = false)

data class DetalleMensual(
    val año: Int,
    val mes: Int,
    val dias: List<DiaResumen>,
    val totalMes: Double)

data class DiaResumen(
    val dia: Int,
    val cantidadCruces: Int,
    val totalDia: Double)

data class DetalleDia(
    val año: Int,
    val mes: Int,
    val dia: Int,
    val totalDia: Double,
    val cantidadCruces: Int,
    val cruces: List<CruceDetalle>)

data class CruceDetalle(
    val codigo: String,
    val nombre: String,
    val autopista: String,
    val tipoTarifa: String,
    val valor: Double,
    val tipoVehiculo: String,
    val horaFechaCruce: String)