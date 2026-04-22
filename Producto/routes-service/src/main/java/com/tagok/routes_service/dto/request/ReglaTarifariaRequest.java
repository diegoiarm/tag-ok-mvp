package com.tagok.routes_service.dto.request;

import java.util.List;

import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaTarifariaRequest 
{
    private List<TipoVehiculo> aplicaA;
    private List<ValorTarifaRequest> valores;
}
