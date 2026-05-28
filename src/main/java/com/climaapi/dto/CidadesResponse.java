package com.climaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public class CidadesResponse {

    private String uf;

    @JsonProperty("quantidade_retornada")
    private int quantidadeRetornada;

    private List<CidadeItem> cidades;

    @JsonProperty("consultado_em")
    private String consultadoEm;

    public CidadesResponse() {
        this.consultadoEm = Instant.now().toString();
    }

    public CidadesResponse(String uf, List<CidadeItem> cidades) {
        this();
        this.uf = uf.toUpperCase();
        this.cidades = cidades;
        this.quantidadeRetornada = cidades.size();
    }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public int getQuantidadeRetornada() { return quantidadeRetornada; }
    public void setQuantidadeRetornada(int quantidadeRetornada) { this.quantidadeRetornada = quantidadeRetornada; }

    public List<CidadeItem> getCidades() { return cidades; }
    public void setCidades(List<CidadeItem> cidades) { this.cidades = cidades; }

    public String getConsultadoEm() { return consultadoEm; }
    public void setConsultadoEm(String consultadoEm) { this.consultadoEm = consultadoEm; }

    // ---- Classe interna CidadeItem ----
    public static class CidadeItem {
        private String nome;

        public CidadeItem(String nome) {
            this.nome = nome;
        }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }
}
