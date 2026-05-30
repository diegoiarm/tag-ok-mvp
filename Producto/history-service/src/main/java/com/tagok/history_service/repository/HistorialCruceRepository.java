package com.tagok.history_service.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tagok.history_service.document.HistorialCruceDocument;

@Repository
public interface HistorialCruceRepository extends MongoRepository<HistorialCruceDocument, String>
{
    Optional<HistorialCruceDocument> findByUsuarioIdAndFecha(String usuarioId, LocalDate fecha);
}
