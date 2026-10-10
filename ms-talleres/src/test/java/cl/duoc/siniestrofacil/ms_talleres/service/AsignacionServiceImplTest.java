package cl.duoc.siniestrofacil.ms_talleres.service;

import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionResponse;
import cl.duoc.siniestrofacil.ms_talleres.model.Asignacion;
import cl.duoc.siniestrofacil.ms_talleres.model.EstadoAsignacion;
import cl.duoc.siniestrofacil.ms_talleres.model.Taller;
import cl.duoc.siniestrofacil.ms_talleres.repository.AsignacionRepository;
import cl.duoc.siniestrofacil.ms_talleres.repository.TallerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceImplTest {

    @Mock
    private AsignacionRepository asignacionRepository;

    @Mock
    private TallerRepository tallerRepository;

    private AsignacionServiceImpl asignacionService;

    @BeforeEach
    void setUp() {
        asignacionService = new AsignacionServiceImpl(asignacionRepository, tallerRepository, new TallerMapper());
    }

    @Test
    void asignar_automatico_priorizaComunaPreferida() {
        Taller santiago = taller(1L, "Santiago", 5);
        Taller maipu = taller(2L, "Maipú", 10);
        when(tallerRepository.findByDisponibleTrue()).thenReturn(List.of(maipu, santiago));
        when(asignacionRepository.save(any(Asignacion.class))).thenAnswer(inv -> inv.getArgument(0));

        AsignacionResponse response = asignacionService.asignar(request("Santiago", null));

        assertThat(response.tallerId()).isEqualTo(1L);
        assertThat(response.estado()).isEqualTo(EstadoAsignacion.ASIGNADA);
        assertThat(response.folioDenuncia()).isEqualTo("A1B2C3D4");
        assertThat(santiago.getAsignaciones()).hasSize(1);
    }

    @Test
    void asignar_automaticoSinComuna_eligeElTallerConMasCupos() {
        Taller chico = taller(1L, "Santiago", 2);
        Taller grande = taller(2L, "Maipú", 10);
        when(tallerRepository.findByDisponibleTrue()).thenReturn(List.of(chico, grande));
        when(asignacionRepository.save(any(Asignacion.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(asignacionService.asignar(request(null, null)).tallerId()).isEqualTo(2L);
    }

    @Test
    void asignar_sinTalleresConCupo_lanza422() {
        Taller lleno = taller(1L, "Santiago", 1);
        lleno.getAsignaciones().add(new Asignacion("X1", "ZZZZ11", "Choque", LocalDateTime.now(),
                EstadoAsignacion.EN_REPARACION, lleno));
        when(tallerRepository.findByDisponibleTrue()).thenReturn(List.of(lleno));

        assertThatThrownBy(() -> asignacionService.asignar(request(null, null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT)
                .hasMessageContaining("No hay talleres disponibles");
        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void asignar_aTallerNoDisponible_lanza422() {
        Taller cerrado = taller(1L, "Santiago", 5);
        cerrado.setDisponible(false);
        when(tallerRepository.findById(1L)).thenReturn(Optional.of(cerrado));

        assertThatThrownBy(() -> asignacionService.asignar(request(null, 1L)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void asignar_denunciaYaAsignada_lanza409() {
        when(asignacionRepository.existsByFolioDenunciaAndEstadoNot("A1B2C3D4", EstadoAsignacion.CANCELADA))
                .thenReturn(true);

        assertThatThrownBy(() -> asignacionService.asignar(request(null, null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.CONFLICT);
    }

    @Test
    void confirmarRecepcion_deAsignacionAsignada_registraFechaYObservaciones() {
        Asignacion asignacion = asignacion(EstadoAsignacion.ASIGNADA);
        when(asignacionRepository.findById(7L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(asignacion)).thenReturn(asignacion);

        AsignacionResponse response = asignacionService.confirmarRecepcion(7L, "Rayón en puerta");

        assertThat(response.estado()).isEqualTo(EstadoAsignacion.VEHICULO_RECIBIDO);
        assertThat(response.fechaRecepcion()).isNotNull();
        assertThat(response.observacionesRecepcion()).isEqualTo("Rayón en puerta");
    }

    @Test
    void confirmarRecepcion_dosVeces_lanza422() {
        when(asignacionRepository.findById(7L)).thenReturn(Optional.of(asignacion(EstadoAsignacion.VEHICULO_RECIBIDO)));

        assertThatThrownBy(() -> asignacionService.confirmarRecepcion(7L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void cambiarEstado_transicionInvalida_lanza422() {
        when(asignacionRepository.findById(7L)).thenReturn(Optional.of(asignacion(EstadoAsignacion.ASIGNADA)));

        assertThatThrownBy(() -> asignacionService.cambiarEstado(7L, EstadoAsignacion.FINALIZADA))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT)
                .hasMessageContaining("ASIGNADA -> FINALIZADA");
    }

    private Taller taller(Long id, String comuna, int capacidad) {
        Taller taller = new Taller("Taller " + comuna, 76000000 + id.intValue(), "0", "Calle 1", comuna,
                null, null, capacidad, true);
        taller.setId(id);
        return taller;
    }

    private Asignacion asignacion(EstadoAsignacion estado) {
        Taller taller = taller(1L, "Santiago", 5);
        Asignacion asignacion = new Asignacion("A1B2C3D4", "ABCD12", "Choque", LocalDateTime.now(), estado, taller);
        asignacion.setId(7L);
        taller.getAsignaciones().add(asignacion);
        return asignacion;
    }

    private AsignacionRequest request(String comuna, Long tallerId) {
        return new AsignacionRequest("a1b2c3d4", "abcd12", "Parachoques dañado", comuna, tallerId);
    }
}
