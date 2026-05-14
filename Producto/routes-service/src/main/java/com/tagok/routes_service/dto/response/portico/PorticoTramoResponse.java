package com.tagok.routes_service.dto.response.portico;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PorticoTramoResponse implements TollResponse
{
    private Long id;
    private String codigo;
    private String nombre;

    private double latitud;
    private double longitud;

    private String autopista;

    private List<TramoResponse> tramos;
}
