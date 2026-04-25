package com.tagok.domain.model;

public record Tags(
    String name,
    String highway,
    String lanes,
    String surface,
    boolean oneway,
    MaxSpeed maxSpeed,
    Integer maxWeight
) 
{

}
