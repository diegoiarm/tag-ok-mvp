package com.tagok.infrastructure.middleware;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class PipelineRunner 
{
    private final ElementMiddleware chain;

    PipelineRunner(List<ElementMiddleware> middlewares) 
    {
        this.chain = buildChain(middlewares, 0);
    }

    private ElementMiddleware buildChain(List<ElementMiddleware> list, int index) 
    {
        if (index >= list.size()) 
        {
            return (node, i, name, ctx, next) -> FilterResult.ACCEPTED;
        }

        ElementMiddleware current = list.get(index);
        ElementMiddleware rest = buildChain(list, index + 1);
        return (node, i, name, ctx, next) ->
            current.process(node, i, name, ctx,
                (n, idx, fname, c) -> rest.process(n, idx, fname, c, next));
    }

    public FilterResult run(JsonNode node, int index, String fileName, Map<String, Object> context) 
    {
        return chain.process(node, index, fileName, context,
                (n, i, f, c) -> FilterResult.ACCEPTED);
    }
}
