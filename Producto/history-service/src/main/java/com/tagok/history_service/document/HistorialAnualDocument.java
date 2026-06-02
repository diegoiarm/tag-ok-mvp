package com.tagok.history_service.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document("historial_anual")
@Data
@Builder
public class HistorialAnualDocument 
{
    @Id
    private String id;

    private String usuarioId;

    private int año;

    @Builder.Default
    private int cantidadCruces = 0;

    @Builder.Default
    private BigDecimal totalAño = BigDecimal.ZERO;

    @Builder.Default
    private List<HistorialMensualSnapshot> meses = new ArrayList<>();

    public static HistorialAnualDocument createNewEmpty(String usuarioId, int año)
    {
        return HistorialAnualDocument.builder()
            .id(generateId(usuarioId, año))
            .usuarioId(usuarioId)
            .año(año)
            .build();
    }

    public static String generateId(String usuarioId, int año)
    {
        return usuarioId + "-" + año;
    }

    public void registrarCruces(LocalDate fecha, List<CruceSnapshot> cruces)
    {
        int mes = fecha.getMonthValue();

        HistorialMensualSnapshot mensual = meses.stream()
            .filter(m -> m.getMes() == mes)
            .findFirst()
            .orElseGet(() -> 
            {
                HistorialMensualSnapshot nuevoMes = HistorialMensualSnapshot.builder()
                    .mes(mes)
                    .build();

                meses.add(nuevoMes);
                
                return nuevoMes;
            });

        mensual.registrarDia(fecha, cruces);
        cantidadCruces += cruces.size();
        totalAño = totalAño.add(cruces.stream().map(CruceSnapshot::getValor).reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
