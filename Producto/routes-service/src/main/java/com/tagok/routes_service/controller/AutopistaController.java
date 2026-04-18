package com.tagok.routes_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.routes_service.domain.dto.request.AutopistaRequest;
import com.tagok.routes_service.domain.dto.response.AutopistaResponse;
import com.tagok.routes_service.service.AutopistaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/autopistas")
@RequiredArgsConstructor
public class AutopistaController 
{
    private final AutopistaService autopistaService;

    @PostMapping
    public ResponseEntity<AutopistaResponse> create(@RequestBody AutopistaRequest request)
    {
        var response = autopistaService.saveAutopistaWithPorticos(request);
        return ResponseEntity.status(201).body(response);
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
}