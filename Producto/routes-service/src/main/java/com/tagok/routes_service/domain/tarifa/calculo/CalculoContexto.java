package com.tagok.routes_service.domain.tarifa.calculo;

import java.time.LocalDateTime;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculoContexto 
{
    private Autopista autopista;

    private TipoVehiculo vehiculo;
    private LocalDateTime fecha;

    private Portico portico;

    private Portico entrada;
    private Portico salida;
}
