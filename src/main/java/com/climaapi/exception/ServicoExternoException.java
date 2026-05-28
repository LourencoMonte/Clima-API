package com.climaapi.exception;

public class ServicoExternoException extends RuntimeException {
    private final String servico;

    public ServicoExternoException(String servico, Throwable cause) {
        super("Serviço externo indisponível: " + servico, cause);
        this.servico = servico;
    }

    public String getServico() { return servico; }
}
