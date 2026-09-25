package pe.edu.upeu.InventarioBackend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AreaResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String responsable;
    private String email;
    private BigDecimal presupuestoMensual;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
