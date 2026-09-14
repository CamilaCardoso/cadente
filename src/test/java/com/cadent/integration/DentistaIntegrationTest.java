package com.cadent.integration;

import com.cadent.entity.Dentista;
import com.cadent.repository.DentistaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DentistaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        dentistaRepository.deleteAll();
    }

    @Test
    void deveCriarDentistaERetornarStatusCreated() throws Exception {
        Dentista dentista = Dentista.builder()
                .nome("Dra. Julia")
                .crm("123456/SP")
                .especialidade("Ortodontia")
                .telefone("11977777777")
                .build();

        mockMvc.perform(post("/api/dentistas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dentista)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Dra. Julia")))
                .andExpect(jsonPath("$.crm", is("123456/SP")));
    }

    @Test
    void deveListarTodosOsDentistas() throws Exception {
        Dentista d1 = Dentista.builder().nome("Dra. Ana").crm("11111/SP").especialidade("Geral").build();
        Dentista d2 = Dentista.builder().nome("Dr. Marcos").crm("22222/RJ").especialidade("Cirurgia").build();
        dentistaRepository.save(d1);
        dentistaRepository.save(d2);

        mockMvc.perform(get("/api/dentistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Dra. Ana")));
    }
}
