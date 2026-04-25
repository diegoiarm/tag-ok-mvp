package com.tagok.infrastructure.parser;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

public final class TryParserUtils 
{
    private TryParserUtils() {}

    public static Optional<String> getText(JsonNode node, String fieldName)
    {
        var field = node.get(fieldName);

        return ((field != null && !field.isNull())) ? Optional.of(field.asText()) : Optional.empty();
    }

    public static int parseIntOrDefault(JsonNode node, String fieldName, int defaultValue)
    {
        var field = node.get(fieldName);

        if (field == null || field.isNull())
            return defaultValue;

        if (field.isNumber())
            return field.asInt();

        if (field.isTextual())
        {
            String text = field.asText().trim().split(" ")[0];

            try
            {
                return Integer.parseInt(text);
            }
            catch (NumberFormatException ignored) { }
        }

        return defaultValue;
    }

    public static Optional<Integer> parseIntegerOrNull(JsonNode node, String fieldName)
    {
        var field = node.get(fieldName);

        if (field == null || field.isNull())
            return Optional.empty();

        if (field.isNumber())
            return Optional.of(field.asInt());

        if (field.isTextual()) {
            String text = field.asText().trim().split(" ")[0];
            try 
            {
                return Optional.of(Integer.valueOf(text));
            } 
            catch (NumberFormatException ignored) { }
        }

        return Optional.empty();
    }

    public static boolean parseBoolean(JsonNode node, String fieldName)
    {
        var field = node.get(fieldName);

        if (field == null || field.isNull())
            return false;

        String val = field.asText().trim();

        return "yes".equalsIgnoreCase(val) || "true".equalsIgnoreCase(val) || "1".equalsIgnoreCase(val);
    }
}
