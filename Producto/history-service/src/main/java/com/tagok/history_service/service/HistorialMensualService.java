package com.tagok.history_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tagok.history_service.document.CruceSnapshot;
import com.tagok.history_service.document.HistorialMensualDocument;
import com.tagok.history_service.event.dtos.HistorialCruceEvent;
import com.tagok.history_service.repository.HistorialMensualRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistorialMensualService 
{
    private final HistorialMensualRepository historialMensualRepository;

    public void saveEvent(HistorialCruceEvent event)
    {
        String id = getHistoryIdFromEvent(event);

        HistorialMensualDocument historial = historialMensualRepository
            .findById(id)
            .orElseGet(() -> createEmptyHistory(event));

        Map<LocalDate, List<CruceSnapshot>> crucesPorDia = event.getCruces()
            .stream()
            .map(CruceSnapshot::fromEvent)
            .collect(Collectors.groupingBy(c -> c.getHoraFechaCruce().toLocalDate()));

        crucesPorDia.forEach(historial::registrarDia);

        historialMensualRepository.save(historial);
    }

    public List<HistorialMensualDocument> getAll()
    {
        return historialMensualRepository.findAll();
    }

    // Inicializador

    private HistorialMensualDocument createEmptyHistory(HistorialCruceEvent event)
    {
        var fecha = LocalDate.now();

        return HistorialMensualDocument.builder()
            .id(event.getUsuarioId() + "-" + fecha.getYear() + "-" + fecha.getMonthValue())
            .userId(event.getUsuarioId())
            .año(fecha.getYear())
            .mes(fecha.getMonthValue())
            .totalMes(BigDecimal.ZERO)
            .cantidadCruces(0)
            .build();
    }

    // Utils

    private String getHistoryIdFromEvent(HistorialCruceEvent event)
    {
        FechaDTO fecha = getAñoAndMes(event.getFechaGeneracion());

        return event.getUsuarioId() + "-" + fecha.año() + "-" + fecha.mes();
    }

    private FechaDTO getAñoAndMes(LocalDateTime fecha)
    {
        return new FechaDTO(fecha.getYear(), fecha.getMonthValue());
    }

    private record FechaDTO(int año, int mes) 
    {
    }
}
