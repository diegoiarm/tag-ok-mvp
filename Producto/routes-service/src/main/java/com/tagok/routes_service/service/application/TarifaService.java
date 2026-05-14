package com.tagok.routes_service.service.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.tarifa.Cruce;
import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.domain.tarifa.calculo.CalculoTarifaService;
import com.tagok.routes_service.domain.tarifa.calculo.CruceRequest;
import com.tagok.routes_service.dto.request.tarifa.TarifaRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService
{
    private final CalculoTarifaService calculoTarifaService;

    public TarifaCalculada calcularTarifa(TarifaRequest request)
    {
        List<CruceRequest> cruceRequests = request.porticosCruzados().stream()
            .map(c -> new CruceRequest(c.porticoId(), c.horaFechaCruce()))
            .toList();

        List<Cruce> cruces = calculoTarifaService.calcularCruces(cruceRequests, request.vehiculo());

        BigDecimal total = cruces.stream()
            .map(Cruce::valor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TarifaCalculada(total, cruces, request.vehiculo());
    }
}