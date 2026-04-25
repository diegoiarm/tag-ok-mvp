package com.tagok.infrastructure.middleware;

import com.fasterxml.jackson.databind.JsonNode;
import com.tagok.domain.model.*;
import java.util.*;
import java.util.logging.Logger;

public class MapToDomainMiddleware implements ElementMiddleware {
    private static final Logger logger = Logger.getLogger(MapToDomainMiddleware.class.getName());
    public static final String ELEMENT_LIST_KEY = "elementList";

    @SuppressWarnings("unchecked")
    @Override
    public FilterResult process(JsonNode node, int index, String fileName,
                                Map<String, Object> context, Next next) {
        try {
            // --- Extraer type ---
            String type = node.has("type") ? node.get("type").asText() : null;

            // --- Extraer id ---
            long id = node.has("id") ? node.get("id").asLong() : 0;

            // --- Extraer bounds ---
            JsonNode boundsNode = node.get("bounds");
            Bounds bounds = null;
            if (boundsNode != null) {
                bounds = new Bounds(
                    boundsNode.get("minlat").asDouble(),
                    boundsNode.get("minlon").asDouble(),
                    boundsNode.get("maxlat").asDouble(),
                    boundsNode.get("maxlon").asDouble()
                );
            }

            // --- Extraer nodes (lista de ids) ---
            JsonNode nodesNode = node.get("nodes");
            List<Long> nodesList = Collections.emptyList();
            if (nodesNode != null && nodesNode.isArray()) {
                nodesList = new ArrayList<>();
                for (JsonNode idNode : nodesNode) {
                    nodesList.add(idNode.asLong());
                }
            }

            // --- Extraer geometry (lista de puntos) ---
            JsonNode geomNode = node.get("geometry");
            List<Geometry> geometryList = Collections.emptyList();
            if (geomNode != null && geomNode.isArray()) 
            {
                geometryList = new ArrayList<>();
                for (JsonNode point : geomNode) 
                {
                    // Cada punto es un array [lon, lat]
                    if (point.isArray() && point.size() >= 2) 
                    {
                        double lon = point.get(0).asDouble();
                        double lat = point.get(1).asDouble();

                        geometryList.add(new Geometry(lon, lat));
                    }
                }
            }

            // --- Extraer tags ---
            JsonNode tagsNode = node.get("tags");
            Tags tags = null;
            if (tagsNode != null && tagsNode.isObject()) 
            {
                String name = getTextValue(tagsNode, "name");
                String highway = getTextValue(tagsNode, "highway");
                String lanes = getTextValue(tagsNode, "lanes");
                String surface = getTextValue(tagsNode, "surface");
                boolean oneway = "yes".equalsIgnoreCase(getTextValue(tagsNode, "oneway"));

                // Extraer maxspeed y sus variantes para MaxSpeed
                int defaultMaxSpeed = parseIntOrDefault(tagsNode, "maxspeed", 0);
                Integer busMaxSpeed = parseIntegerOrNull(tagsNode, "maxspeed:bus");
                Integer hgvMaxSpeed = parseIntegerOrNull(tagsNode, "maxspeed:hgv");
                MaxSpeed maxSpeed = new MaxSpeed(defaultMaxSpeed, busMaxSpeed, hgvMaxSpeed);

                // maxWeight (puede ser decimal, pero tu campo es Integer, asumimos entero)
                Integer maxWeight = parseIntegerOrNull(tagsNode, "maxweight");

                tags = new Tags(name, highway, lanes, surface, oneway, maxSpeed, maxWeight);
            }

            // --- Al final, construye el Element con ese tags ---
            Element element = new Element(type, id, bounds, nodesList, geometryList, tags);

            // --- Almacenar en el contexto ---
            List<Element> lista = (List<Element>) context.computeIfAbsent(ELEMENT_LIST_KEY,
                    k -> new ArrayList<>());
            lista.add(element);

            // Continuar con el siguiente middleware
            return next.invoke(node, index, fileName, context);

        } catch (Exception e) {
            logger.warning("Error mapeando elemento " + index + " en " + fileName + ": " + e.getMessage());
            return FilterResult.ERROR;
        }
    }

    private String getTextValue(JsonNode node, String fieldName) 
    {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }

    private int parseIntOrDefault(JsonNode node, String fieldName, int defaultValue) 
    {
        JsonNode field = node.get(fieldName);
        if (field != null && field.isNumber()) 
        {
            return field.asInt();
        }
        if (field != null && field.isTextual()) 
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

    private Integer parseIntegerOrNull(JsonNode node, String fieldName) 
    {
        JsonNode field = node.get(fieldName);
        if (field != null && field.isNumber()) 
        {
            return field.asInt();
        }
        if (field != null && field.isTextual())
        {
            String text = field.asText().trim().split(" ")[0];
            try 
            {
                return Integer.valueOf(text);
            } 
            catch (NumberFormatException ignored) { }
        }
        return null;
    }
}