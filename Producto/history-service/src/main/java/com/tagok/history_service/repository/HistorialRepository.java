//package com.tagok.history_service.repository;

//public class HistorialRepository {
    
//}

package com.tagok.history_service.repository;

import com.tagok.history_service.domain.Historial;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface HistorialRepository extends MongoRepository<Historial, String> {
    List<Historial> findByIdToken(String idToken);
}
