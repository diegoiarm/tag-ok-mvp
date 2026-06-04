package com.tagok.history_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tagok.history_service.document.CruceSnapshot;
import com.tagok.history_service.document.HistorialAnualDocument;
import com.tagok.history_service.document.HistorialDiarioSnapshot;
import com.tagok.history_service.document.HistorialMensualSnapshot;
import com.tagok.history_service.event.dtos.HistorialCruceEvent;
import com.tagok.history_service.repository.HistorialAnualRepository;
import com.tagok.history_service.repository.ProyeccionAnual;
import com.tagok.history_service.repository.ProyeccionPatente;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistorialService 
{
    private final HistorialAnualRepository historialAnualRepository;

    public List<HistorialAnualDocument> getAll()
    {
        return historialAnualRepository.findAll();
    }

    public void saveEvent(HistorialCruceEvent event)
    {
        Map<Integer, Map<LocalDate, List<CruceSnapshot>>> crucesPorAño =
            event.getCruces()
                .stream()
                .map(CruceSnapshot::fromEvent)
                .collect(Collectors.groupingBy(
                    c -> c.getHoraFechaCruce().getYear(),
                    Collectors.groupingBy(
                        c -> c.getHoraFechaCruce().toLocalDate()
                    )
                ));

        for (var entry : crucesPorAño.entrySet())
        {
            int año = entry.getKey();

            String id =
                HistorialAnualDocument.generateId(
                    event.getUsuarioId(),
                    año
                );

            HistorialAnualDocument historial =
                historialAnualRepository.findById(id)
                    .orElseGet(() ->
                        HistorialAnualDocument.createNewEmpty(
                            event.getUsuarioId(),
                            año
                        )
                    );

            entry.getValue().forEach(historial::registrarCruces);

            historialAnualRepository.save(historial);
        }
    }

    public List<ProyeccionAnual> getResumenAnual(String usuarioId) 
    {
        return historialAnualRepository.findResumenAnual(usuarioId);
    }

    public List<Integer> getAvaliableYears(String usuarioId)
    {
        return historialAnualRepository.findAvailableYears(usuarioId);
    }

    public Optional<HistorialAnualDocument> getByUsuarioIdAndAño(String usuarioId, int año) 
    {
        return historialAnualRepository.findById(HistorialAnualDocument.generateId(usuarioId, año));
    }

    public Page<HistorialAnualDocument> getAllPaged(Pageable pageable)
    {
        return historialAnualRepository.findAll(pageable);
    }

    public Optional<HistorialMensualSnapshot> getMesEspecifico(String usuarioId, int año, int mes)
    {
        return getByUsuarioIdAndAño(usuarioId, año)
            .flatMap(historial -> historial.getMeses().stream()
                .filter(m -> m.getMes() == mes)
                .findFirst());
    }

    public Optional<HistorialDiarioSnapshot> getDiaEspecifico(String usuarioId, int año, int mes, int dia) 
    {
        return getMesEspecifico(usuarioId, año, mes)
            .flatMap(mensual -> mensual.getDias()
                .stream()
                .filter(d -> d.getFecha().getDayOfMonth() == dia)
                .findFirst()
            );
    }

    public List<String> getPatentesUnicas(String usuarioId) 
    {
        return historialAnualRepository.findPatentesUnicas(usuarioId)
            .stream()
            .map(ProyeccionPatente::getPatente)
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .toList();
    }
}
