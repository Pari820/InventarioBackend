package pe.edu.upeu.InventarioBackend.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.InventarioBackend.entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    @Query("select count(a) from Area a where lower(replace(trim(a.nombre), ' ', '')) = :nombre")
    long countByNombreNormalizado(@Param("nombre") String nombre);

    @Query("select count(a) from Area a where a.id <> :id and lower(replace(trim(a.nombre), ' ', '')) = :nombre")
    long countByNombreNormalizadoAndIdNot(@Param("nombre") String nombre, @Param("id") Long id);
}
