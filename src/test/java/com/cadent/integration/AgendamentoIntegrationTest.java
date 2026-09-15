package com.cadent.integration;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Agendamento.StatusAgendamento; // Garanta que o Enum está importado
import com.cadent.entity.Paciente;
import com.cadent.entity.Procedimento;
import org.junit.jupiter.api.Test;

public class AgendamentoIntegrationTest {

    @Test
    void exemploTesteAgendamento() {
        Agendamento agendamento = new Agendamento();
        Procedimento procedimento = new Procedimento();
        Paciente paciente = new Paciente();

        agendamento.setStatus(StatusAgendamento.AGENDADO);

        agendamento.setPaciente(paciente);
        Paciente pacienteObtido = agendamento.getPaciente();
    }
}