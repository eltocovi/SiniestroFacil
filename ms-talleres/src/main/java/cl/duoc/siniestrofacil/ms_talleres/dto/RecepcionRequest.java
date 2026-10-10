package cl.duoc.siniestrofacil.ms_talleres.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record RecepcionRequest(
        @Schema(example = "Vehículo ingresa con rayón adicional en puerta derecha")
        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
        String observaciones
) {
}
