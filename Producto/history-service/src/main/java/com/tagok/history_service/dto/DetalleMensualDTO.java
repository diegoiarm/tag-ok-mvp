package com.tagok.history_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleMensualDTO 
{
    private int año;
    private int mes;
    private List<DiaResumenDTO> dias;
    private BigDecimal totalMes;
}
