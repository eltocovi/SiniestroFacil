package cl.duoc.siniestrofacil.ms_poliza.repository;

import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;
import cl.duoc.siniestrofacil.ms_poliza.model.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    Optional<Poliza> findByNumeroPoliza(String numeroPoliza);

    List<Poliza> findByPatenteVehiculo(String patenteVehiculo);

    List<Poliza> findByEstado(EstadoPoliza estado);

    boolean existsByPatenteVehiculoAndEstado(String patenteVehiculo, EstadoPoliza estado);
}
