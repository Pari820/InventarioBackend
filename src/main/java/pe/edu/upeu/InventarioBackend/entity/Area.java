package pe.edu.upeu.InventarioBackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "areas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4, unique = true)
    private String codigo;

    @Column(nullable = false, length = 120, unique = true)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String responsable;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "presupuesto_mensual", nullable = false, precision = 12, scale = 2)
    private BigDecimal presupuestoMensual;

    @Column(nullable = false)
    private Boolean estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() { fechaModificacion = LocalDateTime.now(); }
}
