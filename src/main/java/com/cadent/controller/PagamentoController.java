package com.cadent.controller;

import com.cadent.entity.Pagamento;
import com.cadent.service.PagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@Validated
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public ResponseEntity<List<Pagamento>> listarTodos() {
        return ResponseEntity.ok(pagamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.buscarPorId(id));
    }

    @GetMapping("/agendamento/{agendamentoId}")
    public ResponseEntity<Pagamento> buscarPorAgendamento(@PathVariable Long agendamentoId) {
        return ResponseEntity.ok(pagamentoService.buscarPorAgendamento(agendamentoId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pagamento>> buscarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(pagamentoService.buscarPorStatus(status));
    }

    @PostMapping
    public ResponseEntity<Pagamento> criar(@Valid @RequestBody Pagamento pagamento) {
        Pagamento pagamentoCriado = pagamentoService.salvar(pagamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pagamento> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Pagamento pagamentoAtualizado) {
        return ResponseEntity.ok(pagamentoService.atualizar(id, pagamentoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/total-pago")
    public ResponseEntity<Long> contarPagosPago() {
        return ResponseEntity.ok(pagamentoService.contarPagos());
    }

    @GetMapping("/stats/total-pendente")
    public ResponseEntity<Long> contarPagamentosPendentes() {
        return ResponseEntity.ok(pagamentoService.contarPendentes());
    }
}