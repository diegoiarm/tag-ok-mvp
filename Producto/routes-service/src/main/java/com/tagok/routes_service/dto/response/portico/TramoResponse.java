package com.tagok.routes_service.dto.response.portico;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TramoResponse
{
    private Long id;

    private PorticoResumenResponse entrada;
    private PorticoResumenResponse salida;

    private double distanciaKm;

    private String autopista;

    private List<ReglaTarifariaResponse> reglas;
    private CalendarioTarifarioResponse calendario;
}
