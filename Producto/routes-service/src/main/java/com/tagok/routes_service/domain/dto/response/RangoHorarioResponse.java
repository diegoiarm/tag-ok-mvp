package com.tagok.routes_service.domain.dto.response;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RangoHorarioResponse 
{
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
