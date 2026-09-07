package com.cadent.controller;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Pagamento;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.AgendamentoRepository;
import com.cadent.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pagamentos")
@Validated
public class PagamentoController {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @GetMapping
    public ResponseEntity<List<Pagamento>> listarTodos() {
        List<Pagamento> pagamentos = pagamentoRepository.findAll();
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable Long id) {
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);
        
        if (pagamento.isEmpty()) {
            throw new ResourceNotFoundException(
                "Pagamento com ID " + id + " não encontrado"
            );
        }
        
        return ResponseEntity.ok(pagamento.get());
    }

    @GetMapping("/agendamento/{agendamentoId}")
    public ResponseEntity<Pagamento> buscarPorAgendamento(@PathVariable Long agendamentoId) {
        Optional<Pagamento> pagamento = pagamentoRepository.findByAgendamentoId(agendamentoId);
        
        if (pagamento.isEmpty()) {
            throw new ResourceNotFoundException(
                "Pagamento para agendamento " + agendamentoId + " não encontrado"
            );
        }
        
        return ResponseEntity.ok(pagamento.get());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pagamento>> buscarPorStatus(@PathVariable String status) {
        try {
            Pagamento.StatusPagamento statusEnum = Pagamento.StatusPagamento.valueOf(status.toUpperCase());
            List<Pagamento> pagamentos = pagamentoRepository.findByStatus(statusEnum);
            return ResponseEntity.ok(pagamentos);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Status inválido. Use: PENDENTE, PAGO ou CANCELADO"
            );
        }
    }

    @PostMapping
    public ResponseEntity<Pagamento> criar(@Valid @RequestBody Pagamento pagamento) {
        
        if (pagamento.getAgendamento() == null || pagamento.getAgendamento().getId() == null) {
            throw new IllegalArgumentException("Agendamento é obrigatório");
        }

        Long agendamentoId = pagamento.getAgendamento().getId();

        // Validar se o agendamento realmente existe no banco
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento com ID " + agendamentoId + " não encontrado"));

        // Validar se já existe pagamento para este agendamento
        Optional<Pagamento> pagamentoExistente = pagamentoRepository.findByAgendamentoId(agendamentoId);
        
        if (pagamentoExistente.isPresent()) {
            throw new DuplicateResourceException(
                "Já existe um pagamento registrado para este agendamento"
            );
        }

        // Validar valor
        if (pagamento.getValorTotal() == null || pagamento.getValorTotal().signum() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        // Associa a entidade gerenciada
        pagamento.setAgendamento(agendamento);

        Pagamento pagamentoCriado = pagamentoRepository.save(pagamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pagamento> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Pagamento pagamentoAtualizado) {

        Optional<Pagamento> pagamentoExistente = pagamentoRepository.findById(id);
        
        if (pagamentoExistente.isEmpty()) {
            throw new ResourceNotFoundException(
                "Pagamento com ID " + id + " não encontrado"
            );
        }

        Pagamento p = pagamentoExistente.get();

        if (pagamentoAtualizado.getDataPagamento() != null) {
            p.setDataPagamento(pagamentoAtualizado.getDataPagamento());
        }

        if (pagamentoAtualizado.getValorTotal() != null) {
            if (pagamentoAtualizado.getValorTotal().signum() <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero");
            }
            p.setValorTotal(pagamentoAtualizado.getValorTotal());
        }

        if (pagamentoAtualizado.getMetodo() != null) {
            p.setMetodo(pagamentoAtualizado.getMetodo());
        }

        if (pagamentoAtualizado.getStatus() != null) {
            p.setStatus(pagamentoAtualizado.getStatus());
        }

        Pagamento salvo = pagamentoRepository.save(p);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!pagamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Pagamento com ID " + id + " não encontrado"
            );
        }

        pagamentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/total-pago")
    public ResponseEntity<Long> contarPagosPago() {
        long total = pagamentoRepository.countByStatus(Pagamento.StatusPagamento.PAGO);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/stats/total-pendente")
    public ResponseEntity<Long> contarPagamentosPendentes() {
        long total = pagamentoRepository.countByStatus(Pagamento.StatusPagamento.PENDENTE);
        return ResponseEntity.ok(total);
    }
}