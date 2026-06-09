package com.tagok.history_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tagok.history_service.controller.dto.BoletaRequest;
import com.tagok.history_service.dto.BoletaDTO;
import com.tagok.history_service.dto.BoletaItemDTO;
import com.tagok.history_service.repository.BoletaRepository;
import com.tagok.history_service.repository.boletaProyeccion.ProyeccionBoletaItem;
import com.tagok.history_service.repository.boletaProyeccion.ProyeccionBoletaTotal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoletaService 
{

    private final BoletaRepository boletaRepository;
    
    public BoletaDTO generarBoleta(String userId, BoletaRequest request) 
    {
        LocalDate fechaDesde = request.getFechaDesde();
        LocalDate fechaHasta = request.getFechaHasta();
        
        int añoDesde = fechaDesde.getYear();
        int añoHasta = fechaHasta.getYear();
        int mesDesde = fechaDesde.getMonthValue();
        int mesHasta = fechaHasta.getMonthValue();
        
        List<String> autopistas = request.getAutopistas() != null ? 
            request.getAutopistas() : Collections.emptyList();
        
        // Obtener items de la boleta
        List<ProyeccionBoletaItem> proyecciones = boletaRepository.findBoletaItems(
            userId,
            añoDesde, añoHasta,
            mesDesde, mesHasta,
            fechaDesde, fechaHasta,
            request.getPatente(),
            autopistas);
        
        List<BoletaItemDTO> items = proyecciones.stream()
            .map(item -> BoletaItemDTO.builder()
                .fecha(item.getFecha())
                .autopista(item.getAutopista())
                .nombre(item.getNombre())
                .tipoTarifa(item.getTipoTarifa())
                .valor(item.getValor() != null ? item.getValor() : BigDecimal.ZERO)
                .horaCruce(item.getHoraFechaCruce() != null ? 
                    item.getHoraFechaCruce().toString() : "")
                .build())
            .collect(Collectors.toList());
        
        ProyeccionBoletaTotal totalProyeccion = boletaRepository.findBoletaTotal(
            userId,
            añoDesde, añoHasta,
            mesDesde, mesHasta,
            fechaDesde, fechaHasta,
            request.getPatente(),
            autopistas);
        
        BigDecimal total = totalProyeccion != null && totalProyeccion.getTotal() != null ? 
            totalProyeccion.getTotal() : BigDecimal.ZERO;
        
        return BoletaDTO.builder()
            .patente(request.getPatente())
            .fechaDesde(fechaDesde)
            .fechaHasta(fechaHasta)
            .items(items)
            .total(total)
            .build();
    }
}