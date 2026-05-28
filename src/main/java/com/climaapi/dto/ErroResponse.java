package com.climaapi.dto;

public class ErroResponse {

    private boolean erro = true;
    private String codigo;
    private String mensagem;

    // Campo extra opcional (nome_informado, sigla_uf_informada, servico)
    private String nomeInformado;
    private String siglaUfInformada;
    private String servico;

    public ErroResponse(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    // Factory methods para cada tipo de erro
    public static ErroResponse cidadeNaoEncontrada(String nomeInformado) {
        ErroResponse e = new ErroResponse("CIDADE_NAO_ENCONTRADA",
                "Nenhuma cidade encontrada com o nome informado");
        e.nomeInformado = nomeInformado;
        return e;
    }

    public static ErroResponse nomeInvalido(String nomeInformado) {
        ErroResponse e = new ErroResponse("NOME_INVALIDO",
                "O nome da cidade deve conter pelo menos 2 caracteres");
        e.nomeInformado = nomeInformado;
        return e;
    }

    public static ErroResponse ufNaoEncontrada(String siglaUf) {
        ErroResponse e = new ErroResponse("UF_NAO_ENCONTRADA",
                "Estado com a sigla informada não foi encontrado");
        e.siglaUfInformada = siglaUf;
        return e;
    }

    public static ErroResponse siglaUfInvalida(String siglaUf) {
        ErroResponse e = new ErroResponse("SIGLA_UF_INVALIDA",
                "A sigla do estado deve conter exatamente 2 letras");
        e.siglaUfInformada = siglaUf;
        return e;
    }

    public static ErroResponse servicoIndisponivel(String servico) {
        ErroResponse e = new ErroResponse("SERVICO_EXTERNO_INDISPONIVEL",
                "Não foi possível obter dados do serviço externo. Tente novamente em alguns instantes");
        e.servico = servico;
        return e;
    }

    // Getters
    public boolean isErro() { return erro; }
    public String getCodigo() { return codigo; }
    public String getMensagem() { return mensagem; }

    public String getNomeInformado() { return nomeInformado; }
    public String getSiglaUfInformada() { return siglaUfInformada; }
    public String getServico() { return servico; }
}
