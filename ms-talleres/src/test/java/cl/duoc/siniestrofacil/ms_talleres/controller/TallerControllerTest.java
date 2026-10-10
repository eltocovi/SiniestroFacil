package cl.duoc.siniestrofacil.ms_talleres.controller;

import cl.duoc.siniestrofacil.ms_talleres.dto.TallerResponse;
import cl.duoc.siniestrofacil.ms_talleres.service.TallerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TallerController.class)
class TallerControllerTest {

    private static final String BODY_VALIDO = """
            {"nombre":"Taller Los Andes","rut":76543210,"digitoVerificador":"3","direccion":"Av. Vicuña Mackenna 1234",
             "comuna":"Santiago","telefono":"+56222334455","capacidadMaxima":5}
            """;

    private static final TallerResponse TALLER = new TallerResponse(1L, "Taller Los Andes", 76543210, "3", "76543210-3",
            "Av. Vicuña Mackenna 1234", "Santiago", "+56222334455", null, 5, true, 0, 5);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TallerService tallerService;

    @Test
    void registrar_conBodyValido_retorna201ConLocation() throws Exception {
        when(tallerService.registrar(any())).thenReturn(TALLER);

        mockMvc.perform(post("/api/v1/talleres").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/talleres/1"))
                .andExpect(jsonPath("$.cuposLibres").value(5));
    }

    @Test
    void registrar_sinCamposObligatorios_retorna400ConErroresPorCampo() throws Exception {
        mockMvc.perform(post("/api/v1/talleres").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.rut").exists())
                .andExpect(jsonPath("$.errores.digitoVerificador").exists())
                .andExpect(jsonPath("$.errores.capacidadMaxima").exists());
    }

    @Test
    void registrar_conRutDuplicado_retorna409() throws Exception {
        when(tallerService.registrar(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un taller con RUT 76543210"));

        mockMvc.perform(post("/api/v1/talleres").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un taller con RUT 76543210"));
    }

    @Test
    void listarDisponibles_retorna200() throws Exception {
        when(tallerService.listarDisponibles("Santiago")).thenReturn(List.of(TALLER));

        mockMvc.perform(get("/api/v1/talleres/disponibles").param("comuna", "Santiago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disponible").value(true));
    }

    @Test
    void actualizarDisponibilidad_sinCampoDisponible_retorna400() throws Exception {
        mockMvc.perform(patch("/api/v1/talleres/1/disponibilidad")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"capacidadMaxima\":3}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.disponible").exists());
    }

    @Test
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(tallerService.obtenerPorId(99L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado con id 99"));

        mockMvc.perform(get("/api/v1/talleres/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Taller no encontrado con id 99"))
                .andExpect(jsonPath("$.path").value("/api/v1/talleres/99"));
    }

    @Test
    void obtenerPorId_conIdNoNumerico_retorna400() throws Exception {
        mockMvc.perform(get("/api/v1/talleres/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_conAsignaciones_retorna422() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "tiene asignaciones")).when(tallerService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/talleres/1"))
                .andExpect(status().isUnprocessableContent());
    }
}
