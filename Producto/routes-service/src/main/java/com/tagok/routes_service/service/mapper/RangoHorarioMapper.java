package com.tagok.routes_service.service.mapper;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.calendario.RangoHorario;
import com.tagok.routes_service.dto.request.RangoHorarioRequest;
import com.tagok.routes_service.dto.response.RangoHorarioResponse;

@Component
public class RangoHorarioMapper implements IEntityMapper<RangoHorarioResponse, RangoHorarioRequest, RangoHorario>
{
    @Override
    public RangoHorario fromRequest(RangoHorarioRequest request)
    {
        RangoHorario rango = new RangoHorario();
        rango.setHoraInicio(request.getInicio());
        rango.setHoraFin(request.getFin());
        return rango;
    }

    @Override
    public RangoHorarioResponse toResponse(RangoHorario entity)
    {
        return RangoHorarioResponse.builder()
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .build();
    }
}
