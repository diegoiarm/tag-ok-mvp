//package com.tagok.history_service.service;

//public class HistorialService {
    
//}

package com.tagok.history_service.service;

import com.tagok.history_service.domain.Historial;
import com.tagok.history_service.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialService {
    private final HistorialRepository repository;

    public List<Historial> findByIdToken(String idToken) {
        return repository.findByIdToken(idToken);
    }

    public Historial save(Historial historial) {
        return repository.save(historial);
    }

    // Agrega métodos como agregar cruce, etc., según necesites
}
