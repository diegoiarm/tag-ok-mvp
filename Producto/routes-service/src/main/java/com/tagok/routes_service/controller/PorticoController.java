package com.tagok.routes_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.dto.request.portico.PorticoBulkItem;
import com.tagok.routes_service.dto.request.portico.PorticoCreateRequest;
import com.tagok.routes_service.dto.request.portico.PorticoEstadoRequest;
import com.tagok.routes_service.dto.request.portico.PorticoUpdateRequest;
import com.tagok.routes_service.dto.response.portico.BulkResultResponse;
import com.tagok.routes_service.dto.response.portico.PorticoAdminResponse;
import com.tagok.routes_service.dto.response.portico.PorticoResumenResponse;
import com.tagok.routes_service.dto.response.portico.TollResponse;
import com.tagok.routes_service.service.application.PorticoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/porticos")
@RequiredArgsConstructor
public class PorticoController
{
    private final PorticoService porticoService;

    @GetMapping
    public ResponseEntity<List<PorticoResumenResponse>> getAll()
    {
        return ResponseEntity.ok(porticoService.findAll());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<PorticoAdminResponse>> getAllForAdmin()
    {
        return ResponseEntity.ok(porticoService.findAllForAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TollResponse> getById(@PathVariable long id)
    {
        return ResponseEntity.ok(porticoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PorticoAdminResponse> create(@Valid @RequestBody PorticoCreateRequest request)
    {
        return ResponseEntity.status(201).body(porticoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PorticoAdminResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody PorticoUpdateRequest request)
    {
        return ResponseEntity.ok(porticoService.update(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PorticoAdminResponse> cambiarEstado(
        @PathVariable Long id,
        @Valid @RequestBody PorticoEstadoRequest request)
    {
        return ResponseEntity.ok(porticoService.cambiarEstado(id, request.activo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id)
    {
        porticoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkResultResponse> crearMasivo(@RequestBody List<PorticoBulkItem> items)
    {
        return ResponseEntity.ok(porticoService.crearMasivo(items));
    }
}
