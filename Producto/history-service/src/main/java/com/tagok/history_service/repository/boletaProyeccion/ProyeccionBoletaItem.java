package com.tagok.history_service.repository.boletaProyeccion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ProyeccionBoletaItem 
{
    LocalDate getFecha();
    String getAutopista();
    String getNombre();
    String getTipoTarifa();
    BigDecimal getValor();
    LocalDateTime getHoraFechaCruce();
    String getPatente();
}
