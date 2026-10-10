package cl.duoc.ms_usuarios.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.ms_usuarios.model.Rol;
import cl.duoc.ms_usuarios.service.RolService;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> obtenerRoles() {
        return ResponseEntity.ok(rolService.obtenerRoles());
    }

    @GetMapping("/{idRol}")
    public ResponseEntity<Rol> obtenerPorId(@PathVariable Long idRol) {
        return rolService.obtenerPorId(idRol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        return ResponseEntity.status(201)
                .body(rolService.crearRol(rol));
    }

    @PutMapping("/{idRol}")
    public ResponseEntity<Rol> modificarRol(
            @PathVariable Long idRol,
            @RequestBody Rol rol) {

        return ResponseEntity.ok(
                rolService.modificarRol(rol, idRol)
        );
    }

    @DeleteMapping("/{idRol}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long idRol) {
        rolService.eliminarRol(idRol);

        return ResponseEntity.noContent().build();
    }
}