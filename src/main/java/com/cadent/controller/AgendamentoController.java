package com.cadent.controller;

import com.cadent.entity.Agendamento;
import com.cadent.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

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
        if (agendamento.getPaciente() == null || agendamento.getDentista() == null || agendamento.getDataHora() == null) {
            return ResponseEntity.badRequest().build();
        }
        Agendamento agendamentoCriado = agendamentoRepository.save(agendamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizar(@PathVariable Long id, @RequestBody Agendamento agendamentoAtualizado) {
        Optional<Agendamento> agendamentoExistente = agendamentoRepository.findById(id);
        
        if (agendamentoExistente.isPresent()) {
            Agendamento a = agendamentoExistente.get();
            if (agendamentoAtualizado.getStatus() != null) a.setStatus(agendamentoAtualizado.getStatus());
            if (agendamentoAtualizado.getObservacoes() != null) a.setObservacoes(agendamentoAtualizado.getObservacoes());
            
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