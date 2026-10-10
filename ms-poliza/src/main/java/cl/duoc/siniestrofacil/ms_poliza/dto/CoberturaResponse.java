package cl.duoc.siniestrofacil.ms_poliza.dto;

import cl.duoc.siniestrofacil.ms_poliza.model.TipoCobertura;

import java.math.BigDecimal;

public record CoberturaResponse(
        Long id,
        TipoCobertura tipo,
        BigDecimal montoMaximo
) {
}
