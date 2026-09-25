package pe.edu.upeu.InventarioBackend.service.service;

import pe.edu.upeu.InventarioBackend.dto.*;
import java.util.List;

public interface DespachoService {
    DespachoResponseDTO registrar(DespachoRequestDTO request);
    DespachoResponseDTO buscar(Long id);
    List<DespachoResponseDTO> listar();
    DespachoResponseDTO anular(Long id);
}
