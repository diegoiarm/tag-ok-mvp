package com.tagok.history_service.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HistorialMensualSnapshot 
{
    private int mes;

    @Builder.Default
    private BigDecimal totalMes = BigDecimal.ZERO;
    
    @Builder.Default
    private int cantidadCruces = 0;

    @Builder.Default
    private List<HistorialDiarioSnapshot> dias = new ArrayList<>();

    public void registrarDia(LocalDate fecha, List<CruceSnapshot> cruces)
    {
        HistorialDiarioSnapshot dia = dias.stream()
            .filter(d -> d.getFecha().equals(fecha))
            .findFirst()
            .orElse(null);

        BigDecimal totalDia = cruces.stream()
            .map(CruceSnapshot::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (dia == null)
        {
            dia = HistorialDiarioSnapshot.builder()
                .fecha(fecha)
                .totalDia(totalDia)
                .cantidadCruces(cruces.size())
                .cruces(new ArrayList<>(cruces))
                .build();

            dias.add(dia);
        }
        else
        {
            dia.setTotalDia(dia.getTotalDia().add(totalDia));
            dia.setCantidadCruces(dia.getCantidadCruces() + cruces.size());
            dia.getCruces().addAll(cruces);
        }

        totalMes = totalMes.add(totalDia);
        cantidadCruces += cruces.size();
    }
}
