package com.tagok.routes_service.domain.dto.request;

import java.util.List;

import com.tagok.routes_service.domain.TipoDia;
import com.tagok.routes_service.domain.TipoTarifa;

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
