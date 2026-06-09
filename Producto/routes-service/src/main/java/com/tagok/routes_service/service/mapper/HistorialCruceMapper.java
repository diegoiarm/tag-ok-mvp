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
    public HistorialCruceEvent toEvent(TarifaCalculada calculo, String patente, String userId)
    {
        return HistorialCruceEvent.builder()
            .usuarioId(userId)
            .total(calculo.total())
            .cruces(calculo.cruces().stream()
                .map(cruce -> this.toSnapshot(cruce, calculo.vehiculo().name(), patente))
                .toList())
            .fechaGeneracion(LocalDateTime.now())
            .build();
    }

    private CruceSnapshot toSnapshot(Cruce cruce, String tipoVehiculo, String patente)
    {
        return CruceSnapshot.builder()
            .codigo(cruce.codigo())
            .nombre(cruce.nombre())
            .autopista(cruce.autopista())
            .tipoTarifa(cruce.tipoTarifa().name())
            .valor(cruce.valor())
            .tipoVehiculo(tipoVehiculo)
            .patente(patente)
            .horaFechaCruce(cruce.horaFechaCruce())
            .build();
    }
}
