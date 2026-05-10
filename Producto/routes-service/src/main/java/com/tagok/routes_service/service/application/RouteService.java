package com.tagok.routes_service.service.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.calculo.CalculadorTarifaFactory;
import com.tagok.routes_service.domain.tarifa.calculo.CalculoContexto;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;
import com.tagok.routes_service.dto.RouteSegment;
import com.tagok.routes_service.dto.response.PorticoRouteResponse;
import com.tagok.routes_service.dto.response.RouteResponse;
import com.tagok.routes_service.repository.PorticoRepository;
import com.tagok.routes_service.repository.RouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteService 
{
    private final RouteRepository routeRepository;
    private final PorticoRepository porticoRepository;
    private final CalculadorTarifaFactory calculadorFactory;

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
        List<PorticoRouteResponse> porticos = new ArrayList<>();

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

                if (porticosProcesados.contains(porticoId)) 
                    continue;

                var optional = porticoRepository.findById(porticoId);

                if (optional.isPresent()) 
                {
                    toResponse(optional.get(), tiempoActual)
                        .ifPresent(p -> 
                        {
                            porticos.add(p);
                            porticosProcesados.add(porticoId);
                        });
                }
            }
        }

        LocalDateTime tiempoFinal = tiempoActual;
        BigDecimal totalCost = porticos.stream()
                .map(PorticoRouteResponse::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RouteResponse.builder()
                .fechaHoraInicio(tiempoInicio)
                .fechaHoraFin(tiempoFinal)
            //    .segments(segments)
                .totalCost(totalCost)
                .porticos(porticos)
                .mergedRouteGeometry(mergedGeometry)
                .build();
    }

    private Optional<PorticoRouteResponse> toResponse(
        Portico portico,
        LocalDateTime horaFecha)
    {
        var tipoVehiculo = TipoVehiculo.AUTO;

        var contexto = CalculoContexto.builder()
            .autopista(portico.getAutopista())
            .portico(portico)
            .vehiculo(tipoVehiculo)
            .fecha(horaFecha)
            .build();

        var strategy = calculadorFactory
            .getStrategy(portico.getAutopista());

        return strategy.calcular(contexto)
            .map(tarifa -> new PorticoRouteResponse(
                portico.getNombre(),
                portico.getCodigo(),
                portico.getAutopista().getNombre(),
                portico.getAutopista().getCodigo(),
                portico.getSentido(),
                portico.getLongitud(),
                portico.getLatitud(),
                tarifa.tipoTarifa(),
                tarifa.monto(),
                horaFecha
            ));
    }
}
