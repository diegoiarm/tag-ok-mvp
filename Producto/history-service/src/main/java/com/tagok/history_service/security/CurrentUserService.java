package com.tagok.history_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;
import java.util.Map;

@Service
public class CurrentUserService 
{
    public String getUserId() 
    {
        String token = extractToken();
        if (token != null) 
        {
            Map<String, Object> claims = parseToken(token);
            if (claims != null) 
                {
                return (String) claims.get("sub");
            }
        }
        return null;
    }

    public String getEmail() 
    {
        String token = extractToken();
        if (token != null) 
            {
            Map<String, Object> claims = parseToken(token);
            if (claims != null) 
            {
                return (String) claims.get("email");
            }
        }
        return null;
    }

    private String extractToken() 
    {
        try 
        {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) 
                return null;
            
            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) 
            {
                return authHeader.substring(7);
            }
        } catch (Exception e) 
        {
            System.err.println("Error extrayendo token: " + e.getMessage());
        }
        return null;
    }

    private Map<String, Object> parseToken(String token) 
    {
        try 
        {
            String[] parts = token.split("\\.");
            if (parts.length >= 2) 
                {
                String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                return new ObjectMapper().readValue(payload, Map.class);
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error parseando token: " + e.getMessage());
        }
        return null;
    }
}