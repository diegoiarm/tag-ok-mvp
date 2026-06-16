package com.tagok.routes_service.service.application;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.auditoria.RegistroAuditoria;
import com.tagok.routes_service.domain.auditoria.TipoAccion;
import com.tagok.routes_service.dto.response.auditoria.RegistroAuditoriaResponse;
import com.tagok.routes_service.repository.RegistroAuditoriaRepository;
import com.tagok.routes_service.security.CurrentUserService;

import io.github.roony11_1.error.core.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaService
{
    private static final int MAX_REGISTROS = 200;

    private final RegistroAuditoriaRepository auditoriaRepository;
    private final CurrentUserService currentUserService;

    /**
     * Registra una acción administrativa. Es best-effort: si el guardado falla no
     * debe interrumpir la operación principal. El usuario se resuelve del JWT actual.
     */
    public void registrar(TipoAccion accion, String entidad, String entidadId, String descripcion) 
    {
        try 
        {
            auditoriaRepository.save(RegistroAuditoria.builder()
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .descripcion(descripcion)
                .usuarioId(currentUserService.getUserId())
                .usuarioEmail(currentUserService.getEmail())
                .build());
        } 
        catch (Exception e) 
        {
            ErrorHandler.toErrorResponse(e);
        }
    }

    /** Últimos registros de auditoría, del más reciente al más antiguo. */
    public List<RegistroAuditoriaResponse> listar()
    {
        return auditoriaRepository
            .findAllByOrderByFechaDesc(PageRequest.of(0, MAX_REGISTROS))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private RegistroAuditoriaResponse toResponse(RegistroAuditoria r)
    {
        return RegistroAuditoriaResponse.builder()
            .id(r.getId())
            .accion(r.getAccion())
            .entidad(r.getEntidad())
            .entidadId(r.getEntidadId())
            .descripcion(r.getDescripcion())
            .usuarioId(r.getUsuarioId())
            .usuarioEmail(r.getUsuarioEmail())
            .fecha(r.getFecha())
            .build();
    }
}
