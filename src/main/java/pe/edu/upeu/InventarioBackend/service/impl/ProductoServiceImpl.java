package pe.edu.upeu.InventarioBackend.service.impl;

import org.slf4j.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.entity.*;
import pe.edu.upeu.InventarioBackend.exception.*;
import pe.edu.upeu.InventarioBackend.repository.*;
import pe.edu.upeu.InventarioBackend.service.service.ProductoService;

import java.math.RoundingMode;
import java.util.*;

@Service
public class ProductoServiceImpl implements ProductoService {
    private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);
    private static final Set<String> CAMPOS_ORDENABLES = Set.of("nombre", "costo", "stock");

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DespachoRepository despachoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               DespachoRepository despachoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.despachoRepository = despachoRepository;
    }

    @Override @Transactional
    public ProductoResponseDTO create(ProductoRequestDTO request) {
        String codigo = request.getCodigo().trim().toUpperCase();
        if (productoRepository.existsByCodigoIgnoreCase(codigo)) {
            log.warn("Código de producto duplicado: {}", codigo);
            throw new ReglaNegocioException("Ya existe un producto con el código " + codigo);
        }
        Categoria categoria = buscarCategoria(request.getCategoriaId());
        Producto producto = new Producto();
        aplicar(producto, request, categoria, codigo);
        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado id={}", guardado.getId());
        return convertir(guardado);
    }

    @Override @Transactional
    public ProductoResponseDTO update(Long id, ProductoRequestDTO request) {
        Producto producto = buscarProducto(id);
        String codigo = request.getCodigo().trim().toUpperCase();
        if (productoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            log.warn("Código de producto duplicado: {}", codigo);
            throw new ReglaNegocioException("Ya existe un producto con el código " + codigo);
        }
        Categoria categoria = buscarCategoria(request.getCategoriaId());
        aplicar(producto, request, categoria, codigo);
        Producto actualizado = productoRepository.saveAndFlush(producto);
        log.info("Producto actualizado id={}", id);
        return convertir(actualizado);
    }

    @Override @Transactional(readOnly = true)
    public ProductoResponseDTO read(Long id) { return convertir(buscarProducto(id)); }

    @Override @Transactional
    public void delete(Long id) {
        Producto producto = buscarProducto(id);
        if (despachoRepository.existsByDetallesProductoId(id)) {
            log.warn("No se puede eliminar producto con despachos asociados id={}", id);
            throw new ReglaNegocioException("No se puede eliminar un producto que tiene despachos asociados");
        }
        productoRepository.delete(producto);
        log.info("Producto eliminado id={}", id);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<ProductoResponseDTO> readAll() {
        return productoRepository.findAll(Sort.by("nombre")).stream().map(this::convertir).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<ProductoResponseDTO> porCategoria(Long categoriaId) {
        buscarCategoria(categoriaId);
        return productoRepository.findByCategoriaIdOrderByNombreAsc(categoriaId).stream().map(this::convertir).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscar(String nombre, Long categoriaId, Boolean stockBajo, String orden, String dir) {
        String campo = (orden == null || orden.isBlank()) ? "nombre" : orden.trim().toLowerCase();
        if (!CAMPOS_ORDENABLES.contains(campo)) {
            throw new ReglaNegocioException("Campo de orden no permitido. Use nombre, costo o stock");
        }
        String direccion = (dir == null || dir.isBlank()) ? "asc" : dir.trim().toLowerCase();
        if (!direccion.equals("asc") && !direccion.equals("desc")) {
            throw new ReglaNegocioException("La dirección debe ser asc o desc");
        }
        String propiedad = campo.equals("costo") ? "costoUnitario" : campo;
        Sort sort = direccion.equals("asc") ? Sort.by(propiedad).ascending() : Sort.by(propiedad).descending();
        String filtroNombre = (nombre == null || nombre.isBlank()) ? null : nombre.trim();
        return productoRepository.buscar(filtroNombre, categoriaId, stockBajo, sort).stream().map(this::convertir).toList();
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
    }

    private void aplicar(Producto p, ProductoRequestDTO r, Categoria c, String codigo) {
        p.setCodigo(codigo);
        p.setNombre(r.getNombre().trim());
        p.setCostoUnitario(r.getCostoUnitario().setScale(2, RoundingMode.HALF_UP));
        p.setStock(r.getStock());
        p.setStockMinimo(r.getStockMinimo());
        p.setEstado(r.getEstado());
        p.setCategoria(c);
    }

    private ProductoResponseDTO convertir(Producto p) {
        return new ProductoResponseDTO(p.getId(), p.getCodigo(), p.getNombre(), p.getCostoUnitario(), p.getStock(),
                p.getStockMinimo(), p.getEstado(), p.getCategoria().getId(), p.getCategoria().getNombre(),
                p.getFechaCreacion(), p.getFechaModificacion());
    }
}
