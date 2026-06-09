package com.tagok.history_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoletaItemDTO 
{
    private LocalDate fecha;
    private String autopista;
    private String nombre;
    private String tipoTarifa;
    private BigDecimal valor;
    private String horaCruce;
}
