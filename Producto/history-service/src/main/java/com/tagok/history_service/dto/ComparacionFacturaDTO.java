package com.tagok.history_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComparacionFacturaDTO
{
    private BoletaDTO boletaApp;
    private FacturaExtraidaDTO facturaCliente;

    private List<ComparacionItemDTO> items;

    private BigDecimal totalApp;
    private BigDecimal totalFactura;
    private BigDecimal diferenciaTotal;

    private long coincidencias;
    private long discrepancias;

    /** true cuando no hay discrepancias y los totales cuadran. */
    private boolean cuadra;
}
