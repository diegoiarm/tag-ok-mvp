package com.tagok.routes_service.dto.response;

import java.util.List;

import com.tagok.routes_service.domain.TipoVehiculo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaTarifariaResponse 
{
    private List<TipoVehiculo> aplicaA;
    private List<ValorTarifaResponse> valores;
}
