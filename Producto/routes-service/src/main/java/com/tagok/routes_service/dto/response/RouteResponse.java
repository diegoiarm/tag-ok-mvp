package com.tagok.routes_service.dto.response;

import java.util.List;

import com.tagok.routes_service.dto.RouteSegment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse 
{
    private List<RouteSegment> segments;
    private double totalCost;
}
