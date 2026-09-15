package com.cadent.controller;

import com.cadent.BaseIntegrationTest;
import com.cadent.entity.Paciente;
import com.cadent.repository.PacienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Paciente Controller - Testes de Integração")
class PacienteControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Paciente pacienteTeste;

    @BeforeEach
    void setUp() {
        pacienteRepository.deleteAll();
        
        pacienteTeste = new Paciente();
        pacienteTeste.setNome("João Silva");
        pacienteTeste.setCpf("12345678909");
        pacienteTeste.setEmail("joao@example.com");
        pacienteTeste.setTelefone("11999999999");
    }

    @Test
    @DisplayName("Deve criar paciente com dados válidos")
    void testCriarPacienteComSucesso() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(pacienteTeste);

        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));
    }

    @Test
    @DisplayName("Deve rejeitar paciente com CPF duplicado")
    void testCriarPacienteCPFDuplicado() throws Exception {
        // Primeiro paciente
        pacienteRepository.save(pacienteTeste);

        // Tentativa de criar outro com mesmo CPF
        Paciente duplicado = new Paciente();
        duplicado.setNome("Outro Nome");
        duplicado.setCpf("12345678909");
        duplicado.setEmail("outro@example.com");
        duplicado.setTelefone("11888888888");

        String jsonRequest = objectMapper.writeValueAsString(duplicado);

        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem", is("Recurso já existe")));
    }

    @Test
    @DisplayName("Deve rejeitar paciente com email inválido")
    void testCriarPacienteEmailInvalido() throws Exception {
        pacienteTeste.setEmail("email-invalido");

        String jsonRequest = objectMapper.writeValueAsString(pacienteTeste);

        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.email").exists());
    }

    @Test
    @DisplayName("Deve listar todos os pacientes")
    void testListarTodosPacientes() throws Exception {
        pacienteRepository.save(pacienteTeste);

        mockMvc.perform(get("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve buscar paciente por ID")
    void testBuscarPacientePorId() throws Exception {
        Paciente salvo = pacienteRepository.save(pacienteTeste);

        mockMvc.perform(get("/api/pacientes/{id}", salvo.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar 404 para paciente inexistente")
    void testBuscarPacienteInexistente() throws Exception {
        mockMvc.perform(get("/api/pacientes/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar paciente")
    void testAtualizarPaciente() throws Exception {
        Paciente salvo = pacienteRepository.save(pacienteTeste);

        Paciente atualizado = new Paciente();
        atualizado.setNome("João Silva Atualizado");
        atualizado.setCpf(salvo.getCpf());
        atualizado.setEmail("joao.novo@example.com");
        atualizado.setTelefone("11988888888");

        String jsonRequest = objectMapper.writeValueAsString(atualizado);

        mockMvc.perform(put("/api/pacientes/{id}", salvo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"));
    }

    @Test
    @DisplayName("Deve deletar paciente")
    void testDeletarPaciente() throws Exception {
        Paciente salvo = pacienteRepository.save(pacienteTeste);

        mockMvc.perform(delete("/api/pacientes/{id}", salvo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/pacientes/{id}", salvo.getId()))
                .andExpect(status().isNotFound());
    }
}