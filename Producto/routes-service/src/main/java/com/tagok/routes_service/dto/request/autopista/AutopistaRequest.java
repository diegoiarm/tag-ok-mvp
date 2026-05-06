package com.tagok.routes_service.dto.request.autopista;

import java.util.List;

import com.tagok.routes_service.domain.autopista.TipoCobro;
import com.tagok.routes_service.dto.request.portico.PorticoRequest;
import com.tagok.routes_service.dto.request.tramo.TramoRequest;

public record AutopistaRequest(
    String autopista,
    String codigo,
    TipoCobro tipoCobro,
    List<PorticoRequest> porticos,
    List<TramoRequest> tramos)
{

}
