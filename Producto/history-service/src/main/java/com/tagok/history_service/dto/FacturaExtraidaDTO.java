package com.tagok.history_service.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos estructurados extraídos por la IA desde la factura del cliente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaExtraidaDTO
{
    private String patente;
    private BigDecimal total;
    private List<FacturaItemDTO> items;
}
