package com.cadent.integration;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Dentista;
import com.cadent.entity.Paciente;
import com.cadent.repository.AgendamentoRepository;
import com.cadent.repository.DentistaRepository;
import com.cadent.repository.PacienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AgendamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Paciente pacienteSalvo;
    private Dentista dentistaSalvo;

    @BeforeEach
    void setUp() {
        agendamentoRepository.deleteAll();
        pacienteRepository.deleteAll();
        dentistaRepository.deleteAll();

        // Para criar um agendamento, precisamos de um paciente e um dentista no banco
        Paciente paciente = Paciente.builder().nome("Carlos").cpf("123.456.789-09").build();
        pacienteSalvo = pacienteRepository.save(paciente);

        Dentista dentista = Dentista.builder().nome("Dr. Ricardo").crm("88888/SP").build();
        dentistaSalvo = dentistaRepository.save(dentista);
    }

    @Test
    void deveCriarAgendamentoERetornarStatusCreated() throws Exception {
        Agendamento agendamento = Agendamento.builder()
                .paciente(pacienteSalvo)
                .dentista(dentistaSalvo)
                .dataHora(LocalDateTime.now().plusDays(1))
                .status(com.cadent.entity.enums.StatusAgendamento.AGENDADO)
                .build();

        mockMvc.perform(post("/api/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendamento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AGENDADO"));
    }

    @Test
    void deveListarAgendamentos() throws Exception {
        Agendamento a1 = Agendamento.builder()
                .paciente(pacienteSalvo)
                .dentista(dentistaSalvo)
                .dataHora(LocalDateTime.now().plusDays(2))
                .status(com.cadent.entity.enums.StatusAgendamento.AGENDADO)
                .build();
        agendamentoRepository.save(a1);

        mockMvc.perform(get("/api/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
