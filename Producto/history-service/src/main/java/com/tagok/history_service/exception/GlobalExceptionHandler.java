package com.tagok.history_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}