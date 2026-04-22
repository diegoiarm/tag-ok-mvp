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
public class CobroResponse 
{
    @Builder.Default
    private double total = 0;
    private List<PorticoResumenResponse> porticos;

    public void addTotal(double monto)
    {
        this.total += monto;
    }
}
