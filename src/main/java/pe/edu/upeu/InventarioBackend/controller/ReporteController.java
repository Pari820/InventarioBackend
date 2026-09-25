package pe.edu.upeu.InventarioBackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.reporte.ProductoDespachadoDTO;
import pe.edu.upeu.InventarioBackend.service.service.ReporteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {
    private final ReporteService service;
    public ReporteController(ReporteService service) { this.service = service; }

    @GetMapping("/productos-despachados")
    public ResponseEntity<List<ProductoDespachadoDTO>> productosDespachados(
            @RequestParam String periodo,
            @RequestParam(required = false) Long categoriaId) {
        return ResponseEntity.ok(service.productosDespachados(periodo, categoriaId));
    }
}
