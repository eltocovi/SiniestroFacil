package cl.duoc.siniestrofacil.ms_talleres.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TallerRequest(

        @Schema(example = "Taller Automotriz Los Andes")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Schema(example = "76543210", description = "RUT sin puntos ni dígito verificador")
        @NotNull(message = "El RUT es obligatorio")
        @Min(value = 1000000, message = "El RUT debe tener 7 u 8 dígitos")
        @Max(value = 99999999, message = "El RUT debe tener 7 u 8 dígitos")
        Integer rut,

        @Schema(example = "3", description = "Dígito verificador: 0-9 o K")
        @NotBlank(message = "El dígito verificador es obligatorio")
        @Pattern(regexp = "^[0-9kK]$", message = "El dígito verificador debe ser un número del 0 al 9 o K")
        String digitoVerificador,

        @Schema(example = "Av. Vicuña Mackenna 1234")
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
        String direccion,

        @Schema(example = "Santiago")
        @NotBlank(message = "La comuna es obligatoria")
        @Size(max = 60, message = "La comuna no puede superar los 60 caracteres")
        String comuna,

        @Schema(example = "+56222334455")
        @Pattern(regexp = "^\\+?\\d{8,15}$", message = "El teléfono debe contener entre 8 y 15 dígitos")
        String telefono,

        @Schema(example = "contacto@tallerlosandes.cl")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @Schema(example = "5")
        @NotNull(message = "La capacidad máxima es obligatoria")
        @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
        @Max(value = 200, message = "La capacidad máxima no puede superar 200")
        Integer capacidadMaxima,

        @Schema(example = "true", description = "Si se omite, el taller se registra como disponible")
        Boolean disponible
) {
}
