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

import java.math.BigDecimal;
import java.time.LocalDate;
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
        LocalDate fecha = LocalDate.now();

        HistorialCruceDocument documento = historialCruceRepository
            .findByUsuarioIdAndFecha(evento.getUsuarioId(), fecha)
            .map(d ->
            {
                d.setTotal(d.getTotal().add(evento.getTotal()));

                return d;
            })
            .orElse(toDocument(evento));

        historialCruceRepository.save(documento);
    }

    private HistorialCruceDocument toDocument(HistorialCruceEvent evento)
    {
        return HistorialCruceDocument.builder()
            .usuarioId(evento.getUsuarioId())
            .total(evento.getTotal())
            .fecha(LocalDate.now())
            .build();
    }

    public Historial save(Historial historial)
    {
        return repository.save(historial);
    }

    // Agrega métodos como agregar cruce, etc., según necesites
}
