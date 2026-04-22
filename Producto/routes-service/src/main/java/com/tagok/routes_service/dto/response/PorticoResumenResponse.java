package com.tagok.routes_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PorticoResumenResponse 
{
    private Long id;
    private String codigo;
    private String nombre;
    private String sentido;
    private double latitud;
    private double longitud;
}
