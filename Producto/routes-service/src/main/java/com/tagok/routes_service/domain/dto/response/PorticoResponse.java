package com.tagok.routes_service.domain.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PorticoResponse 
{
    private Long id;
    private String codigo;
    private String nombre;
    private String sentido;
    private double latitud;
    private double longitud;

    private String autopista;

    private List<ReglaTarifariaResponse> reglas;
    private CalendarioTarifarioResponse calendario;
}
