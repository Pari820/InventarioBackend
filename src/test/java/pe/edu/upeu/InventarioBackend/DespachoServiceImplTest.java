package pe.edu.upeu.InventarioBackend;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.entity.*;
import pe.edu.upeu.InventarioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.InventarioBackend.repository.*;
import pe.edu.upeu.InventarioBackend.service.impl.DespachoServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DespachoServiceImplTest {
    @Test
    void rn01RechazaAreaInactiva() {
        DespachoRepository dr = mock(DespachoRepository.class);
        AreaRepository ar = mock(AreaRepository.class);
        ProductoRepository pr = mock(ProductoRepository.class);
        Area area = new Area(); area.setId(4L); area.setEstado(false);
        when(ar.findById(4L)).thenReturn(java.util.Optional.of(area));

        DespachoServiceImpl service = new DespachoServiceImpl(dr, ar, pr);
        DespachoRequestDTO dto = new DespachoRequestDTO(4L, null,
                List.of(new DetalleDespachoRequestDTO(1L, 1)));

        assertThrows(ReglaNegocioException.class, () -> service.registrar(dto));
        verify(dr, never()).save(any());
    }

    @Test
    void registra17UnidadesY280() {
        DespachoRepository dr = mock(DespachoRepository.class);
        AreaRepository ar = mock(AreaRepository.class);
        ProductoRepository pr = mock(ProductoRepository.class);

        Area area = new Area();
        area.setId(1L); area.setNombre("Secretaría Académica"); area.setEstado(true);
        area.setPresupuestoMensual(new BigDecimal("1500.00"));
        when(ar.findById(1L)).thenReturn(java.util.Optional.of(area));
        when(dr.totalConsumidoMes(anyLong(), any(), any(), any())).thenReturn(BigDecimal.ZERO);

        Producto p1 = producto(1L, "OFI-001", "18.50", 120);
        Producto p2 = producto(2L, "OFI-002", "9.00", 40);
        Producto p8 = producto(8L, "TEC-002", "25.00", 30);
        when(pr.findById(1L)).thenReturn(java.util.Optional.of(p1));
        when(pr.findById(2L)).thenReturn(java.util.Optional.of(p2));
        when(pr.findById(8L)).thenReturn(java.util.Optional.of(p8));
        when(dr.save(any(Despacho.class))).thenAnswer(inv -> { Despacho d = inv.getArgument(0); d.setId(1L); return d; });

        DespachoServiceImpl service = new DespachoServiceImpl(dr, ar, pr);
        DespachoRequestDTO dto = new DespachoRequestDTO(1L, "Pedido de prueba", List.of(
                new DetalleDespachoRequestDTO(1L, 10),
                new DetalleDespachoRequestDTO(2L, 5),
                new DetalleDespachoRequestDTO(8L, 2)));

        DespachoResponseDTO r = service.registrar(dto);
        assertEquals(17, r.getTotalUnidades());
        assertEquals(new BigDecimal("280.00"), r.getMontoTotal());
        assertEquals(110, p1.getStock());
        assertEquals(35, p2.getStock());
        assertEquals(28, p8.getStock());
    }

    private Producto producto(Long id, String codigo, String costo, int stock) {
        Producto p = new Producto();
        p.setId(id); p.setCodigo(codigo); p.setNombre(codigo); p.setCostoUnitario(new BigDecimal(costo));
        p.setStock(stock); p.setStockMinimo(0); p.setEstado(true);
        return p;
    }
}
