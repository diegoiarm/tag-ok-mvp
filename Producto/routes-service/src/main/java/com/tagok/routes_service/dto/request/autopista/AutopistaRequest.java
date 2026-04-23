package com.tagok.routes_service.dto.request.autopista;

import java.util.List;

import com.tagok.routes_service.dto.request.portico.PorticoRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutopistaRequest 
{
    private String autopista;
    private List<PorticoRequest> porticos;
}
