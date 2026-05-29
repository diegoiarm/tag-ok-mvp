package com.tagok.routes_service.domain.tarifa.calculo;

import java.util.Map;
import java.util.Set;

import com.tagok.routes_service.domain.portico.Portico;

public final class PorticoTramoPortico
{
    private static final Map<String, Set<String>> PORTICOS_ESPECIALES = Map.of(
        "AVO1", Set.of("P110"));

    public static boolean esTramoEspecialComoPortico(Portico portico)
    {
        return PORTICOS_ESPECIALES.getOrDefault(portico.getAutopista().getCodigo(), Set.of())
            .contains(portico.getCodigo());
    }
}