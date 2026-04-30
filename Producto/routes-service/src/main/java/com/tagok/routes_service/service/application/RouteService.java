package com.tagok.routes_service.service.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.CalculadorTarifa;
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
    private final CalculadorTarifa calculadorTarifa = new CalculadorTarifa();

    public RouteResponse finAllRoads()
    {
        List<RouteSegment> segments = routeRepository.getAllRoads();

        return RouteResponse.builder()
            .segments(segments)
            .totalCost(0)
            .build();
    }

    public RouteResponse getRoute(double lon1, double lat1, double lon2, double lat2) 
    {
        List<RouteSegment> segments = routeRepository.getRouteSegments(lon1, lat1, lon2, lat2);

        double totalCost = segments.stream()
            .mapToDouble(RouteSegment::getCost)
            .sum();

        LocalDateTime tiempoActual = LocalDateTime.now();

        List<PorticoRouteResponse> porticos = new ArrayList<>();

        for (RouteSegment s : segments) 
        {
            if (s.getDistance() != null && s.getMaxSpeed() != null && s.getMaxSpeed() > 0) 
            {
                double velocidadMs = s.getMaxSpeed() / 3.6;
                long segundos = (long) (s.getDistance() / velocidadMs);

                tiempoActual = tiempoActual.plusSeconds(segundos);
            }

            if (s.getPortico() != null && s.getPortico().id() != null) 
            {
                var optional = porticoRepository.findById(s.getPortico().id());

                if (optional.isPresent())
                    porticos.add(toResponse(optional.get(), tiempoActual));
            }
        }

        return RouteResponse.builder()
            .segments(segments)
            .totalCost(totalCost)
            .porticos(porticos)
            .build();
    }

    private PorticoRouteResponse toResponse(Portico portico, LocalDateTime horaFecha)
    {
        var tipoVehiculo = TipoVehiculo.AUTO;
        var cruce = calculadorTarifa.calcular(portico, tipoVehiculo, horaFecha);

        return new PorticoRouteResponse(
            portico.getNombre(),
            portico.getCodigo(),
            portico.getAutopista().getNombre(),
            portico.getAutopista().getCodigo(),
            portico.getLongitud(),
            portico.getLatitud(),
            cruce.tarifa(),
            cruce.valor(),
            cruce.horaFechaCruce()
        );
    }
}
