package pe.edu.upeu.InventarioBackend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.InventarioBackend.entity.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
    boolean existsByCategoriaId(Long categoriaId);
    List<Producto> findByCategoriaIdOrderByNombreAsc(Long categoriaId);

    @Query("""
            select p from Producto p
            join fetch p.categoria c
            where (:nombre is null or lower(p.nombre) like lower(concat('%', :nombre, '%')))
              and (:categoriaId is null or c.id = :categoriaId)
              and (:stockBajo is null
                   or (:stockBajo = true and p.stock <= p.stockMinimo)
                   or (:stockBajo = false and p.stock > p.stockMinimo))
            """)
    List<Producto> buscar(@Param("nombre") String nombre,
                          @Param("categoriaId") Long categoriaId,
                          @Param("stockBajo") Boolean stockBajo,
                          Sort sort);
}
