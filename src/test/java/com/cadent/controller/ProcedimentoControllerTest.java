package com.cadent.controller;

import com.cadent.entity.Procedimento;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.service.ProcedimentoService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimentoControllerTest {

    @Mock
    private ProcedimentoService procedimentoService;

    @InjectMocks
    private ProcedimentoController procedimentoController;

    private Procedimento procedimento;

    @BeforeEach
    void setUp() {
        procedimento = Procedimento.builder()
                .id(1L)
                .nome("Limpeza Dental")
                .descricao("Profilaxia completa")
                .valorPadrao(new BigDecimal("150.00"))
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os procedimentos ordenados por nome")
    void testListarTodos() {
        when(procedimentoService.listarTodos()).thenReturn(List.of(procedimento));

        ResponseEntity<List<Procedimento>> response = procedimentoController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Limpeza Dental", response.getBody().get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar procedimento por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(procedimentoService.buscarPorId(1L)).thenReturn(procedimento);

        ResponseEntity<Procedimento> response = procedimentoController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Limpeza Dental", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(procedimentoService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Procedimento com ID 99 não encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> procedimentoController.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar procedimento por nome ignorando maiúsculas/minúsculas")
    void testBuscarPorNomeSucesso() {
        when(procedimentoService.buscarPorNome("limpeza dental")).thenReturn(procedimento);

        ResponseEntity<Procedimento> response = procedimentoController.buscarPorNome("limpeza dental");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Limpeza Dental", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve criar procedimento com sucesso")
    void testCriarSucesso() {
        when(procedimentoService.salvar(any(Procedimento.class))).thenReturn(procedimento);

        ResponseEntity<Procedimento> response = procedimentoController.criar(procedimento);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Limpeza Dental", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao criar procedimento com mesmo nome")
    void testCriarNomeDuplicado() {
        when(procedimentoService.salvar(any(Procedimento.class)))
                .thenThrow(new DuplicateResourceException("Já existe um procedimento com nome '" + procedimento.getNome() + "'"));

        assertThrows(DuplicateResourceException.class, () -> procedimentoController.criar(procedimento));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao criar procedimento com valor menor ou igual a zero")
    void testCriarValorInvalido() {
        when(procedimentoService.salvar(any(Procedimento.class)))
                .thenThrow(new IllegalArgumentException("Valor padrão deve ser maior que zero"));

        assertThrows(IllegalArgumentException.class, () -> procedimentoController.criar(procedimento));
    }

    @Test
    @DisplayName("Deve atualizar procedimento com sucesso")
    void testAtualizarSucesso() {
        Procedimento alterado = Procedimento.builder()
                .nome("Limpeza Profunda")
                .descricao("Profilaxia e raspagem")
                .valorPadrao(new BigDecimal("200.00"))
                .build();

        when(procedimentoService.atualizar(eq(1L), any(Procedimento.class))).thenReturn(alterado);

        ResponseEntity<Procedimento> response = procedimentoController.atualizar(1L, alterado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Limpeza Profunda", response.getBody().getNome());
        assertEquals(new BigDecimal("200.00"), response.getBody().getValorPadrao());
    }

    @Test
    @DisplayName("Deve deletar procedimento com sucesso")
    void testDeletarSucesso() {
        doNothing().when(procedimentoService).deletar(1L);

        ResponseEntity<Void> response = procedimentoController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(procedimentoService, times(1)).deletar(1L);
    }

    @Test
    @DisplayName("Deve retornar contagem de procedimentos")
    void testContarProcedimentos() {
        when(procedimentoService.contarProcedimentos()).thenReturn(5L);

        ResponseEntity<Long> response = procedimentoController.contarProcedimentos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody());
    }
}