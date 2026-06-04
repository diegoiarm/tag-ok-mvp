package com.tagok.routes_service.exception;

/**
 * Se lanza cuando se intenta crear o actualizar un recurso con un valor único
 * que ya está en uso (por ejemplo, nombre o código de autopista duplicado).
 */
public class DuplicateResourceException extends RuntimeException
{
    public DuplicateResourceException(String message)
    {
        super(message);
    }
}
