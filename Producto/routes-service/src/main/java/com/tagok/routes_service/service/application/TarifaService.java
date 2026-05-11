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
import com.tagok.routes_service.domain.tarifa.Tarifa;
import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.domain.tarifa.calculo.CalculadorTarifaFactory;
import com.tagok.routes_service.domain.tarifa.calculo.CalculoContexto;
import com.tagok.routes_service.dto.request.tarifa.TarifaRequest;
import com.tagok.routes_service.repository.PorticoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService
{
    private final PorticoRepository porticoRepository;
    private final CalculadorTarifaFactory calculadorFactory;

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

            var strategy = calculadorFactory.getStrategy(portico.getAutopista());

            var contexto = CalculoContexto.builder()
                .autopista(portico.getAutopista())
                .portico(portico)
                .vehiculo(request.vehiculo())
                .fecha(fechaHora)
                .build();

            Optional<Tarifa> tarifaOpt = strategy.calcular(contexto);
            
            if (tarifaOpt.isPresent()) 
            {
                Tarifa tarifa = tarifaOpt.get();

                total = total.add(tarifa.monto());

                cruces.add(new Cruce(
                    portico.getId(),
                    portico.getCodigo(),
                    portico.getNombre(),
                    portico.getAutopista() != null ? portico.getAutopista().getNombre() : null,
                    tarifa.tipoTarifa(),
                    tarifa.monto(),
                    fechaHora
                ));
            }
        }

        return new TarifaCalculada(total, cruces, request.vehiculo());
    }
}