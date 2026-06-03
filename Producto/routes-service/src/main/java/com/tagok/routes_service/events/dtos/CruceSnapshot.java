package com.tagok.routes_service.events.dtos;

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
public class CruceSnapshot 
{
    private String codigo;
    private String nombre;
    private String autopista;

    private String tipoTarifa;

    private BigDecimal valor;

    private String tipoVehiculo;

    private String patente;

    LocalDateTime horaFechaCruce;
}
