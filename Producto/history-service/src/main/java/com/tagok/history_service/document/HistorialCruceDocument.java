package com.tagok.history_service.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("historial_cruces")
public class HistorialCruceDocument
{
    @Id
    private String id;

    private String usuarioId;

    private BigDecimal total;

    private List<CruceSnapshot> cruces;

    private LocalDate fecha;
}