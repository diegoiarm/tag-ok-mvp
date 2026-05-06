package com.tagok.routes_service.domain.tarifa.calculo;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.Tarifa;

@Component
public class CalculadorPorPortico implements CalculadorTarifaStrategy
{
    @Override
    public Optional<Tarifa> calcular(CalculoContexto ctx)
    {
        Portico portico = ctx.getPortico();

        if (portico == null || portico.getCalendario() == null || portico.getReglas().isEmpty())
            return Optional.empty();

        var reglaOpt = portico.getReglas().stream()
            .filter(r -> r.aplicaATipo(ctx.getVehiculo()))
            .findFirst();

        if (reglaOpt.isEmpty())
            return Optional.empty();

        var tipoTarifa = portico.getCalendario()
            .obtenerTipoTarifa(ctx.getFecha());

        var monto = reglaOpt.get()
            .obtenerValor(tipoTarifa)
            .getValor();

        return Optional.of(new Tarifa(monto, tipoTarifa));
    }
}
