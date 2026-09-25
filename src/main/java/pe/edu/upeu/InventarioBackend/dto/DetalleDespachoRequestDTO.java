package pe.edu.upeu.InventarioBackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DetalleDespachoRequestDTO {
    @NotNull(message = "El producto es obligatorio")
    @Positive(message = "El identificador de producto debe ser válido")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser como mínimo 1")
    private Integer cantidad;
}
