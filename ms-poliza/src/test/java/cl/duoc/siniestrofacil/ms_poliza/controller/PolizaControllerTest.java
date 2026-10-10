package cl.duoc.siniestrofacil.ms_poliza.controller;

import cl.duoc.siniestrofacil.ms_poliza.dto.PolizaResponse;
import cl.duoc.siniestrofacil.ms_poliza.dto.VigenciaResponse;
import cl.duoc.siniestrofacil.ms_poliza.model.EstadoPoliza;
import cl.duoc.siniestrofacil.ms_poliza.service.PolizaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolizaController.class)
class PolizaControllerTest {

    private static final String BODY_VALIDO = """
            {"patenteVehiculo":"ABCD12","marcaVehiculo":"Toyota","modeloVehiculo":"Yaris","anioVehiculo":2022,
             "fechaInicio":"2026-01-01","fechaTermino":"2026-12-31","primaMensual":45990,"deducible":150000,
             "aseguradoId":1,"coberturas":[{"tipo":"DANOS_PROPIOS","montoMaximo":15000000}]}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PolizaService polizaService;

    @Test
    void emitir_conBodyValido_retorna201ConLocation() throws Exception {
        when(polizaService.emitir(any())).thenReturn(new PolizaResponse(1L, "POL-ABC12345", "ABCD12", "Toyota",
                "Yaris", 2022, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), new BigDecimal("45990"),
                new BigDecimal("150000"), EstadoPoliza.ACTIVA, 1L, "12345678-5", "Juan Pérez", List.of()));

        mockMvc.perform(post("/api/v1/polizas").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/polizas/1"))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    void emitir_sinCoberturasYConDatosInvalidos_retorna400PorCampo() throws Exception {
        mockMvc.perform(post("/api/v1/polizas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patenteVehiculo\":\"12\",\"primaMensual\":-1,\"coberturas\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.patenteVehiculo").exists())
                .andExpect(jsonPath("$.errores.primaMensual").exists())
                .andExpect(jsonPath("$.errores.coberturas").exists());
    }

    @Test
    void consultarVigencia_retorna200ConResultado() throws Exception {
        when(polizaService.consultarVigencia("ABCD12", null)).thenReturn(new VigenciaResponse("ABCD12",
                LocalDate.of(2026, 10, 8), false, "El vehículo no tiene pólizas registradas",
                null, null, null, null, null, null, null, null));

        mockMvc.perform(get("/api/v1/polizas/vigencia").param("patente", "ABCD12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vigente").value(false))
                .andExpect(jsonPath("$.motivo").value("El vehículo no tiene pólizas registradas"));
    }

    @Test
    void consultarVigencia_conFechaInvalida_retorna400() throws Exception {
        mockMvc.perform(get("/api/v1/polizas/vigencia").param("patente", "ABCD12").param("fecha", "ayer"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(polizaService.obtenerPorId(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Póliza no encontrada con id 99"));

        mockMvc.perform(get("/api/v1/polizas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Póliza no encontrada con id 99"));
    }

    @Test
    void cambiarEstado_conEstadoInexistente_retorna400() throws Exception {
        mockMvc.perform(patch("/api/v1/polizas/1/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"INVENTADO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_filtraPorEstado() throws Exception {
        when(polizaService.listar(EstadoPoliza.SUSPENDIDA, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/polizas").param("estado", "SUSPENDIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void emitir_conflicto_retorna409() throws Exception {
        when(polizaService.emitir(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "El vehículo ABCD12 ya tiene una póliza activa"));

        mockMvc.perform(post("/api/v1/polizas").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void consultarVigencia_sinPatente_retorna400() throws Exception {
        mockMvc.perform(get("/api/v1/polizas/vigencia"))
                .andExpect(status().isBadRequest());
    }
}
