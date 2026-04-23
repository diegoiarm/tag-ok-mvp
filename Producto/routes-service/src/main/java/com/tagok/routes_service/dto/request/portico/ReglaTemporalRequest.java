package com.tagok.routes_service.dto.request.portico;

import java.util.List;

import com.tagok.routes_service.domain.calendario.TipoDia;
import com.tagok.routes_service.domain.tarifa.TipoTarifa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaTemporalRequest 
{
    private TipoTarifa tipoTarifa;
    private TipoDia tipoDia;
    private List<RangoHorarioRequest> tramos;
}
