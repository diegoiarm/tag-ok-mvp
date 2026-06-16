package com.tagok.history_service.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class AuthenticationException extends AppException 
{
    public AuthenticationException(String reason) 
    {
        super("AUTH-001", "Error de autenticación: " + reason, StandardErrorCategories.ACCESS_DENIED, "Error de autenticación: " + reason);
    }
}