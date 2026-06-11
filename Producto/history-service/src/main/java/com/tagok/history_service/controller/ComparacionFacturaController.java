package com.tagok.history_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tagok.history_service.controller.dto.BoletaRequest;
import com.tagok.history_service.dto.ComparacionFacturaDTO;
import com.tagok.history_service.security.CurrentUserService;
import com.tagok.history_service.service.ComparacionFacturaService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/v1/boleta")
@RestController
@RequiredArgsConstructor
public class ComparacionFacturaController
{
    private final ComparacionFacturaService comparacionFacturaService;
    private final CurrentUserService currentUserService;

    /**
     * Compara la boleta generada por la app (para el usuario autenticado,
     * patente y rango de fechas dados) contra la factura del cliente adjunta
     * como PDF o imagen (foto de cámara).
     */
    @PostMapping(value = "/comparar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComparacionFacturaDTO> comparar(
        @RequestPart("archivo") MultipartFile archivo,
        @RequestParam("patente") String patente,
        @RequestParam("fechaDesde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @RequestParam("fechaHasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @RequestParam(value = "autopistas", required = false) List<String> autopistas)
    {
        BoletaRequest request = new BoletaRequest();
        request.setPatente(patente);
        request.setFechaDesde(fechaDesde);
        request.setFechaHasta(fechaHasta);
        request.setAutopistas(autopistas);

        return ResponseEntity.ok(
            comparacionFacturaService.comparar(currentUserService.getUserId(), request, archivo));
    }
}
