package com.tagok.history_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CruceDetalleDTO 
{
    private String codigo;
    private String nombre;
    private String autopista;
    private String tipoTarifa;
    private BigDecimal valor;
    private String tipoVehiculo;
    private String patente;
    private LocalDateTime horaFechaCruce;
}
