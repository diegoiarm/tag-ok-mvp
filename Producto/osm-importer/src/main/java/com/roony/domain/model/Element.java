package com.roony.domain.model;

import java.util.List;

public record Element(
    String type,
    long id,
    Bounds bounds,
    List<Long> nodes,
    List<Geometry> geometry,
    Tags tags) 
{
    public Element 
    {
        type = (type == null || type.isBlank()) ? "No especificado"
            : type;

        nodes = (nodes == null) ? List.of()
            : nodes;

        geometry = (geometry == null) ? List.of()
            : geometry;
    }
}
