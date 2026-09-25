package pe.edu.upeu.InventarioBackend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DespachoResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private Long areaId;
    private String areaNombre;
    private String observacion;
    private String estado;
    private Integer totalUnidades;
    private BigDecimal montoTotal;
    private List<DetalleDespachoResponseDTO> detalles;
}
