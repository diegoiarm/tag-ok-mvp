package com.tagok.history_service.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tagok.history_service.controller.dto.BoletaRequest;
import com.tagok.history_service.dto.BoletaDTO;
import com.tagok.history_service.dto.ComparacionFacturaDTO;
import com.tagok.history_service.dto.FacturaExtraidaDTO;
import com.tagok.history_service.exception.ArchivoFacturaInvalidoException;
import com.tagok.history_service.ia.ExtractorFacturaIA;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComparacionFacturaService
{
    private static final Set<String> MIME_PERMITIDOS = Set.of(
        "application/pdf", "image/jpeg", "image/png", "image/webp", "image/heic", "image/heif");

    private final BoletaService boletaService;
    private final ExtractorFacturaIA extractorFacturaIA;
    private final ComparadorFacturas comparadorFacturas;

    public ComparacionFacturaDTO comparar(String userId, BoletaRequest request, MultipartFile archivo)
    {
        byte[] contenido = leerArchivo(archivo);
        String mimeType = validarMime(archivo);

        BoletaDTO boletaApp = boletaService.generarBoleta(userId, request);
        FacturaExtraidaDTO facturaCliente = extractorFacturaIA.extraer(contenido, mimeType);

        return comparadorFacturas.comparar(boletaApp, facturaCliente);
    }

    private byte[] leerArchivo(MultipartFile archivo)
    {
        if (archivo == null || archivo.isEmpty())
        {
            throw new ArchivoFacturaInvalidoException("Debe adjuntar la factura como PDF o imagen");
        }
        try
        {
            return archivo.getBytes();
        }
        catch (IOException e)
        {
            throw new ArchivoFacturaInvalidoException("No se pudo leer el archivo adjunto");
        }
    }

    private String validarMime(MultipartFile archivo)
    {
        String mimeType = archivo.getContentType();
        if (mimeType == null || !MIME_PERMITIDOS.contains(mimeType.toLowerCase()))
        {
            throw new ArchivoFacturaInvalidoException(
                "Formato no soportado (" + mimeType + "). Use PDF o imagen JPEG/PNG/WEBP/HEIC.");
        }
        return mimeType.toLowerCase();
    }
}
