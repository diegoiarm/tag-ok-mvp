package com.tagok.history_service.ia;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class IANoConfiguradaException extends AppException 
{

    public IANoConfiguradaException(String message) 
    {
        super("IA-002", message, StandardErrorCategories.INTERNAL_ERROR, message);
    }
}