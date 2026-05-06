package com.tagok.routes_service.dto.request.tramo;

import java.util.List;

import com.tagok.routes_service.dto.request.portico.CalendarioTarifarioRequest;
import com.tagok.routes_service.dto.request.portico.ReglaTarifariaRequest;

public record TramoRequest(
    String codigoEntrada,
    String codigoSalida,
    double distanciaKm,
    List<ReglaTarifariaRequest> reglas,
    CalendarioTarifarioRequest calendario)
{
}