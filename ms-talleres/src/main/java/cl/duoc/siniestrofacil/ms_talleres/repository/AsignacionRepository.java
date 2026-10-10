package cl.duoc.siniestrofacil.ms_talleres.repository;

import cl.duoc.siniestrofacil.ms_talleres.model.Asignacion;
import cl.duoc.siniestrofacil.ms_talleres.model.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findByEstado(EstadoAsignacion estado);

    List<Asignacion> findByFolioDenuncia(String folioDenuncia);

    List<Asignacion> findByTallerId(Long tallerId);

    boolean existsByFolioDenunciaAndEstadoNot(String folioDenuncia, EstadoAsignacion estado);
}
