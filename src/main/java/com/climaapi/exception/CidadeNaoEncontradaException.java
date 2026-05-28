package com.climaapi.exception;

public class CidadeNaoEncontradaException extends RuntimeException {
    private final String nomeInformado;

    public CidadeNaoEncontradaException(String nomeInformado) {
        super("Cidade não encontrada: " + nomeInformado);
        this.nomeInformado = nomeInformado;
    }

    public String getNomeInformado() { return nomeInformado; }
}
