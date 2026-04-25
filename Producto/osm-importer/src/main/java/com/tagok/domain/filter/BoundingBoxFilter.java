package com.tagok.domain.filter;

import com.tagok.domain.model.Bounds;

public class BoundingBoxFilter 
{
    private final BoundingBox boundingBox;

    public BoundingBoxFilter(BoundingBox boundingBox) 
    {
        this.boundingBox = boundingBox;
    }

    public static BoundingBoxFilter santiagoFiltering() 
    {
        return new BoundingBoxFilter(
            new BoundingBox(
                -33.643725, // minLat
                -70.877593, // minLon
                -33.284708, // maxLat
                -70.458263  // maxLon
            ));
    }

    public boolean accept(Bounds bounds)
    {
        if (bounds == null)
            return false;

        return (bounds.minlat() >= boundingBox.minLat()) && 
            (bounds.maxlat() <= boundingBox.maxLat()) && 
            (bounds.minlon() >= boundingBox.minLon()) && 
            (bounds.maxlon() <= boundingBox.maxLon());
    }

    public boolean intersects(Bounds bounds) 
    {
        if (bounds == null)
            return false;

        return !((bounds.maxlat() < boundingBox.minLat()) ||
            (bounds.minlat() > boundingBox.maxLat()) ||
            (bounds.maxlon() < boundingBox.minLon()) ||
            (bounds.minlon() > boundingBox.maxLon()));
    }
}
