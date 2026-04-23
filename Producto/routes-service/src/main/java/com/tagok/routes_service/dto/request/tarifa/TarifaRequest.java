package com.tagok.routes_service.dto.request.tarifa;

import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

public record TarifaRequest(
    Long porticoId,
    TipoVehiculo vehiculo) 
{

}
