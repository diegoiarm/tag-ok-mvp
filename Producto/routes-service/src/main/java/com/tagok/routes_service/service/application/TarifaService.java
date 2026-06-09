package com.tagok.routes_service.service.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.autopista.TipoCobro;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.Cruce;
import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.domain.tarifa.calculo.CalculoTarifaService;
import com.tagok.routes_service.domain.tarifa.calculo.CruceRequest;
import com.tagok.routes_service.domain.tarifa.calculo.PorticoTramoPortico;
import com.tagok.routes_service.dto.request.tarifa.PorticoCruzadoReferences;
import com.tagok.routes_service.dto.request.tarifa.TarifaPorticoCruzado;
import com.tagok.routes_service.dto.request.tarifa.TarifaRequest;
import com.tagok.routes_service.events.dtos.HistorialCruceEvent;
import com.tagok.routes_service.events.publishers.HistorialCrucePublisher;
import com.tagok.routes_service.repository.PorticoRepository;
import com.tagok.routes_service.service.mapper.HistorialCruceMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService
{
    private final CalculoTarifaService calculoTarifaService;
    private final PorticoRepository porticoRepository;
    private final HistorialCrucePublisher historialCrucePublisher;
    private final HistorialCruceMapper historialCruceMapper;

    public TarifaCalculada calcularTarifa(TarifaRequest request)
    {
        validarReferencias(request.references());

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

    private void validarReferencias(List<PorticoCruzadoReferences> referencias)
    {
        for (PorticoCruzadoReferences ref : referencias)
        {
            Portico portico = porticoRepository.findById(ref.porticoId().longValue())
                .orElseThrow();

            TipoCobro tipoCobro = portico.getAutopista().getTipoCobro();

            boolean tieneSalida = ref.salidaId() != null || ref.salidaHoraFechaCruce() != null;

            boolean esPorticoEspecial = PorticoTramoPortico.esTramoEspecialComoPortico(portico);

            if (tipoCobro == TipoCobro.PORTICO || esPorticoEspecial)
            {
                if (tieneSalida)
                    throw new IllegalArgumentException("La autopista " + portico.getAutopista().getNombre() + " no admite salida para el pórtico " + portico.getCodigo());

                continue;
            }

            if (tipoCobro == TipoCobro.TRAMO)
            {
                if (ref.salidaId() == null || ref.salidaHoraFechaCruce() == null)
                    throw new IllegalArgumentException("La autopista " + portico.getAutopista().getNombre() + " requiere salida");
            }
        }
    }

    /**
     * La idea del metodo es que publique eventos en Apache Kafka con la información necesaria para
     * que guarde el historial del usuario
     * @param request Request de el "evento" cruce un portico en el cliente
     * 
     * @return devuelve las tarifas para mostrarlas, pudiendo ser notificacion push etc
     */
    public TarifaCalculada calcularCruceTarifa(TarifaPorticoCruzado request, String userId)
    {
        TarifaRequest tarifa = new TarifaRequest(request.references(), request.vehiculo());

        TarifaCalculada calculo = calcularTarifa(tarifa);

        var patente = request.patente();

        try
        {
            HistorialCruceEvent evento = historialCruceMapper.toEvent(calculo, patente, userId);
            historialCrucePublisher.publicar(evento);
        }
        catch (Exception e)
        {
            System.out.println("No fue posible publicar el historial de cruces: " + e);
        }

        return calculo;
    }
}