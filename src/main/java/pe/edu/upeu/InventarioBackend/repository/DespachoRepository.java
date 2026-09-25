package pe.edu.upeu.InventarioBackend.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.InventarioBackend.dto.reporte.ProductoDespachadoDTO;
import pe.edu.upeu.InventarioBackend.entity.Despacho;
import pe.edu.upeu.InventarioBackend.enums.EstadoDespacho;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    boolean existsByAreaId(Long areaId);
    boolean existsByDetallesProductoId(Long productoId);

    @Query("""
            select distinct d from Despacho d
            join fetch d.area a
            left join fetch d.detalles det
            left join fetch det.producto p
            where d.id = :id
            """)
    Optional<Despacho> buscarConDetalles(@Param("id") Long id);

    @Query("""
            select distinct d from Despacho d
            join fetch d.area a
            left join fetch d.detalles det
            left join fetch det.producto p
            order by d.fecha desc
            """)
    List<Despacho> listarConDetalles();

    @Query("""
            select coalesce(sum(d.montoTotal), 0)
            from Despacho d
            where d.area.id = :areaId
              and d.estado = :estado
              and d.fecha >= :inicio
              and d.fecha < :fin
            """)
    BigDecimal totalConsumidoMes(@Param("areaId") Long areaId,
                                 @Param("estado") EstadoDespacho estado,
                                 @Param("inicio") LocalDateTime inicio,
                                 @Param("fin") LocalDateTime fin);

    @Query("""
            select new pe.edu.upeu.InventarioBackend.dto.reporte.ProductoDespachadoDTO(
                p.codigo,
                p.nombre,
                sum(det.cantidad),
                sum(det.importe)
            )
            from DetalleDespacho det
            join det.despacho d
            join det.producto p
            join p.categoria c
            where d.estado = pe.edu.upeu.InventarioBackend.enums.EstadoDespacho.REGISTRADO
              and d.fecha >= :inicio
              and d.fecha < :fin
              and (:categoriaId is null or c.id = :categoriaId)
            group by p.id, p.codigo, p.nombre
            order by sum(det.cantidad) desc, p.codigo asc
            """)
    List<ProductoDespachadoDTO> reporteProductosDespachados(@Param("inicio") LocalDateTime inicio,
                                                            @Param("fin") LocalDateTime fin,
                                                            @Param("categoriaId") Long categoriaId);
}
