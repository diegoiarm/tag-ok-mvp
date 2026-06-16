package com.tagok.history_service.ia;

import io.github.roony11_1.error.core.ErrorCategory;

public final class GeminiErrorCategories 
{
    private GeminiErrorCategories() {}

    public static final ErrorCategory GEMINI_UNAVAILABLE = new ErrorCategory() 
    {
        @Override public String name() { return "GEMINI_UNAVAILABLE"; }
        @Override public String description() { return "Gemini no disponible temporalmente"; }
    };

    public static final ErrorCategory GEMINI_INVALID_CONFIG = new ErrorCategory() 
    {
        @Override public String name() { return "GEMINI_INVALID_CONFIG"; }
        @Override public String description() { return "Configuración de Gemini incorrecta"; }
    };
}