package com.roony.infrastructure.middleware;

import java.util.List;

import com.roony.domain.model.Element;

public class PipelineRunner 
{
    private final ElementMiddleware chain;

    PipelineRunner(List<ElementMiddleware> middlewares) 
    {
        this.chain = buildChain(middlewares, 0);
    }

    private ElementMiddleware buildChain(
        List<ElementMiddleware> list,
        int index) 
    {

        if (index >= list.size())
            return (element, i, name, next) ->
                FilterResult.ACCEPTED;

        ElementMiddleware current = list.get(index);
        ElementMiddleware rest = buildChain(list, index + 1);

        return (element, i, name, next) ->
            current.process(
                element,
                i,
                name,
                (n, idx, fname) ->
                    rest.process(n, idx, fname, next));
    }

    public FilterResult run(
        Element element,
        int index,
        String fileName) 
    {

        return chain.process(
            element,
            index,
            fileName,
            (n, i, f) -> FilterResult.ACCEPTED);
    }
}