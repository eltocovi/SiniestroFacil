package cl.duoc.siniestrofacil.ms_poliza.service;

import cl.duoc.siniestrofacil.ms_poliza.dto.CoberturaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.VigenciaResponse;
import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;

import java.time.LocalDate;
import java.util.List;

public interface PolizaService {

    PolizaResponse emitir(PolizaRequest request);

    List<PolizaResponse> listar(EstadoPoliza estado, String patente);

    PolizaResponse obtenerPorId(Long id);

    PolizaResponse obtenerPorNumero(String numeroPoliza);

    List<CoberturaResponse> listarCoberturas(Long polizaId);

    VigenciaResponse consultarVigencia(String patente, LocalDate fecha);

    PolizaResponse actualizar(Long id, PolizaRequest request);

    PolizaResponse cambiarEstado(Long id, EstadoPoliza nuevoEstado);

    void eliminar(Long id);
}
