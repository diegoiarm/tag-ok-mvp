package com.tagok.history_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.tagok.history_service.document.HistorialAnualDocument;
import com.tagok.history_service.repository.boletaProyeccion.ProyeccionBoletaItem;
import com.tagok.history_service.repository.boletaProyeccion.ProyeccionBoletaTotal;

public interface BoletaRepository extends MongoRepository<HistorialAnualDocument, String>
{
    @Aggregation(pipeline = {
        "{ $match: { " +
            "'usuarioId': ?0, " +
            "'año': { $gte: ?1, $lte: ?2 } " +  // Filtrar por rango de años
        "} }",
        "{ $unwind: '$meses' }",
        "{ $match: { " +
            "'meses.mes': { $gte: ?3, $lte: ?4 } " +  // Filtrar por rango de meses
        "} }",
        "{ $unwind: '$meses.dias' }",
        "{ $match: { " +
            "'meses.dias.fecha': { $gte: ?5, $lte: ?6 } " +  // Filtrar por rango de fechas
        "} }",
        "{ $unwind: '$meses.dias.cruces' }",
        "{ $match: { " +
            "'meses.dias.cruces.patente': ?7, " +  // Filtrar por patente
            "$expr: { " +
                "$cond: { " +
                    "if: { $eq: [?8, []] }, " +  // Si autopistas está vacía
                    "then: true, " +
                    "else: { $in: ['$meses.dias.cruces.autopista', ?8] } " +
                "} " +
            "} " +
        "} }",
        "{ $project: { " +
            "'fecha': '$meses.dias.fecha', " +
            "'autopista': '$meses.dias.cruces.autopista', " +
            "'nombre': '$meses.dias.cruces.nombre', " +
            "'tipoTarifa': '$meses.dias.cruces.tipoTarifa', " +
            "'valor': '$meses.dias.cruces.valor', " +
            "'horaFechaCruce': '$meses.dias.cruces.horaFechaCruce', " +
            "'patente': '$meses.dias.cruces.patente', " +
            "'_id': 0 " +
        "} }",
        "{ $sort: { 'fecha': 1, 'horaFechaCruce': 1 } }"
    })
    List<ProyeccionBoletaItem> findBoletaItems(
        String usuarioId,
        int añoDesde, int añoHasta,
        int mesDesde, int mesHasta,
        LocalDate fechaDesde, LocalDate fechaHasta,
        String patente,
        List<String> autopistas);
    
    @Aggregation(pipeline = {
        "{ $match: { " +
            "'usuarioId': ?0, " +
            "'año': { $gte: ?1, $lte: ?2 } " +
        "} }",
        "{ $unwind: '$meses' }",
        "{ $match: { 'meses.mes': { $gte: ?3, $lte: ?4 } } }",
        "{ $unwind: '$meses.dias' }",
        "{ $match: { 'meses.dias.fecha': { $gte: ?5, $lte: ?6 } } }",
        "{ $unwind: '$meses.dias.cruces' }",
        "{ $match: { " +
            "'meses.dias.cruces.patente': ?7, " +
            "$expr: { " +
                "$cond: { " +
                    "if: { $eq: [?8, []] }, " +
                    "then: true, " +
                    "else: { $in: ['$meses.dias.cruces.autopista', ?8] } " +
                "} " +
            "} " +
        "} }",
        "{ $group: { " +
            "'_id': null, " +
            "'total': { $sum: { $toDecimal: '$meses.dias.cruces.valor' } }, " +
            "'cantidadCruces': { $sum: 1 } " +
        "} }",
        "{ $project: { '_id': 0, 'total': 1, 'cantidadCruces': 1 } }"
    })
    ProyeccionBoletaTotal findBoletaTotal(
        String usuarioId,
        int añoDesde, int añoHasta,
        int mesDesde, int mesHasta,
        LocalDate fechaDesde, LocalDate fechaHasta,
        String patente,
        List<String> autopistas);
}
