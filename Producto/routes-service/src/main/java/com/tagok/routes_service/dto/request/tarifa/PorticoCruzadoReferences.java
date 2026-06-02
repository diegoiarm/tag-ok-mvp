package com.tagok.routes_service.dto.request.tarifa;

import java.time.LocalDateTime;

public record PorticoCruzadoReferences(
    Long porticoId,
    Long salidaId,
    LocalDateTime porticoHoraFechaCruce,
    LocalDateTime salidaHoraFechaCruce) 
{

}
