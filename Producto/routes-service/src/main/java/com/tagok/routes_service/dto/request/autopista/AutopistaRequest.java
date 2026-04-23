package com.tagok.routes_service.dto.request.autopista;

import java.util.List;

import com.tagok.routes_service.dto.request.portico.PorticoRequest;

public record AutopistaRequest(
    String autopista,
    List<PorticoRequest> porticos) 
{

}
