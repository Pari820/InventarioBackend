package pe.edu.upeu.InventarioBackend.dto.reporte;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductoDespachadoDTO {
    private String codigo;
    private String producto;
    private Long unidadesDespachadas;
    private BigDecimal montoTotal;
}
