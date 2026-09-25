package pe.edu.upeu.InventarioBackend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
