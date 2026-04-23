package com.tagok.routes_service.dto.request.portico;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PorticoRequest 
{
    private Long id;
    private String codigo;
    private String nombre;
    private String sentido;
    private double latitud;
    private double longitud;

    private List<ReglaTarifariaRequest> reglas;
    private CalendarioTarifarioRequest calendario;
}
