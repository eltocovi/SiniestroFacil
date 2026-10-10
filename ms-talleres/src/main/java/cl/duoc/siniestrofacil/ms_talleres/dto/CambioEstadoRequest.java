package cl.duoc.siniestrofacil.ms_talleres.dto;

import cl.duoc.siniestrofacil.ms_talleres.model.EstadoAsignacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @Schema(example = "EN_REPARACION")
        @NotNull(message = "El estado es obligatorio")
        EstadoAsignacion estado
) {
}
