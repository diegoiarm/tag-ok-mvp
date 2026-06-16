package com.tagok.routes_service.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class ResourceNotFoundException extends AppException 
{
    public ResourceNotFoundException(String message) 
    {
        super("ROUTE-002", message, StandardErrorCategories.NOT_FOUND, message);
    }
}