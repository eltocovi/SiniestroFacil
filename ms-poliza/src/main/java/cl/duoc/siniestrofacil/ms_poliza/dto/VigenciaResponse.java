package cl.duoc.siniestrofacil.ms_poliza.dto;

import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Resultado de la verificación de vigencia de la póliza de un vehículo en una fecha.
 * Lo consume el proceso de denuncias (vía AWS Lambda) para decidir si el siniestro está cubierto.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VigenciaResponse(
        String patenteVehiculo,
        LocalDate fechaConsulta,
        boolean vigente,
        String motivo,
        String numeroPoliza,
        EstadoPoliza estado,
        LocalDate fechaInicio,
        LocalDate fechaTermino,
        String rutAsegurado,
        String nombreAsegurado,
        BigDecimal deducible,
        List<CoberturaResponse> coberturas
) {
}
