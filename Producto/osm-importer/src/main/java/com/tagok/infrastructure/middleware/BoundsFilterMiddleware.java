package com.tagok.infrastructure.middleware;

import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.tagok.domain.filter.BoundingBoxFilter;
import com.tagok.domain.model.Bounds;

public class BoundsFilterMiddleware implements ElementMiddleware
{
    private static final Logger logger = Logger.getLogger(BoundsFilterMiddleware.class.getName());
    private final BoundingBoxFilter filter;

    public BoundsFilterMiddleware(BoundingBoxFilter filter)
    {
        this.filter = filter;
    }

    @Override
    public FilterResult process(JsonNode node, int index, String fileName, Map<String, Object> context, Next next) 
    {
        JsonNode boundsNode = node.get("bounds");

        if (boundsNode == null)
        {
            logger.fine(() -> "Elemento " + index + " en " + fileName + " sin bounds");
            return FilterResult.ERROR;
        }

        try 
        {
            Bounds b = new Bounds(
                boundsNode.get("minlat").asDouble(),
                boundsNode.get("minlon").asDouble(),
                boundsNode.get("maxlat").asDouble(),
                boundsNode.get("maxlon").asDouble());

            if (!filter.intersects(b))
                return FilterResult.REJECTED;

            return next.invoke(node, index, fileName, context);
        } 
        catch (Exception e) 
        {
            logger.warning("Error parseando bounds en " + fileName + " elem " + index + ": " + e.getMessage());
            return FilterResult.ERROR;
        }
    }
}
