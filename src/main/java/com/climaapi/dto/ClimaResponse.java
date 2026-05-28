package com.climaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ClimaResponse {

    private String nome;
    private String estado;
    private ClimaInfo clima;

    @JsonProperty("consultado_em")
    private String consultadoEm;

    public ClimaResponse() {
        this.consultadoEm = Instant.now().toString();
    }

    public ClimaResponse(String nome, String estado, ClimaInfo clima) {
        this();
        this.nome = nome;
        this.estado = estado;
        this.clima = clima;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public ClimaInfo getClima() { return clima; }
    public void setClima(ClimaInfo clima) { this.clima = clima; }

    public String getConsultadoEm() { return consultadoEm; }
    public void setConsultadoEm(String consultadoEm) { this.consultadoEm = consultadoEm; }

    // ---- Classe interna ClimaInfo ----
    public static class ClimaInfo {

        @JsonProperty("temperatura_min")
        private Double temperaturaMin;

        @JsonProperty("temperatura_max")
        private Double temperaturaMax;

        private String condicao;
        private Unidades unidades;

        public ClimaInfo(Double temperaturaMin, Double temperaturaMax, String condicao) {
            this.temperaturaMin = temperaturaMin;
            this.temperaturaMax = temperaturaMax;
            this.condicao = condicao;
            this.unidades = new Unidades("°C");
        }

        public Double getTemperaturaMin() { return temperaturaMin; }
        public void setTemperaturaMin(Double temperaturaMin) { this.temperaturaMin = temperaturaMin; }

        public Double getTemperaturaMax() { return temperaturaMax; }
        public void setTemperaturaMax(Double temperaturaMax) { this.temperaturaMax = temperaturaMax; }

        public String getCondicao() { return condicao; }
        public void setCondicao(String condicao) { this.condicao = condicao; }

        public Unidades getUnidades() { return unidades; }
        public void setUnidades(Unidades unidades) { this.unidades = unidades; }
    }

    // ---- Classe interna Unidades ----
    public static class Unidades {
        private String temperatura;

        public Unidades(String temperatura) {
            this.temperatura = temperatura;
        }

        public String getTemperatura() { return temperatura; }
        public void setTemperatura(String temperatura) { this.temperatura = temperatura; }
    }
}
