package com.tagok.history_service.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CruceSnapshotDocument
{
    private String eventoId;
    
    private String codigo;
    private String nombre;
    private String autopista;

    private String tipoTarifa;

    private BigDecimal valor;

    private LocalDateTime horaFechaCruce;

    private String tipoVehiculo;

}