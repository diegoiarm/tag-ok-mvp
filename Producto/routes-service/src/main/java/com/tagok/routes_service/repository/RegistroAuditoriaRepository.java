package com.tagok.routes_service.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tagok.routes_service.domain.auditoria.RegistroAuditoria;

@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long>
{
    List<RegistroAuditoria> findAllByOrderByFechaDesc(Pageable pageable);
}
