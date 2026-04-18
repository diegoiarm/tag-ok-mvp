package com.tagok.routes_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.domain.dto.response.PorticoResponse;
import com.tagok.routes_service.service.PorticoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/porticos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PorticoController 
{
    private final PorticoService porticoService;

    @GetMapping
    public ResponseEntity<List<PorticoResponse>> getAll()
    {
        return ResponseEntity.ok(porticoService.findAll());
    }
}
