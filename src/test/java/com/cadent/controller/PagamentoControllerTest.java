package com.cadent.controller;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Pagamento;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.AgendamentoRepository;
import com.cadent.repository.PagamentoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

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
        when(pagamentoRepository.findAll()).thenReturn(List.of(pagamento));

        ResponseEntity<List<Pagamento>> response = pagamentoController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar pagamento por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        ResponseEntity<Pagamento> response = pagamentoController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(pagamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pagamentoController.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar pagamento por Agendamento ID")
    void testBuscarPorAgendamentoSucesso() {
        when(pagamentoRepository.findByAgendamentoId(1L)).thenReturn(Optional.of(pagamento));

        ResponseEntity<Pagamento> response = pagamentoController.buscarPorAgendamento(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getAgendamento().getId());
    }

    @Test
    @DisplayName("Deve buscar pagamentos por Status válido")
    void testBuscarPorStatusSucesso() {
        when(pagamentoRepository.findByStatus(Pagamento.StatusPagamento.PENDENTE)).thenReturn(List.of(pagamento));

        ResponseEntity<List<Pagamento>> response = pagamentoController.buscarPorStatus("PENDENTE");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para status de pagamento inválido")
    void testBuscarPorStatusInvalido() {
        assertThrows(IllegalArgumentException.class, () -> pagamentoController.buscarPorStatus("INVALIDO"));
    }

    @Test
    @DisplayName("Deve criar pagamento com sucesso")
    void testCriarSucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(pagamentoRepository.findByAgendamentoId(1L)).thenReturn(Optional.empty());
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        ResponseEntity<Pagamento> response = pagamentoController.criar(pagamento);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException se agendamento já possuir pagamento")
    void testCriarPagamentoDuplicado() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(pagamentoRepository.findByAgendamentoId(1L)).thenReturn(Optional.of(pagamento));

        assertThrows(DuplicateResourceException.class, () -> pagamentoController.criar(pagamento));
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar pagamento com sucesso")
    void testAtualizarSucesso() {
        Pagamento alterado = new Pagamento();
        alterado.setStatus(Pagamento.StatusPagamento.PAGO);
        alterado.setMetodo(Pagamento.MetodoPagamento.CREDITO);

        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Pagamento> response = pagamentoController.atualizar(1L, alterado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Pagamento.StatusPagamento.PAGO, response.getBody().getStatus());
        assertEquals(Pagamento.MetodoPagamento.CREDITO, response.getBody().getMetodo());
    }

    @Test
    @DisplayName("Deve deletar pagamento com sucesso")
    void testDeletarSucesso() {
        when(pagamentoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pagamentoRepository).deleteById(1L);

        ResponseEntity<Void> response = pagamentoController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pagamentoRepository, times(1)).deleteById(1L);
    }
}

