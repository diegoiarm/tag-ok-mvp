package com.tagok.routes_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.dto.request.tarifa.TarifaPorticoCruzado;
import com.tagok.routes_service.service.application.TarifaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/tarifas")
@RequiredArgsConstructor
public class TarifaController
{
    private final TarifaService tarifaService;

    @PostMapping
    public ResponseEntity<TarifaCalculada> calcularTarifaCruce(@RequestBody TarifaPorticoCruzado request)
    {
        var tarifa = tarifaService.calcularCruceTarifa(request);

        return ResponseEntity.ok(tarifa);
    }
}
