package com.tagok.history_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tagok.history_service.ia.IAException;
import com.tagok.history_service.ia.IANoConfiguradaException;
import com.tagok.history_service.security.NoAuthException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(NoAuthException.class)
    public ResponseEntity<Map<String, String>> handleNoAuth(NoAuthException e)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ArchivoFacturaInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleArchivoInvalido(ArchivoFacturaInvalidoException e)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IANoConfiguradaException.class)
    public ResponseEntity<Map<String, String>> handleIANoConfigurada(IANoConfiguradaException e)
    {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IAException.class)
    public ResponseEntity<Map<String, String>> handleIA(IAException e)
    {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(Map.of("error", e.getMessage()));
    }
}