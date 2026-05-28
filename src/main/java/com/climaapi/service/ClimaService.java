package com.climaapi.service;

import com.climaapi.dto.CidadesResponse;
import com.climaapi.dto.ClimaResponse;
import com.climaapi.dto.ErroResponse;
import com.climaapi.exception.CidadeNaoEncontradaException;
import com.climaapi.exception.ServicoExternoException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClimaService {

    @Value("${app.brasil-api.base-url}")
    private String brasilApiUrl;

    @Value("${app.open-meteo.base-url}")
    private String openMeteoUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // -------------------------------------------------------
    // ENDPOINT 1: Clima por nome de cidade
    // -------------------------------------------------------
    public ClimaResponse buscarClimaPorCidade(String nomeCidade) {
        // 1. Busca a cidade na Brasil API (CPTEC) para obter código e estado
        JsonNode cidadeCptec = buscarCidadeCptec(nomeCidade);

        String nomeOficial = cidadeCptec.get("nome").asText();
        String estado = cidadeCptec.get("estado").asText();
        int codigoCptec = cidadeCptec.get("id").asInt();

        // 2. Busca coordenadas via IBGE para usar no Open-Meteo
        double[] coordenadas = buscarCoordenadasIBGE(nomeOficial, estado);

        // 3. Busca clima no Open-Meteo com lat/long dinâmico
        ClimaResponse.ClimaInfo climaInfo = buscarClimaOpenMeteo(coordenadas[0], coordenadas[1]);

        return new ClimaResponse(nomeOficial, estado, climaInfo);
    }

    // -------------------------------------------------------
    // ENDPOINT 2: Cidades por UF
    // -------------------------------------------------------
    public CidadesResponse listarCidadesPorUF(String uf, int limite) {
        String url = brasilApiUrl + "/ibge/municipios/v1/" + uf.toUpperCase() + "?providers=dados-abertos-br,gov,wikipedia";

        JsonNode resposta = chamarApi(url, "IBGE");

        if (resposta == null || !resposta.isArray() || resposta.size() == 0) {
            throw new CidadeNaoEncontradaException(uf); // reutiliza a exception — UF inválida
        }

        List<CidadesResponse.CidadeItem> cidades = new ArrayList<>();
        int count = 0;
        for (JsonNode municipio : resposta) {
            if (count >= limite) break;
            cidades.add(new CidadesResponse.CidadeItem(municipio.get("nome").asText()));
            count++;
        }

        return new CidadesResponse(uf, cidades);
    }

    // -------------------------------------------------------
    // Métodos auxiliares privados
    // -------------------------------------------------------

    private JsonNode buscarCidadeCptec(String nomeCidade) {
        String nomeEncoded = URLEncoder.encode(nomeCidade, StandardCharsets.UTF_8);
        String url = brasilApiUrl + "/cptec/v1/cidade/" + nomeEncoded;

        JsonNode resposta = chamarApi(url, "CPTEC");

        if (resposta == null || !resposta.isArray() || resposta.size() == 0) {
            throw new CidadeNaoEncontradaException(nomeCidade);
        }

        // Retorna a primeira cidade encontrada
        return resposta.get(0);
    }

    private double[] buscarCoordenadasIBGE(String nomeCidade, String uf) {
        // Usa a API de municipios do IBGE para obter lat/long
        String url = brasilApiUrl + "/ibge/municipios/v1/" + uf + "?providers=dados-abertos-br,gov,wikipedia";

        JsonNode municipios = chamarApi(url, "IBGE");
        if (municipios != null && municipios.isArray()) {
            for (JsonNode municipio : municipios) {
                String nome = municipio.get("nome").asText();
                if (nome.equalsIgnoreCase(nomeCidade)) {
                    // Nem toda resposta inclui lat/long — fallback para Open-Meteo geocoding
                    if (municipio.has("latitude") && municipio.has("longitude")) {
                        return new double[]{
                                municipio.get("latitude").asDouble(),
                                municipio.get("longitude").asDouble()
                        };
                    }
                }
            }
        }

        // Fallback: Open-Meteo geocoding API
        return buscarCoordenadasOpenMeteoGeocoding(nomeCidade);
    }

    private double[] buscarCoordenadasOpenMeteoGeocoding(String nomeCidade) {
        String nomeEncoded = URLEncoder.encode(nomeCidade, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + nomeEncoded + "&count=1&language=pt&format=json";

        JsonNode resposta = chamarApi(url, "Open-Meteo Geocoding");

        if (resposta == null || !resposta.has("results") || resposta.get("results").size() == 0) {
            throw new CidadeNaoEncontradaException(nomeCidade);
        }

        JsonNode primeiro = resposta.get("results").get(0);
        return new double[]{
                primeiro.get("latitude").asDouble(),
                primeiro.get("longitude").asDouble()
        };
    }

    private ClimaResponse.ClimaInfo buscarClimaOpenMeteo(double latitude, double longitude) {
        String url = openMeteoUrl + "/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&daily=temperature_2m_max,temperature_2m_min,weathercode"
                + "&timezone=America%2FSao_Paulo"
                + "&forecast_days=1";

        JsonNode resposta = chamarApi(url, "Open-Meteo");

        if (resposta == null || !resposta.has("daily")) {
            throw new ServicoExternoException("Open-Meteo", new RuntimeException("Resposta inválida"));
        }

        JsonNode daily = resposta.get("daily");
        double tempMax = daily.get("temperature_2m_max").get(0).asDouble();
        double tempMin = daily.get("temperature_2m_min").get(0).asDouble();
        int weatherCode = daily.get("weathercode").get(0).asInt();
        String condicao = traduzirWeatherCode(weatherCode);

        return new ClimaResponse.ClimaInfo(tempMin, tempMax, condicao);
    }

    private JsonNode chamarApi(String url, String nomeServico) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return null;
            }

            if (response.statusCode() >= 500) {
                throw new ServicoExternoException(nomeServico,
                        new RuntimeException("HTTP " + response.statusCode()));
            }

            return mapper.readTree(response.body());

        } catch (ServicoExternoException e) {
            throw e;
        } catch (IOException | InterruptedException e) {
            throw new ServicoExternoException(nomeServico, e);
        }
    }

    private String traduzirWeatherCode(int code) {
        if (code == 0) return "Céu Limpo";
        if (code <= 3) return "Parcialmente Nublado";
        if (code <= 49) return "Nublado ou com Neblina";
        if (code <= 67) return "Chuva";
        if (code <= 77) return "Neve";
        if (code <= 82) return "Aguaceiros";
        if (code <= 99) return "Tempestade";
        return "Indisponível";
    }
}
