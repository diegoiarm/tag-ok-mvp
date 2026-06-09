// domain/model/tarifa/Cruce.kt
package com.tagok.app.domain.model.tarifa

sealed class Cruce
{
    abstract val codigo: String
    abstract val nombre: String
    abstract val autopista: String
    abstract val tipoTarifa: String
    abstract val valor: Double
    abstract val horaFechaCruce: String
}

data class CrucePortico(
    val porticoId: Long,
    override val codigo: String,
    override val nombre: String,
    override val autopista: String,
    val latitud: Double,
    val longitud: Double,
    override val tipoTarifa: String,
    override val valor: Double,
    override val horaFechaCruce: String) : Cruce()

data class CruceTramo(
    override val codigo: String,
    override val nombre: String,
    override val autopista: String,
    override val tipoTarifa: String,
    override val valor: Double,
    override val horaFechaCruce: String,
    val codigoEntrada: String,
    val codigoSalida: String,
    val entradaId: Long,
    val salidaId: Long,
    val nombreEntrada: String,
    val nombreSalida: String,
    val latitudEntrada: Double,
    val longitudEntrada: Double,
    val latitudSalida: Double,
    val longitudSalida: Double) : Cruce()