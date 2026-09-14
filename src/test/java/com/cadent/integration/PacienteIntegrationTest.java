package com.cadent.integration;

import com.cadent.entity.Paciente;
import com.cadent.repository.PacienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Sobe todo o contexto da aplicação do Spring
@AutoConfigureMockMvc // Configura o MockMvc para simular requisições HTTP
public class PacienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Ferramenta que simula as requisições (como se fosse um Postman)

    @Autowired
    private PacienteRepository pacienteRepository; // Repositório real conectado ao banco de dados H2 (em memória)

    @Autowired
    private ObjectMapper objectMapper; // Transforma objetos Java em JSON e vice-versa

    @BeforeEach
    void setUp() {
        // Limpa o banco de dados antes de cada teste para garantir que um teste não interfira no outro
        pacienteRepository.deleteAll();
    }

    @Test
    void deveCriarPacienteERetornarStatusCreated() throws Exception {
        // Cenário (Given) - Criamos um objeto Paciente que queremos salvar
        Paciente novoPaciente = Paciente.builder()
                .nome("Carlos Silva")
                .cpf("123.456.789-00")
                .telefone("(11) 98765-4321")
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .endereco("Rua das Flores, 123")
                .build();

        String pacienteJson = objectMapper.writeValueAsString(novoPaciente);

        // Ação (When) & Validação (Then) - Fazemos a requisição POST e esperamos o status 201 Created
        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pacienteJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Carlos Silva")))
                .andExpect(jsonPath("$.cpf", is("123.456.789-00")));
    }

    @Test
    void deveListarTodosOsPacientesSalvosNoBanco() throws Exception {
        // Cenário (Given) - Salvamos manualmente 2 pacientes no banco de dados real
        Paciente p1 = Paciente.builder().nome("Ana Maria").cpf("111.111.111-11").telefone("11999999999").dataNascimento(LocalDate.of(1985, 10, 15)).endereco("Rua A").build();
        Paciente p2 = Paciente.builder().nome("João Pedro").cpf("222.222.222-22").telefone("11888888888").dataNascimento(LocalDate.of(1992, 2, 10)).endereco("Rua B").build();
        pacienteRepository.save(p1);
        pacienteRepository.save(p2);

        // Ação (When) & Validação (Then) - Fazemos um GET e verificamos se retornam 2 registros e se os nomes batem
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Ana Maria")))
                .andExpect(jsonPath("$[1].nome", is("João Pedro")));
    }
}

