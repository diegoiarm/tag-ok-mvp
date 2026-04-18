package com.tagok.routes_service.domain.dto.request;

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
