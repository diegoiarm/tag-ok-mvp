package com.tagok.history_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumenAnualDTO 
{
    private int año;
    private int cantidadCruces;
    private BigDecimal totalAño;
    private List<Integer> mesesDisponibles;
    private boolean cargadoCompleto;
}