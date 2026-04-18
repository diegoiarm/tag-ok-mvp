package com.tagok.routes_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tagok.routes_service.domain.Portico;

@Repository
public interface PorticoRepository extends JpaRepository<Portico, Long>
{

}
