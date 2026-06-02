package com.tagok.history_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.history_service.document.HistorialAnualDocument;
import com.tagok.history_service.service.HistorialAnualService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historial")
public class HistorialAnualController 
{
    private final HistorialAnualService historialAnualService;

    @GetMapping
    public ResponseEntity<List<HistorialAnualDocument>> getAll()
    {
        return ResponseEntity.ok(historialAnualService.getAll());
    }
}
