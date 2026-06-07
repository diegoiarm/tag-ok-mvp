package com.tagok.routes_service.exception;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tagok.routes_service.security.NoAuthException;

/**
 * Traduce excepciones a respuestas JSON con un código HTTP claro, en lugar de
 * devolver 500 con stacktrace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex)
    {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex)
    {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex)
    {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));

        return build(HttpStatus.BAD_REQUEST, mensaje.isBlank() ? "Datos inválidos" : mensaje);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex)
    {
        // Validaciones de negocio (p. ej. configuración tarifaria inválida) → 400.
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex)
    {
        // Red de seguridad si se viola un constraint de la BD (p. ej. condición de carrera).
        return build(HttpStatus.CONFLICT,
            "Violación de integridad: el registro ya existe o falta un dato obligatorio.");
    }

    @ExceptionHandler(NoAuthException.class)
    public ResponseEntity<Map<String, String>> handleNoAuth(NoAuthException e) 
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", e.getMessage()));
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message)
    {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
