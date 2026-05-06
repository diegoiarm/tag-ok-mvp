package com.tagok.routes_service.domain.tarifa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.tagok.routes_service.domain.calendario.CalendarioTarifario;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

public class CalculadorTarifa
{
    public Optional<Tarifa> calcular(
        CalendarioTarifario calendario,
        List<ReglaTarifaria> reglas,
        TipoVehiculo vehiculo,
        LocalDateTime fecha)
    {
        if (calendario == null || reglas == null || reglas.isEmpty())
            return Optional.empty();

        return reglas.stream()
            .filter(r -> r.aplicaATipo(vehiculo))
            .findFirst()
            .map(regla -> 
            {
                var tipoTarifa = calendario.obtenerTipoTarifa(fecha);
                var monto = regla.obtenerValor(tipoTarifa).getValor();
                return new Tarifa(monto, tipoTarifa);
            });
    }
}
