package com.tagok.app.ui.boleta.comparacion

import com.tagok.app.domain.model.boleta.ArchivoFactura
import com.tagok.app.domain.model.boleta.ComparacionFactura
import kotlinx.datetime.LocalDate

data class ComparacionUiState(
    val patente: String,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate,
    val autopistas: List<String>,
    val archivo: ArchivoFactura? = null,
    val cargandoArchivo: Boolean = false,
    val isComparando: Boolean = false,
    val resultado: ComparacionFactura? = null,
    val error: String? = null)
