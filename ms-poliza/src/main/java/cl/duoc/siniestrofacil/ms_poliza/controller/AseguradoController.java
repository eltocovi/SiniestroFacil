package cl.duoc.siniestrofacil.ms_poliza.controller;

import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoRequest;
import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;
import cl.duoc.siniestrofacil.ms_poliza.service.AseguradoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asegurados")
@Tag(name = "Asegurados", description = "Titulares de las pólizas")
public class AseguradoController {

    private final AseguradoService aseguradoService;

    public AseguradoController(AseguradoService aseguradoService) {
        this.aseguradoService = aseguradoService;
    }

    @PostMapping
    @Operation(summary = "Registrar un asegurado")
    public ResponseEntity<AseguradoResponse> registrar(@Valid @RequestBody AseguradoRequest request) {
        AseguradoResponse creado = aseguradoService.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar asegurados")
    public ResponseEntity<List<AseguradoResponse>> listar() {
        return ResponseEntity.ok(aseguradoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un asegurado por id")
    public ResponseEntity<AseguradoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(aseguradoService.obtenerPorId(id));
    }

    @GetMapping("/rut/{rut}")
    @Operation(summary = "Obtener un asegurado por RUT (sin dígito verificador)")
    public ResponseEntity<AseguradoResponse> obtenerPorRut(@PathVariable Integer rut) {
        return ResponseEntity.ok(aseguradoService.obtenerPorRut(rut));
    }

    @GetMapping("/{id}/polizas")
    @Operation(summary = "Listar las pólizas del asegurado con el detalle de sus coberturas (relación 1:N)")
    public ResponseEntity<List<PolizaResponse>> listarPolizas(@PathVariable Long id) {
        return ResponseEntity.ok(aseguradoService.listarPolizas(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un asegurado")
    public ResponseEntity<AseguradoResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody AseguradoRequest request) {
        return ResponseEntity.ok(aseguradoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un asegurado sin pólizas registradas")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        aseguradoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
