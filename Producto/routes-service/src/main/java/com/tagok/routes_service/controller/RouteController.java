package com.tagok.routes_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;
import com.tagok.routes_service.dto.response.route.RouteResponse;
import com.tagok.routes_service.service.application.RouteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/rutas")
@RequiredArgsConstructor
public class RouteController 
{
    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<RouteResponse> obtenerRuta(@RequestBody RouteRequest request)
    {
        return ResponseEntity.ok(routeService.getRoute(request.lon1, request.lat1, request.lon2, request.lat2, request.vehiculo));
    }

    private record RouteRequest(double lon1, double lat1, double lon2, double lat2, TipoVehiculo vehiculo) {}
}
