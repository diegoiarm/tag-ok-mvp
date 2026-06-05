package com.tagok.routes_service.security;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoAuthException extends RuntimeException 
{
    public NoAuthException(String message) 
    {
        super(message);
    }
}
