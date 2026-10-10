package cl.duoc.siniestrofacil.ms_poliza.service;

import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;
import cl.duoc.siniestrofacil.ms_poliza.model.Asegurado;
import cl.duoc.siniestrofacil.ms_poliza.repository.AseguradoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AseguradoServiceImpl implements AseguradoService {

    private final AseguradoRepository aseguradoRepository;
    private final PolizaMapper polizaMapper;

    // Inyección de dependencias por constructor
    public AseguradoServiceImpl(AseguradoRepository aseguradoRepository, PolizaMapper polizaMapper) {
        this.aseguradoRepository = aseguradoRepository;
        this.polizaMapper = polizaMapper;
    }

    @Override
    @Transactional
    public AseguradoResponse registrar(AseguradoRequest request) {
        String dv = validarRut(request);
        if (aseguradoRepository.existsByRut(request.rut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un asegurado con RUT " + request.rut());
        }
        if (aseguradoRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un asegurado con email " + request.email());
        }
        Asegurado asegurado = new Asegurado(request.rut(), dv, request.nombre(), request.email(), request.telefono());
        return polizaMapper.toResponse(aseguradoRepository.save(asegurado));
    }

    @Override
    public List<AseguradoResponse> listar() {
        return aseguradoRepository.findAll().stream()
                .map(a -> polizaMapper.toResponse(a))
                .toList();
    }

    @Override
    public AseguradoResponse obtenerPorId(Long id) {
        return polizaMapper.toResponse(buscar(id));
    }

    @Override
    public AseguradoResponse obtenerPorRut(Integer rut) {
        return aseguradoRepository.findByRut(rut)
                .map(a -> polizaMapper.toResponse(a))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asegurado no encontrado con RUT " + rut));
    }

    @Override
    public List<PolizaResponse> listarPolizas(Long aseguradoId) {
        return buscar(aseguradoId).getPolizas().stream()
                .map(p -> polizaMapper.toResponse(p))
                .toList();
    }

    @Override
    @Transactional
    public AseguradoResponse actualizar(Long id, AseguradoRequest request) {
        Asegurado asegurado = buscar(id);
        String dv = validarRut(request);
        if (!asegurado.getRut().equals(request.rut()) && aseguradoRepository.existsByRut(request.rut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un asegurado con RUT " + request.rut());
        }
        if (!asegurado.getEmail().equalsIgnoreCase(request.email()) && aseguradoRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un asegurado con email " + request.email());
        }
        asegurado.setRut(request.rut());
        asegurado.setDv(dv);
        asegurado.setNombre(request.nombre());
        asegurado.setEmail(request.email());
        asegurado.setTelefono(request.telefono());
        return polizaMapper.toResponse(aseguradoRepository.save(asegurado));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Asegurado asegurado = buscar(id);
        if (!asegurado.getPolizas().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "No se puede eliminar el asegurado porque tiene pólizas registradas");
        }
        aseguradoRepository.delete(asegurado);
    }

    /** Valida el RUT con el formato de ms-usuarios (12345678-K) y devuelve el dígito verificador en mayúscula. */
    private String validarRut(AseguradoRequest request) {
        String dv = request.digitoVerificador().toUpperCase();
        if (!(request.rut() + "-" + dv).matches("\\d{7,8}-[0-9Kk]")) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "El formato del RUT no es válido");
        }
        return dv;
    }

    private Asegurado buscar(Long id) {
        return aseguradoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asegurado no encontrado con id " + id));
    }
}
