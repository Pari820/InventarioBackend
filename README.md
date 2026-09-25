# InventarioBackend

API REST para el caso StockAndes.

## Tecnologías
- Java 21
- Spring Boot 4
- Spring Data JPA
- Oracle
- Maven
- Swagger / OpenAPI

## Estructura
- `controller`: endpoints REST
- `service`: reglas de negocio y transacciones
- `repository`: acceso a Oracle con JPA
- `dto`: objetos de entrada y salida
- `entity`: entidades JPA
- `exception`: manejo global de errores
- `config`: CORS y OpenAPI

## Perfiles
- `dev`: Oracle local, `ddl-auto: update` y Swagger habilitado.
- `prod`: credenciales por variables de entorno, `ddl-auto: validate` y Swagger deshabilitado.

## Ejecución
1. Crear en Oracle el usuario `INVENTARIODB` con contraseña `1234567`.
2. Verificar la conexión en `application-dev.yaml`.
3. Ejecutar `InventarioBackendApplication`.
4. Abrir `http://localhost:8080/swagger-ui.html`.
5. Ejecutar `sql/datos_semilla.sql` después del primer arranque.

La colección de Postman está en la carpeta `postman`.
