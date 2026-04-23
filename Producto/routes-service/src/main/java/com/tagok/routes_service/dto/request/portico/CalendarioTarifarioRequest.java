package com.tagok.routes_service.dto.request.portico;

import java.util.List;

public record CalendarioTarifarioRequest(
    List<ReglaTemporalRequest> reglas) 
{

}
