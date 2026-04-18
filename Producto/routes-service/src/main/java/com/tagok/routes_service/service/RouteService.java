package com.tagok.routes_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.dto.RouteSegment;
import com.tagok.routes_service.domain.dto.response.RouteResponse;
import com.tagok.routes_service.repository.RouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteService 
{
    private final RouteRepository routeRepository;

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
