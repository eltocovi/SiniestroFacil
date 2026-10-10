package cl.duoc.siniestrofacil.ms_poliza.service;

import cl.duoc.siniestrofacil.ms_poliza.dto.AseguradoResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.CoberturaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;
import cl.duoc.siniestrofacil.ms_poliza.model.Asegurado;
import cl.duoc.siniestrofacil.ms_poliza.model.Cobertura;
import cl.duoc.siniestrofacil.ms_poliza.model.Poliza;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Convierte entidades JPA a DTOs de respuesta, evitando exponer el modelo
 * de persistencia (y las referencias circulares de las relaciones) en la API.
 */
@Component
public class PolizaMapper {

    public AseguradoResponse toResponse(Asegurado asegurado) {
        return new AseguradoResponse(
                asegurado.getId(),
                asegurado.getRut(),
                asegurado.getDv(),
                asegurado.getRut() + "-" + asegurado.getDv(),
                asegurado.getNombre(),
                asegurado.getEmail(),
                asegurado.getTelefono(),
                asegurado.getPolizas().size());
    }

    public PolizaResponse toResponse(Poliza poliza) {
        Asegurado asegurado = poliza.getAsegurado();
        return new PolizaResponse(
                poliza.getId(),
                poliza.getNumeroPoliza(),
                poliza.getPatenteVehiculo(),
                poliza.getMarcaVehiculo(),
                poliza.getModeloVehiculo(),
                poliza.getAnioVehiculo(),
                poliza.getFechaInicio(),
                poliza.getFechaTermino(),
                poliza.getPrimaMensual(),
                poliza.getDeducible(),
                poliza.estadoA(LocalDate.now()),
                asegurado.getId(),
                asegurado.getRut() + "-" + asegurado.getDv(),
                asegurado.getNombre(),
                toResponse(poliza.getCoberturas()));
    }

    public CoberturaResponse toResponse(Cobertura cobertura) {
        return new CoberturaResponse(cobertura.getId(), cobertura.getTipo(), cobertura.getMontoMaximo());
    }

    public List<CoberturaResponse> toResponse(List<Cobertura> coberturas) {
        return coberturas.stream().map(c -> toResponse(c)).toList();
    }
}
