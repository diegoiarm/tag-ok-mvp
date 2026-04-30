package com.tagok.app.data

import com.tagok.app.supabase
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class VehiculoRepository {

    suspend fun getVehiculos(): List<Vehiculo> =
        supabase.postgrest["vehiculos"]
            .select {
                order("created_at", Order.ASCENDING)
            }
            .decodeList()

    suspend fun insertVehiculo(nuevo: NuevoVehiculo) {
        supabase.postgrest["vehiculos"].insert(nuevo)
    }

    suspend fun deleteVehiculo(id: String) {
        supabase.postgrest["vehiculos"].delete {
            filter { eq("id", id) }
        }
    }
}
