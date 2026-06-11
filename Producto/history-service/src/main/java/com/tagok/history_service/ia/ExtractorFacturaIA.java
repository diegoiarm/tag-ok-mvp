package com.tagok.history_service.ia;

import com.tagok.history_service.dto.FacturaExtraidaDTO;

/**
 * Puerto de extracción de datos de una factura/boleta entregada por el cliente
 * (PDF o imagen de cámara). La implementación concreta (Gemini, etc.) es
 * intercambiable sin tocar el resto del servicio.
 */
public interface ExtractorFacturaIA
{
    FacturaExtraidaDTO extraer(byte[] contenido, String mimeType);
}
