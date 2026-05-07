package com.tagok.app.data

import com.tagok.app.supabase
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator

class PresupuestoRepository {

    private val table get() = supabase.postgrest["presupuesto"]

    suspend fun getAll(): List<Presupuesto> = table.select().decodeList()

    suspend fun save(nuevo: NuevoPresupuesto) {
        val existing = table.select {
            filter {
                eq("user_id", nuevo.userId)
                if (nuevo.vehiculoId != null) {
                    eq("vehiculo_id", nuevo.vehiculoId)
                } else {
                    filter("vehiculo_id", FilterOperator.IS, "null")
                }
            }
        }.decodeList<Presupuesto>().firstOrNull()

        if (existing != null) {
            table.update(
                ActualizarPresupuesto(
                    montoMensual  = nuevo.montoMensual,
                    umbralAlerta1 = nuevo.umbralAlerta1,
                    umbralAlerta2 = nuevo.umbralAlerta2,
                )
            ) {
                filter { eq("id", existing.id) }
            }
        } else {
            table.insert(nuevo)
        }
    }

    suspend fun delete(id: String) {
        table.delete { filter { eq("id", id) } }
    }
}
