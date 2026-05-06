package com.tagok.routes_service.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tagok.routes_service.domain.portico.Portico;

@Repository
public interface PorticoRepository extends JpaRepository<Portico, Long>
{
    Optional<Portico> findByCodigo(String codigo);
}
