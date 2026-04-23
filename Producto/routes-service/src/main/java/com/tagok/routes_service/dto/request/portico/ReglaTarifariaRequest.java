package com.tagok.routes_service.dto.request.portico;

import java.util.List;

import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;
import com.tagok.routes_service.dto.request.ValorTarifaRequest;

public record ReglaTarifariaRequest(
    List<TipoVehiculo> aplicaA,
    List<ValorTarifaRequest> valores) 
{

}
