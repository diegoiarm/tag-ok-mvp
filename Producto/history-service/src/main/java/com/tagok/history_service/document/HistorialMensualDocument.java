package com.tagok.history_service.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document("historial_mensual")
@Builder
@Data
public class HistorialMensualDocument 
{
    @Id
    private String id;

    private String userId;

    private int año;
    private int mes;

    private BigDecimal totalMes;
    
    private int cantidadCruces;

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
