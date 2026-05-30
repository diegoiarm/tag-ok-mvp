package com.tagok.history_service.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CruceSnapshot
{
    private String codigo;
    private String nombre;
    private String autopista;

    private String tipoTarifa;

    private BigDecimal valor;

    private LocalDateTime horaFechaCruce;

    public static CruceSnapshot fromEvent(com.tagok.history_service.event.dtos.CruceSnapshot evento)
    {
        return CruceSnapshot.builder()
            .codigo(evento.getCodigo())
            .nombre(evento.getNombre())
            .autopista(evento.getAutopista())
            .tipoTarifa(evento.getTipoTarifa())
            .valor(evento.getValor())
            .horaFechaCruce(evento.getHoraFechaCruce())
            .build();
    }  
}
