package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.service.service.AreaService;

@RestController
@RequestMapping("/api/v1/areas")
public class AreaController {
    private final AreaService service;
    public AreaController(AreaService service) { this.service = service; }

    @PostMapping public ResponseEntity<AreaResponseDTO> crear(@Valid @RequestBody AreaRequestDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto)); }
    @GetMapping("/{id}") public ResponseEntity<AreaResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.read(id)); }
    @GetMapping public ResponseEntity<Iterable<AreaResponseDTO>> listar() { return ResponseEntity.ok(service.readAll()); }
    @PutMapping("/{id}") public ResponseEntity<AreaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AreaRequestDTO dto) { return ResponseEntity.ok(service.update(id, dto)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
