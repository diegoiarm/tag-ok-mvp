package com.tagok.history_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tagok.history_service.document.HistorialAnualDocument;

@Repository
public interface HistorialAnualRepository extends MongoRepository<HistorialAnualDocument, String>
{
    List<HistorialAnualDocument> findByUsuarioId(String usuarioId);
    
    @Aggregation(pipeline = {
        "{ $match: { 'usuarioId': ?0 } }",
        "{ $project: { 'año': 1, '_id': 0 } }",
        "{ $sort: { 'año': -1 } }"
    })
    List<Integer> findAvailableYears(String usuarioId);
    
    @Aggregation(pipeline = {
        "{ $match: { 'usuarioId': ?0 } }",
        "{ $project: { " +
            "'año': 1, " +
            "'cantidadCruces': 1, " +
            "'totalAño': 1, " +
            "'cantidadMeses': { $size: '$meses' } " +
        "} }",
        "{ $sort: { 'año': -1 } }"
    })
    List<ProyeccionAnual> findResumenAnual(String usuarioId);
}
