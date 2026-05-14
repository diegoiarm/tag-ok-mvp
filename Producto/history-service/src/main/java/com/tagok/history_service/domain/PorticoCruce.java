package com.tagok.history_service.domain;

import lombok.Data;

@Data
public class PorticoCruce {
    private String codigo;
    private String nombrePortico;
    private String autopista;
    private String sentido;
    private String tipoTarifa;
    private double valor;
    private String fechaHoraCruce;
    private Vehiculo vehiculo;
}
