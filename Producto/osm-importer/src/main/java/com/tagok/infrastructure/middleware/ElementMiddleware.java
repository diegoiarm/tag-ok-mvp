package com.tagok.infrastructure.middleware;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface ElementMiddleware 
{
    FilterResult process(JsonNode node, int index, String fileName, Map<String, Object> context, Next next);
}
