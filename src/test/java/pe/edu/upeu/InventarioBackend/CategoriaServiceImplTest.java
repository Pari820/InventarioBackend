package pe.edu.upeu.InventarioBackend;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.InventarioBackend.dto.CategoriaRequestDTO;
import pe.edu.upeu.InventarioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.InventarioBackend.repository.*;
import pe.edu.upeu.InventarioBackend.service.impl.CategoriaServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaServiceImplTest {
    @Test
    void rechazaNombreDuplicadoIgnorandoMayusculasYEspacios() {
        CategoriaRepository categoriaRepository = mock(CategoriaRepository.class);
        ProductoRepository productoRepository = mock(ProductoRepository.class);
        when(categoriaRepository.countByNombreNormalizado("útilesdeoficina")).thenReturn(1L);

        CategoriaServiceImpl service = new CategoriaServiceImpl(categoriaRepository, productoRepository);
        CategoriaRequestDTO dto = new CategoriaRequestDTO("  útiles   de oficina  ", "Duplicada", true);

        assertThrows(ReglaNegocioException.class, () -> service.create(dto));
        verify(categoriaRepository, never()).save(any());
    }
}
