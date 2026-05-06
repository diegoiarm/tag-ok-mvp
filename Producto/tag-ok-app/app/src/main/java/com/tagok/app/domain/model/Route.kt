package com.tagok.app.domain.model

data class Route(
    val points: List<Point>,
    val porticos: List<Portico>,
    val totalCost: Double)
