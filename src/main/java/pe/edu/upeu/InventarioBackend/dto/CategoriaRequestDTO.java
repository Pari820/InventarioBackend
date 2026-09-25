package pe.edu.upeu.InventarioBackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoriaRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 60, message = "El nombre debe tener entre 3 y 60 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no puede superar 200 caracteres")
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
