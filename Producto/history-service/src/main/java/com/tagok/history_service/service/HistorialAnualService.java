package com.tagok.history_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tagok.history_service.document.CruceSnapshot;
import com.tagok.history_service.document.HistorialAnualDocument;
import com.tagok.history_service.event.dtos.HistorialCruceEvent;
import com.tagok.history_service.repository.HistorialAnualRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistorialAnualService 
{
    private final HistorialAnualRepository historialAnualRepository;

    public List<HistorialAnualDocument> getAll()
    {
        return historialAnualRepository.findAll();
    }

    public void saveEvent(HistorialCruceEvent event)
    {
        String id = HistorialAnualDocument.generateId(event.getUsuarioId(), event.getFechaGeneracion().getYear());

        HistorialAnualDocument historial = historialAnualRepository.findById(id)
            .orElseGet(() -> HistorialAnualDocument.createNewEmpty(event.getUsuarioId(), event.getFechaGeneracion().getYear()));

        var cruces = getCrucesPorDia(event);

        cruces.forEach(historial::registrarCruces);

        historialAnualRepository.save(historial);
    }

    private Map<LocalDate, List<CruceSnapshot>> getCrucesPorDia(HistorialCruceEvent event)
    {
        return event.getCruces()
            .stream()
            .map(CruceSnapshot::fromEvent)
            .collect(Collectors.groupingBy(c -> c.getHoraFechaCruce().toLocalDate()));
    }
}
