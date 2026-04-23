package com.tagok.routes_service.dto.request.portico;

import java.time.LocalTime;

public record RangoHorarioRequest(
    LocalTime inicio,
    LocalTime fin) 
{

}
