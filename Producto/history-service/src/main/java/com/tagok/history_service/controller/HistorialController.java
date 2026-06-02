package com.tagok.history_service.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.history_service.document.HistorialAnualDocument;
import com.tagok.history_service.document.HistorialMensualSnapshot;
import com.tagok.history_service.dto.DetalleMensualDTO;
import com.tagok.history_service.dto.DiaResumenDTO;
import com.tagok.history_service.dto.ResumenAnualDTO;
import com.tagok.history_service.repository.ProyeccionAnual;
import com.tagok.history_service.service.HistorialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/historial")
public class HistorialController 
{
    private final HistorialService historialService;

    @GetMapping("/{usuarioId}/years")
    public ResponseEntity<List<Integer>> getAvailableYears(@PathVariable String usuarioId) 
    {
        return ResponseEntity.ok(historialService.getAvaliableYears(usuarioId));
    }

    // Endpoint: resumen de todos los años (sin detalles diarios)
    @GetMapping("/{usuarioId}/resumen")
    public ResponseEntity<List<ResumenAnualDTO>> getResumenAnual(@PathVariable String usuarioId) 
    {
        return ResponseEntity.ok(historialService.getResumenAnual(usuarioId).stream()
            .map(this::toResumenDTO)
            .toList()
        );
    }

    @GetMapping("/{usuarioId}/year/{año}")
    public ResponseEntity<ResumenAnualDTO> getDetalleAnual(@PathVariable String usuarioId, @PathVariable int año) 
    {
        return historialService.getByUsuarioIdAndAño(usuarioId, año)
            .map(historial -> toResumenDTOCompleto(historial))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{usuarioId}/year/{año}/month/{mes}")
    public ResponseEntity<?> getDetalleMensual(@PathVariable String usuarioId, @PathVariable int año, @PathVariable int mes) 
    {
        return historialService.getMesEspecifico(usuarioId, año, mes)
            .map(mensual -> toDetalleMensualDTO(mensual, año))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── Mapeadores ──────────────────────────────────────────────────────────
    
    private ResumenAnualDTO toResumenDTO(ProyeccionAnual projection) 
    {
        return ResumenAnualDTO.builder()
            .año(projection.getAño())
            .cantidadCruces(projection.getCantidadCruces())
            .totalAño(projection.getTotalAño())
            .cargadoCompleto(false)
            .build();
    }

    private ResumenAnualDTO toResumenDTOCompleto(HistorialAnualDocument historial) 
    {
        List<Integer> mesesDisponibles = historial.getMeses().stream()
            .map(HistorialMensualSnapshot::getMes)
            .sorted()
            .collect(Collectors.toList());

        return ResumenAnualDTO.builder()
            .año(historial.getAño())
            .cantidadCruces(historial.getCantidadCruces())
            .totalAño(historial.getTotalAño())
            .mesesDisponibles(mesesDisponibles)
            .cargadoCompleto(true)
            .build();
    }

    private DetalleMensualDTO toDetalleMensualDTO(HistorialMensualSnapshot mensual, int año) {
        List<DiaResumenDTO> dias = mensual.getDias().stream()
            .map(dia -> DiaResumenDTO.builder()
                .dia(dia.getFecha().getDayOfMonth())
                .cantidadCruces(dia.getCruces().size())
                .totalDia(dia.getCruces().stream()
                    .map(cruce -> cruce.getValor() != null ? cruce.getValor() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build())
            .sorted((d1, d2) -> Integer.compare(d1.getDia(), d2.getDia()))
            .collect(Collectors.toList());

        BigDecimal totalMes = dias.stream()
            .map(DiaResumenDTO::getTotalDia)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DetalleMensualDTO.builder()
            .año(año)
            .mes(mensual.getMes())
            .dias(dias)
            .totalMes(totalMes)
            .build();
    }
}
