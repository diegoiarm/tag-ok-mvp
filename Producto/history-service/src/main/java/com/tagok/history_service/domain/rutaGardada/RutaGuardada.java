//package com.tagok.history_service.domain.rutaGardada;

//public class RutaGuardada {
    
//}

package com.tagok.history_service.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "rutas_guardadas")
public class RutaGuardada {
    @Id
    private String id;
    private String idToken; // ID del usuario/token
    private List<Segmento> segments; // Segmentos de la ruta
    private List<PorticoRuta> porticos; // Pórticos en la ruta
}

@Data
public static class Segmento {
    private String name;
    private String geometry; // GeoJSON o string de geometría
}

@Data
public static class PorticoRuta {
    private String nombre;
    private String codigo;
    private String autopista;
    private String codigoAutopista;
    private double longitud;
    private double latitud;
    private String tipoTarifa;
    private double valor; // O BigDecimal si prefieres precisión
    private String fechaHora; // ISO Date
}
