package com.tagok.routes_service.service.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.CalculadorTarifa;
import com.tagok.routes_service.domain.tarifa.Cruce;
import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.dto.request.tarifa.TarifaRequest;
import com.tagok.routes_service.repository.PorticoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService
{
    private final PorticoRepository porticoRepository;
    // El dominio(Negocio) sabe como hacer las transacciones, spring no se encarga de eso
    private final CalculadorTarifa calculadorTarifa = new CalculadorTarifa();

    public TarifaCalculada calcularTarifa(TarifaRequest request)
    {
        Map<Portico, LocalDateTime> porticoFechaMap = request.porticosCruzados().stream()
            .map(c -> 
            {
                Portico portico = porticoRepository.findById(c.porticoId())
                    .orElseThrow(() -> new IllegalArgumentException("Pórtico no encontrado"));
                return Map.entry(portico, c.horaFechaCruce());
            })
            .filter(entry -> 
                entry.getKey().getCalendario() != null &&
                entry.getKey().getReglas() != null &&
                !entry.getKey().getReglas().isEmpty()
            )
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));

        BigDecimal total = BigDecimal.ZERO;
        List<Cruce> cruces = new ArrayList<>(); 

        for (Map.Entry<Portico, LocalDateTime> entry : porticoFechaMap.entrySet()) 
        {
            Portico portico = entry.getKey();
            LocalDateTime fechaHora = entry.getValue();

            Optional<Cruce> cruceOpt = calculadorTarifa.calcular(portico, request.vehiculo(), fechaHora);

            if (cruceOpt.isPresent()) 
            {
                Cruce cruce = cruceOpt.get();

                total = total.add(cruce.valor());
                cruces.add(cruce);
            }
        }

        return new TarifaCalculada(total, cruces, request.vehiculo());
    }
}