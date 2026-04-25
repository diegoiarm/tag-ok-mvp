package com.tagok.domain.filter;

public record BoundingBox(
   double minLat,
   double minLon,
   double maxLat,
   double maxLon)
{}
