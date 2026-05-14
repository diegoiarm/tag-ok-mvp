package com.tagok.routes_service.dto.response.autopista;

import java.util.List;

import com.tagok.routes_service.domain.autopista.TipoCobro;
import com.tagok.routes_service.dto.response.portico.PorticoResponse;
import com.tagok.routes_service.dto.response.portico.TramoRouteResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutopistaResponse 
{
    private Long id;
    private String nombre;
    private String codigo;
    private TipoCobro tipoCobro;
    private List<PorticoResponse> porticos;
    private List<TramoRouteResponse> tramos;
}
