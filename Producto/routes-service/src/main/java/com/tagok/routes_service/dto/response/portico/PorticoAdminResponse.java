package com.tagok.routes_service.dto.response.portico;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vista completa de un pórtico para la gestión administrativa (CU20). Incluye
 * código, sentido, estado, la autopista a la que pertenece y las marcas de
 * tiempo de auditoría.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PorticoAdminResponse
{
    private Long id;
    private String codigo;
    private String nombre;
    private String sentido;
    private double latitud;
    private double longitud;
    private boolean activo;
    private Long autopistaId;
    private String autopistaNombre;
    private String autopistaCodigo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
