package cl.duoc.ms_usuarios.service;

import cl.duoc.ms_usuarios.model.Rol;
import cl.duoc.ms_usuarios.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private Rol rol;

    @BeforeEach
    void setUp() {

        rol = new Rol();

        rol.setIdRol(1L);
        rol.setNombre("ASEGURADO");
    }

    @Test
    void obtenerRoles_deberiaRetornarListaDeRoles() {

        List<Rol> roles = Arrays.asList(rol);

        when(rolRepository.findAll()).thenReturn(roles);

        List<Rol> resultado = rolService.obtenerRoles();

        assertEquals(1, resultado.size());
        assertEquals(rol, resultado.get(0));

        verify(rolRepository).findAll();
    }

    @Test
    void obtenerPorId_deberiaRetornarRol() {

        when(rolRepository.findById(1L))
                .thenReturn(Optional.of(rol));

        Optional<Rol> resultado = rolService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(rol, resultado.get());

        verify(rolRepository).findById(1L);
    }

    @Test
    void crearRol_deberiaGuardarRol() {

        when(rolRepository.save(rol))
                .thenReturn(rol);

        Rol resultado = rolService.crearRol(rol);

        assertEquals(rol, resultado);

        verify(rolRepository).save(rol);
    }

    @Test
    void modificarRol_deberiaModificarNombre() {

        Rol nuevosDatos = new Rol();
        nuevosDatos.setNombre("ADMIN");

        when(rolRepository.findById(1L))
                .thenReturn(Optional.of(rol));

        when(rolRepository.save(rol))
                .thenReturn(rol);

        Rol resultado =
                rolService.modificarRol(nuevosDatos, 1L);

        assertEquals("ADMIN", resultado.getNombre());

        verify(rolRepository).findById(1L);
        verify(rolRepository).save(rol);
    }

    @Test
    void modificarRol_siNoExiste_deberiaLanzarExcepcion() {

        when(rolRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.modificarRol(rol, 99L)
        );

        assertEquals("Rol no encontrado", exception.getMessage());

        verify(rolRepository).findById(99L);
        verify(rolRepository, never()).save(any());
    }

    @Test
    void eliminarRol_deberiaEliminarRol() {

        rolService.eliminarRol(1L);

        verify(rolRepository).deleteById(1L);
    }
}