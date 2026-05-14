package com.tagok.routes_service.dto.response.route;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tagok.routes_service.domain.tarifa.TipoTarifa;

public record CobroPorticoResponse(
    Long porticoId,
    String nombre,
    String codigo,
    String autopista,
    double latitud,
    double longitud,
    TipoTarifa tarifa,
    BigDecimal valor,
    LocalDateTime fechaHora) implements CobroRutaResponse
{
}
