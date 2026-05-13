package com.tagok.routes_service.service.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.tarifa.Cruce;
import com.tagok.routes_service.domain.tarifa.CrucePortico;
import com.tagok.routes_service.domain.tarifa.CruceTramo;
import com.tagok.routes_service.domain.tarifa.calculo.CalculoTarifaService;
import com.tagok.routes_service.domain.tarifa.calculo.CruceRequest;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;
import com.tagok.routes_service.dto.RouteSegment;
import com.tagok.routes_service.dto.response.CobroPorticoResponse;
import com.tagok.routes_service.dto.response.CobroRutaResponse;
import com.tagok.routes_service.dto.response.CobroTramoResponse;
import com.tagok.routes_service.dto.response.RouteResponse;
import com.tagok.routes_service.repository.RouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteService 
{
    private final RouteRepository routeRepository;
    private final CalculoTarifaService calculoTarifaService;

    public RouteResponse finAllRoads()
    {
        return RouteResponse.builder()
            .totalCost(BigDecimal.ZERO)
            .build();
    }

    public RouteResponse getRoute(double lon1, double lat1, double lon2, double lat2) 
    {
        Long startId = routeRepository.findNearestVertex(lon1, lat1)
                .orElseThrow(() -> new RuntimeException("No se encontró vértice cercano a inicio"));
        Long endId = routeRepository.findNearestVertex(lon2, lat2)
                .orElseThrow(() -> new RuntimeException("No se encontró vértice cercano a fin"));

        List<RouteSegment> segments = routeRepository.getRouteSegments(startId, endId);

        String mergedGeometry = routeRepository.findMergedRouteGeometry(startId, endId)
            .orElseThrow(() -> new IllegalStateException("No se pudo encontrar una ruta para los dos puntos asociados"));

        LocalDateTime tiempoInicio = LocalDateTime.now();
        LocalDateTime tiempoActual = tiempoInicio;

        List<CruceRequest> crucesReq = new ArrayList<>();
        Set<Long> porticosProcesados = new HashSet<>();

        for (RouteSegment s : segments)
        {
            if (s.getDistance() != null && s.getMaxSpeed() != null && s.getMaxSpeed() > 0)
            {
                double velocidadMs = s.getMaxSpeed() / 3.6;
                long segundos = Math.round(s.getDistance() / velocidadMs);

                tiempoActual = tiempoActual.plusSeconds(segundos);
            }

            if (s.getPortico() != null && s.getPortico().id() != null)
            {
                Long porticoId = s.getPortico().id();

                if (porticosProcesados.add(porticoId))
                {
                    crucesReq.add(new CruceRequest(porticoId, tiempoActual));
                }
            }
        }

        List<Cruce> cruces = calculoTarifaService.calcularCruces(crucesReq, TipoVehiculo.AUTO);

        List<CobroRutaResponse> cobros = cruces.stream()
                .map(this::toResponse)
                .toList();

        BigDecimal totalCost = cruces.stream()
            .map(Cruce::valor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RouteResponse.builder()
            .fechaHoraInicio(tiempoInicio)
            .fechaHoraFin(tiempoActual)
            .totalCost(totalCost)
            .cobros(cobros)
            .mergedRouteGeometry(mergedGeometry)
            .build();
    }

    private CobroRutaResponse toResponse(Cruce cruce) 
    {
        if (cruce instanceof CrucePortico cp) 
        {
            return new CobroPorticoResponse(
                    cp.porticoId(),
                    cp.nombre(),
                    cp.codigo(),
                    cp.autopista(),
                    cp.latitud(),
                    cp.longitud(),
                    cp.tipoTarifa(),
                    cp.valor(),
                    cp.horaFechaCruce()
            );
        }

        if (cruce instanceof CruceTramo ct) 
        {
            return new CobroTramoResponse(
                    ct.entradaId(),
                    ct.salidaId(),
                    ct.nombreEntrada(),
                    ct.nombreSalida(),
                    ct.autopista(),
                    ct.tipoTarifa(),
                    ct.valor(),
                    ct.horaFechaCruce()
            );
        }

        throw new IllegalStateException("Tipo de cruce desconocido: " + cruce.getClass());
    }
}
