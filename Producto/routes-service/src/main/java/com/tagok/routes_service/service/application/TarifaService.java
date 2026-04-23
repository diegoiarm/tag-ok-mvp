package com.tagok.routes_service.service.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.CalculadorTarifa;
import com.tagok.routes_service.domain.tarifa.TarifaCalculada;
import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;
import com.tagok.routes_service.repository.PorticoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService
{
    private final PorticoRepository porticoRepository;
    // El dominio(Negocio) sabe como hacer las transacciones, spring no se encarga de eso
    private final CalculadorTarifa calculadorTarifa = new CalculadorTarifa();

    public TarifaCalculada calcularTarifa(
        Long porticoId,
        TipoVehiculo vehiculo,
        LocalDateTime fechaHora)
    {
        Portico portico = porticoRepository.findById(porticoId)
            .orElseThrow(() -> new IllegalArgumentException("Pórtico no encontrado"));

        return calculadorTarifa.calcular(portico,vehiculo,fechaHora);
    }
}