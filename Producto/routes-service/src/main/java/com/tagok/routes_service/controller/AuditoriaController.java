package com.tagok.routes_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.dto.response.auditoria.RegistroAuditoriaResponse;
import com.tagok.routes_service.service.application.AuditoriaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auditoria")
@RequiredArgsConstructor
public class AuditoriaController
{
    private final AuditoriaService auditoriaService;

    /** Bitácora de cambios administrativos (acción + usuario) — CU18. */
    @GetMapping
    public ResponseEntity<List<RegistroAuditoriaResponse>> listar()
    {
        return ResponseEntity.ok(auditoriaService.listar());
    }
}
