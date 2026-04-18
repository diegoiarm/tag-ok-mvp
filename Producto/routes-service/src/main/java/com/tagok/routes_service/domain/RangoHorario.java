package com.tagok.routes_service.domain;

import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RangoHorario 
{
    private LocalTime inicio;
    private LocalTime fin;
}
