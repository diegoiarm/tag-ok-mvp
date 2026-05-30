package com.tagok.history_service.repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tagok.history_service.document.HistorialMensualDocument;

@Repository
public interface HistorialMensualRepository extends MongoRepository<HistorialMensualDocument, String>
{
    Optional<HistorialMensualDocument> findByUsuarioIdAndAñoAndMes(String usuarioId, int año, int mes);
}
