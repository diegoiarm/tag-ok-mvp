package com.tagok.routes_service.dto.response.route;

import com.tagok.routes_service.dto.PorticoRuta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteSegment 
{
    private Integer seq;
    private Long edgeId;
    private Long node;

    private Double cost;
    private Double aggCost;

    private String name;

    private String geometry;

    private Double distance;
    private Double maxSpeed;

    private PorticoRuta portico;
}
