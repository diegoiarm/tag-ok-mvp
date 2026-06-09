package com.tagok.routes_service.service.application;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.tarifa.TarifaConfigValidator;
import com.tagok.routes_service.domain.tramo.Tramo;
import com.tagok.routes_service.dto.request.tarifa.TarifaConfigRequest;
import com.tagok.routes_service.dto.response.tarifa.TarifaConfigResponse;
import com.tagok.routes_service.dto.response.tarifa.TramoAdminResponse;
import com.tagok.routes_service.exception.ResourceNotFoundException;
import com.tagok.routes_service.repository.TramoRepository;
import com.tagok.routes_service.service.mapper.TramoMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoService
{
    private final TramoRepository tramoRepository;
    private final TramoMapper tramoMapper;

    /** Lista todos los tramos para la gestión administrativa de tarifas (CU19). */
    public List<TramoAdminResponse> findAllForAdmin()
    {
        return tramoRepository.findAll().stream()
            .sorted(Comparator.comparing(Tramo::getId))
            .map(tramoMapper::toAdminResponse)
            .toList();
    }

    /** Configuración tarifaria (reglas + calendario) de un tramo, para edición. */
    public TarifaConfigResponse getTarifaConfig(Long id)
    {
        Tramo tramo = tramoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tramo no encontrado: " + id));

        return tramoMapper.toTarifaConfig(tramo);
    }

    /** Reemplaza la configuración tarifaria de un tramo tras validarla. */
    @Transactional
    public TarifaConfigResponse actualizarTarifaConfig(Long id, TarifaConfigRequest request)
    {
        Tramo tramo = tramoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tramo no encontrado: " + id));

        tramoMapper.aplicarTarifaConfig(tramo, request);
        TarifaConfigValidator.validar(tramo.getReglas(), tramo.getCalendario());

        return tramoMapper.toTarifaConfig(tramoRepository.save(tramo));
    }
}
