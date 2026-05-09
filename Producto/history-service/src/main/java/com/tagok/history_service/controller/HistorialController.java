//package com.tagok.history_service.controller;

//public class HistorialController {
    
//}
package com.tagok.history_service.controller;

import com.tagok.history_service.domain.Historial;
import com.tagok.history_service.service.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/historial")
@RequiredArgsConstructor
public class HistorialController {
    private final HistorialService service;

    @GetMapping("/{idToken}")
    public ResponseEntity<List<Historial>> getByIdToken(@PathVariable String idToken) {
        return ResponseEntity.ok(service.findByIdToken(idToken));
    }

    @PostMapping
    public ResponseEntity<Historial> save(@RequestBody Historial historial) {
        return ResponseEntity.status(201).body(service.save(historial));
    }
}