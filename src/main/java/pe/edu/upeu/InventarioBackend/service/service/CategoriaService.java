package pe.edu.upeu.InventarioBackend.service.service;

import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.service.generic.CrudService;
import java.util.List;

public interface CategoriaService extends CrudService<CategoriaRequestDTO, CategoriaResponseDTO, Long> {
    List<ProductoResponseDTO> listarProductos(Long categoriaId);
}
