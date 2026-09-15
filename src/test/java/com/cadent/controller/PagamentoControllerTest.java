package com.cadent.controller;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Pagamento;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.service.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private PagamentoController pagamentoController;

    private Pagamento pagamento;
    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
        agendamento.setId(1L);

        pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setAgendamento(agendamento);
        pagamento.setValorTotal(new BigDecimal("250.00"));
        pagamento.setMetodo(Pagamento.MetodoPagamento.PIX);
        pagamento.setStatus(Pagamento.StatusPagamento.PENDENTE);
        pagamento.setDataPagamento(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve listar todos os pagamentos")
    void testListarTodos() {
        when(pagamentoService.listarTodos()).thenReturn(List.of(pagamento));

        ResponseEntity<List<Pagamento>> response = pagamentoController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar pagamento por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(pagamentoService.buscarPorId(1L)).thenReturn(pagamento);

        ResponseEntity<Pagamento> response = pagamentoController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(pagamentoService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Pagamento com ID 99 não encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> pagamentoController.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar pagamento por Agendamento ID")
    void testBuscarPorAgendamentoSucesso() {
        when(pagamentoService.buscarPorAgendamento(1L)).thenReturn(pagamento);

        ResponseEntity<Pagamento> response = pagamentoController.buscarPorAgendamento(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getAgendamento().getId());
    }

    @Test
    @DisplayName("Deve buscar pagamentos por Status válido")
    void testBuscarPorStatusSucesso() {
        when(pagamentoService.buscarPorStatus("PENDENTE")).thenReturn(List.of(pagamento));

        ResponseEntity<List<Pagamento>> response = pagamentoController.buscarPorStatus("PENDENTE");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para status de pagamento inválido")
    void testBuscarPorStatusInvalido() {
        when(pagamentoService.buscarPorStatus("INVALIDO"))
                .thenThrow(new IllegalArgumentException("Status inválido. Use: PENDENTE, PAGO ou CANCELADO"));

        assertThrows(IllegalArgumentException.class, () -> pagamentoController.buscarPorStatus("INVALIDO"));
    }

    @Test
    @DisplayName("Deve criar pagamento com sucesso")
    void testCriarSucesso() {
        when(pagamentoService.salvar(any(Pagamento.class))).thenReturn(pagamento);

        ResponseEntity<Pagamento> response = pagamentoController.criar(pagamento);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException se agendamento já possuir pagamento")
    void testCriarPagamentoDuplicado() {
        when(pagamentoService.salvar(any(Pagamento.class)))
                .thenThrow(new DuplicateResourceException("Já existe um pagamento registrado para este agendamento"));

        assertThrows(DuplicateResourceException.class, () -> pagamentoController.criar(pagamento));
    }

    @Test
    @DisplayName("Deve atualizar pagamento com sucesso")
    void testAtualizarSucesso() {
        Pagamento alterado = new Pagamento();
        alterado.setStatus(Pagamento.StatusPagamento.PAGO);
        alterado.setMetodo(Pagamento.MetodoPagamento.CREDITO);

        when(pagamentoService.atualizar(eq(1L), any(Pagamento.class))).thenReturn(alterado);

        ResponseEntity<Pagamento> response = pagamentoController.atualizar(1L, alterado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Pagamento.StatusPagamento.PAGO, response.getBody().getStatus());
        assertEquals(Pagamento.MetodoPagamento.CREDITO, response.getBody().getMetodo());
    }

    @Test
    @DisplayName("Deve deletar pagamento com sucesso")
    void testDeletarSucesso() {
        doNothing().when(pagamentoService).deletar(1L);

        ResponseEntity<Void> response = pagamentoController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pagamentoService, times(1)).deletar(1L);
    }
}