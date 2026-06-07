package com.tagok.routes_service.domain.auditoria;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registro de auditoría de una acción administrativa: qué cambio se hizo, sobre
 * qué entidad y qué usuario lo realizó. Alimenta la sección de Auditoría (CU18).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "registro_auditoria")
public class RegistroAuditoria
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAccion accion;

    /** Tipo de entidad afectada, p. ej. "Pórtico". */
    @Column(nullable = false)
    private String entidad;

    /** Identificador de la entidad afectada (puede ser null en cargas masivas). */
    @Column(name = "entidad_id")
    private String entidadId;

    /** Descripción legible del cambio. */
    @Column(length = 500)
    private String descripcion;

    /** Usuario que realizó la acción (claim "sub" del JWT). */
    @Column(name = "usuario_id")
    private String usuarioId;

    /** Email del usuario que realizó la acción (claim "email" del JWT). */
    @Column(name = "usuario_email")
    private String usuarioEmail;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;
}
