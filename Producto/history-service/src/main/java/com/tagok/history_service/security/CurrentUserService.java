package com.tagok.history_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tagok.history_service.exception.AuthenticationException;
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

        if (claims == null || !claims.containsKey("sub")) 
            throw new AuthenticationException("El token no contiene un subject (userId)");

        return (String) claims.get("sub");
    }

    public String getEmail() 
    {
        String token = extractToken();
        Map<String, Object> claims = parseToken(token);

        if (claims == null || !claims.containsKey("email"))
            throw new AuthenticationException("El token no contiene el claim 'email'");

        return (String) claims.get("email");
    }

    private String extractToken() 
    {
        try 
        {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null)
                throw new AuthenticationException("No hay contexto de petición");

            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer "))
                return authHeader.substring(7);

            throw new AuthenticationException("Cabecera Authorization no encontrada o mal formada");
        } catch (AuthenticationException e) 
        {
            throw e;
        } 
        catch (Exception e) 
        {
            throw new AuthenticationException("Error al extraer el token: " + e.getMessage());
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
            throw new AuthenticationException("Formato de token inválido");
        } 
        catch (AuthenticationException e) 
        {
            throw e;
        } 
        catch (Exception e) 
        {
            throw new AuthenticationException("Error al parsear el token: " + e.getMessage());
        }
    }
}