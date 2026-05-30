package com.tagok.history_service.event.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tagok.history_service.event.dtos.HistorialCruceEvent;
import com.tagok.history_service.service.HistorialService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HistorialCruceConsumer
{
    private final HistorialService historialService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "portico-cruzado", groupId = "history-service")
    public void consumir(String evento)
    {
        try 
        {
            HistorialCruceEvent eventoMapped = objectMapper.readValue(evento, HistorialCruceEvent.class);

            historialService.guardar(eventoMapped);

            System.out.println("Historial guardado: " + eventoMapped.getEventoId());

        } 
        catch (Exception e) 
        {
            System.out.println("tac");
            e.printStackTrace();
        }

    }
}