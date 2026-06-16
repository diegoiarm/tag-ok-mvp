package com.tagok.routes_service.exception;

import io.github.roony11_1.error.core.ErrorResponse;
import io.github.roony11_1.error.core.exceptions.InvalidInputException;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ValidationExceptionHandler 
{
    private final HttpServletRequest request;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) 
    {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        InvalidInputException appEx = new InvalidInputException("Datos inválidos: " + details);
        ErrorResponse body = new ErrorResponse(appEx.getCode(), appEx.getDisplayMessage());
        body.setPath(request.getRequestURI());
        body.setTraceId(MDC.get("traceId"));

        return ResponseEntity.badRequest().body(body);
    }
}