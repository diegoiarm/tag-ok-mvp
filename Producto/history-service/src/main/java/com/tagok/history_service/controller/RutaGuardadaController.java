//package com.tagok.history_service.controller;

//public class RutaGuardadaController {
    
//}

package com.tagok.history_service.controller;

import com.tagok.history_service.domain.RutaGuardada;
import com.tagok.history_service.service.RutaGuardadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rutas-guardadas")
@RequiredArgsConstructor
public class RutaGuardadaController {
    private final RutaGuardadaService service;

    @GetMapping("/{idToken}")
    public ResponseEntity<List<RutaGuardada>> getByIdToken(@PathVariable String idToken) {
        return ResponseEntity.ok(service.findByIdToken(idToken));
    }

    @PostMapping
    public ResponseEntity<RutaGuardada> save(@RequestBody RutaGuardada ruta) {
        return ResponseEntity.status(201).body(service.save(ruta));
    }
}
