package com.cadent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;                      // 400, 404, 409, etc
    private String mensagem;                 // Mensagem principal
    private String detalhes;                 // Detalhes do erro
    private LocalDateTime timestamp;         // Quando ocorreu
    private String caminho;                  // Qual endpoint
    private Map<String, String> erros;       // Erros por campo (validação)
}