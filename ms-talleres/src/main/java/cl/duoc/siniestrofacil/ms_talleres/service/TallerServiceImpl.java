package cl.duoc.siniestrofacil.ms_talleres.service;

import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionResponse;
import cl.duoc.siniestrofacil.ms_talleres.dto.DisponibilidadRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.TallerRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.TallerResponse;
import cl.duoc.siniestrofacil.ms_talleres.model.Taller;
import cl.duoc.siniestrofacil.ms_talleres.repository.TallerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TallerServiceImpl implements TallerService {

    private final TallerRepository tallerRepository;
    private final TallerMapper tallerMapper;

    public TallerServiceImpl(TallerRepository tallerRepository, TallerMapper tallerMapper) {
        this.tallerRepository = tallerRepository;
        this.tallerMapper = tallerMapper;
    }

    @Override
    @Transactional
    public TallerResponse registrar(TallerRequest request) {
        String digitoVerificador = validarRut(request);
        if (tallerRepository.existsByRut(request.rut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un taller con RUT " + request.rut());
        }
        Taller taller = new Taller(
                request.nombre(),
                request.rut(),
                digitoVerificador,
                request.direccion(),
                request.comuna(),
                request.telefono(),
                request.email(),
                request.capacidadMaxima(),
                request.disponible() == null || request.disponible());
        return tallerMapper.toResponse(tallerRepository.save(taller));
    }

    @Override
    public List<TallerResponse> listar(String comuna) {
        List<Taller> talleres = (comuna != null && !comuna.isBlank())
                ? tallerRepository.findByComunaIgnoreCase(comuna)
                : tallerRepository.findAll();
        return talleres.stream().map(tallerMapper::toResponse).toList();
    }

    @Override
    public List<TallerResponse> listarDisponibles(String comuna) {
        return tallerRepository.findByDisponibleTrue().stream()
                .filter(t -> t.puedeRecibirAsignacion())
                .filter(t -> comuna == null || comuna.isBlank() || t.getComuna().equalsIgnoreCase(comuna))
                .map(tallerMapper::toResponse)
                .toList();
    }

    @Override
    public TallerResponse obtenerPorId(Long id) {
        return tallerMapper.toResponse(buscar(id));
    }

    @Override
    public List<AsignacionResponse> listarAsignaciones(Long tallerId) {
        return buscar(tallerId).getAsignaciones().stream()
                .map(tallerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TallerResponse actualizar(Long id, TallerRequest request) {
        Taller taller = buscar(id);
        String digitoVerificador = validarRut(request);
        if (!taller.getRut().equals(request.rut()) && tallerRepository.existsByRut(request.rut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un taller con RUT " + request.rut());
        }
        taller.setNombre(request.nombre());
        taller.setRut(request.rut());
        taller.setDv(digitoVerificador);
        taller.setDireccion(request.direccion());
        taller.setComuna(request.comuna());
        taller.setTelefono(request.telefono());
        taller.setEmail(request.email());
        taller.setCapacidadMaxima(request.capacidadMaxima());
        if (request.disponible() != null) {
            taller.setDisponible(request.disponible());
        }
        return tallerMapper.toResponse(tallerRepository.save(taller));
    }

    @Override
    @Transactional
    public TallerResponse actualizarDisponibilidad(Long id, DisponibilidadRequest request) {
        Taller taller = buscar(id);
        taller.setDisponible(request.disponible());
        if (request.capacidadMaxima() != null) {
            taller.setCapacidadMaxima(request.capacidadMaxima());
        }
        return tallerMapper.toResponse(tallerRepository.save(taller));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Taller taller = buscar(id);
        if (!taller.getAsignaciones().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "No se puede eliminar el taller porque tiene asignaciones registradas; márquelo como no disponible");
        }
        tallerRepository.delete(taller);
    }

    private String validarRut(TallerRequest request) {
        String digitoVerificador = request.digitoVerificador().toUpperCase();
        if (!(request.rut() + "-" + digitoVerificador).matches("\\d{7,8}-[0-9Kk]")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "El formato del RUT no es válido");
        }
        return digitoVerificador;
    }

    private Taller buscar(Long id) {
        return tallerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado con id " + id));
    }
}
