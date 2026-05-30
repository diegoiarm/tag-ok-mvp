package com.tagok.history_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.history_service.document.HistorialMensualDocument;
import com.tagok.history_service.service.HistorialMensualService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historial")
public class HistorialMensualController 
{
    private final HistorialMensualService historialMensualService;

    @GetMapping
    public ResponseEntity<List<HistorialMensualDocument>> getAll()
    {
        return ResponseEntity.ok(historialMensualService.getAll());
    }
}
