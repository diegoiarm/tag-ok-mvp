package com.tagok.history_service.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tagok.history_service.controller.dto.FiltroHistorialRequest;
import com.tagok.history_service.document.HistorialAnualDocument;
import com.tagok.history_service.document.HistorialDiarioSnapshot;
import com.tagok.history_service.document.HistorialMensualSnapshot;
import com.tagok.history_service.dto.CruceDetalleDTO;
import com.tagok.history_service.dto.DetalleDiaDTO;
import com.tagok.history_service.dto.DetalleMensualDTO;
import com.tagok.history_service.dto.DiaResumenDTO;
import com.tagok.history_service.dto.ResumenAnualDTO;
import com.tagok.history_service.repository.historialProyeccion.ProyeccionAnual;
import com.tagok.history_service.security.CurrentUserService;
import com.tagok.history_service.service.HistorialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/historial")
public class HistorialController 
{
    private final HistorialService historialService;
    private final CurrentUserService currentUser;

    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getAvailableYears() 
    {
        return ResponseEntity.ok(historialService.getAvaliableYears(currentUser.getUserId()));
    }

    // Endpoint: resumen de todos los años (sin detalles diarios)
    @GetMapping("/resumen")
    public ResponseEntity<List<ResumenAnualDTO>> getResumenAnual() 
    {
        return ResponseEntity.ok(historialService.getResumenAnual(currentUser.getUserId()).stream()
            .map(this::toResumenDTO)
            .toList()
        );
    }

    @GetMapping("/year/{año}")
    public ResponseEntity<ResumenAnualDTO> getDetalleAnual(@PathVariable int año) 
    {
        return historialService.getByUsuarioIdAndAño(currentUser.getUserId(), año)
            .map(historial -> toResumenDTOCompleto(historial))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/year/{año}/month/{mes}")
    public ResponseEntity<?> getDetalleMensual(@PathVariable int año, @PathVariable int mes) 
    {
        return historialService.getMesEspecifico(currentUser.getUserId(), año, mes)
            .map(mensual -> toDetalleMensualDTO(mensual, año))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/year/{año}/month/{mes}/day/{dia}")
    public ResponseEntity<DetalleDiaDTO> getDetalleDia(@PathVariable int año,@PathVariable int mes, @PathVariable int dia) 
    {
        return historialService.getDiaEspecifico(currentUser.getUserId(), año, mes, dia)
            .map(diario -> toDetalleDiaDTO(diario, año, mes))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patentes")
    public ResponseEntity<List<String>> getPatentes() 
    {
        return ResponseEntity.ok(historialService.getPatentesUnicas(currentUser.getUserId()));
    }

    @GetMapping("/autopistas")
    public ResponseEntity<List<String>> getAutopistas() 
    {
        return ResponseEntity.ok(historialService.getAutopistasUnicas(currentUser.getUserId()));
    }

    @PostMapping("/resumen-filtrado")
    public ResponseEntity<List<ResumenAnualDTO>> getResumenAnualFiltrado(
        @RequestBody FiltroHistorialRequest filtro) 
    {
        
        return ResponseEntity.ok(historialService.getResumenAnualFiltrado(currentUser.getUserId(), filtro).stream()
            .map(this::toResumenDTO)
            .toList()
        );
    }

    // ── Mapeadores ──────────────────────────────────────────────────────────
    
    private ResumenAnualDTO toResumenDTO(ProyeccionAnual projection) 
    {
        return ResumenAnualDTO.builder()
            .año(projection.getAño())
            .cantidadCruces(projection.getCantidadCruces())
            .totalAño(projection.getTotalAño())
            .mesesDisponibles(projection.getMesesDisponibles())
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

    private DetalleDiaDTO toDetalleDiaDTO(HistorialDiarioSnapshot diario, int año, int mes) 
    {
        List<CruceDetalleDTO> cruces = diario.getCruces().stream()
            .map(cruce -> CruceDetalleDTO.builder()
                .codigo(cruce.getCodigo())
                .nombre(cruce.getNombre())
                .autopista(cruce.getAutopista())
                .tipoTarifa(cruce.getTipoTarifa())
                .valor(cruce.getValor())
                .tipoVehiculo(cruce.getTipoVehiculo())
                .patente(cruce.getPatente())
                .horaFechaCruce(cruce.getHoraFechaCruce())
                .build())
            .collect(Collectors.toList());

        return DetalleDiaDTO.builder()
            .año(año)
            .mes(mes)
            .dia(diario.getFecha().getDayOfMonth())
            .totalDia(diario.getTotalDia())
            .cantidadCruces(diario.getCantidadCruces())
            .cruces(cruces)
            .build();
    }
}
