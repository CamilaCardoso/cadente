package com.cadent.controller;

import com.cadent.entity.Paciente;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.service.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteControllerTest {

    @Mock
    private PacienteService pacienteService;

    @InjectMocks
    private PacienteController pacienteController;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = Paciente.builder()
                .id(1L)
                .nome("Maria Silva")
                .cpf("52998224725")
                .email("maria@email.com")
                .telefone("11999998888")
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os pacientes com sucesso")
    void testListarTodos() {
        when(pacienteService.listarTodos()).thenReturn(List.of(paciente));

        ResponseEntity<List<Paciente>> response = pacienteController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Maria Silva", response.getBody().get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar paciente por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(pacienteService.buscarPorId(1L)).thenReturn(paciente);

        ResponseEntity<Paciente> response = pacienteController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria Silva", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(pacienteService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Paciente não encontrado com o ID: 99"));

        assertThrows(ResourceNotFoundException.class, () -> pacienteController.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar paciente por CPF com sucesso")
    void testBuscarPorCpfSucesso() {
        when(pacienteService.buscarPorCpf("52998224725")).thenReturn(Optional.of(paciente));

        ResponseEntity<Paciente> response = pacienteController.buscarPorCpf("52998224725");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Maria Silva", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve criar paciente com sucesso")
    void testCriarSucesso() {
        when(pacienteService.salvar(any(Paciente.class))).thenReturn(paciente);

        ResponseEntity<Paciente> response = pacienteController.criar(paciente);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria Silva", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao criar paciente com CPF duplicado")
    void testCriarCpfDuplicado() {
        when(pacienteService.salvar(any(Paciente.class)))
                .thenThrow(new DuplicateResourceException("Já existe um paciente cadastrado com o CPF: 52998224725"));

        assertThrows(DuplicateResourceException.class, () -> pacienteController.criar(paciente));
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao criar paciente com Email duplicado")
    void testCriarEmailDuplicado() {
        when(pacienteService.salvar(any(Paciente.class)))
                .thenThrow(new DuplicateResourceException("Já existe um paciente cadastrado com o e-mail: maria@email.com"));

        assertThrows(DuplicateResourceException.class, () -> pacienteController.criar(paciente));
    }

    @Test
    @DisplayName("Deve atualizar paciente com sucesso")
    void testAtualizarSucesso() {
        Paciente atualizadoInfo = Paciente.builder()
                .nome("Maria Silva Souza")
                .cpf("52998224725")
                .email("maria.souza@email.com")
                .telefone("11999997777")
                .build();

        when(pacienteService.atualizar(eq(1L), any(Paciente.class))).thenReturn(atualizadoInfo);

        ResponseEntity<Paciente> response = pacienteController.atualizar(1L, atualizadoInfo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Maria Silva Souza", response.getBody().getNome());
        assertEquals("maria.souza@email.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("Deve deletar paciente com sucesso")
    void testDeletarSucesso() {
        doNothing().when(pacienteService).deletar(1L);

        ResponseEntity<Void> response = pacienteController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pacienteService, times(1)).deletar(1L);
    }
}