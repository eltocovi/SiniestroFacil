package cl.duoc.siniestrofacil.ms_talleres.service;

import java.util.List;

import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionResponse;
import cl.duoc.siniestrofacil.ms_talleres.model.EstadoAsignacion;

public interface AsignacionService {
    AsignacionResponse asignar (AsignacionRequest request);

    List<AsignacionResponse> listar(EstadoAsignacion estado, String folioDenuncia);

    AsignacionResponse obtenerPorId(Long id);
    AsignacionResponse confirmarRecepcion(Long id, String observaciones);
    AsignacionResponse cambiarEstado(Long id, EstadoAsignacion nuevoEstado);    

    Void eliminar(Long id);
}
