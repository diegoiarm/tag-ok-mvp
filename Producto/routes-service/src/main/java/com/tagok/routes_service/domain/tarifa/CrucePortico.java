package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CrucePortico(
    Long porticoId,
    String codigo,
    String nombre,
    String autopista,
    double latitud,
    double longitud,
    TipoTarifa tipoTarifa,
    BigDecimal valor,
    LocalDateTime horaFechaCruce) implements Cruce 
{

}
