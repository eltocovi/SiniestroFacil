package cl.duoc.siniestrofacil.ms_poliza.service;

import cl.duoc.siniestrofacil.ms_poliza.dto.CoberturaRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.VigenciaResponse;
import cl.duoc.siniestrofacil.ms_poliza.model.Asegurado;
import cl.duoc.siniestrofacil.ms_poliza.model.Cobertura;
import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;
import cl.duoc.siniestrofacil.ms_poliza.model.Poliza;
import cl.duoc.siniestrofacil.ms_poliza.model.TipoCobertura;
import cl.duoc.siniestrofacil.ms_poliza.repository.AseguradoRepository;
import cl.duoc.siniestrofacil.ms_poliza.repository.PolizaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolizaServiceImplTest {

    private static final LocalDate HOY = LocalDate.now();

    @Mock
    private PolizaRepository polizaRepository;

    @Mock
    private AseguradoRepository aseguradoRepository;

    private PolizaServiceImpl polizaService;

    private Asegurado asegurado;

    @BeforeEach
    void setUp() {
        polizaService = new PolizaServiceImpl(polizaRepository, aseguradoRepository, new PolizaMapper());
        asegurado = new Asegurado(12345678, "5", "Juan Pérez", "juan@mail.cl", null);
        asegurado.setId(1L);
    }

    @Test
    void emitir_conDatosValidos_quedaActivaConSusCoberturas() {
        when(aseguradoRepository.findById(1L)).thenReturn(Optional.of(asegurado));
        when(polizaRepository.save(any(Poliza.class))).thenAnswer(inv -> inv.getArgument(0));

        PolizaResponse response = polizaService.emitir(request("abcd12", HOY.minusMonths(1), HOY.plusYears(1),
                TipoCobertura.RESPONSABILIDAD_CIVIL, TipoCobertura.DANOS_PROPIOS));

        assertThat(response.numeroPoliza()).startsWith("POL-");
        assertThat(response.patenteVehiculo()).isEqualTo("ABCD12");
        assertThat(response.estado()).isEqualTo(EstadoPoliza.ACTIVA);
        assertThat(response.rutAsegurado()).isEqualTo("12345678-5");
        assertThat(response.coberturas()).hasSize(2);
        assertThat(asegurado.getPolizas()).hasSize(1);
    }

    @Test
    void emitir_conFechasInvertidas_lanza422() {
        assertThatThrownBy(() -> polizaService.emitir(request("ABCD12", HOY, HOY.minusDays(1), TipoCobertura.ROBO)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT);
        verify(polizaRepository, never()).save(any());
    }

    @Test
    void emitir_conCoberturaRepetida_lanza422() {
        assertThatThrownBy(() -> polizaService.emitir(request("ABCD12", HOY, HOY.plusYears(1),
                TipoCobertura.ROBO, TipoCobertura.ROBO)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT)
                .hasMessageContaining("repetida");
    }

    @Test
    void emitir_conPatenteConPolizaActiva_lanza409() {
        when(polizaRepository.findByPatenteVehiculo("ABCD12")).thenReturn(List.of(poliza(EstadoPoliza.ACTIVA, HOY.plusMonths(6))));

        assertThatThrownBy(() -> polizaService.emitir(request("ABCD12", HOY, HOY.plusYears(1), TipoCobertura.ROBO)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.CONFLICT);
    }

    @Test
    void emitir_siLaPolizaAnteriorYaVencio_permiteEmitir() {
        when(polizaRepository.findByPatenteVehiculo("ABCD12")).thenReturn(List.of(poliza(EstadoPoliza.ACTIVA, HOY.minusDays(1))));
        when(aseguradoRepository.findById(1L)).thenReturn(Optional.of(asegurado));
        when(polizaRepository.save(any(Poliza.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(polizaService.emitir(request("ABCD12", HOY, HOY.plusYears(1), TipoCobertura.ROBO)).estado())
                .isEqualTo(EstadoPoliza.ACTIVA);
    }

    @Test
    void consultarVigencia_conPolizaActivaEnFecha_retornaVigenteConCoberturas() {
        when(polizaRepository.findByPatenteVehiculo("ABCD12")).thenReturn(List.of(poliza(EstadoPoliza.ACTIVA, HOY.plusMonths(6))));

        VigenciaResponse vigencia = polizaService.consultarVigencia("abcd12", null);

        assertThat(vigencia.vigente()).isTrue();
        assertThat(vigencia.numeroPoliza()).isEqualTo("POL-TEST");
        assertThat(vigencia.rutAsegurado()).isEqualTo("12345678-5");
        assertThat(vigencia.coberturas()).hasSize(1);
    }

    @Test
    void consultarVigencia_conPolizaSuspendida_retornaNoVigenteConMotivo() {
        when(polizaRepository.findByPatenteVehiculo("ABCD12")).thenReturn(List.of(poliza(EstadoPoliza.SUSPENDIDA, HOY.plusMonths(6))));

        VigenciaResponse vigencia = polizaService.consultarVigencia("ABCD12", HOY);

        assertThat(vigencia.vigente()).isFalse();
        assertThat(vigencia.motivo()).contains("suspendida");
    }

    @Test
    void consultarVigencia_sinPolizas_retornaNoVigente() {
        when(polizaRepository.findByPatenteVehiculo("ZZZZ99")).thenReturn(List.of());

        VigenciaResponse vigencia = polizaService.consultarVigencia("ZZZZ99", HOY);

        assertThat(vigencia.vigente()).isFalse();
        assertThat(vigencia.motivo()).contains("no tiene pólizas");
    }

    @Test
    void cambiarEstado_aVencidaManualmente_lanza422() {
        when(polizaRepository.findById(5L)).thenReturn(Optional.of(poliza(EstadoPoliza.ACTIVA, HOY.plusMonths(6))));

        assertThatThrownBy(() -> polizaService.cambiarEstado(5L, EstadoPoliza.VENCIDA))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void cambiarEstado_suspenderPolizaActiva_actualiza() {
        Poliza poliza = poliza(EstadoPoliza.ACTIVA, HOY.plusMonths(6));
        when(polizaRepository.findById(5L)).thenReturn(Optional.of(poliza));
        when(polizaRepository.save(poliza)).thenReturn(poliza);

        assertThat(polizaService.cambiarEstado(5L, EstadoPoliza.SUSPENDIDA).estado()).isEqualTo(EstadoPoliza.SUSPENDIDA);
    }

    private Poliza poliza(EstadoPoliza estado, LocalDate fechaTermino) {
        Poliza poliza = new Poliza();
        poliza.setId(5L);
        poliza.setNumeroPoliza("POL-TEST");
        poliza.setPatenteVehiculo("ABCD12");
        poliza.setFechaInicio(HOY.minusMonths(6));
        poliza.setFechaTermino(fechaTermino);
        poliza.setEstado(estado);
        poliza.setDeducible(new BigDecimal("150000"));
        poliza.setAsegurado(asegurado);
        poliza.agregarCobertura(new Cobertura(TipoCobertura.DANOS_PROPIOS, new BigDecimal("15000000")));
        return poliza;
    }

    private PolizaRequest request(String patente, LocalDate inicio, LocalDate termino, TipoCobertura... tipos) {
        List<CoberturaRequest> coberturas = java.util.Arrays.stream(tipos)
                .map(t -> new CoberturaRequest(t, new BigDecimal("10000000")))
                .toList();
        return new PolizaRequest(patente, "Toyota", "Yaris", 2022, inicio, termino,
                new BigDecimal("45990"), new BigDecimal("150000"), 1L, coberturas);
    }
}
