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
