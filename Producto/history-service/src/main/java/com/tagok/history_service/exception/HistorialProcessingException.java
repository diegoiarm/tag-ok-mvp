package com.tagok.history_service.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class HistorialProcessingException extends AppException 
{
    public HistorialProcessingException(String message) 
    {
        super("HIST-001", message, StandardErrorCategories.INTERNAL_ERROR, message);
    }
}
