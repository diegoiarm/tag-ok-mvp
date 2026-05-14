package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public sealed interface Cruce permits CrucePortico, CruceTramo 
{
    String codigo();
    String nombre();
    String autopista();
    TipoTarifa tipoTarifa();
    BigDecimal valor();
    LocalDateTime horaFechaCruce();
}
