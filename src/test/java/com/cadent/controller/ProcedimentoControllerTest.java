package com.cadent.controller;

import com.cadent.entity.Procedimento;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.ProcedimentoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimentoControllerTest {

    @Mock
    private ProcedimentoRepository procedimentoRepository;

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
        when(procedimentoRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(procedimento));

        ResponseEntity<List<Procedimento>> response = procedimentoController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Limpeza Dental", response.getBody().get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar procedimento por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));

        ResponseEntity<Procedimento> response = procedimentoController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Limpeza Dental", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(procedimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> procedimentoController.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar procedimento por nome ignorando maiúsculas/minúsculas")
    void testBuscarPorNomeSucesso() {
        when(procedimentoRepository.findByNomeIgnoreCase("limpeza dental")).thenReturn(Optional.of(procedimento));

        ResponseEntity<Procedimento> response = procedimentoController.buscarPorNome("limpeza dental");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Limpeza Dental", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve criar procedimento com sucesso")
    void testCriarSucesso() {
        when(procedimentoRepository.findByNomeIgnoreCase(procedimento.getNome())).thenReturn(Optional.empty());
        when(procedimentoRepository.save(any(Procedimento.class))).thenReturn(procedimento);

        ResponseEntity<Procedimento> response = procedimentoController.criar(procedimento);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Limpeza Dental", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao criar procedimento com mesmo nome")
    void testCriarNomeDuplicado() {
        when(procedimentoRepository.findByNomeIgnoreCase(procedimento.getNome())).thenReturn(Optional.of(procedimento));

        assertThrows(DuplicateResourceException.class, () -> procedimentoController.criar(procedimento));
        verify(procedimentoRepository, never()).save(any(Procedimento.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao criar procedimento com valor menor ou igual a zero")
    void testCriarValorInvalido() {
        procedimento.setValorPadrao(BigDecimal.ZERO);
        when(procedimentoRepository.findByNomeIgnoreCase(procedimento.getNome())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> procedimentoController.criar(procedimento));
        verify(procedimentoRepository, never()).save(any(Procedimento.class));
    }

    @Test
    @DisplayName("Deve atualizar procedimento com sucesso")
    void testAtualizarSucesso() {
        Procedimento alterado = Procedimento.builder()
                .nome("Limpeza Profunda")
                .descricao("Profilaxia e raspagem")
                .valorPadrao(new BigDecimal("200.00"))
                .build();

        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));
        when(procedimentoRepository.findByNomeIgnoreCase("Limpeza Profunda")).thenReturn(Optional.empty());
        when(procedimentoRepository.save(any(Procedimento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Procedimento> response = procedimentoController.atualizar(1L, alterado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Limpeza Profunda", response.getBody().getNome());
        assertEquals(new BigDecimal("200.00"), response.getBody().getValorPadrao());
    }

    @Test
    @DisplayName("Deve deletar procedimento com sucesso")
    void testDeletarSucesso() {
        when(procedimentoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(procedimentoRepository).deleteById(1L);

        ResponseEntity<Void> response = procedimentoController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(procedimentoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar contagem de procedimentos")
    void testContarProcedimentos() {
        when(procedimentoRepository.count()).thenReturn(5L);

        ResponseEntity<Long> response = procedimentoController.contarProcedimentos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody());
    }
}

