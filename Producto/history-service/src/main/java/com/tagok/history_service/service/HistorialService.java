package com.tagok.history_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tagok.history_service.controller.dto.FiltroHistorialRequest;
import com.tagok.history_service.document.CruceSnapshot;
import com.tagok.history_service.document.HistorialAnualDocument;
import com.tagok.history_service.document.HistorialDiarioSnapshot;
import com.tagok.history_service.document.HistorialMensualSnapshot;
import com.tagok.history_service.dto.CruceDetalleDTO;
import com.tagok.history_service.dto.DetalleDiaDTO;
import com.tagok.history_service.dto.EstadisticasGlobalesDTO;
import com.tagok.history_service.dto.EstadisticasGlobalesDTO.PuntoAnual;
import com.tagok.history_service.event.dtos.HistorialCruceEvent;
import com.tagok.history_service.repository.HistorialAnualRepository;
import com.tagok.history_service.repository.historialProyeccion.ProyeccionAnual;
import com.tagok.history_service.repository.historialProyeccion.ProyeccionAutopista;
import com.tagok.history_service.repository.historialProyeccion.ProyeccionPatente;

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

    private boolean cumpleFiltro(
        CruceSnapshot cruce,
        List<String> patentes,
        List<String> autopistas)
    {
        boolean patenteOk =
            patentes == null ||
            patentes.isEmpty() ||
            patentes.contains(cruce.getPatente());

        boolean autopistaOk =
            autopistas == null ||
            autopistas.isEmpty() ||
            autopistas.contains(cruce.getAutopista());

        return patenteOk && autopistaOk;
    }

    public Optional<HistorialMensualSnapshot> getMesEspecifico(String usuarioId, int año, int mes)
    {
        return getByUsuarioIdAndAño(usuarioId, año)
            .flatMap(historial -> historial.getMeses().stream()
                .filter(m -> m.getMes() == mes)
                .findFirst());
    }

    public Optional<HistorialMensualSnapshot> getMesFiltrado(
        String usuarioId,
        int año,
        int mes,
        FiltroHistorialRequest filtro)
    {
        return getMesEspecifico(usuarioId, año, mes)
            .map(snapshot -> {

                List<HistorialDiarioSnapshot> diasFiltrados =
                    snapshot.getDias()
                        .stream()
                        .map(dia -> {

                            List<CruceSnapshot> crucesFiltrados =
                                dia.getCruces()
                                    .stream()
                                    .filter(c -> cumpleFiltro(
                                        c,
                                        filtro.getPatentes(),
                                        filtro.getAutopistas()))
                                    .toList();

                            BigDecimal totalDia =
                                crucesFiltrados.stream()
                                    .map(CruceSnapshot::getValor)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            return HistorialDiarioSnapshot.builder()
                                .fecha(dia.getFecha())
                                .totalDia(totalDia)
                                .cantidadCruces(crucesFiltrados.size())
                                .cruces(crucesFiltrados)
                                .build();
                        })
                        .filter(d -> d.getCantidadCruces() > 0)
                        .toList();

                BigDecimal totalMes =
                    diasFiltrados.stream()
                        .map(HistorialDiarioSnapshot::getTotalDia)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                int cantidadCruces =
                    diasFiltrados.stream()
                        .mapToInt(HistorialDiarioSnapshot::getCantidadCruces)
                        .sum();

                return HistorialMensualSnapshot.builder()
                    .mes(snapshot.getMes())
                    .totalMes(totalMes)
                    .cantidadCruces(cantidadCruces)
                    .dias(diasFiltrados)
                    .build();
            });
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

    public Optional<HistorialDiarioSnapshot> getDiaFiltrado(
        String usuarioId,
        int año,
        int mes,
        int dia,
        FiltroHistorialRequest filtro)
    {
        return getDiaEspecifico(usuarioId, año, mes, dia)
            .map(snapshot -> {

                List<CruceSnapshot> crucesFiltrados =
                    snapshot.getCruces()
                        .stream()
                        .filter(c -> cumpleFiltro(
                            c,
                            filtro.getPatentes(),
                            filtro.getAutopistas()))
                        .toList();

                BigDecimal totalDia =
                    crucesFiltrados.stream()
                        .map(CruceSnapshot::getValor)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                return HistorialDiarioSnapshot.builder()
                    .fecha(snapshot.getFecha())
                    .totalDia(totalDia)
                    .cantidadCruces(crucesFiltrados.size())
                    .cruces(crucesFiltrados)
                    .build();
            });
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

    public List<String> getAutopistasUnicas(String usuarioId) 
    {
        return historialAnualRepository.findAutopistasUnicas(usuarioId)
            .stream()
            .map(ProyeccionAutopista::getAutopista)
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .toList();
    }

    /**
     * Agrega el uso del historial de todos los usuarios para la reportería admin (CU18):
     * total de cruces, gasto acumulado, usuarios con cruces y serie por año.
     */
    public EstadisticasGlobalesDTO getEstadisticasGlobales()
    {
        long totalCruces = 0;
        BigDecimal totalGasto = BigDecimal.ZERO;
        long usuariosConCruces = 0;

        // año -> [cruces, gasto]
        Map<Integer, long[]> crucesPorAnio = new TreeMap<>();
        Map<Integer, BigDecimal> gastoPorAnio = new TreeMap<>();

        for (HistorialAnualDocument doc : historialAnualRepository.findAll())
        {
            int cruces = doc.getCantidadCruces();
            BigDecimal gasto = doc.getTotalAño() != null ? doc.getTotalAño() : BigDecimal.ZERO;

            totalCruces += cruces;
            totalGasto = totalGasto.add(gasto);
            if (cruces > 0) usuariosConCruces++;

            crucesPorAnio.computeIfAbsent(doc.getAño(), a -> new long[1])[0] += cruces;
            gastoPorAnio.merge(doc.getAño(), gasto, BigDecimal::add);
        }

        List<PuntoAnual> porAnio = new ArrayList<>();
        for (var entry : crucesPorAnio.entrySet())
        {
            porAnio.add(PuntoAnual.builder()
                .año(entry.getKey())
                .cruces(entry.getValue()[0])
                .gasto(gastoPorAnio.getOrDefault(entry.getKey(), BigDecimal.ZERO))
                .build());
        }

        return EstadisticasGlobalesDTO.builder()
            .totalCruces(totalCruces)
            .totalGasto(totalGasto)
            .usuariosConCruces(usuariosConCruces)
            .porAnio(porAnio)
            .build();
    }

    public List<ProyeccionAnual> getResumenAnualFiltrado(String usuarioId, FiltroHistorialRequest filtro)
    {
        List<String> patentesFiltro = filtro.getPatentes() != null ? filtro.getPatentes() : List.of();
        List<String> autopistasFiltro = filtro.getAutopistas() != null ? filtro.getAutopistas() : List.of();
        
        return historialAnualRepository.findResumenAnualFiltrado(usuarioId, patentesFiltro, autopistasFiltro);
    }
}
