package pe.edu.upeu.InventarioBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DespachoRequestDTO {
    @NotNull(message = "El área es obligatoria")
    @Positive(message = "El identificador de área debe ser válido")
    private Long areaId;

    @Size(max = 200, message = "La observación no puede superar 200 caracteres")
    private String observacion;

    @NotEmpty(message = "El despacho debe contener al menos un producto")
    @Valid
    private List<DetalleDespachoRequestDTO> detalles;
}
