package com.tagok.routes_service.domain.dto.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarioTarifarioResponse 
{
    private Set<ReglaTemporalResponse> reglas;
}
