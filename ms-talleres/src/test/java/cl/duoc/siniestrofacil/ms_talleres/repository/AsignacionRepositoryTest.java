package cl.duoc.siniestrofacil.ms_talleres.repository;

import cl.duoc.siniestrofacil.ms_talleres.model.Asignacion;
import cl.duoc.siniestrofacil.ms_talleres.model.EstadoAsignacion;
import cl.duoc.siniestrofacil.ms_talleres.model.Taller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifica el mapeo JPA y la relación Taller (1) -> (N) Asignacion contra H2. */
@DataJpaTest
class AsignacionRepositoryTest {

    @Autowired
    private TallerRepository tallerRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Test
    void guardaAsignacionesDeUnTallerYLasConsultaPorLaRelacion() {
        Taller taller = tallerRepository.save(new Taller("Taller Los Andes", 77123450, "K",
                "Av. Vicuña Mackenna 1234", "Santiago", "+56222334455", "contacto@tallerlosandes.cl", 5, true));

        asignacionRepository.save(new Asignacion("A1B2C3D4", "ABCD12", "Choque",
                LocalDateTime.now(), EstadoAsignacion.ASIGNADA, taller));

        assertThat(asignacionRepository.findByTallerId(taller.getId())).hasSize(1);
        assertThat(asignacionRepository.existsByFolioDenunciaAndEstadoNot("A1B2C3D4", EstadoAsignacion.CANCELADA)).isTrue();
        assertThat(tallerRepository.findByDisponibleTrue()).extracting(t -> t.getRut()).contains(77123450);
    }
}
