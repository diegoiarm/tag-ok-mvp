package com.tagok.app.data.mapper

import com.tagok.app.data.dto.presupuesto.PresupuestoDto
import com.tagok.app.domain.model.presupuesto.Presupuesto

fun PresupuestoDto.toDomain(): Presupuesto = Presupuesto(
    id = id,
    userId = userId,
    vehiculoId = vehiculoId,
    montoMensual = montoMensual,
    umbralAlerta1 = umbralAlerta1,
    umbralAlerta2 = umbralAlerta2,
    alertasActivas = alertasActivas,
    createdAt = createdAt ?: "Sin especificar"
)