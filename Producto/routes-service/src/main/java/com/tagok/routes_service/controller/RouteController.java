package com.tagok.routes_service.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.dto.response.RouteResponse;
import com.tagok.routes_service.service.application.RouteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RouteController 
{
    private final RouteService routeService;

    @GetMapping
    public RouteResponse getRoute(
        @RequestParam double lon1,
        @RequestParam double lat1,
        @RequestParam double lon2,
        @RequestParam double lat2)
    {
        return routeService.getRoute(lon1, lat1, lon2, lat2);
    }
}
