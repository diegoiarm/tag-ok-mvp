package com.tagok.routes_service.dto.response.route;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tagok.routes_service.domain.tarifa.TipoTarifa;

public record CobroTramoResponse(
    Long entradaId,
    Long salidaId,
    String nombreEntrada,
    String nombreSalida,
    String autopista,
    double latitudEntrada,
    double longitudEntrada,
    double latitudSalida,
    double longitudSalida,
    TipoTarifa tarifa,
    BigDecimal valor,
    LocalDateTime fechaHora) implements CobroRutaResponse 
{

}
