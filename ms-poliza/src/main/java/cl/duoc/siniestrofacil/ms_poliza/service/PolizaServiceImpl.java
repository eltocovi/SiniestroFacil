package cl.duoc.siniestrofacil.ms_poliza.service;

import cl.duoc.siniestrofacil.ms_poliza.dto.CoberturaRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.CoberturaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.VigenciaResponse;
import cl.duoc.siniestrofacil.ms_poliza.model.Asegurado;
import cl.duoc.siniestrofacil.ms_poliza.model.Cobertura;
import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;
import cl.duoc.siniestrofacil.ms_poliza.model.Poliza;
import cl.duoc.siniestrofacil.ms_poliza.model.TipoCobertura;
import cl.duoc.siniestrofacil.ms_poliza.repository.AseguradoRepository;
import cl.duoc.siniestrofacil.ms_poliza.repository.PolizaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PolizaServiceImpl implements PolizaService {

    private final PolizaRepository polizaRepository;
    private final AseguradoRepository aseguradoRepository;
    private final PolizaMapper polizaMapper;

    // Inyección de dependencias por constructor
    public PolizaServiceImpl(PolizaRepository polizaRepository,
                             AseguradoRepository aseguradoRepository,
                             PolizaMapper polizaMapper) {
        this.polizaRepository = polizaRepository;
        this.aseguradoRepository = aseguradoRepository;
        this.polizaMapper = polizaMapper;
    }

    @Override
    @Transactional
    public PolizaResponse emitir(PolizaRequest request) {
        validarFechas(request);
        if (request.fechaTermino().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "No se puede emitir una póliza cuya fecha de término ya pasó");
        }
        validarCoberturas(request.coberturas());
        String patente = request.patenteVehiculo().toUpperCase();
        validarSinOtraPolizaActiva(patente, null);
        Asegurado asegurado = buscarAsegurado(request.aseguradoId());

        Poliza poliza = new Poliza();
        poliza.setNumeroPoliza(generarNumeroPoliza());
        poliza.setEstado(EstadoPoliza.ACTIVA);
        copiarDatos(request, patente, poliza);
        poliza.setAsegurado(asegurado);
        asegurado.getPolizas().add(poliza);
        return polizaMapper.toResponse(polizaRepository.save(poliza));
    }

    @Override
    public List<PolizaResponse> listar(EstadoPoliza estado, String patente) {
        List<Poliza> polizas = (patente != null && !patente.isBlank())
                ? polizaRepository.findByPatenteVehiculo(patente.toUpperCase())
                : polizaRepository.findAll();
        LocalDate hoy = LocalDate.now();
        return polizas.stream()
                .filter(p -> estado == null || p.estadoA(hoy) == estado)
                .map(p -> polizaMapper.toResponse(p))
                .toList();
    }

    @Override
    public PolizaResponse obtenerPorId(Long id) {
        return polizaMapper.toResponse(buscar(id));
    }

    @Override
    public PolizaResponse obtenerPorNumero(String numeroPoliza) {
        return polizaRepository.findByNumeroPoliza(numeroPoliza.toUpperCase())
                .map(p -> polizaMapper.toResponse(p))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Póliza no encontrada con número " + numeroPoliza));
    }

    @Override
    public List<CoberturaResponse> listarCoberturas(Long polizaId) {
        return polizaMapper.toResponse(buscar(polizaId).getCoberturas());
    }

    /**
     * Verifica si el vehículo tenía una póliza vigente en la fecha indicada (por defecto, hoy).
     * Siempre responde 200: el resultado se indica en el campo "vigente" y, si no lo está, en "motivo".
     */
    @Override
    public VigenciaResponse consultarVigencia(String patente, LocalDate fecha) {
        String patenteNormalizada = patente.toUpperCase();
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        List<Poliza> polizas = polizaRepository.findByPatenteVehiculo(patenteNormalizada);

        Optional<Poliza> vigente = polizas.stream().filter(p -> p.estaVigenteEn(fechaConsulta)).findFirst();
        if (vigente.isPresent()) {
            return vigencia(patenteNormalizada, fechaConsulta, true, null, vigente.get());
        }
        if (polizas.isEmpty()) {
            return new VigenciaResponse(patenteNormalizada, fechaConsulta, false,
                    "El vehículo no tiene pólizas registradas", null, null, null, null, null, null, null, null);
        }
        // Se informa la póliza más reciente para explicar por qué no hay cobertura
        Poliza masReciente = polizas.stream()
                .max(Comparator.comparing(p -> p.getFechaTermino()))
                .orElseThrow();
        String motivo = masReciente.getEstado() == EstadoPoliza.SUSPENDIDA
                ? "La póliza " + masReciente.getNumeroPoliza() + " está suspendida"
                : "La póliza " + masReciente.getNumeroPoliza() + " no cubre la fecha consultada";
        return vigencia(patenteNormalizada, fechaConsulta, false, motivo, masReciente);
    }

    @Override
    @Transactional
    public PolizaResponse actualizar(Long id, PolizaRequest request) {
        Poliza poliza = buscar(id);
        validarFechas(request);
        validarCoberturas(request.coberturas());
        String patente = request.patenteVehiculo().toUpperCase();
        if (!poliza.getPatenteVehiculo().equals(patente) && poliza.getEstado() == EstadoPoliza.ACTIVA) {
            validarSinOtraPolizaActiva(patente, poliza.getId());
        }
        if (!poliza.getAsegurado().getId().equals(request.aseguradoId())) {
            Asegurado nuevoAsegurado = buscarAsegurado(request.aseguradoId());
            poliza.getAsegurado().getPolizas().remove(poliza);
            nuevoAsegurado.getPolizas().add(poliza);
            poliza.setAsegurado(nuevoAsegurado);
        }
        // orphanRemoval elimina de la base las coberturas que se quitan de la lista
        poliza.getCoberturas().clear();
        copiarDatos(request, patente, poliza);
        return polizaMapper.toResponse(polizaRepository.save(poliza));
    }

    @Override
    @Transactional
    public PolizaResponse cambiarEstado(Long id, EstadoPoliza nuevoEstado) {
        Poliza poliza = buscar(id);
        LocalDate hoy = LocalDate.now();
        if (nuevoEstado == EstadoPoliza.VENCIDA) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "El estado VENCIDA se asigna automáticamente al cumplirse la fecha de término");
        }
        if (poliza.estadoA(hoy) == EstadoPoliza.VENCIDA) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "La póliza " + poliza.getNumeroPoliza() + " está vencida y no puede cambiar de estado");
        }
        if (nuevoEstado == EstadoPoliza.ACTIVA && poliza.getEstado() != EstadoPoliza.ACTIVA) {
            validarSinOtraPolizaActiva(poliza.getPatenteVehiculo(), poliza.getId());
        }
        poliza.setEstado(nuevoEstado);
        return polizaMapper.toResponse(polizaRepository.save(poliza));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Poliza poliza = buscar(id);
        // Las coberturas se eliminan en cascada (CascadeType.ALL + orphanRemoval)
        poliza.getAsegurado().getPolizas().remove(poliza);
        polizaRepository.delete(poliza);
    }

    private void copiarDatos(PolizaRequest request, String patente, Poliza poliza) {
        poliza.setPatenteVehiculo(patente);
        poliza.setMarcaVehiculo(request.marcaVehiculo());
        poliza.setModeloVehiculo(request.modeloVehiculo());
        poliza.setAnioVehiculo(request.anioVehiculo());
        poliza.setFechaInicio(request.fechaInicio());
        poliza.setFechaTermino(request.fechaTermino());
        poliza.setPrimaMensual(request.primaMensual());
        poliza.setDeducible(request.deducible());
        for (CoberturaRequest c : request.coberturas()) {
            poliza.agregarCobertura(new Cobertura(c.tipo(), c.montoMaximo()));
        }
    }

    private void validarFechas(PolizaRequest request) {
        if (!request.fechaTermino().isAfter(request.fechaInicio())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "La fecha de término debe ser posterior a la fecha de inicio");
        }
    }

    private void validarCoberturas(List<CoberturaRequest> coberturas) {
        Set<TipoCobertura> tipos = EnumSet.noneOf(TipoCobertura.class);
        for (CoberturaRequest c : coberturas) {
            if (!tipos.add(c.tipo())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "La cobertura " + c.tipo() + " está repetida");
            }
        }
    }

    /** Un vehículo no puede tener dos pólizas activas (no vencidas) al mismo tiempo. */
    private void validarSinOtraPolizaActiva(String patente, Long idExcluido) {
        LocalDate hoy = LocalDate.now();
        boolean existe = polizaRepository.findByPatenteVehiculo(patente).stream()
                .anyMatch(p -> !p.getId().equals(idExcluido) && p.estadoA(hoy) == EstadoPoliza.ACTIVA);
        if (existe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El vehículo " + patente + " ya tiene una póliza activa");
        }
    }

    private VigenciaResponse vigencia(String patente, LocalDate fecha, boolean vigente, String motivo, Poliza poliza) {
        Asegurado asegurado = poliza.getAsegurado();
        return new VigenciaResponse(
                patente,
                fecha,
                vigente,
                motivo,
                poliza.getNumeroPoliza(),
                poliza.estadoA(fecha),
                poliza.getFechaInicio(),
                poliza.getFechaTermino(),
                asegurado.getRut() + "-" + asegurado.getDv(),
                asegurado.getNombre(),
                poliza.getDeducible(),
                polizaMapper.toResponse(poliza.getCoberturas()));
    }

    private Poliza buscar(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Póliza no encontrada con id " + id));
    }

    private Asegurado buscarAsegurado(Long id) {
        return aseguradoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asegurado no encontrado con id " + id));
    }

    private String generarNumeroPoliza() {
        return "POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
