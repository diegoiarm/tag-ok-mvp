package com.tagok.routes_service.domain.dto.response;

import java.util.List;

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
    private List<ReglaTemporalResponse> reglas;
}
