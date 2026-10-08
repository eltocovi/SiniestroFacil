package cl.duoc.siniestrofacil.ms_talleres.service;

import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionResponse;
import cl.duoc.siniestrofacil.ms_talleres.dto.DisponibilidadRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.TallerRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.TallerResponse;

import java.util.List;

public interface TallerService {

    TallerResponse registrar(TallerRequest request);

    List<TallerResponse> listar(String comuna);

    List<TallerResponse> listarDisponibles(String comuna);

    TallerResponse obtenerPorId(Long id);

    List<AsignacionResponse> listarAsignaciones(Long tallerId);

    TallerResponse actualizar(Long id, TallerRequest request);

    TallerResponse actualizarDisponibilidad(Long id, DisponibilidadRequest request);

    void eliminar(Long id);
}
