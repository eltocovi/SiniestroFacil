package cl.duoc.ms_usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.duoc.ms_usuarios.model.Usuario;
import cl.duoc.ms_usuarios.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final String REGEX_RUT = "\\d{7,8}-[0-9Kk]";
    private static final String REGEX_CORREO = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    public Optional<Usuario> obtenerPorRut(String rutUsuario) {
        return usuarioRepository.findByRutUsuario(rutUsuario);
    }

    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario registrarUsuario(Usuario usuario) {
        validarUsuario(usuario);

        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Usuario usuario, Long idUsuario) {
        validarUsuario(usuario);

        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioExistente.setRutUsuario(usuario.getRutUsuario());
        usuarioExistente.setEdad(usuario.getEdad());
        usuarioExistente.setNombreUsuario(usuario.getNombreUsuario());
        usuarioExistente.setApellidoUsuario(usuario.getApellidoUsuario());
        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setTelefono(usuario.getTelefono());
        usuarioExistente.setDireccion(usuario.getDireccion());

        return usuarioRepository.save(usuarioExistente);
    }

    public void eliminarUsuario(Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    public Usuario cambiarEstadoUsuario(Long idUsuario, Boolean estado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstado(estado);

        return usuarioRepository.save(usuario);
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario.getRutUsuario() == null
                || !usuario.getRutUsuario().matches(REGEX_RUT)) {
            throw new IllegalArgumentException("El formato del RUT no es válido");
        }

        if (usuario.getCorreo() == null
                || !usuario.getCorreo().matches(REGEX_CORREO)) {
            throw new IllegalArgumentException("El formato del correo no es válido");
        }
    }
}