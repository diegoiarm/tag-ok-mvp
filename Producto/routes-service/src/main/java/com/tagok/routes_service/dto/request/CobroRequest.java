package com.tagok.routes_service.dto.request;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CobroRequest 
{
    private String tipoVehiculo;
    private List<PorticoCobroRequest> porticos;
}
