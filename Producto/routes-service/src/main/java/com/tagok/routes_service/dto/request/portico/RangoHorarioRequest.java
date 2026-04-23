package com.tagok.routes_service.dto.request.portico;

import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RangoHorarioRequest 
{
    private LocalTime inicio;
    private LocalTime fin;
}
