package cl.duoc.siniestrofacil.ms_poliza.controller;

import cl.duoc.siniestrofacil.ms_poliza.dto.CambioEstadoRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.CoberturaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.VigenciaResponse;
import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;
import cl.duoc.siniestrofacil.ms_poliza.service.PolizaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/polizas")
@Tag(name = "Pólizas", description = "Registro de pólizas, coberturas y verificación de vigencia")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @PostMapping
    @Operation(summary = "Emitir una póliza con sus coberturas")
    public ResponseEntity<PolizaResponse> emitir(@Valid @RequestBody PolizaRequest request) {
        PolizaResponse creada = polizaService.emitir(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping
    @Operation(summary = "Listar pólizas, opcionalmente por estado (ACTIVA, SUSPENDIDA, VENCIDA) o patente")
    public ResponseEntity<List<PolizaResponse>> listar(@RequestParam(required = false) EstadoPoliza estado,
                                                       @RequestParam(required = false) String patente) {
        return ResponseEntity.ok(polizaService.listar(estado, patente));
    }

    @GetMapping("/vigencia")
    @Operation(summary = "Verificar si un vehículo tiene póliza vigente en una fecha (por defecto, hoy)")
    public ResponseEntity<VigenciaResponse> consultarVigencia(
            @RequestParam String patente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(polizaService.consultarVigencia(patente, fecha));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una póliza por id")
    public ResponseEntity<PolizaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.obtenerPorId(id));
    }

    @GetMapping("/numero/{numeroPoliza}")
    @Operation(summary = "Obtener una póliza por su número")
    public ResponseEntity<PolizaResponse> obtenerPorNumero(@PathVariable String numeroPoliza) {
        return ResponseEntity.ok(polizaService.obtenerPorNumero(numeroPoliza));
    }

    @GetMapping("/{id}/coberturas")
    @Operation(summary = "Detalle de las coberturas de una póliza (relación 1:N)")
    public ResponseEntity<List<CoberturaResponse>> listarCoberturas(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarCoberturas(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una póliza y reemplazar sus coberturas")
    public ResponseEntity<PolizaResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody PolizaRequest request) {
        return ResponseEntity.ok(polizaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Suspender o reactivar una póliza")
    public ResponseEntity<PolizaResponse> cambiarEstado(@PathVariable Long id,
                                                        @Valid @RequestBody CambioEstadoRequest request) {
        return ResponseEntity.ok(polizaService.cambiarEstado(id, request.estado()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una póliza y sus coberturas")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        polizaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
