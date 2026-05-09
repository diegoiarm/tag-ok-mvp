//package com.tagok.history_service.repository;

//public class RutaGuardadaRepository {
    
//}

package com.tagok.history_service.repository;

import com.tagok.history_service.domain.RutaGuardada;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RutaGuardadaRepository extends MongoRepository<RutaGuardada, String> {
    List<RutaGuardada> findByIdToken(String idToken);
}
