package com.tagok.history_service.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Un cruce de pórtico tal como fue leído desde la factura del cliente.
 * La fecha se mantiene como texto (YYYY-MM-DD) porque proviene de la IA
 * y puede venir incompleta o ilegible.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaItemDTO
{
    private String fecha;
    private String hora;
    private String portico;
    private String autopista;
    private BigDecimal valor;
}
