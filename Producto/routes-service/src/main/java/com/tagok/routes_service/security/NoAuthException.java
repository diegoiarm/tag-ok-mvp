package com.tagok.routes_service.security;


import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;


public class NoAuthException extends AppException 
{
    public NoAuthException(String message) 
    {
        super("AUTH-001", message, StandardErrorCategories.ACCESS_DENIED, message);
    }
}
