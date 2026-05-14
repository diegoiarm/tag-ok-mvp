package com.tagok.history_service.domain;

import lombok.Data;

@Data
public class PorticoRuta {
    private String nombre;
    private String codigo;
    private String autopista;
    private String codigoAutopista;
    private double longitud;
    private double latitud;
    private String tipoTarifa;
    private double valor;
    private String fechaHora;
}
