package com.tagok.routes_service.dto.response.portico;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoResponse
{
    private String entrada;
    private String nombreEntrada;
    private String salida;
    private String nombreSalida;

    private List<ReglaTarifariaResponse> reglas;

    private CalendarioTarifarioResponse calendario;
}
