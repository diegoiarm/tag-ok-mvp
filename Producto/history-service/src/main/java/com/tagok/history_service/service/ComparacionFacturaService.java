package com.tagok.history_service.service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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

        // La extracción con Gemini (hasta 60 s) y la generación de la boleta
        // (consulta a Mongo) son independientes: se ejecutan en paralelo.
        CompletableFuture<FacturaExtraidaDTO> extraccion =
            CompletableFuture.supplyAsync(() -> extractorFacturaIA.extraer(contenido, mimeType));

        BoletaDTO boletaApp = boletaService.generarBoleta(userId, request);
        FacturaExtraidaDTO facturaCliente = esperarExtraccion(extraccion);

        return comparadorFacturas.comparar(boletaApp, facturaCliente);
    }

    private FacturaExtraidaDTO esperarExtraccion(CompletableFuture<FacturaExtraidaDTO> extraccion)
    {
        try
        {
            return extraccion.join();
        }
        catch (CompletionException e)
        {
            // Re-lanza la excepción original (IAException, etc.) para que el
            // handler la traduzca al código HTTP correcto, no a un 500 genérico.
            if (e.getCause() instanceof RuntimeException causa)
                throw causa;
            throw e;
        }
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
