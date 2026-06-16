package com.tagok.routes_service.service.mapper;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.tarifa.Cruce;
import com.tagok.routes_service.domain.tarifa.CrucePortico;
import com.tagok.routes_service.domain.tarifa.CruceTramo;
import com.tagok.routes_service.dto.response.route.CobroPorticoResponse;
import com.tagok.routes_service.dto.response.route.CobroRutaResponse;
import com.tagok.routes_service.dto.response.route.CobroTramoResponse;

import io.github.roony11_1.error.core.exceptions.InternalErrorException;

@Component
public class CobroRutaMapper 
{
    public CobroRutaResponse toResponse(Cruce cruce) 
    {
        if (cruce instanceof CrucePortico cp) 
        {
            return new CobroPorticoResponse(
                cp.porticoId(), 
                cp.nombre(), 
                cp.codigo(), 
                cp.autopista(),
                cp.latitud(), cp.longitud(), 
                cp.tipoTarifa(), 
                cp.valor(), 
                cp.horaFechaCruce()
            );
        }

        if (cruce instanceof CruceTramo ct) {
            return new CobroTramoResponse(
                ct.entradaId(),
                ct.salidaId(),
                ct.codigoEntrada(),
                ct.codigoSalida(),
                ct.nombreEntrada(),
                ct.nombreSalida(),
                ct.autopista(),
                ct.latitudEntrada(),
                ct.longitudEntrada(),
                ct.latitudSalida(),
                ct.longitudSalida(),
                ct.tipoTarifa(),
                ct.valor(),
                ct.horaFechaCruce()
            );
        }

        throw new InternalErrorException("Tipo de cruce desconocido: " + cruce.getClass());
    }
}
