package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

public class CalculadorTarifa
{
    public Cruce calcular(
        Portico portico,
        TipoVehiculo vehiculo,
        LocalDateTime fechaHora)
    {
        Tarifa tarifa = portico.calcularTarifa(vehiculo, fechaHora);

        var cruce = new Cruce(
            portico.getId(), 
            portico.getCodigo(), 
            portico.getNombre(),
            portico.getAutopista().getNombre(),
            tarifa.tipoTarifa(),
            tarifa.monto(),
            fechaHora);

        return cruce;
    }
}
