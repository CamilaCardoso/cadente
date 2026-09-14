package com.cadent.controller;

import com.cadent.entity.Paciente;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.PacienteRepository;
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
    private PacienteRepository pacienteRepository;

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
        when(pacienteRepository.findAll()).thenReturn(List.of(paciente));

        ResponseEntity<List<Paciente>> response = pacienteController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Maria Silva", response.getBody().get(0).getNome());
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar paciente por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        ResponseEntity<Paciente> response = pacienteController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria Silva", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pacienteController.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar paciente por CPF com sucesso")
    void testBuscarPorCpfSucesso() {
        when(pacienteRepository.findByCpf("52998224725")).thenReturn(Optional.of(paciente));

        ResponseEntity<Paciente> response = pacienteController.buscarPorCpf("52998224725");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Maria Silva", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar CPF inexistente")
    void testBuscarPorCpfNaoEncontrado() {
        when(pacienteRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pacienteController.buscarPorCpf("00000000000"));
    }

    @Test
    @DisplayName("Deve criar paciente com sucesso")
    void testCriarSucesso() {
        when(pacienteRepository.findByCpf(paciente.getCpf())).thenReturn(Optional.empty());
        when(pacienteRepository.findByEmail(paciente.getEmail())).thenReturn(Optional.empty());
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        ResponseEntity<Paciente> response = pacienteController.criar(paciente);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria Silva", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao criar paciente com CPF duplicado")
    void testCriarCpfDuplicado() {
        when(pacienteRepository.findByCpf(paciente.getCpf())).thenReturn(Optional.of(paciente));

        assertThrows(DuplicateResourceException.class, () -> pacienteController.criar(paciente));
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao criar paciente com Email duplicado")
    void testCriarEmailDuplicado() {
        when(pacienteRepository.findByCpf(paciente.getCpf())).thenReturn(Optional.empty());
        when(pacienteRepository.findByEmail(paciente.getEmail())).thenReturn(Optional.of(paciente));

        assertThrows(DuplicateResourceException.class, () -> pacienteController.criar(paciente));
        verify(pacienteRepository, never()).save(any(Paciente.class));
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

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Paciente> response = pacienteController.atualizar(1L, atualizadoInfo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Maria Silva Souza", response.getBody().getNome());
        assertEquals("maria.souza@email.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("Deve deletar paciente com sucesso")
    void testDeletarSucesso() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pacienteRepository).deleteById(1L);

        ResponseEntity<Void> response = pacienteController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pacienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar paciente inexistente")
    void testDeletarNaoEncontrado() {
        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> pacienteController.deletar(99L));
        verify(pacienteRepository, never()).deleteById(99L);
    }
}

