package com.tagok.routes_service.domain.tarifa.calculo;

import java.time.LocalDateTime;

public record CruceRequest(
    Long porticoId,
    LocalDateTime horaFechaCruce) 
{
    
}
