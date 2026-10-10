package cl.duoc.siniestrofacil.ms_poliza.service;

import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoResponse;
import cl.duoc.siniestrofacil.ms_poliza.model.Asegurado;
import cl.duoc.siniestrofacil.ms_poliza.model.Poliza;
import cl.duoc.siniestrofacil.ms_poliza.repository.AseguradoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AseguradoServiceImplTest {

    @Mock
    private AseguradoRepository aseguradoRepository;

    private AseguradoServiceImpl aseguradoService;

    @BeforeEach
    void setUp() {
        aseguradoService = new AseguradoServiceImpl(aseguradoRepository, new PolizaMapper());
    }

    @Test
    void registrar_conRutValido_guardaDvEnMayuscula() {
        when(aseguradoRepository.save(any(Asegurado.class))).thenAnswer(inv -> inv.getArgument(0));

        AseguradoResponse response = aseguradoService.registrar(request(77123450, "k"));

        assertThat(response.digitoVerificador()).isEqualTo("K");
        assertThat(response.rutCompleto()).isEqualTo("77123450-K");
    }

    @Test
    void registrar_conDigitoVerificadorIncorrecto_lanza422() {
        assertThatThrownBy(() -> aseguradoService.registrar(request(12345678, "9")))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT);
        verify(aseguradoRepository, never()).save(any());
    }

    @Test
    void registrar_conEmailExistente_lanza409() {
        when(aseguradoRepository.existsByEmail("juan@mail.cl")).thenReturn(true);

        assertThatThrownBy(() -> aseguradoService.registrar(request(12345678, "5")))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.CONFLICT);
    }

    @Test
    void eliminar_conPolizas_lanza422() {
        Asegurado asegurado = new Asegurado(12345678, "5", "Juan Pérez", "juan@mail.cl", null);
        asegurado.getPolizas().add(new Poliza());
        when(aseguradoRepository.findById(1L)).thenReturn(Optional.of(asegurado));

        assertThatThrownBy(() -> aseguradoService.eliminar(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    private AseguradoRequest request(int rut, String dv) {
        return new AseguradoRequest(rut, dv, "Juan Pérez", "juan@mail.cl", "+56912345678");
    }
}
