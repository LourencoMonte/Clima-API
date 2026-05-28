package com.climaapi.controller;

import com.climaapi.dto.CidadesResponse;
import com.climaapi.dto.ClimaResponse;
import com.climaapi.dto.ErroResponse;
import com.climaapi.exception.CidadeNaoEncontradaException;
import com.climaapi.service.ClimaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*") // CORS habilitado conforme requisito
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    // -------------------------------------------------------
    // ENDPOINT 1: Clima por cidade
    // GET /api/v1/clima/{nome_cidade}
    // -------------------------------------------------------
    @GetMapping("/clima/{nomeCidade}")
    public ResponseEntity<?> getClimaPorCidade(@PathVariable String nomeCidade) {

        // Validação: nome deve ter ao menos 2 caracteres (HTTP 400)
        if (nomeCidade == null || nomeCidade.trim().length() < 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErroResponse.nomeInvalido(nomeCidade));
        }

        ClimaResponse resposta = climaService.buscarClimaPorCidade(nomeCidade.trim());
        return ResponseEntity.ok(resposta);
    }

    // -------------------------------------------------------
    // ENDPOINT 2: Cidades por estado
    // GET /api/v1/cidades/{sigla_uf}?limite=10
    // -------------------------------------------------------
    @GetMapping("/cidades/{siglaUf}")
    public ResponseEntity<?> getCidadesPorEstado(
            @PathVariable String siglaUf,
            @RequestParam(defaultValue = "10") int limite) {

        // Validação: sigla deve ter exatamente 2 letras (HTTP 400)
        if (siglaUf == null || !siglaUf.matches("[a-zA-Z]{2}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErroResponse.siglaUfInvalida(siglaUf));
        }

        // Validação: limite entre 1 e 100
        if (limite < 1 || limite > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErroResponse("LIMITE_INVALIDO", "O parâmetro 'limite' deve estar entre 1 e 100"));
        }

        try {
            CidadesResponse resposta = climaService.listarCidadesPorUF(siglaUf, limite);
            return ResponseEntity.ok(resposta);
        } catch (CidadeNaoEncontradaException e) {
            // UF não existe → HTTP 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErroResponse.ufNaoEncontrada(siglaUf.toUpperCase()));
        }
    }

    // -------------------------------------------------------
    // ENDPOINT 3: Health Check
    // GET /api/v1/health
    // -------------------------------------------------------
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("status", "healthy");
        resposta.put("versao", "1.0.0");
        resposta.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(resposta);
    }
}
