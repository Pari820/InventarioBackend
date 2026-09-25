package pe.edu.upeu.InventarioBackend.service.service;

import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.service.generic.CrudService;
import java.util.List;

public interface ProductoService extends CrudService<ProductoRequestDTO, ProductoResponseDTO, Long> {
    List<ProductoResponseDTO> buscar(String nombre, Long categoriaId, Boolean stockBajo, String orden, String dir);
    List<ProductoResponseDTO> porCategoria(Long categoriaId);
}
