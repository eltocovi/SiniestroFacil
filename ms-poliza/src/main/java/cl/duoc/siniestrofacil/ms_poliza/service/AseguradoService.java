package cl.duoc.siniestrofacil.ms_poliza.service;

import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;

import java.util.List;

public interface AseguradoService {

    AseguradoResponse registrar(AseguradoRequest request);

    List<AseguradoResponse> listar();

    AseguradoResponse obtenerPorId(Long id);

    AseguradoResponse obtenerPorRut(Integer rut);

    List<PolizaResponse> listarPolizas(Long aseguradoId);

    AseguradoResponse actualizar(Long id, AseguradoRequest request);

    void eliminar(Long id);
}
