package pe.edu.upeu.InventarioBackend.service.impl;

import org.slf4j.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.entity.Categoria;
import pe.edu.upeu.InventarioBackend.exception.*;
import pe.edu.upeu.InventarioBackend.repository.*;
import pe.edu.upeu.InventarioBackend.service.service.CategoriaService;

import java.util.*;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    private static final Logger log = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override @Transactional
    public CategoriaResponseDTO create(CategoriaRequestDTO request) {
        validarNombre(request.getNombre(), null);
        Categoria categoria = new Categoria();
        categoria.setNombre(limpiarNombre(request.getNombre()));
        categoria.setDescripcion(request.getDescripcion());
        categoria.setEstado(request.getEstado());
        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoría creada id={}", guardada.getId());
        return convertir(guardada);
    }

    @Override @Transactional
    public CategoriaResponseDTO update(Long id, CategoriaRequestDTO request) {
        Categoria categoria = obtener(id);
        validarNombre(request.getNombre(), id);
        categoria.setNombre(limpiarNombre(request.getNombre()));
        categoria.setDescripcion(request.getDescripcion());
        categoria.setEstado(request.getEstado());
        Categoria actualizada = categoriaRepository.saveAndFlush(categoria);
        log.info("Categoría actualizada id={}", id);
        return convertir(actualizada);
    }

    @Override @Transactional(readOnly = true)
    public CategoriaResponseDTO read(Long id) { return convertir(obtener(id)); }

    @Override @Transactional
    public void delete(Long id) {
        Categoria categoria = obtener(id);
        if (productoRepository.existsByCategoriaId(id)) {
            log.warn("No se puede eliminar categoría con productos id={}", id);
            throw new ReglaNegocioException("No se puede eliminar una categoría que tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
        log.info("Categoría eliminada id={}", id);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<CategoriaResponseDTO> readAll() {
        return categoriaRepository.findAll(Sort.by("nombre")).stream().map(this::convertir).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos(Long categoriaId) {
        obtener(categoriaId);
        return productoRepository.findByCategoriaIdOrderByNombreAsc(categoriaId).stream().map(p ->
                new ProductoResponseDTO(p.getId(), p.getCodigo(), p.getNombre(), p.getCostoUnitario(), p.getStock(),
                        p.getStockMinimo(), p.getEstado(), p.getCategoria().getId(), p.getCategoria().getNombre(),
                        p.getFechaCreacion(), p.getFechaModificacion())).toList();
    }

    private Categoria obtener(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
    }

    private void validarNombre(String nombre, Long id) {
        String normalizado = normalizar(nombre);
        long cantidad = id == null
                ? categoriaRepository.countByNombreNormalizado(normalizado)
                : categoriaRepository.countByNombreNormalizadoAndIdNot(normalizado, id);
        if (cantidad > 0) {
            log.warn("Nombre de categoría duplicado: {}", nombre);
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }
    }

    private String normalizar(String nombre) {
        return nombre.trim().replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    private String limpiarNombre(String nombre) {
        return nombre.trim().replaceAll("\\s+", " ");
    }

    private CategoriaResponseDTO convertir(Categoria c) {
        return new CategoriaResponseDTO(c.getId(), c.getNombre(), c.getDescripcion(), c.getEstado(),
                c.getFechaCreacion(), c.getFechaModificacion());
    }
}
