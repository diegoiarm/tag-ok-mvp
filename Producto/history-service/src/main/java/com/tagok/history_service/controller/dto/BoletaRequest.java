package com.tagok.history_service.controller.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class BoletaRequest 
{
    private String patente;
    private List<String> autopistas;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
}
