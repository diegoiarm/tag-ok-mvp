package com.tagok.routes_service.service.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.tarifa.Cruce;
import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.domain.tarifa.calculo.CalculoTarifaService;
import com.tagok.routes_service.domain.tarifa.calculo.CruceRequest;
import com.tagok.routes_service.dto.request.tarifa.TarifaPorticoCruzado;
import com.tagok.routes_service.dto.request.tarifa.TarifaRequest;
import com.tagok.routes_service.events.dtos.HistorialCruceEvent;
import com.tagok.routes_service.events.publishers.HistorialCrucePublisher;
import com.tagok.routes_service.service.mapper.HistorialCruceMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService
{
    private final CalculoTarifaService calculoTarifaService;
    private final HistorialCrucePublisher historialCrucePublisher;
    private final HistorialCruceMapper historialCruceMapper;

    public TarifaCalculada calcularTarifa(TarifaRequest request)
    {
        List<CruceRequest> cruceRequests = request.references().stream()
            .flatMap(c -> 
            {
                List<CruceRequest> cruces = new ArrayList<>();

                System.out.println("Añadiendo portico: " + c.porticoId());
                cruces.add(new CruceRequest(c.porticoId(), c.porticoHoraFechaCruce()));

                if (c.salidaId() != null && c.salidaHoraFechaCruce() != null)
                {
                    System.out.println("Añadiendo salida: " + c.salidaId());
                     cruces.add(new CruceRequest(c.salidaId(), c.salidaHoraFechaCruce()));
                }

                return cruces.stream();
            })
            .toList();

        List<Cruce> cruces = calculoTarifaService.calcularCruces(cruceRequests, request.vehiculo());

        BigDecimal total = cruces.stream()
            .map(Cruce::valor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TarifaCalculada(total, cruces, request.vehiculo());
    }

    /**
     * La idea del metodo es que publique eventos en Apache Kafka con la información necesaria para
     * que guarde el historial del usuario
     * @param request Request de el "evento" cruce un portico en el cliente
     * 
     * @return devuelve las tarifas para mostrarlas, pudiendo ser notificacion push etc
     */
    public TarifaCalculada calcularCruceTarifa(TarifaPorticoCruzado request)
    {
        TarifaRequest tarifa = new TarifaRequest(request.references(), request.vehiculo());

        TarifaCalculada calculo = calcularTarifa(tarifa);

        //HistorialCruceEvent evento = historialCruceMapper.toEvent(calculo);

        // Publica el evento
        //historialCrucePublisher.publicar(evento);

        return calculo;
    }
}