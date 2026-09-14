package com.cadent.integration;

import com.cadent.entity.Procedimento;
import com.cadent.repository.ProcedimentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProcedimentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        procedimentoRepository.deleteAll();
    }

    @Test
    void deveCriarProcedimentoERetornarStatusCreated() throws Exception {
        Procedimento procedimento = Procedimento.builder()
                .nome("Limpeza")
                .descricao("Limpeza geral dos dentes")
                .valorPadrao(new BigDecimal("150.00"))
                .build();

        mockMvc.perform(post("/api/procedimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(procedimento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Limpeza")))
                .andExpect(jsonPath("$.valorPadrao", is(150.00)));
    }

    @Test
    void deveListarTodosOsProcedimentos() throws Exception {
        Procedimento p1 = Procedimento.builder().nome("Extração").valorPadrao(new BigDecimal("200.00")).build();
        Procedimento p2 = Procedimento.builder().nome("Clareamento").valorPadrao(new BigDecimal("500.00")).build();
        procedimentoRepository.save(p1);
        procedimentoRepository.save(p2);

        mockMvc.perform(get("/api/procedimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Extração")));
    }
}
