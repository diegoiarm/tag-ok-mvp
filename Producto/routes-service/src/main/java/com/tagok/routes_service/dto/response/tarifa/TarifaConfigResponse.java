package com.tagok.routes_service.dto.response.tarifa;

import java.util.List;

import com.tagok.routes_service.dto.response.portico.CalendarioTarifarioResponse;
import com.tagok.routes_service.dto.response.portico.ReglaTarifariaResponse;

import lombok.Builder;

/**
 * Configuración tarifaria completa (reglas + calendario) de un pórtico o tramo,
 * devuelta para edición en el panel admin (CU19).
 */
@Builder
public record TarifaConfigResponse(
    List<ReglaTarifariaResponse> reglas,
    CalendarioTarifarioResponse calendario)
{
}
