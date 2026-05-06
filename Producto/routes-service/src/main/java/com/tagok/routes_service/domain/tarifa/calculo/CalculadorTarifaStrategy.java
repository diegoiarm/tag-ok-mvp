package com.tagok.routes_service.domain.tarifa.calculo;

import java.util.Optional;

import com.tagok.routes_service.domain.tarifa.Tarifa;

public interface CalculadorTarifaStrategy 
{
    Optional<Tarifa> calcular(CalculoContexto contexto);
}
