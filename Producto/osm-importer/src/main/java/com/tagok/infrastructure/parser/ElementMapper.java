package com.tagok.infrastructure.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.tagok.domain.model.Bounds;
import com.tagok.domain.model.Element;
import com.tagok.domain.model.Geometry;
import com.tagok.domain.model.MaxSpeed;
import com.tagok.domain.model.Tags;

public class ElementMapper 
{
    private final String DEFAULT_STRING = "No especificado";

    public Element map(JsonNode node) 
    {
        // --- type ---
        String type = TryParserUtils.getText(node, "type")
            .orElse(DEFAULT_STRING);

        // --- id ---
        long id = node.has("id") ? node.get("id").asLong() : 0;

        // --- bounds ---
        Bounds bounds = parseBounds(node.get("bounds"));

        // --- nodes ---
        List<Long> nodesList = parseNodeList(node.get("nodes"));

        // --- geometry ---
        List<Geometry> geometryList = parseGeometryList(node.get("geometry"));

        // --- tags ---
        Tags tags = parseTags(node.get("tags"));

        return new Element(type, id, bounds, nodesList, geometryList, tags);
    }

    private Bounds parseBounds(JsonNode bNode) 
    {
        if (bNode == null || bNode.isNull())
            return null;
        
        return new Bounds(
            bNode.get("minlat").asDouble(),
            bNode.get("minlon").asDouble(),
            bNode.get("maxlat").asDouble(),
            bNode.get("maxlon").asDouble()
        );
    }

    private List<Long> parseNodeList(JsonNode arr) 
    {
        if (arr == null || !arr.isArray()) 
            return Collections.emptyList();

        List<Long> list = new ArrayList<>(arr.size());
        for (JsonNode idNode : arr) 
        {
            list.add(idNode.asLong());
        }

        return list;
    }

    private List<Geometry> parseGeometryList(JsonNode arr) 
    {
        if (arr == null || !arr.isArray())
            return Collections.emptyList();

        List<Geometry> geoms = new ArrayList<>(arr.size());

        for (JsonNode pt : arr) 
        {
            if (pt.isObject() && pt.has("lat") && pt.has("lon")) 
            {
                double lat = pt.get("lat").asDouble();
                double lon = pt.get("lon").asDouble();
                geoms.add(new Geometry(lat, lon));
            }
        }
        return geoms;
    }

    private Tags parseTags(JsonNode tagsNode) 
    {
        if (tagsNode == null || !tagsNode.isObject()) 
            return null;

        String name = TryParserUtils.getText(tagsNode, "name")
            .orElse(DEFAULT_STRING);
        String highway = TryParserUtils.getText(tagsNode, "highway")
            .orElse(DEFAULT_STRING);;
        String lanes = TryParserUtils.getText(tagsNode, "lanes")
            .orElse(DEFAULT_STRING);;
        String surface = TryParserUtils.getText(tagsNode, "surface")
            .orElse(DEFAULT_STRING);;
        boolean oneway = TryParserUtils.parseBoolean(tagsNode, "oneway");

        int defaultMaxSpeed = TryParserUtils.parseIntOrDefault(tagsNode, "maxspeed", 50);
        Integer busMaxSpeed = TryParserUtils.parseIntegerOrNull(tagsNode, "maxspeed:bus")
            .orElse(null);
        Integer hgvMaxSpeed = TryParserUtils.parseIntegerOrNull(tagsNode, "maxspeed:hgv")
            .orElse(null);
        MaxSpeed maxSpeed = new MaxSpeed(defaultMaxSpeed, busMaxSpeed, hgvMaxSpeed);

        Integer maxWeight = TryParserUtils.parseIntegerOrNull(tagsNode, "maxweight")
            .orElse(null);;

        return new Tags(name, highway, lanes, surface, oneway, maxSpeed, maxWeight);
    }
}
