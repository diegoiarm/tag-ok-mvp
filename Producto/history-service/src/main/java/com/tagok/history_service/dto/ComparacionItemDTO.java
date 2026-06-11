package com.tagok.history_service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * Resultado de comparar un cruce: el item de la app, el de la factura
 * del cliente (uno de los dos puede ser null) y la diferencia de valor.
 */
@Data
@Builder
public class ComparacionItemDTO
{
    private EstadoComparacion estado;
    private BoletaItemDTO itemApp;
    private FacturaItemDTO itemFactura;
    private BigDecimal diferenciaValor;
}
