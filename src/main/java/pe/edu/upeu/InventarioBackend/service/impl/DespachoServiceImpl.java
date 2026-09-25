package pe.edu.upeu.InventarioBackend.service.impl;

import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.entity.*;
import pe.edu.upeu.InventarioBackend.enums.EstadoDespacho;
import pe.edu.upeu.InventarioBackend.exception.*;
import pe.edu.upeu.InventarioBackend.repository.*;
import pe.edu.upeu.InventarioBackend.service.service.DespachoService;

import java.math.*;
import java.time.*;
import java.util.*;

@Service
public class DespachoServiceImpl implements DespachoService {
    private static final Logger log = LoggerFactory.getLogger(DespachoServiceImpl.class);

    private final DespachoRepository despachoRepository;
    private final AreaRepository areaRepository;
    private final ProductoRepository productoRepository;

    public DespachoServiceImpl(DespachoRepository despachoRepository,
                               AreaRepository areaRepository,
                               ProductoRepository productoRepository) {
        this.despachoRepository = despachoRepository;
        this.areaRepository = areaRepository;
        this.productoRepository = productoRepository;
    }

    @Override @Transactional
    public DespachoResponseDTO registrar(DespachoRequestDTO request) {
        Area area = areaRepository.findById(request.getAreaId()).orElseThrow(() ->
                new RecursoNoEncontradoException("Área no encontrada con id: " + request.getAreaId()));

        if (!Boolean.TRUE.equals(area.getEstado())) {
            log.warn("RN-01 área inactiva | areaId={}", area.getId());
            throw new ReglaNegocioException("Solo se puede despachar a un área activa");
        }

        Despacho despacho = new Despacho();
        despacho.setFecha(LocalDateTime.now());
        despacho.setArea(area);
        despacho.setObservacion(request.getObservacion());
        despacho.setEstado(EstadoDespacho.REGISTRADO);

        Set<Long> productosUnicos = new HashSet<>();
        int totalUnidades = 0;
        BigDecimal montoTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (DetalleDespachoRequestDTO item : request.getDetalles()) {
            if (!productosUnicos.add(item.getProductoId())) {
                log.warn("RN-04 producto repetido | productoId={}", item.getProductoId());
                throw new ReglaNegocioException("Un producto no puede repetirse dentro del mismo despacho");
            }

            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow(() ->
                    new RecursoNoEncontradoException("Producto no encontrado con id: " + item.getProductoId()));

            if (!Boolean.TRUE.equals(producto.getEstado())) {
                log.warn("RN-01 producto inactivo | productoId={}", producto.getId());
                throw new ReglaNegocioException("Solo se pueden despachar productos activos");
            }

            if (item.getCantidad() > producto.getStock()) {
                log.warn("RN-02 stock insuficiente | productoId={} | solicitado={} | stock={}",
                        producto.getId(), item.getCantidad(), producto.getStock());
                throw new ReglaNegocioException("Stock insuficiente para el producto " + producto.getCodigo());
            }

            BigDecimal costo = producto.getCostoUnitario().setScale(2, RoundingMode.HALF_UP);
            BigDecimal importe = costo.multiply(BigDecimal.valueOf(item.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setCostoUnitario(costo);
            detalle.setImporte(importe);
            despacho.agregarDetalle(detalle);

            totalUnidades += item.getCantidad();
            montoTotal = montoTotal.add(importe).setScale(2, RoundingMode.HALF_UP);
            producto.setStock(producto.getStock() - item.getCantidad());
        }

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = inicioMes.plusMonths(1);
        BigDecimal consumido = despachoRepository.totalConsumidoMes(
                area.getId(), EstadoDespacho.REGISTRADO, inicioMes, finMes);
        if (consumido == null) consumido = BigDecimal.ZERO;
        BigDecimal acumulado = consumido.add(montoTotal).setScale(2, RoundingMode.HALF_UP);

        if (acumulado.compareTo(area.getPresupuestoMensual()) > 0) {
            log.warn("RN-03 presupuesto superado | areaId={} | consumido={} | nuevo={} | presupuesto={}",
                    area.getId(), consumido, montoTotal, area.getPresupuestoMensual());
            throw new ReglaNegocioException("El despacho supera el presupuesto mensual disponible del área");
        }

        despacho.setTotalUnidades(totalUnidades);
        despacho.setMontoTotal(montoTotal);
        Despacho guardado = despachoRepository.save(despacho);
        log.info("Despacho registrado id={} | unidades={} | monto={}", guardado.getId(), totalUnidades, montoTotal);
        return convertir(guardado);
    }

    @Override @Transactional(readOnly = true)
    public DespachoResponseDTO buscar(Long id) { return convertir(obtenerConDetalles(id)); }

    @Override @Transactional(readOnly = true)
    public List<DespachoResponseDTO> listar() {
        return despachoRepository.listarConDetalles().stream().map(this::convertir).toList();
    }

    @Override @Transactional
    public DespachoResponseDTO anular(Long id) {
        Despacho despacho = obtenerConDetalles(id);
        if (despacho.getEstado() != EstadoDespacho.REGISTRADO) {
            log.warn("Anulación inválida | despachoId={} | estado={}", id, despacho.getEstado());
            throw new ReglaNegocioException("Solo se puede anular un despacho en estado REGISTRADO");
        }
        for (DetalleDespacho detalle : despacho.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }
        despacho.setEstado(EstadoDespacho.ANULADO);
        Despacho actualizado = despachoRepository.saveAndFlush(despacho);
        log.info("Despacho anulado id={}", id);
        return convertir(actualizado);
    }

    private Despacho obtenerConDetalles(Long id) {
        return despachoRepository.buscarConDetalles(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Despacho no encontrado con id: " + id));
    }

    private DespachoResponseDTO convertir(Despacho d) {
        List<DetalleDespachoResponseDTO> detalles = d.getDetalles().stream().map(det ->
                new DetalleDespachoResponseDTO(det.getProducto().getId(), det.getProducto().getCodigo(),
                        det.getProducto().getNombre(), det.getCantidad(), det.getCostoUnitario(), det.getImporte()))
                .toList();
        return new DespachoResponseDTO(d.getId(), d.getFecha(), d.getArea().getId(), d.getArea().getNombre(),
                d.getObservacion(), d.getEstado().name(), d.getTotalUnidades(), d.getMontoTotal(), detalles);
    }
}
