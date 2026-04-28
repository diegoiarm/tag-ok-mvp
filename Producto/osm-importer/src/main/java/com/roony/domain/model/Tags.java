package com.roony.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Tags(
    String name,
    String highway,
    String lanes,
    String surface,
    boolean oneway,
    MaxSpeed maxSpeed,
    @JsonProperty("maxspeed:maxweight")
    Integer maxweight
) 
{
    public Tags 
    {
        name = (name == null || name.isBlank()) ? "No especificado"
            : name;

        highway = (highway == null || highway.isBlank()) ? "No especificado"
            : highway;

        lanes = (lanes == null || lanes.isBlank()) ? "1"
            : lanes;

        surface = (surface == null || surface.isBlank()) ? "No especificado"
            : surface;

        if (maxSpeed == null) 
            maxSpeed = new MaxSpeed(
                50,
                null,
                null);
    }
}
