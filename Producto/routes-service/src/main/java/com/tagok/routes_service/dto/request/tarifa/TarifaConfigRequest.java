package com.tagok.routes_service.dto.request.tarifa;

import java.util.List;

import com.tagok.routes_service.dto.request.portico.CalendarioTarifarioRequest;
import com.tagok.routes_service.dto.request.portico.ReglaTarifariaRequest;

/**
 * Configuración tarifaria completa de un pórtico o tramo: las reglas por tipo de
 * vehículo (con sus valores por {@code TipoTarifa}) y el calendario que decide qué
 * {@code TipoTarifa} aplica según día y rango horario. Se usa para editar (CU19)
 * reemplazando la configuración existente.
 */
public record TarifaConfigRequest(
    List<ReglaTarifariaRequest> reglas,
    CalendarioTarifarioRequest calendario)
{
}
