package com.tagok.routes_service.dto.request.tramo;

import java.util.List;

import com.tagok.routes_service.dto.request.portico.CalendarioTarifarioRequest;
import com.tagok.routes_service.dto.request.portico.ReglaTarifariaRequest;

public record TramoRequest(
    String entrada,
    String salida,
    double distancia,
    String area,
    String sentido,
    List<ReglaTarifariaRequest> reglas,
    CalendarioTarifarioRequest calendario)
{
}