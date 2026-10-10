package cl.duoc.ms_usuarios.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import cl.duoc.ms_usuarios.dto.LoginRequestDTO;
import cl.duoc.ms_usuarios.service.JwtService;

import cl.duoc.ms_usuarios.model.Usuario;
import cl.duoc.ms_usuarios.service.UsuarioService;

@RestController 
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired 
    private UsuarioService usuarioService;
    @Autowired 
    private AuthenticationManager authenticationManager;
    @Autowired 
    private JwtService jwtService;


    @GetMapping("/api/usuarios")
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    @GetMapping("/api/usuarios/{idUsuario}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long idUsuario) {
        return usuarioService.obtenerPorId(idUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/rut/{rutUsuario}")
    public ResponseEntity<Usuario> obtenerPorRut(@PathVariable String rutUsuario) {

        return usuarioService.obtenerPorRut(rutUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/correo/{correo}")
    public ResponseEntity<Usuario> obtenerPorCorreo (@PathVariable String correo) {

        return usuarioService.obtenerPorCorreo(correo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.correo(),
                        request.contrasena()
                )
        );

        Usuario usuarioEncontrado = usuarioService.obtenerPorCorreo(request.correo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuarioEncontrado);

        return ResponseEntity.ok(token);
    }

    @PostMapping
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarUsuario(usuario));
    }

    @PatchMapping ("/{idUsuario}")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario, @PathVariable Long idUsuario) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario, idUsuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();   
   }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{idUsuario}/estado")
    public ResponseEntity<Usuario> cambiarEstadoUsuario(
            @PathVariable Long idUsuario,
            @RequestParam Boolean estado) {

        return ResponseEntity.ok(
                usuarioService.cambiarEstadoUsuario(idUsuario, estado)
        );
    }   
}