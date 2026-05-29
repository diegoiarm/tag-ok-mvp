package com.tagok.routes_service.events.publishers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tagok.routes_service.events.dtos.HistorialCruceEvent;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Component
public class KafkaHistorialCrucePublisher implements HistorialCrucePublisher
{
    private static final String TOPIC = "portico-cruzado";

    private final KafkaTemplate<String, HistorialCruceEvent> kafkaTemplate;

    @Override
    public void publicar(HistorialCruceEvent evento)
    {
        if (evento.getUsuarioId() == null)
            throw new IllegalStateException("No hay id de usuario");

        kafkaTemplate.send(TOPIC, evento.getUsuarioId(), evento)
        .whenComplete((result, ex) -> 
        {
            if (ex != null) 
                System.out.println("Kafka no disponible: " + ex);
        });
    }
}
