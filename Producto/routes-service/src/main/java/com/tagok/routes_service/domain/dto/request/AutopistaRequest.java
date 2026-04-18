package com.tagok.routes_service.domain.dto.request;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutopistaRequest 
{
    private String autopista;
    private List<PorticoRequest> porticos;
}
