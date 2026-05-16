package com.tagok.app.domain.model.portico

data class ReglaTarifaria(
    val aplicaA: List<String>,
    val valores: List<ValorTarifa>)