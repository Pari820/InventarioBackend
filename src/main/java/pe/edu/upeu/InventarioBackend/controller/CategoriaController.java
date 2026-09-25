package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.service.service.CategoriaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {
    private final CategoriaService service;
    public CategoriaController(CategoriaService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping("/{id}") public ResponseEntity<CategoriaResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.read(id)); }
    @GetMapping public ResponseEntity<Iterable<CategoriaResponseDTO>> listar() { return ResponseEntity.ok(service.readAll()); }
    @PutMapping("/{id}") public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) { return ResponseEntity.ok(service.update(id, dto)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/{id}/productos") public ResponseEntity<List<ProductoResponseDTO>> productos(@PathVariable Long id) { return ResponseEntity.ok(service.listarProductos(id)); }
}
