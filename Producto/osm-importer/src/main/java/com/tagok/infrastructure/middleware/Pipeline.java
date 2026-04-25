package com.tagok.infrastructure.middleware;

import java.util.ArrayList;
import java.util.List;

import com.tagok.domain.filter.BoundingBoxFilter;

public class Pipeline 
{
    private final List<ElementMiddleware> middlewares = new ArrayList<>();

    public Pipeline add(ElementMiddleware middleware)
    {
        middlewares.add(middleware);

        return this;
    }

    public Pipeline addBoundsFilter(BoundingBoxFilter filter)
    {
        return add(new BoundsFilterMiddleware(filter));
    }

    public PipelineRunner build()
    {
        return new PipelineRunner(middlewares);
    }
}
