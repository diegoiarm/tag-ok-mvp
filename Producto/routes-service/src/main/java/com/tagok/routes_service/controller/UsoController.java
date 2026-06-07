package com.tagok.routes_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.dto.response.uso.EstadisticasUsoResponse;
import com.tagok.routes_service.service.application.UsoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/uso")
@RequiredArgsConstructor
public class UsoController
{
    private final UsoService usoService;

    /** KPIs de producto agregados (consultas de ruta y estimaciones de tarifa) — CU18. */
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasUsoResponse> getEstadisticas()
    {
        return ResponseEntity.ok(usoService.obtenerEstadisticas());
    }
}
