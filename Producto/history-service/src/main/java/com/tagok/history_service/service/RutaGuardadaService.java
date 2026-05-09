//package com.tagok.history_service.service;

//public class RutaGuardadaService {
    
//}

package com.tagok.history_service.service;

import com.tagok.history_service.domain.RutaGuardada;
import com.tagok.history_service.repository.RutaGuardadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutaGuardadaService {
    private final RutaGuardadaRepository repository;

    public List<RutaGuardada> findByIdToken(String idToken) {
        return repository.findByIdToken(idToken);
    }

    public RutaGuardada save(RutaGuardada ruta) {
        return repository.save(ruta);
    }
}
