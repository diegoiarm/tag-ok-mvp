package com.tagok.history_service.event.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCruceEvent 
{
    private String eventoId;
    private String usuarioId;
    private BigDecimal total;

    @Builder.Default
    List<CruceSnapshot> cruces = new ArrayList<>();
    LocalDateTime fechaGeneracion;
}