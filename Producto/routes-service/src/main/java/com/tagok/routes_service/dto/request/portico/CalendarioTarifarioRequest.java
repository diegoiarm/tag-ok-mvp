package com.tagok.routes_service.dto.request.portico;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarioTarifarioRequest 
{
    private List<ReglaTemporalRequest> reglas;
}
