package com.tagok.routes_service.dto.request.tarifa;

import java.util.List;

import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

/**
 * Se usa para mapear la información enviada por el cliente
 * cuando reporta el cruce de un pórtico.
 * Lo separe de tarifa request, para que puedan divergir,
 * la logica que equivale es la misma solo que publicara el evento
 * La mayoria de veces sera cruces.size() = 1
 */
public record TarifaPorticoCruzado(
    List<PorticoCruzadoRequest> cruces,
    TipoVehiculo vehiculo) 
{
    
}
