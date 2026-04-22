package com.tagok.routes_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tagok.routes_service.domain.autopista.Autopista;

@Repository
public interface AutopistaRepository extends JpaRepository<Autopista, Long>
{
    Optional<Autopista> findByNombre(String nombre);
}
