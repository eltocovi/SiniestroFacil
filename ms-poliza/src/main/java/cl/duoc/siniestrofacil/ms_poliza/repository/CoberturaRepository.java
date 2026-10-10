package cl.duoc.siniestrofacil.ms_poliza.repository;

import cl.duoc.siniestrofacil.ms_poliza.model.Cobertura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoberturaRepository extends JpaRepository<Cobertura, Long> {

    List<Cobertura> findByPolizaId(Long polizaId);
}
