package com.tagok.history_service.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistorialDiarioSnapshot 
{
    private LocalDate fecha;

    private BigDecimal totalDia;

    private int cantidadCruces;

    private List<CruceSnapshot> cruces;
}
