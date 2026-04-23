package com.tagok.routes_service.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;
import com.tagok.routes_service.service.application.TarifaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tarifas")
@RequiredArgsConstructor
public class TarifaController
{
    private final TarifaService tarifaService;

    @GetMapping("/calcular")
    public TarifaCalculada calcular(
        @RequestParam Long porticoId,
        @RequestParam TipoVehiculo vehiculo)
    {
        return tarifaService.calcularTarifa(porticoId, vehiculo, LocalDateTime.now());
    }
}
