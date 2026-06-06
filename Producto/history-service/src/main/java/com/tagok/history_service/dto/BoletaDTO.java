package com.tagok.history_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoletaDTO 
{
    private String patente;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private List<BoletaItemDTO> items;
    private BigDecimal total;
}