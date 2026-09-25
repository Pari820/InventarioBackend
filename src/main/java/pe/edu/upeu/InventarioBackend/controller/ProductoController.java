package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.service.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
    private final ProductoService service;
    public ProductoController(ProductoService service) { this.service = service; }

    @PostMapping public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto)); }
    @GetMapping("/{id}") public ResponseEntity<ProductoResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.read(id)); }
    @GetMapping public ResponseEntity<Iterable<ProductoResponseDTO>> listar() { return ResponseEntity.ok(service.readAll()); }
    @PutMapping("/{id}") public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) { return ResponseEntity.ok(service.update(id, dto)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDTO>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean stockBajo,
            @RequestParam(required = false, defaultValue = "nombre") String orden,
            @RequestParam(required = false, defaultValue = "asc") String dir) {
        return ResponseEntity.ok(service.buscar(nombre, categoriaId, stockBajo, orden, dir));
    }
}
