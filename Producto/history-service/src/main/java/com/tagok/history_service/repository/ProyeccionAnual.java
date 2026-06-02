package com.tagok.history_service.repository;

import java.math.BigDecimal;

public interface ProyeccionAnual 
{
    Integer getAño();
    Integer getCantidadCruces();
    BigDecimal getTotalAño();
    Integer getCantidadMeses();
}
