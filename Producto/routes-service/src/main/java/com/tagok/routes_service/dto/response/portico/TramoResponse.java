package com.tagok.routes_service.dto.response.portico;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoResponse implements TollResponse
{
    private String entrada;
    private String salida;

    private List<ReglaTarifariaResponse> reglas;

    private CalendarioTarifarioResponse calendario;

    private final String type = "TRAMO";
}
