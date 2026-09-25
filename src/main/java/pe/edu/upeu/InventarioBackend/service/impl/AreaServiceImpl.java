package pe.edu.upeu.InventarioBackend.service.impl;

import org.slf4j.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.entity.Area;
import pe.edu.upeu.InventarioBackend.exception.*;
import pe.edu.upeu.InventarioBackend.repository.*;
import pe.edu.upeu.InventarioBackend.service.service.AreaService;

import java.math.RoundingMode;
import java.util.Locale;

@Service
public class AreaServiceImpl implements AreaService {
    private static final Logger log = LoggerFactory.getLogger(AreaServiceImpl.class);

    private final AreaRepository areaRepository;
    private final DespachoRepository despachoRepository;

    public AreaServiceImpl(AreaRepository areaRepository, DespachoRepository despachoRepository) {
        this.areaRepository = areaRepository;
        this.despachoRepository = despachoRepository;
    }

    @Override @Transactional
    public AreaResponseDTO create(AreaRequestDTO request) {
        String codigo = request.getCodigo().trim().toUpperCase();
        validarUnicos(codigo, request.getNombre(), null);
        Area area = new Area();
        aplicar(area, request, codigo);
        Area guardada = areaRepository.save(area);
        log.info("Área creada id={}", guardada.getId());
        return convertir(guardada);
    }

    @Override @Transactional
    public AreaResponseDTO update(Long id, AreaRequestDTO request) {
        Area area = obtener(id);
        String codigo = request.getCodigo().trim().toUpperCase();
        validarUnicos(codigo, request.getNombre(), id);
        aplicar(area, request, codigo);
        Area actualizada = areaRepository.saveAndFlush(area);
        log.info("Área actualizada id={}", id);
        return convertir(actualizada);
    }

    @Override @Transactional(readOnly = true)
    public AreaResponseDTO read(Long id) { return convertir(obtener(id)); }

    @Override @Transactional
    public void delete(Long id) {
        Area area = obtener(id);
        if (despachoRepository.existsByAreaId(id)) {
            log.warn("No se puede eliminar área con despachos asociados id={}", id);
            throw new ReglaNegocioException("No se puede eliminar un área que tiene despachos asociados");
        }
        areaRepository.delete(area);
        log.info("Área eliminada id={}", id);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<AreaResponseDTO> readAll() {
        return areaRepository.findAll(Sort.by("nombre")).stream().map(this::convertir).toList();
    }

    private Area obtener(Long id) {
        return areaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Área no encontrada con id: " + id));
    }

    private void validarUnicos(String codigo, String nombre, Long id) {
        boolean codigoExiste = id == null ? areaRepository.existsByCodigoIgnoreCase(codigo)
                : areaRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id);
        if (codigoExiste) throw new ReglaNegocioException("Ya existe un área con ese código");

        String nombreNormalizado = nombre.trim().replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
        long cantidad = id == null ? areaRepository.countByNombreNormalizado(nombreNormalizado)
                : areaRepository.countByNombreNormalizadoAndIdNot(nombreNormalizado, id);
        if (cantidad > 0) throw new ReglaNegocioException("Ya existe un área con ese nombre");
    }

    private void aplicar(Area a, AreaRequestDTO r, String codigo) {
        a.setCodigo(codigo);
        a.setNombre(r.getNombre().trim().replaceAll("\\s+", " "));
        a.setResponsable(r.getResponsable().trim());
        a.setEmail(r.getEmail().trim().toLowerCase(Locale.ROOT));
        a.setPresupuestoMensual(r.getPresupuestoMensual().setScale(2, RoundingMode.HALF_UP));
        a.setEstado(r.getEstado());
    }

    private AreaResponseDTO convertir(Area a) {
        return new AreaResponseDTO(a.getId(), a.getCodigo(), a.getNombre(), a.getResponsable(), a.getEmail(),
                a.getPresupuestoMensual(), a.getEstado(), a.getFechaCreacion(), a.getFechaModificacion());
    }
}
