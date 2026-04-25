package com.tagok.domain.model;

import java.util.List;

public record Element(
    String type,
    long id,
    Bounds bounds,
    List<Long> nodes,
    List<Geometry> geometry,
    Tags tags) 
{

}
