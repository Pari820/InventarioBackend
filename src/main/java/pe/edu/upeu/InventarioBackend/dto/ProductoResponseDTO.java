package pe.edu.upeu.InventarioBackend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private BigDecimal costoUnitario;
    private Integer stock;
    private Integer stockMinimo;
    private Boolean estado;
    private Long categoriaId;
    private String categoriaNombre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
