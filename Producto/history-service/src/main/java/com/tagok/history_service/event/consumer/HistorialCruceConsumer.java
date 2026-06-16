package com.tagok.history_service.event.consumer;

import com.tagok.history_service.event.dtos.HistorialCruceEvent;
import com.tagok.history_service.exception.HistorialProcessingException;
import com.tagok.history_service.service.HistorialService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.roony11_1.error.core.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HistorialCruceConsumer {

    private final ObjectMapper objectMapper;
    private final HistorialService historialService;

    @KafkaListener(topics = "portico-cruzado", groupId = "history-service")
    public void consumir(String evento) 
    {
        try 
        {
            HistorialCruceEvent eventoMapped = objectMapper.readValue(evento, HistorialCruceEvent.class);
            historialService.saveEvent(eventoMapped);
            log.info("Historial guardado: {}", eventoMapped.getEventoId());
        } 
        catch (JsonProcessingException e) 
        {
            ErrorHandler.toErrorResponse(e);
        } 
        catch (Exception e) 
        {

            throw new HistorialProcessingException("Error al procesar evento de historial");
        }
    }
}