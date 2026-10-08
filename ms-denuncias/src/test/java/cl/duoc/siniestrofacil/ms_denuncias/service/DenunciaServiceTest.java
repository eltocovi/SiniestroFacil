package cl.duoc.siniestrofacil.ms_denuncias.service;

import cl.duoc.siniestrofacil.ms_denuncias.model.Denuncia;
import cl.duoc.siniestrofacil.ms_denuncias.model.Fotografia;
import cl.duoc.siniestrofacil.ms_denuncias.repository.DenunciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DenunciaServiceTest {

    @Mock
    private DenunciaRepository denunciaRepository;

    @InjectMocks
    private DenunciaService denunciaService;

    private Denuncia denunciaPrueba;

    @BeforeEach
    void setUp() {
        denunciaPrueba = new Denuncia();
        denunciaPrueba.setId(1L);
        denunciaPrueba.setDescripcion("Choque por alcance en la avenida");
    }

    @Test
    void registrarDenuncia_DeberiaAsignarValoresInicialesYGuardar() {
        List<Fotografia> fotos = new ArrayList<>();
        fotos.add(new Fotografia());
        denunciaPrueba.setFotografias(fotos);

        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denunciaPrueba);

        Denuncia resultado = denunciaService.registrarDenuncia(denunciaPrueba);

        assertNotNull(resultado.getFolio(), "El folio no debe ser nulo");
        assertEquals(8, resultado.getFolio().length(), "El folio debe tener 8 caracteres");
        assertNotNull(resultado.getFechaRegistro(), "La fecha de registro no debe ser nula");
        assertEquals("PENDIENTE_ASIGNACION", resultado.getEstado(), "El estado inicial debe ser PENDIENTE_ASIGNACION");

        assertEquals(resultado, fotos.get(0).getDenuncia(), "La fotografía debe tener asignada la denuncia");

        verify(denunciaRepository, times(1)).save(denunciaPrueba);
    }

    @Test
    void obtenerTodas_DeberiaRetornarListaDeDenuncias() {
        List<Denuncia> listaEsperada = List.of(denunciaPrueba, new Denuncia());
        when(denunciaRepository.findAll()).thenReturn(listaEsperada);

        List<Denuncia> resultado = denunciaService.obtenerTodas();

        assertEquals(2, resultado.size());
        verify(denunciaRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_DeberiaRetornarDenunciaSiExiste() {
        when(denunciaRepository.findById(1L)).thenReturn(Optional.of(denunciaPrueba));

        Optional<Denuncia> resultado = denunciaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(denunciaRepository, times(1)).findById(1L);
    }

    @Test
    void actualizarEstado_DeberiaActualizarYGuardarSiExiste() {

        String nuevoEstado = "EN_REVISION";
        when(denunciaRepository.findById(1L)).thenReturn(Optional.of(denunciaPrueba));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denunciaPrueba);

        Denuncia resultado = denunciaService.actualizarEstado(1L, nuevoEstado);

        assertEquals(nuevoEstado, resultado.getEstado());
        verify(denunciaRepository, times(1)).findById(1L);
        verify(denunciaRepository, times(1)).save(denunciaPrueba);
    }

    @Test
    void actualizarEstado_DeberiaLanzarExcepcionSiNoExiste() {
        when(denunciaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            denunciaService.actualizarEstado(99L, "EN_REVISION");
        });

        assertEquals("Denuncia no encontrada con ID: 99", excepcion.getMessage());
        verify(denunciaRepository, times(1)).findById(99L);
        verify(denunciaRepository, never()).save(any(Denuncia.class));
    }

    @Test
    void eliminarDenuncia_DeberiaLlamarDeleteById() {
        denunciaService.eliminarDenuncia(1L);

        verify(denunciaRepository, times(1)).deleteById(1L);
    }
}