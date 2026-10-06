package cl.duoc.siniestrofacil.ms_denuncias.service;

import cl.duoc.siniestrofacil.ms_denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.ms_denuncias.repository.DenunciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DenunciaService {

    @Autowired
    private DenunciaRepository denunciaRepository;

    public DenunciaService(DenunciaRepository denunciaRepository) {
        this.denunciaRepository = denunciaRepository;
    }

    public Denuncia registrarDenuncia(Denuncia denuncia) {
        denuncia.setFolio(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        denuncia.setFechaRegistro(LocalDateTime.now());
        denuncia.setEstado("PENDIENTE_ASIGNACION");

        return denunciaRepository.save(denuncia);
    }

    public List<Denuncia> obtenerTodas() {
        return denunciaRepository.findAll();
    }

    public Optional<Denuncia> obtenerPorId(Long id) {
        return denunciaRepository.findById(id);
    }

    public Denuncia actualizarEstado(Long id, String nuevoEstado) {
        return denunciaRepository.findById(id).map(denunciaExistente -> {
            denunciaExistente.setEstado(nuevoEstado);
            return denunciaRepository.save(denunciaExistente);
        }).orElseThrow(() -> new RuntimeException("Denuncia no encontrada con ID: " + id));
    }

    public void eliminarDenuncia(Long id) {
        denunciaRepository.deleteById(id);
    }
}