package com.tagok.app.ui.boleta

import com.tagok.app.domain.model.boleta.Boleta
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class BoletaUiState(
    val patentes: List<String> = emptyList(),
    val patenteSeleccionada: String = "",
    val autopistas: List<String> = emptyList(),
    val autopistasSeleccionadas: List<String> = emptyList(),
    val fechaDesde: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date,
    val fechaHasta: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date,
    val boleta: Boleta? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)