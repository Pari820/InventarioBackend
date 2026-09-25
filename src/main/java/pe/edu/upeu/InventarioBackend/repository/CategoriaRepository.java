package pe.edu.upeu.InventarioBackend.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.InventarioBackend.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    @Query("select count(c) from Categoria c where lower(replace(trim(c.nombre), ' ', '')) = :nombre")
    long countByNombreNormalizado(@Param("nombre") String nombre);

    @Query("select count(c) from Categoria c where c.id <> :id and lower(replace(trim(c.nombre), ' ', '')) = :nombre")
    long countByNombreNormalizadoAndIdNot(@Param("nombre") String nombre, @Param("id") Long id);
}
