package cl.duoc.siniestrofacil.ms_denuncias.controller;

import cl.duoc.siniestrofacil.ms_denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.ms_denuncias.service.DenunciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/denuncias")
public class DenunciaController {

    @Autowired
    private DenunciaService denunciaService;

    @PostMapping
    public ResponseEntity<Denuncia> registrarDenuncia(
            @Valid @RequestBody Denuncia denuncia,
            JwtAuthenticationToken token) {

        String correoUsuario = token.getName();

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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Denuncia> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        try {
            return ResponseEntity.ok(denunciaService.actualizarEstado(id, estado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDenuncia(@PathVariable Long id) {
        denunciaService.eliminarDenuncia(id);
        return ResponseEntity.noContent().build();
    }
}