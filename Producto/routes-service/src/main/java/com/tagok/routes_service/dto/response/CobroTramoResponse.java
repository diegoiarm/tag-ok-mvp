package com.tagok.routes_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tagok.routes_service.domain.tarifa.TipoTarifa;

public record CobroTramoResponse(
    Long entradaId,
    Long salidaId,
    String nombreEntrada,
    String nombreSalida,
    String autopista,
    TipoTarifa tarifa,
    BigDecimal valor,
    LocalDateTime fechaHora) implements CobroRutaResponse
{
}
