package com.tagok.routes_service.service.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.tarifa.Cruce;
import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.events.dtos.CruceSnapshot;
import com.tagok.routes_service.events.dtos.HistorialCruceEvent;

@Component
public class HistorialCruceMapper 
{
    public HistorialCruceEvent toEvent(TarifaCalculada calculo)
    {
        return HistorialCruceEvent.builder()
            .usuarioId(null)
            .total(calculo.total())
            .tipoVehiculo(calculo.vehiculo().name())
            .cruces(calculo.cruces().stream()
                .map(this::toSnapshot)
                .toList())
            .fechaGeneracion(LocalDateTime.now())
            .build();
    }

    private CruceSnapshot toSnapshot(Cruce cruce)
    {
        return CruceSnapshot.builder()
            .codigo(cruce.codigo())
            .nombre(cruce.nombre())
            .autopista(cruce.autopista())
            .tipoTarifa(cruce.tipoTarifa().name())
            .valor(cruce.valor())
            .horaFechaCruce(cruce.horaFechaCruce())
            .build();
    }
}
