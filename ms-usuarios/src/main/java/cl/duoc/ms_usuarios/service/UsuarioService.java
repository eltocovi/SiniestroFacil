package cl.duoc.ms_usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import cl.duoc.ms_usuarios.dto.LoginRequestDTO;
import cl.duoc.ms_usuarios.model.Usuario;
import cl.duoc.ms_usuarios.repository.UsuarioRepository;

@Service 
public class UsuarioService {

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

        usuario.setContrasena(
        passwordEncoder.encode(usuario.getContrasena())
    );
    return usuarioRepository.save(usuario);
    }
    
    public Usuario actualizarUsuario(Usuario usuario, Long idUsuario) {

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

    public void eliminarUsuario(Long idUsuario){
        this.usuarioRepository.deleteById(idUsuario);
    }
}
