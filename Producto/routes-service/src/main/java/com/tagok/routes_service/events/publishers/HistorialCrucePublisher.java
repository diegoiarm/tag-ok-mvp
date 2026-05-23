package com.tagok.routes_service.events.publishers;

import com.tagok.routes_service.events.dtos.HistorialCruceEvent;

public interface HistorialCrucePublisher 
{
    void publicar(HistorialCruceEvent evento);
}
