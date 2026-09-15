package com.cadent.controller;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Dentista;
import com.cadent.entity.Paciente;
import com.cadent.entity.Procedimento;
import com.cadent.repository.AgendamentoRepository;
import com.cadent.repository.DentistaRepository;
import com.cadent.repository.PacienteRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoControllerTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private DentistaRepository dentistaRepository;

    @Mock
    private ProcedimentoRepository procedimentoRepository;

    @InjectMocks
    private AgendamentoController agendamentoController;

    private Agendamento agendamento;
    private Paciente paciente;
    private Dentista dentista;
    private Procedimento procedimento;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("Maria");

        dentista = new Dentista();
        dentista.setId(1L);
        dentista.setNome("Dr. João");

        procedimento = new Procedimento();
        procedimento.setId(1L);
        procedimento.setNome("Limpeza");

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setPaciente(paciente);
        agendamento.setDentista(dentista);
        agendamento.setProcedimentos(List.of(procedimento));
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));
        agendamento.setStatus(Agendamento.StatusAgendamento.AGENDADO);
        agendamento.setObservacoes("Primeira consulta");
    }

    @Test
    @DisplayName("Deve listar todos os agendamentos")
    void testListarTodos() {
        when(agendamentoRepository.findAll()).thenReturn(List.of(agendamento));

        ResponseEntity<List<Agendamento>> response = agendamentoController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar agendamento por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        ResponseEntity<Agendamento> response = agendamentoController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar por ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(agendamentoRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Agendamento> response = agendamentoController.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar agendamentos por Paciente ID")
    void testBuscarPorPaciente() {
        when(agendamentoRepository.findByPacienteId(1L)).thenReturn(List.of(agendamento));

        ResponseEntity<List<Agendamento>> response = agendamentoController.buscarPorPaciente(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Deve buscar agendamentos por Dentista ID")
    void testBuscarPorDentista() {
        when(agendamentoRepository.findByDentistaId(1L)).thenReturn(List.of(agendamento));

        ResponseEntity<List<Agendamento>> response = agendamentoController.buscarPorDentista(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void testCriarSucesso() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        ResponseEntity<Agendamento> response = agendamentoController.criar(agendamento);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar Bad Request ao criar agendamento com dados incompletos")
    void testCriarDadosIncompletos() {
        Agendamento invalido = new Agendamento();
        ResponseEntity<Agendamento> response = agendamentoController.criar(invalido);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar agendamento com sucesso")
    void testAtualizarSucesso() {
        Agendamento atualizado = new Agendamento();
        atualizado.setStatus(Agendamento.StatusAgendamento.REALIZADO);
        atualizado.setObservacoes("Consulta realizada com sucesso");
        atualizado.setProcedimentos(List.of(procedimento));

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Agendamento> response = agendamentoController.atualizar(1L, atualizado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Agendamento.StatusAgendamento.REALIZADO, response.getBody().getStatus());
        assertEquals("Consulta realizada com sucesso", response.getBody().getObservacoes());
    }

    @Test
    @DisplayName("Deve deletar agendamento com sucesso")
    void testDeletarSucesso() {
        when(agendamentoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(agendamentoRepository).deleteById(1L);

        ResponseEntity<Void> response = agendamentoController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agendamentoRepository, times(1)).deleteById(1L);
    }
}

