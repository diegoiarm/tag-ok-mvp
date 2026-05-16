package com.tagok.app.domain.model

data class Route(
    val points: List<Point>,
    val tolls: List<Toll>,
    val totalCost: Double)
