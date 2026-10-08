package cl.duoc.siniestrofacil.ms_talleres.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DisponibilidadRequest(
    @Schema (example = "false")
    @NotNull (message = "El campo 'disponible' es obligatorio")
    Boolean disponible,

    @Schema (example = "1", description = "Opcional: nueva cantidad de cupos disponibles. Si no se entrega, se mantiene la cantidad actual")
    @Min (value = 1, message = "La cantidad de cupos disponibles debe ser mayor o igual a 1")
    @Max (value = 100, message = "La cantidad de cupos disponibles debe ser menor o igual a 100")
    Integer capacidadMaxima
) 

{}
