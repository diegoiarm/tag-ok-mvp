package com.tagok.routes_service.dto.request.portico;

import java.util.List;

public record PorticoRequest(
    String codigo,
    String nombre,
    String sentido,
    double latitud,
    double longitud,
    List<ReglaTarifariaRequest> reglas,
    CalendarioTarifarioRequest calendario) 
{

}
