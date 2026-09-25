package pe.edu.upeu.InventarioBackend.service.impl;

import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.dto.reporte.ProductoDespachadoDTO;
import pe.edu.upeu.InventarioBackend.repository.DespachoRepository;
import pe.edu.upeu.InventarioBackend.service.service.ReporteService;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {
    private static final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);
    private final DespachoRepository despachoRepository;

    public ReporteServiceImpl(DespachoRepository despachoRepository) {
        this.despachoRepository = despachoRepository;
    }

    @Override @Transactional(readOnly = true)
    public List<ProductoDespachadoDTO> productosDespachados(String periodo, Long categoriaId) {
        YearMonth mes;
        try {
            mes = YearMonth.parse(periodo);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("El periodo debe tener formato AAAA-MM");
        }
        LocalDateTime inicio = mes.atDay(1).atStartOfDay();
        LocalDateTime fin = inicio.plusMonths(1);
        List<ProductoDespachadoDTO> resultado = despachoRepository.reporteProductosDespachados(inicio, fin, categoriaId);
        log.info("Reporte productos despachados | periodo={} | categoriaId={} | filas={}",
                periodo, categoriaId, resultado.size());
        return resultado;
    }
}
