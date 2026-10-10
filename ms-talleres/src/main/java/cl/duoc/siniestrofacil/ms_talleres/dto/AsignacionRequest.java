package cl.duoc.siniestrofacil.ms_talleres.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record AsignacionRequest(

        @Schema(example = "A1B2C3D4")
        @NotBlank(message = "El folio de la denuncia es obligatorio")
        @Size(max = 20, message = "El folio no puede superar los 20 caracteres")
        String folioDenuncia,

        @Schema(example = "ABCD12")
        @NotBlank(message = "La patente es obligatoria")
        @Pattern(regexp = "^[A-Za-z]{2}[A-Za-z0-9]{2}\\d{2}$", message = "La patente debe tener formato AB1234 o ABCD12")
        String patenteVehiculo,

        @Schema(example = "Parachoques trasero y foco izquierdo dañados")
        @NotBlank(message = "La descripción de los daños es obligatoria")
        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String descripcionDanos,

        @Schema(example = "Santiago", description = "Opcional: comuna preferida para la asignación automática")
        @Size(max = 60, message = "La comuna no puede superar los 60 caracteres")
        String comunaPreferida,

        @Schema(description = "Opcional: asignar a un taller específico en vez de elegir uno automáticamente")
        Long tallerId
) {
}
