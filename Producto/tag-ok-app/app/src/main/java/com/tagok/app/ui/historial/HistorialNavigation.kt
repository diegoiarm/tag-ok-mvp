package com.tagok.app.ui.historial

import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.historial.model.PatenteFilter
import com.tagok.app.ui.historial.model.SortOption

sealed class HistorialDestination
{
    object YearList : HistorialDestination()
    data class MonthView(val year: Int) : HistorialDestination()
    data class DayDetail(val year: Int, val month: Int, val day: Int) : HistorialDestination()
}

data class ListState(
    val years: List<Int> = emptyList(),
    val resumenAnual: List<ResumenAnual> = emptyList(),
    val resumenAnualOriginal: List<ResumenAnual> = emptyList(),
    val currentSort: SortOption = SortOption.DEFAULT)

data class FilterState(
    val patentes: List<PatenteFilter> = emptyList(),
    val patentesSeleccionadas: List<String> = emptyList()
)

data class DetailState(
    val selectedYear: Int,
    val detalleAnual: ResumenAnual? = null,
    val detalleMensual: DetalleMensual? = null,
    val detalleDia: DetalleDia? = null)

data class LoadingState(
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false)

data class HistorialUiState(
    val listState: ListState = ListState(),
    val filterState: FilterState = FilterState(),
    val detailState: DetailState? = null,
    val loadingState: LoadingState = LoadingState(),
    val error: String? = null,
    val navigationStack: List<HistorialDestination> = listOf(HistorialDestination.YearList))