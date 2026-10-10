package cl.duoc.siniestrofacil.ms_poliza.dto;

import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PolizaResponse(
        Long id,
        String numeroPoliza,
        String patenteVehiculo,
        String marcaVehiculo,
        String modeloVehiculo,
        Integer anioVehiculo,
        LocalDate fechaInicio,
        LocalDate fechaTermino,
        BigDecimal primaMensual,
        BigDecimal deducible,
        EstadoPoliza estado,
        Long aseguradoId,
        String rutAsegurado,
        String nombreAsegurado,
        List<CoberturaResponse> coberturas
) {
}
