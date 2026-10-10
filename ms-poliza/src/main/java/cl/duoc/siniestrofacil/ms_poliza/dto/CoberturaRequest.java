package cl.duoc.siniestrofacil.ms_poliza.dto;

import cl.duoc.siniestrofacil.ms_poliza.model.TipoCobertura;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CoberturaRequest(

        @Schema(example = "DANOS_PROPIOS")
        @NotNull(message = "El tipo de cobertura es obligatorio")
        TipoCobertura tipo,

        @Schema(example = "15000000")
        @NotNull(message = "El monto máximo es obligatorio")
        @Positive(message = "El monto máximo debe ser mayor a 0")
        BigDecimal montoMaximo
) {
}
