package com.cadent.controller;

import com.cadent.entity.Procedimento;
import com.cadent.service.ProcedimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/procedimentos")
@Validated
public class ProcedimentoController {

    private final ProcedimentoService procedimentoService;

    public ProcedimentoController(ProcedimentoService procedimentoService) {
        this.procedimentoService = procedimentoService;
    }

    @GetMapping
    public ResponseEntity<List<Procedimento>> listarTodos() {
        return ResponseEntity.ok(procedimentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Procedimento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(procedimentoService.buscarPorId(id));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Procedimento> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(procedimentoService.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<Procedimento> criar(@Valid @RequestBody Procedimento procedimento) {
        Procedimento procedimentoCriado = procedimentoService.salvar(procedimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(procedimentoCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Procedimento> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Procedimento procedimentoAtualizado) {
        return ResponseEntity.ok(procedimentoService.atualizar(id, procedimentoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        procedimentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> contarProcedimentos() {
        return ResponseEntity.ok(procedimentoService.contarProcedimentos());
    }
}