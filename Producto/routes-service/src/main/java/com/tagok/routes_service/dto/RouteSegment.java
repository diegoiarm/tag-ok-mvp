package com.tagok.routes_service.dto;

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
}
