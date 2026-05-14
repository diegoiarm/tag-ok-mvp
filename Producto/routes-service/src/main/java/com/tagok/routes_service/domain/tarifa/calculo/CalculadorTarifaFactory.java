package com.tagok.routes_service.domain.tarifa.calculo;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.autopista.Autopista;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CalculadorTarifaFactory 
{
    private final CalculadorPorPortico porPortico;
    private final CalculadorPorTramo porTramo;

    public CalculadorTarifaStrategy getStrategy(Autopista autopista) 
    {
        if (autopista == null)
            throw new IllegalArgumentException("Autopista nula");

        return switch (autopista.getTipoCobro()) 
        {
            case PORTICO -> porPortico;
            case TRAMO   -> porTramo;
        };
    }
}
