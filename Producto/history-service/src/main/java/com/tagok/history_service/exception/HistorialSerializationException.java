package com.tagok.history_service.exception;

import io.github.roony11_1.error.core.StandardErrorCategories;
import io.github.roony11_1.error.core.exceptions.AppException;

public class HistorialSerializationException extends AppException 
{
    public HistorialSerializationException(String message) 
    {
        super("HIST-002", message, StandardErrorCategories.INVALID_INPUT, message);
    }
}
