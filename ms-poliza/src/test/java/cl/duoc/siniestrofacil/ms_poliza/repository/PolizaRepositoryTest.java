package cl.duoc.siniestrofacil.ms_poliza.repository;

import cl.duoc.siniestrofacil.ms_poliza.model.Asegurado;
import cl.duoc.siniestrofacil.ms_poliza.model.Cobertura;
import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;
import cl.duoc.siniestrofacil.ms_poliza.model.Poliza;
import cl.duoc.siniestrofacil.ms_poliza.model.TipoCobertura;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica el mapeo JPA contra H2:
 * Asegurado (1) -> (N) Poliza y Poliza (1) -> (N) Cobertura con cascada.
 */
@DataJpaTest
class PolizaRepositoryTest {

    @Autowired
    private AseguradoRepository aseguradoRepository;

    @Autowired
    private PolizaRepository polizaRepository;

    @Autowired
    private CoberturaRepository coberturaRepository;

    @Test
    void guardaPolizaConCoberturasEnCascada_yLasEliminaJuntoALaPoliza() {
        Asegurado asegurado = aseguradoRepository.save(new Asegurado(12345678, "5", "Juan Pérez", "juan@mail.cl", null));

        Poliza poliza = new Poliza();
        poliza.setNumeroPoliza("POL-TEST0001");
        poliza.setPatenteVehiculo("ABCD12");
        poliza.setMarcaVehiculo("Toyota");
        poliza.setModeloVehiculo("Yaris");
        poliza.setAnioVehiculo(2022);
        poliza.setFechaInicio(LocalDate.now());
        poliza.setFechaTermino(LocalDate.now().plusYears(1));
        poliza.setPrimaMensual(new BigDecimal("45990"));
        poliza.setDeducible(new BigDecimal("150000"));
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setAsegurado(asegurado);
        poliza.agregarCobertura(new Cobertura(TipoCobertura.RESPONSABILIDAD_CIVIL, new BigDecimal("10000000")));
        poliza.agregarCobertura(new Cobertura(TipoCobertura.ROBO, new BigDecimal("12000000")));

        Poliza guardada = polizaRepository.saveAndFlush(poliza);

        assertThat(coberturaRepository.findByPolizaId(guardada.getId())).hasSize(2);
        assertThat(polizaRepository.findByPatenteVehiculo("ABCD12")).hasSize(1);
        assertThat(polizaRepository.findByNumeroPoliza("POL-TEST0001")).isPresent();

        polizaRepository.delete(guardada);
        polizaRepository.flush();

        assertThat(coberturaRepository.count()).isZero();
        assertThat(aseguradoRepository.findByRut(12345678)).isPresent();
    }
}
