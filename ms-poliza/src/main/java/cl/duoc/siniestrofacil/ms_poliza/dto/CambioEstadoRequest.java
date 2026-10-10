package cl.duoc.siniestrofacil.ms_poliza.dto;

import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @Schema(example = "SUSPENDIDA")
        @NotNull(message = "El estado es obligatorio")
        EstadoPoliza estado
) {
}
