package com.tagok.routes_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.dto.request.tarifa.TarifaConfigRequest;
import com.tagok.routes_service.dto.response.tarifa.TarifaConfigResponse;
import com.tagok.routes_service.dto.response.tarifa.TramoAdminResponse;
import com.tagok.routes_service.service.application.TramoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/tramos")
@RequiredArgsConstructor
public class TramoController
{
    private final TramoService tramoService;

    @GetMapping
    public ResponseEntity<List<TramoAdminResponse>> getAllForAdmin()
    {
        return ResponseEntity.ok(tramoService.findAllForAdmin());
    }

    @GetMapping("/{id}/tarifas")
    public ResponseEntity<TarifaConfigResponse> getTarifaConfig(@PathVariable Long id)
    {
        return ResponseEntity.ok(tramoService.getTarifaConfig(id));
    }

    @PutMapping("/{id}/tarifas")
    public ResponseEntity<TarifaConfigResponse> actualizarTarifaConfig(
        @PathVariable Long id,
        @RequestBody TarifaConfigRequest request)
    {
        return ResponseEntity.ok(tramoService.actualizarTarifaConfig(id, request));
    }
}
