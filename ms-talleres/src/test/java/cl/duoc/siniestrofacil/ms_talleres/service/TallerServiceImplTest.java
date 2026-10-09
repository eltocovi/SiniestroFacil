package cl.duoc.siniestrofacil.ms_talleres.service;

import cl.duoc.siniestrofacil.ms_talleres.dto.TallerRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.TallerResponse;
import cl.duoc.siniestrofacil.ms_talleres.model.Taller;
import cl.duoc.siniestrofacil.ms_talleres.repository.TallerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TallerServiceImplTest {

    @Mock
    private TallerRepository tallerRepository;

    private TallerServiceImpl tallerService;

    @BeforeEach
    void setUp() {
        tallerService = new TallerServiceImpl(tallerRepository, new TallerMapper());
    }

    @Test
    void registrar_conDigitoVerificadorKMinuscula_loGuardaEnMayuscula() {
        when(tallerRepository.save(any(Taller.class))).thenAnswer(inv -> inv.getArgument(0));

        TallerResponse response = tallerService.registrar(request(77123450, "k"));

        ArgumentCaptor<Taller> captor = ArgumentCaptor.forClass(Taller.class);
        verify(tallerRepository).save(captor.capture());
        assertThat(captor.getValue().getRut()).isEqualTo(77123450);
        assertThat(captor.getValue().getDv()).isEqualTo("K");
        assertThat(response.digitoVerificador()).isEqualTo("K");
        assertThat(response.rutCompleto()).isEqualTo("77123450-K");
        assertThat(response.disponible()).isTrue();
    }

    @Test
    void registrar_conDigitoVerificadorIncorrecto_lanza422() {
        assertThatThrownBy(() -> tallerService.registrar(request(76543210, "4")))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT)
                .hasMessageContaining("no corresponde");
        verify(tallerRepository, never()).save(any());
    }

    @Test
    void registrar_conRutExistente_lanza409() {
        when(tallerRepository.existsByRut(76543210)).thenReturn(true);

        assertThatThrownBy(() -> tallerService.registrar(request(76543210, "3")))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.CONFLICT);
    }

    private TallerRequest request(int rut, String dv) {
        return new TallerRequest("Taller Los Andes", rut, dv, "Av. Vicuña Mackenna 1234", "Santiago",
                null, null, 5, null);
    }
}
