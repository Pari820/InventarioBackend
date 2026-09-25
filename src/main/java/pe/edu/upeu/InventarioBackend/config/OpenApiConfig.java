package pe.edu.upeu.InventarioBackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Inventario API",
        version = "v1",
        description = "API REST de control de inventarios StockAndes"
))
public class OpenApiConfig {
}
