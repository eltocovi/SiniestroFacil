package cl.duoc.siniestrofacil.ms_talleres.controller;

import cl.duoc.siniestrofacil.ms_talleres.dto.AsignacionResponse;
import cl.duoc.siniestrofacil.ms_talleres.dto.DisponibilidadRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.TallerRequest;
import cl.duoc.siniestrofacil.ms_talleres.dto.TallerResponse;
import cl.duoc.siniestrofacil.ms_talleres.service.TallerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/talleres")
@Tag(name = "Talleres", description = "Registro de talleres en convenio y su disponibilidad")
public class TallerController {

    private final TallerService tallerService;

    public TallerController(TallerService tallerService) {
        this.tallerService = tallerService;
    }

    @PostMapping
    @Operation(summary = "Registrar un taller en convenio")
    public ResponseEntity<TallerResponse> registrar(@Valid @RequestBody TallerRequest request) {
        TallerResponse creado = tallerService.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar talleres, opcionalmente filtrados por comuna")
    public ResponseEntity<List<TallerResponse>> listar(@RequestParam(required = false) String comuna) {
        return ResponseEntity.ok(tallerService.listar(comuna));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Consultar talleres disponibles con cupo, opcionalmente por comuna")
    public ResponseEntity<List<TallerResponse>> listarDisponibles(@RequestParam(required = false) String comuna) {
        return ResponseEntity.ok(tallerService.listarDisponibles(comuna));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un taller por id")
    public ResponseEntity<TallerResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tallerService.obtenerPorId(id));
    }

    @GetMapping("/{id}/asignaciones")
    @Operation(summary = "Listar las denuncias asignadas a un taller (relación 1:N)")
    public ResponseEntity<List<AsignacionResponse>> listarAsignaciones(@PathVariable Long id) {
        return ResponseEntity.ok(tallerService.listarAsignaciones(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar los datos de un taller")
    public ResponseEntity<TallerResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody TallerRequest request) {
        return ResponseEntity.ok(tallerService.actualizar(id, request));
    }

    @PatchMapping("/{id}/disponibilidad")
    @Operation(summary = "Actualizar la disponibilidad y/o capacidad de un taller")
    public ResponseEntity<TallerResponse> actualizarDisponibilidad(@PathVariable Long id,
                                                                   @Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.ok(tallerService.actualizarDisponibilidad(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un taller sin asignaciones registradas")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tallerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
