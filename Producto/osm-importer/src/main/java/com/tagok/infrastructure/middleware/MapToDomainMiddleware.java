package com.tagok.infrastructure.middleware;

import com.fasterxml.jackson.databind.JsonNode;
import com.tagok.domain.model.*;
import com.tagok.infrastructure.parser.ElementMapper;

import java.util.*;
import java.util.logging.Logger;

public class MapToDomainMiddleware implements ElementMiddleware 
{
    private static final Logger logger = Logger.getLogger(MapToDomainMiddleware.class.getName());
    public static final String CURRENT_ELEMENT_KEY = "currentElement";

    private final ElementMapper mapper = new ElementMapper();

    @Override
    public FilterResult process(JsonNode node, int index, String fileName, Map<String, Object> context, Next next) 
    {
        try 
        {
            Element element = mapper.map(node);
            context.put(CURRENT_ELEMENT_KEY, element);
            return next.invoke(node, index, fileName, context);
        } 
        catch (Exception e) 
        {
            logger.warning("Error mapeando elemento " + index + " en " + fileName + ": " + e.getMessage());
            return FilterResult.ERROR;
        }
    }
}