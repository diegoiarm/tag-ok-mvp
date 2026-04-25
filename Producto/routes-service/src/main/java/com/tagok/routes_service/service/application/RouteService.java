package com.tagok.routes_service.service.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.dto.RouteSegment;
import com.tagok.routes_service.dto.response.RouteResponse;
import com.tagok.routes_service.repository.RouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteService 
{
    private final RouteRepository routeRepository;

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

        return RouteResponse.builder()
            .segments(segments)
            .totalCost(totalCost)
            .build();
    }
}
