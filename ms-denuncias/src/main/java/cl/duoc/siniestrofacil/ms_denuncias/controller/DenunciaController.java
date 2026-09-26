package cl.duoc.siniestrofacil.ms_denuncias.controller;

import cl.duoc.siniestrofacil.ms_denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.ms_denuncias.service.DenunciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/denuncias")
public class DenunciaController {

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @PostMapping
    public ResponseEntity<Denuncia> registrarDenuncia(@Valid @RequestBody Denuncia denuncia) {
        return new ResponseEntity<>(denunciaService.registrarDenuncia(denuncia), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Denuncia>> obtenerTodas() {
        return ResponseEntity.ok(denunciaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Denuncia> obtenerPorId(@PathVariable Long id) {
        return denunciaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Denuncia> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        try {
            return ResponseEntity.ok(denunciaService.actualizarEstado(id, estado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDenuncia(@PathVariable Long id) {
        denunciaService.eliminarDenuncia(id);
        return ResponseEntity.noContent().build();
    }
}