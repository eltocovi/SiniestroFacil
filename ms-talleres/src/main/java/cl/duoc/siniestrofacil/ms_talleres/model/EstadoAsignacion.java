package cl.duoc.siniestrofacil.ms_talleres.model;

import java.util.Set;


public enum EstadoAsignacion {
    ASIGNADA,
    VEHICULO_RECIBIDO,
    EN_REPARACION,
    FINALIZADA,
    CANCELADA;

    public boolean esActiva() {
        return this == ASIGNADA || this == VEHICULO_RECIBIDO || this == EN_REPARACION;
    }

    public boolean puedeCambiarA(EstadoAsignacion nuevo) {
        return switch (this) {
            case ASIGNADA -> Set.of(VEHICULO_RECIBIDO, CANCELADA).contains(nuevo);
            case VEHICULO_RECIBIDO -> Set.of(EN_REPARACION, CANCELADA).contains(nuevo);
            case EN_REPARACION -> nuevo == FINALIZADA;
            case FINALIZADA, CANCELADA -> false;
        };
    }
}
