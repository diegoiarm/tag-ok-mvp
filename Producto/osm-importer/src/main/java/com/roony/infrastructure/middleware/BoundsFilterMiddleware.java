package com.roony.infrastructure.middleware;

import java.util.logging.Logger;

import com.roony.domain.filter.BoundingBoxFilter;
import com.roony.domain.model.Bounds;
import com.roony.domain.model.Element;

public class BoundsFilterMiddleware implements ElementMiddleware
{
    private static final Logger logger = Logger.getLogger(BoundsFilterMiddleware.class.getName());
    private final BoundingBoxFilter filter;

    public BoundsFilterMiddleware(BoundingBoxFilter filter)
    {
        this.filter = filter;
    }

    @Override
    public FilterResult process(Element element, int index, String fileName, Next next) 
    {

        if (element.bounds() == null)
        {
            logger.fine(() -> "Elemento " + index + " en " + fileName + " sin bounds");
            return FilterResult.ERROR;
        }

        try 
        {
            Bounds b = element.bounds();

            if (!filter.intersects(b))
                return FilterResult.REJECTED;

            return next.invoke(element, index, fileName);
        } 
        catch (Exception e) 
        {
            logger.warning("Error parseando bounds en " + fileName + " elem " + index + ": " + e.getMessage());
            return FilterResult.ERROR;
        }
    }
}
