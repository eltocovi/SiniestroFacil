package cl.duoc.siniestrofacil.ms_talleres.dto;

public record TallerResponse(
    long id,
    String nombre,
    Integer rut,
    String digitoVerificador,
    String direccion,
    String comuna,
    String telefono,
    String email,
    Integer capacidadMaxima,
    Boolean disponible,
    long asignacionesActivas,
    int cuposLibres
) {

}
