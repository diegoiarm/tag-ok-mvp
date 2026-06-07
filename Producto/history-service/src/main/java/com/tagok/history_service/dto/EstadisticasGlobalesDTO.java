package com.tagok.history_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/** Estadísticas agregadas de uso del historial de todos los usuarios (CU18). */
@Data
@Builder
public class EstadisticasGlobalesDTO
{
    private long totalCruces;
    private BigDecimal totalGasto;
    private long usuariosConCruces;
    private List<PuntoAnual> porAnio;

    @Data
    @Builder
    public static class PuntoAnual
    {
        private int año;
        private long cruces;
        private BigDecimal gasto;
    }
}
