package com.tagok.routes_service.exception;

/**
 * Se lanza cuando un recurso solicitado por id no existe.
 */
public class ResourceNotFoundException extends RuntimeException
{
    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}
