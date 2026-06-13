package com.tagok.app.data.mapper

import com.tagok.app.data.dto.presupuesto.PresupuestoDto
import com.tagok.app.domain.model.presupuesto.Presupuesto

fun PresupuestoDto.toDomain(): Presupuesto = Presupuesto(
    id = id,
    userId = userId,
    vehiculoId = vehiculoId ?: "Sin especificar",
    montoMensual = montoMensual,
    umbralAlerta1 = umbralAlerta1,
    umbralAlerta2 = umbralAlerta2,
    createdAt = createdAt ?: "Sin especificar"
)