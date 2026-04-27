package com.roony.domain.filter;

public record BoundingBox(
   double minLat,
   double minLon,
   double maxLat,
   double maxLon)
{}
