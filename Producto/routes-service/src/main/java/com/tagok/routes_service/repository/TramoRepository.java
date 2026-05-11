package com.tagok.routes_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tramo.Tramo;

public interface TramoRepository extends JpaRepository<Tramo, Long>
{
    Optional<Tramo> findByEntradaAndSalida(Portico entrada, Portico salida);
}
