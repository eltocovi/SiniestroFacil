package cl.duoc.ms_usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.ms_usuarios.model.Usuario;
import cl.duoc.ms_usuarios.service.UsuarioService;

@RestController 
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    @GetMapping("/buscar/{idUsuario}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long idUsuario) {
        return usuarioService.obtenerPorId(idUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarUsuario(usuario));
    }

    @PatchMapping ("/modificar/{idusuario}")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario, @PathVariable Long idUsuario) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario, idUsuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/eliminar/{idUsuario}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();   
   }
}