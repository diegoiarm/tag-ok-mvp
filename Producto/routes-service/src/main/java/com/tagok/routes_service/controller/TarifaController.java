package com.tagok.routes_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.domain.uso.TipoEventoUso;
import com.tagok.routes_service.dto.request.tarifa.TarifaPorticoCruzado;
import com.tagok.routes_service.security.CurrentUserService;
import com.tagok.routes_service.service.application.TarifaService;
import com.tagok.routes_service.service.application.UsoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/tarifas")
@RequiredArgsConstructor
public class TarifaController
{
    private final TarifaService tarifaService;
    private final CurrentUserService currentUserService;
    private final UsoService usoService;

    @PostMapping
    public ResponseEntity<TarifaCalculada> calcularTarifaCruce(@RequestBody TarifaPorticoCruzado request)
    {
        String userId = currentUserService.getUserId();
        var tarifa = tarifaService.calcularCruceTarifa(request, userId);
        usoService.registrar(TipoEventoUso.ESTIMACION_TARIFA, userId);

        return ResponseEntity.ok(tarifa);
    }
}
