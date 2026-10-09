package cl.duoc.siniestrofacil.ms_talleres.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RutUtilTest {

    @Test
    void calculaDigitoVerificadorNumerico() {
        assertThat(RutUtil.calcularDigitoVerificador(76543210)).isEqualTo("3");
        assertThat(RutUtil.calcularDigitoVerificador(12345678)).isEqualTo("5");
    }

    @Test
    void calculaDigitoVerificadorCeroYK() {
        assertThat(RutUtil.calcularDigitoVerificador(10000004)).isEqualTo("0");
        assertThat(RutUtil.calcularDigitoVerificador(77123450)).isEqualTo("K");
    }

    @Test
    void validaRutSinImportarMayusculas() {
        assertThat(RutUtil.esValido(77123450, "k")).isTrue();
        assertThat(RutUtil.esValido(76543210, "4")).isFalse();
    }

    @Test
    void formateaRutCompleto() {
        assertThat(RutUtil.formatear(77123450, "K")).isEqualTo("77123450-K");
    }
}
