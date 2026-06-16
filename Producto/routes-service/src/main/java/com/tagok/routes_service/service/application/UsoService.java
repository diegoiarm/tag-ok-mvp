package com.tagok.routes_service.service.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.uso.EventoUso;
import com.tagok.routes_service.domain.uso.TipoEventoUso;
import com.tagok.routes_service.dto.response.uso.EstadisticasUsoResponse;
import com.tagok.routes_service.dto.response.uso.EstadisticasUsoResponse.PuntoMensual;
import com.tagok.routes_service.repository.EventoUsoRepository;
import com.tagok.routes_service.repository.EventoUsoRepository.ConteoMensualUso;

import io.github.roony11_1.error.core.ErrorHandler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsoService
{
    private final EventoUsoRepository eventoUsoRepository;

    /**
     * Registra un evento de uso. Es best-effort: si el guardado falla no debe
     * interrumpir la operación principal (cálculo de ruta / estimación).
     */
    public void registrar(TipoEventoUso tipo, String usuarioId) 
    {
        try 
        {
            eventoUsoRepository.save(EventoUso.builder()
                .tipo(tipo)
                .usuarioId(usuarioId)
                .build());
        } 
        catch (Exception e) 
        {
            ErrorHandler.toErrorResponse(e);
        }
    }

    public EstadisticasUsoResponse obtenerEstadisticas()
    {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);

        return EstadisticasUsoResponse.builder()
            .totalConsultasRutas(eventoUsoRepository.countByTipo(TipoEventoUso.CONSULTA_RUTA))
            .totalEstimaciones(eventoUsoRepository.countByTipo(TipoEventoUso.ESTIMACION_TARIFA))
            .consultasRutasUltimos30Dias(
                eventoUsoRepository.countByTipoAndFechaAfter(TipoEventoUso.CONSULTA_RUTA, hace30Dias))
            .estimacionesUltimos30Dias(
                eventoUsoRepository.countByTipoAndFechaAfter(TipoEventoUso.ESTIMACION_TARIFA, hace30Dias))
            .porMes(construirSerieMensual())
            .build();
    }

    private List<PuntoMensual> construirSerieMensual()
    {
        // Acumula por clave "YYYY-MM" preservando el orden cronológico del query.
        Map<String, long[]> acumulado = new LinkedHashMap<>();

        for (ConteoMensualUso fila : eventoUsoRepository.contarPorMes())
        {
            String clave = String.format("%04d-%02d", fila.getAnio(), fila.getMes());
            long[] contadores = acumulado.computeIfAbsent(clave, k -> new long[2]);

            if (fila.getTipo() == TipoEventoUso.CONSULTA_RUTA)
                contadores[0] += fila.getTotal();
            else if (fila.getTipo() == TipoEventoUso.ESTIMACION_TARIFA)
                contadores[1] += fila.getTotal();
        }

        List<PuntoMensual> serie = new ArrayList<>();
        for (var entry : acumulado.entrySet())
        {
            serie.add(PuntoMensual.builder()
                .mes(entry.getKey())
                .consultasRutas(entry.getValue()[0])
                .estimaciones(entry.getValue()[1])
                .build());
        }
        return serie;
    }
}
