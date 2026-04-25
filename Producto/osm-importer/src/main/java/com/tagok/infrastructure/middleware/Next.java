package com.tagok.infrastructure.middleware;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface Next 
{
    FilterResult invoke(JsonNode node, int index, String fileName, Map<String, Object> context);
}
