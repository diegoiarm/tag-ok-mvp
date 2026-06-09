package com.tagok.history_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.history_service.controller.dto.BoletaRequest;
import com.tagok.history_service.dto.BoletaDTO;
import com.tagok.history_service.security.CurrentUserService;
import com.tagok.history_service.service.BoletaService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/v1/boleta")
@RestController
@RequiredArgsConstructor
public class BoletaController 
{
    private final BoletaService boletaService;
    private final CurrentUserService currentUserService;

    @PostMapping("/obtener")
    public ResponseEntity<BoletaDTO> generarBoleta(@RequestBody BoletaRequest request) 
    {
        return ResponseEntity.ok(boletaService.generarBoleta(currentUserService.getUserId(), request));
    }
}
