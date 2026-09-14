package com.cadent.exception;

import com.cadent.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/api/teste");
    }

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException e retornar HTTP 404")
    void testHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso não encontrado");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFound(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso não encontrado", response.getBody().getMensagem());
        assertEquals("Recurso não encontrado", response.getBody().getDetalhes());
        assertEquals("/api/teste", response.getBody().getCaminho());
    }

    @Test
    @DisplayName("Deve tratar DuplicateResourceException e retornar HTTP 409")
    void testHandleDuplicateResource() {
        DuplicateResourceException ex = new DuplicateResourceException("Recurso duplicado");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResource(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Recurso já existe", response.getBody().getMensagem());
        assertEquals("Recurso duplicado", response.getBody().getDetalhes());
        assertEquals("/api/teste", response.getBody().getCaminho());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException e retornar HTTP 400 com mapa de erros")
    void testHandleValidationException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("paciente", "cpf", "CPF inválido");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Erro de validação", response.getBody().getMensagem());
        assertEquals("CPF inválido", response.getBody().getErros().get("cpf"));
        assertEquals("/api/teste", response.getBody().getCaminho());
    }

    @Test
    @DisplayName("Deve tratar Exception genérica e retornar HTTP 500")
    void testHandleGlobalException() {
        Exception ex = new RuntimeException("Erro inesperado no servidor");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Erro interno do servidor", response.getBody().getMensagem());
        assertEquals("Erro inesperado no servidor", response.getBody().getDetalhes());
        assertEquals("/api/teste", response.getBody().getCaminho());
    }
}

