package com.tagok.routes_service.dto.request.portico;

import java.util.List;

import com.tagok.routes_service.domain.calendario.TipoDia;
import com.tagok.routes_service.domain.tarifa.TipoTarifa;

public record ReglaTemporalRequest(
    TipoTarifa tipoTarifa,
    TipoDia tipoDia,
    List<RangoHorarioRequest> tramos) 
{

}
