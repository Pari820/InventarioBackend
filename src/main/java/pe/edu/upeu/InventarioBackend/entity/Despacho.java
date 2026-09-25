package pe.edu.upeu.InventarioBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.upeu.InventarioBackend.enums.EstadoDespacho;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "despachos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Despacho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(length = 200)
    private String observacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDespacho estado;

    @Column(name = "total_unidades", nullable = false)
    private Integer totalUnidades;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDespacho> detalles = new ArrayList<>();

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
        if (estado == null) estado = EstadoDespacho.REGISTRADO;
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() { fechaModificacion = LocalDateTime.now(); }

    public void agregarDetalle(DetalleDespacho detalle) {
        detalles.add(detalle);
        detalle.setDespacho(this);
    }

    public void retirarDetalle(DetalleDespacho detalle) {
        detalles.remove(detalle);
        detalle.setDespacho(null);
    }
}
