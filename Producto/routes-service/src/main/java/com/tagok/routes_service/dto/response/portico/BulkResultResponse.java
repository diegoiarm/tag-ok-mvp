package com.tagok.routes_service.dto.response.portico;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resultado de una carga masiva de pórticos: cuántos se crearon y qué filas fallaron. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkResultResponse
{
    private int creados;
    private int fallidos;
    private List<String> errores;
}
