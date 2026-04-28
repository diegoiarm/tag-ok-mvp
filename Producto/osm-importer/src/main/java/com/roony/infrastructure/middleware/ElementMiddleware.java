package com.roony.infrastructure.middleware;

import com.roony.domain.model.Element;

@FunctionalInterface
public interface ElementMiddleware 
{
    FilterResult process(Element element, int index, String fileName, Next next);
}
