package cl.duoc.siniestrofacil.ms_talleres.dto;

import cl.duoc.siniestrofacil.ms_talleres.model.EstadoAsignacion;

import java.time.LocalDateTime;

public record AsignacionResponse(
        Long id,
        String folioDenuncia,
        String patenteVehiculo,
        String descripcionDanos,
        LocalDateTime fechaAsignacion,
        LocalDateTime fechaRecepcion,
        String observacionesRecepcion,
        EstadoAsignacion estado,
        Long tallerId,
        String nombreTaller,
        String direccionTaller
) {
}
