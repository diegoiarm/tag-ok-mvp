package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tagok.routes_service.domain.portico.Cruce;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

public class CalculadorTarifa
{
    public TarifaCalculada calcular(
        Portico portico,
        TipoVehiculo vehiculo,
        LocalDateTime fechaHora)
    {
        BigDecimal monto = portico.calcularTarifa(vehiculo, fechaHora);

        var cruce = new Cruce(portico.getId(), portico.getCodigo(), monto);

        return new TarifaCalculada(monto, cruce, fechaHora);
    }
}
