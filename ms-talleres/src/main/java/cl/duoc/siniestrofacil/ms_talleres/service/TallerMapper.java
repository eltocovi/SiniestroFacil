package cl.duoc.siniestrofacil.ms_talleres.service;

import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionResponse;
import cl.duoc.siniestrofacil.ms_talleres.dto.TallerResponse;
import cl.duoc.siniestrofacil.ms_talleres.model.Asignacion;
import cl.duoc.siniestrofacil.ms_talleres.model.Taller;
import org.springframework.stereotype.Component;


@Component
public class TallerMapper {

    public TallerResponse toResponse(Taller taller) {
        return new TallerResponse(
                taller.getId(),
                taller.getNombre(),
                taller.getRut(),
                taller.getDv(),
                taller.getRut() + "-" + taller.getDv(),
                taller.getDireccion(),
                taller.getComuna(),
                taller.getTelefono(),
                taller.getEmail(),
                taller.getCapacidadMaxima(),
                taller.getDisponible(),
                taller.contarAsignacionesActivas(),
                taller.cuposLibres());
    }

    public AsignacionResponse toResponse(Asignacion asignacion) {
        Taller taller = asignacion.getTaller();
        return new AsignacionResponse(
                asignacion.getId(),
                asignacion.getFolioDenuncia(),
                asignacion.getPatenteVehiculo(),
                asignacion.getDescripcionDanos(),
                asignacion.getFechaAsignacion(),
                asignacion.getFechaRecepcion(),
                asignacion.getObservacionesRecepcion(),
                asignacion.getEstado(),
                taller.getId(),
                taller.getNombre(),
                taller.getDireccion());
    }
}
