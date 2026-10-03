package cl.duoc.ms_usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.duoc.ms_usuarios.model.Usuario;
import cl.duoc.ms_usuarios.repository.UsuarioRepository;

@Service 
public class UsuarioService {
    private  final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    public Usuario registrarUsuario(Usuario usuario) {
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
