package com.cadent.controller;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Dentista;
import com.cadent.entity.Paciente;
import com.cadent.entity.Procedimento;
import com.cadent.repository.AgendamentoRepository;
import com.cadent.repository.DentistaRepository;
import com.cadent.repository.PacienteRepository;
import com.cadent.repository.ProcedimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos() {
        List<Agendamento> agendamentos = agendamentoRepository.findAll();
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        return agendamento.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Agendamento>> buscarPorPaciente(@PathVariable Long pacienteId) {
        List<Agendamento> agendamentos = agendamentoRepository.findByPacienteId(pacienteId);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/dentista/{dentistaId}")
    public ResponseEntity<List<Agendamento>> buscarPorDentista(@PathVariable Long dentistaId) {
        List<Agendamento> agendamentos = agendamentoRepository.findByDentistaId(dentistaId);
        return ResponseEntity.ok(agendamentos);
    }

    @PostMapping
    public ResponseEntity<Agendamento> criar(@RequestBody Agendamento agendamento) {
        if (agendamento.getPaciente() == null || agendamento.getPaciente().getId() == null ||
            agendamento.getDentista() == null || agendamento.getDentista().getId() == null ||
            agendamento.getDataHora() == null) {
            return ResponseEntity.badRequest().build();
        }

        // 1. Valida e busca Paciente e Dentista gerenciados pelo JPA
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(agendamento.getPaciente().getId());
        Optional<Dentista> dentistaOpt = dentistaRepository.findById(agendamento.getDentista().getId());

        if (!pacienteOpt.isPresent() || !dentistaOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        agendamento.setPaciente(pacienteOpt.get());
        agendamento.setDentista(dentistaOpt.get());

        // 2. Busca e vincula os Procedimentos existentes no banco
        if (agendamento.getProcedimentos() != null && !agendamento.getProcedimentos().isEmpty()) {
            Set<Procedimento> procedimentosGerenciados = agendamento.getProcedimentos().stream()
                    .map(Procedimento::getId)
                    .filter(java.util.Objects::nonNull)
                    .map(id -> procedimentoRepository.findById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            agendamento.setProcedimentos(procedimentosGerenciados);
        } else {
            agendamento.setProcedimentos(new HashSet<>());
        }

        // 3. Associa a referência do Agendamento dentro de cada Pagamento (relacionamento bidirecional)
        if (agendamento.getPagamentos() != null && !agendamento.getPagamentos().isEmpty()) {
            agendamento.getPagamentos().forEach(pagamento -> pagamento.setAgendamento(agendamento));
        }

        Agendamento agendamentoCriado = agendamentoRepository.save(agendamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizar(@PathVariable Long id, @RequestBody Agendamento agendamentoAtualizado) {
        Optional<Agendamento> agendamentoExistente = agendamentoRepository.findById(id);
        
        if (agendamentoExistente.isPresent()) {
            Agendamento a = agendamentoExistente.get();
            
            if (agendamentoAtualizado.getStatus() != null) {
                a.setStatus(agendamentoAtualizado.getStatus());
            }
            if (agendamentoAtualizado.getObservacoes() != null) {
                a.setObservacoes(agendamentoAtualizado.getObservacoes());
            }
            
            // Atualização dos procedimentos caso enviados no PUT
            if (agendamentoAtualizado.getProcedimentos() != null) {
                Set<Procedimento> procedimentosGerenciados = agendamentoAtualizado.getProcedimentos().stream()
                        .map(Procedimento::getId)
                        .filter(java.util.Objects::nonNull)
                        .map(pId -> procedimentoRepository.findById(pId))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
                
                a.setProcedimentos(procedimentosGerenciados);
            }

            Agendamento salvo = agendamentoRepository.save(a);
            return ResponseEntity.ok(salvo);
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (agendamentoRepository.existsById(id)) {
            agendamentoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}