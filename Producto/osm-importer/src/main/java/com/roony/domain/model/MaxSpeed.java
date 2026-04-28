package com.roony.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MaxSpeed(
    @JsonProperty("maxspeed")
    Integer defaultMaxSpeed,
    @JsonProperty("maxspeed:bus")
    Integer bus,
    @JsonProperty("maxspeed:hgv")
    Integer hgv) 
{
    public MaxSpeed 
    {
        defaultMaxSpeed = defaultMaxSpeed == null ? 50 : defaultMaxSpeed;
        bus = bus == null ? null : bus;
        hgv = hgv == null ? null : hgv;
    }
}
