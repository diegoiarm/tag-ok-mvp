package com.tagok.routes_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tagok.routes_service.domain.autopista.Autopista;

@Repository
public interface AutopistaRepository extends JpaRepository<Autopista, Long>
{
    Optional<Autopista> findByNombre(String nombre);

    Optional<Autopista> findByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    boolean existsByCodigo(String codigo);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
