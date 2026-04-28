package com.roony.infrastructure.middleware;

import com.roony.domain.model.Element;

@FunctionalInterface
public interface Next 
{
    FilterResult invoke(Element element, int index, String fileName);
}
