package com.tagok.app.domain.model.boleta

enum class EstadoComparacion
{
    COINCIDE,
    MONTO_DIFERENTE,
    SOLO_EN_APP,
    SOLO_EN_FACTURA
}

data class FacturaItem(
    val fecha: String?,
    val hora: String?,
    val portico: String?,
    val autopista: String?,
    val valor: Double?)

data class FacturaExtraida(
    val patente: String?,
    val total: Double?,
    val items: List<FacturaItem>)

data class ComparacionItem(
    val estado: EstadoComparacion,
    val itemApp: BoletaItem?,
    val itemFactura: FacturaItem?,
    val diferenciaValor: Double)

data class ComparacionFactura(
    val boletaApp: Boleta,
    val facturaCliente: FacturaExtraida,
    val items: List<ComparacionItem>,
    val totalApp: Double,
    val totalFactura: Double,
    val diferenciaTotal: Double,
    val coincidencias: Int,
    val discrepancias: Int,
    val cuadra: Boolean)
{
    val diferencias: List<ComparacionItem>
        get() = items.filter { it.estado != EstadoComparacion.COINCIDE }

    val itemsCoincidentes: List<ComparacionItem>
        get() = items.filter { it.estado == EstadoComparacion.COINCIDE }
}

/**
 * Archivo adjuntado por el usuario (PDF de la concesionaria o foto de cámara),
 * ya leído en memoria y listo para subir al backend.
 */
class ArchivoFactura(
    val nombre: String,
    val mimeType: String,
    val bytes: ByteArray)
{
    val esPdf: Boolean
        get() = mimeType == "application/pdf"

    val tamanoLegible: String
        get()
        {
            val kb = bytes.size / 1024.0
            return if (kb >= 1024) "%.1f MB".format(kb / 1024.0) else "%.0f KB".format(kb)
        }
}
