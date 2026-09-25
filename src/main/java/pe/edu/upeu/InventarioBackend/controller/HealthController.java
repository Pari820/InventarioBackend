package pe.edu.upeu.InventarioBackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
            respuesta.put("status", "UP");
            respuesta.put("database", "UP");
            respuesta.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            respuesta.put("status", "DOWN");
            respuesta.put("database", "DOWN");
            respuesta.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(respuesta);
        }
    }
}
