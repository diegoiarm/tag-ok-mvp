package com.tagok.routes_service.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutopistaResponse 
{
    private Long id;
    private String nombre;
    private List
    <PorticoResponse> porticos;
}
