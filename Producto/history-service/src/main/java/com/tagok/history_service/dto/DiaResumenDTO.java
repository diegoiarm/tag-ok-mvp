package com.tagok.history_service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiaResumenDTO 
{
    private int dia;
    private int cantidadCruces;
    private BigDecimal totalDia;
}
