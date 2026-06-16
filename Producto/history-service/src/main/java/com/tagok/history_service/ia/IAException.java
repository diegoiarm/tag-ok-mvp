package com.tagok.history_service.ia;

import io.github.roony11_1.error.core.exceptions.AppException;

public class IAException extends AppException 
{
    public IAException(String message) 
    {
        super("IA-001", message, GeminiErrorCategories.GEMINI_UNAVAILABLE,
              message);
    }

    public IAException(String message, Throwable cause) 
    {
        super("IA-001", message, GeminiErrorCategories.GEMINI_UNAVAILABLE, message);
        initCause(cause);
    }
}