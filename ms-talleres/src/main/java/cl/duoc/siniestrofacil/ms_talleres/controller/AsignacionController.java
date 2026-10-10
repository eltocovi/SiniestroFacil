package cl.duoc.siniestrofacil.ms_talleres.controller;

import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionResponse;
import cl.duoc.siniestrofacil.ms_talleres.dto.CambioEstadoRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.RecepcionRequest;
import cl.duoc.siniestrofacil.ms_talleres.model.EstadoAsignacion;
import cl.duoc.siniestrofacil.ms_talleres.service.AsignacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/v1/asignaciones")
@Tag(name = "Asignaciones", description = "Asignación de denuncias a talleres y recepción de vehículos")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @PostMapping
    @Operation(summary = "Asignar un taller disponible a una denuncia (automático o a un taller específico)")
    public ResponseEntity<AsignacionResponse> asignar(@Valid @RequestBody AsignacionRequest request) {
        AsignacionResponse creada = asignacionService.asignar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping
    @Operation(summary = "Listar asignaciones, opcionalmente por estado o folio de denuncia")
    public ResponseEntity<List<AsignacionResponse>> listar(@RequestParam(required = false) EstadoAsignacion estado,
                                                           @RequestParam(required = false) String folioDenuncia) {
        return ResponseEntity.ok(asignacionService.listar(estado, folioDenuncia));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una asignación por id")
    public ResponseEntity<AsignacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/recepcion")
    @Operation(summary = "Confirmar que el taller recibió el vehículo")
    public ResponseEntity<AsignacionResponse> confirmarRecepcion(@PathVariable Long id,
                                                                 @Valid @RequestBody(required = false) RecepcionRequest request) {
        String observaciones = request != null ? request.observaciones() : null;
        return ResponseEntity.ok(asignacionService.confirmarRecepcion(id, observaciones));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Avanzar o cancelar una asignación (EN_REPARACION, FINALIZADA, CANCELADA)")
    public ResponseEntity<AsignacionResponse> cambiarEstado(@PathVariable Long id,
                                                            @Valid @RequestBody CambioEstadoRequest request) {
        return ResponseEntity.ok(asignacionService.cambiarEstado(id, request.estado()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una asignación")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asignacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
