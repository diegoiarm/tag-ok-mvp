package com.tagok.history_service.ia.gemini;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tagok.history_service.dto.FacturaExtraidaDTO;
import com.tagok.history_service.ia.ExtractorFacturaIA;
import com.tagok.history_service.ia.IAException;
import com.tagok.history_service.ia.IANoConfiguradaException;

/**
 * Adaptador de {@link ExtractorFacturaIA} sobre la API REST de Google Gemini
 * (tier gratuito). Envía el PDF/imagen como inline_data y fuerza salida JSON
 * con response_schema, por lo que la respuesta se parsea directo al DTO.
 */
@Service
public class GeminiExtractorFactura implements ExtractorFacturaIA
{
    private static final Logger log = LoggerFactory.getLogger(GeminiExtractorFactura.class);

    private static final String PROMPT = """
        Extrae los datos de esta boleta o factura de peajes TAG de autopistas chilenas.
        Para cada cruce de pórtico registrado entrega: fecha (formato YYYY-MM-DD), hora \
        (HH:mm:ss si aparece), portico (nombre del pórtico o punto de cobro tal como aparece \
        en el documento), autopista (nombre de la autopista o concesionaria, null si no aparece) \
        y valor (monto cobrado en pesos chilenos). Los montos en pesos chilenos no llevan \
        decimales: interpreta los puntos como separadores de miles. Además entrega la patente \
        del vehículo (null si no aparece) y el total del documento (null si no aparece). \
        Omite filas que no sean cruces de peaje (cargos administrativos, intereses, descuentos).""";

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;
    private final String model;

    public GeminiExtractorFactura(
        @Value("${gemini.api-key:}") String apiKey,
        @Value("${gemini.model:gemini-2.5-flash}") String model,
        @Value("${gemini.base-url:https://generativelanguage.googleapis.com}") String baseUrl,
        @Value("${gemini.timeout-segundos:60}") int timeoutSegundos)
    {
        this.apiKey = apiKey;
        this.model = model;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(timeoutSegundos * 1000);

        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(factory)
            .build();
    }

    @Override
    public FacturaExtraidaDTO extraer(byte[] contenido, String mimeType)
    {
        if (apiKey == null || apiKey.isBlank())
        {
            throw new IANoConfiguradaException(
                "La comparación con IA no está disponible: falta configurar GEMINI_API_KEY");
        }

        try
        {
            String respuesta = restClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(construirRequest(contenido, mimeType))
                .retrieve()
                .body(String.class);

            return parsearRespuesta(respuesta);
        }
        catch (RestClientResponseException e)
        {
            // El cuerpo del error de Gemini trae la causa precisa (status PERMISSION_DENIED,
            // restricción de la API key, API deshabilitada, región no soportada, etc.).
            String cuerpo = e.getResponseBodyAsString();
            log.error("Gemini respondió HTTP {} (modelo {}): {}",
                e.getStatusCode().value(), model, cuerpo);

            if (e.getStatusCode().value() == 429)
            {
                throw new IAException(
                    "Límite de uso del tier gratuito de Gemini alcanzado. Intenta nuevamente en unos minutos.", e);
            }
            if (e.getStatusCode().value() == 403)
            {
                throw new IAException(
                    "Gemini rechazó la solicitud (403): la API key es inválida, tiene restricciones "
                        + "(p.ej. limitada a apps Android/IP/referer) o la Generative Language API no está "
                        + "habilitada para su proyecto.", e);
            }
            throw new IAException("Gemini respondió HTTP " + e.getStatusCode().value(), e);
        }
        catch (ResourceAccessException e)
        {
            throw new IAException("No se pudo contactar a Gemini (timeout o problema de red)", e);
        }
    }

    private Map<String, Object> construirRequest(byte[] contenido, String mimeType)
    {
        Map<String, Object> schemaItem = Map.of(
            "type", "OBJECT",
            "properties", Map.of(
                "fecha", Map.of("type", "STRING", "description", "Fecha del cruce en formato YYYY-MM-DD"),
                "hora", Map.of("type", "STRING", "nullable", true),
                "portico", Map.of("type", "STRING"),
                "autopista", Map.of("type", "STRING", "nullable", true),
                "valor", Map.of("type", "NUMBER")),
            "required", List.of("portico", "valor"));

        Map<String, Object> schema = Map.of(
            "type", "OBJECT",
            "properties", Map.of(
                "patente", Map.of("type", "STRING", "nullable", true),
                "total", Map.of("type", "NUMBER", "nullable", true),
                "items", Map.of("type", "ARRAY", "items", schemaItem)),
            "required", List.of("items"));

        return Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(
                    Map.of("inline_data", Map.of(
                        "mime_type", mimeType,
                        "data", Base64.getEncoder().encodeToString(contenido))),
                    Map.of("text", PROMPT)))),
            "generationConfig", Map.of(
                "temperature", 0,
                "response_mime_type", "application/json",
                "response_schema", schema));
    }

    private FacturaExtraidaDTO parsearRespuesta(String respuesta)
    {
        try
        {
            JsonNode raiz = objectMapper.readTree(respuesta);
            JsonNode texto = raiz.path("candidates").path(0)
                .path("content").path("parts").path(0).path("text");

            if (texto.isMissingNode() || texto.asText().isBlank())
            {
                throw new IAException("Gemini no devolvió contenido extraíble para el documento");
            }

            return objectMapper.readValue(texto.asText(), FacturaExtraidaDTO.class);
        }
        catch (IAException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new IAException("No se pudo interpretar la respuesta de Gemini", e);
        }
    }
}
