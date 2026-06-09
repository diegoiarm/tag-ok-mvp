package com.tagok.history_service.repository.historialProyeccion;

import java.math.BigDecimal;
import java.util.List;

public interface ProyeccionAnual 
{
    Integer getAño();
    Integer getCantidadCruces();
    BigDecimal getTotalAño();
    List<Integer> getMesesDisponibles();
}
