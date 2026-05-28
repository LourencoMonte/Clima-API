package com.climaapi.exception;

import com.climaapi.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleCidadeNaoEncontrada(CidadeNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.cidadeNaoEncontrada(ex.getNomeInformado()));
    }

    @ExceptionHandler(ServicoExternoException.class)
    public ResponseEntity<ErroResponse> handleServicoExterno(ServicoExternoException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErroResponse.servicoIndisponivel(ex.getServico()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> handleIllegalArgument(IllegalArgumentException ex) {
        // Reutilizado para erros 400 genéricos
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse("REQUISICAO_INVALIDA", ex.getMessage()));
    }
}
