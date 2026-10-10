package cl.duoc.siniestrofacil.ms_poliza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos de entrada para registrar o actualizar un asegurado. */
public record AseguradoRequest(

        @Schema(example = "12345678", description = "RUT sin puntos ni dígito verificador")
        @NotNull(message = "El RUT es obligatorio")
        @Min(value = 1000000, message = "El RUT debe tener 7 u 8 dígitos")
        @Max(value = 99999999, message = "El RUT debe tener 7 u 8 dígitos")
        Integer rut,

        @Schema(example = "5", description = "Dígito verificador: 0-9 o K")
        @NotBlank(message = "El dígito verificador es obligatorio")
        @Pattern(regexp = "^[0-9kK]$", message = "El dígito verificador debe ser un número del 0 al 9 o K")
        String digitoVerificador,

        @Schema(example = "Juan Pérez González")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Schema(example = "juan.perez@mail.cl")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 100, message = "El email no puede superar los 100 caracteres")
        String email,

        @Schema(example = "+56912345678")
        @Pattern(regexp = "^\\+?\\d{8,15}$", message = "El teléfono debe contener entre 8 y 15 dígitos")
        String telefono
) {
}
