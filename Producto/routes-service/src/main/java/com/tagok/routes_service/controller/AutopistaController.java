package com.tagok.routes_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.dto.request.autopista.AutopistaRequest;
import com.tagok.routes_service.dto.request.autopista.AutopistaUpdateRequest;
import com.tagok.routes_service.dto.response.autopista.AutopistaResponse;
import com.tagok.routes_service.service.application.AutopistaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/autopistas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AutopistaController 
{
    private final AutopistaService autopistaService;

    @PostMapping
    public ResponseEntity<AutopistaResponse> create(@Valid @RequestBody AutopistaRequest request)
    {
        var response = autopistaService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/import")
    public ResponseEntity<AutopistaResponse> importar(@RequestBody AutopistaRequest request)
    {
        var response = autopistaService.saveAutopistaWithPorticos(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutopistaResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody AutopistaUpdateRequest request)
    {
        return ResponseEntity.ok(autopistaService.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<AutopistaResponse>> getAll()
    {
        return ResponseEntity.ok(autopistaService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) 
    {
        autopistaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll()
    {
        autopistaService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}