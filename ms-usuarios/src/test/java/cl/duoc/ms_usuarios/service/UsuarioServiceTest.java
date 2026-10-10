package cl.duoc.ms_usuarios.service;

import cl.duoc.ms_usuarios.model.Usuario;
import cl.duoc.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();

        usuario.setIdUsuario(1L);
        usuario.setRutUsuario("11111111-1");
        usuario.setEdad(27);
        usuario.setNombreUsuario("Juan");
        usuario.setApellidoUsuario("Perez");
        usuario.setContrasena("123456");
        usuario.setCorreo("juan@test.cl");
        usuario.setTelefono("912345678");
        usuario.setDireccion("Santiago");
        usuario.setEstado(true);
    }

    @Test
    void obtenerUsuarios_deberiaRetornarListaDeUsuarios() {

        List<Usuario> usuarios = Arrays.asList(usuario);

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.obtenerUsuarios();

        assertEquals(1, resultado.size());
        assertEquals(usuario, resultado.get(0));

        verify(usuarioRepository).findAll();
    }

    @Test
    void obtenerPorId_deberiaRetornarUsuario() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void obtenerPorRut_deberiaRetornarUsuario() {

        when(usuarioRepository.findByRutUsuario("11111111-1"))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado =
                usuarioService.obtenerPorRut("11111111-1");

        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());

        verify(usuarioRepository).findByRutUsuario("11111111-1");
    }

    @Test
    void obtenerPorCorreo_deberiaRetornarUsuario() {

        when(usuarioRepository.findByCorreo("juan@test.cl"))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado =
                usuarioService.obtenerPorCorreo("juan@test.cl");

        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());

        verify(usuarioRepository).findByCorreo("juan@test.cl");
    }

    @Test
    void registrarUsuario_deberiaCifrarContrasenaYGuardar() {

        when(passwordEncoder.encode("123456"))
                .thenReturn("HASH_BCRYPT");

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        Usuario resultado = usuarioService.registrarUsuario(usuario);

        assertEquals("HASH_BCRYPT", resultado.getContrasena());

        verify(passwordEncoder).encode("123456");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void actualizarUsuario_deberiaActualizarDatos() {

        Usuario datosActualizados = new Usuario();

        datosActualizados.setRutUsuario("22222222-2");
        datosActualizados.setEdad(30);
        datosActualizados.setNombreUsuario("Pedro");
        datosActualizados.setApellidoUsuario("Gonzalez");
        datosActualizados.setCorreo("pedro@test.cl");
        datosActualizados.setTelefono("987654321");
        datosActualizados.setDireccion("Providencia");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        Usuario resultado =
                usuarioService.actualizarUsuario(datosActualizados, 1L);

        assertEquals("22222222-2", resultado.getRutUsuario());
        assertEquals(30, resultado.getEdad());
        assertEquals("Pedro", resultado.getNombreUsuario());
        assertEquals("Gonzalez", resultado.getApellidoUsuario());
        assertEquals("pedro@test.cl", resultado.getCorreo());
        assertEquals("987654321", resultado.getTelefono());
        assertEquals("Providencia", resultado.getDireccion());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void actualizarUsuario_siNoExiste_deberiaLanzarExcepcion() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.actualizarUsuario(usuario, 99L)
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void eliminarUsuario_deberiaEliminarUsuario() {

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void cambiarEstadoUsuario_deberiaCambiarEstado() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        Usuario resultado =
                usuarioService.cambiarEstadoUsuario(1L, false);

        assertFalse(resultado.getEstado());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarEstadoUsuario_siNoExiste_deberiaLanzarExcepcion() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cambiarEstadoUsuario(99L, false)
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).save(any());
    }
}