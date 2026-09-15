package com.cadent.service;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Dentista;
import com.cadent.entity.Paciente;
import com.cadent.entity.Procedimento;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.AgendamentoRepository;
import com.cadent.repository.DentistaRepository;
import com.cadent.repository.PacienteRepository;
import com.cadent.repository.ProcedimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;
    private final ProcedimentoRepository procedimentoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              PacienteRepository pacienteRepository,
                              DentistaRepository dentistaRepository,
                              ProcedimentoRepository procedimentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.dentistaRepository = dentistaRepository;
        this.procedimentoRepository = procedimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Agendamento> buscarPorPaciente(Long pacienteId) {
        return agendamentoRepository.findByPacienteId(pacienteId);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> buscarPorDentista(Long dentistaId) {
        return agendamentoRepository.findByDentistaId(dentistaId);
    }

    @Transactional
    public Agendamento salvar(Agendamento agendamento) {
        Long pacienteId = agendamento.getPaciente().getId();
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + pacienteId));
        agendamento.setPaciente(paciente);

        Long dentistaId = agendamento.getDentista().getId();
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new ResourceNotFoundException("Dentista não encontrado com ID: " + dentistaId));
        agendamento.setDentista(dentista);

        if (agendamento.getProcedimentos() != null && !agendamento.getProcedimentos().isEmpty()) {
            List<Procedimento> procedimentosCarregados = new ArrayList<>();
            for (Procedimento proc : agendamento.getProcedimentos()) {
                Procedimento procedimentoEncontrado = procedimentoRepository.findById(proc.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Procedimento não encontrado com ID: " + proc.getId()));
                procedimentosCarregados.add(procedimentoEncontrado);
            }
            agendamento.setProcedimentos(procedimentosCarregados);
        }

        return agendamentoRepository.save(agendamento);
    }

    @Transactional
    public Agendamento atualizar(Long id, Agendamento agendamentoAtualizado) {
        Agendamento agendamentoExistente = buscarPorId(id);
        agendamentoExistente.setDataHora(agendamentoAtualizado.getDataHora());
        agendamentoExistente.setStatus(agendamentoAtualizado.getStatus());
        agendamentoExistente.setObservacoes(agendamentoAtualizado.getObservacoes());
        if (agendamentoAtualizado.getProcedimentos() != null) {
            agendamentoExistente.setProcedimentos(agendamentoAtualizado.getProcedimentos());
        }
        return agendamentoRepository.save(agendamentoExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamentoRepository.delete(agendamento);
    }
}