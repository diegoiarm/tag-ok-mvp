package com.tagok.routes_service.security;

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

        Map<String, Object> claims = parseToken(token);

        if (claims == null || claims.get("sub") == null) 
            throw new NoAuthException("Token inválido: falta el subject");

        return (String) claims.get("sub");
    }

    public String getEmail() 
    {
        String token = extractToken();

        Map<String, Object> claims = parseToken(token);

        if (claims == null || claims.get("email") == null)
            throw new NoAuthException("Token inválido: falta el email");

        return (String) claims.get("email");
    }

    private String extractToken() 
    {
        try 
        {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) throw new NoAuthException("No hay contexto de petición");

            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) 
            {
                return authHeader.substring(7);
            }
            throw new NoAuthException("Cabecera Authorization no encontrada");
        } 
        catch (NoAuthException e) 
        {
            throw e;
        } 
        catch (Exception e) 
        {
            throw new NoAuthException("Error al extraer el token: " + e.getMessage());
        }
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
            throw new NoAuthException("Formato de token inválido");
        } catch (NoAuthException e) 
        {
            throw e;
        } 
        catch (Exception e) 
        {
            throw new NoAuthException("Error al parsear el token: " + e.getMessage());
        }
    }
}