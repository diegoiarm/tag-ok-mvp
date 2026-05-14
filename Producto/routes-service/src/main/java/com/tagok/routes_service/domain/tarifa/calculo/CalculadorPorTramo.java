package com.tagok.routes_service.domain.tarifa.calculo;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.tarifa.Tarifa;
import com.tagok.routes_service.domain.tramo.Tramo;
import com.tagok.routes_service.repository.TramoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CalculadorPorTramo implements CalculadorTarifaStrategy
{
    private final TramoRepository tramoRepository;

    @Override
    public Optional<Tarifa> calcular(CalculoContexto ctx)
    {
        if (ctx.getEntrada() == null || ctx.getSalida() == null)
            return Optional.empty();

        System.out.println("Buscando tramos para | Entrada: " + ctx.getEntrada().getCodigo() + " Salida: " + ctx.getSalida().getCodigo());

        Optional<Tramo> tramoOpt = tramoRepository.findByEntradaAndSalida(ctx.getEntrada(), ctx.getSalida());

        if (tramoOpt.isEmpty()) 
        {
            tramoOpt = tramoRepository.findByEntradaAndSalida(ctx.getSalida(), ctx.getEntrada());
        }

        Tramo tramo = tramoOpt
            //.orElseThrow(() -> new IllegalStateException("No existe el tramo asociado a los porticos"));
            .orElse(null);

        if (tramo == null || tramo.getCalendario() == null || tramo.getReglas().isEmpty())
            return Optional.empty();

        var reglaOpt = tramo.getReglas().stream()
            .filter(r -> r.aplicaATipo(ctx.getVehiculo()))
            .findFirst();

        if (reglaOpt.isEmpty()) 
            return Optional.empty();

        var tipoTarifa = tramo.getCalendario().obtenerTipoTarifa(ctx.getFecha());
        var monto = reglaOpt.get().obtenerValor(tipoTarifa).getValor();

        return Optional.of(new Tarifa(monto, tipoTarifa));
    }
    
}
