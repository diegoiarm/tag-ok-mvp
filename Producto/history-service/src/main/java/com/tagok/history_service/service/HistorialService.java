//package com.tagok.history_service.service;

//public class HistorialService {
    
//}

package com.tagok.history_service.service;

import com.tagok.history_service.document.CruceSnapshotDocument;
import com.tagok.history_service.document.HistorialCruceDocument;
import com.tagok.history_service.domain.Historial;
import com.tagok.history_service.event.dtos.HistorialCruceEvent;
import com.tagok.history_service.repository.HistorialCruceRepository;
import com.tagok.history_service.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialService 
{
    private final HistorialRepository repository;
    private final HistorialCruceRepository historialCruceRepository;

    public List<Historial> findByIdToken(String idToken) {
        return repository.findByIdToken(idToken);
    }

    public List<HistorialCruceDocument> getAll()
    {
        return historialCruceRepository.findAll();
    }

    public void guardar(HistorialCruceEvent evento)
    {
        HistorialCruceDocument documento =
            HistorialCruceDocument.builder()
                .eventoId(evento.getEventoId())
                .usuarioId(evento.getUsuarioId())
                .total(evento.getTotal())
                .tipoVehiculo(evento.getTipoVehiculo())
                .fechaGeneracion(evento.getFechaGeneracion())
                .cruces(
                    evento.getCruces()
                        .stream()
                        .map(c ->
                            CruceSnapshotDocument.builder()
                                .codigo(c.getCodigo())
                                .nombre(c.getNombre())
                                .autopista(c.getAutopista())
                                .tipoTarifa(c.getTipoTarifa())
                                .valor(c.getValor())
                                .horaFechaCruce(c.getHoraFechaCruce())
                                .build()
                        )
                        .toList()
                )
                .build();

        historialCruceRepository.save(documento);
    }

    public Historial save(Historial historial)
    {
        return repository.save(historial);
    }

    // Agrega métodos como agregar cruce, etc., según necesites
}
