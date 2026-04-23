package com.tagok.routes_service.dto.request.tarifa;

import java.time.LocalDateTime;

public record PorticoCruzadoRequest(
    Long porticoId,
    LocalDateTime horaFechaCruce) 
{

}
