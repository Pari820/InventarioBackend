package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.service.service.DespachoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/despachos")
public class DespachoController {
    private final DespachoService service;
    public DespachoController(DespachoService service) { this.service = service; }

    @PostMapping public ResponseEntity<DespachoResponseDTO> registrar(@Valid @RequestBody DespachoRequestDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto)); }
    @GetMapping("/{id}") public ResponseEntity<DespachoResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.buscar(id)); }
    @GetMapping public ResponseEntity<List<DespachoResponseDTO>> listar() { return ResponseEntity.ok(service.listar()); }
    @PatchMapping("/{id}/anular") public ResponseEntity<DespachoResponseDTO> anular(@PathVariable Long id) { return ResponseEntity.ok(service.anular(id)); }
}
