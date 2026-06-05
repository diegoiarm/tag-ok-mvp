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

    @Aggregation(pipeline = {
        "{ $match: { 'usuarioId': ?0 } }",
        "{ $unwind: '$meses' }",
        "{ $unwind: '$meses.dias' }",
        "{ $unwind: '$meses.dias.cruces' }",
        "{ $group: { '_id': '$meses.dias.cruces.patente' } }",
        "{ $project: { 'patente': '$_id', '_id': 0 } }",
        "{ $sort: { 'patente': 1 } }"
    })
    List<ProyeccionPatente> findPatentesUnicas(String usuarioId);

    @Aggregation(pipeline = {
        "{ $match: { 'usuarioId': ?0 } }",
        "{ $unwind: '$meses' }",
        "{ $unwind: '$meses.dias' }",
        "{ $unwind: '$meses.dias.cruces' }",
        "{ $match: { 'meses.dias.cruces.patente': { $in: ?1 } } }",
        "{ $group: { " +
            "'_id': '$año', " +
            "'cantidadCruces': { $sum: 1 }, " +
            "'totalAño': { $sum: { $toDecimal: '$meses.dias.cruces.valor' } }, " +
            "'mesesDisponibles': { $addToSet: '$meses.mes' } " +
        "} }",
        "{ $project: { " +
            "'año': '$_id', " +
            "'cantidadCruces': 1, " +
            "'totalAño': 1, " +
            "'mesesDisponibles': 1, " +
            "'cargadoCompleto': { $literal: false }, " +
            "'_id': 0 " +
        "} }",
        "{ $sort: { 'año': -1 } }"
    })
    List<ProyeccionAnual> findResumenAnualFiltrado(String usuarioId, List<String> patentes);
}
