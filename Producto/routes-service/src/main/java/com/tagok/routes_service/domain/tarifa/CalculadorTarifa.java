package com.tagok.routes_service.domain.tarifa;

import java.time.LocalDateTime;
import java.util.Optional;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

public class CalculadorTarifa
{
    public Optional<Cruce> calcular(
        Portico portico,
        TipoVehiculo vehiculo,
        LocalDateTime fechaHora)
    {
        return portico.calcularTarifa(vehiculo, fechaHora)
            .map(tarifa -> 
                new Cruce(
                    portico.getId(), 
                    portico.getCodigo(), 
                    portico.getNombre(),
                    portico.getAutopista() != null 
                        ? portico.getAutopista().getNombre() 
                        : null,
                    tarifa.tipoTarifa(),
                    tarifa.monto(),
                    fechaHora
                )
            );
    }
}
