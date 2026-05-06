package com.tagok.routes_service.domain.tarifa.calculo;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.autopista.TipoCobro;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CalculadorTarifaFactory 
{
    private final CalculadorPorPortico porPortico;

    public CalculadorTarifaStrategy getStrategy(Autopista autopista)
    {
        if (autopista.getTipoCobro() == TipoCobro.PORTICO)
            return porPortico;

        throw new UnsupportedOperationException("Tipo de cobro no soportado aún");
    }
}
