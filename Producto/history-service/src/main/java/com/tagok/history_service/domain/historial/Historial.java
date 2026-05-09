//package com.tagok.history_service.domain.historial;

//public class Historial 
//{
    
//}

package com.tagok.history_service.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "historiales")
public class Historial {
    @Id
    private String id;
    private String idToken; // ID del usuario/token
    private List<Dia> dias; // Lista de días con cruces
}

@Data
public static class Dia {
    private String fechaDia; // Fecha en formato ISO (e.g., "2023-10-01")
    private List<PorticoCruce> porticos; // Crucés en ese día
}

@Data
public static class PorticoCruce {
    private String codigo;
    private String nombrePortico;
    private String autopista;
    private String sentido;
    private String tipoTarifa;
    private double valor;
    private String fechaHoraCruce; // ISO Date
    private Vehiculo vehiculo;
}

@Data
public static class Vehiculo {
    private String patente;
    private String tipoVehiculo;
}
