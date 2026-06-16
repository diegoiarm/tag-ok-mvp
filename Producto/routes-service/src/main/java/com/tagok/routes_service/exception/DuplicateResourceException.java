package com.tagok.routes_service.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class DuplicateResourceException extends AppException 
{
    public DuplicateResourceException(String message) 
    {
        super("ROUTE-001", message, StandardErrorCategories.ALREADY_EXISTS, message);
    }
}