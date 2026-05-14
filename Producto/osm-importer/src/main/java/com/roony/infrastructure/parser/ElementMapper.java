package com.roony.infrastructure.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
//import com.fasterxml.jackson.databind.JsonNode;
import com.roony.domain.model.Bounds;
import com.roony.domain.model.Element;
import com.roony.domain.model.Geometry;
import com.roony.domain.model.MaxSpeed;
import com.roony.domain.model.Tags;

public class ElementMapper 
{
    private final String DEFAULT_STRING = "No especificado";

    public Element map(JsonParser jp) throws IOException
    {
        String type = DEFAULT_STRING;
        long id = 0;
        Bounds bounds = null;
        List<Long> nodes = Collections.emptyList();
        List<Geometry> geometry = Collections.emptyList();
        Tags tags = null;

        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String field = jp.currentName();
            jp.nextToken();

            switch (field)
            {
                case "type":
                    type = getTextOrDefault(jp, DEFAULT_STRING);
                    break;

                case "id":
                    id = jp.getValueAsLong(0);
                    break;

                case "bounds":
                    bounds = parseBounds(jp);
                    break;

                case "nodes":
                    nodes = parseNodes(jp);
                    break;

                case "geometry":
                    geometry = parseGeometry(jp);
                    break;

                case "tags":
                    tags = parseTags(jp);
                    break;

                default:
                    jp.skipChildren();
            }
        }

        return new Element(type, id, bounds, nodes, geometry, tags);
    }

    private Bounds parseBounds(JsonParser jp) throws IOException
    {
        if (jp.currentToken() != JsonToken.START_OBJECT)
        {
            jp.skipChildren();
            return null;
        }

        double minlat = 0, minlon = 0, maxlat = 0, maxlon = 0;

        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String field = jp.currentName();
            jp.nextToken();

            switch (field)
            {
                case "minlat": minlat = jp.getValueAsDouble(); break;
                case "minlon": minlon = jp.getValueAsDouble(); break;
                case "maxlat": maxlat = jp.getValueAsDouble(); break;
                case "maxlon": maxlon = jp.getValueAsDouble(); break;
                default: jp.skipChildren();
            }
        }

        return new Bounds(minlat, minlon, maxlat, maxlon);
    }

    private List<Long> parseNodes(JsonParser jp) throws IOException
    {
        if (jp.currentToken() != JsonToken.START_ARRAY)
        {
            jp.skipChildren();
            return Collections.emptyList();
        }

        List<Long> list = new ArrayList<>();

        while (jp.nextToken() != JsonToken.END_ARRAY)
        {
            list.add(jp.getValueAsLong());
        }

        return list;
    }

    private List<Geometry> parseGeometry(JsonParser jp) throws IOException
    {
        if (jp.currentToken() != JsonToken.START_ARRAY)
        {
            jp.skipChildren();
            return Collections.emptyList();
        }

        List<Geometry> geoms = new ArrayList<>();

        while (jp.nextToken() != JsonToken.END_ARRAY)
        {
            double lat = 0, lon = 0;

            while (jp.nextToken() != JsonToken.END_OBJECT)
            {
                String field = jp.currentName();
                jp.nextToken();

                if ("lat".equals(field))
                    lat = jp.getValueAsDouble();
                else if ("lon".equals(field))
                    lon = jp.getValueAsDouble();
                else
                    jp.skipChildren();
            }

            geoms.add(new Geometry(lat, lon));
        }

        return geoms;
    }

    private Tags parseTags(JsonParser jp) throws IOException
    {
        if (jp.currentToken() != JsonToken.START_OBJECT)
        {
            jp.skipChildren();
            return null;
        }

        String name = DEFAULT_STRING;
        String highway = DEFAULT_STRING;
        String lanes = DEFAULT_STRING;
        String surface = DEFAULT_STRING;
        boolean oneway = false;

        int defaultMaxSpeed = 50;
        Integer busMaxSpeed = null;
        Integer hgvMaxSpeed = null;
        Integer maxWeight = null;

        while (jp.nextToken() != JsonToken.END_OBJECT)
        {
            String field = jp.currentName();
            jp.nextToken();

            String value = jp.getValueAsString();

            switch (field)
            {
                case "name":
                    name = safeText(value, DEFAULT_STRING);
                    break;

                case "highway":
                    highway = safeText(value, DEFAULT_STRING);
                    break;

                case "lanes":
                    lanes = safeText(value, "1");
                    break;

                case "surface":
                    surface = safeText(value, DEFAULT_STRING);
                    break;

                case "oneway":
                    oneway = parseBoolean(value);
                    break;

                case "maxspeed":
                    defaultMaxSpeed = parseIntOrDefault(value, 50);
                    break;

                case "maxspeed:bus":
                    busMaxSpeed = parseIntegerOrNull(value);
                    break;

                case "maxspeed:hgv":
                    hgvMaxSpeed = parseIntegerOrNull(value);
                    break;

                case "maxweight":
                    maxWeight = parseIntegerOrNull(value);
                    break;

                default:
                    // ignorar tags que no usas
            }
        }

        MaxSpeed maxSpeed = new MaxSpeed(defaultMaxSpeed, busMaxSpeed, hgvMaxSpeed);

        return new Tags(name, highway, lanes, surface, oneway, maxSpeed, maxWeight);
    }

    private String getTextOrDefault(JsonParser jp, String def) throws IOException
    {
        String v = jp.getValueAsString();
        return (v == null || v.isBlank()) ? def : v;
    }

    private String safeText(String v, String def)
    {
        return (v == null || v.isBlank()) ? def : v;
    }

    private boolean parseBoolean(String v)
    {
        if (v == null) return false;

        return switch (v.trim().toLowerCase())
        {
            case "yes", "true", "1" -> true;
            default -> false;
        };
    }

    private int parseIntOrDefault(String v, int def)
    {
        try
        {
            return v == null ? def : Integer.parseInt(v.replaceAll("[^0-9]", ""));
        }
        catch (Exception e)
        {
            return def;
        }
    }

    private Integer parseIntegerOrNull(String v)
    {
        try
        {
            if (v == null) return null;
            return Integer.parseInt(v.replaceAll("[^0-9]", ""));
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
