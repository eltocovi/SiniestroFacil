
package cl.duoc.siniestrofacil.ms_talleres.service;

import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionResponse;
import cl.duoc.siniestrofacil.ms_talleres.model.Asignacion;
import cl.duoc.siniestrofacil.ms_talleres.model.EstadoAsignacion;
import cl.duoc.siniestrofacil.ms_talleres.model.Taller;
import cl.duoc.siniestrofacil.ms_talleres.repository.AsignacionRepository;
import cl.duoc.siniestrofacil.ms_talleres.repository.TallerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AsignacionServiceImpl implements AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final TallerRepository tallerRepository;
    private final TallerMapper tallerMapper;

    public AsignacionServiceImpl(AsignacionRepository asignacionRepository,
                                 TallerRepository tallerRepository,
                                 TallerMapper tallerMapper) {
        this.asignacionRepository = asignacionRepository;
        this.tallerRepository = tallerRepository;
        this.tallerMapper = tallerMapper;
    }

    @Override
    @Transactional
    public AsignacionResponse asignar(AsignacionRequest request) {
        String folio = request.folioDenuncia().toUpperCase();
        if (asignacionRepository.existsByFolioDenunciaAndEstadoNot(folio, EstadoAsignacion.CANCELADA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La denuncia " + folio + " ya tiene un taller asignado");
        }
        Taller taller = request.tallerId() != null
                ? validarTallerElegido(request.tallerId())
                : elegirTallerDisponible(request.comunaPreferida());

        Asignacion asignacion = new Asignacion(
                folio,
                request.patenteVehiculo().toUpperCase(),
                request.descripcionDanos(),
                LocalDateTime.now(),
                EstadoAsignacion.ASIGNADA,
                taller);
        taller.getAsignaciones().add(asignacion);
        return tallerMapper.toResponse(asignacionRepository.save(asignacion));
    }

    @Override
    public List<AsignacionResponse> listar(EstadoAsignacion estado, String folioDenuncia) {
        List<Asignacion> asignaciones;
        if (folioDenuncia != null && !folioDenuncia.isBlank()) {
            asignaciones = asignacionRepository.findByFolioDenuncia(folioDenuncia.toUpperCase());
        } else if (estado != null) {
            asignaciones = asignacionRepository.findByEstado(estado);
        } else {
            asignaciones = asignacionRepository.findAll();
        }
        return asignaciones.stream()
                .filter(a -> estado == null || a.getEstado() == estado)
                .map(tallerMapper::toResponse)
                .toList();
    }

    @Override
    public AsignacionResponse obtenerPorId(Long id) {
        return tallerMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional
    public AsignacionResponse confirmarRecepcion(Long id, String observaciones) {
        Asignacion asignacion = buscar(id);
        if (asignacion.getEstado() != EstadoAsignacion.ASIGNADA) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Solo se puede confirmar la recepción de una asignación en estado ASIGNADA (estado actual: "
                            + asignacion.getEstado() + ")");
        }
        asignacion.setEstado(EstadoAsignacion.VEHICULO_RECIBIDO);
        asignacion.setFechaRecepcion(LocalDateTime.now());
        asignacion.setObservacionesRecepcion(observaciones);
        return tallerMapper.toResponse(asignacionRepository.save(asignacion));
    }

    @Override
    @Transactional
    public AsignacionResponse cambiarEstado(Long id, EstadoAsignacion nuevoEstado) {
        Asignacion asignacion = buscar(id);
        if (nuevoEstado == EstadoAsignacion.VEHICULO_RECIBIDO) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Para registrar la recepción use PATCH /api/v1/asignaciones/{id}/recepcion");
        }
        if (!asignacion.getEstado().puedeCambiarA(nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "Transición de estado no permitida: " + asignacion.getEstado() + " -> " + nuevoEstado);
        }
        asignacion.setEstado(nuevoEstado);
        return tallerMapper.toResponse(asignacionRepository.save(asignacion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Asignacion asignacion = buscar(id);
        asignacion.getTaller().getAsignaciones().remove(asignacion);
        asignacionRepository.delete(asignacion);
    }

    private Taller elegirTallerDisponible(String comunaPreferida) {
        Comparator<Taller> porComuna = Comparator.comparing(
                t -> comunaPreferida == null || !t.getComuna().equalsIgnoreCase(comunaPreferida));
        return tallerRepository.findByDisponibleTrue().stream()
                .filter(t -> t.puedeRecibirAsignacion())
                .min(porComuna.thenComparing(t -> t.cuposLibres(), Comparator.reverseOrder()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "No hay talleres disponibles para asignar la denuncia"));
    }

    private Taller validarTallerElegido(Long tallerId) {
        Taller taller = tallerRepository.findById(tallerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado con id " + tallerId));
        if (!taller.puedeRecibirAsignacion()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "El taller " + taller.getNombre() + " no está disponible o no tiene cupos");
        }
        return taller;
    }

    private Asignacion buscar(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada con id " + id));
    }
}
