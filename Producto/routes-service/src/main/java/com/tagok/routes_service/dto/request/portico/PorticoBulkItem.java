package com.tagok.routes_service.dto.request.portico;

/**
 * Fila de la carga masiva de pórticos (CU20). La autopista se referencia por su
 * código para que el mismo formato sirva tanto en JSON como en CSV.
 */
public record PorticoBulkItem(
    String autopistaCodigo,
    String codigo,
    String nombre,
    String sentido,
    Double latitud,
    Double longitud)
{

}
