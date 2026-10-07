package cl.duoc.siniestrofacil.ms_talleres.repository;

import cl.duoc.siniestrofacil.ms_talleres.model.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TallerRepository extends JpaRepository<Taller, Long> {

    boolean existsByRut(Integer rut);

    List<Taller> findByComunaIgnoreCase(String comuna);

    List<Taller> findByDisponibleTrue();
}
