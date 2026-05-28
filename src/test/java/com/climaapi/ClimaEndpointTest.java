package com.climaapi;

import com.climaapi.controller.ClimaController;
import com.climaapi.dto.ClimaResponse;
import com.climaapi.service.ClimaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClimaController.class)
public class ClimaEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClimaService climaService;

    /**
     * Teste 1: Busca por cidade válida deve retornar HTTP 200
     * com os campos nome, estado, clima e consultado_em.
     */
    @Test
    public void testBuscarCidadeValida_deveRetornar200() throws Exception {
        // Arrange: simula o retorno do serviço para "Fortaleza"
        ClimaResponse.ClimaInfo climaInfo = new ClimaResponse.ClimaInfo(24.0, 32.0, "Parcialmente Nublado");
        ClimaResponse climaMock = new ClimaResponse("Fortaleza", "CE", climaInfo);

        when(climaService.buscarClimaPorCidade("Fortaleza")).thenReturn(climaMock);

        // Act + Assert
        mockMvc.perform(get("/api/v1/clima/Fortaleza")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fortaleza"))
                .andExpect(jsonPath("$.estado").value("CE"))
                .andExpect(jsonPath("$.clima.temperatura_min").value(24.0))
                .andExpect(jsonPath("$.clima.temperatura_max").value(32.0))
                .andExpect(jsonPath("$.clima.condicao").value("Parcialmente Nublado"))
                .andExpect(jsonPath("$.consultado_em").exists());
    }

    /**
     * Teste 2: Nome com menos de 2 caracteres deve retornar HTTP 400
     * com código NOME_INVALIDO.
     */
    @Test
    public void testNomeCidadeInvalido_deveRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/clima/X")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(true))
                .andExpect(jsonPath("$.codigo").value("NOME_INVALIDO"));
    }
}
