package com.tagok.routes_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.dto.request.tarifa.TarifaRequest;
import com.tagok.routes_service.service.application.TarifaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tarifas")
@RequiredArgsConstructor
public class TarifaController
{
    private final TarifaService tarifaService;

    @GetMapping("/calcular")
    public TarifaCalculada calcular(@RequestBody TarifaRequest request)
    {
        return tarifaService.calcularTarifa(request);
    }
}
