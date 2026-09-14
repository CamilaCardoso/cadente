package com.cadent.integration;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Dentista;
import com.cadent.entity.Paciente;
import com.cadent.entity.Pagamento;
import com.cadent.repository.AgendamentoRepository;
import com.cadent.repository.DentistaRepository;
import com.cadent.repository.PacienteRepository;
import com.cadent.repository.PagamentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PagamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Agendamento agendamentoSalvo;

    @BeforeEach
    void setUp() {
        pagamentoRepository.deleteAll();
        agendamentoRepository.deleteAll();
        pacienteRepository.deleteAll();
        dentistaRepository.deleteAll();

        Paciente paciente = pacienteRepository.save(Paciente.builder().nome("Carlos").cpf("987.654.321-00").build());
        Dentista dentista = dentistaRepository.save(Dentista.builder().nome("Dr. Ricardo").crm("99999/SP").build());

        Agendamento agendamento = Agendamento.builder()
                .paciente(paciente)
                .dentista(dentista)
                .dataHora(LocalDateTime.now().plusDays(1))
                .status(com.cadent.entity.enums.StatusAgendamento.CONCLUIDO)
                .build();
        agendamentoSalvo = agendamentoRepository.save(agendamento);
    }

    @Test
    void deveCriarPagamentoERetornarStatusCreated() throws Exception {
        Pagamento pagamento = Pagamento.builder()
                .agendamento(agendamentoSalvo)
                .valorTotal(new BigDecimal("300.00"))
                .metodo(com.cadent.entity.enums.MetodoPagamento.PIX)
                .status(com.cadent.entity.enums.StatusPagamento.PAGO)
                .dataPagamento(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagamento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAGO"));
    }

    @Test
    void deveListarPagamentos() throws Exception {
        Pagamento p1 = Pagamento.builder()
                .agendamento(agendamentoSalvo)
                .valorTotal(new BigDecimal("150.00"))
                .status(com.cadent.entity.enums.StatusPagamento.PENDENTE)
                .build();
        pagamentoRepository.save(p1);

        mockMvc.perform(get("/api/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
