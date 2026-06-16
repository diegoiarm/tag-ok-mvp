package com.tagok.history_service.config;

import io.github.roony11_1.error.rest.HttpStatusRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import com.tagok.history_service.ia.GeminiErrorCategories;

@Configuration
public class ErrorHttpConfig 
{
    @PostConstruct
    void register() 
    {
        HttpStatusRegistry.register(GeminiErrorCategories.GEMINI_UNAVAILABLE, 503);
        HttpStatusRegistry.register(GeminiErrorCategories.GEMINI_INVALID_CONFIG, 500);
    }
}