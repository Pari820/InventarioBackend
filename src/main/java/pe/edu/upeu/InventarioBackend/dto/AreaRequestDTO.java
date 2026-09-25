package pe.edu.upeu.InventarioBackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AreaRequestDTO {
    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^AR\\d{2}$", message = "El código debe tener formato AR99")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El responsable es obligatorio")
    private String responsable;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String email;

    @NotNull(message = "El presupuesto mensual es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El presupuesto mensual no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El presupuesto mensual debe tener como máximo 2 decimales")
    private BigDecimal presupuestoMensual;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
