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
