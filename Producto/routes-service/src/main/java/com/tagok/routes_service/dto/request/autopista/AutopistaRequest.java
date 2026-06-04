package com.tagok.routes_service.dto.request.autopista;

import java.util.List;

import com.tagok.routes_service.domain.autopista.TipoCobro;
import com.tagok.routes_service.dto.request.portico.PorticoRequest;
import com.tagok.routes_service.dto.request.tramo.TramoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AutopistaRequest(
    @NotBlank(message = "El nombre es obligatorio") String autopista,
    @NotBlank(message = "El código es obligatorio") String codigo,
    @NotNull(message = "El tipo de cobro es obligatorio") TipoCobro tipoCobro,
    List<PorticoRequest> porticos,
    List<TramoRequest> tramos)
{

}
