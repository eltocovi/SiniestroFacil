package cl.duoc.siniestrofacil.ms_poliza.repository;

import cl.duoc.siniestrofacil.ms_poliza.model.Asegurado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AseguradoRepository extends JpaRepository<Asegurado, Long> {

    Optional<Asegurado> findByRut(Integer rut);

    boolean existsByRut(Integer rut);

    boolean existsByEmail(String email);
}
