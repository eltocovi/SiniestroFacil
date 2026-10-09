package cl.duoc.siniestrofacil.ms_poliza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Datos de entrada para emitir o actualizar una póliza, incluyendo sus coberturas. */
public record PolizaRequest(

        @Schema(example = "ABCD12")
        @NotBlank(message = "La patente es obligatoria")
        @Pattern(regexp = "^[A-Za-z]{2}[A-Za-z0-9]{2}\\d{2}$", message = "La patente debe tener formato AB1234 o ABCD12")
        String patenteVehiculo,

        @Schema(example = "Toyota")
        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 50, message = "La marca no puede superar los 50 caracteres")
        String marcaVehiculo,

        @Schema(example = "Yaris")
        @NotBlank(message = "El modelo es obligatorio")
        @Size(max = 50, message = "El modelo no puede superar los 50 caracteres")
        String modeloVehiculo,

        @Schema(example = "2022")
        @NotNull(message = "El año del vehículo es obligatorio")
        @Min(value = 1980, message = "El año del vehículo debe ser 1980 o posterior")
        @Max(value = 2100, message = "El año del vehículo no es válido")
        Integer anioVehiculo,

        @Schema(example = "2026-01-01")
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @Schema(example = "2026-12-31")
        @NotNull(message = "La fecha de término es obligatoria")
        LocalDate fechaTermino,

        @Schema(example = "45990")
        @NotNull(message = "La prima mensual es obligatoria")
        @Positive(message = "La prima mensual debe ser mayor a 0")
        BigDecimal primaMensual,

        @Schema(example = "150000")
        @NotNull(message = "El deducible es obligatorio")
        @PositiveOrZero(message = "El deducible no puede ser negativo")
        BigDecimal deducible,

        @Schema(example = "1")
        @NotNull(message = "El id del asegurado es obligatorio")
        Long aseguradoId,

        @NotEmpty(message = "La póliza debe incluir al menos una cobertura")
        List<@Valid CoberturaRequest> coberturas
) {
}
