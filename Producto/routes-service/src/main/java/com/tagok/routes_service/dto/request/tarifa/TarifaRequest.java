package com.tagok.routes_service.dto.request.tarifa;

import java.util.List;

import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

public record TarifaRequest(
    List<PorticoCruzadoReferences> references,
    TipoVehiculo vehiculo) 
{

}
