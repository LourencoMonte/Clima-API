package com.climaapi;

import com.climaapi.controller.ClimaController;
import com.climaapi.dto.CidadesResponse;
import com.climaapi.exception.CidadeNaoEncontradaException;
import com.climaapi.service.ClimaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClimaController.class)
public class CidadesEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClimaService climaService;

    /**
     * Teste 3: Busca por cidade inexistente deve retornar HTTP 404
     * com código CIDADE_NAO_ENCONTRADA.
     */
    @Test
    public void testCidadeInexistente_deveRetornar404() throws Exception {
        // Simula serviço lançando exceção de cidade não encontrada
        when(climaService.buscarClimaPorCidade("CidadeInexistente"))
                .thenThrow(new CidadeNaoEncontradaException("CidadeInexistente"));

        mockMvc.perform(get("/api/v1/clima/CidadeInexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value(true))
                .andExpect(jsonPath("$.codigo").value("CIDADE_NAO_ENCONTRADA"))
                .andExpect(jsonPath("$.nome_informado").value("CidadeInexistente"));
    }

    /**
     * Teste 4: Listagem de cidades por UF válida deve retornar HTTP 200
     * com campos uf, quantidade_retornada e cidades.
     */
    @Test
    public void testListarCidadesPorUF_deveRetornar200() throws Exception {
        List<CidadesResponse.CidadeItem> lista = List.of(
                new CidadesResponse.CidadeItem("Fortaleza"),
                new CidadesResponse.CidadeItem("Caucaia"),
                new CidadesResponse.CidadeItem("Juazeiro do Norte")
        );
        CidadesResponse mockResposta = new CidadesResponse("CE", lista);

        when(climaService.listarCidadesPorUF("CE", 10)).thenReturn(mockResposta);

        mockMvc.perform(get("/api/v1/cidades/CE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uf").value("CE"))
                .andExpect(jsonPath("$.quantidade_retornada").value(3))
                .andExpect(jsonPath("$.cidades[0].nome").value("Fortaleza"))
                .andExpect(jsonPath("$.consultado_em").exists());
    }

    /**
     * Teste 5: Sigla UF inválida deve retornar HTTP 400
     * com código SIGLA_UF_INVALIDA.
     */
    @Test
    public void testSiglaUfInvalida_deveRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/cidades/ceara")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(true))
                .andExpect(jsonPath("$.codigo").value("SIGLA_UF_INVALIDA"));
    }

    /**
     * Teste 6: Health check deve retornar HTTP 200 com status "healthy".
     */
    @Test
    public void testHealthCheck_deveRetornar200() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.versao").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
