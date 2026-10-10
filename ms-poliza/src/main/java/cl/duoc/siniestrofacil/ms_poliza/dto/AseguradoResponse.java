package cl.duoc.siniestrofacil.ms_poliza.dto;

public record AseguradoResponse(
        Long id,
        Integer rut,
        String digitoVerificador,
        String rutCompleto,
        String nombre,
        String email,
        String telefono,
        int cantidadPolizas
) {
}
