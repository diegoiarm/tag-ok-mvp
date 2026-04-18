package com.tagok.routes_service.service.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.CalendarioTarifario;
import com.tagok.routes_service.domain.Portico;
import com.tagok.routes_service.domain.RangoHorario;
import com.tagok.routes_service.domain.ReglaTarifaria;
import com.tagok.routes_service.domain.ReglaTemporal;
import com.tagok.routes_service.domain.ValorTarifa;
import com.tagok.routes_service.domain.dto.request.PorticoRequest;
import com.tagok.routes_service.domain.dto.response.CalendarioTarifarioResponse;
import com.tagok.routes_service.domain.dto.response.PorticoResponse;
import com.tagok.routes_service.domain.dto.response.RangoHorarioResponse;
import com.tagok.routes_service.domain.dto.response.ReglaTarifariaResponse;
import com.tagok.routes_service.domain.dto.response.ReglaTemporalResponse;
import com.tagok.routes_service.domain.dto.response.ValorTarifaResponse;

@Component
public class PorticoMapper 
{
    public Portico fromRequest(PorticoRequest request)
    {
        Portico portico = Portico.builder()
            .codigo(request.getCodigo())
            .sentido(request.getSentido())
            .latitud(request.getLatitud())
            .longitud(request.getLongitud())
            .build();

        if (request.getReglas() != null)
        {
            request.getReglas().forEach(r -> 
            {
                ReglaTarifaria regla = new ReglaTarifaria();
                regla.setAplicaA(r.getAplicaA());

                if (r.getValores() != null)
                {
                    r.getValores().forEach(v -> 
                    {
                        ValorTarifa valor = new ValorTarifa();
                        valor.setTipoTarifa(v.getTipoTarifa());
                        valor.setValor(v.getValor());

                        regla.addValor(valor);
                    });
                }

                portico.addRegla(regla);
            });
        }

        if (request.getCalendario() != null)
        {
            CalendarioTarifario calendario = new CalendarioTarifario();

            if (request.getCalendario().getReglas() != null)
            {
                request.getCalendario().getReglas().forEach(rt -> 
                {
                    ReglaTemporal reglaTemporal = new ReglaTemporal();
                    reglaTemporal.setTipoTarifa(rt.getTipoTarifa());
                    reglaTemporal.setTipoDia(rt.getTipoDia());

                    if (rt.getTramos() != null)
                    {
                        rt.getTramos().forEach(t -> 
                        {
                            RangoHorario rango = new RangoHorario();
                            rango.setHoraInicio(t.getInicio());
                            rango.setHoraFin(t.getFin());

                            rango.setRegla(reglaTemporal);

                            reglaTemporal.addTramo(rango);
                        });
                    }

                    calendario.addRegla(reglaTemporal);
                });
            }

            portico.setCalendario(calendario);
        }

        return portico;
    }

    public PorticoResponse toResponse(Portico portico)
    {
        return PorticoResponse.builder()
            .id(portico.getId())
            .codigo(portico.getCodigo())
            .sentido(portico.getSentido())
            .latitud(portico.getLatitud())
            .longitud(portico.getLongitud())
            .reglas(
                portico.getReglas() != null
                    ? portico.getReglas().stream().map(this::toReglaResponse).collect(Collectors.toList())
                    : null
            )
            .calendario(
                portico.getCalendario() != null
                    ? toCalendarioResponse(portico.getCalendario())
                    : null
            )
            .build();
    }

    private ReglaTarifariaResponse toReglaResponse(ReglaTarifaria regla)
    {
        return ReglaTarifariaResponse.builder()
            .aplicaA(regla.getAplicaA())
            .valores(
                regla.getValores() != null
                    ? regla.getValores().stream().map(this::toValorResponse).collect(Collectors.toList())
                    : null
            )
            .build();
    }

    private ValorTarifaResponse toValorResponse(ValorTarifa valor)
    {
        return ValorTarifaResponse.builder()
            .tipoTarifa(valor.getTipoTarifa())
            .valor(valor.getValor())
            .build();
    }

    private CalendarioTarifarioResponse toCalendarioResponse(CalendarioTarifario calendario)
    {
        return CalendarioTarifarioResponse.builder()
            .reglas(
                calendario.getReglas() != null
                    ? calendario.getReglas().stream().map(this::toReglaTemporalResponse).collect(Collectors.toSet())
                    : null
            )
            .build();
    }

    private ReglaTemporalResponse toReglaTemporalResponse(ReglaTemporal regla)
    {
        return ReglaTemporalResponse.builder()
            .tipoTarifa(regla.getTipoTarifa())
            .tipoDia(regla.getTipoDia())
            .tramos(
                regla.getTramos() != null
                    ? regla.getTramos().stream()
                        .map(this::toRangoResponse)
                        .collect(Collectors.toList())
                    : null
            )
            .build();
    }

    private RangoHorarioResponse toRangoResponse(RangoHorario rango)
    {
        return RangoHorarioResponse.builder()
            .horaInicio(rango.getHoraInicio())
            .horaFin(rango.getHoraFin())
            .build();
    }
}
